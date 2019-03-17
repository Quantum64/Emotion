package co.q64.emotion.ast;

import javax.inject.Inject;

import com.google.auto.factory.AutoFactory;

import co.q64.emotion.lang.Program;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.types.Comparison;

@AutoFactory
public class ASTConditional implements ASTNode {
	private Program program;
	private AST pass, fail;
	private Comparison type;

	protected @Inject ASTConditional(Program program, AST pass, AST fail, Comparison type) {
		this.program = program;
		this.pass = pass;
		this.fail = fail;
		this.type = type;
	}

	@Override
	public void enter() {
		Value b = program.getStack().pop();
		Value a = program.getStack().pop();
		if (a.compare(b, type)) {
			pass.enter();
		} else {
			fail.enter();
		}
	}
}
