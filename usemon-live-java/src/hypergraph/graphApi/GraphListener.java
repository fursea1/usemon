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

import java.util.EventListener;

/**
 * An <code>EventListener</code> that listens to changes in a graph.
 *
 * @see Graph
 * @see GraphEvent
 * @author Jens Kanschik
 */
public interface GraphListener extends EventListener {
	/** Invoked when an element or a set of elements has been added to the graph.
	 * @param ge A <code>GraphEvent</code> that stores the actual event.
	 */
	public void elementsAdded(GraphEvent ge);
	/** Invoked when an element or a set of elements has been removed from the graph.
	 * @param ge A <code>GraphEvent</code> that stores the actual event.
	 */
	public void elementsRemoved(GraphEvent ge);
	/** Invoked when the structure has been changed without adding or removing single elements.
	 * @param ge A <code>GraphEvent</code> that stores the actual event.
	 */
	public void structureChanged(GraphEvent ge);
}
