package co.q64.emotion.opcode;

import co.q64.emotion.lang.Stack;
import co.q64.emotion.lang.opcode.OpcodeCache;
import co.q64.emotion.lang.opcode.OpcodeRegistry;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.lang.value.special.Null;
import co.q64.emotion.types.Operation;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static co.q64.emotion.lang.opcode.OpcodeMarker.BREAK;
import static co.q64.emotion.lang.opcode.OpcodeMarker.COMPRESSION_BASE256;
import static co.q64.emotion.lang.opcode.OpcodeMarker.COMPRESSION_DEFLATE;
import static co.q64.emotion.lang.opcode.OpcodeMarker.COMPRESSION_LZMA;
import static co.q64.emotion.lang.opcode.OpcodeMarker.COMPRESSION_SHOCO;
import static co.q64.emotion.lang.opcode.OpcodeMarker.COMPRESSION_SMAZ;
import static co.q64.emotion.lang.opcode.OpcodeMarker.CONTINUE;
import static co.q64.emotion.lang.opcode.OpcodeMarker.ELSE;
import static co.q64.emotion.lang.opcode.OpcodeMarker.END;
import static co.q64.emotion.lang.opcode.OpcodeMarker.EQUAL;
import static co.q64.emotion.lang.opcode.OpcodeMarker.EXIT;
import static co.q64.emotion.lang.opcode.OpcodeMarker.FALSE;
import static co.q64.emotion.lang.opcode.OpcodeMarker.FUNCTION;
import static co.q64.emotion.lang.opcode.OpcodeMarker.GREATER;
import static co.q64.emotion.lang.opcode.OpcodeMarker.GREATER_EQUAL;
import static co.q64.emotion.lang.opcode.OpcodeMarker.ITERATE;
import static co.q64.emotion.lang.opcode.OpcodeMarker.ITERATE_STACK;
import static co.q64.emotion.lang.opcode.OpcodeMarker.LESS;
import static co.q64.emotion.lang.opcode.OpcodeMarker.LESS_EQUAL;
import static co.q64.emotion.lang.opcode.OpcodeMarker.LITERAL_PAIR;
import static co.q64.emotion.lang.opcode.OpcodeMarker.LITERAL_SINGLE;
import static co.q64.emotion.lang.opcode.OpcodeMarker.LOOP;
import static co.q64.emotion.lang.opcode.OpcodeMarker.NOT_EQUAL;
import static co.q64.emotion.lang.opcode.OpcodeMarker.STANDARD;
import static co.q64.emotion.lang.opcode.OpcodeMarker.TRUE;

@Singleton
public class StandardOpcodes extends OpcodeRegistry {
    protected @Inject StandardOpcodes() {
        super(STANDARD);
    }

    protected @Inject Null nul;
    protected @Inject OpcodeCache cache;

    @Override
    public void register() {
        r("load 0", stack -> stack.push(0));
        r("load 1", stack -> stack.push(1));
        r("load 2", stack -> stack.push(2));
        r("load 3", stack -> stack.push(3));
        r("load 4", stack -> stack.push(4));
        r("load 5", stack -> stack.push(5));
        r("load 6", stack -> stack.push(6));
        r("load 7", stack -> stack.push(7));
        r("load 8", stack -> stack.push(8));
        r("load 9", stack -> stack.push(9));
        r("def", FUNCTION, stack -> astFail(stack, "FUNCTION"), "Declares a function.");
        r("UNUSED literal compression mode 1", COMPRESSION_SHOCO, stack -> stack.getProgram().crash("The interpreter attempted to execute an unused literal opcode!"));
        r("UNUSED literal compression mode 2", COMPRESSION_BASE256, stack -> stack.getProgram().crash("The interpreter attempted to execute an unused literal opcode!"));
        r("UNUSED literal compression mode 3", COMPRESSION_SMAZ, stack -> stack.getProgram().crash("The interpreter attempted to execute an unused literal opcode!"));
        r("UNUSED literal compression mode 4", COMPRESSION_DEFLATE, stack -> stack.getProgram().crash("The interpreter attempted to execute an unused literal opcode!"));
        r("UNUSED literal compression mode 5", LITERAL_SINGLE, stack -> stack.getProgram().crash("The interpreter attempted to execute an unused literal opcode!"));
        r("UNUSED literal compression mode 6", LITERAL_PAIR, stack -> stack.getProgram().crash("The interpreter attempted to execute an unused literal opcode!"));
        r("UNUSED literal compression mode 7", COMPRESSION_LZMA, stack -> stack.getProgram().crash("The interpreter attempted to execute an unused literal opcode!"));
        r("if equal", EQUAL, stack -> astFail(stack, "IF ="), "Enter a conditional block if the top two stack values are equal.");
        r("if notEqual", NOT_EQUAL, stack -> astFail(stack, "IF !="), "Enter a conditional block if the top two stack values not equal.");
        r("if greater", GREATER, stack -> astFail(stack, "IF >"), "Enter a conditional block if the second stack value is greater than the top stack value.");
        r("if greaterEqual", GREATER_EQUAL, stack -> astFail(stack, "IF >="), "Enter a conditional block if the second stack value is greater than or equal to the top stack value.");
        r("if less", LESS, stack -> astFail(stack, "IF <"), "Enter a conditional block if the second stack value is less than the top stack value.");
        r("if lessEqual", LESS_EQUAL, stack -> astFail(stack, "IF <="), "Enter a conditional block if the second stack value is less than or equal to the top stack value.");
        r("if isTrue", TRUE, stack -> astFail(stack, "IF TRUE"), "Enter a conditional block if first stack value exactly equals true.");
        r("if isFalse", FALSE, stack -> astFail(stack, "IF FALSE"), "Enter a conditional block if first stack value exactly equals false.");
        r("load true", stack -> stack.push(true));
        r("load false", stack -> stack.push(false));
        r("load null", stack -> stack.push(nul));
        r("else", ELSE, stack -> astFail(stack, "ELSE"), "Enter a conditional block if and only if the last conditional block was not executed.");
        r("pop", stack -> stack.pop(), "Remove the first stack values from the stack.");
        r("pop 2", stack -> stack.pop(2), "Remove the first two stack values from the stack.");
        r("clr", stack -> stack.clr(), "Remove all values on the stack.");
        r("dup", stack -> stack.dup(), "Push a copy of the first stack value.");
        r("dup 2", stack -> stack.dup(2), "Push two copies of the first stack value.");
        r("dup 3", stack -> stack.dup(3), "Push three copies of the first stack value.");
        r("dup x", stack -> stack.dup(Math.abs(stack.pop().asNumber().intValue())), "Push the second stack value the absolute value of the first stack value times.");
        r("swp", stack -> stack.swap(), "Swap the top two stack values.");
        r("ldv", stack -> stack.push(stack.getProgram().getLocalRegisters().getData().get(stack.pop())), "Push the value from the stack frame variable table with the name of the first stack value.");
        r("sdv", stack -> stack.getProgram().getLocalRegisters().getData().put(stack.pop(), stack.pop()), "Saves the second stack value to the the stack frame variable table with the name of the first stack value.");
        r("ldr a", stack -> stack.push(stack.getProgram().getRegisters().getA()), "Push the value contained in the a register.");
        r("ldr b", stack -> stack.push(stack.getProgram().getRegisters().getB()), "Push the value contained in the b register.");
        r("ldr c", stack -> stack.push(stack.getProgram().getRegisters().getC()), "Push the value contained in the c register.");
        r("ldr d", stack -> stack.push(stack.getProgram().getRegisters().getD()), "Push the value contained in the d register.");
        r("ldr i", stack -> stack.push(stack.getProgram().getRegisters().getI()), "Push the value contained in the iteration index register.");
        r("ldr o", stack -> stack.push(stack.getProgram().getRegisters().getO()), "Push the value contained in the iteration element register.");
        r("sdr a", stack -> stack.getProgram().getRegisters().setA(stack.pop()), "Store the first stack value in the a register.");
        r("sdr b", stack -> stack.getProgram().getRegisters().setB(stack.pop()), "Store the first stack value in the b register.");
        r("sdr c", stack -> stack.getProgram().getRegisters().setC(stack.pop()), "Store the first stack value in the c register.");
        r("sdr d", stack -> stack.getProgram().getRegisters().setD(stack.pop()), "Store the first stack value in the d register.");
        r("iterate", ITERATE, stack -> stack.getProgram().crash("ITERATE"), "Enter an iteration block over the first stack value.");
        r("iterate stack", ITERATE_STACK, stack -> astFail(stack, "ITERATE STACK"), "Enter an iteration block over the first stack value and push the iteration element register at the begining of each loop.");
        r("break", BREAK, stack -> astFail(stack, "BREAK"), "Immediately exits a loop or iteration structure.");
        r("continue", CONTINUE, stack -> astFail(stack, "CONTINUE"), "Jumps to the next iteration of a loop or iteration structure.");
        r("end", END, stack -> astFail(stack, "END"), "End a control flow structure.");
        r("print", stack -> stack.getProgram().print(stack.pop().toString()), "Print the first stack value.");
        r("print space", stack -> stack.getProgram().print(" "), "Print a space character.");
        r("println", stack -> stack.getProgram().println(stack.pop().toString()), "Print the first stack value, then a newline.");
        r("exit", EXIT, stack -> stack.getProgram().terminate(), "End program execution, then prints the top stack value followed by a newline.");
        r("terminate", stack -> stack.getProgram().terminateNoPrint(), "End program execution.");
        r("loop", LOOP, stack -> astFail(stack, "LOOP"), "Control flow block that repeats until a break statement is encountered. Bewear infinite loops.");
        r("jump", stack -> stack.getProgram().jumpToFunction(stack.pop().asNumber().intValue()), "Jump to the top stack value interpreted as a functor index.");
        r("str", stack -> stack.push(stack.pop().toString()), "Push the first stack value as a string.");
        r("args", stack -> stack.push(stack.getProgram().getArgs()), "Push the command line arguments.");
        r("args list", stack -> stack.push(Arrays.stream(stack.getProgram().getArgs().split(" ")).collect(Collectors.toList())), "Push the command line arguments as a list seperated by spaces.");
        r("flatten", stack -> pushStackAsList(stack), "Collapse all stack values into a list, then push that list.");
        r("join", stack -> pushStackSplit(stack, ""), "Collapse all stack values into a string, then push that string.");
        r("join space", stack -> pushStackSplit(stack, " "), "Collapse all stack values into a string seperated by spaces, then push that string.");
        r("add", stack -> stack.push(stack.peek(2).operate(stack.pull(2), Operation.ADD)), "Push the sum of the second and first stack values.");
        r("subtract", stack -> stack.push(stack.peek(2).operate(stack.pull(2), Operation.SUBTRACT)), "Push the difference of the second and first stack values.");
        r("multiply", stack -> stack.push(stack.peek(2).operate(stack.pull(2), Operation.MULTIPLY)), "Push the product of the second and first stack values.");
        r("divide", stack -> stack.push(stack.peek(2).operate(stack.pull(2), Operation.DIVIDE)), "Push the quotient of the second and first stack values.");
        r("floorDiv", stack -> stack.push(stack.peek(2).operate(stack.pull(2), Operation.DIVIDE).asNumber().longValue()), "Push the quotient of the second and first stack values rounded to an integer.");
        r("mod", stack -> stack.push(stack.peek(2).asNumber().longValue() % stack.pull(2).asNumber().longValue()), "Push the modulus of the second and first stack values.");
        // 73
        r("increment", stack -> stack.push(stack.pop().operate(Values.create(1), Operation.ADD)));
        r("decrement", stack -> stack.push(stack.pop().operate(Values.create(1), Operation.SUBTRACT)));
        r("time", stack -> stack.push(System.currentTimeMillis()), "Push the current time in milliseconds.");

		/*
		r("remap.math", PRIORITIZATION, stack -> cache.prioritize(OpcodeMarker.MATH), "Prioritize math opcodes for one byte instructions.");
		r("remap.list", PRIORITIZATION, stack -> cache.prioritize(OpcodeMarker.LIST), "Prioritize list opcodes for one byte instructions.");
		r("remap.string", PRIORITIZATION, stack -> cache.prioritize(OpcodeMarker.STRING), "Prioritize string opcodes for one byte instructions.");
		r("remap.meta", PRIORITIZATION, stack -> cache.prioritize(OpcodeMarker.META), "Prioritize meta opcodes for one byte instructions.");
		r("remap.bignumber", PRIORITIZATION, stack -> cache.prioritize(OpcodeMarker.BIG_NUMBER), "Prioritize big number opcodes for one byte instructions.");
		r("remap.graphics", PRIORITIZATION, stack -> cache.prioritize(OpcodeMarker.GRAPHICS), "Prioritize graphics opcodes for one byte instructions.");
		r("remap.integer", PRIORITIZATION, stack -> cache.prioritize(OpcodeMarker.GRAPHICS), "Prioritize integer opcodes for one byte instructions.");
		*/
        // 79
    }

    private void astFail(Stack stack, String type) {
        stack.getProgram().crash("Attempted to execute an AST control flow structure. [" + type + "]");
    }

    private void pushStackAsList(Stack stack) {
        if (stack.size() <= 0) {
            return;
        }
        List<Value> values = new ArrayList<>();
        for (int i = 0; i < stack.size(); i++) {
            values.add(stack.peek(stack.size() - i));
        }
        stack.clr();
        stack.push(values);
    }

    private void pushStackSplit(Stack stack, String splitter) {
        if (stack.size() <= 0) {
            return;
        }
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < stack.size(); i++) {
            value.append(stack.peek(stack.size() - i));
            if (i < stack.size() - 1) {
                value.append(splitter);
            }
        }
        stack.clr();
        stack.push(value.toString());
    }
}
