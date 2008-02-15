/**
 * 
 */
package org.usemon.service.dao;

import java.util.List;

import org.usemon.domain.HeapObservation;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface HeapObservationDao {

	void addHeapObservations(List<HeapObservation> list);

}
