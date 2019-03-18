package co.q64.emotion.ast;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import co.q64.emotion.factory.IteratorFactoryFactory;
import co.q64.emotion.lang.Iterator;
import co.q64.emotion.lang.IteratorFactory;
import co.q64.emotion.lang.Program;

@AutoFactory
public class ASTIterator implements ASTNode {
	private IteratorFactory iteratorFactory;
	private Program program;
	private ASTNode nodes;
	private boolean stack;

	protected @Inject ASTIterator(@Provided IteratorFactoryFactory iteratorFactoryFactory, @Nullable Program program, ASTNode nodes, boolean stack) {
		this.iteratorFactory = iteratorFactoryFactory.getFactory();
		this.program = program;
		this.nodes = nodes;
		this.stack = stack;
	}

	@Override
	public void enter() {
		Iterator itr = iteratorFactory.create(program, stack);
		while (!itr.next()) {
			if (!program.shouldContinueExecution()) {
				break;
			}
			nodes.enter();
		}
	}

	@Override
	public String toString() {
		return "(iterate " + nodes.toString() + ")";
	}
}
