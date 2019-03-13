package co.q64.emoji.tea.inject;

import co.q64.emoji.tea.runtime.GWTGraphics;
import co.q64.emoji.tea.runtime.GWTJavascript;
import co.q64.jstx.runtime.Graphics;
import co.q64.jstx.runtime.Javascript;
import dagger.Binds;
import dagger.Module;

@Module
public interface TeaModule {
	// @formatter:off
	@Binds Graphics bindGraphics(GWTGraphics graphics);
	@Binds Javascript bindJavascript(GWTJavascript javascript);
	// @formatter:on
}
