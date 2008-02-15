/**
 * 
 */
package org.usemon.gui.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author t514257
 *
 */
public class UsemonQueryResultSetGUI implements Serializable{
	/**
	 * @gwt.typeArgs <java.lang.String>	
	 */	
	ArrayList columnNames;
	
	/**
	 * @gwt.typeArgs <java.lang.String,java.lang.Double[]>
	 */ 
	HashMap resultMap =null;	
	
	UsemonQueryObjectGUI queryObject;
	
	public HashMap getResultMap() {
		return resultMap;
	}

	public void setResultMap(HashMap resultMap) {
		this.resultMap = resultMap;
	}

	
	public void setColumnNames(ArrayList columnNames) {
		this.columnNames = columnNames;
	}

	public ArrayList getColumnNames() {
		return columnNames;
	}


	public UsemonQueryObjectGUI getQueryObject() {
		return queryObject;
	}

	public void setQueryObject(UsemonQueryObjectGUI queryObject) {
		this.queryObject = queryObject;
	}

}
