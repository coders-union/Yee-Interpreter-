package com.netlify.totallynotafterurmoneys.yeestandardlibryary;

import java.util.List;

import com.netlify.totallynotafterurmoneys.yeeinterpretor.Interpreter;
import com.netlify.totallynotafterurmoneys.yeeinterpretor.YeeCallable;

public class LengthOf implements YeeCallable {
	
	@Override
	public int arity() {
		return 1;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		if (arguments.size() == 1) {
			String yee = (String) arguments.get(0);
			return (double) yee.length();
		}
		else return null;
	}

}
