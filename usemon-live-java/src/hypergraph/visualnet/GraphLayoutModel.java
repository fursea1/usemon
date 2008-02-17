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

package hypergraph.visualnet;

import hypergraph.graphApi.Node;
import hypergraph.hyperbolic.ModelPoint;

/**
 * @author Jens Kanschik
 *
 */
public interface GraphLayoutModel {
	
	public void addLayoutEventListener(GraphLayoutListener gll);
	public void removeLayoutEventListener(GraphLayoutListener gll);
	public void fireLayoutChanged();
	
	/**
	 * Clears all information about node positions. This is called when a graph needs to be layouted.
	 * It shouldn't fire a GraphLayoutEvent, because this causes the graph
	 * to be repainted without a valid layout.
	 *
	 */
	public void clearNodePositions();

	public void setNodePosition(Node node,ModelPoint mp);
	public ModelPoint getNodePosition(Node node);
	
	/**
	 * Sets the property <code>valid</code>, which indicates whether the current layout is 
	 * still valid or not. Should only be called via the GraphLayoutManager.
	 */
	public void setValid(boolean valid);
	/**
	 * Returns <code>true</code> if the layout is still valid for the graph 
	 * and <code>false</code> when the graph layout needs to be redone.
	 * This property is some kind of read only, but there exists <code>invalidate()</code>
	 * which sets the property <code>valid</code> to <code>false</code>.
	 * After a successful layout, this property has to be set to true by <code>layout()</code>. 
	 */
	public boolean isValid();

}
