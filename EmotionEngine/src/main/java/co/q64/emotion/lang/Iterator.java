package co.q64.emotion.lang;

import java.util.ListIterator;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import co.q64.emotion.factory.LiteralFactoryFactory;
import co.q64.emotion.lang.value.LiteralFactory;
import co.q64.emotion.lang.value.Value;
import lombok.Setter;

import javax.inject.Inject;

public class Iterator {
    private ListIterator<Value> values;
    private Registers registers;
    protected @Inject LiteralFactory literal;
    private int index;
    private Value o, i;

    private @Setter Program program;
    private @Setter boolean onStack;

    protected @Inject Iterator() {}

    public Iterator setup(Program program, boolean onStack) {
        this.program = program;
        this.onStack = onStack;
        this.registers = program.getRegisters();
        this.index = 0;
        this.values = program.getStack().pop().iterate().listIterator();
        return this;
    }

    public boolean next() {
        if (values.hasNext()) {
            i = literal.create(index);
            o = values.next();
            register();
            if (onStack) {
                program.getStack().push(registers.getO());
            }
            index++;
            return false;
        }
        return true;
    }

    public void register() {
        registers.setI(i);
        registers.setO(o);
    }
}
