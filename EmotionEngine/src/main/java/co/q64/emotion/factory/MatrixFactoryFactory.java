package co.q64.emotion.factory;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.value.MatrixFactory;
import lombok.Getter;

@Singleton
public class MatrixFactoryFactory {
	protected @Inject MatrixFactoryFactory() {}

	protected @Inject @Getter MatrixFactory factory;
}
