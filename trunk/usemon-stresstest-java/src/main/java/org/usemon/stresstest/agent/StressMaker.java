package org.usemon.stresstest.agent;

import java.util.Random;

import org.usemon.usageinfo.Info;

import com.usemon.agent.registry.Registry;

public class StressMaker implements Runnable {

	long invocationCounter = 0;

	private static Random rnd = new Random();
	private static final String[] NAMES = new String[] { "Eos", "Metis", "Atlas", "Leto", "Steinar", "PaulRene", "David", "Goliat", "AlfRune", "Asgeir" };

	private int stressMakerId;
	
	public int getStressMakerId() {
		return stressMakerId;
	}

	public void setStressMakerId(int stressMakerId) {
		this.stressMakerId = stressMakerId;
	}

	public StressMaker(int stressMakerId) {
		this.stressMakerId = stressMakerId;
	}

	public void run() {
		while (true) {
			generateEntry();
			// Places our thread at the end of the priority queue, allowing the JVM
			// to run another thread.
			if (invocationCounter % 1000 == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.err.println("Interrupted while sleeping, thread stops. Queue size " + Registry.getUsageCollectionSize());
					System.err.println("Generated " + invocationCounter + " entries");
					break;
				}
			}
		}
	}

	protected void generateEntry() {
		String packageName = generatePackageName();
		String simpleClassName = generateClassName();
		String methodName = generateMethodName();
		String signature = "v;";
		String modifiers = "public";
		String principal = generatePrincipal();
		invocationCounter++;
		int responseTime = rnd.nextInt(40) + 5;
		Registry.invocation(Info.COMPONENT_CUSTOM, packageName, simpleClassName, methodName, signature, modifiers, responseTime, principal, null);
	}

	public int getUsageMapSize() {
		return Registry.getUsageCollectionSize();
	}
	
	private String generatePrincipal() {
		int i = rnd.nextInt(NAMES.length);
		return NAMES[i];
	}

	/**
	 * @return
	 */
	protected static String generateMethodName() {
		return generateClassName().toLowerCase();
	}

	/**
	 * @return
	 */
	protected static String generateClassName() {
		return NAMES[rnd.nextInt(NAMES.length)];
	}

	protected static String generatePackageName() {
		StringBuffer packageName = new StringBuffer();
		int count = rnd.nextInt(4) + 1;
		while (count-- > 0) {
			packageName.append(generateMethodName());
			if (count > 0)
				packageName.append(".");
		}
		return packageName.toString();
	}

	public long getInvocationCounter() {
		return invocationCounter;
	}

	public void setInvocationCounter(long sendCounter) {
		this.invocationCounter = sendCounter;
	}

}
