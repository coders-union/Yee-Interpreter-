package com.netlify.totallynotafterurmoneys.yeestandardlibryary;

import java.util.List;
import java.util.Scanner;

import com.netlify.totallynotafterurmoneys.yeeinterpretor.Interpreter;
import com.netlify.totallynotafterurmoneys.yeeinterpretor.YeeCallable;

public class GetInput implements YeeCallable {
	
	private int type;
	
	public GetInput(int type) {
		this.type = type;
	}

	@Override
	public int arity() {
		return 0;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Scanner getInput = new Scanner(System.in);
		Double dInput;
		String sInput;
		if (type == 1) {
			dInput = getInput.nextDouble();
			return dInput;
		}
		else {
			sInput = getInput.nextLine(); 
			return sInput;
		}
	}

}
