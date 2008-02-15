package org.usemon.domain.query;

public class Filter {

	String tableColumn;
	ArithmeticComparator comparator;
	String value;
	
	public Filter(String tableColumn, String value, ArithmeticComparator comparator) {
		this.tableColumn = tableColumn;
		this.value = value;
		this.comparator = comparator; 
	}

	public String getTableColumn() {
		return tableColumn;
	}

	public ArithmeticComparator getComparator() {
		return comparator;
	}

	public String getValue() {
		return value;
	}
}
