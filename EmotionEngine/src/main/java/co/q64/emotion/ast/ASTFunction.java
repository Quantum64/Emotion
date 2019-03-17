package co.q64.emotion.ast;

import javax.inject.Inject;

import com.google.auto.factory.AutoFactory;

import co.q64.emotion.lang.Program;

@AutoFactory
public class ASTFunction implements ASTNode {
	private Program program;
	private ASTNode nodes;

	protected @Inject ASTFunction(Program program, ASTNode nodes) {
		this.program = program;
		this.nodes = nodes;
	}

	@Override
	public void enter() {}

	public void enterFunction() {
		nodes.enter();
	}
}
