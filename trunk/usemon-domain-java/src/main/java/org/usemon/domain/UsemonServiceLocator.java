/**
 * 
 */
package org.usemon.domain;


/**
 * Locates, loads and instantiates the services provided by usemon. 
 * 
 * <p>TODO: In a future release, rather than hard coding the class name of the implementing class, the 
 * Java standard mechanism for services, which means looking up the name of the implementing class in 
 * the <code>META-INF/services</code>, should be used.</p> 
 * 
 * @author t547116
 * TODO: implement the proper META-INF/services stuff
 *
 */
public class UsemonServiceLocator implements UsemonServiceProvider {

	protected static final String ORG_USEMON_SERVICE_USEMON_SERVICE_PROVIDER_IMPL = "org.usemon.service.UsemonServiceProviderImpl";
	protected UsemonServiceProvider usemonServiceProvider = null;
	// Holds our singleton
	private static final UsemonServiceLocator _instance = new UsemonServiceLocator();

	// Constructor is private since this is a singleton
	private UsemonServiceLocator() {
		init();	// initializes the concrete implementation.
	}
	
	public static synchronized UsemonServiceLocator getInstance() {
		return _instance ;
	}
	
	// Initializes the object by establishing a Guice injector.
	protected synchronized void init() {
		// Dynamically loads the implementation which should reside in the service module
		// a.k.a. usemon-service-java
		usemonServiceProvider = (UsemonServiceProvider) loadAndCreate(ORG_USEMON_SERVICE_USEMON_SERVICE_PROVIDER_IMPL);
	}

	/** Obtains a reference to a {@link DeputoService} object by invoking
	 * the underlying implementation of the {@link UsemonServiceProvider}
	 */
	public DeputoService deputoService() {
		return usemonServiceProvider.deputoService();
	}

	/** Obtains a reference to a {@link DeputoService} object by invoking
	 * the underlying implementation of the {@link InvocationService}
	 */
	public InvocationService invocationService() {
		return usemonServiceProvider.invocationService();
	}
	
	/**
	 * Instantiates the default implementation of the {@link DeputoService} interface, and injects
	 * the required reference to a data source.
	 * @return instance of DeputoService
	 */
	public static DeputoService getDeputoService() {
		return getInstance().deputoService();
	}
	
	/**
	 * Instantiates the default implementation of the {@link InvocationService}, ready injected 
	 * with a data source and everything.
	 * 
	 * @return instance of InvocationService
	 */
	public static InvocationService getInvocationService() {
		return getInstance().invocationService();
	}

	/**
	 * Helper method for loading and instantiating a fully named class.
	 * 
	 * @param className fully classified class name
	 * @return reference to instance.
	 */
	private static Object loadAndCreate(String className) {
		Class<?> clazz = loadClass(className);
		return instantiate(clazz);
	}
	
	/** Creates an object instance of the given type
	 * 
	 * @param clazz Class reference
	 * @return reference to object instance.
	 */
	private static Object instantiate(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalStateException("Unable to instantiate object of type " + clazz.getName() +"; "+ e.getMessage(),e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	/** Loads the byte stream for a given class
	 * 
	 * @param className fully qualified class name
	 * @return Class instance 
	 */
	private static Class<?> loadClass(String className) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Class " + className + " not found in classpath" + e.getMessage(),e);
		}
		return clazz;
	}

	/* (non-Javadoc)
	 * @see org.usemon.domain.UsemonServiceProvider#dimensionalQueryService()
	 */
	public DimensionalQueryService dimensionalQueryService() {
		return usemonServiceProvider.dimensionalQueryService();
	}
	
	/**
	 * Instantiates the default implementation of the {@link InvocationService}, ready injected 
	 * with a data source and everything.
	 * 
	 * @return instance of InvocationService
	 */
	public static DimensionalQueryService getDimensionalQueryService() {
		return getInstance().dimensionalQueryService();
	}
}
