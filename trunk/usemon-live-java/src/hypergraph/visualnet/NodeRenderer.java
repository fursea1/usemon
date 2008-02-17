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
import hypergraph.hyperbolic.Renderer;

/**
 * @author Jens Kanschik
 */
public interface NodeRenderer extends Renderer {

	/** Configures the node renderer.
	 * This method is called for each node prior to painting the node.
	 * It is responsible to make all preparations for the painting
	 * like mapping the node's position from the model coordinates to screen coordinates and
	 * determining the text of the node.
	 * @param gp The GraphPanel to which the node is painted.
	 * @param mp The model coordinate of the node.
	 * @param node The node that will be painted.
	 */
	public void configure(GraphPanel gp, ModelPoint mp, Node node);

}
