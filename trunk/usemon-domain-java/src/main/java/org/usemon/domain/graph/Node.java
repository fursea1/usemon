package org.usemon.domain.graph;



/** General representation of a node in a graph.
 * <p>May be used as the basis for specific node types like for instance
 * objects, classes, packages etc.
 * </p>
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public abstract class Node implements GraphVisitable {
	
    // Unique identification of the node, may be modified
	private Long uid;

    public Node() {
    }
    
    public Node(Long uid) {
        this();
        this.uid = uid;
    }
    
    public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getUid() {
        return uid;
    }

	public Object accept(GraphVisitor visitor) {
		return visitor.visit(this);
	}

}