package org.usemon.gui.client;

import java.io.Serializable;

/**
 * 
 * @author t514257
 *
 */
public class QueryFactGUI implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4329976720582719773L;
	String factName;
	boolean highestValue;
	
	public QueryFactGUI(){
		
	}
	
	public QueryFactGUI(String factName, boolean highestValue) {
		this.factName = factName;
		this.highestValue = highestValue;
	}

	public String getFactName() {
		return factName;
	}

	public boolean isHighestValue() {
		return highestValue;
	}

	public void setFactName(String factName) {
		this.factName = factName;
	}

	public void setHighestValue(boolean highestValue) {
		this.highestValue = highestValue;
	}
}
