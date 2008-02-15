package org.usemon.usageinfo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.usemon.utils.SingleLinkedNode;

/**
 * Holds statistical usage information per unique method.
 * 
 * @author Paul Rene Jørgensen.
 *
 */
public class Usage implements Serializable, SingleLinkedNode {
	private static final long serialVersionUID = -1145538062952066334L;
	
	private Info info;		// Holds static meta data like for instance name of method.
	private Date expirationTime;
	private Date createTime;
	// All method invocations are grouped by the principal performing the method call
	private Map principalUsage; 
	private Map invokees; // Info objects
	private boolean movedToTransport;
	private String location;

	private long freeMemory;
	private long totalMemory;
	private long maxMemory;

	transient private SingleLinkedNode next;
	
	public Usage(Info info) {
		this.principalUsage = new HashMap();
		this.invokees = new HashMap();
		this.movedToTransport = false;
		this.createTime = new Date();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.SECOND, 59);
		this.expirationTime = c.getTime();
		this.freeMemory = Runtime.getRuntime().freeMemory();
		this.totalMemory = Runtime.getRuntime().totalMemory();
		this.maxMemory = Runtime.getRuntime().maxMemory();
		this.location = generateLocation();
		this.info = info;
	}
	
	public String toString() {
		StringBuffer o = new StringBuffer();
		o.append("\n### BEGIN "+super.toString()+"\n");
		o.append("Location: "+location+"\n");
		o.append("Info    : "+info.toString()+"\n");
		o.append("Created : "+createTime+"\n");
		o.append("Expired : "+expirationTime+"\n");
		o.append("Usage   : "+principalUsage+"\n");
		o.append("Invokees: "+invokees+"\n");
		o.append("### END "+super.toString());
		return o.toString();
	}

	private String generateLocation() {
		StringBuffer l = new StringBuffer();
	    // TODO: Document system properties
	    l.append(System.getProperty("usemon.system.id", "unknownSystem")).append(".");
	    l.append(System.getProperty("usemon.cluster.id", "unknownCluster")).append(".");
	    l.append(System.getProperty("usemon.server.id", "unknownServer"));
	    return l.toString();
	}

	public boolean isExpired() {
		return new Date().after(expirationTime);
	}

	/**
	 * Adds another observation sample, in this case that means yet another method invocation
	 * @param principal the principal performing the method invocation
	 * @param responseTime elapsed time for the method invocation in milliseconds
	 * @param exception exception thrown, optional
	 */
	public void addSample(String principal, long responseTime, Throwable exception) {
		PrincipalUsage pu = null;
		// All invocations are grouped by principal
		if(principalUsage.containsKey(principal)) {
			// Find existing
			pu = (PrincipalUsage) principalUsage.get(principal);
		} else {
			// creates a new one
			pu = new PrincipalUsage(principal);
			principalUsage.put(principal, pu);
		}
		pu.addSample(responseTime, exception);
	}

	public void addInvokee(Info info) {
		String key = info.getKey();
		if(invokees.containsKey(key)) {
			((Info) invokees.get(key)).incCount();
		} else {
			info.incCount();
			invokees.put(key, info);
		}
	}
	
	public boolean isMovedToTransport() {
		return movedToTransport;
	}

	public void setMovedToTransport(boolean movedToTransport) {
		this.movedToTransport = movedToTransport;
	}
	
	public String getLocation() {
		return location;
	}

	public Info getInfo() {
		return info;
	}

	public String getKey() {
		return info.getKey();
	}
	
	public Map getPrincipalUsage() {
		return principalUsage;
	}

	public Map getInvokees() {
		return invokees;
	}

	public void setPrincipalUsage(Map principalUsage) {
		this.principalUsage = principalUsage;
	}

	public void setInvokees(Map invokees) {
		this.invokees = invokees;
	}

	public void setInfo(Info info) {
		this.info = info;
	}
	
	public Date getExpirationTime() {
		return expirationTime;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}
	
	public long getPeriodLengthInMillis() {
		return expirationTime.getTime() - createTime.getTime();
	}

	public SingleLinkedNode getNext() {
		return next;
	}

	public void setNext(SingleLinkedNode singleLinkedNode) {
		next = singleLinkedNode;
	}
}
