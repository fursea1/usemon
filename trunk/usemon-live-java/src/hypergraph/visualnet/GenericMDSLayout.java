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

import java.util.*;

import hypergraph.graphApi.*;
import hypergraph.hyperbolic.*;

/**
 * @author Jens Kanschik
 */
public class GenericMDSLayout extends IteratingGraphLayout {

	NodeIndex 	nodeIndex;
	Complex		nodePositions[];
	Complex		tempPositions[];
	ComplexVector	gradient[];
	ComplexVector	previousGradient[];
	double		distances[][];
		
	private double		stepWidth = 1;
	
	private double		gradientNorm2 = 0;
	private double		previousGradientNorm2 = 0;
	private double		energy = 0;
	private double		lastEnergy = 0;
		
	private Complex		z1 = new Complex();
	private Complex		z2 = new Complex();
	private Complex		z3 = new Complex();
	private Complex		z4 = new Complex();
	private static final Complex zero = new Complex();
	private ComplexVector		v1 = new ComplexVector();
	private ComplexVector		v2 = new ComplexVector();
	private Isometry			isom1;
	
	private MDSWeight 	weight;

	public GenericMDSLayout() {
		ForceDirectedWeight w = new ForceDirectedWeight();
		w.setConnectedDisparity(0.2);
		w.setRepulsingForce(0.01);
		w.setRepulsingForceCutOff(100);
		setWeight(w);
	}
	public GenericMDSLayout(MDSWeight w, Model model, Graph graph, PropertyManager props) {
		setModel(model);
		setProperties(props);
		if (w == null) {
			w = new ForceDirectedWeight();
			Double doubleProp;
			doubleProp = getProperties().getDouble("visualnet.GenericMDSLayout.repulsingForce", new Double(0.01));
			((ForceDirectedWeight) w).setRepulsingForce(doubleProp.doubleValue());
			doubleProp = getProperties().getDouble("visualnet.GenericMDSLayout.repulsingForceCutOff", new Double(100));
			((ForceDirectedWeight) w).setRepulsingForceCutOff(doubleProp.doubleValue());
			doubleProp = getProperties().getDouble("visualnet.GenericMDSLayout.connectedDisparity", new Double(0.2));
			((ForceDirectedWeight) w).setConnectedDisparity(doubleProp.doubleValue());
		}
		weight = w;
		setGraph(graph);
	}
	public void setGraph(Graph graph) {
		weight.setGraph(graph);
		nodeIndex = new NodeIndex(graph);
		super.setGraph(graph);
	}
	public Graph getGraph() {
		return super.getGraph();
	}

	public void setWeight(MDSWeight weight) {
		this.weight = weight;
	}
	private Complex getBarycenter(Node node) {
		Complex position = new Complex();
		double n = 0;
		for (Iterator iter = getGraph().getEdges(node).iterator(); iter.hasNext();) {
			Edge edge = (Edge) iter.next();
			position.add((Complex) getGraphLayoutModel().getNodePosition(edge.getOtherNode(node)));
			n++;
		}
		if (n == 0)
			return (Complex) getGraphLayoutModel().getNodePosition(node);
		position.multiply(1 / n);
		return position;
	}

	private void translateRandomly(Complex[] positions,int i) {
		translateRandomly(positions[i], 0.01, 0.05);
	}
	private void translateRandomly(Complex pos,double minR, double maxR) {
		double alpha = 2 * Math.PI * Math.random();
		double t = minR + (maxR - minR) * Math.random();
		ComplexVector v = new ComplexVector();
		v.setBase(pos);
		v.v.setReal(t * Math.cos(alpha));
		v.v.setImag(t * Math.sin(alpha));
		Isometry isom = getModel().getTranslation(v, t);
		isom.apply(pos);
	}
	public synchronized void elementsAdded(GraphEvent ge) {
		if (ge.getElement().getElementType() == Element.NODE_ELEMENT) {
			Complex[] newNodePositions = new Complex[getGraph().getNodes().size()];
			for (int i = 0; i<nodePositions.length; i++) 
				newNodePositions[i] = nodePositions[i];
			newNodePositions[nodePositions.length] = new Complex();
			translateRandomly(newNodePositions[nodePositions.length], 0.1, 1);
			nodePositions = newNodePositions;
			nodeIndex.addNode((Node) ge.getElement());
		}
		if (ge.getElement().getElementType() == Element.EDGE_ELEMENT) {
			// a new edge has been added.
			// if one of the nodes has degree 1, it means that this node
			// hasn't been in the same component of the graph before.
			// In this case it's better to place it near the other node.
			Edge edge = (Edge) ge.getElement();
			Node node = edge.getSource();
			if (getGraph().getEdges(node).size() != 1) {
				node = edge.getTarget();
				if (getGraph().getEdges(node).size() != 1)
					return; // if both nodes have a degree >1, both nodes have already been positioned.
			}
			int i = nodeIndex.getIndex(node);
			int j = nodeIndex.getIndex(edge.getOtherNode(node));
			nodePositions[i].setTo(nodePositions[j]);
			translateRandomly(nodePositions[i], 0.01, 0.05);
		}
		invalidate();
	}
	public synchronized void elementsRemoved(GraphEvent ge) {
		if (ge.getElement().getElementType() == Element.NODE_ELEMENT) {
			NodeIndex newNodeIndex = new NodeIndex(getGraph());
			Complex[] newNodePositions = new Complex[getGraph().getNodes().size()];
			for (Iterator i = getGraph().getNodes().iterator(); i.hasNext();) {
				Node node = (Node) i.next();
				int j = nodeIndex.getIndex(node);
				int k = newNodeIndex.getIndex(node);
				newNodePositions[k] = nodePositions[j];
			}
			nodePositions = newNodePositions;
			nodeIndex = newNodeIndex;
		}
		invalidate();
	}
	public void invalidate() {
		super.invalidate();
		distances = null;
		gradient = null;
		previousGradient = null;
		tempPositions = null;
		stepWidth = 0.05;
	}

	public void initializeRandomly(Complex[] nodePositions) {
		for (Iterator i = getGraph().getNodes().iterator(); i.hasNext();) {
			Node node = (Node) i.next();
			Complex p = new Complex();
			translateRandomly(p, 0, 5);
			nodePositions[nodeIndex.getIndex(node)] = p;
		}
	}
	private void setLayoutModelPositions(Complex[] nodePositions) {
		for (int i = 0; i < nodePositions.length; i++)
			getGraphLayoutModel().setNodePosition(nodeIndex.getNode(i), nodePositions[i]);
	}
	private double getEnergy(double distances[][]) {
		double energy = 0;
		for (int i=0; i < nodeIndex.getSize(); i++)
			for (int j=i+1; j < nodeIndex.getSize(); j++) 
				energy = energy + weight.getWeight(nodeIndex.getNode(j),nodeIndex.getNode(i),getDistance(i,j));
		return energy;
	}
	
	private void computeDistances(Complex[] position) {
		for (int k=0; k < nodeIndex.getSize(); k++) 
			for (int i=k+1; i < nodeIndex.getSize(); i++) 
				distances[k][i] = ((PoincareModel) getModel()).dist(position[k],position[i]);
	}
	
	private double getDistance(int i, int j) {
		if (j<i) 
			return distances[j][i];
		else
			return distances[i][j];
	}
	
	private double computeGradient(Complex position[], ComplexVector[] gradient) {
		double norm2 = 0;
		double nodeNorm2 = 0;
		for (int k=0; k < nodeIndex.getSize(); k++) {
			gradient[k].scale(0);
			for (int i=0; i < nodeIndex.getSize(); i++) {
				if (k==i) continue;
				double w = weight.getWeightDerivative(nodeIndex.getNode(k),nodeIndex.getNode(i),getDistance(i,k));
				if (Math.abs(w) > 0.001) 
				{
					getModel().distanceGradient(position[k],position[i],v1);
					v1.v.multiply(w);
					gradient[k].v.add(v1.v);
				}
			}
			nodeNorm2 = getModel().length2(gradient[k]);
			norm2 += nodeNorm2;
		}
		return norm2;	
	}
	
	
	protected void iteration(IterationThread thread) {
		if (getGraph().getNodes().size() == 0) {
			thread.stopIterating();
			return;
		}
		// initializing of some global variables to avoid garbage collection.
		if (isom1 == null)
			isom1 = getModel().getIdentity();
		if (gradient == null) {
			gradient = new ComplexVector[nodeIndex.getSize()];
			for (int k = 0; k < nodeIndex.getSize(); k++) 
				gradient[k] = new ComplexVector(nodePositions[k], new Complex());
		}
		if (tempPositions == null) {
			tempPositions = new Complex[nodeIndex.getSize()];
			for (int k=0; k < nodeIndex.getSize(); k++) 
				tempPositions[k] = new Complex();
		}
		if (distances == null) 
			distances = new double[nodeIndex.getSize()][nodeIndex.getSize()];

	for (int loop = 0; loop < 10; loop++) {
		computeDistances(nodePositions);

		gradientNorm2 = computeGradient(nodePositions, gradient);

		if (previousGradient == null) {
			previousGradient = new ComplexVector[nodeIndex.getSize()];
			for (int k = 0; k < nodeIndex.getSize(); k++)
				previousGradient[k] = (ComplexVector) gradient[k].clone();
			previousGradientNorm2 = gradientNorm2;
		}
		// compare gradient with previous gradient to spot oscillation
		double gradientProduct = 0;
		for (int i = 0; i < gradient.length; i++)
			gradientProduct += getModel().product(gradient[i], previousGradient[i]);
		double angle = gradientProduct / Math.sqrt(gradientNorm2 * previousGradientNorm2);
		if (angle > 0.9)
			stepWidth *= 1.1;
		else if (angle > 0.8)
			stepWidth *= 1.05;
		else if (angle > 0.5)
			stepWidth *= 1;
		else if (angle > 0.3)
			stepWidth *= 0.7;
		else if (angle > 0.2)
			stepWidth *= 0.4;
		else if (angle > 0.1)
			stepWidth *= 0.2;
		else
			stepWidth *= 0.1;
		if (stepWidth < 0.000000001)
			thread.stopIterating();

		for (int k = 0; k < nodeIndex.getSize(); k++) {
			double step = stepWidth * getModel().length(gradient[k]);
			if (step > 0.2)
				step = 0.2;
			tempPositions[k].setTo(nodePositions[k]);
			getModel().getTranslation(isom1, gradient[k], -step);
			isom1.apply(tempPositions[k]);
			isom1.apply(gradient[k]); // the gradient has to be translated along the geodesic from the old to the new position so that we can compare it with the next gradient.
			previousGradient[k].setTo(gradient[k]);
			nodePositions[k].setTo(tempPositions[k]);
		}
		energy = getEnergy(distances);
		if ( energy <0.01 || Math.abs((energy-lastEnergy) / energy) < 0.000000001 )
//			chooseFreeNodes();
			thread.stopIterating();

//		System.out.println("energy " + energy + "  stepWidth " + stepWidth + "  gradientProduct "  + gradientProduct/Math.sqrt(gradientNorm2*previousGradientNorm2));
		
		previousGradientNorm2 = gradientNorm2;
		lastEnergy = energy;
}
	setLayoutModelPositions(nodePositions);

	}

	public synchronized void layout() {
		synchronized (getGraph()) {
			if (getGraphLayoutModel() == null)
				setGraphLayoutModel(new DefaultGraphLayoutModel());
		
			if (nodePositions == null) {
				nodePositions = new Complex[nodeIndex.getSize()];
				initializeRandomly(nodePositions);
				setLayoutModelPositions(nodePositions);
			}
			super.layout();				
		}
//		System.out.println(getEnergy(nodePositions));
	}

	public boolean isExpandingEnabled() {
		return false;
	}

	public static class NodeIndex {
		private List	list;
		private Map		map;
		private Graph	graph;

		NodeIndex(Graph g) {
			graph = g;
			buildIndex();
		}
		void buildIndex() {
			list = new ArrayList();
			map = new HashMap();
			int j = 0;
			for (Iterator i = graph.getNodes().iterator(); i.hasNext();) {
				Node node = (Node) i.next();
				list.add(node);
				map.put(node, new Integer(j));
				j++;
			}
		}
		void addNode(Node node) {
			list.add(node);
			map.put(node, new Integer(list.size() - 1));
		}
		void removeNode(Node node) {
			buildIndex();
		}
		int getSize() {
			return list.size();
		}
		int getIndex(Node node) {
			Integer i = (Integer) map.get(node);
			if (i == null)
				return -1;
			return i.intValue();
		}
		Node getNode(int i) {
			return (Node) list.get(i);
		}
	}


/**
 * The algorithm of the <code>GenericMDSLayout</code> uses a weight which is based on
 * two nodes and the distance between those two nodes.
 *
 * @author Jens Kanschik
 */
public interface MDSWeight {
	public void setGraph(Graph graph);
	public double getWeight(Node n);
	public double getWeight(Node n1, Node n2, double d);
	public double getWeightDerivative(Node n1, Node n2, double d);
}


}
