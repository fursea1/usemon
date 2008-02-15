package org.usemon.gui.client;

import java.io.Serializable;
/**
 * 
 * @author t514257
 *
 */
public class FilterGUI implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3621845411670874643L;
	String tableColumn;
	String comparator;
	String value;
	
	/**
	 * defaiult constructor
	 */
	public FilterGUI() {
		
	}
	
	public FilterGUI(String tableColumn, String value, String comparator) {
		this.tableColumn = tableColumn;
		this.value = value;
		this.comparator = comparator; 
	}

	public String getTableColumn() {
		return tableColumn;
	}

	public String getComparator() {
		return comparator;
	}

	public String getValue() {
		return value;
	}

	public void setTableColumn(String tableColumn) {
		this.tableColumn = tableColumn;
	}

	public void setComparator(String comparator) {
		this.comparator = comparator;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
