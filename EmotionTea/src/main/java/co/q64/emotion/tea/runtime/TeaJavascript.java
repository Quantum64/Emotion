package co.q64.emotion.tea.runtime;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.runtime.Javascript;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class TeaJavascript implements Javascript {
	protected @Inject TeaJavascript() {}

	@Override
	public Value evalFunction(String function, List<Value> args) {
		StringBuilder payload = new StringBuilder("f=");
		payload.append(function.replace("\\n", "\n"));
		payload.append("; f");
		for (Value v : args) {
			payload.append("(");
			payload.append(v);
			payload.append(")");
		}
		if (args.size() == 0) {
			payload.append("()");
		}
		return Values.create(eval(payload.toString()) + "");
	}

	@JSBody(params = { "emotionFunc" }, script = "return eval(emotionFunc);")
	private static native JSObject eval(String func);
}
