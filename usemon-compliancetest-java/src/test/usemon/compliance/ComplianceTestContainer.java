package test.usemon.compliance;

import java.io.IOException;
import java.util.Random;

import javax.jms.JMSException;
import javax.servlet.ServletException;

import test.usemon.compliance.servlet.TestHttpServletRequestImpl;
import test.usemon.compliance.servlet.TestServletConfigImpl;
import test.usemon.compliance.servlet.TestServletImpl;

public class ComplianceTestContainer {
	private static final Random rnd = new Random();

	public static void main(String[] args) throws InterruptedException, ServletException, IOException, JMSException {
		// Servlet
		TestHttpServletRequestImpl req = new TestHttpServletRequestImpl();
		TestServletImpl ts = new TestServletImpl();
		ts.init(new TestServletConfigImpl());

		for(int n=0;n<500;n++) {
			ts.service(req, null);
			Thread.sleep(rnd.nextInt(1500));
		}			
	}
}
