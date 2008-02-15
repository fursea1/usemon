/**
 * 
 */
package org.usemon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author t514257 - Jarle Dagestad-Thu
 *
 */
public class UsemonQueryResult implements Serializable{
	ArrayList columnNames;
	ArrayList rows = new ArrayList();
	String splitByName;
	String splitByValue;
	// Holds all the rows. The key of the map being the value
	// of the vertical dimension, while the map entry value is
	// an array of the fact values
	HashMap resultMap =null;  // <String, <Double[]>>
	UsemonQueryObject queryObject;
	
	public UsemonQueryObject getQueryObject() {
		return queryObject;
	}

	public void setQueryObject(UsemonQueryObject queryObject) {
		this.queryObject = queryObject;
	}

	public void addRow(String[] rowValues) {
		rows.add(rowValues);
	}
	
	public void setColumnNames(ArrayList columnNames) {
		this.columnNames = columnNames;
	}

	public String getSplitByName() {
		return splitByName;
	}

	public void setSplitByName(String splitByName) {
		this.splitByName = splitByName;
	}

	public String getSplitByValue() {
		return splitByValue;
	}

	public void setSplitByValue(String splitByValue) {
		this.splitByValue = splitByValue;
	}

	public ArrayList getColumnNames() {
		return columnNames;
	}

	public ArrayList getRows() {
		return rows;
	}

	public HashMap getResultMap() {
		return resultMap;
	}

	public void setResultMap(HashMap resultMap) {
		this.resultMap = resultMap;
	}


}
