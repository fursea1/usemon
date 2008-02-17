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

import java.util.Iterator;
import java.util.Set;

import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphEvent;
import hypergraph.graphApi.GraphListener;
import hypergraph.graphApi.Node;
import hypergraph.graphApi.algorithms.GraphUtilities;

/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ForceDirectedWeight implements GenericMDSLayout.MDSWeight, GraphListener {
	private double 		connectedDisparity;
	private double 		repulsingForce;
	private double		repulsingForceCutOff;
	private Graph 		graph;
	private Set			connectedComponents;
	public void setGraph(Graph graph) {
		this.graph = graph;
		graph.addGraphListener(this);
	}
	private Set getConnectedComponent(Node n) {
		if (connectedComponents == null)
			connectedComponents = GraphUtilities.getConnectedComponents(graph);
		for (Iterator i = connectedComponents.iterator(); i.hasNext();) {
			Set comp = (Set) i.next();
			if (comp.contains(n))
				return comp;
		}
		return null;
	}
	public double getWeight(Node n) {
		return 1.0;
	}
	private double cut = 1;
	public double getWeight(Node n1, Node n2, double d) {
		if (graph.isConnected(n1, n2))
			return (d-getConnectedDisparity())*(d-getConnectedDisparity()); // for classical MDS
		if (getConnectedComponent(n1).contains(n2)) {
			if (d < getRepulsingForceCutOff())
				return getRepulsingForce()/d - getRepulsingForce()/getRepulsingForceCutOff();
		}
			else if (d < cut)
				return getRepulsingForce()/d - getRepulsingForce()/cut;
		return 0;		
	}
	public double getWeightDerivative(Node n1, Node n2, double d) {
		if (graph.isConnected(n1, n2))
			return 2 * (d - getConnectedDisparity());
		if (getConnectedComponent(n1).contains(n2)) {
			if (d < getRepulsingForceCutOff())
				return -getRepulsingForce() / (d * d); // repulsing force for force directed layout.
		} 
		else if (d < cut)
			return -getRepulsingForce() / (d * d); // repulsing force for force directed layout.
		return 0;		
	}
	public double getRepulsingForce() {	return repulsingForce; }
	public void setRepulsingForce(double d) { repulsingForce = d; }
	public double getRepulsingForceCutOff() { return repulsingForceCutOff; }
	public void setRepulsingForceCutOff(double d) { repulsingForceCutOff = d; }
	public double getConnectedDisparity() { return connectedDisparity; }
	public void setConnectedDisparity(double d) { connectedDisparity = d; }

	public void elementsAdded(GraphEvent ge) {
		connectedComponents = null;
	}
	public void elementsRemoved(GraphEvent ge) {
		connectedComponents = null;
	}
	public void structureChanged(GraphEvent ge) {
		connectedComponents = null;
	}
}