package co.q64.emoji.tea;

import java.util.Arrays;

import co.q64.emoji.tea.inject.DaggerJstxComponent;
import co.q64.emoji.tea.inject.JstxComponent;
import co.q64.jstx.compiler.CompilerOutput;
import co.q64.jstx.runtime.Output;

public class EmojiTea {
	JstxComponent component;

	public static void main(String[] args) {
		EmojiTea instance = new EmojiTea();
		instance.init();
	}

	private void init() {
		component = DaggerJstxComponent.create();
		CompilerOutput output = component.getCompiler().compile(Arrays.asList("load hello", "load  world", "+"));
		String program = output.getProgram();
		System.out.println("Compiled program: " + program);
		component.getJstx().runProgram(program, "", new TestOutput());
	}
	
	static class TestOutput implements Output {

		@Override
		public void print(String message) {
			System.out.print(message);
		}

		@Override
		public void println(String message) {
			System.out.println(message);
		}
	}
}
