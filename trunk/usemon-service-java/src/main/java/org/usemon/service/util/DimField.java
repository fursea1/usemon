package org.usemon.service.util;

import java.lang.reflect.Method;

import org.usemon.domain.Observation;

/**
 * Holds meta information for each bean property, which during insert should be translated into
 * the foreign key value of the corresponding dimension table
 */
public class DimField {

	private String beanPropertyName;
	private Method getterMethod;
	private Dimension dimension;
	private String dimensionalForeignKeyColName;
	

	/** Constructor
	 * @param dimension dimension for which we are creating a dimensional field meta data object
	 * @param beanPropertyName
	 * @param foreignKeyColName
	 */
	public DimField(Class<?> clazz, Dimension dimension, String beanPropertyName, String foreignKeyColName){
		this.beanPropertyName = beanPropertyName;
		this.dimension = dimension;
		dimensionalForeignKeyColName = foreignKeyColName;
		getterMethod = ReflectionHelper.findGetterMethod(beanPropertyName, clazz);
	}


	public String getBeanPropertyName() {
		return beanPropertyName;
	}

	public void setBeanPropertyName(String beanPropertyName) {
		this.beanPropertyName = beanPropertyName;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public Method getGetterMethod() {
		return getterMethod;
	}

	public void setGetterMethod(Method getterMethod) {
		this.getterMethod = getterMethod;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	/** Provides the name of the column in the fact table, holding the value
	 * of the foreign key for this dimension.
	 * @return
	 */
	public String getDimensionalForeignKeyColName() {
		return dimensionalForeignKeyColName;
	}


	/** Provides the transformed value for the dimensional field for the
	 * given observation
	 * @param observation observation holding the various facts and dimensional values
	 * @return transformed value of the dimension
	 */
	public Object getValue(Observation observation) {
		return dimension.getValue(getterMethod, observation);
	}
}