package co.q64.emotion;

import org.junit.Test;

import co.q64.emotion.util.SimpleEmotionTest;

public class OperationTest {
	@Test
	public void testAddition() {
		new SimpleEmotionTest("load 4", "load 5", "+").execute("9");
	}

	@Test
	public void testSubtraction() {
		new SimpleEmotionTest("load 10", "load 8", "-").execute("2");
	}

	@Test
	public void testMultiplication() {
		new SimpleEmotionTest("load 5", "load 6", "*").execute("30");
	}

	@Test
	public void testDivision() {
		new SimpleEmotionTest("load 30", "load 10", "/").execute("3");
	}
}
