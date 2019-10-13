package co.q64.emotion.lang.value.standard;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Rational;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BooleanValue implements Value {
    private final boolean value;

    private BooleanValue(boolean value) {
        this.value = value;
    }

    public static BooleanValue of(boolean value) {
        return new BooleanValue(value);
    }

    public int compareTo(Value in) {
        return Boolean.compare(value, in.asBoolean());
    }

    public List<Value> iterate() {
        return value ? Arrays.asList(this) : Collections.emptyList();
    }

    public Rational asNumber() {
        return value ? Rational.ONE : Rational.ZERO;
    }

    public boolean asBoolean() {
        return value;
    }

    public Value operate(Value value, Operation type) {
        if (value.isBoolean()) {
            return of(asBoolean() || value.asBoolean());
        }
        return Values.create(TextValue.of(toString()).operate(value, type));
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
