package co.q64.emotion.lang.value.standard;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Rational;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class ListValue implements Value {
    private final List<Value> values;

    private ListValue(List<Value> values) {
        this.values = values;
    }

    public static ListValue of(List<?> in) {
        List<Value> values = new ArrayList<>();
        for (Object object : in) {
            if (in instanceof Value) {
                values.add((Value) object);
            } else {
                values.add(Values.create(object.toString()));
            }
        }
        return new ListValue(values);
    }

    public Value operate(Value value, Operation type) {
        return null;
    }

    public int compareTo(Value value) {
        if (value.isList()) {
            return values.equals(value.iterate()) ? 0 : -1;
        }
        return -1;
    }

    public List<Value> iterate() {
        return new ArrayList<>(values);
    }

    public Rational asNumber() {
        return Rational.of(values.size());
    }

    public boolean asBoolean() {
        return !values.isEmpty();
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public String toString() {
        return "[" + values.stream().map(Value::toString).collect(Collectors.joining(",")) + "]";
    }
}
