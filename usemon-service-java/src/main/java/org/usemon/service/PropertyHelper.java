/**
 * 
 */
package org.usemon.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows properties to be configured in either:
 * <ol>
 * <li><code>usemon-default-properties</code> supplied with the software
 * <li><code>usemon.properties</code>, which you may place whereever you like in the classpath
 * <li>command line using the <code>-D</code> syntax.
 * 
 * @author Steinar Overbeck Cook
 * 
 */
public class PropertyHelper {

	public static final String JDBC_USER_PASSWORD = "jdbc.user.password";

	public static final String JDBC_USER_NAME = "jdbc.user.name";

	public static final String JDBC_URL_PROP_NAME = "jdbc.url";

	private static final Logger log = LoggerFactory.getLogger(PropertyHelper.class);

	private static final String USEMON_PROPERTIES = "usemon.properties";
	private static final String USEMON_DEFAULT_PROPERTIES = "usemon-default.properties";
	static Properties props = null;

	public static String getJdbcDriverClassName() {
		init();
		return getProperty("jdbc.driver.class");
	}

	public static Properties getAllPropertiesAsCopy() {
		return new Properties(props);
	}
	
	private static String getProperty(String propName) {
		init();
		// First we check our own .properties files before reverting to the
		// system
		// properties.
		String value = System.getProperty(propName, props.getProperty(propName));
		if (value == null)
			throw new IllegalStateException("Unable to determine value for property '" + propName + "'");
		return value;
	}

	private synchronized static void init() {
		if (props == null) {
			Properties defaultProps = loadPropsFromClassPath(USEMON_DEFAULT_PROPERTIES, null);
			props = loadPropsFromClassPath(USEMON_PROPERTIES, defaultProps);
		}
	}

	private static Properties loadPropsFromClassPath(String resourceFileName, Properties defaultProps) {
		InputStream streamWithDefaults = PropertyHelper.class.getClassLoader().getResourceAsStream(resourceFileName);
		if (streamWithDefaults == null && defaultProps == null)
			throw new IllegalStateException("Unable to locate " + resourceFileName + " in classpath");
		else {
			if (log.isDebugEnabled()) {
				if (streamWithDefaults == null) {
					log.debug("Unable to locate " + resourceFileName + " in class path");
				} else {
					URL url = PropertyHelper.class.getClassLoader().getResource(resourceFileName);
					log.debug("Loading properties from: " + url);
					if (defaultProps != null)
						log.debug("Properties:" + defaultProps);
				}
			}
		}
		Properties properties = null;
		if (defaultProps == null)
			properties = new Properties();
		else
			properties = new Properties(defaultProps);
		try {
			if (streamWithDefaults != null)
				properties.load(streamWithDefaults);
			return properties;
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load from resource " + resourceFileName);
		}
	}

	public static String getJdbcUrl() {
		return getProperty(PropertyHelper.JDBC_URL_PROP_NAME);
	}

	public static String getJdbcUserName() {
		return getProperty(PropertyHelper.JDBC_USER_NAME);
	}

	public static String getJdbcPassword() {
		return getProperty(PropertyHelper.JDBC_USER_PASSWORD);
	}

	public static int getCacheReportingInterval() {
		String v = getProperty("usemon.cahce.report.interval");
		return Integer.parseInt(v);
	}

	public static String getEhCacheConfig() {
		return getProperty("usemon.cache.ehcache.configfile");
	}

	public static int getJdbcIntialPoolSize() {
		String v = getProperty("jdbc.initial.pool.size");
		return Integer.parseInt(v);
	}
}
