package org.usemon.stresstest.agent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created 6. des. 2007 11.11.17 by Steinar Overbeck Cook
 */

/**
 * Simulates an agent which publishes observations by multicasting them onto the
 * net.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class AgentSimulator {
	private static final String USEMON_DURATION_PROP_NAME = "usemon.duration";

	private static final String USEMON_THREAD_COUNT_PROP_NAME = "usemon.thread.count";

	private static final int STRESS_THREADS = 4;

	private int threadCount = STRESS_THREADS;
	private int duration = 1000*60*5; // run the simulation for 5 minutes as default
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AgentSimulator agentSimulator = new AgentSimulator();
		
		System.out.println("Setting up simulation, inspecting these properties:");
		System.out.format("collectorThread%s - number of simulation threads\n",USEMON_THREAD_COUNT_PROP_NAME);
		System.out.format("collectorThread%s - duration of simulation in milliseconds\n",USEMON_DURATION_PROP_NAME);
		
		String propValue = System.getProperty(USEMON_THREAD_COUNT_PROP_NAME);
		if (propValue != null) {
			int i = Integer.parseInt(propValue);
			agentSimulator.setThreadCount(i);
		}
		
		propValue = System.getProperty(USEMON_DURATION_PROP_NAME);
		if (propValue != null && propValue.length() > 0) {
			int i = Integer.parseInt(propValue);
			agentSimulator.setDuration(i);
		}
		
		agentSimulator.simulate();
	}

	protected void simulate() {
		System.setProperty("usemon.system.id", "stress_system");
		System.setProperty("usemon.cluster.id", "stress_cluster");
		System.setProperty("usemon.server.id", "stress_server");
		
		System.out.format("Starting up %d threads for stress testing the publisher\n", getThreadCount());
		System.out.format("Sleeping for %dms (%d seconds) while stress testing threads are running\n", getDuration(), getDuration()/1000);

		Vector<Thread> threads = new Vector<Thread>();
		List<StressMaker> stressMakerList = new ArrayList<StressMaker>();
		for(int n=0;n<getThreadCount();n++) {
			
			StressMaker stressMaker = new StressMaker(n);
			stressMakerList.add(stressMaker);
			Thread t = new Thread(stressMaker, "StressThread-#"+n);
			t.setDaemon(true);
			threads.add(t);
			t.start();
			System.out.format("Started thread #%d\n", n);
		}
		int testPeriod = getDuration();

		try {
			System.out.println("Main thread waiting while the separate threads are simulating method calls...");
			Thread.sleep(testPeriod);
		} catch (InterruptedException e) {
			System.err.println("Simulationg interrupted - all threads will be halted and the simulation will stop");
			e.printStackTrace();
		}
		
		for(Thread t : threads) {
			t.interrupt();
		}
		long methodInvocations = 0;
		for (StressMaker stressMaker : stressMakerList) {
			methodInvocations += stressMaker.getInvocationCounter();
		}
		System.out.format("Simulation completed, simulated %d method invocations\n", methodInvocations);		
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	
}
