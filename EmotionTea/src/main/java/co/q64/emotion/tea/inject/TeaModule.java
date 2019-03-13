package co.q64.emotion.tea.inject;

import co.q64.emotion.runtime.Graphics;
import co.q64.emotion.runtime.Javascript;
import co.q64.emotion.tea.runtime.GWTGraphics;
import co.q64.emotion.tea.runtime.GWTJavascript;
import dagger.Binds;
import dagger.Module;

@Module
public interface TeaModule {
	// @formatter:off
	@Binds Graphics bindGraphics(GWTGraphics graphics);
	@Binds Javascript bindJavascript(GWTJavascript javascript);
	// @formatter:on
}
