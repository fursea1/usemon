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

import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphListener;
import hypergraph.hyperbolic.Model;
import hypergraph.hyperbolic.PropertyManager;

/**
 * This interface has to be implemented by each graph layout algorithm.
 * @author Jens Kanschik
 */
public interface GraphLayout extends GraphListener {

	/** Sets the graph that has to be laid out.
	 * The graph layout has to be invalidated by an implementation of this method,
	 * such that the graph is laid out.
	 * @param graph The new graph that has to be laid out.
	 */
	public void setGraph(Graph graph);

	/** Returns the graph the GraphLayout works with.
	 * @return The graph the layout works with.
	 */
	public Graph getGraph();

	/** Returns the model of the geometry in which the graph has to be laid out.
	 * @return The model of the geometry that is used for the layout.
	 */
	public Model getModel();
	
	/** Sets the model of geometry in which the graph has to be laid out.
	 * The whole layout has to be redone when the model changes. 
	 * @param model The geometric model in which the graph is laid out. 
	 */
	public void setModel(Model model);

	/**
	 * This method has to be called when the graph layout is not valid anymore.
	 * This happens for example when important settings have been changed,
	 * the graph has changed in such a way that the layout has to be done from scratch etc.
	 * Usually this method doesn't relayout the graph; instead it sets a flag
	 * which can be checked via <code>isValid()</code>. If the layout is needed, e.g.
	 * to draw the graph, this should be checked and the graph layout should be done.
	 * <br />
	 * Passes directly to the <code>GraphLayoutModel</code>.
	 */
	public void invalidate();

	/**
	 * Returns <code>true</code> if the layout is still valid for the graph
	 * and <code>false</code> when the graph layout needs to be redone.
	 * This property is some kind of read only, but there exists <code>invalidate()</code>
	 * which sets the property <code>valid</code> to <code>false</code>.
	 * After a successful layout, this property has to be set to true by <code>layout()</code>.
	 * <br />
	 * Passes directly to the <code>GraphLayoutModel</code>.
	 * @return <code>True</code> if the graph layout is valid.
	 */
	public boolean isValid();

	/**
	 * Does the layout for the graph.
	 * Before the method returns to the caller, the property <code>valid</code> has to be set
	 * to <code>true</code> to indicate that the graph layout is now valid.
	 *
	 */
	public void layout();

	/** Sets the <code>GraphLayoutModel</code> that stores the node positions that are
	 * computed by this layout algorithm.
	 * @param glm An implementation of <code>GraphLayoutModel</code>
	 */
	public void setGraphLayoutModel(GraphLayoutModel glm);

	/** Returns the <code>GraphLayoutModel</code> that stores the node positions that are
	 * computed by this layout algorithm.
	 * @return The <code>GraphLayoutModel</code>
	 */
	public GraphLayoutModel getGraphLayoutModel();

	/** Passes a {@link hypergraph.hyperbolic.PropertyManager} object to the layout algorithm. This object might
	 * contain some special parameters for the layout algorithm.
	 * @param properties Contains properties for the algorithm.
	 */
	public void setProperties(PropertyManager properties);

	/**
	 * Returns <code>true</code> if the layout algorithm allows expanding and shrinking of subtrees.
	 * This depends on the algorithm, but also on settings (property visualnet.layout.useExpander).
	 * @return  <code>True</code> if expanding is allowed and enabled.
	 */
	public boolean isExpandingEnabled();

}
