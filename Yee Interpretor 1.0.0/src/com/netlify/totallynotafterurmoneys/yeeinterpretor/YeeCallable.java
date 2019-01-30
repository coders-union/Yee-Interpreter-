package com.netlify.totallynotafterurmoneys.yeeinterpretor;

import java.util.List;

public interface YeeCallable {
	
	int arity();         
	Object call(Interpreter interpreter, List<Object> arguments);
	
}
