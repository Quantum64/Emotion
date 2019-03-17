package co.q64.emotion.ast;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.Instruction;
import co.q64.emotion.lang.Program;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.lang.value.LiteralFactory;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lexer.Lexer;
import co.q64.emotion.types.Comparison;

@Singleton
public class ASTBuilder {
	protected @Inject Lexer lexer;
	protected @Inject Opcodes opcodes;
	protected @Inject LiteralFactory literalFactory;
	protected @Inject ASTFactory astFactory;
	protected @Inject ASTInstructionFactory astInstructionFactory;
	protected @Inject ASTIteratorFactory astIteratorFactory;
	protected @Inject ASTConditionalFactory astConditionalFactory;
	protected @Inject ASTFunctionFactory astFunctionFactory;

	protected @Inject ASTBuilder() {}

	public AST build(Program program, String source) {
		List<Instruction> instructions = lexer.parse(source, program.getOutput());
		AST result = astFactory.create();
		for (Iterator<Instruction> itr = instructions.iterator(); itr.hasNext();) {
			investigateNextInstruction(program, itr, result);
		}
		return result;
	}

	private Verdict investigateNextInstruction(Program program, Iterator<Instruction> itr, AST result) {
		if (!itr.hasNext()) {
			return Verdict.NOTHING;
		}
		Instruction instruction = itr.next();
		if (hasFlag(instruction, OpcodeMarker.ITERATE)) {
			investigateIterator(program, itr, result, false);
		} else if (hasFlag(instruction, OpcodeMarker.ITERATE_STACK)) {
			investigateIterator(program, itr, result, true);
		} else if (hasFlag(instruction, OpcodeMarker.FUNCTION)) {
			investigateFunction(program, itr, result);
		} else if (hasFlag(instruction, OpcodeMarker.EQUAL)) {
			invertigateConditional(program, itr, result, Comparison.EQUAL, Optional.empty());
		} else if (hasFlag(instruction, OpcodeMarker.GREATER)) {
			invertigateConditional(program, itr, result, Comparison.GREATER, Optional.empty());
		} else if (hasFlag(instruction, OpcodeMarker.LESS)) {
			invertigateConditional(program, itr, result, Comparison.LESS, Optional.empty());
		} else if (hasFlag(instruction, OpcodeMarker.NOT_EQUAL)) {
			invertigateConditional(program, itr, result, Comparison.NOT_EQUAL, Optional.empty());
		} else if (hasFlag(instruction, OpcodeMarker.LESS_EQUAL)) {
			invertigateConditional(program, itr, result, Comparison.EQUAL_LESS, Optional.empty());
		} else if (hasFlag(instruction, OpcodeMarker.GREATER_EQUAL)) {
			invertigateConditional(program, itr, result, Comparison.EQUAL_GREATER, Optional.empty());
		} else if (hasFlag(instruction, OpcodeMarker.TRUE)) {
			invertigateConditional(program, itr, result, Comparison.EQUAL, Optional.of(literalFactory.create(true)));
		} else if (hasFlag(instruction, OpcodeMarker.FALSE)) {
			invertigateConditional(program, itr, result, Comparison.EQUAL, Optional.of(literalFactory.create(false)));
		} else if (hasFlag(instruction, OpcodeMarker.ELSE)) {
			return Verdict.ELSE;
		} else if (hasFlag(instruction, OpcodeMarker.END)) {
			return Verdict.END;
		} else {
			result.add(astInstructionFactory.create(program, instruction));
		}
		return Verdict.NOTHING;
	}

	private void investigateFunction(Program program, Iterator<Instruction> itr, AST result) {
		AST nodes = astFactory.create();
		while (true) {
			if (!itr.hasNext()) {
				break;
			}
			if (investigateNextInstruction(program, itr, nodes) == Verdict.END) {
				break;
			}
		}
		result.add(astFunctionFactory.create(program, nodes));
	}

	private void invertigateConditional(Program program, Iterator<Instruction> itr, AST result, Comparison type, Optional<Value> push) {
		AST pass = astFactory.create();
		AST fail = astFactory.create();
		boolean inElse = false;
		while (true) {
			if (!itr.hasNext()) {
				break;
			}
			Verdict verdict = investigateNextInstruction(program, itr, inElse ? fail : pass);
			if (verdict == Verdict.ELSE) {
				inElse = true;
			} else if (verdict == Verdict.END) {
				break;
			}
		}
		result.add(astConditionalFactory.create(program, pass, fail, type, push));
	}

	private void investigateIterator(Program program, Iterator<Instruction> itr, AST result, boolean stack) {
		AST nodes = astFactory.create();
		while (true) {
			if (!itr.hasNext()) {
				break;
			}
			if (investigateNextInstruction(program, itr, nodes) == Verdict.END) {
				break;
			}
		}
		result.add(astIteratorFactory.create(program, nodes, stack));
	}

	private boolean hasFlag(Instruction instruction, OpcodeMarker flag) {
		return opcodes.getFlags(flag).contains(instruction.getOpcode());
	}

	private static enum Verdict {
		END, ELSE, NOTHING
	}
}
