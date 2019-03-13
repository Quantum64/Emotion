package co.q64.emotion.tea.runtime;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.value.LiteralFactory;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.runtime.Javascript;

@Singleton
public class GWTJavascript implements Javascript {
	protected @Inject LiteralFactory literal;

	protected @Inject GWTJavascript() {}

	@Override
	public Value evalFunction(String function, List<Value> args) {
		// TODO Auto-generated method stub
		return null;
	}
}
