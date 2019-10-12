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

import javax.inject.Inject;
import javax.inject.Provider;

public class Program {
    private static final int TOO_LONG = 5000;

    protected @Inject Provider<Stack> stackProvider;
    protected @Inject Provider<Registers> registersProvider;
    protected @Inject Provider<LocalRegisters> localRegistersProvider;
    protected @Inject Opcodes opcodes;
    protected @Inject ASTBuilder astBuilder;

    private @Getter Output output;
    private @Getter AST ast;
    private @Getter Stack stack;
    private @Getter Registers registers;
    private @Getter LocalRegisters localRegisters;
    private @Getter String source;
    private @Getter String args;

    private @Getter boolean printOnTerminate, terminated;
    private long start;

    protected @Inject Program() {}

    public void execute(String source, String args, Output output) {
        execute(source, args, output, true);
    }

    public void execute(String source, String args, Output output, boolean full) {
        this.source = source;
        this.args = args;
        this.output = output;
        this.opcodes.reset();
        this.stack = stackProvider.get();
        this.registers = registersProvider.get();
        this.localRegisters = localRegistersProvider.get();
        this.printOnTerminate = true;
        this.terminated = false;
        this.start = System.currentTimeMillis();
        this.ast = astBuilder.build(this, source);
        stack.setProgram(this);
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
                    LocalRegisters parent = localRegisters;
                    localRegisters = localRegistersProvider.get();
                    ((ASTFunction) node).enterFunction();
                    localRegisters = parent;
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
