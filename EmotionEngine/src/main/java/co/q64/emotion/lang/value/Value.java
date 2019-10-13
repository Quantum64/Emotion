package co.q64.emotion.lang.value;

import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Rational;

import java.util.List;

public interface Value extends Comparable<Value> {
    public Value operate(Value value, Operation type);

    public int compareTo(Value value);

    public List<Value> iterate();

    public Rational asNumber();

    public boolean asBoolean();

    public default boolean isBoolean() {
        return false;
    }

    public default boolean isList() {
        return false;
    }

    public default boolean isNumber() {
        return false;
    }

    public default int asInt() {
        return asNumber().intValue();
    }

    public default long asLong() {
        return asNumber().longValue();
    }

    public default double asDouble() {
        return asNumber().doubleValue();
    }

    public default char asChar() {
        if (toString().length() > 0) {
            return toString().toCharArray()[0];
        }
        return 0;
    }
}
