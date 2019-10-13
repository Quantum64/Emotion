package co.q64.emotion.ast;

import co.q64.emotion.lang.Instruction;
import co.q64.emotion.lang.Program;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.lexer.Lexer;
import co.q64.emotion.runtime.Output;
import co.q64.emotion.types.Comparison;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Singleton
public class ASTBuilder {
    protected @Inject Lexer lexer;
    protected @Inject Opcodes opcodes;
    protected @Inject ASTFactory astFactory;
    protected @Inject ASTInstructionFactory astInstructionFactory;
    protected @Inject ASTIteratorFactory astIteratorFactory;
    protected @Inject ASTConditionalFactory astConditionalFactory;
    protected @Inject ASTFunctionFactory astFunctionFactory;
    protected @Inject ASTLoopFactory astLoopFactory;
    protected @Inject ASTBreakToken astBreakToken;
    protected @Inject ASTContinueToken astContinueToken;

    protected @Inject ASTBuilder() {}

    public AST build(@Nullable Program program, String source) {
        Output output = new Output() {
            public void println(String message) {}

            public void print(String message) {}
        };
        if (program != null) {
            output = program.getOutput();
        }
        List<Instruction> instructions = lexer.parse(source, output);
        AST result = astFactory.create();
        for (Iterator<Instruction> itr = instructions.iterator(); itr.hasNext(); ) {
            visitNextInstruction(program, itr, result);
        }
        return result;
    }

    private Directive visitNextInstruction(Program program, Iterator<Instruction> itr, AST result) {
        if (!itr.hasNext()) {
            return Directive.NOTHING;
        }
        Instruction instruction = itr.next();
        if (hasFlag(instruction, OpcodeMarker.ITERATE)) {
            visitIterator(program, itr, result, false);
        } else if (hasFlag(instruction, OpcodeMarker.ITERATE_STACK)) {
            visitIterator(program, itr, result, true);
        } else if (hasFlag(instruction, OpcodeMarker.LOOP)) {
            visitLoop(program, itr, result);
        } else if (hasFlag(instruction, OpcodeMarker.FUNCTION)) {
            visitFunction(program, itr, result);
        } else if (hasFlag(instruction, OpcodeMarker.EQUAL)) {
            visitConditional(program, itr, result, Comparison.EQUAL, Optional.empty());
        } else if (hasFlag(instruction, OpcodeMarker.GREATER)) {
            visitConditional(program, itr, result, Comparison.GREATER, Optional.empty());
        } else if (hasFlag(instruction, OpcodeMarker.LESS)) {
            visitConditional(program, itr, result, Comparison.LESS, Optional.empty());
        } else if (hasFlag(instruction, OpcodeMarker.NOT_EQUAL)) {
            visitConditional(program, itr, result, Comparison.NOT_EQUAL, Optional.empty());
        } else if (hasFlag(instruction, OpcodeMarker.LESS_EQUAL)) {
            visitConditional(program, itr, result, Comparison.EQUAL_LESS, Optional.empty());
        } else if (hasFlag(instruction, OpcodeMarker.GREATER_EQUAL)) {
            visitConditional(program, itr, result, Comparison.EQUAL_GREATER, Optional.empty());
        } else if (hasFlag(instruction, OpcodeMarker.TRUE)) {
            visitConditional(program, itr, result, Comparison.EQUAL, Optional.of(Values.create(true)));
        } else if (hasFlag(instruction, OpcodeMarker.FALSE)) {
            visitConditional(program, itr, result, Comparison.EQUAL, Optional.of(Values.create(false)));
        } else if (hasFlag(instruction, OpcodeMarker.ELSE)) {
            return Directive.ELSE;
        } else if (hasFlag(instruction, OpcodeMarker.END)) {
            return Directive.END;
        } else if (hasFlag(instruction, OpcodeMarker.BREAK)) {
            result.add(astBreakToken);
        } else if (hasFlag(instruction, OpcodeMarker.CONTINUE)) {
            result.add(astContinueToken);
        } else {
            result.add(astInstructionFactory.create(program, instruction));
        }
        return Directive.NOTHING;
    }

    private void visitFunction(Program program, Iterator<Instruction> itr, AST result) {
        AST nodes = astFactory.create();
        while (true) {
            if (!itr.hasNext()) {
                break;
            }
            if (visitNextInstruction(program, itr, nodes) == Directive.END) {
                break;
            }
        }
        result.add(astFunctionFactory.create(nodes));
    }

    private void visitConditional(Program program, Iterator<Instruction> itr, AST result, Comparison type, Optional<Value> push) {
        AST pass = astFactory.create();
        AST fail = astFactory.create();
        boolean inElse = false;
        while (true) {
            if (!itr.hasNext()) {
                break;
            }
            Directive directive = visitNextInstruction(program, itr, inElse ? fail : pass);
            if (directive == Directive.ELSE) {
                inElse = true;
            } else if (directive == Directive.END) {
                break;
            }
        }
        result.add(astConditionalFactory.create(program, pass, fail, type, push));
    }

    private void visitIterator(Program program, Iterator<Instruction> itr, AST result, boolean stack) {
        AST nodes = astFactory.create();
        while (true) {
            if (!itr.hasNext()) {
                break;
            }
            if (visitNextInstruction(program, itr, nodes) == Directive.END) {
                break;
            }
        }
        result.add(astIteratorFactory.create(program, nodes, stack));
    }

    private void visitLoop(Program program, Iterator<Instruction> itr, AST result) {
        AST nodes = astFactory.create();
        while (true) {
            if (!itr.hasNext()) {
                break;
            }
            if (visitNextInstruction(program, itr, nodes) == Directive.END) {
                break;
            }
        }
        result.add(astLoopFactory.create(program, nodes));
    }

    private boolean hasFlag(Instruction instruction, OpcodeMarker flag) {
        return opcodes.getFlags(flag).contains(instruction.opcode());
    }

    private static enum Directive {
        END, ELSE, NOTHING
    }
}
