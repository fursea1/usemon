package org.usemon.test.agent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AgentItegrationTest {

	private enum Version { V13, V14, V50, V60, UNKNOWN }
	
	private Version version;
	
	@Before
	public void setup() {
		String vendorStr = System.getProperty("java.vm.vendor");
		if("Sun Microsystems Inc.".equals(vendorStr)) {
		} else if("IBM".equals(vendorStr)) {
		} else if("BEA".equals(vendorStr)) {
		} else {
		}
		
		String versionStr = System.getProperty("java.vm.version");
		if(versionStr.startsWith("1.6")) {
			version = Version.V60;
		} else if(versionStr.startsWith("1.5")) {
			version = Version.V50;
		} else if(versionStr.startsWith("1.4")) {
			version = Version.V14;
		} else if(versionStr.startsWith("1.3")) {
			version = Version.V13;
		} else {
			version = Version.UNKNOWN;
		}
		
		if(version.compareTo(Version.V50)<0) {
			System.out.println("PRE 50");
		} else {
			System.out.println("50 or older");
		}

	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void test() {
	}

}
