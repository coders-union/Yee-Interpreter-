package com.netlify.totallynotafterurmoneys.yeeinterpretor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Yee {
	
	private static final Interpreter interpreter = new Interpreter();
	static boolean hadError = false;
	static boolean hadRuntimeError = false;       

	public static void main(String[] args) throws IOException {
		
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		System.out.println("Run a file (1)\nRun prompt(2)");
		String choice = reader.readLine();
		Integer choice1 = new Integer(choice);
		if (choice1 == 1) {
			System.out.println("Enter path to file to run");
			String path = reader.readLine();
			runFile(path);
		}
		else {
			try {
				runPrompt();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	private static void runFile(String path) throws IOException {
		
		System.out.println("Executing file at " + path);
		System.out.println();
			
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		if (hadError) System.exit(1);
		if (hadRuntimeError) System.exit(1);
		
	}
	
	private static void runPrompt() throws IOException {
		
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		System.out.println("Yee command console - v1.0.0");
		
		for (;;) {
			System.out.print("> ");
			String yee = reader.readLine();
			run(yee);
			hadError = false;
		}
		
	}
	
	private static void run(String source) {
		
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		
		Parser parser = new Parser(tokens);                    
		List<Stmt> statements = parser.parse();

	    // Stop if there was a syntax error.                   
	    if (hadError) return;
	    
	    Resolver resolver = new Resolver(interpreter);
	    resolver.resolve(statements);
	    
	    if (hadError) return;  

	    interpreter.interpret(statements);
	}
	
	static void error(int line, String message) {
		report(line, "", message);
	}
	
	static void runtimeError(RuntimeError error) {
	    System.err.println(error.getMessage() +     
	        "\n[line " + error.token.line + "]");   
	    hadRuntimeError = true;                     
	  } 
	
	private static void report(int line, String where, String message) {
		System.err.println("[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}
	
	public static void error(Token token, String message) {              
	    if (token.type == TokenType.EOF) {                          
	    	report(token.line, " at end", message);                   
	    } else {                                                    
	    	report(token.line, " at '" + token.lexeme + "'", message);
	    }                                                           
	}

}
