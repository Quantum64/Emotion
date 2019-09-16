package co.q64.emotion.binary;

import java.util.Arrays;

import co.q64.emotion.binary.inject.DaggerEmotionComponent;
import co.q64.emotion.binary.inject.EmotionComponent;
import co.q64.emotion.compiler.CompilerOutput;
import co.q64.emotion.runtime.Output;

public class EmotionBinary {

	public static void main(String[] args) {
		EmotionComponent component = DaggerEmotionComponent.create();
		CompilerOutput co = component.getCompiler().compile(Arrays.asList("load Hello World!"));
		component.getEngine().runProgram(co.getProgram(), "", new Output() {
			
			@Override
			public void println(String message) {
				System.out.println(message);
			}
			
			@Override
			public void print(String message) {
				System.out.print(message);
			}
		});
	}
}
