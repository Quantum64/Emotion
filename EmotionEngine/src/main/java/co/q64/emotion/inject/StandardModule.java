package co.q64.emotion.inject;

import co.q64.emotion.EmotionInfo;
import co.q64.emotion.annotation.Constants.Author;
import co.q64.emotion.annotation.Constants.Name;
import co.q64.emotion.annotation.Constants.Version;
import co.q64.emotion.runtime.Graphics;
import co.q64.emotion.runtime.graphics.NewGraphics;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public interface StandardModule {
	// @formatter:off
	@Binds Graphics bindGraphics(NewGraphics graphics);
	
	static @Provides @Name String provideName() { return EmotionInfo.name; }
	static @Provides @Author String provideAuthor() { return EmotionInfo.author; }
	static @Provides @Version String provideVersion() { return EmotionInfo.version; }
	// @formatter:on
}
