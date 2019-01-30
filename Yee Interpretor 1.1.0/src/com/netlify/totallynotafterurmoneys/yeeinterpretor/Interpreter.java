package com.netlify.totallynotafterurmoneys.yeeinterpretor;

import com.netlify.totallynotafterurmoneys.yeestandardlibryary.*;
import com.netlify.totallynotafterurmoneys.yeeinterpretor.Stmt.Return;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
	
	 final Environment globals = new Environment();                       
	 private Environment environment = globals;
	 private final Map<Expr, Integer> locals = new HashMap<>();
	 
	 Interpreter() {                                
		 
		    globals.define("clock", new YeeCallable() {            
		      @Override                                            
		      public int arity() { return 0; }

		      @Override                                            
		      public Object call(Interpreter interpreter,          
		                         List<Object> arguments) {         
		        return (double)System.currentTimeMillis() / 1000.0;
		      }                                                    

		      @Override                                            
		      public String toString() { return "<native fn>"; }   
		    });
		    
		    //String functions
		    globals.define("charAt", new CharAt());
		    globals.define("lengthOf", new LengthOf());
		    
		    //User input
		    globals.define("getStr", new GetInput(2));
		    globals.define("getNum", new GetInput(1));
		   
	 }
	 
	
	void interpret(List<Stmt> statements) {
	    try {                                
	      for (Stmt statement : statements) {
	        execute(statement);              
	      }                                  
	    } catch (RuntimeError error) {       
	      Yee.runtimeError(error);           
	    }                                    
	  }
	
	private String stringify(Object object) {                         
	    if (object == null) return "nil";
	    if (object instanceof Double) {                                 
	      String text = object.toString();                              
	      if (text.endsWith(".0")) {                                    
	        text = text.substring(0, text.length() - 2);                
	      }                                                             
	      return text;                                                  
	    }                                                               

	    return object.toString();                                       
	}
	
	@Override
	public Object visitLiteralExpr(Expr.Literal expr) {
		return expr.value;
	}
	
	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return evaluate(expr.expression);
	}
	
	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}
	
	private void execute(Stmt stmt) {
	    stmt.accept(this);             
	}
	
	void resolve(Expr expr, int depth) {
		locals.put(expr, depth);          
	}
	
	void executeBlock(List<Stmt> statements, Environment environment) {
	    Environment previous = this.environment;                         
	    try {                                                            
	    	this.environment = environment;

	    	for (Stmt statement : statements) {                            
	    		execute(statement);                                          
	        }                                                              
	    } finally {                                                      
	    	this.environment = previous;                                   
	    }                                                                
	}
	
	@Override                                                     
	public Void visitBlockStmt(Stmt.Block stmt) {                 
	    executeBlock(stmt.statements, new Environment(environment));
	    return null;                                                
	}
	
	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		evaluate(stmt.expression);
		return null;
	}
	
	@Override                                          
	public Void visitFunctionStmt(Stmt.Function stmt) {
	    YeeFunction function = new YeeFunction(stmt, environment);    
	    environment.define(stmt.name.lexeme, function);  
	    return null;                                     
	}
	
	@Override                                          
	  public Object visitLogicalExpr(Expr.Logical expr) {
	    Object left = evaluate(expr.left);

	    if (expr.operator.type == TokenType.OR) {        
	      if (isTruthy(left)) return left;               
	    } else {                                         
	      if (!isTruthy(left)) return left;              
	    }                                                

	    return evaluate(expr.right);                     
	  }   
	
	@Override                                  
	  public Void visitIfStmt(Stmt.If stmt) {    
	    if (isTruthy(evaluate(stmt.condition))) {
	      execute(stmt.thenBranch);              
	    } else if (stmt.elseBranch != null) {    
	      execute(stmt.elseBranch);              
	    }                                        
	    return null;                             
	  }
	
	@Override                                    
	public Void visitPrintStmt(Stmt.Print stmt) {
		Object value = evaluate(stmt.expression);  
	    System.out.println(stringify(value));      
	    return null;                               
	}
	
	@Override                                              
	public Void visitReturnStmt(Stmt.Return stmt) {        
	    Object value = null;                                 
	    if (stmt.value != null) value = evaluate(stmt.value);

	    throw new com.netlify.totallynotafterurmoneys.yeeinterpretor.Return(value);                             
	} 
	
	@Override                                     
	public Void visitVarStmt(Stmt.Var stmt) {     
	    Object value = null;                        
	    if (stmt.initializer != null) {             
	      value = evaluate(stmt.initializer);       
	    }

	    environment.define(stmt.name.lexeme, value);
	    return null;                                
	  }
	
	
	
	@Override                                     
	  public Void visitWhileStmt(Stmt.While stmt) { 
	    while (isTruthy(evaluate(stmt.condition))) {
	      execute(stmt.body);                       
	    }                                           
	    return null;                                
	  }
	
	 @Override                                        
	  public Object visitAssignExpr(Expr.Assign expr) {
	    Object value = evaluate(expr.value);

	    Integer distance = locals.get(expr);               
	    if (distance != null) {                            
	      environment.assignAt(distance, expr.name, value);
	    } else {                                           
	      globals.assign(expr.name, value);                
	    }
	    
	    return value;                                  
	  }
	
	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
	    Object right = evaluate(expr.right);

	    switch (expr.operator.type) {  
	    case NOT:                 
	        return !isTruthy(right);
	    case MINUS:       
	    	checkNumberOperand(expr.operator, right);
	        return -(double)right;                   
	    }                                            
                              
	    return null;                                 
	}
	
	@Override                                            
	public Object visitVariableExpr(Expr.Variable expr) {
		return lookUpVariable(expr.name, expr);               
	}
	
	private Object lookUpVariable(Token name, Expr expr) {
	    Integer distance = locals.get(expr);                
	    if (distance != null) {                             
	      return environment.getAt(distance, name.lexeme);  
	    } else {                                            
	      return globals.get(name);                         
	    }                                                   
	}
	
	 private boolean isTruthy(Object object) {               
		 if (object == null) return false;                     
		 if (object instanceof Boolean) return (boolean)object;
		 return true;                                          
	 }
	 
	 @Override                                        
	 public Object visitBinaryExpr(Expr.Binary expr) {
	    Object left = evaluate(expr.left);             
	    Object right = evaluate(expr.right); 

	    switch (expr.operator.type) {   
	    case GREATER:                
	    	checkNumberOperands(expr.operator, left, right);
	        return (double)left > (double)right; 
	    case GREATER_EQUAL:  
	    	checkNumberOperands(expr.operator, left, right);
	        return (double)left >= (double)right;
	    case LESS: 
	    	checkNumberOperands(expr.operator, left, right);
	        return (double)left < (double)right; 
	    case LESS_EQUAL:    
	    	checkNumberOperands(expr.operator, left, right);
	        return (double)left <= (double)right;
	    case MINUS:            
	    	checkNumberOperands(expr.operator, left, right);
	        return (double)left - (double)right;
	    case PLUS:                                                
	        if (left instanceof Double && right instanceof Double) {
	        	return (double)left + (double)right;                  
	        } 
	        if (left instanceof String && right instanceof String) {
	        	return (String)left + (String)right;                  
	        }
	        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
	    case SLASH:
	    	checkNumberOperands(expr.operator, left, right);
	        return (double)left / (double)right;    
	    case MODULO:
	    	checkNumberOperands(expr.operator, left, right);
	    	return (double) left % (double) right;
	    case STAR:    
	    	checkNumberOperands(expr.operator, left, right);
	        return (double)left * (double)right;
	    case NOT_EQUAL: return !isEqual(left, right);
	    case EQUAL_EQUAL: return isEqual(left, right);
	    }                                              
                             
	    return null;                                   
	 }
	 
	 @Override                                             
	  public Object visitCallExpr(Expr.Call expr) {         
	    Object callee = evaluate(expr.callee);

	    List<Object> arguments = new ArrayList<>();         
	    for (Expr argument : expr.arguments) { 
	      arguments.add(evaluate(argument));                
	    } 
	    
	    if (!(callee instanceof YeeCallable)) {       
	        throw new RuntimeError(expr.paren,          
	            "Can only call functions and classes.");
	      }

	    YeeCallable function = (YeeCallable)callee;
	    
	    if (arguments.size() != function.arity()) {       
	        throw new RuntimeError(expr.paren, "Expected " +
	            function.arity() + " arguments but got " +  
	            arguments.size() + ".");                    
	    }
	    
	    return function.call(this, arguments);              
	  }	 
	 private boolean isEqual(Object a, Object b) {
		 // nil is only equal to nil.               
		 if (a == null && b == null) return true;   
		 if (a == null) return false;

		 return a.equals(b);                        
	 }
	 
	 private void checkNumberOperand(Token operator, Object operand) {
		    if (operand instanceof Double) return;                         
		    throw new RuntimeError(operator, "Operand must be a number."); 
	 }
	 
	 private void checkNumberOperands(Token operator, Object left, Object right) {   
		 if (left instanceof Double && right instanceof Double) return;
		 throw new RuntimeError(operator, "Operands must be numbers.");
	 }
	
}
