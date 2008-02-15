/**
 * 
 */
package org.usemon.domain;

import java.util.Date;
import java.util.List;


/** Services provided by Deputo for the purpose of storing observations into
 * persistent storage (DBMS). 
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface DeputoService {

	/** Persists a single observation */
	public void addObservation(Observation observation);

	/** Bulk addition of {@link MethodObservation} objects. */
	public void addMethodObservations(List<MethodObservation> methodObservations);
	/** Bulk addition of {@link InvocationObservation} objects */
	public void addInvocationObservations(List<InvocationObservation> invocationObservationList);
	/** Bulk addition of {@link HeapObservation} objects into persistent storage */
	public void addHeapObservations(List<HeapObservation> list);
	
	/** Number of hits in the cache when looking for dimension entries */
	public int getCacheHits();
	/** The ratio of cache hits to the number of insert requests */
	public float getCacheHitRatio();

	/** Retrieves the time stamp of the latest MethodObservation */
	public Date getLastMethodObservation();
}
