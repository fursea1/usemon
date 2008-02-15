/**
 * 
 */
package org.usemon.gui.client.utils;

/**
 * @author t514257
 *
 */
public class DateTimeUtils {

	private static final int MILLIS_PER_SECOND = 1000;
	private static final int MINUTE = 60;
	private static final int HOUR = 60;
	private static final int DAY = 24;
	private static final int WEEK = 7;
	private final static int MONTH = 30;
	
	
	public static long getMillisPerMinute() {
		return MILLIS_PER_SECOND * MINUTE;
	}
	
	public static long getMillisPerHour() {
		return getMillisPerMinute() * HOUR;
	}
	
	public static long getMillisPerDay() {
		return getMillisPerHour() * DAY;
	}
	
	public static long getMillisPerWeek() {
		return getMillisPerDay() * WEEK;
	}
	
	public static long getMillisPerMonth() {
		return getMillisPerDay() * MONTH;
	}
	
}
