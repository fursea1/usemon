/**
 * 
 */
package org.usemon.domain;


/** Represents the location at which an observation was been made within a J2ee environment consisting of 
 * <i>platforms</i> with many <i>clusters</i> with 
 * many <i>servers</i>.
 * <p>A <i>locationName</i> may be encoded like so:
 * <code>
 * <i>platform</i>.<i>cluster</i>.<i>server</i>
 * </code> 
 * <p>The following rules apply when specifying an incomplete location:
 * <table border="1">
 * <tr>
 * <th>
 * given location
 * </th>
 * <th>
 * platform
 * </th>
 * <th>
 * cluster
 * </th>
 * <th>
 * server
 * </th>
 * </tr>
 * <tr>
 * <td>
 * <i>xxxxx</i>
 * </td>
 * <td>
 * default
 * </td>
 * <td>
 * default
 * </td>
 * <td>
 * xxxxx
 * </td>
 * </tr>
 * <tr>
 * <td>
 * <i>xxxxx.yyyyy</i>
 * </td>
 * <td>
 * default
 * </td>
 * <td>
 * xxxxx
 * </td>
 * <td>
 * yyyyy
 * </td>
 * </tr>
 * <tr>
 * <td>
 * <i>xxxxx.yyyyy.zzzzz</i>
 * </td>
 * <td>
 * xxxxx
 * </td>
 * <td>
 * yyyyy
 * </td>
 * <td>
 * zzzzz
 * </td>
 * </tr>
 * </table
 * @author t547116
 * @version $Revision$
 * 
 */
public class J2eeLocation implements ObservationLocation {
	static final String DEFAULT = "default";
	String platformName;
	String clusterName;
	String serverName;

	/** Constructor which requires the location components to be set
	 * via the various property setters.
	 */
	public J2eeLocation() {
	}

	public J2eeLocation(String platformName, String clusterName, String serverName) {
		this.platformName = platformName;
		this.clusterName = clusterName;
		this.serverName = serverName;
	}
	
	public J2eeLocation(String locationName) {
		setLocationName(locationName);
	}

	/**
	 * Property getter for <code>locationName</code>, which is simply a string 
	 * representation of the name of the location at which an observation was made.
	 * @return
	 */
	public String getLocationName() {
		return platformName + "."  + clusterName + "." + serverName;
	}

	
	/**
	 * Parses the location name, which should consist of a platform, cluster and 
	 * server separated by '.'
	 */
	public void setLocationName(String locationName) {
		if (locationName == null)
			throw new IllegalArgumentException("locationName can not be null");
		String[] components = locationName.split("\\.");
		switch(components.length) {
		case 1:	// only one component, so this is deemed to be the server name
			setPlatformName(DEFAULT);
			setClusterName(DEFAULT);
			setServerName(components[0]);
			break;
		case 2:	// two components, so first is cluster and second is server name
			setPlatformName(DEFAULT);
			setClusterName(components[0]);
			setServerName(components[1]);
			break;
		case 3:	// All three components are given
			setPlatformName(components[0]);
			setClusterName(components[1]);
			setServerName(components[2]);
			break;
		default:
			throw new IllegalArgumentException("Invalid locationName, only 2 '.' is allowed: " + locationName);
		}
	}
	
	@Override
	public String toString() {
		return getLocationName();
	}

	

	/** Implementation of <code>{@link #hashCode()}</code> which uses all the 3 fields
	 * which this location consists of.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clusterName == null) ? 0 : clusterName.hashCode());
		result = prime * result + ((platformName == null) ? 0 : platformName.hashCode());
		result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
		return result;
	}

	/** Implementation which will compare all three fields of the location. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof J2eeLocation))
			return false;
		final J2eeLocation other = (J2eeLocation) obj;
		if (clusterName == null) {
			if (other.clusterName != null)
				return false;
		} else if (!clusterName.equals(other.clusterName))
			return false;
		if (platformName == null) {
			if (other.platformName != null)
				return false;
		} else if (!platformName.equals(other.platformName))
			return false;
		if (serverName == null) {
			if (other.serverName != null)
				return false;
		} else if (!serverName.equals(other.serverName))
			return false;
		return true;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
