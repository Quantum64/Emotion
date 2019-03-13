package co.q64.emotion;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.annotation.Constants.Version;
import co.q64.emotion.compiler.Compiler;
import co.q64.emotion.compiler.CompilerOutput;
import co.q64.emotion.lang.Program;
import co.q64.emotion.lang.ProgramFactory;
import co.q64.emotion.runtime.Output;
import lombok.Getter;

@Singleton
public class EmotionEngine {
	protected @Inject EmotionEngine() {}

	protected @Getter @Version @Inject String version;
	protected @Inject Compiler compiler;
	protected @Inject ProgramFactory programFactory;

	public CompilerOutput compileProgram(List<String> lines) {
		return compiler.compile(lines);
	}

	public void runProgram(String compiled, String args, Output output) {
		Program program = programFactory.create(compiled, args, output);
		program.execute();
	}
}
