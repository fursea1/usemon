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
package hypergraph.graphApi.algorithms;

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LayerAssignment {

	/** The name of the graph attribute that stores the layer information */
	public static final String LAYER = "layer";

	/** Indicates whether dummy nodes have to be created or not. */
	private boolean createDummy;

	/** Stores the class which is used to store the layer information.
	 * This class has to implement Layer. It can be set via setLayerClass(Class).
	 */
	private Class layerImpl;

	/** The graph for which a layer has to be computed. */
	private Graph graph;

	/** Sets the class of the layer implementation.
	 * This class is used to store the layer information.
	 * The class parameter has to be an instance of Layer.
	 * If it isn't, the layer class is not changed.
	 * @param limpl The specified implementation of the Layer interface
	 * that is used to store the layer information.
	 */
	public void setLayerClass(Class limpl) {
		if (limpl.isAssignableFrom(Layer.class))
			layerImpl = limpl;
	}

	/** Sets the property <code>creatDummy</code>.
	 * If this property is <code>true</code>,
	 * the algorithms creates dummy node so that the resulting layer is proper.
	 * @param b The property value.
	 */
	public void setCreateDummy(boolean b) {
		createDummy = b;
	}

	/**Sets the graph for which a layer has to be computed.
	 * The graph has to be acyclic, otherwise the algorithm will loop infinitely.
	 * @param g The graph.
	 */
	public void setGraph(Graph g) {
		graph = g;
	}

	/** Responsible for the assignment of layers.
	 * When the layers are assigned,
	 * the algorithm may or may not introduce dummy vertices,
	 * depending on the algorithm and on the property <code>createDummy</code>.
	 * If dummy vertices are introduced, they are added to the graph
	 * and a reference is stored in the parameter dummyNode.
	 * The old long edges (i.e. those spanning dummy nodes) are removed
	 * from the graph and stored in the parameter longEdges.
	 * With these two sets it is possible to restore the graph later on (if necessary).
	 * </br>
	 * The layer information is stored in implementations of the interface Layer
	 * which are assigned to each node (original and dummy node)
	 * using the AttributeManager of the graph.
	 * @param dummyNodes A set in which the dummy nodes are stored.
	 * May be null, in this case the information which nodes in the graph
	 * are dummy nodes and which aren't is lost.
	 * @param longEdges A set that stores a reference to each of the long edges that are
	 * replaced by dummy nodes and dummy edges.
	 */
	public void assignLayers(Set dummyNodes, Set longEdges) {
		AttributeManager attrMgr = graph.getAttributeManager();
		List topOrder = GraphUtilities.getTopologicalOrdering(graph);
		if (topOrder.size() != graph.getNodes().size()) {
			GraphUtilities.makeAcyclic(graph);
			topOrder = GraphUtilities.getTopologicalOrdering(graph);
		}
		for (Iterator nodes = topOrder.iterator(); nodes.hasNext();) {
			Node node = (Node) nodes.next();
			long layer = 0;
			for (Iterator edges = graph.getEdges(node).iterator(); edges.hasNext();) {
				Edge edge = (Edge) edges.next();
				if (edge.getTarget().equals(node))
					layer = Math.max(layer,
								((Layer) attrMgr.getAttribute(LAYER, edge.getSource())).getLayer());
			}
			attrMgr.setAttribute(LAYER, node, createLayer(layer + 1));
			node.setLabel(node.getLabel() + " layer = " + (layer + 1));
		}
		if (createDummy) {
			if (longEdges == null)
				longEdges = new HashSet();
			Collection oldEdges = new ArrayList();
			oldEdges.addAll(graph.getEdges());
			for (Iterator edges = oldEdges.iterator(); edges.hasNext();) {
				Edge edge = (Edge) edges.next();
				long sourceLayer = ((Layer) attrMgr.getAttribute(LAYER, edge.getSource())).getLayer();
				long targetLayer = ((Layer) attrMgr.getAttribute(LAYER, edge.getTarget())).getLayer();
				if (targetLayer > sourceLayer + 1) {
					longEdges.add(edge);
					Node prevNode = edge.getSource();
					for (long layer = sourceLayer + 1; layer < targetLayer; layer++) {
						Node node = graph.createNode();
						attrMgr.setAttribute(LAYER, node, createLayer(layer));
						graph.createEdge(prevNode, node);
						prevNode = node;
						if (dummyNodes != null)
							dummyNodes.add(node);
					}
					graph.createEdge(prevNode, edge.getTarget());
				}
			}
			for (Iterator edges = longEdges.iterator(); edges.hasNext();)
				graph.removeElement((Edge) edges.next());
		}
	}

	/** Utility method to create a new layer object with the given layer.
	 * @param value The number of the layer.
	 * @return An instance of Layer as defined by the property <code>LayerClass</code>.
	 */
	private Layer createLayer(long value) {
		Layer layerObj;
		try {
			layerObj = (Layer) layerImpl.newInstance();
		} catch (Exception exception) {
			layerObj = new LayerImpl();
		}
		layerObj.setLayer(value);
		return layerObj;
	}


	public interface Layer {
		public long getLayer();
		public void setLayer(long layer);
	}
	private class LayerImpl implements Layer {
		private long layer;
		public void setLayer(long l) {
			layer = l;
		}
		public long getLayer() {
			return layer;
		}
	}

}
