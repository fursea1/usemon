package org.usemon.service.dao;

import javax.sql.DataSource;

import org.usemon.service.util.Dimension;

public interface DimensionalDao {

	/**
	 * Creates a new entry in a dimension table
	 * @param dimension dimension for which a new entry should be created
	 * @param newEntry the value of the entry
	 * @return
	 */
	public int createDimensionalValue(Dimension dimension, Object newEntry);

	/**
	 * @param dimension
	 * @param lookupValue
	 * @return
	 */
	public int findForeginKeyInDbms(Dimension dimension, Object lookupValue);

	/** Property setter for data source */
	public void setDataSource(DataSource dataSource);

}