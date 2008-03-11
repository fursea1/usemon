package test.usemon.compliance.servlet;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import test.usemon.compliance.ComplianceTestContainer;
import test.usemon.compliance.custom.CustomScopeService;
import test.usemon.compliance.jms.MessageImpl;
import test.usemon.compliance.jms.QueueImpl;
import test.usemon.compliance.jms.QueueSenderImpl;

public class TestServletImpl extends HttpServlet {
	private static final long serialVersionUID = 5222793251610509039L;
	private CustomScopeService service;
	private int counter;
	private Queue q1;
	private QueueSender qs;
	
	public void init() throws ServletException {
		service = new CustomScopeService();
		counter = 0;

		// QueueSender
		q1 = new QueueImpl(ComplianceTestContainer.QUEUE_NAME);
		qs = new QueueSenderImpl(q1);
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			System.out.println(service.sayHello("Paul René - "+counter++));
			qs.send(new MessageImpl());
		} catch (InterruptedException e) {
			throw new ServletException(e);
		} catch (JMSException e) {
			throw new ServletException(e);
		}
	}

}
