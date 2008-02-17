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

import hypergraph.graphApi.Edge;
import hypergraph.hyperbolic.Renderer;

/**
 * This interface defines methods that have to be implemented
 * by a renderer that is used to paint edges. Since an edge is
 * naturally rendered as a line, the EdgeRenderer is based on the interface
 * {@link hypergraph.hyperbolic.LineRenderer}
 *
 * @author Jens Kanschik
 */
public interface EdgeRenderer extends Renderer {

	/** Configures the edge renderer.
	 * This method is called for each edge prior to painting the edge.
	 * It is responsible to make all preparations for the painting
	 * like computation of the end points.
	 * @param mp The GraphPanel to which the edge is painted.
	 * @param edge The edge that will be painted.
	 */
	public void configure(GraphPanel mp, Edge edge);

	/** Sets the visibility of the label.
	 * If it's set to <code>true</code>, the label is shown between the two nodes,
	 * otherwise the label of the edge is not shown.
	 * @param vis Indicates, whether the label is shown or not.
	 */
	public void setLabelVisible(boolean vis);

	/** Gets the visibilty of the label.
	 * If it's set to <code>true</code>, the label is shown between the two nodes,
	 * otherwise the label of the edge is not shown.
	 * @return <code>true</code> if the label is shown.
	 */
	public boolean isLabelVisible();

}
