/**
 * Created 15. nov.. 2007 17.01.31 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class InvocationEdge extends Edge {

	
	private long invocationCount;

	/**
	 * @param source
	 * @param target
	 */
	public InvocationEdge(long invocationCount, InstanceNode source, InstanceNode target) {
		super(source, target);
		this.invocationCount = invocationCount;
	}

	public long getInvocationCount() {
		return invocationCount;
	}

	public void setInvocationCount(long invocationCount) {
		this.invocationCount = invocationCount;
	}

}
