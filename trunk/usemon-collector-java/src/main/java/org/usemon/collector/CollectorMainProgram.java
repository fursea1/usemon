package org.usemon.collector;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.slf4j.LoggerFactory;
import org.usemon.domain.DeputoService;
import org.usemon.domain.HeapObservation;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodObservation;
import org.usemon.domain.UsemonServiceLocator;
import org.usemon.multicast.JavaMulticastClient;
import org.usemon.multicast.JsonMulticastClient;
import org.usemon.multicast.MulticastConfig;
import org.usemon.service.dao.TestDataGenerator;
import org.usemon.usageinfo.Usage;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Receives multicasted {@link Usage} objects, decodes and transforms them after which they are shoved into the DBMS.
 * 
 * <p>
 * This implementation is multi threaded. There are several multi cast receiver threads which will receive the data and places them
 * onto a main input queue, which the main thread will read in order to transform and persist the observations.
 * </p>
 * 
 * @author Paul Rene Jørgensen
 * @author t547116 - Steinar Overbeck Cook
 * 
 */
public class CollectorMainProgram {

	private static final String LOGBACK_CONFIG_PROPERTY = "org.usemon.logback.configfile";

	/**
	 * Default Constructor Initializes queues, sets up multicast configuration by creating the multicast client objects which will
	 * write their received data into the queue held in an instance of this class.
	 * 
	 * @throws IOException
	 */
	public CollectorMainProgram() throws Exception {
	}

	/** Executable stand alone program entry point */
	public static void main(String[] args) throws Exception {
		initializeLogging();

		if (args.length > 0 && args[0].equals("help")) {
			printHelp();
		} else {
			startMulticastClientsAndCollector();
		}
	}

	/**
	 * Initializes the collector and multi cast clients and starts the threads.
	 * 
	 * @throws
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	private static void startMulticastClientsAndCollector() {
		Collector collector = new Collector();
		// Injects the deputo service
		DeputoService deputoService = UsemonServiceLocator.getDeputoService();
		collector.setDeputoService(deputoService);

		// Creates the multicast clients, injecting a reference to the collector
		JavaMulticastClient javaMulticastClient = new JavaMulticastClient(MulticastConfig.multicastUsageHost, MulticastConfig.multicastUsagePort, collector);
		JsonMulticastClient jsonMulticastClient = new JsonMulticastClient(MulticastConfig.multicastJSONHost, MulticastConfig.multicastJSONPort, collector);

		// Starts the threads
		javaMulticastClient.startThread();
		jsonMulticastClient.startThread();
		
		Thread.currentThread().setName("Collector main");
		collector.run();	// runs the collector in this thread
	}

	/**
	 * Initializes the logger sub system.
	 */
	private static void initializeLogging() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		String logBackConfigFile = System.getProperty(LOGBACK_CONFIG_PROPERTY);
		if (logBackConfigFile != null) {
			try {
				System.out.println("Configuring logback with: " + logBackConfigFile);
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(lc);
				lc.shutdownAndReset();
				configurator.doConfigure(logBackConfigFile);
			} catch (JoranException je) {
				StatusPrinter.print(lc);
			}
		} else {
			System.out.println("Configuring logback with default configuration");
			System.out.println("You may override by setting system property: " + LOGBACK_CONFIG_PROPERTY);
			System.out.println("to point to your configuration");
		}
	}

	/**
	 * Prints examples for different JSON objects
	 */
	private static void printHelp() {
		InvocationObservation originalInvocation = TestDataGenerator.generateInvocationObservation();
		JSONObject jsonInvocation = JSONObject.fromBean(originalInvocation);
		System.out.println("Accepting JSON messages in the following format:");
		System.out.println(Collector.PREFIX_INVOCATION_OBSERVATION + Collector.TOKENIZER + jsonInvocation.toString(2));

		MethodObservation methodObservation = TestDataGenerator.generateMethodObservation();
		JSONObject jsonMethod = JSONObject.fromBean(methodObservation);
		System.out.println("");
		System.out.println(Collector.PREFIX_METHOD_OBSERVATION + Collector.TOKENIZER + jsonMethod.toString(2));

		HeapObservation heapObservation = TestDataGenerator.generateHeapObservation();
		JSONObject jsonHeap = JSONObject.fromBean(heapObservation);
		System.out.println("");
		System.out.println(Collector.PREFIX_HEAP_OBSERVATION + Collector.TOKENIZER + jsonHeap.toString(2));

		System.out.println("Note that all messages should be prefixed with the Object type it represents");
	}
}