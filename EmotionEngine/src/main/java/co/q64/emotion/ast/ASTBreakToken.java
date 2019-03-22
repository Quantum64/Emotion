package co.q64.emotion.ast;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ASTBreakToken implements ASTNode {
	protected @Inject ASTBreakToken() {}

	@Override
	public ASTBackpropagation enter() {
		return ASTBackpropagation.BREAK;
	}
}
