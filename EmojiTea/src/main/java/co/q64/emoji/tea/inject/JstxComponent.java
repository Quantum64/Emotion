package co.q64.emoji.tea.inject;

import javax.inject.Singleton;

import co.q64.jstx.Jstx;
import co.q64.jstx.compiler.Compiler;
import co.q64.jstx.inject.StandardModule;
import co.q64.jstx.lang.opcode.Opcodes;
import co.q64.jstx.lexer.Lexer;
import dagger.Component;

@Singleton
@Component(modules = { TeaModule.class, StandardModule.class })
public interface JstxComponent {
	public Compiler getCompiler();

	public Lexer getLexer();

	public Jstx getJstx();

	public Opcodes getOpcodes();
}
