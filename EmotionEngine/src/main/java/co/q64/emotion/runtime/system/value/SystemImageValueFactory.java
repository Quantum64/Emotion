package co.q64.emotion.runtime.system.value;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.annotations.GwtIncompatible;

import co.q64.emotion.annotation.GWT;

@Singleton
@GwtIncompatible(GWT.MESSAGE)
public class SystemImageValueFactory {
	protected @Inject SystemImageValueFactory() {}

	public SystemImageValue create(int w, int h) {
		return new SystemImageValue(w, h);
	}
}
