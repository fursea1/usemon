/**
 * 
 */
package org.usemon.service.util;

import java.lang.reflect.Method;

import org.usemon.domain.MethodObservation;

/** Holds meta data information for a fact field 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class FactField {
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public Method getGetterMethod() {
		return getterMethod;
	}
	public void setGetterMethod(Method getterMethod) {
		this.getterMethod = getterMethod;
	}
	public FactField(Class<?> clazz, String colName, String propertyName) {
		this.colName = colName;
		this.propertyName = propertyName;
		getterMethod = ReflectionHelper.findGetterMethod(propertyName, clazz);
	}
	
	String colName;
	String propertyName;
	Method getterMethod;
}