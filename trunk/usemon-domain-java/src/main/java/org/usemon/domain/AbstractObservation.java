/**
 * 
 */
package org.usemon.domain;

import java.util.Date;

/**
 * Holds the default dimensional data which applies to
 * all observations.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * @version $LastChangedRevision$
 * 
 *
 */
public abstract class AbstractObservation implements Observation {


	protected J2eeLocation j2eeLocation;
	protected Date timeStamp;
	protected Long periodLength;

	/** {@inheritDoc}
	 * @see org.usemon.domain.Observation#getLocation()
	 */
	public J2eeLocation getLocation() {
		return j2eeLocation;
	}

	/** {@inheritDoc}
	 * @see org.usemon.domain.Observation#getPeriodLength()
	 */
	public Long getPeriodLength() {
		return periodLength;
	}

	/** {@inheritDoc}
	 * @see org.usemon.domain.Observation#getTimeStamp()
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	public J2eeLocation getObservationLocation() {
		return j2eeLocation;
	}

	public void setObservationLocation(J2eeLocation j2eeLocation) {
		this.j2eeLocation = j2eeLocation;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setPeriodLength(Long periodLength) {
		this.periodLength = periodLength;
	}

}
