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
	private AST nodes;
	private boolean stack;

	protected @Inject ASTIterator(@Provided IteratorFactoryFactory iteratorFactoryFactory, @Nullable Program program, AST nodes, boolean stack) {
		this.iteratorFactory = iteratorFactoryFactory.getFactory();
		this.program = program;
		this.nodes = nodes;
		this.stack = stack;
	}

	@Override
	public ASTBackpropagation enter() {
		Iterator itr = iteratorFactory.create(program, stack);
		loop: while (!itr.next()) {
			if (!program.shouldContinueExecution()) {
				break;
			}
			for (ASTNode node : nodes.getNodes()) {
				switch (node.enter()) {
				case BREAK:
					break loop;
				case CONTINUE:
					continue loop;
				default:
					break;
				}
			}
		}
		return ASTBackpropagation.NONE;
	}

	@Override
	public String toString() {
		return "(iterate " + nodes.toString() + ")";
	}
}
