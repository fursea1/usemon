/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import java.util.HashMap;

/**
 * @author t514257
 *
 */
public class FactDBConstants {
	public final static String AVG_RESP_TIME = "avg(avg_response_time)";
	public final static String MAX_RESP_TIME = "avg(max_response_time)";
	public final static String INV_COUNT = "sum(invocation_count)";
	public final static String CHECKED_EXCEPTIONS = "sum(checked_exceptions)";
	public final static String UNCHECKED_EXCEPTIONS = "sum(unchecked_exceptions)";

	
	public final static String AVG_RESP_TIME_DESC = "Average response time";
	public final static String MAX_RESP_TIME_DESC = "Max response time)";
	public final static String INV_COUNT_DESC = "Invocation count";
	public final static String CHECKED_EXCEPTIONS_DESC = "Checked exceptions";
	public final static String UNCHECKED_EXCEPTIONS_DESC = "Unchecked exceptions";
	
	HashMap tableAndDescription;
	
	private FactDBConstants() {
		tableAndDescription = new HashMap();
		tableAndDescription.put(AVG_RESP_TIME_DESC, AVG_RESP_TIME);
		tableAndDescription.put(MAX_RESP_TIME_DESC, MAX_RESP_TIME);
		tableAndDescription.put(INV_COUNT_DESC, INV_COUNT);
		tableAndDescription.put(CHECKED_EXCEPTIONS_DESC, CHECKED_EXCEPTIONS);
		tableAndDescription.put(UNCHECKED_EXCEPTIONS_DESC, UNCHECKED_EXCEPTIONS);

		
		
	}
	
	public String getTableNameForDescription(String desc) {
		return (String)tableAndDescription.get(desc);
	}
	
	public static FactDBConstants createInstance() {
		return new FactDBConstants();
	}	

}
