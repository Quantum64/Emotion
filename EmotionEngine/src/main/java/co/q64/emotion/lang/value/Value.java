package co.q64.emotion.lang.value;

import java.util.List;

import co.q64.emotion.types.Comparison;
import co.q64.emotion.types.Operation;

public interface Value {
	public boolean compare(Value value, Comparison type);

	public Value operate(Value value, Operation type);

	public List<Value> iterate();

	public int asInt();

	public long asLong();

	public char asChar();

	public double asDouble();

	public boolean asBoolean();

	public default boolean isBoolean() {
		return false;
	}

	public default boolean isList() {
		return false;
	}

	public default boolean isFloat() {
		return false;
	}

	public default boolean isInteger() {
		return false;
	}
}
