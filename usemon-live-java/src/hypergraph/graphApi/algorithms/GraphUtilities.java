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

import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphException;
import hypergraph.graphApi.GraphSystem;
import hypergraph.graphApi.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a set of useful graph theoretical algorithms such
 * as retrieving all connected components, as well as some methods to create
 * standard graphs such as trees, grids and complete graphs.
 * @author Jens Kanschik
 */
public final class GraphUtilities {

	/** Private constructor to prevent the class from being instantiated. */
	private GraphUtilities() {
	}

	/**
	 * Creates a two dimensional grid with <code>width</code> times <code>height</code> nodes.
	 * The names of the nodes are "i/j",
	 * where 1&le i &le <code>height</code> and 1&le j &le <code>weight</code>,i.e. "12/15" etc.
	 *
	 * @param gs The graph system used to create the graph.
	 * @param width The width of the grid.
	 * @param height The height of the grid.
	 * @return The two dimensional grid.
	 */
	public static Graph createGrid2(GraphSystem gs, int width, int height) {
		Graph graph = gs.createGraph();
		Node node = null;
		for (int i = 1; i <= height; i++)
			for (int j = 1; j <= width; j++) {
				try {
					node = graph.createNode(Integer.toString(i) + "/" + Integer.toString(j));
				} catch (GraphException ge) {
					System.out.println(ge);
				}
				if (i > 1) {
					Node source = (Node) graph.getElement(Integer.toString(i - 1) + "/" + Integer.toString(j));
					graph.createEdge(source, node);
				}
				if (j > 1) {
					Node source = (Node) graph.getElement(Integer.toString(i) + "/" + Integer.toString(j - 1));
					graph.createEdge(source, node);
				}
			}
		return graph;
	}

	/**
	 * Creates a discrete graph consisting of <code>n</code> nodes.
	 * The nodes have names from 1 to <code>n</code>.
	 *
	 * @param gs The graph system used to create the graph.
	 * @param n The number of nodes.
	 * @return The discrete graph.
	 */
	public static Graph createDiscreteGraph(GraphSystem gs, int n) {
		Graph graph = gs.createGraph();
		Node node;
		for (int i = 1; i <= n; i++)
			node = graph.createNode();
		return graph;
	}

	/**
	 * Creates a complete graph consisting of <code>n</code> nodes, i.e. each node is connected
	 * with every other node. The nodes have names from 1 to <code>n</code>.
	 *
	 * @param gs The graph system used to create the graph.
	 * @param n The number of nodes.
	 * @return The complete graph.
	 */
	public static Graph createCompleteGraph(GraphSystem gs, int n) {
		Graph graph = gs.createGraph();
		Node node;
		for (int i = 1; i <= n; i++) {
			node = graph.createNode();
			node.setLabel(Integer.toString(i));
		}
		for (Iterator i = graph.getNodes().iterator(); i.hasNext();) {
			node = (Node) i.next();
			for (Iterator j = graph.getNodes().iterator(); j.hasNext();) {
				Node next = (Node) j.next();
				if (node != next)
					graph.createEdge(node, next); // a node should not be connected to itself.
			}
		}
		return graph;
	}

	/**
	 * Retrieves the set of connected components of a graph.
	 *
	 * @param graph An arbitrary graph.
	 * @return The set of connected components of the graph.
	 * Each element in the set is again a set that contains the nodes belonging to the component.
	 */
	public static Set getConnectedComponents(Graph graph) {
		Set components = new HashSet();
		// nodeSet contains all nodes which haven't been processed yet.
		Set nodeSet = new HashSet();
		nodeSet.addAll(graph.getNodes());
		while (!nodeSet.isEmpty()) {
			// pick some node an create a new component with this node.
			Node node = (Node) nodeSet.iterator().next();
			Set comp = new HashSet();
			components.add(comp);
			comp.add(node);
			nodeSet.remove(node);
			// the set of all edges where one node is known to be in comp, but not the other.
			Set edges = new HashSet();
			edges.addAll(graph.getEdges(node));
			// loop until there are no such edges anymore
			while (!edges.isEmpty()) {
				Edge edge = (Edge) edges.iterator().next();
				// if the source can be removed, it means that the source hasn't been processed yet, so add all edges or this node.
				if (nodeSet.remove(edge.getSource())) {
					comp.add(edge.getSource());
					edges.addAll(graph.getEdges(edge.getSource()));
				}
				if (nodeSet.remove(edge.getTarget())) {
					comp.add(edge.getTarget());
					edges.addAll(graph.getEdges(edge.getTarget()));
				}
				edges.remove(edge);
			}
		}
		return components;
	}

	/**
	 * Recursively creates a subtree for the method <code>createTree(int,int)</code>.
	 * If a delay != 0 is passed, the method first waits <code>delay</code> milliseconds.
	 * This is used in createTreeDelayed.
	 */
	private static Graph createSubTree(Graph graph, Node root, int generations, int currentGen, int branchFactor, long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
		}
		if (generations == currentGen)
			return null;
		Node node;
		for (int i = 1; i <= branchFactor; i++) {
			node = graph.createNode();
//			node.setName("(" + currentGen + "/" + i + ")");
			synchronized (graph) {
				graph.createEdge(root, node);
			}
			createSubTree(graph, node, generations, currentGen + 1, branchFactor, delay);
		}
		return graph;
	}
	/**
	 * Creates a tree. The root of the tree has the name "root" and can be retrieved using the method
	 * {@link hypergraph.graphApi.Graph#getElement(String)}. The parameter <code>generations</code>
	 * contains the number of generations or levels of the tree,
	 * each node has <code>branchFactor</code> children.
	 * If <code>generation</code> is 0, the tree consists only of one node, the root.
	 *
	 * @param gs The <code>GraphSystem</code> to which the graph belongs.
	 * @param generations The number of generations.
	 * @param branchFactor The number of children for each node.
	 * @return The tree.
	 */
	public static Graph createTree(GraphSystem gs, int generations, int branchFactor) {
		Graph tree = gs.createGraph();
		Node root = tree.createNode();
		createSubTree(tree, root, generations, 0, branchFactor, 0);
		return tree;
	}

	/**
	 * Creates a tree in a seperate thread.
	 * Each subtree is created after a certain delay, so that this method
	 * can be used to test the event/listener functionality of the graph and the ability
	 * of listeners to handle changing trees.
	 * <p>
	 * The root of the tree has the name "root" and can be retrieved using the method
	 * {@link hypergraph.graphApi.Graph#getElement(String)}.
	 * The parameter <code>generations</code>
	 * contains the number of generations or levels of the tree,
	 * each node has <code>branchFactor</code> children.
	 * If <code>generation</code> is 0, the tree consists only of 1 node, the root.
	 *
	 * @param gs The <code>GraphSystem</code> to which the graph belongs.
	 * @param generations The number of generations.
	 * @param branchFactor The number of children for each node.
	 * @param delay The delay is the number of milliseconds the thread waits
	 * before the next node is created.
	 * @return The tree.
	 */
	public static Graph createTreeDelayed(GraphSystem gs, int generations, int branchFactor, long delay) {
		Graph tree = gs.createGraph();
		Thread thread = new CreateTreeThread(tree, generations, branchFactor, delay);
		thread.start();
		return tree;
	}

	private static class CreateTreeThread extends Thread {
		Graph graph;
		Node root;
		int generations;
		int branchFactor;
		long delay;
		public CreateTreeThread(Graph graph, int generations,int branchFactor,long delay) {
			this.graph = graph;
			this.generations = generations;
			this.branchFactor = branchFactor;
			this.delay = delay;
			root = graph.createNode();
		}
		public void run() {
			createSubTree(graph,root,generations,0,branchFactor,delay);
		}
	}	
	private static class CreateRandomGraphThread extends Thread {
		Graph graph;
		Node root;
		int size;
		double addNodeProb;
		double removeNodeProb;
		double addEdgeProb;
		double removeEdgeProb;
		long delay;
		public CreateRandomGraphThread(Graph graph, int size , double addNodeProb, double removeNodeProb,double addEdgeProb, double removeEdgeProb,long delay) {
			this.graph = graph;
			this.size = size;
			this.addNodeProb = addNodeProb;
			this.removeNodeProb = removeNodeProb;
			this.addEdgeProb = addEdgeProb;
			this.removeEdgeProb = removeEdgeProb;
			this.delay = delay;
			root = graph.createNode();
		}
		private Node getRandomNode() {
			Node n = null;
			for (Iterator i = graph.getNodes().iterator(); i.hasNext();) {
				n = (Node) i.next();
				if (Math.random() < 1 / (double) graph.getNodes().size())
					return n;
			}
			return n;
		}
		private Edge getRandomEdge() {
			Edge e = null;
			for (Iterator i = graph.getEdges().iterator(); i.hasNext();) {
				e = (Edge) i.next();
				if (Math.random() < 1 / (double) graph.getEdges().size())
					return e;
			}
			return e;
		}
		public void run() {
			while (graph.getNodes().size() < size) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
				}
				System.out.println("==============================================");
				System.out.println(graph);
				Node node = getRandomNode();
				double random = Math.random();
				if (random < addNodeProb)
					graph.createNode();
				else if (random < addNodeProb + addEdgeProb) {
					Node n = getRandomNode();
					graph.createEdge(node, n);
				} else if (random < addNodeProb + addEdgeProb + removeNodeProb)
					graph.removeElement(node);
				else if (random < addNodeProb + addEdgeProb + removeNodeProb + removeEdgeProb)
					graph.removeElement(getRandomEdge());
				System.out.println(graph);
			}
		}
	}

	/** Creates a random graph. Not reliable, better don't use. */
	public static Graph createRandomGraph(GraphSystem gs, int size, double addNodeProb, double removeNodeProb,double addEdgeProb, double removeEdgeProb,long delay) {
		Graph graph = gs.createGraph();
		Thread thread = new CreateRandomGraphThread(graph, size, addNodeProb, removeNodeProb, addEdgeProb, removeEdgeProb, delay);
		thread.start();
		return graph;
	}

	/** Checks whether a given graph is acyclic or not
	 * using the fact that a graph is acyclic
	 * if and only if it has a topological ordering.
	 * @param graph The graph that has to be checked.
	 * @return <code>True</code> if and only if the graph is acyclic.
	 */
	public static boolean isAcyclic(Graph graph) {
		return getTopologicalOrdering(graph).size() == graph.getNodes().size();
	}

	/** Returns a list of all nodes on a topological order.
	 * A topological order of a graph is an ordering of the nodes such that
	 * n1 &lt; n2 if there exists an edge from n1 to n2.
	 * A topological ordering exists if and only if the graph is acyclic.
	 * If it isn't acyclic, the returned list doesn't contain all nodes of the graph.
	 *
	 * @param graph The graph for which the topological ordering has to be determined.
	 * @return A list of the nodes of the graph in a topological order.
	 */
	public static List getTopologicalOrdering(Graph graph) {
		List orderedNodes = new ArrayList();
		Map inDegrees = new HashMap();
		List sources = new LinkedList();
		for (Iterator edges = graph.getEdges().iterator(); edges.hasNext();) {
			Edge edge = (Edge) edges.next();
			Integer id = (Integer) inDegrees.get(edge.getTarget());
			if (id == null)
				inDegrees.put(edge.getTarget(), new Integer(1));
			else
				inDegrees.put(edge.getTarget(), new Integer(id.intValue() + 1));
		}
		for (Iterator nodes = graph.getNodes().iterator(); nodes.hasNext();) {
			Node node = (Node) nodes.next();
			if (!inDegrees.keySet().contains(node)) {
				inDegrees.put(node, new Integer(0));
				sources.add(node);
			}
		}
		while (!sources.isEmpty()) {
			Node node = (Node) sources.remove(0);
			for (Iterator edges = graph.getEdges(node).iterator(); edges.hasNext();) {
				Edge edge = (Edge) edges.next();
				if (edge.getSource().equals(node)) {
					Integer id = (Integer) inDegrees.get(edge.getTarget());
					if (id.intValue() == 1)
						sources.add(edge.getTarget());
					inDegrees.put(edge.getTarget(), new Integer(id.intValue() - 1));
				}
			}
			orderedNodes.add(node);
		}
		for (int i = 0; i < orderedNodes.size(); i++) {
			Node node = (Node) orderedNodes.get(i);
			node.setLabel(node.getLabel() + " / " + i);
		}
		return orderedNodes;
	}

	/** Makes a graph to an acyclic graph by reversing a minimal set of edges.
	 * The original edges are reversed within the graph and returned in a set.
	 * This set can be used later on to restore the original graph structure
	 * by simply reversing each edge in this set again.
	 * @param graph The graph that has to be made acyclic. This graph will be changed.
	 * @return A set that contains all edges that have been reversed
	 * by the algorithm to make the graph acyclic.
	 */
	public static Set makeAcyclic(Graph graph) {
		Set reversedEdges = new HashSet();
		for (Iterator iter = graph.getNodes().iterator(); iter.hasNext();) {
			Node node = (Node) iter.next();
			Collection edges = graph.getEdges(node);
			int inDegree = 0;
			int outDegree = 0;
			for (Iterator e = edges.iterator(); e.hasNext();) {
				Edge edge = (Edge) e.next();
				if (edge.getSource().equals(node))
					outDegree++;
				else
					inDegree++;
			}
			for (Iterator e = edges.iterator(); e.hasNext();) {
				Edge edge = (Edge) e.next();
				if ((inDegree > outDegree && edge.getSource().equals(node) ||
					 inDegree <= outDegree && edge.getTarget().equals(node)) &&
					!reversedEdges.contains(edge)) {
					reversedEdges.add(edge);
					edge.reverse();
				}
			}
		}
		return reversedEdges;
	}
}
