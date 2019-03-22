package co.q64.emotion.ast;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ASTContinueToken implements ASTNode {
	protected @Inject ASTContinueToken() {}

	@Override
	public ASTBackpropagation enter() {
		return ASTBackpropagation.CONTINUE;
	}
}
