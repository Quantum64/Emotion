package co.q64.emotion.lang;

import java.util.ListIterator;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import co.q64.emotion.factory.LiteralFactoryFactory;
import co.q64.emotion.lang.value.LiteralFactory;
import co.q64.emotion.lang.value.Value;

@AutoFactory
public class Iterator {
	private ListIterator<Value> values;
	private Registers registers;
	private LiteralFactory literal;
	private Program program;
	private boolean onStack;
	private int index;
	private Value o, i;

	protected Iterator(@Provided LiteralFactoryFactory literal, Program program, boolean onStack) {
		this.program = program;
		this.registers = program.getRegisters();
		this.onStack = onStack;
		this.literal = literal.getFactory();
		this.index = 0;
		this.values = program.getStack().pop().iterate().listIterator();
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
