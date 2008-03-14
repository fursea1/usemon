package org.usemon.usageinfo;

/**
 * Holds static information which pertains to every method call.
 * 
 * @author Paul Rene Jørgensen
 *
 */
public class MethodInfo extends Info{
	private static final long serialVersionUID = -2334112916099623586L;

	private String simpleClassName;
	private String packageName;
	private String methodName;
	private String signature;
	private String returnType;

	public MethodInfo() {
	}
	public MethodInfo(int componentType, String packageName, String simpleClassName, String methodName, String signature) {
		super(componentType);
		this.simpleClassName = simpleClassName;
		this.packageName = packageName;
		this.methodName = methodName;
		this.signature = signature;
	}

	public String getReturnType() {
		return returnType;
	}

	// Needed, because not set in constructor (and not part of hashCode)
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getSimpleClassName() {
		return simpleClassName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getSignature() {
		return signature;
	}

	public String getKey() {
		return createKey(packageName, simpleClassName, methodName, signature);
	}
	
	public static String createKey(String packageName, String simpleClassName, String methodName, String signature) {
		return new StringBuffer(packageName).append(".").append(simpleClassName).append(".").append(methodName).append(signature).toString();
	}

	public void setSimpleClassName(String simpleClassName) {
		this.simpleClassName = simpleClassName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String toString() {
		return "["+TYPES[componentType]+"] "+packageName+"."+simpleClassName+"."+methodName;
	}
}