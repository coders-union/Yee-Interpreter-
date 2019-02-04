package com.netlify.totallynotafterurmoneys.yeeinterpretor;

public enum TokenType {
	//Single char
	LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
	COMMA, DOT, MINUS, PLUS, MODULO, QUESTION, SLASH, STAR,
	
	//Multi char
	NOT, NOT_EQUAL,
	EQUAL, EQUAL_EQUAL,
	GREATER, GREATER_EQUAL,
	LESS, LESS_EQUAL,
	
	//Literals
	IDENTIFIER, STRING, NUMBER,
	
	//Keywords
	AND, CLASS, ELSE, FALSE, FUNCTION, FOR, IF, NIL, OR,
	PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,
	
	EOF
}
