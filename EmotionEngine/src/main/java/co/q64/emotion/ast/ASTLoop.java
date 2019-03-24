package co.q64.emotion.ast;

import com.google.auto.factory.AutoFactory;

import co.q64.emotion.lang.Program;

@AutoFactory
public class ASTLoop implements ASTNode {
	private Program program;
	private AST nodes;

	protected ASTLoop(Program program, AST nodes) {
		this.program = program;
		this.nodes = nodes;
	}

	@Override
	public ASTBackpropagation enter() {
		loop: while (true) {
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
}
