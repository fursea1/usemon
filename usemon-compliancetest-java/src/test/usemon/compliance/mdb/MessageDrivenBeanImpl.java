package test.usemon.compliance.mdb;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

public class MessageDrivenBeanImpl implements MessageDrivenBean, MessageListener {
	private static final long serialVersionUID = -5962442335698924540L;
	private MessageDrivenContext ctx;

	public void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException {
		this.ctx = ctx;
	}

	public void onMessage(Message message) {
		System.out.println("Message received: "+message);
	}

	public void ejbRemove() throws EJBException {
	}

}
