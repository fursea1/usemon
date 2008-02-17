/*
 *  Copyright (C) 2003  Jens Kanschik,
 * 	mail : jensKanschik@users.sourceforge.net
 *
 *  Part of <hypergraph>, an open source project at sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package hypergraph.graph;

import hypergraph.graphApi.Node;

/**
 * This class represents is a straight forward implementation
 * of the interface <code>Edge</code>.
 * @author Jens Kanschik
 */
public class EdgeImpl extends ElementImpl implements hypergraph.graphApi.Edge {

	/** The source of the edge */
    private Node source;
    /** The target of the edge. */
    private Node target;
    /** The label of the edge. */
    private String label;
    /** The indicator whether the edge is directed. */
    private boolean isDirected;

	/** Instantiates a new edge with a given name, source and target.
	 * This is a protected constructor because an edge must never be instantiated
	 * directly.
	 * To create an edge in a graph,
	 * call {@link hypergraph.graphApi.Graph#createEdge(Node, Node)}.
	 * @param source The source of the edge.
	 * @param target The target of the edge.
	 * @param name The name (or unique identifier) of the edge.
	 */
    protected EdgeImpl(String name, Node source, Node target) {
    	super(name);
		this.source = source;
		this.target = target;
		this.label = null;
		this.isDirected = false;
    }
	/** {@inheritDoc} */
    public Node getSource() { return source; }
	/** {@inheritDoc} */
    public Node getTarget() { return target; }
	/** {@inheritDoc} */
    public void setLabel(String label) { this.label = label; }
	/** {@inheritDoc} */
    public String getLabel() { return label; }

	/**
	 * Determines the "other" node than the passed node.
	 * If a node is passed that is not incident to the edge, <code>null</code> is returned.
	 * @param node One of the two nodes.
	 * @return The other of the two nodes or <code>null</code>
	 */
    public Node getOtherNode(Node node) {
		if (node.equals(source))
	    	return target;
		if (node.equals(target))
		    return source;
		return null;
    }
    /** {@inheritDoc} */
    public void reverse() {
    	Node temp = target;
    	target = source;
    	source = temp;
    }
    /** {@inheritDoc} */
    public void setDirected(boolean directed) {
    	isDirected = directed;
    }
    /** {@inheritDoc} */
    public boolean isDirected() {
    	return isDirected;
    }
	/**
	 * Returns <code>EDGE_ELEMENT</code>.
	 * @return Always <code>EDGE_ELEMENT</code>.
	 */
	public int getElementType() {
		return EDGE_ELEMENT;
	}

	/** Returns a String representation of the edge.
	 * Usually only useful for debugging purpose.
	 * @return A String representation of the edge.
	 */
    public String toString() {
    	return
			"[ Edge : \n"
		  + "  name   : " + getName() + "\n"
		  +	"  source : " + source + "\n"
    	  +	"  target : " + target + " ]\n";
    }
}
