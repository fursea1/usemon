package test.usemon.compliance.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;

import test.usemon.compliance.ComplianceTestContainer;

public class QueueSenderImpl implements javax.jms.QueueSender {

	private Queue queue;

	public QueueSenderImpl(Queue queue) {
		this.queue = queue;
	}

	public Queue getQueue() throws JMSException {
		return queue;
	}

	public void send(Message m) throws JMSException {
		ComplianceTestContainer.queue.add(m);
	}

	public void send(Queue arg0, Message m) throws JMSException {
		ComplianceTestContainer.queue.add(m);
	}

	public void send(Message m, int arg1, int arg2, long arg3) throws JMSException {
		ComplianceTestContainer.queue.add(m);
	}

	public void send(Queue arg0, Message m, int arg2, int arg3, long arg4) throws JMSException {
		ComplianceTestContainer.queue.add(m);
	}

	@Override
	public void close() throws JMSException {
		// TODO Auto-generated method stub
	}

	@Override
	public int getDeliveryMode() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDisableMessageID() throws JMSException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDisableMessageTimestamp() throws JMSException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPriority() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTimeToLive() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDeliveryMode(int arg0) throws JMSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDisableMessageID(boolean arg0) throws JMSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDisableMessageTimestamp(boolean arg0) throws JMSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPriority(int arg0) throws JMSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimeToLive(long arg0) throws JMSException {
		// TODO Auto-generated method stub
		
	}

	
	
}
