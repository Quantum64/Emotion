package co.q64.emotion.opcode;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.Program;
import co.q64.emotion.lang.Registers;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.OpcodeRegistry;
import co.q64.emotion.lang.value.MatrixFactory;
import co.q64.emotion.lang.value.Value;

@Singleton
public class DebugOpcodes extends OpcodeRegistry {
	protected @Inject MatrixFactory matrix;
	protected @Inject Registers registers;

	protected @Inject DebugOpcodes() {
		super(OpcodeMarker.DEBUG);
	}

	@Override
	public void register() {
		r("debug.memory", stack -> {
			Program p = stack.getProgram();
			p.println("=========");
			p.println("= Stack =");
			p.println("=========");
			for (Value v : stack.getStack()) {
				p.println(v.toString());
			}
			p.println("=============");
			p.println("= Registers =");
			p.println("=============");
			p.println("a: " + registers.getA().toString());
			p.println("b: " + registers.getB().toString());
			p.println("c: " + registers.getC().toString());
			p.println("d: " + registers.getD().toString());
			p.println("i: " + registers.getI().toString());
			p.println("o: " + registers.getO().toString());
		});
	}
}
