package co.q64.emotion.runtime.system;

import co.q64.emotion.annotation.GWT;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.runtime.Javascript;
import com.google.common.annotations.GwtIncompatible;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

@Singleton
@GwtIncompatible(GWT.MESSAGE)
public class SystemJavascript implements Javascript {
	private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

	protected @Inject SystemJavascript() {}

	@Override
	public Value evalFunction(String function, List<Value> args) {
		StringBuilder payload = new StringBuilder("f=");
		payload.append(function);
		payload.append("; f");
		for (Value v : args) {
			payload.append("(");
			payload.append(v);
			payload.append("");
		}
		try {
			return Values.create(engine.eval(payload.toString()).toString());
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}
}
