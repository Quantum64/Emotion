package co.q64.emotion.runtime.mock;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.runtime.Output;

@Singleton
public class MockOutput implements Output {
	protected @Inject MockOutput() {}

	@Override
	public void print(String message) {}

	@Override
	public void println(String message) {}
}
