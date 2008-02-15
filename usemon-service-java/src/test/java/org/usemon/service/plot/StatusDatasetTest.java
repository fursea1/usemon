/**
 * Created 29. okt.. 2007 15.33.00 by Steinar Overbeck Cook
 */
package org.usemon.service.plot;

import java.util.Calendar;

import org.jfree.data.time.Minute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.service.plot.StatusDataset;

import junit.framework.TestCase;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class StatusDatasetTest extends TestCase {
	private static final Logger log = LoggerFactory.getLogger(StatusDatasetTest.class);
	StatusDataset statusDataset = null;
	protected void setUp() throws Exception {
		statusDataset = generateDummyStatusDataSet();
		super.setUp();
	}
	
	/**
	 * @return 
	 * 
	 */
	public static StatusDataset generateDummyStatusDataSet() {
		StatusDataset statusDataset = new StatusDataset();
		
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < 30; i++) {
			if (log.isDebugEnabled()) {
				log.debug("Time is " +  cal.getTime());
			}
			if (i == 0)
				statusDataset.setEnd(cal.getTime());
			Minute minute = new Minute(cal.getTime());
			statusDataset.addInvocationCount( minute, (int)(10000*Math.random()) );
			statusDataset.addCheckedExceptionsDataset(minute, (int)(100*Math.random()));
			statusDataset.addUncheckedExceptionsDataset(minute, (int)(1000*Math.random()));
			statusDataset.addMaxResponseTimeDataset(minute, (int)(1000*Math.random()));
			statusDataset.addAvgResponseTimeDataset(minute, (int)(100*Math.random()));
			cal.add(Calendar.MINUTE, -1);
			
		}
		statusDataset.setStart(cal.getTime());
		return statusDataset;
	}
	
	/**
	 * Test method for {@link org.usemon.service.plot.StatusDataset#getDatasetCollection()}.
	 */
	public void testGetDatasetCollection() {
		assertNotNull(statusDataset.getDatasetCollection());
		assertEquals(3, statusDataset.getDatasetCollection().size());
	}

	/**
	 * Test method for {@link org.usemon.service.plot.StatusDataset#getInvocationCountDataset()}.
	 */
	public void testGetInvocationCountDataset() {
		assertNotNull(statusDataset.getInvocationCountDataset());
		assertEquals(30, statusDataset.getInvocationCountDataset().getSeries(0).getItemCount());
	}

	/**
	 * Test method for {@link org.usemon.service.plot.StatusDataset#getExceptionDataset()}.
	 */
	public void testGetExceptionDataset() {
		assertNotNull(statusDataset.getExceptionDataset());
	}

}
