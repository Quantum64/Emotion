package co.q64.emotion.factory;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.IteratorFactory;
import lombok.Getter;

@Singleton
public class IteratorFactoryFactory {
	protected @Inject IteratorFactoryFactory() {}

	protected @Inject @Getter IteratorFactory factory;
}
