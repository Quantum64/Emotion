package co.q64.emotion.compiler;

import co.q64.emotion.compression.Base;
import co.q64.emotion.compression.Deflate;
import co.q64.emotion.compression.Lzma;
import co.q64.emotion.compression.Shoco;
import co.q64.emotion.compression.Smaz;
import co.q64.emotion.lang.Instruction;
import co.q64.emotion.lang.Stack;
import co.q64.emotion.lang.opcode.Chars;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.Opcodes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Singleton
public class InstructionTokenizer {
    private static final boolean isDeflateTooSlow = true;

    protected @Inject Provider<CompilerOutput> compilerOutputProvider;
    protected @Inject Provider<InstructionResult> instructionResultProvider;
    protected @Inject Opcodes opcodes;
    protected @Inject Deflate deflate;
    protected @Inject Smaz smaz;
    protected @Inject Base base;
    protected @Inject Shoco shoco;
    protected @Inject Lzma lzma;

    protected @Inject InstructionTokenizer() {}

    public InstructionResult processInstruction(String ins, int line) {
        StringBuilder result = new StringBuilder();
        Optional<List<Chars>> opt = opcodes.lookupName(ins);
        if (opt.isPresent()) {
            StringBuilder current = new StringBuilder();
            for (Chars c : opt.get()) {
                if (c == null) {
                    break;
                }
                current.append(c.getCharacter());
            }
            result.append(current.toString());
            int targetId = opcodes.lookupSymbol(current.toString()).orElse(0);
            Optional<Consumer<Stack>> executor = Optional.empty();
            for (int id : opcodes.getFlags(OpcodeMarker.PRIORITIZATION)) {
                if (targetId == id) {
                    executor = Optional.of(opcodes.getExecutor(id));
                }
            }
            if (executor.isPresent()) {
                executor.get().accept(null);
            }
            return instructionResultProvider.get().compiled(result.toString());
        }
        if (ins.startsWith("load ")) {
            String load = ins.substring(5);
            if (load.length() == 0) {
                return instructionResultProvider.get().error(Optional.of(compilerOutputProvider.get().error("Attempted to load a 0 length literal (probably an empty load instruction). Line: " + line)));
            }
            if (load.length() == 1) {
                if (load.matches("\\A\\p{ASCII}*\\z")) {
                    result.append(opcodes.getChars(OpcodeMarker.LITERAL_SINGLE).getCharacter());
                    result.append(Chars.fromInt(load.toCharArray()[0]).getCharacter());
                    return instructionResultProvider.get().compiled(result.toString());
                }
            }
            if (load.length() == 2) {
                boolean canInclude = true;
                for (char c : load.toCharArray()) {
                    if (!String.valueOf(c).matches("\\A\\p{ASCII}*\\z")) {
                        canInclude = false;
                        break;
                    }
                }
                if (canInclude) {
                    result.append(opcodes.getChars(OpcodeMarker.LITERAL_PAIR).getCharacter());
                    for (char c : load.toCharArray()) {
                        result.append(Chars.fromInt(c).getCharacter());
                    }
                    return instructionResultProvider.get().compiled(result.toString());
                }
            }
            List<String> attempts = new ArrayList<>();
            if (base.canCompress(load)) {
                byte[] compressed = base.compress(load);
                if (compressed.length <= 256 && compressed.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(opcodes.getChars(OpcodeMarker.COMPRESSION_BASE256).getCharacter());
                    sb.append(Chars.fromInt(compressed.length - 1).getCharacter());
                    for (byte b : compressed) {
                        sb.append(Chars.fromByte(b).getCharacter());
                    }
                    attempts.add(sb.toString());
                }
            }
            if (shoco.canCompress(load)) {
                byte[] compressed = shoco.compress(load);
                if (compressed.length <= 256 && compressed.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(opcodes.getChars(OpcodeMarker.COMPRESSION_SHOCO).getCharacter());
                    sb.append(Chars.fromInt(compressed.length - 1).getCharacter());
                    for (byte b : compressed) {
                        sb.append(Chars.fromByte(b).getCharacter());
                    }
                    attempts.add(sb.toString());
                }
            }
            if (smaz.canCompress(load)) {
                byte[] compressed = smaz.compress(load);
                if (compressed.length <= 256 && compressed.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(opcodes.getChars(OpcodeMarker.COMPRESSION_SMAZ).getCharacter());
                    sb.append(Chars.fromInt(compressed.length - 1).getCharacter());
                    for (byte b : compressed) {
                        sb.append(Chars.fromByte(b).getCharacter());
                    }
                    attempts.add(sb.toString());
                }
            }
            if (lzma.canCompress(load)) {
                byte[] compressed = lzma.compress(load);
                if (compressed.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(opcodes.getChars(OpcodeMarker.COMPRESSION_LZMA).getCharacter());
                    sb.append(Chars.fromInt(compressed.length - 1 >= 255 ? 255 : compressed.length - 1).getCharacter());
                    if (compressed.length - 1 >= 255) {
                        sb.append(Chars.fromInt(compressed.length - 256).getCharacter());
                    }
                    for (byte b : compressed) {
                        sb.append(Chars.fromByte(b).getCharacter());
                    }
                    attempts.add(sb.toString());
                }
            }
            if (deflate.canCompress(load)) {
                if (!isDeflateTooSlow) {
                    byte[] compressed = deflate.compress(load);
                    if (compressed.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(opcodes.getChars(OpcodeMarker.COMPRESSION_DEFLATE).getCharacter());
                        sb.append(Chars.fromInt(compressed.length - 1 >= 255 ? 255 : compressed.length - 1).getCharacter());
                        if (compressed.length - 1 >= 255) {
                            sb.append(Chars.fromInt(compressed.length - 256).getCharacter());
                        }
                        for (byte b : compressed) {
                            sb.append(Chars.fromByte(b).getCharacter());
                        }
                        attempts.add(sb.toString());
                    }
                }
            }
            List<String> less = attempts.stream().filter(s -> s.length() - 2 <= load.length()).collect(Collectors.toList());
            if (less.size() > 0) {
                result.append(Collections.min(less, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.length() - s2.length();
                    }
                }));
                return instructionResultProvider.get().compiled(result.toString());
            }
            if (attempts.size() > 0) {
                result.append(Collections.min(attempts, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.length() - s2.length();
                    }
                }));
                return instructionResultProvider.get().compiled(result.toString());
            }
            return instructionResultProvider.get().error(Optional.of(compilerOutputProvider.get().error("Failed to process literal. Line: " + line)));
        }
        return instructionResultProvider.get().error(Optional.of(compilerOutputProvider.get().error("Invalid instruction '" + ins + "' in source. Line: " + line)));
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Accessors(fluent = true)
    public static class InstructionResult {
        private Optional<CompilerOutput> error = Optional.empty();
        private String compiled = "";

        protected @Inject InstructionResult() {}
    }
}
