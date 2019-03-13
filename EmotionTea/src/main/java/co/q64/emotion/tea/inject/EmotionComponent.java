package co.q64.emotion.tea.inject;

import javax.inject.Singleton;

import co.q64.emotion.EmotionEngine;
import co.q64.emotion.compiler.Compiler;
import co.q64.emotion.inject.StandardModule;
import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.lexer.Lexer;
import dagger.Component;

@Singleton
@Component(modules = { TeaModule.class, StandardModule.class })
public interface EmotionComponent {
	public Compiler getCompiler();

	public Lexer getLexer();

	public EmotionEngine getEngine();

	public Opcodes getOpcodes();
}
