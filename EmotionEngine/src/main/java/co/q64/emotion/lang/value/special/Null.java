package co.q64.emotion.lang.value.special;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.types.Comparison;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Rational;

@Singleton
public class Null implements Value {
    protected @Inject Null() {}

    public Value operate(Value value, Operation type) {
        return this;
    }

    public int compareTo(Value value) {
        return -1;
    }

    public List<Value> iterate() {
        return Collections.emptyList();
    }

    public Rational asNumber() {
        return Rational.of(0);
    }

    public boolean asBoolean() {
        return false;
    }

    @Override
    public String toString() {
        return "";
    }
}
