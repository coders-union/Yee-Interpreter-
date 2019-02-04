package com.netlify.totallynotafterurmoneys.yeestandardlibryary;

import java.util.Base64;
import java.util.List;

import com.netlify.totallynotafterurmoneys.yeeinterpretor.Interpreter;
import com.netlify.totallynotafterurmoneys.yeeinterpretor.YeeCallable;

public class Base64Enc implements YeeCallable {

	@Override
	public int arity() {
		return 1;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		if (arguments.size() == 1) {
			return base64Encode((String) arguments.get(0));
		}
		else return null;
	}
	
	private String base64Encode(String input) {
		String base64Encoded = Base64.getEncoder().encodeToString(input.getBytes());
		return base64Encoded;
	}
}
