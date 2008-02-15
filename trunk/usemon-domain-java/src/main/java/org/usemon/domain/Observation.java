/**
 * 
 */
package org.usemon.domain;

import java.util.Date;

/**
 * Represents an observation of one or more single valued properties at a given point in time. Example of an observation would be:
 * <ul>
 * <li>invocation of a method on an object
 * <li>current status of the JVM
 * <li>Air (temperature, humidity etc.)
 * </ul>
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public interface Observation {
	/** Provides the name of this type of observation. I.e. "methodInvocation" */
	String getTypeName();

	/** Time stamp when observation was made */
	Date getTimeStamp();
	
	void setTimeStamp(Date timeStamp);
	/** Length of observation period in milliseconds */
	Long getPeriodLength();
	
	/** Location at which observation was made */
	J2eeLocation getLocation();
}