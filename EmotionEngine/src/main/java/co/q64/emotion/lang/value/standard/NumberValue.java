package co.q64.emotion.lang.value.standard;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Rational;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
public class NumberValue implements Value {
    private final Rational value;

    private NumberValue(long in) {
        this.value = Rational.of(in);
    }

    private NumberValue(String in) {
        this.value = Rational.of(in);
    }

    private NumberValue(String n, String d) {
        this.value = Rational.of(n, d);
    }

    private NumberValue(Rational rational) {
        this.value = rational;
    }

    public static NumberValue of(long in) {
        return new NumberValue(in);
    }

    public static NumberValue of(String in) {
        return new NumberValue(in);
    }

    public static NumberValue of(String n, String d) {
        return new NumberValue(n, d);
    }

    public static NumberValue of(Rational rational) {
        return new NumberValue(rational);
    }

    public Value operate(Value in, Operation type) {
        if (in.isNumber()) {
            switch (type) {
                case ADD:
                    return of(value.add(in.asNumber()));
                case SUBTRACT:
                    return of(value.substract(in.asNumber()));
                case MULTIPLY:
                    return of(value.multiply(in.asNumber()));
                case DIVIDE:
                    return of(value.divide(in.asNumber()));
            }
        }
        switch (type) {
            case ADD:
                return Values.create(toString() + in.toString());
            case SUBTRACT:
                return Values.create(in.toString() + toString());
            case MULTIPLY:
                return Values.create(String.join("", Collections.nCopies(value.intValue(), in.toString())));
            case DIVIDE:
                return this; // TODO
        }
        return this;
    }

    public int compareTo(Value in) {
        if (in.isNumber()) {
            return value.compareTo(in.asNumber());
        } else if (in.isBoolean()) {
            return Boolean.compare(asBoolean(), in.asBoolean());
        }
        return value.compareTo(Rational.of(in.toString().length()));
    }

    public List<Value> iterate() {
        List<Value> result = new ArrayList<>();
        for (int index = 0; index < value.intValue(); index++) {
            result.add(of(index));
        }
        return result;
    }

    public Rational asNumber() {
        return value;
    }

    public boolean asBoolean() {
        return !value.equals(Rational.ZERO);
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public String toString() {
        if (value.getDenominator().longValue() == 1) {
            return value.getNumerator().toString();
        }
        return Double.toString(value.doubleValue());
    }
}
