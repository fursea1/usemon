package org.usemon.multicast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JsonMulticastClient extends AbstractMulticastClient {
	static final String CHARSET = "ISO-8859-1";

	/**
	 * @param multicastAddress
	 * @param multicastPort
	 * @param queue the queue onto which the received objects should be added
	 * @throws IOException
	 */
	public JsonMulticastClient(final String multicastAddress, final int multicastPort,MulticastProcessor multicastProcessor)  {
		super(multicastAddress, multicastPort, multicastProcessor);
	}
	
	Object transformToObject(final byte[] buffer) {
		try {
			return new String(buffer,CHARSET );
		} catch (UnsupportedEncodingException e) {
			// FIXME: what do we do here?
			return null;
		}
	}

	protected void sendToMulticastReceiver(MulticastProcessor multicastProcessor, Object receivedObject) {
		assert receivedObject != null;
		assert receivedObject instanceof String;
		
		multicastProcessor.addJsonMessage((String)receivedObject);
	}
}