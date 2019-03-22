package co.q64.emotion.ast;

import com.google.auto.factory.AutoFactory;

@AutoFactory
public class ASTLoop implements ASTNode {
	private AST nodes;

	protected ASTLoop(AST nodes) {
		this.nodes = nodes;
	}

	@Override
	public ASTBackpropagation enter() {
		loop: while (true) {
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
