/**
 * 
 */
package org.usemon.service.dao;

import java.util.List;

import org.usemon.domain.InvocationObservation;

/**
 * Data Access Object services for managing persistent {@link InvocationObservation} objects.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface InvocationObservationDao {

	public void add(InvocationObservation invocationObservation);

	/** Inserts a bunch of {@link InvocationObservation} objects */
	void addInvocationObservations(List<InvocationObservation> invocationObservationList);

	public long getInsertCounter();
	public long getElapsedTime();
	public void clearStatistics();

}
