package org.usemon.domain.query;

public class QueryFact {

	String factName;
	boolean highestValue;
	
	public QueryFact(String factName, boolean highestValue) {
		this.factName = factName;
		this.highestValue = highestValue;
	}

	public String getFactName() {
		return factName;
	}

	public boolean isHighestValue() {
		return highestValue;
	}
}
