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
 * This interface represents a node within the graph.
 * Apart of the general properties that are inherited from {@link Element},
 * a <code>Node</code> has a label that is used by graph visualisations and usually contains
 * some information about the node respectively the object or notion the
 * node represents. The label can be an arbitrary text.
 * <br/>
 * Any implementation of this interface must have only non-public constructors.
 * A node must only be created using the corresponding creation methods of the <code>Graph</code>.
 *
 * @author Jens Kanschik
 */
public interface Node extends Element {

	/** Returns the label of the node.
	 * The label must not be mixed up with the name of a node.
	 * The name is used to identify an element and hence has to be unique
	 * and is usually not known to the user,
	 * while the label is usually shown in the visualisation and can be an
	 * arbitrary text.
	 * @return The label of the node.
	 */
	public String getLabel();

	/** Sets the label of a node.
	 * @see #getLabel()
	 * @param label The new label of the node.
	 */
	void setLabel(String label);

}