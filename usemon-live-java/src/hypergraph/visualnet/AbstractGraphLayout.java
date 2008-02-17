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
import hypergraph.graphApi.GraphEvent;
import hypergraph.hyperbolic.Model;
import hypergraph.hyperbolic.PropertyManager;

/**
 * @author Jens Kanschik
 *
 */
public abstract class AbstractGraphLayout implements GraphLayout {

	/** Stores the graph. */
	private Graph	graph;
	/** The GraphLayoutModel that stores the positions of the nodes. */
	private GraphLayoutModel graphLayoutModel;
	/** Stores the properties for this object. */
	private PropertyManager properties;
	/** Stores the model for the geometry. */
	private Model model;

	/** @inheritDoc */
	public void setGraph(Graph g) {
		if (graph != null)
			graph.removeGraphListener(this);
		graph = g;
		if (graph != null)
			graph.addGraphListener(this);
		invalidate();
	}

	/** @inheritDoc */
	public Model getModel() {
		return model;
	}

	/** @inheritDoc */
	public void setModel(Model model) {
		this.model = model;
	}

	/** @inheritDoc */
	public Graph getGraph() {
		return graph;
	}

	/** @inheritDoc */
	public void invalidate() {
		if (graphLayoutModel != null)
			graphLayoutModel.setValid(false);
	}

	/** @inheritDoc */
	public boolean isValid() {
		if (graphLayoutModel != null)
			return graphLayoutModel.isValid();
		else
			return false;
	}

	/** @inheritDoc */
	public void setGraphLayoutModel(GraphLayoutModel glm) {
		graphLayoutModel = glm;
	}
	/** @inheritDoc */
	public GraphLayoutModel getGraphLayoutModel() {
		return graphLayoutModel;
	}

	/** @inheritDoc */
	public void setProperties(PropertyManager props) {
		properties = props;
	}
	/** @return The properties that contain parameters for the layout algorithm.
	 */
	protected PropertyManager getProperties() {
		return properties;
	}

// Implementations for the GraphListener.
// Make sure that the layout is invalidated if something changes.

	/** Is called when an element is added to the graph.
	 * Invalidates the layout by calling <code>invalidate()</code>.
	 * @param ge The GraphEvent that represents the change of the graph's structure. */
	public void elementsAdded(GraphEvent ge) {
		invalidate();
	}
	/** Is called when an element is removed from the graph.
	 * Invalidates the layout by calling <code>invalidate()</code>.
	 * @param ge The GraphEvent that represents the change of the graph's structure. */
	public void elementsRemoved(GraphEvent ge) {
		invalidate();
	}
	/** Is called when the graph's structure has been changed.
	 * Invalidates the layout by calling <code>invalidate()</code>.
	 * @param ge The GraphEvent that represents the change of the graph's structure. */
	public void structureChanged(GraphEvent ge) {
		invalidate();
	}

}
