package test.usemon.compliance.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

public class QueueImpl implements Queue {
	private String queueName;
	
	public QueueImpl(String queueName) {
		this.queueName = queueName;
	}
	
	public String getQueueName() throws JMSException {
		return queueName;
	}
	
	public String toString() {
		try {
			return getQueueName();
		} catch (JMSException e) {
			return super.toString();
		}
	}

}
