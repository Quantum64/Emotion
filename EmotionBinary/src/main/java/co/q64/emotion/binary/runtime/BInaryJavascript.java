package co.q64.emotion.binary.runtime;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.runtime.Javascript;

@Singleton
public class BInaryJavascript implements Javascript {
	protected @Inject BInaryJavascript() {}

	@Override
	public Value evalFunction(String function, List<Value> args) {
		throw new UnsupportedOperationException("Javascript not supported");
	}
}
