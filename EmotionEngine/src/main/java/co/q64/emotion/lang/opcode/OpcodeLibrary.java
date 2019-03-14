package co.q64.emotion.lang.opcode;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.opcode.BigNumberOpcodes;
import co.q64.emotion.opcode.DictionaryOpcodes;
import co.q64.emotion.opcode.GraphicsOpcodes;
import co.q64.emotion.opcode.IntegerOpcodes;
import co.q64.emotion.opcode.JavascriptOpcodes;
import co.q64.emotion.opcode.ListOpcodes;
import co.q64.emotion.opcode.LongRegisterOpcodes;
import co.q64.emotion.opcode.MathOpcodes;
import co.q64.emotion.opcode.MetaOpcodes;
import co.q64.emotion.opcode.StackOpcodes;
import co.q64.emotion.opcode.StandardOpcodes;
import co.q64.emotion.opcode.StringOpcodes;

@Singleton
public class OpcodeLibrary {
	protected @Inject OpcodeLibrary() {}

	protected @Inject StandardOpcodes standard;
	protected @Inject StackOpcodes stack;
	protected @Inject StringOpcodes string;
	protected @Inject ListOpcodes list;
	protected @Inject MathOpcodes math;
	protected @Inject IntegerOpcodes integer;
	protected @Inject MetaOpcodes meta;
	protected @Inject GraphicsOpcodes graphics;
	protected @Inject BigNumberOpcodes bigNumber;
	protected @Inject JavascriptOpcodes javascript;
	protected @Inject LongRegisterOpcodes registry;
	protected @Inject DictionaryOpcodes dictionary;

	protected List<OpcodeRegistry> get() {
		return Arrays.asList(standard, stack, string, math, integer, list, meta, graphics, bigNumber, javascript, registry, dictionary);
	}
}
