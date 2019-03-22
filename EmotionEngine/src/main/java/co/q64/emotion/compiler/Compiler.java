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
	private static final boolean isDeflateTooSlow = true;

	protected @Inject Compiler() {}

	protected @Inject Opcodes opcodes;
	protected @Inject OpcodeCache cache;
	protected @Inject Smaz smaz;
	protected @Inject Base base;
	protected @Inject Shoco shoco;
	protected @Inject Lzma lzma;
	protected @Inject Deflate deflate;
	protected @Inject CompilerOutputFactory output;

	public CompilerOutput compile(List<String> input) {
		cache.resetPrioritization();
		List<String> instructions = new ArrayList<>();
		for (String current : input) {
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
		List<String> compiledInsns = new ArrayList<>();
		List<String> functions = new ArrayList<>();
		for (ListIterator<String> itr = instructions.listIterator(); itr.hasNext();) {
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
				Optional<CompilerOutput> output = processInstruction(instruction, compiled, line);
				if (output.isPresent()) {
					return output.get();
				}
			}
			compiledInsns.add(compiled.toString());
		}
		int debt = 0;
		for (ListIterator<String> itr = compiledInsns.listIterator(); itr.hasNext();) {
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
		return output.create(compiledInsns, instructions);
	}

	private Optional<CompilerOutput> processInstruction(String ins, StringBuilder result, int line) {
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
			return Optional.empty();
		}
		if (ins.startsWith("load ")) {
			String load = ins.substring(5);
			if (load.length() == 0) {
				return Optional.of(output.create("Attempted to load a 0 length literal (probably an empty load instruction). Line: " + line));
			}
			if (load.length() == 1) {
				if (load.matches("\\A\\p{ASCII}*\\z")) {
					result.append(opcodes.getChars(OpcodeMarker.LITERAL_SINGLE).getCharacter());
					result.append(Chars.fromInt(load.toCharArray()[0]).getCharacter());
					return Optional.empty();
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
					return Optional.empty();
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
				return Optional.empty();
			}
			if (attempts.size() > 0) {
				result.append(Collections.min(attempts, new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {
						return s1.length() - s2.length();
					}
				}));
				return Optional.empty();
			}
			return Optional.of(output.create("Failed to process literal. Line: " + line));
		}
		return Optional.of(output.create("Invalid instruction '" + ins + "' in source. Line: " + line));
	}
}
