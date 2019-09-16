package co.q64.emotion.binary.inject;

import co.q64.emotion.binary.runtime.BInaryJavascript;
import co.q64.emotion.runtime.Javascript;
import dagger.Binds;
import dagger.Module;

@Module
public interface BinaryModule {
	// @formatter:off
	@Binds Javascript bindJavascript(BInaryJavascript javascript);
	// @formatter:on
}
