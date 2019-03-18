package co.q64.emotion.ast;

import javax.inject.Inject;

import com.google.auto.factory.AutoFactory;

@AutoFactory
public class ASTFunction implements ASTNode {
	private ASTNode nodes;

	protected @Inject ASTFunction(ASTNode nodes) {
		this.nodes = nodes;
	}

	@Override
	public void enter() {}

	public void enterFunction() {
		nodes.enter();
	}

	@Override
	public String toString() {
		return "<function " + nodes.toString() + ">";
	}
}
