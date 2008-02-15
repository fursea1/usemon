/**
 * Created 5. nov.. 2007 10.54.19 by Steinar Overbeck Cook
 */
package org.usemon.service.util;

import java.util.Calendar;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public enum WeekDay {

	sun(Calendar.SUNDAY),
	mon(Calendar.MONDAY),
	tue(Calendar.TUESDAY),
	wed(Calendar.WEDNESDAY),
	thu(Calendar.THURSDAY),
	fri(Calendar.FRIDAY),
	sat(Calendar.SATURDAY);
	
	private int dayNumber;

	private WeekDay(int dayNumber) {
		this.dayNumber = dayNumber;
	}

	
	/** Day numbers are in the range 1..7, sunday being 1, etc. */
	public static WeekDay valueOf(int dayNumber) {
		for (WeekDay day : WeekDay.values()) {
			if (day.dayNumber == dayNumber)
				return day;
		}
		throw new IllegalArgumentException("Unknown day number: " + dayNumber);
	}

}