package co.q64.emotion.inject;

import co.q64.emotion.runtime.Javascript;
import co.q64.emotion.runtime.Output;
import co.q64.emotion.runtime.system.SystemJavascript;
import co.q64.emotion.runtime.system.SystemOutput;
import dagger.Binds;
import dagger.Module;

@Module
public interface SystemModule {
	// @formatter:off
	@Binds Output bindOutput(SystemOutput output);
	@Binds Javascript bindJavascript(SystemJavascript javascript);
	// @formatter:on
}
