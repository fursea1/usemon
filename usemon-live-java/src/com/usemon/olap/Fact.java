package com.usemon.olap;

public enum Fact {

	INVOCATIONS("Invocations"),
	AVERAGE_RESPONSE_TIME("Average Response Time"),
	MAX_RESPONSE_TIME("Max Response Time"),
	CHECKED_EXCEPTIONS("Checked Exceptions"),
	UNCHECKED_EXCEPTIONS("Unchecked Exceptions"),
	THREAD_TIME("CPU Time"),
	EXCEPTIONAL_EXITS("Exceptional Exits");
	
	private String factName;

	private Fact(String factName) {
		this.factName = factName;
	}
	
	public String getFactName() {
		return factName;
	}
}
