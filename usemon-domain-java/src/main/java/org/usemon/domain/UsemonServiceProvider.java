/**
 * Created 15. nov.. 2007 13.34.41 by Steinar Overbeck Cook
 */
package org.usemon.domain;

/**
 * A usemon service implementation must provide the services listed below.
 * 
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface UsemonServiceProvider {

	/** Provides services related to storing observations and counting them */
	DeputoService deputoService();
	/** Provides services related to inspecting invocations */
	InvocationService invocationService();
	
	DimensionalQueryService dimensionalQueryService(); 
}
