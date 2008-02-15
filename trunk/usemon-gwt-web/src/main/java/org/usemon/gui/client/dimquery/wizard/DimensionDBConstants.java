/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import java.util.HashMap;

/**
 * @author t514257
 *
 */
public class DimensionDBConstants {
	
	public final static String LOCATION = "location.location";
	public final static String PACKAGE = "package.package";
	public final static String CLASS = "class.class";
	public final static String METHOD = "method.method";
	public final static String PRINCIPAL = "principal.principal";
	public final static String DAY_OF_WEEK = "d_date.day_of_week_v";
	public final static String DATE = "d_date.date_v";
	public final static String TIME = "d_time.time_v";
	public final static String HOUR_OF_DAY = "d_time.hh";
	
	public final static String LOCATION_DESC = "Location";
	public final static String PACKAGE_DESC = "Package";
	public final static String CLASS_DESC = "Class";
	public final static String METHOD_DESC = "Method";
	public final static String PRINCIPAL_DESC = "Principal";
	public final static String DAY_OF_WEEK_DESC = "Day of week";
	public final static String DATE_DESC = "Date";
	public final static String TIME_DESC = "Time";
	public final static String HOUR_OF_DAY_DESC = "Hour of Day";
	
	HashMap tableAndDescription;
	
	private DimensionDBConstants() {
		tableAndDescription = new HashMap();
		tableAndDescription.put(LOCATION_DESC, LOCATION);
		tableAndDescription.put(PACKAGE_DESC, PACKAGE);
		tableAndDescription.put(CLASS_DESC, CLASS);
		tableAndDescription.put(METHOD_DESC, METHOD);
		tableAndDescription.put(PRINCIPAL_DESC, PRINCIPAL);
		tableAndDescription.put(DAY_OF_WEEK_DESC, DAY_OF_WEEK);
		tableAndDescription.put(DATE_DESC, DATE);
		tableAndDescription.put(TIME_DESC, TIME);
		tableAndDescription.put(HOUR_OF_DAY_DESC, HOUR_OF_DAY);
		
		
	}
	
	public String getTableNameForDescription(String desc) {
		return (String)tableAndDescription.get(desc);
	}
	
	public static DimensionDBConstants createInstance() {
		return new DimensionDBConstants();
	}
}
