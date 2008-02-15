/**
 * 
 */
package org.usemon.domain;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class TimeStampHelper {

	private static Calendar cal = Calendar.getInstance();

	/**
	 * The supplied time stamp is modified so that the milliseconds and seconds
	 * are knocked off
	 * 
	 * @param dt
	 *            time stamp to modify
	 * @return reference to the modified time stamp
	 */
	public static Date modifyTimeStampGranularity(Date dt) {
		cal.setTime(dt);
		cal.clear(Calendar.MILLISECOND);
		cal.clear(Calendar.SECOND);
		dt.setTime(cal.getTimeInMillis());
		return cal.getTime();
	}

	public static Date clearTimeAttributes(Date dt) {
		Calendar tmpCal = Calendar.getInstance();
		tmpCal.setTime(dt);
		cal.clear();
		cal.set(Calendar.YEAR, tmpCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, tmpCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, tmpCal.get(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	public static Date clearDateAttributes(Date dt) {
		Calendar tmpCal = Calendar.getInstance();
		tmpCal.setTime(dt);
		cal.clear();
		cal.set(Calendar.HOUR_OF_DAY, tmpCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, tmpCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, tmpCal.get(Calendar.SECOND));
		return cal.getTime();
	}
}
