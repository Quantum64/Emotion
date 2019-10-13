package co.q64.emotion.compiler;

import co.q64.emotion.compiler.InstructionTokenizer.InstructionResult;
import co.q64.emotion.compiler.NameTable.NameTableResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Singleton
public class LineTokenizer {
    protected @Inject InstructionTokenizer instructionTokenizer;
    protected @Inject Provider<LineResult> lineResultProvider;
    protected @Inject Provider<InstructionInfo> instructionInfoProvider;

    protected @Inject LineTokenizer() {}

    public LineResult processLine(String line, NameTable nameTable, int index) {
        List<InstructionInfo> result = new ArrayList<>();
        boolean assignment = line.contains("=") && line.indexOf("=") < line.length() - 1;
        String assignmentName = null;
        if (assignment) {
            assignmentName = line.substring(0, line.indexOf("=")).trim();
            line = line.substring(line.indexOf("=") + 1, line.length()).trim();
        }
        if (line.contains("(") && !line.startsWith("load")) {
            if (line.contains("(")) {
                line = line.replace(")", "");
            }
            if (line.indexOf("(") < line.length() - 1) {
                String arguments = line.substring(line.indexOf("(") + 1).trim();
                line = line.substring(0, line.indexOf("(")).trim();
                for (String argument : arguments.split(Pattern.quote(","))) {
                    NameTableResult nameTableResult = nameTable.getLoadInstructions(argument.trim());
                    if (nameTableResult.getError().isPresent()) {
                        return lineResultProvider.get().error(nameTableResult.getError());
                    }
                    for (String instruction : nameTableResult.getResult().get()) {
                        Optional<LineResult> optionalLineResult = compileInstruction(instruction, result, index);
                        if (optionalLineResult.isPresent()) {
                            return optionalLineResult.get();
                        }
                    }
                }
            }
        }
        Optional<LineResult> optionalLineResult = compileInstruction(line, result, index);
        if (optionalLineResult.isPresent()) {
            return optionalLineResult.get();
        }
        if (assignment) {
            for (String instruction : nameTable.getSaveInstructions(assignmentName).getResult().get()) {
                optionalLineResult = compileInstruction(instruction, result, index);
                if (optionalLineResult.isPresent()) {
                    return optionalLineResult.get();
                }
            }
        }
        return lineResultProvider.get().instructions(result);
    }

    private Optional<LineResult> compileInstruction(String instruction, List<InstructionInfo> result, int index) {
        InstructionResult ir = instructionTokenizer.processInstruction(instruction, index);
        if (ir.error().isPresent()) {
            return Optional.of(lineResultProvider.get().error(ir.error()));
        }
        result.add(instructionInfoProvider.get().line(index).compiled(ir.compiled()).instruction(instruction));
        return Optional.empty();
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Accessors(fluent = true)
    public static class LineResult {
        private Optional<CompilerOutput> error = Optional.empty();
        private List<InstructionInfo> instructions;

        protected @Inject LineResult() {}
    }
}
