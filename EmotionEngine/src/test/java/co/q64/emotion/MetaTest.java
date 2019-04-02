package co.q64.emotion;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import co.q64.emotion.DaggerEmotionMain_EmotionMainComponent;
import co.q64.emotion.compiler.CompilerOutput;
import co.q64.emotion.util.SimpleEmotionTest;

public class MetaTest {
	@Test
	public void testSource() {
		String[] program = { "load Hello World", "meta.source", "print", "print", "terminate" };
		CompilerOutput co = DaggerEmotionMain_EmotionMainComponent.create().getJstx().compileProgram(Arrays.asList(program));
		Assert.assertEquals(true, co.isSuccess());
		new SimpleEmotionTest(program).execute(co.getProgram() + "Hello World");
	}
}
