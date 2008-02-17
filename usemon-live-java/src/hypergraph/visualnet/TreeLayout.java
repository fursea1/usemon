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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import hypergraph.graphApi.*;
import hypergraph.hyperbolic.*;

/**
 * @author Jens Kanschik
 *
 */
public class TreeLayout extends AbstractGraphLayout {

	/**
	 * distance is the distance from the parent node to the current node;
	 * angle is the angle under which the current node is seen from the parent node
	 */
	protected class NodeProperties {
		double distance;
		double angle1; // angle under which the node is seen			
		double angle2; // angle under which the node's children are seen
		public String toString() {
			return "[ TreeLayout.NodeProperties : \n " +
					"angle1    = " + angle1 + " \n" +
					"angle2    = " + angle2 + " \n" +
					"distance    = " + distance + "]\n";
		}
	};

	private double maxAngle;
	private double minDistance;
	private double defaultSize;
	private double rootAngle = Math.PI;
	Map		properties;
	Node	root;
	
	private Boolean expandingEnabled;

//	private Group MAIN_EDGE = new Group("main_edge");
//	private Group MARGINAL_EDGE = new Group("marginal_edge");
	
	public TreeLayout(Graph graph, Model model, PropertyManager props) {
		setGraphLayoutModel(new DefaultGraphLayoutModel());
		setGraph(graph);
		setModel(model);
		setProperties(props);
		setRootAngle(Math.PI);
		setRoot(root);
	}
	/** @inheritDoc */
	public void setProperties(PropertyManager props) {
		super.setProperties(props);
		Double doubleProp = props.getDouble("hypergraph.visualnet.TreeLayout.maxangle", new Double(180));
		setMaxAngle(doubleProp.doubleValue() * Math.PI / 360);
		doubleProp = props.getDouble("hypergraph.visualnet.TreeLayout.mindistance", new Double(0.3));
		setMinDistance(doubleProp.doubleValue());
		doubleProp = props.getDouble("hypergraph.visualnet.TreeLayout.defaultSize", new Double(0.3));
		setDefaultSize(doubleProp.doubleValue());
		invalidate();
	}


	private double getDistance(double angle1, double angle2, double size) {
		double d1 = Functions.arsinh( Functions.sinh(size) / Math.sin(angle1) );
		if ( Math.abs(angle2) < 0.001 )
			return d1;
		double d2 = Functions.arcosh( (1 + Math.cos(angle1) * Math.cos(Math.PI-angle2) ) /
						  (    Math.sin(angle1) * Math.sin(Math.PI-angle2) ) );
		return Math.max(d1,d2);
	}		
	private double getAngle(double distance, double angle2, double size) {
		double a1 = Math.asin( Functions.sinh(size) / Functions.sinh(distance) );
		double a2 = Math.asin(Math.sin(Math.PI-angle2) /
						  (    Functions.cosh(distance) - Functions.sinh(distance)* Math.cos(Math.PI-angle2) ) );
		return Math.max(a1,a2);
	}		
	private double getSize(Node node) {
		return defaultSize;
	}
	/* Computes the angle / distance of all child nodes
	 */
	private void computeNodeProperties(Node node) {
		NodeProperties prop = new NodeProperties();
		properties.put(node,prop);
		prop.angle2 = 0.0;
		Collection edges = getGraph().getSpanningTree().getEdges(node);
		if (edges.size() == 0) 
			return;
		int n = 0;
		for (Iterator iterator = edges.iterator(); iterator.hasNext(); ) {
			Edge edge = (Edge) iterator.next();
			Node child;
			if (node != edge.getSource())
				continue;
			n++;
			child = edge.getTarget();
			computeNodeProperties(child);
			NodeProperties childProp = (NodeProperties) properties.get(child);	
			childProp.distance = Math.max( 	minDistance,
							 			getSize(node) + getSize(child) );
			childProp.angle1 = getAngle(childProp.distance, childProp.angle2, getSize(child));
			prop.angle2 += childProp.angle1;
		}
		double scale;
		if (node.equals(root) && n > 1) 
			scale = rootAngle / prop.angle2;
		else {
			if (prop.angle2 < maxAngle )
				return;
			scale = maxAngle / prop.angle2;
			prop.angle2 = maxAngle;
		}
		
		for (Iterator iterator = edges.iterator(); iterator.hasNext(); ) {
			Edge edge = (Edge) iterator.next();
			Node child;
			if (!node.equals(edge.getSource()))
				continue;
			child = edge.getTarget();
			NodeProperties childProp = (NodeProperties) properties.get(child);	
			childProp.angle1 *= scale;
			childProp.distance = getDistance(childProp.angle1,childProp.angle2, getSize(child));
			childProp.distance = Math.max( 	minDistance, childProp.distance);
		}
	}
	
	private class EdgeComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1; 
			Edge e1 = (Edge) o1;
			Edge e2 = (Edge) o2;
			int c = e1.getSource().getName().compareTo(e2.getSource().getName());
			if (c != 0)
				return c;
			c = e1.getTarget().getName().compareTo(e2.getTarget().getName());
			return c;			
		}
	}
	
	protected void layoutSubTree(Node node, hypergraph.hyperbolic.ModelVector direction) {
		getGraphLayoutModel().setNodePosition(node,(ModelPoint) direction.getBase().clone());
	
		NodeProperties prop = (NodeProperties) properties.get(node);

		Collection edges = getGraph().getSpanningTree().getEdges(node);
		if (edges.size() == 0) 
			return;

		List outgoingNodes = new ArrayList();
		for (Iterator iterator = edges.iterator(); iterator.hasNext(); ) {
			Edge edge = (Edge) iterator.next();
			Node child;
			if (node.equals(edge.getSource()))
				outgoingNodes.add(edge.getTarget());
		}
		Collections.sort(outgoingNodes,
			new Comparator() {
				public int compare(Object o1,Object o2) { 
					return ((Node) o1).getName().compareTo(((Node) o2).getName());
				}
			}
		);

		getModel().getRotation(direction.getBase(),-prop.angle2).apply(direction);

		for (Iterator iterator = outgoingNodes.iterator(); iterator.hasNext(); ) {
			Node child = (Node) iterator.next();
			NodeProperties childProp = (NodeProperties) properties.get(child);
			Isometry rotation = getModel().getRotation(direction.getBase(),childProp.angle1);
			rotation.apply(direction);
			
			ModelVector nextDirection = (ModelVector) direction.clone();
			getModel().getTranslation(direction, childProp.distance).apply(nextDirection);
			layoutSubTree(child,nextDirection);
			
			rotation.apply(direction);			
		}
		
	}
	public void layout() {
		getGraphLayoutModel().clearNodePositions();
		ModelVector direction = getModel().getDefaultVector();
		
//		Iterator i = graph.getNodeIterator();
//		root = null;
//		while (i.hasNext()) {
//			root = (Node) i.next();
//			if (graph.getIncomingEdges(root).size() == 0 )
//				break;
//		}
		if (root == null) {
			Graph tree = getGraph().getSpanningTree();
			root = (Node) tree.getAttributeManager().getAttribute(AttributeManager.GRAPH_ROOT,tree);
		}
		getGraph().getSpanningTree(root);
		synchronized (getGraph()) {
			properties = new HashMap();
			computeNodeProperties(root);
			layoutSubTree(root,direction);
			
//			System.out.println(graph.getNodes().size());
		}
		
//		for (Iterator i = graph.getEdgeIterator(); i.hasNext();) {
//			Edge edge = (Edge) i.next();
//			if (graph.getSpanningTree().contains(edge))
//				edge.setGroup(MAIN_EDGE);
//			else
//				edge.setGroup(MARGINAL_EDGE);			
//		}
		
		getGraphLayoutModel().setValid(true);

	}

	public void setExpandingEnabled(boolean flag) {
		expandingEnabled = new Boolean(flag);
	}
	public boolean isExpandingEnabled() {
		if (expandingEnabled != null)
			return expandingEnabled.booleanValue();
		String flag = getProperties().getString("visualnet.layout.expandingEnabled");
		if (flag != null && flag.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	/**
	 * Returns the maxAngle.
	 * @return double
	 */
	public double getMaxAngle() {
		return maxAngle;
	}

	/**
	 * Returns the minDistance.
	 * @return double
	 */
	public double getMinDistance() {
		return minDistance;
	}

	/**
	 * Returns the rootAngle.
	 * @return double
	 */
	public double getRootAngle() {
		return rootAngle;
	}

	/**
	 * Sets the maxAngle.
	 * @param maxAngle The maxAngle to set
	 */
	public void setMaxAngle(double maxAngle) {
		this.maxAngle = maxAngle;
	}

	/**
	 * Sets the minDistance.
	 * @param minDistance The minDistance to set
	 */
	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}

	/**
	 * Sets the rootAngle.
	 * @param rootAngle The rootAngle to set
	 */
	public void setRootAngle(double rootAngle) {
		this.rootAngle = rootAngle;
	}

	/**
	 * Returns the defaultSize.
	 * @return double
	 */
	public double getDefaultSize() {
		return defaultSize;
	}

	/**
	 * Sets the defaultSize.
	 * @param defaultSize The defaultSize to set
	 */
	public void setDefaultSize(double defaultSize) {
		this.defaultSize = defaultSize;
	}

	/**
	 * @return
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * @param node
	 */
	public void setRoot(Node node) {
		root = node;
	}

}
