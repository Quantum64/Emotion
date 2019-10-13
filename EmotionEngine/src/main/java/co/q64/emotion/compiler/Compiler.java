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

import co.q64.emotion.compiler.LineTokenizer.LineResult;
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
    protected @Inject LineTokenizer lineTokenizer;
    protected @Inject Provider<NameTable> nameTableProvider;
    protected @Inject Provider<CompilerOutput> compilerOutput;

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
        List<InstructionInfo> compiled = new ArrayList<>();
        List<String> functions = new ArrayList<>();
        for (ListIterator<String> itr = instructions.listIterator(); itr.hasNext(); ) {
            List<String> instructionsToCompile = new ArrayList<>(Arrays.asList(itr.next()));
            String firstInstruction = instructionsToCompile.get(0);
            line++;
            if (firstInstruction.isEmpty()) {
                itr.remove();
                continue;
            }
            if (firstInstruction.startsWith("def ") && firstInstruction.length() > 4) {
                if (functions.contains(firstInstruction.substring(4))) {
                    return compilerOutput.get().error("AST structure violation: Function '" + firstInstruction.substring(4) + "' defined multiple times! Line: " + line);
                }
                instructionsToCompile.clear();
                instructionsToCompile.add("def");
            } else if (firstInstruction.startsWith("def")) {
                return compilerOutput.get().error("Unnamed function definition. Line: " + line);
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
                    return compilerOutput.get().error("AST structure violation: Function '" + functionName + "' was never defined! Line: " + line);
                }
                instructionsToCompile.clear();
                instructionsToCompile.add("load " + locatedFunctorIndex);
                instructionsToCompile.add("jump");
            }
            for (String instruction : instructionsToCompile) {
                LineResult lineResult = lineTokenizer.processLine(instruction, nameTable, line);
                if (lineResult.error().isPresent()) {
                    return lineResult.error().get();
                }
                compiled.addAll(lineResult.instructions());
            }
        }
        int debt = 0;
        for (ListIterator<InstructionInfo> itr = compiled.listIterator(); itr.hasNext(); ) {
            Optional<Integer> opt = opcodes.lookupSymbol(itr.next().compiled());
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
            return compilerOutput.get().error("AST control flow violation: " + debt + " end instruction" + (debt == 1 ? "" : "s") + " missing!");
        } else if (debt < 0) {
            return compilerOutput.get().error("AST control flow violation: " + Math.abs(debt) + " extra end instruction" + (debt == 1 ? "" : "s"));
        }
        return compilerOutput.get().success(compiled);
    }


}
