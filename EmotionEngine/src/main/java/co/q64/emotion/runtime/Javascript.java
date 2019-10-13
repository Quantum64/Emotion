package co.q64.emotion.runtime;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;

import java.util.List;

public interface Javascript {
	public Value evalFunction(String function, List<Value> args);

	// TODO what does this even do....
	public default Value literal(Value value) {
		return Values.create("\"" + value.toString() + "\""); // TODO fix this nonsense lol
	}
}
