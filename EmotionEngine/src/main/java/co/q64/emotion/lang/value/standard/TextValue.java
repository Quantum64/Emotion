package co.q64.emotion.lang.value.standard;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Rational;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@EqualsAndHashCode
public class TextValue implements Value {
    private String value;

    private TextValue(String value) {
        this.value = value;
    }

    public static TextValue of(String value) {
        return new TextValue(value);
    }

    public List<Value> iterate() {
        List<Value> result = new ArrayList<>();
        for (char c : toString().toCharArray()) {
            result.add(of(String.valueOf(c)));
        }
        return result;
    }

    public Rational asNumber() {
        return Rational.of(toString().length());
    }

    public boolean asBoolean() {
        return !value.isEmpty();
    }

    public Value operate(Value value, Operation type) {
        if (value.isNumber() && type == Operation.MULTIPLY) {
            return Values.create(String.join("", Collections.nCopies(value.asNumber().intValue(), toString())));
        }
        switch (type) {
            case SUBTRACT:
                return Values.create(value.toString() + toString());
            default:
                return Values.create(toString() + value.toString());
        }
    }

    public int compareTo(Value in) {
        if (in.isNumber()) {
            return NumberValue.of(toString().length()).compareTo(in);
        }
        return toString().compareTo(in.toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
