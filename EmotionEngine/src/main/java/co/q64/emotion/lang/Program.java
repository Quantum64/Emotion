package co.q64.emotion.lang;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import co.q64.emotion.ast.AST;
import co.q64.emotion.ast.ASTBuilder;
import co.q64.emotion.ast.ASTFunction;
import co.q64.emotion.ast.ASTNode;
import co.q64.emotion.factory.IteratorFactoryFactory;
import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.runtime.Output;
import lombok.Getter;

@AutoFactory
public class Program {
	private StackFactory stackFactory;
	private RegistersFactory registersFactory;
	private IteratorFactory iteratorFactory;
	private Opcodes opcodes;
	private ASTBuilder astBuilder;

	private @Getter Output output;
	//private @Getter List<Instruction> instructions;
	private @Getter AST ast;
	private @Getter Stack stack;
	private @Getter Registers registers;
	private @Getter String source;
	private @Getter String args;

	//private @Getter int instruction;
	private @Getter boolean printOnTerminate, terminated;
	//private Deque<Integer> jumps = new ArrayDeque<>();
	//private @Getter boolean lastConditional = false; // TODO replace with stack?
	private long start;

	protected Program(@Provided StackFactory stackFactory, @Provided RegistersFactory registersFactory, @Provided IteratorFactoryFactory iteratorFactory, @Provided Opcodes opcodes, @Provided ASTBuilder astBuilder, String source, String args, Output output) {
		this.stackFactory = stackFactory;
		this.registersFactory = registersFactory;
		this.iteratorFactory = iteratorFactory.getFactory();
		//this.instructions = lexer.parse(source, output);
		this.source = source;
		this.opcodes = opcodes;
		this.args = args;
		this.output = output;
		this.astBuilder = astBuilder;
		//instructions.add(0, new Instruction());
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
		//this.instruction = 0;
		this.start = System.currentTimeMillis();
		this.ast = astBuilder.build(this, source);
		//this.jumps.clear();
		if (!args.isEmpty()) {
			stack.push(args);
		}
		/*
		while (full) {
			if (terminated) { // move to ast insn
				break;
			}
			//if (instructions.size() <= instruction) {
			//	break;
			//}
			if (System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(2) > start) {
				crash("Unusually long execution time! (2000ms)");
				continue;
			}
			
		}
		*/
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

	/*
	public void jump(int node) {
		jumps.push(instruction);
		jumpToNode(node);
	}
	
	public void jumpToNode(int node) {
		if (node >= instructions.size()) {
			crash("Jump to node attempted to jump outside the program! (Instruction " + (instruction - 1) + " JMP to " + node + ")");
			return;
		}
		instruction = node;
	}
	
	public void jumpToEndif() {
		int debt = 0;
		for (int i = instruction; i < instructions.size(); i++) {
			Instruction ins = instructions.get(i);
			if (ins.getOpcode() == null) {
				continue;
			}
			if (opcodes.getFlags(OpcodeMarker.CONDITIONAL).contains(ins.getOpcode()) || (ins.getOpcode().equals(opcodes.getFlag(OpcodeMarker.ELSE)))) {
				debt++;
			}
			if (ins.getOpcode().equals(opcodes.getFlag(OpcodeMarker.ELSE)) || ins.getOpcode().equals(opcodes.getFlag(OpcodeMarker.ENDIF))) {
				if (debt <= 0) {
					instruction = i;
					return;
				}
				debt--;
			}
		}
		// If no endif instruction is found then we will simply jump over the next line.
		// This special case should only be used for base-layer conditionals. Nested conditionals will
		// see the endif statement meant for the base-layer conditional and jump there instead
		instruction++;
	}
	
	private void jumpToEnd() {
		for (int i = instruction; i < instructions.size(); i++) {
			Instruction ins = instructions.get(i);
			if (ins.getOpcode() == null) {
				continue;
			}
			if (ins.getOpcode().equals(opcodes.getFlag(OpcodeMarker.END))) {
				instruction = i + 1; // Don't actually run the end instruction
				return;
			}
		}
	}
	
	public void jumpReturn() {
		if (jumps.size() <= 0) {
			crash("Call stack underflow detected! Likely attempted to return without calling a function: did program execution fall through a function declaration? (Instruction " + (instruction - 1) + " RETURN)");
			return;
		}
		jumpToNode(jumps.poll());
	}
	*/

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
