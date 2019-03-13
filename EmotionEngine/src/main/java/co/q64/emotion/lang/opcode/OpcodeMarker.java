package co.q64.emotion.lang.opcode;

public enum OpcodeMarker {
	// Lexer flags
	CONDITIONAL, END, ENDIF, ELSE, LITERAL, LITERAL1, LITERAL2, COMPRESSION1, COMPRESSION2, COMPRESSION3, SPECIAL, EXIT, LZMA, PRIORITIZATION,
	// Prioritization flags
	STRING, MATH, LIST, INTEGER, GRAPHICS, META, BIG_NUMBER, LONG_REGISTER, DICTIONARY, STACK, STANDARD, JAVASCRIPT;
}