package com.netlify.totallynotafterurmoneys.yeestandardlibryary;

import java.util.Base64;
import java.util.List;

import com.netlify.totallynotafterurmoneys.yeeinterpretor.Interpreter;
import com.netlify.totallynotafterurmoneys.yeeinterpretor.YeeCallable;

public class Base64Dec implements YeeCallable {

	@Override
	public int arity() {
		return 1;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		if (arguments.size() == 1)  {
			return base64Decode((String) arguments.get(0));
		}
		else return null;
	}
	
	private String base64Decode(String input) {
		byte[] decodedBytes = Base64.getDecoder().decode(input);
		String base64Decoded = new String(decodedBytes);
		return base64Decoded;
	}

}
