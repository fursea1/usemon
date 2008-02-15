/**
 * 
 */
package org.usemon.domain;

/** Represents the location at which an observation has been made. You may extend this class
 * to your liking to includes things like "cluster", "server", etc.
 * 
 * @author t547116
 * @version $Revision$
 * 
 */
public interface ObservationLocation {
	/**
	 * Property getter for <code>locationName</code>, which is simply a string 
	 * representation of the name of the location at which an observation was made.
	 * @return
	 */
	public String getLocationName();
	/**
	 * Parses the given location name into an implementation specific representation.
	 * 
	 * @param locationName stringified encoding of a location
	 * @see J2eeLocation
	 */
	public void setLocationName(String locationName) ;
}
