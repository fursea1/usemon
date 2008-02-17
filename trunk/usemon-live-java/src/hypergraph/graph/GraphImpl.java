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

package hypergraph.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphEvent;
import hypergraph.graphApi.GraphException;
import hypergraph.graphApi.GraphListener;
import hypergraph.graphApi.GraphSystem;
import hypergraph.graphApi.Group;
import hypergraph.graphApi.Node;

import javax.swing.event.EventListenerList;

/**
 * <p>
 * This class handles any kind of graphs.
 * Actually the edges are all directed due to the setup of the <code>Edge</code>
 * interface, but there are convenience methods to work with an edge as being undirected.
 * The class provides a set of methods to search / add / remove
 * nodes and edges, as well as some iterators to traverse through
 * the whole set of nodes and edges or through subsets.
 * It also provides some convenience methods to get standard
 * information about nodes and edges like the incoming or outcoming degree
 * as well as a view on a spanning graph.
 * </p>
 * <p>
 * <b>At the moment, at most one edge between two nodes is allowed.</b>
 * This is due to the usage of class <code>Map2D</code> for the adjacency list.
 * </p>
 * <p>
 * <b> Editing the graph</b>
 * <br/>
 * The following list shows the commonly used methods to edit the graph :
 * <ul>
 * <li> <code>addNode(Node)</code> to add a new node. </li>
 * <li> <code>addEdge(Edge)</code> to add a new edge. The nodes connected by the edge are added as well (if necessary). </li>
 * <li> <code>connect(Node,Node)</code> to connect to node with a new edge. The nodes are added to the graph (if necessary).</li>
 * <li> <code>removeNode(Node)</code> to remove a node. All edges adjacent to this node are removed as well. </li>
 * <li> <code>removeEdge(Edge)</code> to remove an edge. The adjacent nodes are not removed, even if they are not connected to the rest of the graph.</li>
 * </ul>
 * <b> Getting nodes and edges, traversing the graph</b>
 * <br/>
 * The following list shows the commonly used methods to access the elements of a graph :
 * <ul>
 * <li> <code>getNodeByName(String)</code> to get a node with the passed name.</li>
 * <li> <code>getEdgeByName(String)</code> to get an edge with the passed name.</li>
 * <li> <code>contains(Node)</code> and <code>contains(Edge)</code> return true if the node or edge is contained in the graph.</li>
 * <li> <code>getNodes()</code> and <code>getEdges()</code> return views on the set of nodes or edges.</li>
 * <li> <code>getNodeIterator()</code> and <code>getEdgeIterator()</code> return iterators to traverse through the set of nodes or edges.
 *      It is also possible to pass a filter to create an iterator that traverses only through a certain subset of the nodes / edges.</li>
 * </ul>
 *
 * <b> Example code to create a simple graph </b>
 * <br/>
 * This is some example to see the editing methods in action.
 * <pre>
 *  Graph graph = new Graph();
 *  Node  n1    = new Node("Node1");       // creates a new node with name "Node1"
 *  Node  n2    = new Node("Node2");       // creates a new node with name "Node2"
 *  Edge  e     = new DefaultEdge(n1,n2);  // creates a new edge from n1 to n2.
 *  graph.addEdge(e);                      // adds the edge to the graph
 *                                         // and adds the source and target of the edge
 *                                         // since these nodes are not yet part of the graph.
 *  graph.addNode(new Node("Node3"));      // adds a new node to the graph
 *  n1 = graph.getNodeByName("Node3");     // gets the node with name "Node3".
 *  graph.connect(n1,n2);                  // connects n1 ("Node3") with n2 ("Node2"),
 *                                         // doesn't add the nodes as they are already part of the graph.
 *  graph.addEdge(new DefaultEdge(n1,n2)); // equivalent to previous line.
 * </pre>
 *</p>
 *
 *<p>
 *<b> Accessing the adjacency list </b>
 *<br/>
 * This is only relevant for implementors of subclassed,
 * since the adjacency list is protected. All operations concerning the graph
 * structure must be done using the public methods.
 * The edges are stored in the so called adjacebcy list
 * (protected variable <code>adjacencyList</code> of type <code>Map2D</code>).
 * This list is actually a two dimensional, symmetric matrix which is indexed by the nodes.
 * The following operations are standard :
 * <dl>
 * <dt>Getting the edge between two nodes :</dt>
 * <dd>The edge connecting two given nodes can be retrieved by calling
 * <code>adjacencyList.get(Node,Node)</code>. Since the matrix is symmetric,
 * the order of the two nodes is not important. If you only need to know whether
 * there exists such an edge, call <code>adjacencyList.containsKey(Node,Node)</code>.
 * </dd>
 * <dt>Getting all edges at one node :</dt>
 * <dd>
 * The method <code>adjacencyList.get(Node)</code> returns a map.
 * The values of this map are the edges that are adjacent to the node,
 * the keys are the adjacent nodes.
 * Due to the symmetry of the adjacency matrix,
 * the direction of the edges is neglected.
 * </dd>
 * <dt>Removing edges :</dt>
 * <dd>
 * Methods <code>adjacenjyList.remove(Node,Node)</code> and
 * <code>adjacenjyList.remove(Node)</code> are used to remove edges.
 * The first method removes the edge between the passed nodes if existing,
 * the second method removes all edges adjacent to the passed node.
 * The symmetry of the adjacency matrix is guaranteed by these methods.
 * </dd>
 * </p>
 *
 *
 * @author Jens Kanschik
 * @see Node
 * @see Edge
 * @see Map2D
 *
 */
public class GraphImpl extends ElementImpl implements Graph, GraphListener {

	/** Stores the set of groups.
	 * The key of the map is the group's name,
	 * the value the <code>Group</code> instances.
	 */
	protected Map groups;
	/** Stores the set of nodes.
	 * The key of the map is the node's name,
	 * the value the <code>Node</code> instances.
	 */
	protected Map nodes;
	/** Stores the set of edges.
	 * The key of the map is the edge's name,
	 * the value the <code>Edge</code> instances.
	 */
	protected Map	edges;
	/**Stores the edges in a two dimensional symmetric matrix, which is indexed
	 * by the nodes of the graph.
	 */
	protected Map2D adjacencyList;
	// stores the attributes for this graph
	private AttributeManager attributeManager;

	private GraphImpl	spanningTree;

	private EventListenerList listenerList;

	private GraphSystem graphSystem;

	protected GraphImpl(GraphSystemImpl gs) {
		super(GraphSystemImpl.createId());
		graphSystem = gs;
		nodes = new HashMap();
		adjacencyList = new NodeMap2D();
		edges = new HashMap();
		groups = new HashMap();
		spanningTree = null;
		attributeManager = new AttributeManagerImpl(this);
		listenerList = new EventListenerList();
		addGraphListener(this);
	}

	public void elementsAdded(GraphEvent ge) {
		spanningTree = null;
	}
	public void elementsRemoved(GraphEvent ge) {
		spanningTree = null;
	}
	public void structureChanged(GraphEvent ge) {
		spanningTree = null;
	}

	public void addGraphListener(GraphListener gll) {
		listenerList.add(GraphListener.class, gll);
	}
	public void removeGraphListener(GraphListener gll) {
		listenerList.remove(GraphListener.class, gll);
	}
	void fireElementsAdded(GraphEvent ge) {
		Object[] list = listenerList.getListenerList();
		for (int index = list.length - 2; index >= 0; index -= 2) {
			if (list[index] == GraphListener.class) {
				((GraphListener) list[index + 1]).elementsAdded(ge);
			}
		}
	}
	void fireElementsRemoved(GraphEvent ge) {
		Object[] list = listenerList.getListenerList();
		for (int index = list.length - 2; index >= 0; index -= 2) {
			if (list[index] == GraphListener.class) {
				((GraphListener) list[index + 1]).elementsRemoved(ge);
			}
		}
	}

	public GraphSystem getGraphSystem() {
		return graphSystem;
	}
	public synchronized void addElement(Element element) {
		if (element == null)
			return;
		if (element.getElementType() == Element.NODE_ELEMENT) {
			boolean isNew = (nodes.put(element.getName(), element) == null);
			if (isNew)
				fireElementsAdded(new GraphEventImpl(this, element)); // fire only if the node is really new.
			return;
		}
		if (element.getElementType() == Element.EDGE_ELEMENT) {
			Edge edge = (Edge) element;
			if (isConnected(edge.getSource(), edge.getTarget()))
				return;
			addElement(edge.getSource());
			addElement(edge.getTarget());
			adjacencyList.put(edge.getSource(), edge.getTarget(), edge);
			edges.put(edge.getName(), edge);
			fireElementsAdded(new GraphEventImpl(this, edge));
			return;
		}
		if (element.getElementType() == Element.GROUP_ELEMENT) {
			groups.put(element.getName(), (Group) element);
			return;
		}
	}
	public synchronized void removeElement(Element element) {
		if (element == null)
			return;
		if (element.getElementType() == Element.NODE_ELEMENT) {
			Collection edges = getEdges((Node) element);
			Object[] edgeArray = edges.toArray();
			for (int i = 0; i < edgeArray.length; i++)
				removeElement((Edge) edgeArray[i]);
			nodes.remove(element.getName());
			fireElementsRemoved(new GraphEventImpl(this, element));
			return;
		}
		if (element.getElementType() == Element.EDGE_ELEMENT) {
			adjacencyList.remove(((Edge) element).getSource(), ((Edge) element).getTarget());
			edges.remove(((Edge) element).getName());
			fireElementsRemoved(new GraphEventImpl(this, element));
			return;
		}
//		if (element.getElementType()==Element.GROUP_ELEMENT) {
//			removeGroup((Group) element);
//			return;
//		}
	}

	public synchronized void removeAll() {
		Object[] nodeArray = nodes.values().toArray();
		for (int i = 0; i < nodeArray.length; i++)
			removeElement((Node) nodeArray[i]);
	}
	/**
	 * Implements the method inherited from {@link Element} to return {@link #GRAPH_ELEMENT}.
	 * @return Always {@link #GRAPH_ELEMENT}.
	 */
	public int getElementType() {
		return GRAPH_ELEMENT;
	}


	/**
	 * Returns a unmodifiable <code>Collection</code> of all nodes of this graph.
	 * The result is unmodifiable because any deletions in the collection would leave the
	 * graph in an undefined state. To delete or add a node, the methods in this class
	 * must be used.
	 *
	 * @return The nodes of the graph.
	 */
	public Collection getNodes() {
		return Collections.unmodifiableCollection(nodes.values());
	}

	/**
	 * Returns a unmodifiable <code>Collection</code> of all groups of this graph.
	 * The result is unmodifiable because any deletions in the collection would leave the
	 * graph in an undefined state. To delete or add a group, the methods in this class
	 * must be used.
	 *
	 * @return The groups of the graph.
	 */
	public Collection getGroups() {
		return Collections.unmodifiableCollection(groups.values());
	}

	/**
	 * Returns a unmodifiable <code>Collection</code> of all edges of this graph.
	 * The result is unmodifiable because any deletions in the collection would leave the
	 * graph in an undefined state. To delete or add an edge, the methods in this class
	 * must be used.
	 *
	 * @return The edges of the graph.
	 */
	public Collection getEdges() {
		return Collections.unmodifiableCollection(edges.values());
	}
	/**
	 * Gets an unmodifiable collection of all edges adjacent to <code>node</code>.
	 * This method ignores the direction of the edges.
	 * @param node The node.
	 * @return The set of adjacent edges.
	 */
	public  Collection getEdges(Node node) {
		Map map = (Map) adjacencyList.get(node);
		if (map == null)
			return Collections.EMPTY_LIST;
		return Collections.unmodifiableCollection(((Map) adjacencyList.get(node)).values());
	}

	/**
	 * Insert a complete graph.
	 * @param graph The subgraph.
	 */
	public  void insertGraph(Graph graph) {
		if (graph == null)
			return;
		addEdges(graph.getEdges()); // since addEdges adds also the nodes, this is sufficient.
	}
	public Node createNode() {
		Node node = new NodeImpl(GraphSystemImpl.createId());
		addElement(node);
		return node;
	}
	public Node createNode(String name) throws GraphException {
		if (name == null)
			return createNode();
		Element e = getElement(name);
		if (e != null)
			throw new GraphException("Duplicate name \"" + name  + "\"for element.");
		Node node = new NodeImpl(name);
		addElement(node);
		return node;
	}
	public Edge createEdge(Node source, Node target) {
		if (source == null || target == null || source.equals(target))
			return null;
		Edge edge = new EdgeImpl(GraphSystemImpl.createId(), source, target);
		addElement(edge);
		return edge;
	}
	public Edge createEdge(String name, Node source, Node target) throws GraphException {
		if (name == null)
			return createEdge(source, target);
		Element e = getElement(name);
		if (e != null)
			throw new GraphException("Duplicate name \"" + name  + "\"for element.");
		Edge edge = new EdgeImpl(name, source, target);
		addElement(edge);
		return edge;
	}
	public Group createGroup() {
		try {
			return createGroup(GraphSystemImpl.createId());
		} catch (GraphException ge) {
			return null;
		}
	}
	public Group createGroup(String name) throws GraphException {
		if (name == null)
			return createGroup();
		Element e = getElement(name);
		if (e != null)
			throw new GraphException("Duplicate name \"" + name  + "\"for element.");
		Group group = new GroupImpl(name);
		addElement(group);
		return group;
	}
	public Element getElement(String name) {
		Element e;
		e = (Element) nodes.get(name);
		if (e != null)
			return e;
		e = (Element) edges.get(name);
		if (e != null)
			return e;
		e = (Element) groups.get(name);
		if (e != null)
			return e;
		return null;
	}
	/**
	 * Adds a collection of Nodes.
	 * @param nodes
	 */
	public void addNodes(Collection nodes) {
		nodes.addAll(nodes);
	}
	/**
	 * Adds a collection of edges. The method ensures that the source and target
	 * of each of the edges are also contained in the graph.
	 * @param edges A collection of the new edges.
	 */
	public void addEdges(Collection edges) {
		for (Iterator i = edges.iterator(); i.hasNext();)
			addElement((Edge) i.next());
	}

	/**
	 * Returns true if two given nodes are connected. It is not important
	 * which of the two nodes is the source and which is the target.

	 * @param n1 First of the two nodes
	 * @param n2 The other node.
	 * @return True if there exists an edge connecting both nodes, ignoring direction.
	 */
	public boolean isConnected(Node n1, Node n2) {
		return adjacencyList.containsKey(n1, n2);
	}

	/**
	 * Returns a spanning tree of the graph. The spanning tree is a new instance
	 * of <code>Graph</code>, the nodes of the original graph and the spanning tree
	 * are shared and the edges are either shared or the spanning tree uses
	 * the wrapper {@link ReverseEdge}. The root of the spanning tree is the node
	 * that is returned by {@link #getMinInDegree()}, i.e. a node with minimal
	 * incoming degree. The spanning tree is directed, i.e. the direction
	 * of all edges is as it is expected for trees.
	 * The result is stored in a private variable which is returned
	 * if the method is called a second time.
	 *
	 * @return A spanning tree of the graph.
	 */
	public synchronized Graph getSpanningTree() {
		if (spanningTree == null) {
			Node root = getMinInDegree();
			getSpanningTree(root);
		}
		return spanningTree;
	}
	public synchronized Graph getSpanningTree(Node root) {
		if (spanningTree != null) {
			AttributeManager amgr = spanningTree.getAttributeManager();
			Node oldRoot = (Node) amgr.getAttribute(AttributeManager.GRAPH_ROOT, spanningTree);
			if (root == oldRoot)
				return spanningTree;
		}
		spanningTree = new GraphImpl((GraphSystemImpl) getGraphSystem());
		spanningTree.addElement(root);
		AttributeManager amgr = spanningTree.getAttributeManager();
		amgr.setAttribute(AttributeManager.GRAPH_ROOT, spanningTree, root);
		Set nodeSet = new HashSet();
		nodeSet.add(root);
		while (!nodeSet.isEmpty()) {
			Node node = (Node) nodeSet.iterator().next();
			nodeSet.remove(node);
			for (Iterator i = getEdges(node).iterator(); i.hasNext();) {
				Edge edge = (Edge) i.next();
				Node newNode = edge.getOtherNode(node);
				if (spanningTree.getElement(newNode.getName()) == null) {
					if (newNode == edge.getSource())
						edge = new ReverseEdge("reverse" + edge.getName(), edge);
					spanningTree.addElement(newNode);
					spanningTree.addElement(edge);
					nodeSet.add(newNode);
				}
			}
		}
		return spanningTree;
	}
	/**
	 * Determines a node with the minimal incoming degree. Often there are several
	 * such nodes, this method returns any of these, it is not guaranteed that
	 * two seperate calls return the same node.
	 *
	 * @return A node with the minimal incoming degree.
	 */
	public  Node getMinInDegree() {
		Iterator i = getNodes().iterator();
		Node minNode = null; // the node with the smallest incoming degree so far.
		Node next = null; // the next node to be tested.
		int minIncomingDegree = -1; // the current in degree
		while (i.hasNext()) {
			next = (Node) i.next();
			int inDegree = 0;
			for (Iterator iter = getEdges(next).iterator(); iter.hasNext();) {
				Edge edge = (Edge) iter.next();
				if (edge.getTarget().equals(next))
					inDegree++;
			}
			if (minIncomingDegree < 0
			 || minIncomingDegree > inDegree) {
					minNode = next; // next is either the first node or better than all before
					minIncomingDegree = inDegree;
			}
			if (minIncomingDegree == 0)
				break; // the degree can not be smaller than 0, we can stop here.
		}
		return minNode;
	}
	/**
	 * Determines a node with the maximal outgoing degree. Often there are several
	 * such nodes, this method returns any of these, it is not guaranteed that
	 * two seperate calls return the same node.
	 *
	 * @return A node with the maximal outgoing degree.
	 */
	public  Node getMaxOutDegree() {
		Iterator i = getNodes().iterator();
		Node maxNode = null;
		Node next = null;
		int maxOutgoingDegree = -1;
		while (i.hasNext()) {
			next = (Node) i.next();
			int outDegree = 0;
			for (Iterator iter = getEdges(next).iterator(); iter.hasNext();) {
				Edge edge = (Edge) iter.next();
				if (edge.getSource().equals(next))
					outDegree++;
			}
			if (maxOutgoingDegree < outDegree) {
				maxNode = next;
				maxOutgoingDegree = outDegree;
			}
		}
		return maxNode;
	}
	protected  void createSpanningTreeDFS(Node root) {
		// check all outgoing nodes, whether they are already in the spanning tree or not.
		// if not, add them.
		for (Iterator i = getEdges(root).iterator(); i.hasNext();) {
			Edge edge = (Edge) i.next();
			Node newNode = edge.getOtherNode(root);
			if (spanningTree.getElement(newNode.getName()) != null) {
				if (newNode == edge.getSource())
					edge = new ReverseEdge("reverse" + edge.getName(), edge);
				spanningTree.addElement(newNode);
				spanningTree.addElement(edge);
				createSpanningTreeDFS(newNode);
			}
		}
	}

	protected  void processBFS(Set nodeSet) {
		Node node = (Node) nodeSet.iterator().next();
		nodeSet.remove(node);
		for (Iterator i = getEdges(node).iterator(); i.hasNext();) {
			Edge edge = (Edge) i.next();
			Node newNode = edge.getOtherNode(node);
			if (spanningTree.getElement(newNode.getName()) != null) {
				if (newNode == edge.getSource())
					edge = new ReverseEdge("reverse" + edge.getName(), edge);
				spanningTree.addElement(newNode);
				spanningTree.addElement(edge);
				nodeSet.add(newNode);
			}
		}
	}

	/**
	 * Sets the {@link AttributeManager} for this graph.
	 * @param attrman The new <code>AttributeManager</code>.
	 */
	public void setAttributeManager(AttributeManager attrman) {
		attributeManager = attrman;
	}
	/**
	 * Gets the {@link AttributeManager} for this graph.
	 * @return The <code>AttributeManager</code> of this graph.
	 */
	public AttributeManager getAttributeManager() {
		return attributeManager;
	}

	public Object clone() {
		return null;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
//		String sep = System.getProperty("lineseperator");
		String sep = "\n";
		buf.append("hypergraph.graph.GraphImpl" + sep);
		buf.append("nodes : " + sep);
		for (Iterator iter = getNodes().iterator(); iter.hasNext();)
			buf.append(iter.next().toString() + sep);
		buf.append("edges : " + sep);
		for (Iterator iter = getEdges().iterator(); iter.hasNext();)
			buf.append(iter.next().toString() + sep);

		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see hypergraph.graph.ElementImpl#getLabel()
	 */
	public String getLabel() {
		return null;
	}

	public class ReverseEdge extends ElementImpl implements Edge {
		private Edge underlying;
		/**
		 * Instantiates a new <code>ReverseEdge</code> based on the passed edge.
		 * @param underlying The underlying edge.
		 */
		public ReverseEdge(String name, Edge underlying) {
			super(name);
			this.underlying = underlying;
		}
		/**
		 * Returns the name of the edge. The name can be used as an identifier.
		 * @return The name of the element.
		 */
		public String getName() {
			return underlying.getName();
		}
//		/**
//		 * Returns the name of the edge. The name can be used as an identifier.
//		 * See also {@link #getName()}.
//		 * @param name The name of the edge.
//		 */
//		public void setName(String name) {
//			underlying.setName(name);
//		}
		/**
		 * Returns the group of the edge.
		 * See also {@link #setGroup(Group)}.
		 * @return The group of the edge.
		 */
		public Group getGroup() {
			return underlying.getGroup();
		}
		/**
		 * Sets the group of the edge.
		 * See also {@link #getGroup()}.
		 */
		public void setGroup(Group group) {
			underlying.setGroup(group);
		}

		/**
		 * Returns the source of this edge, i.e. the target of the underlying edge.
		 *
		 * @return The source of the edge.
		 */
		public Node getSource() {
			return underlying.getTarget();
		}
		/**
		 * Returns the target of this edge, i.e. the source of the underlying edge.
		 * @return The target of the edge.
		 */
		public Node getTarget() {
			return underlying.getSource();
		}
		public Node getOtherNode(Node node) {
			return underlying.getOtherNode(node);
		}
		public void reverse() {
			underlying.reverse();
		}
	    /** {@inheritDoc} */
	    public void setDirected(boolean directed) {
	    	underlying.setDirected(directed);
	    }
	    /** {@inheritDoc} */
	    public boolean isDirected() {
	    	return underlying.isDirected();
	    }
		public String getLabel() { return underlying.getLabel(); }
		public void setLabel(String label) { underlying.setLabel(label); }

		/**
		 * Overwrites the abstract method defined in <code>Element</code> to return <code>EDGE_ELEMENT</code>.
		 * @return Always <code>EDGE_ELEMENT</code>.
		 */
		public int getElementType() {
			return EDGE_ELEMENT;
		}
		public String toString() {
			return "[ ReverseEdge : \n" +
					"  underlying Edge : " + underlying + " ]\n";
		}
	}
}
