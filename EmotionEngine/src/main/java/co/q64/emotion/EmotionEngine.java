package co.q64.emotion;

import co.q64.emotion.annotation.Constants.Version;
import co.q64.emotion.compiler.Compiler;
import co.q64.emotion.compiler.CompilerOutput;
import co.q64.emotion.lang.Program;
import co.q64.emotion.runtime.Output;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class EmotionEngine {
    protected @Inject EmotionEngine() {}

    protected @Getter @Version @Inject String version;
    protected @Inject Compiler compiler;
    protected @Inject Provider<Program> programProvider;

    public CompilerOutput compileProgram(List<String> lines) {
        return compiler.compile(lines);
    }

    public void runProgram(String compiled, String args, Output output) {
        programProvider.get().execute(compiled, args, output);
    }
}
