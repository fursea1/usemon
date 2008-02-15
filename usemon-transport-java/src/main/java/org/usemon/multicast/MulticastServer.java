package org.usemon.multicast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServer {
	private static final int MAX_PACKAGE_SIZE = 1024*64; // 64k
	private static final String CHARSET = "ISO-8859-1";
	
	private InetAddress address;
	private MulticastSocket socket;
	private int port;

	public MulticastServer(String multicastAddress, int multicastPort) throws IOException {
		this.port = multicastPort;
		address = InetAddress.getByName(multicastAddress);
		socket = new MulticastSocket();
		socket.joinGroup(address);
		socket.setTimeToLive(16);
	}
	
	public void send(String message) throws IOException {
        try {
            byte[] byteMessage = message.getBytes(CHARSET);
			send(byteMessage, 0, byteMessage.length);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
		}
	}
	
	public void sendObject(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();
		baos.close();
		byte[] buf = baos.toByteArray();
		send(buf, 0, buf.length);
	}
	
	
	public void send(byte[] bytes, int offset, int length) throws IOException {
        if(bytes.length>MAX_PACKAGE_SIZE) {
            throw new IllegalArgumentException("Max message size is "+MAX_PACKAGE_SIZE+" bytes.");
        }
        DatagramPacket pkg = new DatagramPacket(bytes, offset, length, address, port);
        socket.send(pkg);
	}

	public void close() {
		if(!socket.isClosed()) {
			socket.close();
		}
	}
}