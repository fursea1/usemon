package org.usemon.usageinfo;

import java.io.Serializable;

/**
 * Statistics for a method invocation per principal.
 * This object is held in the {@link Usage} object in order to track
 * statistics on a per principal basis.
 * 
 * @author Paul Rene Jørgensen
 *
 */
public class PrincipalUsage implements Serializable{
	private static final long serialVersionUID = -6345756420152598433L;
	
	private String principal;
	private long lastExecutionTime;
	private long totalExecutionTime;
	private long minExecutionTime;
	private long maxExecutionTime;
	private int executionCount;
	private int uncheckedCount;
	private int checkedCount;
	
	public PrincipalUsage() {
		this(null);
	}
	
	public PrincipalUsage(String principal) {
		this.principal = principal;
		this.minExecutionTime = Long.MAX_VALUE;
		this.maxExecutionTime = Long.MIN_VALUE;
	}
	
	/** Adds another sample of method invocation for the principal associated with this object */
	public void addSample(long time, Throwable exception) {
		lastExecutionTime = System.currentTimeMillis();
		totalExecutionTime+=time;
		if(minExecutionTime>time) minExecutionTime = time;
		if(maxExecutionTime<time) maxExecutionTime = time;
		if(exception!=null) {
			if(exception instanceof RuntimeException) uncheckedCount++; else checkedCount++;
		}
		executionCount++;
	}
	
	public String toString() {
		return "{"+principal+"} "+lastExecutionTime+"¤"+totalExecutionTime+"¤"+minExecutionTime+"¤"+maxExecutionTime+"¤"+executionCount+"¤"+uncheckedCount+"¤"+checkedCount;
	}

	public String getPrincipal() {
		return principal;
	}

	public long getLastExecutionTime() {
		return lastExecutionTime;
	}

	public long getTotalExecutionTime() {
		return totalExecutionTime;
	}

	public long getMinExecutionTime() {
		return minExecutionTime;
	}

	public long getMaxExecutionTime() {
		return maxExecutionTime;
	}

	public int getExecutionCount() {
		return executionCount;
	}

	public int getUncheckedCount() {
		return uncheckedCount;
	}

	public int getCheckedCount() {
		return checkedCount;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public void setLastExecutionTime(long lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
	}

	public void setTotalExecutionTime(long totalExecutionTime) {
		this.totalExecutionTime = totalExecutionTime;
	}

	public void setMinExecutionTime(long minExecutionTime) {
		this.minExecutionTime = minExecutionTime;
	}

	public void setMaxExecutionTime(long maxExecutionTime) {
		this.maxExecutionTime = maxExecutionTime;
	}

	public void setExecutionCount(int executionCount) {
		this.executionCount = executionCount;
	}

	public void setUncheckedCount(int uncheckedCount) {
		this.uncheckedCount = uncheckedCount;
	}

	public void setCheckedCount(int checkedCount) {
		this.checkedCount = checkedCount;
	}

}
