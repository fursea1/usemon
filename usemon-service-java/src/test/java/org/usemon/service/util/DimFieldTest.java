package org.usemon.service.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.usemon.domain.MethodObservation;
import org.usemon.service.dao.TestDataGenerator;

public class DimFieldTest {

	@Test
	public void testGetGetterMethod() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		MethodObservation methodObservation = (MethodObservation) TestDataGenerator.generateMethodObservation();
		DimField dimField = new DimField(MethodObservation.class, Dimension.LOCATION, "location", "location_id");
		Object value = dimField.getGetterMethod().invoke(methodObservation);
		assertEquals(methodObservation.getLocation(), value);
		
		methodObservation.setObservationLocation(null);
		value = dimField.getGetterMethod().invoke(methodObservation);
		assertNull(value);
	}

	@Test
	public void testGetValue() {
		MethodObservation observation = TestDataGenerator.generateMethodObservation();
		Calendar tcal = Calendar.getInstance();
		tcal.set(2007, 11-1, 28, 14, 56, 24);
		observation.setTimeStamp(tcal.getTime());
		DimField dateField = new DimField(MethodObservation.class, Dimension.DATE, "timeStamp","d_date_id");
		
		Date dt = (Date) dateField.getValue(observation);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0,cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));
		assertEquals(2007, cal.get(Calendar.YEAR));

		// Verifies that we have knocked of the date part from the time stamp
		DimField timeField = new DimField(MethodObservation.class, Dimension.TIME, "timeStamp", "d_time_id");
		Date time = (Date) timeField.getValue(observation);
		cal.setTime(time);

		Calendar cal2 = Calendar.getInstance();
		cal2.clear();
		assertEquals(cal2.get(Calendar.YEAR), cal.get(Calendar.YEAR));
		assertEquals(cal2.get(Calendar.MONTH), cal.get(Calendar.MONTH));
		assertEquals(cal2.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(cal.get(Calendar.HOUR_OF_DAY), tcal.get(Calendar.HOUR_OF_DAY));
		assertEquals(cal.get(Calendar.MINUTE), tcal.get(Calendar.MINUTE));
		assertEquals(cal.get(Calendar.SECOND), tcal.get(Calendar.SECOND));
	}
}
