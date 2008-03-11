package com.usemon.agent.registry;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

import org.usemon.usageinfo.Info;
import org.usemon.usageinfo.MethodInfo;
import org.usemon.usageinfo.ResourceInfo;
import org.usemon.usageinfo.Usage;
import org.usemon.utils.Log;

import com.usemon.agent.Constants;
import com.usemon.agent.publisher.Publisher;

/**
 * Registers and holds usage observation objects and ships them over to the publisher once approx. every minute.
 * <p>
 * The static methods are invoked by the code injected into the public methods of the classes instrumented by us.
 * <p>
 * The data is collected in our thread local objects for a minute, after which they are transferred to the {@link Publisher} queue.
 * 
 * @author Paul Rene Jørgensen
 * @author Steinar Overbeck Cook
 * 
 */
public class Registry {

	/**
	 * Creates {@link Usage} objects when requested to do so. Allows for lazy creation Usage objects through the use of a "closure".
	 * 
	 * @author t547116 (Steinar Overbeck Cook)
	 */
	public interface UsageCreator {
		Usage createNewUsageObject();
	}

	// Contains Stack
	private static ThreadLocal localCallStack = new ThreadLocal();
	// Holds our usage objects which have not been transferred to the publisher queue
	private static ThreadLocal localUsageCollection = new ThreadLocal();
	public static Publisher publisher;
	private static boolean publisherFailed = false;

	/** Provides a lazy initialized thread local Usage collection */
	private static UsageCollection getUsageCollection() {
		if (localUsageCollection.get() == null) {
			localUsageCollection.set(new UsageCollectionMapImpl());
		}
		return (UsageCollection) localUsageCollection.get();
	}

	/**
	 * Provides the size of the usage map, i.e. the map holding our current usage objects.
	 * 
	 * @return size of usage map.
	 */
	public static int getUsageCollectionSize() {
		return getUsageCollection().size();
	}

	// TODO: Methods called infrequently are not sent before the next call. Need
	// another thread that checks usageMap and transport them when expired.
	public static void reportTransformation(int componentType, String className, String method, String signature, String modifiers) {
	}

	/**
	 * Registers an invocation of a method, which is accumulated on per-method basis into a {@link Usage} object.
	 * 
	 * @param componentType
	 * @param packageName
	 *            the package which the class belongs to
	 * @param simpleClassName
	 *            name of the class
	 * @param methodName
	 *            name of the method
	 * @param signature
	 *            method signature
	 * @param modifiers
	 *            modifiers of the method, i.e. "public", "protected" etc.
	 * @param responseTime
	 *            response time in milliseconds
	 * @param principal
	 *            principal performing the invocation
	 * @param exceptionThrownByThisInvocation
	 *            exception thrown by the method invocation
	 */
	public static void invocation(int componentType, String packageName, String simpleClassName, String methodName, String signature, String modifiers, long responseTime, String principal, Throwable exceptionThrownByThisInvocation) {
		if (!okToPublish())
			return;
		principal = ensurePrincipal(principal); // Sanity check of the principal
		// Locates the usage object
		Usage u = ensureUsage(componentType, packageName, simpleClassName, methodName, signature);
		Info invokee = getMyInvokee();
		if (invokee != null) {
			u.addInvokee(invokee);
		}
		u.addSample(principal, responseTime, exceptionThrownByThisInvocation);
//		System.err.println(packageName+"."+simpleClassName+"."+methodName+" "+signature);
//		System.err.println(u.getInvokees());
	}

	/**
	 * Registers an invocation of a resource object, i.e. SQL, JMS queue etc.
	 * 
	 * @param componentType
	 * @param className
	 *            complete class name with package name etc., i.e. java.net.URL
	 * @param resource
	 *            instanceId, which could be the SQL statement, the name of queue, the URL etc.
	 * @param responseTime
	 * @param principal
	 * @param exception
	 */
	public static void invocation(int componentType, String className, String resource, long responseTime, String principal, Throwable exception) {
		if (!okToPublish())
			return;
		principal = ensurePrincipal(principal);
		Usage u = ensureUsage(componentType, className, resource);
		Info invokee = getMyInvokee();
		if (invokee != null) {
			u.addInvokee(invokee);
		}
		u.addSample(principal, responseTime, exception);
	}

	public static void push(int componentType, String className, String resource) {
//		System.out.println(componentType+": "+className+" - "+resource);
		if (!okToPublish())
			return;
		Stack s = ensureCallStack();
		s.push(new ResourceInfo(componentType, className, resource));
	}

	public static void push(int componentType, String packageName, String simpleClassName, String methodName, String signature) {
		if (!okToPublish())
			return;
		Stack s = ensureCallStack();
		s.push(new MethodInfo(componentType, packageName, simpleClassName, methodName, signature));
	}

	public static void pop() {
		if (!okToPublish())
			return;
		Stack s = (Stack) localCallStack.get();
		if (s != null) {
			try {
				s.pop();
			} catch (EmptyStackException e) {
				// IGNORE: Just in case the push/pops don't add up
			}
		}
	}

	private static boolean okToPublish() {
		if (publisherFailed) {
			if (localUsageCollection != null) {
				localUsageCollection = new ThreadLocal();
			}
			return false;
		}
		return true;
	}

	private static Stack ensureCallStack() {
		Stack myStack = (Stack) localCallStack.get();
		if (myStack == null) {
			myStack = new Stack();
			localCallStack.set(myStack);
		}
		return myStack;
	}

	private static Info getMyInvokee() {
		Info info = null;
		Stack s = ensureCallStack();
		if (s.size() > 1) {
			info = (Info) s.elementAt(s.size() - 2);
		}
		return info;
	}

	/**
	 * Locates the {@link Usage} associated with the given parameters, which represents method invocations.
	 * 
	 * @param componentType
	 * @param packageName
	 * @param simpleClassName
	 * @param methodName
	 * @param signature
	 * @return reference to an existing Usage object or a newly created Usage object if an existing one could not be found.
	 */
	protected static Usage ensureUsage(final int componentType, final String packageName, final String simpleClassName, final String methodName, final String signature) {
		ensurePublisherAvailable();
		// Composes the hash map key
		String key = MethodInfo.createKey(packageName, simpleClassName, methodName, signature);

		// logic for locating, publishing and creating a new usage object has
		// been
		// placed into a convenience method.
		Usage usage = findOrCreateCurrentUsageObject(key, new UsageCreator() {
			/**
			 * Implements the Usage creator method for the enclosing method signature
			 */
			public Usage createNewUsageObject() {
				return new Usage(new MethodInfo(componentType, packageName, simpleClassName, methodName, signature));
			}
		});

		return usage;
	}

	/**
	 * Locates the {@link Usage} object associated with the given parameters which represents an object instance.
	 * <p>
	 * Expired usage objects will be transferred to the publisher, after which a new usage object is created and returned.
	 * 
	 * @param componentType
	 * @param className
	 * @param resource
	 * @return
	 */
	private static Usage ensureUsage(final int componentType, final String className, final String resource) {
		ensurePublisherAvailable();
		// Composes the hash map key
		String key = ResourceInfo.createKey(resource);
		// logic for locating, publishing and creating a new usage object has
		// been placed into a convenience method.
		Usage usage = findOrCreateCurrentUsageObject(key, new UsageCreator() {
			public Usage createNewUsageObject() {
				return new Usage(new ResourceInfo(componentType, className, resource));
			}
		});

		return usage;
	}

	/**
	 * Convenience method for finding the Usage object in our thread local map, creating it if it does not exist and optionally shipping it off to the publisher.
	 * <p>
	 * The {@link UsageCreator} argument allows for lazy creation of the Usage object, that is, we do not need to create it if the Usage object for the key was found in our thread local map.
	 * </p>
	 * 
	 * @param key
	 *            the text representation of the key for the Usage object type
	 * @param usageCreator
	 *            a closure which will generate the correct type of Usage object
	 * @return reference to the current Usage object to be used.
	 */
	private static Usage findOrCreateCurrentUsageObject(final String key, final UsageCreator usageCreator) {
		Usage usage = null;
		// Checks the existence of the usage key
		if (!getUsageCollection().containsKey(key)) {
			// usage key does not exist, creates it and saves it.
			usage = createAndSaveNewUsageObject(usageCreator);
		} else {
			// Retrieves existing usage object from the map
			usage = (Usage) getUsageCollection().get(key);
			// Expired usages objects needs to be evicted from our local map and
			// transferred to
			// the publisher queue.
			if (usage.isExpired()) {
				removeAndSendExpiredUsageObjects(usage);
				usage = createAndSaveNewUsageObject(usageCreator);
			}
		}
		return usage;
	}

	/**
	 * Creates new usage object and saves it in our internal storage structure.
	 * 
	 * @param key
	 *            represents the key under which the new usage object should be stored.
	 * @param usageCreator
	 *            closure for creating a new usage object.
	 * @return newly created usage object.
	 */
	private static Usage createAndSaveNewUsageObject(final UsageCreator usageCreator) {
		Usage usage;
		usage = usageCreator.createNewUsageObject();
		getUsageCollection().put(usage);
		return usage;
	}

	/**
	 * checks to see if the publisher is available and clears the outbound queue if not.
	 * 
	 * @return <em>true</em> if publisher is available, clears the queue and returns <em>false</em> otherwise.
	 */
	private static boolean isPublisherUnavailableAndUsageCollectionCleared() {
		if (!okToPublish()) {
			Log.warning("Publisher not initialized or have failed, Usage statistics are lost");
			getUsageCollection().clear();
			return true;
		} else
			return false;
	}

	/**
	 * Sends and removes all usage objects created prior to the supplied one, after which the supplied usage object is removed from the out bound queue and sent for publishing.
	 * 
	 * @param key
	 *            represents the key into the out bound queue
	 * @param usage
	 *            the object to be enqueued.
	 */
	private static void removeAndSendExpiredUsageObjects(final Usage usage) {
		if (isPublisherUnavailableAndUsageCollectionCleared())
			return;
		Log.debug("Number of items in Registry queue:" + getUsageCollectionSize());
		int i = 0;
		for (Iterator iter = getUsageCollection().iterator(usage); iter.hasNext();) {
			Usage u = (Usage) iter.next();
			if (u != null) {
				sendUsageObject(u);
				i++;
				iter.remove();
			}
		}
		Log.debug("Transferred " + i + " usage objects to the publisher");
	}

	/**
	 * Enqueues the supplied Usage object to the publisher.
	 * 
	 * @param usage
	 *            object to be enqueued
	 */
	private static void sendUsageObject(final Usage usage) {
		// Publishes the usage object!
		if (!publisherFailed && !publisher.enqueue(usage))
			publisherFailed = true;
	}

	private static String ensurePrincipal(final String principal) {
		return principal != null ? principal : Constants.DEFAULT_PRINCIPAL_NAME;
	}

	/**
	 * Ensures that we have an instance of a publisher, which will broadcast our observations to the collector or anyone that cares to listen :-)
	 */
	public static void ensurePublisherAvailable() {
		// Start the publisher if it does not exist or is not running
		if (publisher == null) {
			try {
				publisher = Publisher.getInstance();
			} catch (IOException e) {
				Log.error("Publisher has failed. I'll try to clean up my data structures!");
				publisherFailed = true;
			}
		}
	}
}
