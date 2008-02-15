package org.usemon.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public abstract class AbstractMulticastClient implements Runnable {
	private static final int MAX_PACKAGE_SIZE = 1024 * 64; // 64k
	private Thread receiverThread;
	private InetAddress address = null;
	private MulticastSocket socket = null;
	private final int port;
	private MulticastProcessor multicastProcessor;

	/**
	 * @param multicastAddress
	 * @param multicastPort
	 * @param queue
	 *            the queue onto which the received objects should be added
	 * @throws IOException
	 */
	public AbstractMulticastClient(final String multicastAddress, final int multicastPort, final MulticastProcessor multicastProcessor) {
		this.port = multicastPort;
		this.multicastProcessor = multicastProcessor; 
		try {
			address = InetAddress.getByName(multicastAddress);
			socket = new MulticastSocket(port);
			socket.joinGroup(address);
			socket.setTimeToLive(16);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Error establishing address for " + multicastAddress + e.getMessage());
		} catch (IOException e) {
			throw new IllegalStateException("Unable to connect to port " + port + " " + e.getMessage());
		}
	}

	public void startThread() {
		receiverThread = new Thread(this, "MulticastClient Receiver Thread");
		receiverThread.setDaemon(true);
		receiverThread.start();
		System.out.println("MulticastClient on " + address.getHostName() + ":" + socket.getLocalPort() + " started");
	}

	public void run() {
		byte[] buf = new byte[MAX_PACKAGE_SIZE];
		while (true) {
			try {
				DatagramPacket pkg = new DatagramPacket(buf, buf.length);
				socket.receive(pkg); // Receives the data on the socket

				try {
					// Transforms datagram into appropriate Java object
					Object receivedObject = transformPackageToObject(pkg);
					// Shove it into the outgoing queue for processing
					if (receivedObject != null) {
						sendToMulticastReceiver(multicastProcessor, receivedObject);
					}

				} catch (ClassNotFoundException e) {
					// FIXME: this should be logged through logback
					System.err.println("JavaMulticastClient received invalid object, class not found: " + e);
				}
			} catch (IOException e) {
				System.err.println("IOException: " + e.getMessage());
			}
		}
	}

	/**
	 * Ships the received observation to the multicast processor
	 * @param receivedObject
	 */
	protected abstract void sendToMulticastReceiver(MulticastProcessor multicastProcessor,Object receivedObject);


	/** Transforms the received datagram package into an Object */
	private Object transformPackageToObject(final DatagramPacket pkg) throws IOException, ClassNotFoundException {
		byte[] buffer = new byte[pkg.getLength()];

		System.arraycopy(pkg.getData(), pkg.getOffset(), buffer, 0, pkg.getLength());

		// Transforms into the correct type of Object
		return transformToObject(buffer);
	}

	public void close() {
		if (!socket.isClosed()) {
			socket.close();
		}
	}

	/** Transforms the received data into the appropriate object */
	abstract Object transformToObject(final byte[] buffer);
}