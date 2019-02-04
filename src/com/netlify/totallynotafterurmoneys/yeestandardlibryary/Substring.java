package com.netlify.totallynotafterurmoneys.yeestandardlibryary;

import java.util.List;

import com.netlify.totallynotafterurmoneys.yeeinterpretor.Interpreter;
import com.netlify.totallynotafterurmoneys.yeeinterpretor.YeeCallable;

public class Substring implements YeeCallable {

	@Override
	public int arity() {
		return 3;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		if (arguments.size() == 3) {
			String oper = (String) arguments.get(0);
			double first = (double) arguments.get(1);
			double second = (double) arguments.get(2);
			return (String) oper.substring((int) first, (int) second);
		}
		else return null;
	}

}
