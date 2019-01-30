package com.netlify.totallynotafterurmoneys.yeestandardlibryary;

import com.netlify.totallynotafterurmoneys.yeeinterpretor.*;

import java.util.List;

public class CharAt implements YeeCallable {

	@Override
	public int arity() {
		return 2;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		if (arguments.size() == 2) {
			String yee = (String) arguments.get(0);
			double index = (double) arguments.get(1);
			return yee.charAt((int) index);
		}
		else return null;
	}

}
