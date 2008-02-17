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
 * This interface is the base for all elements of a graph, including the graph itself.
 * An element has a unique <code>name</code>, which is a read-only property.
 * This name has to be set at creation time by the graph to which the element belongs.
 * Any element can belong to a {@link hypergraph.graphApi.Group}.
 * Groups are usually used to group elements that have some common attribute.
 * <br />
 * An element must only be created using the corresponding creation methods of
 * a <code>Graph</code>, but never be instantiated directly.
 * For example call {@link hypergraph.graphApi.Graph#createNode()} to create a new node etc.
 * <br/>
 * To distinguish the different kinds of elements like edges and nodes,
 * each element has a method {@link #getElementType()}, which returns an integer that
 * indicates what kind of element it is. The values that are returned must be one of the
 * constants <code>*_ELEMENT</code> that are provided by this interface.
 * For example the <code>getElementType()</code> method of an edge always
 * returns <code>Element.EDGE_ELEMENT</code>, no matter which actual implementation
 * it is.
 */
public interface Element {

	/** Returned by <code>getElementType()</code> if the element is a graph. */
	public static final int GRAPH_ELEMENT	= 0;
	/** Returned by <code>getElementType()</code> if the element is a node. */
	public static final int NODE_ELEMENT	= 1;
	/** Returned by <code>getElementType()</code> if the element is a edge. */
	public static final int EDGE_ELEMENT	= 2;
	/** Returned by <code>getElementType()</code> if the element is a group. */
	public static final int GROUP_ELEMENT	= 3;

	/** Returns the group to which this element belongs (can be <code>null</code>).
	 * @return The group to which the element belongs.
	 */
	public Group getGroup();

	/** Sets the group of this element.
	 * @param group The group of the element.
	 */
	public void setGroup(Group group);

	/**
	 * The <code>name</code> of an element
	 * is a identifier for this element. This identifier has to be unique
	 * within a {@link GraphSystem}.
	 * It is set during instantiation and must never be changed.
	 * @return The unique name of this element.
	 */
	public String getName();

	/** Returns an integer that decodes the type of this element (node, edge etc).
	 * The values that this method returns must be one of the constants
	 * <code>Element.*_ELEMENT</code>.
	 * @return The type of this element.
	 */
	public int getElementType();

}
