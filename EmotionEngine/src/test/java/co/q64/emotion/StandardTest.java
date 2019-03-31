package co.q64.emotion;

import org.junit.Test;

import co.q64.emotion.util.SimpleEmotionTest;

public class StandardTest {
	@Test
	public void testLoadIncompressibleString() {
		new SimpleEmotionTest("load H1e2l3l4o5 6W7o8r9l0d!").execute("H1e2l3l4o5 6W7o8r9l0d!");
	}

	@Test
	public void testLoadLowercaseString() {
		new SimpleEmotionTest("load hello world").execute("hello world");
	}

	@Test
	public void testLoadTitlecaseString() {
		new SimpleEmotionTest("load Hello World").execute("Hello World");
	}

	@Test
	public void testLoadSingleChar() {
		new SimpleEmotionTest("load Q").execute("Q");
	}

	@Test
	public void testLoadDoubleChar() {
		new SimpleEmotionTest("load 64").execute("64");
	}

	@Test
	public void testLoadLargeNumber() {
		new SimpleEmotionTest("load 1234567890987654321").execute("1234567890987654321");
	}
	
	@Test
	public void testHighlyCompressable() {
		String value = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		new SimpleEmotionTest("load " + value).execute(value);
	}
}
