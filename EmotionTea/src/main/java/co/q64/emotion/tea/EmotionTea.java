package co.q64.emotion.tea;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

import co.q64.emotion.compiler.CompilerOutput;
import co.q64.emotion.lang.Instruction;
import co.q64.emotion.lang.opcode.Chars;
import co.q64.emotion.tea.inject.DaggerEmotionComponent;
import co.q64.emotion.tea.inject.EmotionComponent;

public class EmotionTea {
	EmotionComponent component;

	public static void main(String[] args) {
		EmotionTea instance = new EmotionTea();
		instance.init();
	}

	private void init() {
		component = DaggerEmotionComponent.create();
		Emotion emotion = createEmotion();
		emotion.setCompile(program -> {
			CompilerOutput co = component.getCompiler().compile(Arrays.asList(program.replaceAll("\\r", "").split("\n")));
			CompiledCode cc = createCompiledCode();
			cc.setOutput(co.getDisplayOutput().stream().collect(Collectors.joining("\n")));
			if (co.isSuccess()) {
				cc.setProgram(co.getProgram());
			}
			return cc;
		});
		emotion.setDecompile(program -> {
			OutputBuffer output = new OutputBuffer();
			for (Instruction insn : component.getLexer().parse(program, output)) {
				output.println(insn.getInstruction());
			}
			return output.toString();
		});
		emotion.setExecute((program, args) -> {
			CompilerOutput compiled = component.getCompiler().compile(Arrays.asList(program.replaceAll("\\r", "").split("\n")));
			if (!compiled.isSuccess()) {
				return "Fatal: Could not run program due to compiler error!";
			}
			OutputBuffer buffer = new OutputBuffer();
			component.getEngine().runProgram(compiled.getProgram(), args, buffer);
			return buffer.toString();
		});
		emotion.setGetOpcodeDescription(name -> component.getOpcodes().getDescription(name));
		emotion.setGetOpcodes(() -> component.getOpcodes().getNames().toArray(new String[0]));
		emotion.setGetCodepage(() -> {
			return Arrays.asList(Chars.values()).stream().map(Chars::getCharacter).collect(Collectors.toList()).toArray(new String[0]);
		});
		setEmotion(emotion);
	}

	private static interface Emotion extends JSObject {
		public @JSProperty Compile getCompile();

		public @JSProperty void setCompile(Compile compile);

		public @JSProperty Execute getExecute();

		public @JSProperty void setExecute(Execute execute);

		public @JSProperty Decompile getDecompile();

		public @JSProperty void setDecompile(Decompile decompile);

		public @JSProperty GetOpcodes getGetOpcodes();

		public @JSProperty void setGetOpcodes(GetOpcodes getOpcodes);

		public @JSProperty GetOpcodeDescription getGetOpcodeDescription();

		public @JSProperty void setGetOpcodeDescription(GetOpcodeDescription getOpcodeDescription);

		public @JSProperty GetCodepage getGetCodepage();

		public @JSProperty void setGetCodepage(GetCodepage getCodepage);
	}

	private @JSFunctor @FunctionalInterface static interface Compile extends JSObject {
		public CompiledCode compile(String program);
	}

	private @JSFunctor @FunctionalInterface static interface Execute extends JSObject {
		public String execute(String program, String args);
	}

	private @JSFunctor @FunctionalInterface static interface Decompile extends JSObject {
		public String execute(String program);
	}

	private @JSFunctor @FunctionalInterface static interface GetOpcodes extends JSObject {
		public String[] getOpcodes();
	}

	private @JSFunctor @FunctionalInterface static interface GetOpcodeDescription extends JSObject {
		public String getOpcodeDescription(String opcode);
	}

	private @JSFunctor @FunctionalInterface static interface GetCodepage extends JSObject {
		public String[] getCodepage();
	}

	private @JSFunctor @FunctionalInterface static interface DebuggerInit extends JSObject {
		public String[] getCodepage();
	}
	
	private @JSFunctor @FunctionalInterface static interface DebuggerStep extends JSObject {
		public String[] getCodepage();
	}

	private static interface CompiledCode extends JSObject {
		public @JSProperty String getProgram();

		public @JSProperty void setProgram(String program);

		public @JSProperty String getOutput();

		public @JSProperty void setOutput(String output);
	}

	@JSBody(params = { "emotionEngine" }, script = "window.emotion = emotionEngine;")
	private static native void setEmotion(Emotion emotion);

	@JSBody(script = "return {};")
	private static native CompiledCode createCompiledCode();

	@JSBody(script = "return {};")
	private static native Emotion createEmotion();
}
