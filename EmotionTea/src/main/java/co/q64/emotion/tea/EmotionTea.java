package co.q64.emotion.tea;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

import co.q64.emotion.tea.inject.DaggerEmotionComponent;
import co.q64.emotion.compiler.CompilerOutput;
import co.q64.emotion.tea.inject.EmotionComponent;

public class EmotionTea {
	EmotionComponent component;

	public static void main(String[] args) {
		EmotionTea instance = new EmotionTea();
		instance.init();
	}

	private void init() {
		component = DaggerEmotionComponent.create();
		setFunctions(program -> component.getCompiler().compile(Arrays.asList(program.replaceAll("\\r", "").split("\n"))).getDisplayOutput().stream().collect(Collectors.joining("\n")), (program, args) -> {
			CompilerOutput compiled = component.getCompiler().compile(Arrays.asList(program.replaceAll("\\r", "").split("\n")));
			if (!compiled.isSuccess()) {
				return "Fatal: Could not run program due to compiler error!";
			}
			OutputBuffer buffer = new OutputBuffer();
			component.getEngine().runProgram(compiled.getProgram(), args, buffer);
			return buffer.toString();
		}, () -> component.getOpcodes().getNames().toArray(new String[0]), name -> component.getOpcodes().getDescription(name));
	}

	@JSFunctor
	@FunctionalInterface
	private static interface Compile extends JSObject {
		public String compile(String program);
	}

	@JSFunctor
	@FunctionalInterface
	private static interface Execute extends JSObject {
		public String execute(String program, String args);
	}

	@JSFunctor
	@FunctionalInterface
	private static interface GetOpcodes extends JSObject {
		public String[] getOpcodes();
	}

	@JSFunctor
	@FunctionalInterface
	private static interface GetOpcodeName extends JSObject {
		public String getOpcodeName(String opcode);
	}

	@JSBody(params = { "emotionCompiler", "emotionExecutor", "emotionGetOpcodes", "emotionGetOpcodeName" }, //
			script = "window.compile = emotionCompiler;window.execute = emotionExecutor;window.getOpcodes = emotionGetOpcodes; window.getOpcodeName = emotionGetOpcodeName;")
	private static native void setFunctions(Compile compile, Execute execute, GetOpcodes getOpcodes, GetOpcodeName getOpcodeName);
}
