package co.q64.emotion.opcode;

import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.OpcodeRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LongRegisterOpcodes extends OpcodeRegistry {
	protected @Inject LongRegisterOpcodes() {
		super(OpcodeMarker.LONG_REGISTER);
	}

	@Override
	public void register() {
		for (int i = 0; i < 256; i++) {
			final int lock = i;
			String register = "0x" + (i < 16 ? "0" : "") + Integer.toHexString(i);
			r("ldr " + register, stack -> stack.push(stack.getProgram().getRegisters().getLongRegister()[lock]));
			r("sdr " + register, stack -> stack.getProgram().getRegisters().getLongRegister()[lock] = stack.pop());
		}
	}
}
