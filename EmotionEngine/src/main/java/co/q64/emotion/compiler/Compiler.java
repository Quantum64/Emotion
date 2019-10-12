package co.q64.emotion.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import co.q64.emotion.compression.Base;
import co.q64.emotion.compression.Deflate;
import co.q64.emotion.compression.Lzma;
import co.q64.emotion.compression.Shoco;
import co.q64.emotion.compression.Smaz;
import co.q64.emotion.lang.Stack;
import co.q64.emotion.lang.opcode.Chars;
import co.q64.emotion.lang.opcode.OpcodeCache;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.Opcodes;

@Singleton
public class Compiler {
    protected @Inject Compiler() {}

    protected @Inject Opcodes opcodes;
    protected @Inject OpcodeCache cache;
    protected @Inject CompilerOutputFactory output;
    protected @Inject LineTokenizer lineTokenizer;
    protected @Inject Provider<NameTable> nameTableProvider;

    public CompilerOutput compile(List<String> input) {
        cache.resetPrioritization();
        List<String> instructions = new ArrayList<>();
        NameTable nameTable = nameTableProvider.get();
        for (String current : input) {
            current = current.replaceFirst("^\\s++", "");
            if (current.startsWith("load")) {
                instructions.add(current);
                continue;
            }
            StringBuilder updated = new StringBuilder();
            for (char c : current.toCharArray()) {
                if (String.valueOf(c).equals("'") || String.valueOf(c).equals("#")) {
                    break;
                }
                updated.append(c);
            }
            current = updated.toString().trim();
            instructions.add(current);
        }
        int line = 0;
        List<String> compiledLines = new ArrayList<>();
        List<String> compiledInstructions = new ArrayList<>();
        List<String> functions = new ArrayList<>();
        for (ListIterator<String> itr = instructions.listIterator(); itr.hasNext(); ) {
            List<String> instructionsToCompile = new ArrayList<>(Arrays.asList(itr.next()));
            String firstInstruction = instructionsToCompile.get(0);
            StringBuilder compiled = new StringBuilder();
            line++;
            if (firstInstruction.isEmpty()) {
                itr.remove();
                continue;
            }
            if (firstInstruction.startsWith("def ") && firstInstruction.length() > 4) {
                if (functions.contains(firstInstruction.substring(4))) {
                    return output.create("AST structure violation: Function '" + firstInstruction.substring(4) + "' defined multiple times! Line: " + line);
                }
                instructionsToCompile.clear();
                instructionsToCompile.add("def");
            } else if (firstInstruction.startsWith("def")) {
                return output.create("Unnamed function definition. Line: " + line);
            }
            if (firstInstruction.startsWith("jump ") && firstInstruction.length() > 5) {
                String functionName = firstInstruction.substring(5);
                int currentFunctorIndex = 0, locatedFunctorIndex = -1;
                for (String ins : instructions) {
                    if (ins.startsWith("def ") && ins.length() > 4) {
                        String currentFuncName = ins.substring(4);
                        if (currentFuncName.equals(functionName)) {
                            locatedFunctorIndex = currentFunctorIndex;
                            break;
                        }
                        currentFunctorIndex++;

                    }
                }
                if (locatedFunctorIndex < 0) {
                    return output.create("AST structure violation: Function '" + functionName + "' was never defined! Line: " + line);
                }
                instructionsToCompile.clear();
                instructionsToCompile.add("load " + locatedFunctorIndex);
                instructionsToCompile.add("jump");
            }
            for (String instruction : instructionsToCompile) {
                Optional<CompilerOutput> output = lineTokenizer.processLine(instruction, compiled, nameTable, line);
                if (output.isPresent()) {
                    return output.get();
                }
            }
            compiledLines.add(compiled.toString());
        }
        int debt = 0;
        for (ListIterator<String> itr = compiledLines.listIterator(); itr.hasNext(); ) {
            Optional<Integer> opt = opcodes.lookupSymbol(itr.next());
            if (opt.isPresent()) {
                int id = opt.get();
                if (opcodes.getFlags(OpcodeMarker.EQUAL).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.NOT_EQUAL).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.GREATER).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.LESS).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.GREATER_EQUAL).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.LESS_EQUAL).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.FUNCTION).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.ITERATE).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.TRUE).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.FALSE).contains(id) || //
                        opcodes.getFlags(OpcodeMarker.ITERATE_STACK).contains(id)) {
                    debt++;
                }
                if (opcodes.getFlags(OpcodeMarker.END).contains(id)) {
                    debt--;
                }
            }
        }
        if (debt > 0) {
            return output.create("AST control flow violation: " + debt + " end instruction" + (debt == 1 ? "" : "s") + " missing!");
        } else if (debt < 0) {
            return output.create("AST control flow violation: " + Math.abs(debt) + " extra end instruction" + (debt == 1 ? "" : "s"));
        }
        return output.create(compiledLines, instructions);
    }


}
