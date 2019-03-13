package co.q64.emoji.tea;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

import co.q64.emoji.tea.inject.DaggerJstxComponent;
import co.q64.emoji.tea.inject.JstxComponent;
import co.q64.jstx.compiler.CompilerOutput;

public class EmojiTea {
	JstxComponent component;

	public static void main(String[] args) {
		EmojiTea instance = new EmojiTea();
		instance.init();
	}

	private void init() {
		component = DaggerJstxComponent.create();
		setFunctions(program -> component.getCompiler().compile(Arrays.asList(program.replaceAll("\\r", "").split("\n"))).getDisplayOutput().stream().collect(Collectors.joining("\n")), (program, args) -> {
			CompilerOutput compiled = component.getCompiler().compile(Arrays.asList(program.replaceAll("\\r", "").split("\n")));
			if (!compiled.isSuccess()) {
				return "Fatal: Could not run program due to compiler error!";
			}
			OutputBuffer buffer = new OutputBuffer();
			component.getJstx().runProgram(compiled.getProgram(), args, buffer);
			return buffer.toString();
		});
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

	@JSBody(params = { "emojiCompiler", "emojiExecutor" }, script = "window.compile = emojiCompiler;window.execute = emojiExecutor;")
	private static native void setFunctions(Compile compile, Execute execute);
}
