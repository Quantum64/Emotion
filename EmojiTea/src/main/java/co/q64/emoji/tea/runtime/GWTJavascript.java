package co.q64.emoji.tea.runtime;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.jstx.lang.value.LiteralFactory;
import co.q64.jstx.lang.value.Value;
import co.q64.jstx.runtime.Javascript;

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
