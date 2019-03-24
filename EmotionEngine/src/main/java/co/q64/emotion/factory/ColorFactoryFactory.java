package co.q64.emotion.factory;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.util.ColorFactory;
import lombok.Getter;

@Singleton
public class ColorFactoryFactory {
	protected @Inject ColorFactoryFactory() {}

	protected @Inject @Getter ColorFactory factory;
}
