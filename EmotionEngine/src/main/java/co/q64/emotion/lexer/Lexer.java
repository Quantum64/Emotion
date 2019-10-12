package co.q64.emotion.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.compression.Base;
import co.q64.emotion.compression.Deflate;
import co.q64.emotion.compression.Lzma;
import co.q64.emotion.compression.Shoco;
import co.q64.emotion.compression.Smaz;
import co.q64.emotion.lang.Instruction;
import co.q64.emotion.lang.InstructionFactory;
import co.q64.emotion.lang.opcode.Chars;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.lang.value.LiteralFactory;
import co.q64.emotion.runtime.Output;
import co.q64.emotion.runtime.common.ByteBuffer;

@Singleton
public class Lexer {
    protected @Inject Lexer() {}

    protected @Inject InstructionFactory instructionFactory;
    protected @Inject LiteralFactory literalFactory;
    protected @Inject Opcodes opcodes;
    protected @Inject Smaz smaz;
    protected @Inject Shoco shoco;
    protected @Inject Base base;
    protected @Inject Lzma lzma;
    protected @Inject Deflate deflate;

    public List<Instruction> parse(String program, Output output) {
        int smazToRead = 0, baseToRead = 0, shortToRead = 0, shocoToRead = 0, lzmaToRead = 0, deflateToRead = 0;
        StringBuilder currentLiteral = null;
        ByteBuffer currentBuffer = null;
        String opcodeQueue = "";
        List<Instruction> instructions = new ArrayList<Instruction>();
        String[] chars = new String[program.codePointCount(0, program.length())];
        for (int i = 0, j = 0; i < program.length(); j++) {
            int cp = program.codePointAt(i);
            char c[] = Character.toChars(cp);
            chars[j] = new String(c);
            i += Character.charCount(cp);
        }
        for (int index = 0; index < chars.length; index++) {
            String c = String.valueOf(chars[index]);
            if (opcodeQueue.isEmpty()) {
                if (shortToRead > 0) {
                    currentLiteral.append(String.valueOf((char) (Chars.fromCode(c).getId())));
                    shortToRead--;
                    if (shortToRead == 0) {
                        instructions.add(instructionFactory.create(literalFactory.create(currentLiteral.toString())));
                    }
                    continue;
                }
                if (smazToRead > 0) {
                    currentBuffer.put(Chars.fromCode(c).getByte());
                    smazToRead--;
                    if (smazToRead == 0) {
                        String decomp = smaz.decompress(currentBuffer.array());
                        instructions.add(instructionFactory.create(literalFactory.create(decomp)));
                    }
                    continue;
                }
                if (shocoToRead > 0) {
                    currentBuffer.put(Chars.fromCode(c).getByte());
                    shocoToRead--;
                    if (shocoToRead == 0) {
                        String decomp = shoco.decompress(currentBuffer.array());
                        instructions.add(instructionFactory.create(literalFactory.create(decomp)));
                    }
                    continue;
                }
                if (baseToRead > 0) {
                    currentBuffer.put(Chars.fromCode(c).getByte());
                    baseToRead--;
                    if (baseToRead == 0) {
                        instructions.add(instructionFactory.create(literalFactory.create(base.decompress(currentBuffer.array()))));
                    }
                    continue;
                }
                if (lzmaToRead > 0) {
                    currentBuffer.put(Chars.fromCode(c).getByte());
                    lzmaToRead--;
                    if (lzmaToRead == 0) {
                        instructions.add(instructionFactory.create(literalFactory.create(lzma.decompress(currentBuffer.array()))));
                    }
                    continue;
                }
                if (deflateToRead > 0) {
                    currentBuffer.put(Chars.fromCode(c).getByte());
                    deflateToRead--;
                    if (deflateToRead == 0) {
                        instructions.add(instructionFactory.create(literalFactory.create(deflate.decompress(currentBuffer.array()))));
                    }
                    continue;
                }
                if (opcodes.getChars(OpcodeMarker.COMPRESSION_BASE256).getCharacter().equals(c)) {
                    index++;
                    baseToRead = Chars.fromCode(String.valueOf(chars[index])).getId() + 1;
                    currentBuffer = new ByteBuffer(baseToRead);
                    continue;
                }
                if (opcodes.getChars(OpcodeMarker.COMPRESSION_SMAZ).getCharacter().equals(c)) {
                    index++;
                    smazToRead = Chars.fromCode(String.valueOf(chars[index])).getId() + 1;
                    currentBuffer = new ByteBuffer(smazToRead);
                    continue;
                }
                if (opcodes.getChars(OpcodeMarker.LITERAL_PAIR).getCharacter().equals(c)) {
                    currentLiteral = new StringBuilder();
                    shortToRead = 2;
                    continue;
                }
                if (opcodes.getChars(OpcodeMarker.LITERAL_SINGLE).getCharacter().equals(c)) {
                    currentLiteral = new StringBuilder();
                    shortToRead = 1;
                    continue;
                }
                if (opcodes.getChars(OpcodeMarker.COMPRESSION_SHOCO).getCharacter().equals(c)) {
                    index++;
                    shocoToRead = Chars.fromCode(String.valueOf(chars[index])).getId() + 1;
                    currentBuffer = new ByteBuffer(shocoToRead);
                    continue;
                }
                if (opcodes.getChars(OpcodeMarker.COMPRESSION_LZMA).getCharacter().equals(c)) {
                    index++;
                    lzmaToRead = Chars.fromCode(String.valueOf(chars[index])).getId() + 1;
                    if (lzmaToRead == 256) {
                        index++;
                        lzmaToRead += Chars.fromCode(String.valueOf(chars[index])).getId();
                    }
                    currentBuffer = new ByteBuffer(lzmaToRead);
                    continue;
                }
                if (opcodes.getChars(OpcodeMarker.COMPRESSION_DEFLATE).getCharacter().equals(c)) {
                    index++;
                    deflateToRead = Chars.fromCode(String.valueOf(chars[index])).getId() + 1;
                    if (deflateToRead == 256) {
                        index++;
                        deflateToRead += Chars.fromCode(String.valueOf(chars[index])).getId();
                    }
                    currentBuffer = new ByteBuffer(deflateToRead);
                    continue;
                }
            }
            Optional<Integer> oc = opcodes.lookupSymbol(opcodeQueue + c);
            if (!oc.isPresent()) {
                opcodeQueue += c;
                if (opcodeQueue.length() > 3) {
                    output.println("Lexer Warning: Unusual opcode '" + opcodeQueue + "'. There is likely a syntax error in your program.");
                }
                continue;
            }
            opcodeQueue = "";
            Instruction instruction = instructionFactory.create(oc.get());
            instructions.add(instruction);
        }
        return instructions;
    }
}
