/**
 * 
 */
package org.usemon.domain;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class TimeStampHelperTest {

	private static final int H14 = 14;

	/**
	 * Test method for {@link org.usemon.domain.TimeStampHelper#modifyTimeStampGranularity(java.util.Date)}.
	 */
	/**
	 * Test method for {@link org.usemon.service.DeputoServiceImpl#modifyTimeStampGranularity(java.util.Date)}.
	 */
	@Test
	public void testModifyTimeStampGranularity() {
		Date dt = new Date();

		Date dt2 = TimeStampHelper.modifyTimeStampGranularity(dt);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt2);
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0,cal.get(Calendar.MILLISECOND));
		
	}

	@Test
	public void testClearDateAttributes() {
		Calendar tcal = Calendar.getInstance();
		tcal.set(2007, 11-1, 28, H14, 56, 24);
		Date timeStamp = TimeStampHelper.clearDateAttributes(tcal.getTime());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(timeStamp);
		
		assertEquals(H14, cal.get(Calendar.HOUR_OF_DAY));
	}

	@Test 
	public void testClearTimeStampAttributes() {
		Calendar tcal = Calendar.getInstance();
		tcal.set(2007, 11-1, 28, H14, 56, 24);
		Date dt = TimeStampHelper.clearTimeAttributes(tcal.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		assertEquals(2007,cal.get(Calendar.YEAR));
		assertEquals(11-1,cal.get(Calendar.MONTH));
		assertEquals(28,cal.get(Calendar.DAY_OF_MONTH));

		tcal.clear();
		tcal.set(2007,11-1,28);
		assertTrue(tcal.getTime() + "!=" + cal.getTime(),tcal.compareTo(cal) == 0);
	}
}
