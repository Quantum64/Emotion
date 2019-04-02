package co.q64.emotion.lang;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import co.q64.emotion.ast.AST;
import co.q64.emotion.ast.ASTBuilder;
import co.q64.emotion.ast.ASTFunction;
import co.q64.emotion.ast.ASTNode;
import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.runtime.Output;
import lombok.Getter;

@AutoFactory
public class Program {
	private static final int TOO_LONG = 5000;
	
	private StackFactory stackFactory;
	private RegistersFactory registersFactory;
	private Opcodes opcodes;
	private ASTBuilder astBuilder;

	private @Getter Output output;
	private @Getter AST ast;
	private @Getter Stack stack;
	private @Getter Registers registers;
	private @Getter String source;
	private @Getter String args;

	private @Getter boolean printOnTerminate, terminated;
	private long start;

	protected Program(@Provided StackFactory stackFactory, @Provided RegistersFactory registersFactory, @Provided Opcodes opcodes, @Provided ASTBuilder astBuilder, String source, String args, Output output) {
		this.stackFactory = stackFactory;
		this.registersFactory = registersFactory;
		this.source = source;
		this.opcodes = opcodes;
		this.args = args;
		this.output = output;
		this.astBuilder = astBuilder;
	}

	public void execute() {
		execute(true);
	}

	public void execute(boolean full) {
		this.opcodes.reset();
		this.stack = stackFactory.create(this);
		this.registers = registersFactory.create();
		this.printOnTerminate = true;
		this.terminated = false;
		this.start = System.currentTimeMillis();
		this.ast = astBuilder.build(this, source);
		if (!args.isEmpty()) {
			stack.push(args);
		}
		executeAst();
		if (printOnTerminate) {
			println(stack.pop().toString());
		}
	}

	public void executeAst() {
		ast.enter();
	}

	public void jumpToFunction(int funcDecal) {
		int functor = 0;
		for (ASTNode node : ast.getNodes()) {
			if (node instanceof ASTFunction) {
				if (functor == funcDecal) {
					((ASTFunction) node).enterFunction();
					return;
				}
				functor++;
			}
		}
	}

	public boolean shouldContinueExecution() {
		if (!terminated) {
			if (System.currentTimeMillis() - TOO_LONG > start) {
				crash("Unusually long execution time! (" + TOO_LONG + " ms)");
				terminated = true;
			}
		}
		return !terminated;
	}

	// Probably unsupported with AST
	public void step() {
		//if (instructions.size() < instruction) {
		//	return;
		//}
		//Instruction current = instructions.get(instruction);
		//instruction++;
		//try {
		//	current.execute(stack);
		//} catch (Exception e) {
		//	crash(e.getClass().getSimpleName() + ": " + e.getMessage() + " [Instruction: " + current.getInstruction() + ", Line: " + (instruction - 1) + "]");
		//}
	}

	public void terminate() {
		this.terminated = true;
	}

	public void terminateNoPrint() {
		this.terminated = true;
		this.printOnTerminate = false;
	}

	public void warn(String message) {
		output.println("");
		output.println("Warning: " + message);
	}

	public void crash(String message) {
		output.println("");
		output.println("Fatal: " + message);
		output.println("The program cannot continue and will now terminate.");
		terminateNoPrint();
	}

	public void print(String str) {
		output.print(str.replace("\\n", "\n"));
	}

	public void println(String str) {
		output.println(str.replace("\\n", "\n"));
	}
}
