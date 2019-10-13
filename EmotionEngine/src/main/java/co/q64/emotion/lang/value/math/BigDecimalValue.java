package co.q64.emotion.lang.value.math;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Rational;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BigDecimalValue implements Value {
    private @Getter BigDecimal value;

    private BigDecimalValue(BigDecimal value) {
        this.value = value;
    }

    public static BigDecimalValue of(BigDecimal value) {
        return new BigDecimalValue(value);
    }

    public static BigDecimalValue of(String value) {
        return new BigDecimalValue(new BigDecimal(value));
    }

    @Override
    public int compareTo(Value to) {
        int result = -1;
        if (to instanceof BigDecimalValue) {
            result = value.compareTo(((BigDecimalValue) to).getValue());
        } else if (to.isInteger()) {
            result = value.compareTo(BigDecimal.valueOf(to.asLong()));
        }
        return result;
    }

    @Override
    public Value operate(Value on, Operation type) {
        BigDecimal target = null;
        if (on instanceof BigDecimalValue) {
            target = ((BigDecimalValue) on).getValue();
        } else if (on.isNumber()) {
            target = BigDecimal.valueOf(on.asDouble());
        }
        if (target == null) {
            return this;
        }
        switch (type) {
            case DIVIDE:
                return new BigDecimalValue(value.divide(target));
            case SUBTRACT:
                return new BigDecimalValue(value.subtract(target));
            case MULTIPLY:
                return new BigDecimalValue(value.multiply(target));
            case ADD:
                return new BigDecimalValue(value.add(target));
        }
        return this;
    }

    // Should this operation even be allowed?...
    @Override
    public List<Value> iterate() {
        List<Value> result = new ArrayList<>();
        for (long l = 0; l < getValue().longValue(); l++) {
            result.add(Values.create(l));
        }
        return result;
    }

    @Override
    public Rational asNumber() {
        return Rational.of(value.doubleValue());
    }

    @Override
    public boolean asBoolean() {
        return value.intValue() != 0;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean isList() {
        return false;
    }
}
