<<<<<<< HEAD:EmotionEngine/src/test/java/co/q64/emotion/util/OutputBuffer.java
package co.q64.emotion.util;

import co.q64.emotion.runtime.Output;

public class OutputBuffer implements Output {
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void print(String message) {
		buffer.append(message);
	}

	@Override
	public void println(String message) {
		buffer.append(message + "\n");
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

	public String firstLine() {
		if (buffer.indexOf("\n") > -1) {
			return buffer.substring(0, buffer.indexOf("\n"));
		}
		return buffer.toString();
	}
}
=======
package co.q64.emotion.util;

import co.q64.emotion.runtime.Output;

public class OutputBuffer implements Output {
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void print(String message) {
		buffer.append(message);
	}

	@Override
	public void println(String message) {
		buffer.append(message + "\n");
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

	public String firstLine() {
		if (buffer.indexOf("\n") > -1) {
			return buffer.substring(0, buffer.indexOf("\n"));
		}
		return buffer.toString();
	}
}
>>>>>>> 2bf1db3787d18788c3c2a600bb24ec56f3e5beeb:EmotionEngine/src/test/java/co/q64/emotion/util/OutputBuffer.java
