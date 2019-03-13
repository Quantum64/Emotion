package co.q64.emotion.factory;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.value.LiteralFactory;
import lombok.Getter;

@Singleton
public class LiteralFactoryFactory {
	protected @Inject LiteralFactoryFactory() {}

	protected @Inject @Getter LiteralFactory factory;
}
