/**
 * 
 */
package org.usemon.service;

import org.usemon.service.util.Dimension;


/** Services related to caching and management of dimensional data.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface DimensionCacheManager {

	/** Locates the primary key to for a dimensional value.
	 * 
	 * @return primary key of row with matching value
	 */ 
	int getForeignKeyForValue(Dimension dimension, Object lookupValue);
	int getCacheHits();
	int getRequests();
	void clearStatistics();
}
