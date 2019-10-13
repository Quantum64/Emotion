package co.q64.emotion.lang;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import lombok.Setter;

import javax.inject.Inject;
import java.util.ListIterator;

public class Iterator {
    private ListIterator<Value> values;
    private Registers registers;
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
            i = Values.create(index);
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
