package com.usemon.agent.publisher;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.usemon.multicast.MulticastServer;
import org.usemon.usageinfo.Usage;
import org.usemon.utils.Log;

import com.usemon.agent.Config;

/**
 * Publishes {@link Usage} objects via multi cast. The objects to be published are placed into an internal queue by other threads
 * invoking the {@link #enqueue(Usage)} method.
 * 
 * @author Paul Rene Jørgensen
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class Publisher implements Runnable {

	private static Publisher instance;
	private Thread thread;
	private boolean running;
	private MulticastServer sender;
	private List queue;
	private boolean tightLoop;

	private Publisher() throws IOException {
		queue = Collections.synchronizedList(new LinkedList());
		sender = new MulticastServer(Config.getMulticastAddress(), Config.getMulticastPort());
		thread = new Thread(this, "Usemon Publisher Thread (id:" + System.currentTimeMillis() + ")");
		thread.setDaemon(true);
		thread.start();
		Log.info("Usemon Publisher created!");
	}

	public synchronized static Publisher getInstance() throws IOException {
		if (instance == null) {
			instance = new Publisher();
		}
		return instance;
	}

	public boolean isRunning() {
		return running;
	}

	public void run() {
		running = true;
		tightLoop = false;
		Log.info("Usemon publisher started");
		int sendCounter = 0;
		while (true) {
			try {
				long time = System.currentTimeMillis();
				while (!queue.isEmpty()) {
					if (System.currentTimeMillis() - time > 1000) {
						Log.debug("In tight (1000 ms) loop - Queue depth: " + queue.size() + ". Have sent " + sendCounter + ". Free mem:" + freememory());
						time = System.currentTimeMillis();
						tightLoop = true;
					}
					// Removes first entry
					SoftReference softReference = (SoftReference)(queue.remove(0));
					sender.sendObject(softReference.get());
					sendCounter++;
				}
				if (tightLoop) {
					tightLoop = false;
					Log.debug("Exited tight loop");
				}
				Log.debug("Sleep 15 seconds. Have sent " + sendCounter + " object so far, size of sender queue is now " + queue.size());
				Thread.sleep(1000 * 15);
			} catch (InterruptedException e) {
				Log.info("Usemon publisher received InterruptedException and is shutting down");
				// Exit the loop and allow the thread to die
				break;
			} catch (IOException e) {
				Log.info("Unable to send message: " + e.getMessage());
				break;
			} catch (Throwable t) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				t.printStackTrace(pw);
				Log.error(sw.toString());
				Log.error("ERROR:" + t.getMessage() + ". " + sendCounter + " messages sent");
				break;
			}
		}
		Log.warning("Usemon publisher died");
		running = false;
	}

	private long freememory() {
		Runtime r = Runtime.getRuntime();
		
		return r.maxMemory() - r.totalMemory() + r.freeMemory();
	}

	/**
	 * Places a {@link Usage} object on the queue of objects to be published. This method is invoked by other threads
	 */
	public boolean enqueue(Usage usage) {
		if (running) {
			queue.add(new SoftReference(usage));
			return true;
		} else {
			Log.error("PUBLISHER STOPPED! USEMON WILL (HOPEFULLY) BE INACTIVATED!");
			return false;
		}
	}
}
