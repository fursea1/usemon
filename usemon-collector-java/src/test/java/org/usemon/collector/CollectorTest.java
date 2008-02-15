/**
 * Created 10. jan.. 2008 13.43.06 by Steinar Overbeck Cook
 */
package org.usemon.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.usemon.domain.DeputoService;
import org.usemon.domain.HeapObservation;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodObservation;
import org.usemon.domain.Observation;
import org.usemon.usageinfo.Info;
import org.usemon.usageinfo.MethodInfo;
import org.usemon.usageinfo.Usage;

/**
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class CollectorTest {

	private static final int NUMBER_OF_USAGE_OBJECTS = 501;
	Collector collector;
	MockDeputoService mockDeputoService = new MockDeputoService();
	protected Thread collectorThread;

	/**
	 * Creates 500 usage objects, shoves them into the collectors queue, after which we ensure that the collector has processed the
	 * usage messages as expected.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testRun() throws InterruptedException {
		System.err.println("Thread running");
		for (int i = 0; i < NUMBER_OF_USAGE_OBJECTS; i++) {
			Usage usage = new Usage(new MethodInfo(Info.COMPONENT_CUSTOM, "P1", "C" + i, "M1", "()"));
			usage.addSample("prince1", 50, null);
			List<MethodObservation> ml = ObservationMappingUtils.buildMethodObservationFromMethodUsage(usage);
			assertTrue("error in testcase", ml.size() > 0);

			collector.addUsageMessage(usage);
		}
		Thread.sleep(2 * 1000); // Wait a second to allow the collector to process all items
		assertTrue(collector.getNumberOfQueuedUsageMessages() == 0); // The queue should be empty

		assertEquals("Collector has not received the expected number of objects",NUMBER_OF_USAGE_OBJECTS, collector.getNumberOfReceivedUsageMessages());

		assertTrue( mockDeputoService.addMethodObservationsInvocationCounter > 0);
	}

	/**
	 * @throws InterruptedException
	 */
	@After
	public void tearDown() throws InterruptedException {
		if (collectorThread != null) {
			collectorThread.interrupt();
			collectorThread.join(1);
		}
	}

	/**
	 * @return
	 */
	@Before
	public void startUp() {
		collector = new Collector();
		collector.setDeputoService(mockDeputoService);
		System.err.println("Started running test...");
		collectorThread = new Thread(collector);
		System.err.println("Thread created");
		collectorThread.start();
		assertNotNull("Unable to start the collection thread", collectorThread);
	}

	protected static class MockDeputoService implements DeputoService {

		int heapObservationCount;
		int invocationObservationCount;
		int addMethodObservationsInvocationCounter;
		int observationCount;

		public void addHeapObservations(List<HeapObservation> list) {
			heapObservationCount++;
		}

		public void addInvocationObservations(List<InvocationObservation> invocationObservationList) {
			invocationObservationCount++;
		}

		public void addMethodObservations(List<MethodObservation> methodObservations) {
			addMethodObservationsInvocationCounter++;
		}

		public void addObservation(Observation observation) {
			observationCount++;
		}

		public float getCacheHitRatio() {
			return 0;
		}

		public int getCacheHits() {
			return 0;
		}

		public Date getLastMethodObservation() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
