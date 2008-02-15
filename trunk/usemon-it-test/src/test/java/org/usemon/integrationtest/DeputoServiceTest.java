/**
 * 
 */
package org.usemon.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.DeputoService;
import org.usemon.domain.HeapObservation;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodObservation;
import org.usemon.domain.Observation;
import org.usemon.domain.UsemonServiceLocator;
import org.usemon.service.DbmsHelper;
import org.usemon.service.dao.TestDataGenerator;
import org.usemon.service.util.ResultSetProcessor;

/**
 * @author t547116
 * 
 */
public class DeputoServiceTest {

	private static final int AVG_RESPONSE_FLAG = 999999;

	private static Logger log = LoggerFactory.getLogger(DeputoService.class);

	DeputoService deputoService;

	@org.junit.Before
	public void oneTimeSetup() {
		deputoService = UsemonServiceLocator.getDeputoService();
		
	}

	/**
	 * Test method for
	 * {@link org.usemon.service.DeputoServiceImpl#DeputoServiceImpl()}.
	 */
	@Test
	public void testDeputoServiceImpl() {
		assertNotNull(deputoService);
	}

	/**
	 * Test method for
	 * {@link org.usemon.service.DeputoServiceImpl#addObservation(org.usemon.domain.Observation)}.
	 */
	@Test
	public void testAddSingleMethodObservation() {

		long start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			Observation observation = TestDataGenerator.generateMethodObservation();
			// lazy toString()
			// String s = ToStringBuilder.reflectionToString(observation,
			// ToStringStyle.MULTI_LINE_STYLE);
			// System.err.println(s);
			assertNotNull (observation.getPeriodLength());
			deputoService.addObservation(observation);
		}
		long end = System.currentTimeMillis();
		long elapsed = end - start;
		log.info("Elapsed per single entry: " + (elapsed / 1000.0));
	}

	@Test
	public void testAddAndVerify() {
		// Deletes measurement with given avg_response_time
		String deleteSql = "delete FROM   method_measurement_fact where avg_response_time=" + AVG_RESPONSE_FLAG;
		DbmsHelper.executeUpdate(deleteSql);
		
		MethodObservation m = TestDataGenerator.generateMethodObservation();
		m.setAvgResponseTime(AVG_RESPONSE_FLAG);
		m.setPrincipal("JUnitTest");
		deputoService.addObservation(m);

		final Calendar cal = Calendar.getInstance();
		cal.setTime(m.getTimeStamp());
		
		// reads the data back from the database
		// This select verifies that new entry now exsits
		String selectSql = "SELECT  * " +
				"FROM   method_measurement_fact map   " +
				"join d_date on d_date.id=map.d_date_id   " +
				"join d_time on d_time.id=map.d_time_id " +
				"where avg_response_time=" + AVG_RESPONSE_FLAG;
		DbmsHelper.executeQuery(selectSql, new ResultSetProcessor() {
			int count = 0;
			Calendar tcal = Calendar.getInstance();	// Temporary calendar
			
			public Object forEachRow(ResultSet rs) {
				if (count++ > 1)
					fail("Expected only a single row");
				try {
					// Inspects the time part 
					Date timestamp = rs.getTime("time_v");
					tcal.setTime(timestamp);
					assertEquals("Timestamp: " + timestamp,cal.get(Calendar.HOUR_OF_DAY), tcal.get(Calendar.HOUR_OF_DAY));
					assertEquals("Timestamp: " + timestamp,cal.get(Calendar.MINUTE), tcal.get(Calendar.MINUTE));
					assertEquals("Timestamp: " + timestamp,cal.get(Calendar.SECOND), tcal.get(Calendar.SECOND));
					// Inspects the date part
					Date date = rs.getDate("date_v");
					tcal.setTime(date);
					assertEquals(cal.get(Calendar.YEAR), tcal.get(Calendar.YEAR));
					assertEquals(cal.get(Calendar.MONTH), tcal.get(Calendar.MONTH));
					assertEquals(cal.get(Calendar.DAY_OF_MONTH), tcal.get(Calendar.DAY_OF_MONTH));
					
				} catch (SQLException e) {
					throw new IllegalStateException(e);
				}
				
				return null;
			}});
	}
	
	@Test
	public void testAddObservationWithNullValueForChannel() {
		MethodObservation observation = TestDataGenerator.generateMethodObservation();
		observation.setChannel(null);
		deputoService.addObservation(observation);
	}

	@Test
	public void testAddInvocationObservation() {
		InvocationObservation invocationObservation = TestDataGenerator.generateInvocationObservation();
		deputoService.addObservation(invocationObservation);
		
		invocationObservation = TestDataGenerator.generateInvocationObservation(new Date());
		String s = "very loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo";
		assertTrue(s.length() > 254);
		invocationObservation.getSourceMethod().setInstanceId(s);
		deputoService.addObservation(invocationObservation);
	}
	
	@Test
	public void testAddSeveralMethodObservations() throws SQLException {
		List<MethodObservation> mlist = new ArrayList<MethodObservation>();
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		for (int i = 0; i < 20; i++) {
			MethodObservation observation = (MethodObservation) TestDataGenerator.generateMethodObservation();
			cal.setTime(observation.getTimeStamp());
			cal.add(Calendar.HOUR_OF_DAY, 1);
			// lazy toString()
			// String s = ToStringBuilder.reflectionToString(observation,
			// ToStringStyle.MULTI_LINE_STYLE);
			// System.err.println(s);
			assertNotNull(observation.getPeriodLength());
			mlist.add(observation);
		}
		long start = System.currentTimeMillis();
		deputoService.addMethodObservations(mlist);
		long end = System.currentTimeMillis();
		long elapsed = end - start;
		log.info("Elapsed time:" + elapsed);
		log.info("Elapsed per entry " + (elapsed / 1000.0));
		
		Connection con = DbmsHelper.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT date_v, count(*) FROM d_date d group by 1 having count(*) > 1;");
		Map<String, Integer> resultValues = new HashMap<String, Integer>();
		while (rs.next())
			resultValues.put(rs.getString(1), rs.getInt(2));
		
		assertEquals("Duplicate entries in date dimension",0,resultValues.size());
	}

	@Test
	public void testAddSeveralInvocationObservations() {
		List<InvocationObservation> invocationObservationList = new ArrayList<InvocationObservation>();
		for (int i = 0; i < 1000; i++) {
			InvocationObservation invocationObservation = TestDataGenerator.generateInvocationObservation();
			invocationObservationList.add(invocationObservation);
		}

		deputoService.addInvocationObservations(invocationObservationList);
	}

	@Test
	public void testAddSeveralHeapObservations() {
		List<HeapObservation> list = new ArrayList<HeapObservation>();
		for (int i = 0; i < 1000; i++) {
			HeapObservation heapObservation = TestDataGenerator.generateHeapObservation();
			list.add(heapObservation);
		}
		deputoService.addHeapObservations(list);
	}

//	@Test
//	public void testGetLastMethodObservation() {
//		Date dt = deputoService.getLastMethodObservation();
//		assertNotNull(dt);
//	}
}
