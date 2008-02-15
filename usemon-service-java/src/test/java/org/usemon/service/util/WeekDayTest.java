/**
 * Created 5. nov.. 2007 10.58.40 by Steinar Overbeck Cook
 */
package org.usemon.service.util;

import java.util.Calendar;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class WeekDayTest {

	@Test
	public void testSunday() {
		WeekDay w = WeekDay.valueOf(Calendar.SUNDAY);
		assertEquals(WeekDay.sun.toString(), w.toString());
		assertEquals("sun", w.toString());
		System.err.println(w);
		Calendar cal = Calendar.getInstance();
		String s = WeekDay.valueOf(cal.get(Calendar.DAY_OF_WEEK)).toString();
		System.out.println(s);
		System.out.println(System.getProperty("java.home"));
	}
}
