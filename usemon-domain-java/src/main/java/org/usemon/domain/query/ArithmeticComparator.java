package org.usemon.domain.query;

public enum ArithmeticComparator {
	
	EQUALS("like"),
	NOT_EQUALS("not like"),
	LESS_THAN("<"),
	BIGGER_THAN(">")
	;
	
	
	String comparator;
	
	private ArithmeticComparator(String comparator) {
		this.comparator = comparator;
	}

	public String getComparator() {
		return comparator;
	}
	public static ArithmeticComparator getByValue(String value) {
		for (ArithmeticComparator comparator:ArithmeticComparator.values()) {
			if (comparator.getComparator().equals(value))
				return comparator;
		}
		return null;
	}
}
