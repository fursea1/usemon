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

/**
 * <p>
 * This class is the default implementation of the interface <code>Node</code>.
 * </p>
 * @author Jens Kanschik
 */
public class NodeImpl extends ElementImpl implements hypergraph.graphApi.Node {

	/** Stores the label of the node. Accessed via usual get/set method.*/
	private String label;

	/** Creates a new node with a unique name.
	 * @param name The unique name of the node.
	 */
    protected NodeImpl(String name) {
    	super(name);
    }

	/**
	 * Returns the label of the node. This is usually shown in a window.
	 * If the label has not been set, the name of the node is returned.
	 *
	 * @return The label of the node.
	 * @see #setLabel(String)
	 */
    public String getLabel() {
		if (label != null)
			return label;
		else
			return getName();
    }

     /**
     * Sets the label of the node.
     * @param l The label of the node.
     * @see #getLabel()
     */
    public void setLabel(String l) {
    	this.label = l;
    }
	/**
	 * Overwrites the abstract method inherited from <code>Element</code> to
	 * return <code>NODE_ELEMENT</code>.
	 * @return Always <code>NODE_ELEMENT</code>.
	 */
	public int getElementType() {
		return NODE_ELEMENT;
	}
	/** Creates a clone of the node.
	 * @return A copy of the node.
	 */
	public Object clone() {
		NodeImpl node = new NodeImpl(getName());
		node.setGroup(getGroup());
		node.setLabel(getLabel());
		return node;
	}
	/**
	 * @return A string representation of the node.
	 */
	public String toString() {
		return "[Node : " + getName() + " ]";
	}
}