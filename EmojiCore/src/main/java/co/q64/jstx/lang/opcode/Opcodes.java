package co.q64.jstx.lang.opcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.jstx.lang.Stack;
import lombok.Getter;

// 0 - 156 = 1 byte
// 157 - 24990 = 2 byte

@Singleton
public class Opcodes {
	protected @Inject Opcodes() {}

	protected @Inject OpcodeLibrary library;
	protected @Inject OpcodeCache cache;
	protected @Inject OpcodeFactory opcodeFactory;

	private @Getter List<Opcode> opcodes = new ArrayList<>();

	@Inject
	protected void init() {
		for (OpcodeRegistry registry : library.get()) {
			registry.registerExternal(this);
		}
		cache.init(opcodes);
		cache.remapOpcodes(Collections.emptyList());
	}

	public Consumer<Stack> getExecutor(int id) {
		return cache.getOpcodes().get(id);
	}

	public Optional<String> getName(int id) {
		for (Entry<String, Integer> e : cache.getNames().entrySet()) {
			if (e.getValue().equals(id)) {
				return Optional.of(e.getKey());
			}
		}
		return Optional.empty();
	}

	public int getFlag(OpcodeMarker marker) {
		return cache.getMarkers().get(marker).get(0);
	}

	public List<Integer> getFlags(OpcodeMarker marker) {
		return cache.getMarkers().get(marker);
	}

	public Chars getChars(OpcodeMarker marker) {
		return lookupChars(getFlag(marker))[0];
	}

	public String getDescription(String name) {
		return Optional.ofNullable(cache.getDescriptions().get(name)).orElse("No description available.");
	}

	public List<String> getNames() {
		return new ArrayList<>(cache.getNames().keySet());
	}

	public String getDebugInfo() {
		return "Using " + cache.getMapped() + "/" + lookupId(new Chars[] { Chars.xff, Chars.xff }).get() + " opcodes (" + (lookupId(new Chars[] { Chars.xff, Chars.xff }).get() - cache.getMapped()) + " remaining)";
	}

	public void reset() {
		cache.resetPrioritization();
	}

	public Optional<List<Chars>> lookupName(String name) {
		Integer id = cache.getNames().get(name);
		if (id == null) {
			return Optional.empty();
		}
		return Optional.of(Arrays.asList(lookupChars(id)));
	}

	public Optional<Integer> lookupSymbol(String symbol) {
		Chars[] chars = new Chars[2];
		if (symbol.length() > 0) {
			String[] ar = new String[symbol.codePointCount(0, symbol.length())];
			for (int i = 0, j = 0; i < symbol.length(); j++) {
				int cp = symbol.codePointAt(i);
				char c[] = Character.toChars(cp);
				ar[j] = new String(c);
				i += Character.charCount(cp);
			}
			chars[0] = Chars.fromCode(ar[0]);
			if (ar.length > 1) {
				chars[1] = Chars.fromCode(ar[1]);
			}
		}
		return lookupId(chars);
	}

	private Chars[] lookupChars(int id) {
		Chars[] result = new Chars[2];
		if (id < 156) {
			result[0] = Chars.fromInt(id);
			return result;
		}
		result[0] = Chars.fromInt(((id - 156) / 256) + 156);
		result[1] = Chars.fromInt((id - 156) % 256);
		return result;
	}

	private Optional<Integer> lookupId(Chars[] chars) {
		if (chars[0].getId() < 156) {
			return Optional.of(chars[0].getId());
		}
		if (chars[1] == null) {
			return Optional.empty();
		}
		int result = ((chars[0].getId() - 156) * 256 + 156);
		result += chars[1].getId();
		return Optional.of(result);
	}
}
