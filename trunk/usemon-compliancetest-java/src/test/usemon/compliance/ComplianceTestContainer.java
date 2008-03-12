package test.usemon.compliance;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.servlet.ServletException;

import test.usemon.compliance.mdb.MessageDrivenBeanImpl;
import test.usemon.compliance.servlet.TestHttpServletRequestImpl;
import test.usemon.compliance.servlet.TestServletConfigImpl;
import test.usemon.compliance.servlet.TestServletImpl;

public class ComplianceTestContainer extends Thread {
	private static final Random rnd = new Random();
	public static final String QUEUE_NAME = "compliance_test_queue";
	public static LinkedList queue = new LinkedList();
	private MessageListener mdb;
	
	public static void main(String[] args) throws InterruptedException, ServletException, IOException, JMSException {
		MessageDrivenBeanImpl mdb = new MessageDrivenBeanImpl();
		ComplianceTestContainer ctc = new ComplianceTestContainer();
		ctc.setMessageDrivenBean(mdb);
		ctc.start();

		// Servlet
		TestHttpServletRequestImpl req = new TestHttpServletRequestImpl();
		TestServletImpl ts = new TestServletImpl();
		ts.init(new TestServletConfigImpl());

		for(int n=0;n<500;n++) {
			ts.service(req, null);
			Thread.sleep(rnd.nextInt(1500));
		}			
	}
	
	private void setMessageDrivenBean(MessageListener mdb) {
		this.mdb = mdb;
	}

	public void run() {
		while(true) {
			while(!queue.isEmpty()) {
				mdb.onMessage((Message) queue.removeFirst());
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
