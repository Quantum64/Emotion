package co.q64.emotion;

import java.util.Arrays;

import co.q64.emotion.EmotionMain.EmotionMainComponent;
import co.q64.emotion.runtime.Output;

public class WhyIsThisBroken {
	public static void main(String[] args) {
		EmotionMainComponent emc = DaggerEmotionMain_EmotionMainComponent.create();
		EmotionEngine ee = emc.getJstx();
		String prog = ee.compileProgram(Arrays.asList("load lol ")).getProgram();
		ee.runProgram(prog, "", new Output() {

			@Override
			public void println(String message) {}

			@Override
			public void print(String message) {}
		});
	}
}
