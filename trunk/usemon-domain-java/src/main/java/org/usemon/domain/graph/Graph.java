package org.usemon.domain.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph implements GraphVisitable{
	
    private List<InstanceNode> instanceNodes;
    private Set<Edge> edges;
    
    private long nodeIdCounter = 1;
    
    public Graph() {
        instanceNodes = new ArrayList<InstanceNode>();
        edges = new HashSet<Edge>();
    }
    
    /**
     * Adds another instance node to the graph, unless it already exists,
     * in which case nothing happens.
     * @param instanceNode references node to be added to graph
     * @return reference to the newly added node or the existing node
     */
    public InstanceNode addNode(InstanceNode instanceNode) {
    	InstanceNode nodeFoundOrAdded = instanceNode;
    	if (instanceNodes.contains(instanceNode)) {
    		int i = instanceNodes.indexOf(instanceNode);
    		nodeFoundOrAdded = instanceNodes.get(i);
    	} else {
    		// Bumps the unique node identification counter
    		// thus ensuring that all nodes have a unique id in this
    		// graph
    		instanceNode.setUid(nodeIdCounter++);
    		
    		instanceNodes.add(instanceNode);
    	}
    	return nodeFoundOrAdded;
    }

    /**
     * Adds another edge with associated source and target instanceNodes.
     * If either of the instanceNodes have not previously been added to the graph, they
     * will now be added.
     *  
     * @param edge
     */
    public void addEdge(Edge edge) {
    	// Replaces reference to the source node if that node already exists
    	edge.setSource(addNode(edge.getSource()));
    	// Replaces the reference to the target node if that node already exists
    	edge.setTarget(addNode(edge.getTarget()));
    	
        edges.add(edge);
    }

    public Collection<InstanceNode> getNodes() {
    	return instanceNodes;
    }
    public Collection<Edge> getEdges() {
        return edges;
    }

	public Object accept(GraphVisitor visitor) {
		return visitor.visit(this);
	}

}