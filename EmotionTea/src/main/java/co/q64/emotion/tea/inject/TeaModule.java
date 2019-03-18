package co.q64.emotion.tea.inject;

import co.q64.emotion.runtime.Javascript;
import co.q64.emotion.tea.runtime.TeaJavascript;
import dagger.Binds;
import dagger.Module;

@Module
public interface TeaModule {
	// @formatter:off
	@Binds Javascript bindJavascript(TeaJavascript javascript);
	// @formatter:on
}
