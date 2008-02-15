package org.usemon.domain.graph;


/**
 * Represents a generic Edge between two nodes.
 * <p>For our purposes an edge is always directed and represents
 * the invocation of a method on an object executed by a yet another
 * method on another object.
 *  
 * @author Paul-Rene Jørgensen
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class Edge implements GraphVisitable {

	private InstanceNode source;
	private InstanceNode target;

	public Edge() {
	}

	public Edge(InstanceNode source, InstanceNode target) {
		this();
		this.source = source;
		this.target = target;
	}

	public InstanceNode getSource() {
		return source;
	}

	public InstanceNode getTarget() {
		return target;
	}

	public Object accept(GraphVisitor visitor) {
		return visitor.visit(this);
	}

	public void setSource(InstanceNode source) {
		this.source = source;
	}

	public void setTarget(InstanceNode target) {
		this.target = target;
	}
}
