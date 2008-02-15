/**
 * 
 */
package org.usemon.domain;


/**
 * @author t514257
 *
 */
public interface DimensionalQueryService {
	/** Performs a multi dimensional query based upon the input parameters
	 * @param queryObject describes the multi dimensional query to be executed
	 * @return multi dimensional result set
	 */
	public UsemonQueryResult fetchData(UsemonQueryObject queryObject);

}
