package co.q64.emotion.compiler;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@Getter
@Setter
@Accessors(fluent = true)
public class InstructionInfo {
    private String instruction, compiled;
    private int line;

    protected @Inject InstructionInfo() {}
}
