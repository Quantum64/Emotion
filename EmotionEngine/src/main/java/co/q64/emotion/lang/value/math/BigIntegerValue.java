package co.q64.emotion.lang.value.math;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Rational;
import lombok.Getter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BigIntegerValue implements Value {
    private @Getter BigInteger value;

    private BigIntegerValue(BigInteger value) {
        this.value = value;
    }

    public static BigIntegerValue of(BigInteger value) {
        return new BigIntegerValue(value);
    }

    public static BigIntegerValue of(String value) {
        return new BigIntegerValue(new BigInteger(value));
    }

    @Override
    public int compareTo(Value to) {
        int result = -1;
        if (to instanceof BigIntegerValue) {
            result = value.compareTo(((BigIntegerValue) to).getValue());
        } else if (to.isInteger()) {
            result = value.compareTo(BigInteger.valueOf(to.asLong()));
        }
        return result;
    }

    @Override
    public Value operate(Value on, Operation type) {
        BigInteger target = null;
        if (on instanceof BigIntegerValue) {
            target = ((BigIntegerValue) on).getValue();
        } else if (on.isInteger()) {
            target = BigInteger.valueOf(on.asLong());
        }
        if (target == null) {
            return this;
        }
        switch (type) {
            case DIVIDE:
                return new BigIntegerValue(value.divide(target));
            case SUBTRACT:
                return new BigIntegerValue(value.subtract(target));
            case MULTIPLY:
                return new BigIntegerValue(value.multiply(target));
            case ADD:
                return new BigIntegerValue(value.add(target));
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

    public Value and(Value target) {
        return new BigIntegerValue(value.and(convert(target)));
    }

    public Value andNot(Value target) {
        return new BigIntegerValue(value.andNot(convert(target)));
    }

    public List<Value> divideAndRemainder(Value target) {
        return Arrays.stream(value.divideAndRemainder(convert(target))).map(v -> new BigIntegerValue(v)).collect(Collectors.toList());
    }

    public Value gcd(Value target) {
        return new BigIntegerValue(value.gcd(convert(target)));
    }

    public Value max(Value target) {
        return new BigIntegerValue(value.max(convert(target)));
    }

    public Value min(Value target) {
        return new BigIntegerValue(value.min(convert(target)));
    }

    public Value mod(Value target) {
        return new BigIntegerValue(value.mod(convert(target)));
    }

    public Value modInverse(Value target) {
        return new BigIntegerValue(value.modInverse(convert(target)));
    }

    public Value modPow(Value target, Value pow) {
        return new BigIntegerValue(value.modPow(convert(target), convert(pow)));
    }

    public Value or(Value target) {
        return new BigIntegerValue(value.or(convert(target)));
    }

    public Value remainder(Value target) {
        return new BigIntegerValue(value.min(convert(target)));
    }

    public Value xor(Value target) {
        return new BigIntegerValue(value.xor(convert(target)));
    }

    private BigInteger convert(Value value) {
        if (value instanceof BigIntegerValue) {
            return ((BigIntegerValue) value).getValue();
        }
        if (value.isInteger()) {
            return BigInteger.valueOf(value.asLong());
        }
        throw new IllegalArgumentException("Could not convert " + value + " to a BigInteger");
    }

    @Override
    public Rational asNumber() {
        return Rational.of(value.toString(), "1");
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
