package co.q64.emotion;

import org.junit.Test;

import co.q64.emotion.util.SimpleEmotionTest;

public class MathTest {
	@Test
	public void testE() {
		new SimpleEmotionTest("math.e").execute(Math.E);
	}
	
	@Test
	public void testPi() {
		new SimpleEmotionTest("math.pi").execute(Math.PI);
	}
}
