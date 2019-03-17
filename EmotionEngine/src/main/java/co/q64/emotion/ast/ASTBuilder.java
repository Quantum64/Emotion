package co.q64.emotion.ast;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.Instruction;
import co.q64.emotion.lang.Program;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.lexer.Lexer;

@Singleton
public class ASTBuilder {
	protected @Inject Lexer lexer;
	protected @Inject Opcodes opcodes;
	protected @Inject ASTFactory astFactory;
	protected @Inject ASTInstructionFactory astInstructionFactory;
	protected @Inject ASTIteratorFactory astIteratorFactory;

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
		} else if (hasFlag(instruction, OpcodeMarker.END)) {
			return Verdict.END;
		} else {
			result.add(astInstructionFactory.create(program, instruction));
		}
		return Verdict.NOTHING;
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
