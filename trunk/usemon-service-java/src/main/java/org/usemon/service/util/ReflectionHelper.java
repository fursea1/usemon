/**
 * 
 */
package org.usemon.service.util;

import java.lang.reflect.Method;

/**
 * Helpfull methods for reflection.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class ReflectionHelper {

	/** Locates the getter method for a given property on a given class */
	public static Method findGetterMethod(String beanPropName, Class<?> clazz) {
		// Locates the property getter method for the property
		String methodName = null;
		try {
			methodName = "get" + beanPropName.substring(0, 1).toUpperCase() + beanPropName.substring(1);
			return clazz.getMethod(methodName);
		} catch (SecurityException e) {
			throw new IllegalStateException("Security problem obtaining method for " + methodName + "() for property " + beanPropName);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Unable to locate method name " + methodName + " for property " + beanPropName);
		}
	}
}
