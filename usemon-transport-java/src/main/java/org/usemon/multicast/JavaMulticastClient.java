package org.usemon.multicast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.usemon.usageinfo.Usage;

public class JavaMulticastClient extends AbstractMulticastClient {

	/**
	 * @param multicastAddress
	 * @param multicastPort
	 * @param queue
	 *            the queue onto which the received objects should be added
	 * @throws IOException
	 */
	public JavaMulticastClient(final String multicastAddress, final int multicastPort, final MulticastProcessor multicastProcessor) {
		super(multicastAddress, multicastPort, multicastProcessor);
	}

	/** Transforms a buffer into a Usage object */
	Object transformToObject(final byte[] buffer) {
		Object obj = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer));
			obj = ois.readObject();
			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!(obj instanceof Usage))
			// FIXME: handle objects which are not Usage objects
			return null;
		else
			return obj;
	}

	protected void sendToMulticastReceiver(MulticastProcessor multicastProcessor, Object receivedObject) {
		assert receivedObject != null;
		multicastProcessor.addUsageMessage((Usage)receivedObject);
	}
}