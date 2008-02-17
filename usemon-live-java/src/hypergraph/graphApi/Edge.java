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

package hypergraph.graphApi;

/**
 * Represents an edge within a graph.
 * As an element of a graph, it extends {@link hypergraph.graphApi.Element}
 * and has a unique identifier, the <code>name</code>.
 * <br />
 * Technically, each edge is a directed edge, i.e. it has a source node and a target node;
 * both are read-only properties of the edge;
 * however, an edge might be considered to be directed
 * (source and target having the obvious meanings), or it may not considered to be directed.
 * In the latter case, the properties source and target must not be considered to be special,
 * the difference is only made for technical simplicity,
 * the nodes could have been called node1 and node2 as well.
 * Being directed or not is controlled with the boolean property <code>directed</code>.
 * <br />
 * An implementation of this interface must not provide a public constructor;
 * the construction of an edge must be done by the graph using
 * {@link hypergraph.graphApi.Graph#createEdge(Node, Node)}
 * The only two methods that change the edge are {@link #setLabel(String)}
 * to change the label and {@link #reverse()} to exchange source and target.
 * This ensures that the graph structure can not be corrupted.
 *
 * @see Node
 * @see Graph
 * @author Jens Kanschik
 */
public interface Edge extends Element {

	/**
	 * Returns the source of the edge.
	 * @return The source of the edge.
	 */
	public Node getSource();

	/**
	 * Returns the target of the edge.
	 * @return The target of the edge.
	 */
	public Node getTarget();

	/** Sets the label of the edge.
	 * @param label The label of the edge.
	 */
	public void setLabel(String label);
	/** Gets the label of the edge.
	 * @return The label of the edge.
	 */
    public String getLabel();

    /**
     * Determines the "other" node than the passed node.
     * If a node is passed that is not incident to the edge,
     * <code>null</code> is returned.
     * @param node One of the two nodes.
     * @return The other of the two nodes or <code>null</code>
     */
    public Node getOtherNode(Node node);

	/** Reverses the edge, i.e. exchanges source and target. */
	public void reverse();
	
	/** Sets the directed property.
	 * @param directed Boolean, that indicates whether this edge is directed or not. */
	void setDirected(boolean directed);
	
	/** Returns <code>true</code> if the edge has to be considered as directed.
	 * this case the properties source and target have their obvious meanings.
	 * If this property is <code>false</code>, the edge is not directed and the properties
	 * source and target are only used to differentiate between the two nodes, none of them
	 * has a special role.
	 */
	boolean isDirected();
}