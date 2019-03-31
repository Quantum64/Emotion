package co.q64.emotion;

import org.junit.Test;

import co.q64.emotion.util.SimpleEmotionTest;

public class DefTest {
	@Test
	public void testDef() {
		new SimpleEmotionTest("load 2", "load 3", "jump add", "def add", "+", "end").execute("5");
	}

	@Test
	public void testMultiFunctionWithReturn() {
		new SimpleEmotionTest(
		// @formatter:off
				"# Test factorial function several times",
				"load 2",
				"jump factorial",
				"print",
				"load 5",
				"jump factorial",
				"print",
				"load 9",
				"jump addOne",
				"jump factorial",
				"println",
				"terminate",
				"",
				"# The factorial function",
				"def factorial",
				"load 1",
				"swp",
				"iterate",
				"ldr o",
				"jump addOne",
				"*",
				"end",
				"end",
				"",
				"# Adds one to the current value on the stack",
				"def addOne",
				"load 1",
				"+",
				"end"
		// @formatter:on
		).execute("21203628800");
	}
}
