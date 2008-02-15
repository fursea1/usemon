package org.usemon.stresstest.agent;

import org.junit.Test;

public class RegistryStressTest {
	

	
	/** Performs a stress test of the Registry and Publiser
	 * @param args
	 * @throws InterruptedException
	 */
	@Test
	public void stressTestTheAgent() throws InterruptedException {
		AgentSimulator agentSimulator = new AgentSimulator();
		agentSimulator.simulate();
	}
}
