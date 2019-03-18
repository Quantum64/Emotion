package co.q64.emotion.ast;

import java.util.Optional;

import javax.annotation.Nullable;
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
	private Optional<Value> push;

	protected @Inject ASTConditional(@Nullable Program program, AST pass, AST fail, Comparison type, Optional<Value> push) {
		this.program = program;
		this.pass = pass;
		this.fail = fail;
		this.type = type;
		this.push = push;
	}

	@Override
	public void enter() {
		if (push.isPresent()) {
			program.getStack().push(push.get());
		}
		Value b = program.getStack().pop();
		Value a = program.getStack().pop();
		if (a.compare(b, type)) {
			pass.enter();
		} else {
			fail.enter();
		}
	}

	@Override
	public String toString() {
		return "{if " + type.name() + pass.toString() + " else " + fail.toString() + "}";
	}
}
