/**
 * 
 */
package org.usemon.domain;


/**
 * Represents the observation of the invocation of a method on object of a given class being executed
 * by another object of a given class. Instances of this type are typically created by analyzing a 
 * call stack.
 * 
 * @author t514257
 *
 */
public class InvocationObservation extends AbstractObservation {
	
	private MethodDetail sourceMethod;
	private MethodDetail targetMethod;
	int	invocationCount;
	
	public int getInvocationCount() {
		return invocationCount;
	}

	public void setInvocationCount(int invocationCount) {
		this.invocationCount = invocationCount;
	}

	public InvocationObservation() {

	}

	/** Convenience constructor */
	public InvocationObservation(MethodDetail sourceMethod, MethodDetail targetMethod) {
		this.sourceMethod= sourceMethod;
		this.targetMethod = targetMethod;
	}
	
	/** {@inheritDoc}
	 * 
	 * @see org.usemon.domain.Observation#getTypeName()
	 */
	public String getTypeName() {
		return ObservationType.METHOD_INVOCATION.name();
	}


	public MethodDetail getSourceMethod() {
		return sourceMethod;
	}

	public void setSourceMethod(MethodDetail sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	public MethodDetail getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(MethodDetail targetMethod) {
		this.targetMethod = targetMethod;
	}

	public String getTargetPackageName() {
		return targetMethod.getPackageName();
	}
	
	public String getTargetClassName() {
		return targetMethod.getClassName();
	}
	
	public String getTargetMethodName() {
		return targetMethod.getMethodName();
	}
	
	public String getTargetInstanceId() {
		return targetMethod.getInstanceId();
	}
	
	public String getSrcPackageName() {
		return sourceMethod.getPackageName();
	}
	public String getSrcClassName() {
		return sourceMethod.getClassName();
	}
	public String getSrcMethodName() {
		return sourceMethod.getMethodName();
	}
	public String getSrcInstanceId() {
		return sourceMethod.getInstanceId();
	}
}
