/**
 * 
 */
package org.usemon.service.dao;

import java.util.Date;
import java.util.List;

import org.usemon.domain.MethodObservation;

/** Data Access Object for working with the "fact" table method_measurement_fact table.
 * 
 * @author t547116
 *
 */
public interface MethodObservationDao {

	public void add(MethodObservation methodObservation);
	public void addMethodObservations(List<MethodObservation> observationList);

	public long getInsertCounter();
	public long getElapsedTime();
	public void clearStatistics();
	public Date getLastMethodObservation();
}
