/**
 * Created 10. jan.. 2008 13.43.06 by Steinar Overbeck Cook
 */
package org.usemon.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MegaCollectorTest {

	private static final int NUMBER_OF_USAGE_OBJECTS = 400000;
	private static final Logger log = LoggerFactory.getLogger(MegaCollectorTest.class);
	Collector collector;
	SlowDeputoService slowDeputoService = new SlowDeputoService();

	@Before
	public void setUp() {
		Runtime r = Runtime.getRuntime();
		System.out.format("maxmemory:   %10d\n", r.maxMemory());
		System.out.format("totalMemory: %10d\n", r.totalMemory());
		System.out.format("freeMemory:  %10d\n", r.freeMemory());
		System.out.format("available:   %10d\n", r.maxMemory() - r.totalMemory() + r.freeMemory());
		float max = r.maxMemory();
		float available = r.maxMemory() - r.totalMemory() + r.freeMemory();
		float availablePct = available / max;

		System.out.format("Available in %% is %4.2f\n", availablePct);
	}

	@Test
	public void megaTest() throws InterruptedException {
		collector = new Collector();
		collector.setDeputoService(slowDeputoService);
		System.err.println("Started running test...");
		Thread collectorThread = new Thread(collector);
		System.err.println("Thread created");
		collectorThread.start();
		assertNotNull("Unable to start the collection thread", collectorThread);

		float lossRatio = 0;
		
		for (int sent = 1; sent <= NUMBER_OF_USAGE_OBJECTS; sent++) {
			Usage usage = new Usage(new MethodInfo(Info.COMPONENT_CUSTOM, "P1", "C" + sent, "M1", "()"));
			usage.addSample("prince1", 50, null);
			collector.addUsageMessage(usage);
			
			float lost = collector.getSkippedUsageObjects();
			lossRatio = lost / sent;
			
			assertTrue("Loosing more than 75% of usage objects. Have sent " + sent + " objects", lossRatio < 0.75f);
		}

		log.debug("Completed generation of " + NUMBER_OF_USAGE_OBJECTS + " usage objects, now waiting for collector thread");

		long sleep = (collector.getNumberOfQueuedUsageMessages() / collector.getQueueFlushThreshold() * slowDeputoService.getSleepPeriod());
		sleep += 3000;
		log.debug("Sleeping for a period of " + sleep + "ms");
		Date wakeupAt = new Date(System.currentTimeMillis() + sleep);
		log.debug("Waking up at " + wakeupAt);

		Thread.sleep(sleep); // wait to let collector thread complete
		assertTrue(collector.getSkippedUsageObjects() > 0);
		if (collector.getSkippedUsageObjects() > 0)
			log.warn("Skipped " + collector.getSkippedUsageObjects() + " usage objects");
		// The collectors input queue should be empty
		assertEquals(0, collector.getNumberOfQueuedUsageMessages());

		// Terminates the collector thread
		collectorThread.interrupt();
		collectorThread.join(1);

		// We expect the collector to have received at least more than zero
		assertTrue("Incorrect number of usage message received in collector", collector.getNumberOfReceivedUsageMessages() > 0);
		
		assertTrue("Unacceptable lossratio: " + lossRatio + " with Deputo latency of " + slowDeputoService.getSleepPeriod() + "ms", lossRatio < 0.5f);
		log.info("Loss ratio with a delay of " + slowDeputoService.getSleepPeriod() + " ms, was " + lossRatio);
	}

	public static class SlowDeputoService implements DeputoService {

		public static final int DEFAULT_SLEEP_IN_MILLIS = 5;

		private int sleepPeriod = DEFAULT_SLEEP_IN_MILLIS;

		protected void setSleepPeriod(int sleepPeriod) {
			this.sleepPeriod = sleepPeriod;
		}

		protected int getSleepPeriod() {
			return sleepPeriod;
		}

		public void addHeapObservations(List<HeapObservation> list) {
			try {
				Thread.sleep(sleepPeriod);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void addInvocationObservations(List<InvocationObservation> invocationObservationList) {
			try {
				Thread.sleep(sleepPeriod);
			} catch (InterruptedException e) {
			}
		}

		public void addMethodObservations(List<MethodObservation> methodObservations) {
			try {
				Thread.sleep(sleepPeriod);
			} catch (InterruptedException e) {
			}
		}

		public void addObservation(Observation observation) {
			try {
				Thread.sleep(sleepPeriod);
			} catch (InterruptedException e) {
			}
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
