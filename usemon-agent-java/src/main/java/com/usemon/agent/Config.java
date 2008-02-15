package com.usemon.agent;

import java.io.InputStream;
import java.util.Properties;

import org.usemon.utils.Log;

public class Config {
	private static Properties props;

	private static boolean sessionBeanInScope;
	private static boolean entityBeanInScope;
	private static boolean servletInScope;
	private static boolean messagedrivenBeanInScope;
	private static boolean singletonInScope;
	private static boolean messageProducerInScope;
	private static boolean sqlStatementInScope;
	private static boolean customInScope;

	private static String[] ignoredClasses;
	private static String[] ignoredMethods;
	private static String[] customClasses;
	
	private static String multicastAddress;
	private static int multicastPort;
	
	static {
		reload();
	}

	public static void main(String[] opts) {
		Config.reload();
	}
	
	public static void reload() {
		props = new Properties();
		try {
			InputStream in = Config.class.getResourceAsStream(Constants.USEMON_AGENT_PROPERTIES);
			props.load(in);
		} catch (Throwable e) {
			Log.warning("Could not load "+Constants.USEMON_AGENT_PROPERTIES+" through classpath. Using default values!");
		}
		populateConfigFromPropertiesOrSetDefaults();
	}
	
	private static void populateConfigFromPropertiesOrSetDefaults() {
		sessionBeanInScope = getBooleanProperty("usemon.scope.sessionbean", true);
		entityBeanInScope = getBooleanProperty("usemon.scope.sessionbean", true);
		messagedrivenBeanInScope = getBooleanProperty("usemon.scope.entitybean", true);
		servletInScope = getBooleanProperty("usemon.scope.servlet", true);
		singletonInScope = getBooleanProperty("usemon.scope.singleton", false);
		messageProducerInScope = getBooleanProperty("usemon.scope.messageproducer", true);
		sqlStatementInScope = getBooleanProperty("usemon.scope.sqlstatement", true);
		customInScope = getBooleanProperty("usemon.scope.custom", true);
		ignoredClasses = getStringArrayProperty("usemon.ignore.class", new String[0]);
		ignoredMethods = getStringArrayProperty("usemon.ignore.method", new String[0]);
		customClasses = getStringArrayProperty("usemon.custom.class", new String[0]);
		// TODO: Add default multicast address and port
		multicastAddress = getStringProperty("usemon.multicast.address", "224.82.199.166");
		multicastPort = getIntProperty("usemon.multicast.port", 16200);
	}

	private static int getIntProperty(String key, int defaultValue) {
		if(props!=null) {
			String strValue = props.getProperty(key);
			if(strValue!=null) {
				try {
					return Integer.parseInt(strValue);
				} catch(NumberFormatException e) {
					// Ignore and return default
				}
			}
		}
		return defaultValue;
	}

	private static String getStringProperty(String key, String defaultValue) {
		if(props!=null) {
			String value = props.getProperty(key);
			if(value!=null) {
				return value;				
			}
		}
		return defaultValue;
	}

	private static String[] getStringArrayProperty(String key, String[] defaultValue) {
		if(props!=null) {
			String value = props.getProperty(key);
			if(value!=null) {
				// Remove whitespace (spaces and tabs)
				value = value.replaceAll(" ", "").replaceAll("\t", "");
				return value.split(",");
				
			}
		}
		return defaultValue;
	}

	private static boolean getBooleanProperty(String key, boolean defaultValue) {
		if(props!=null) {
			return new Boolean(props.getProperty(key, defaultValue?"true":"false")).booleanValue();
//			return Boolean.parseBoolean(props.getProperty(key, defaultValue?"true":"false"));
		} else {
			return defaultValue;
		}
	}

	public static String[] getIgnoredClasses() {
		return ignoredClasses;
	}

	public static String[] getIgnoredMethods() {
		return ignoredMethods;
	}
	
	public static String[] getCustomClasses() {
		return customClasses;
	}

	public static boolean isCustomInScope() {
		return customInScope;
	}

	public static boolean isEntityBeanInScope() {
		return entityBeanInScope;
	}

	public static boolean isMessagedrivenBeanInScope() {
		return messagedrivenBeanInScope;
	}

	public static boolean isMessageProducerInScope() {
		return messageProducerInScope;
	}

	public static boolean isServletInScope() {
		return servletInScope;
	}

	public static boolean isSessionBeanInScope() {
		return sessionBeanInScope;
	}

	public static boolean isSingletonInScope() {
		return singletonInScope;
	}

	public static boolean isSQLStatementInScope() {
		return sqlStatementInScope;
	}

	public static String getMulticastAddress() {
		return multicastAddress;
	}

	public static int getMulticastPort() {
		return multicastPort;
	}
}
