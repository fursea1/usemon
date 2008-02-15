/**
 * 
 */
package org.usemon.domain;

/**
 * Holds all information about a single method, which is required to uniquely identify
 * it, including the package name etc.
 * @author t514257
 * 
 */
public class MethodDetail {
	private String packageName;
	private String className;
	private String methodName;
	private String signature;
	private String instanceId;

	public MethodDetail() {}

	/** Convenience constructor */
	public MethodDetail(String packageName, String className, String methodSignature){
		this(packageName,className, methodSignature, null);
	}

	/**
	 * @param packageName
	 * @param className
	 * @param methodSignature
	 * @param instanceId	object identiy
	 */
	public MethodDetail(String packageName, String className, String methodSignature, String instanceId){
		this.packageName = packageName;
		this.className = className;
		int indexOfLeftParenthesis = methodSignature.indexOf('(');
		if (indexOfLeftParenthesis < 0)
			throw new IllegalArgumentException("Methodsignature must contain \"()\"");
		
		this.methodName = methodSignature.substring(0, indexOfLeftParenthesis);
		this.signature = methodSignature.substring(indexOfLeftParenthesis);
		this.instanceId = instanceId;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

}
