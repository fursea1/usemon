/**
 * 
 */
package org.usemon.domain;

import java.util.Date;

/** JavaBean holding the properties for a observation of the invocation 
 * of a given method on a given class.
 * 
 * @author t547116
 *
 */

public class MethodObservation extends AbstractObservation {

	private String channel;
	private String principal;
	
	private MethodDetail methodDetail; 
	
	// Facts
	int	invocationCount;
	int maxResponseTime;
	int avgResponseTime;
	int checkedExceptions;
	int uncheckedExceptions;
	
	/**
	 * Default Constructor
	 */
	public MethodObservation() {

	}	
	/** Convenience constructor which allows for easy creation of a new instance with most of the
	 * dimensions set in one string. The syntax of the string is:
	 * <pre>
	 *  package.Class#method()
	 * </pre>
	 * @param string
	 */
	public MethodObservation(String packageName, String className, String methodName) {
		methodDetail = new MethodDetail(packageName, className, methodName);
	}


	public String getPackageName() {
		return methodDetail.getPackageName();
	}


	public String getClassName() {
		return methodDetail.getClassName();
	}


	public String getMethodName() {
		return methodDetail.getMethodName();
	}


	public String getChannel() {
		return channel;
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}


	public String getPrincipal() {
		return principal;
	}


	public void setPrincipal(String principal) {
		this.principal = principal;
	}


	public int getInvocationCount() {
		return invocationCount;
	}


	public void setInvocationCount(int invocationCount) {
		this.invocationCount = invocationCount;
	}


	public int getMaxResponseTime() {
		return maxResponseTime;
	}


	public void setMaxResponseTime(int maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}


	public int getAvgResponseTime() {
		return avgResponseTime;
	}


	public void setAvgResponseTime(int avgResponseTime) {
		this.avgResponseTime = avgResponseTime;
	}


	public int getCheckedExceptions() {
		return checkedExceptions;
	}


	public void setCheckedExceptions(int checkedExceptions) {
		this.checkedExceptions = checkedExceptions;
	}


	public int getUncheckedExceptions() {
		return uncheckedExceptions;
	}


	public void setUncheckedExceptions(int uncheckedExceptions) {
		this.uncheckedExceptions = uncheckedExceptions;
	}


	public String getTypeName() {
		return ObservationType.METHOD_OBSERVATION.name();
	}
	
	public MethodDetail getMethodDetail() {
		return methodDetail;
	}
	public void setMethodDetail(MethodDetail methodDetail) {
		this.methodDetail = methodDetail;
	}

	
}
