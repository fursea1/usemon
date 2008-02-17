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
import java.util.List;

import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphEvent;
import hypergraph.graphApi.GraphListener;
import hypergraph.graphApi.Node;
import hypergraph.graphApi.algorithms.BFSGraphWalker;
import hypergraph.graphApi.algorithms.GraphWalkerListener;

/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MDSWeight implements GenericMDSLayout.MDSWeight, GraphListener {
	private Graph graph;
	private DistanceArray distances;

	public MDSWeight() {
	}
	public void setGraph(Graph g) {
		graph = g;
		graph.addGraphListener(this);
		distances = new DistanceArray(graph);
	}
	public void elementsAdded(GraphEvent ge) {
		distances = new DistanceArray(graph);
	}
	public void elementsRemoved(GraphEvent ge) {
		distances = new DistanceArray(graph);
	}
	public void structureChanged(GraphEvent ge) {
		distances = new DistanceArray(graph);
	}
	public double getWeight(Node n) {
		return distances.getSphereSize(n, 2);
	}
	public double getWeight(Node n1, Node n2, double d) {
		double w = 0;
		if (d < 20)
			w += 0.5/d - 0.5/20;
		double i = distances.getDistance(n1, n2);
		if (i > 0 && i < 2)
			w += (d - i*0.15) * (d - i*0.15) / i ; 
		return w;		
	}

	public double getWeightDerivative(Node n1, Node n2, double d) {
		double w = 0;
		if (d < 20)
			w += -0.5/(d*d);
		double i = distances.getDistance(n1, n2);
		if (i > 0 && i < 2)
			w += (d - i*0.15) / i ; 
		return w;		
	}

	public class DistanceArray implements GraphWalkerListener {
		private Graph graph;
		private int[][] distances;	
		private List spheres;	
		private GenericMDSLayout.NodeIndex nodeIndex;
		private int currentNode;
		private BFSGraphWalker gw;
		private int c,d;
		
		public DistanceArray(Graph g) {
			graph = g;
			initializeDistances();
		}
		public int getSphereSize(Node n, int radius) {
			return ((int[]) spheres.get(radius))[nodeIndex.getIndex(n)];
		}
		public int getDistance(Node n1, Node n2) {
			return distances[nodeIndex.getIndex(n1)][nodeIndex.getIndex(n2)];
		}
		private void initializeDistances() {
			nodeIndex = new GenericMDSLayout.NodeIndex(graph);
			distances = new int[nodeIndex.getSize()][nodeIndex.getSize()];
			spheres = new ArrayList();
			gw = new BFSGraphWalker();
			gw.setGraph(graph);
			gw.addListener(this);
			for (currentNode = 0; currentNode < nodeIndex.getSize(); currentNode++) {
				Node n = nodeIndex.getNode(currentNode);
				gw.setStartNode(n);
				c = d = 0;
				gw.walk();
				setSphereSize(currentNode, d, c);
			}
		}
		private void setSphereSize(int node, int radius, int size) {
			int[] s;
			if (radius >= spheres.size()) {
				s = new int[nodeIndex.getSize()];
				spheres.add(radius, s);
			} else
				s = (int[]) spheres.get(radius);
			s[node] = size;
		}
		public void visitElement(Element e) {
			if (e.getElementType() == Element.NODE_ELEMENT) {
				int i = nodeIndex.getIndex((Node) e);
				distances[currentNode][i] = gw.getIteration();
				distances[i][currentNode] = gw.getIteration();
				if (gw.getIteration() != d) {
					setSphereSize(currentNode, d, c);
					d = gw.getIteration();
					c = 0;
				}
				c++;
			}
		}
	}
}
