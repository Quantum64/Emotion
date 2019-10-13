package co.q64.emotion.lang;

import java.util.function.Consumer;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.lang.value.Value;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@Accessors(fluent = true)
public class Instruction {
    protected @Inject Opcodes opcodes;

    private @Getter @Setter Integer opcode;
    private @Setter Value value;
    private Consumer<Stack> executor;

    protected @Inject Instruction() {}

    public void execute(Stack stack) {
        if (value != null) {
            stack.push(value);
            return;
        }
        if (opcode != null) {
            if (executor == null) {
                this.executor = opcodes.getExecutor(opcode);
            }
        }
        if (executor != null) {
            executor.accept(stack);
        }
        // no-op
    }

    public String getInstruction() {
        if (value != null) {
            return "load " + value.toString();
        }
        if (executor != null) {
            return opcodes.getName(opcode).orElse("undefined");
        }
        return "nop";
    }

    @Override
    public String toString() {
        return getInstruction();
    }
}
