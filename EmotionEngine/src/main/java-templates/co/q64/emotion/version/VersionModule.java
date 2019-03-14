package co.q64.emotion.version;

import co.q64.emotion.annotation.Constants.Version;
import dagger.Module;
import dagger.Provides;

@Module
public interface VersionModule {
	static @Provides @Version String bindVersion() { return "${project.version}"; }
}