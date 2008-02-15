package com.usemon.agent.utils;

import java.util.Stack;

public class CallStack extends Stack {
	private static final long serialVersionUID = -6497747627306813974L;

	private boolean firstPop;
	
	public CallStack() {
		super();
	}
	
	public Object push(Object obj) {
		if(size()<=100) { // If something goes wrong we limit the number of entries for each Thread
			firstPop = true;
			return super.push(obj);
		} else {
			return obj;
		}
	}
	
	public synchronized Object pop() {
		Object obj = super.pop();
		firstPop = false;
		if(isEmpty()) {
			firstPop = true;
		}
		return obj;
	}

	public boolean isFirstPop() {
		return firstPop;
	}

}