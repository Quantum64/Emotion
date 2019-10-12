package co.q64.emotion.compiler;

import co.q64.emotion.compiler.NameTable.NameTableResult;
import co.q64.emotion.runtime.Output;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Singleton
public class LineTokenizer {
    private static final boolean DEBUG_TOKENIZER = false;

    protected @Inject InstructionTokenizer instructionTokenizer;
    protected @Inject Output logger;

    protected @Inject LineTokenizer() {}

    public Optional<CompilerOutput> processLine(String line, StringBuilder result, NameTable nameTable, int index) {
        Optional<CompilerOutput> output = Optional.empty();
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
                        return nameTableResult.getError();
                    }
                    for (String instruction : nameTableResult.getResult().get()) {
                        output = instructionTokenizer.processInstruction(instruction, result, index);
                        if (output.isPresent()) {
                            return output;
                        }
                    }
                }
            }
        }
        if (DEBUG_TOKENIZER) {
            logger.println("Processing instruction: '" + line + "'");
        }
        output = instructionTokenizer.processInstruction(line, result, index);
        if (output.isPresent()) {
            return output;
        }
        if (assignment) {
            for (String instruction : nameTable.getSaveInstructions(assignmentName).getResult().get()) {
                output = instructionTokenizer.processInstruction(instruction, result, index);
                if (output.isPresent()) {
                    return output;
                }
            }
        }
        return output;
    }
}
