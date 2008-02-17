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

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphException;
import hypergraph.graphApi.Node;
import hypergraph.graphApi.io.CSSColourParser;
import hypergraph.hyperbolic.ModelPanel;
import hypergraph.hyperbolic.ModelPoint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * @author Jens Kanschik
 * 
 * To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public class GraphPanel extends ModelPanel implements MouseListener, GraphLayoutListener, GraphSelectionListener {

	/**
	 * The attribute name for foreground colour of nodes.
	 */
	public static final String NODE_FOREGROUND = "node.color";
	/**
	 * The attribute name for background colour of nodes.
	 */
	public static final String NODE_BACKGROUND = "node.bkcolor";
	/**
	 * The attribute name for an icon of a node.
	 */
	public static final String NODE_ICON = "node.icon";
	/**
	 * The attribute name for foreground colour of edges.
	 */
	public static final String EDGE_TEXTCOLOR = "edge.textcolor";
	public static final String EDGE_LINECOLOR = "edge.linecolor";
	public static final String EDGE_LINEWIDTH = "edge.linewidth";
	public static final String EDGE_STROKE = "edge.stroke";

	public static final String NODE_EXPANDED = "NODE_EXPANDED";
	public final ExpandAction expandAction = new ExpandAction(true);
	public final ExpandAction shrinkAction = new ExpandAction(false);

	Graph graph;
	GraphLayout graphLayout;
	GraphSelectionModel selectionModel;

	private Image logo;
	private Image smallLogo;

	private Element hoverElement;
	private Node lastMouseClickNode;

	private NodeRenderer nodeRenderer;
	private EdgeRenderer edgeRenderer;

	public GraphPanel(Graph graph) {
		super();
		setGraph(graph);
		// initVisibleGraph();
		createGraphLayout();
		createGraphSelectionModel();
		selectionModel.addSelectionEventListener(this);
		setNodeRenderer(new DefaultNodeRenderer());
		setEdgeRenderer(new DefaultEdgeRenderer());
		initDefaultAttributes();

		// read the logo for small and large applets :
		URL logoUrl = this.getClass().getResource("/hypergraph/visualnet/logo.png");
		if (logoUrl != null)
			logo = Toolkit.getDefaultToolkit().createImage(logoUrl);
		logoUrl = this.getClass().getResource("/hypergraph/visualnet/logo_small.png");
		if (logoUrl != null)
			smallLogo = Toolkit.getDefaultToolkit().createImage(logoUrl);
	}

	protected void initDefaultAttributes() {
		AttributeManager amgr = graph.getAttributeManager();

		String colorString = getPropertyManager().getString("hypergraph.hyperbolic.line.color");
		Color color = CSSColourParser.stringToColor(colorString);
		if (color == null || colorString == null)
			color = Color.LIGHT_GRAY;
		amgr.setAttribute(EDGE_LINECOLOR, graph, color);
		amgr.setAttribute(EDGE_STROKE, graph, null);
		amgr.setAttribute(EDGE_LINEWIDTH, graph, new Float(1));
	}

	// public void initVisibleGraph() {
	// visibleGraph = graph;
	// // visibleGraph = graph.getSpanningTree();
	// // visibleGraph = new Graph();
	// // Graph spanningTree = graph.getSpanningTree();
	// // AttributeManager amgr = spanningTree.getAttributeManager();
	// // Node root = (Node) amgr.getAttribute(AttributeManager.GRAPH_ROOT,spanningTree);
	// // visibleGraph.addNode(root);
	// // if (graph.getOutgoingEdges(root).size() > 0)
	// // amgr.setAttribute(NODE_EXPANDED,root,expandAction);
	// // expandAction.actionPerformed(new ActionEvent(root,0,""));
	// }
	public void createGraphLayout() {
		GraphLayout layout;
		try {
			Class layoutClass = getPropertyManager().getClass("hypergraph.visualnet.layout.class");
			layout = (GraphLayout) layoutClass.newInstance();
		} catch (Exception e) {
			layout = new TreeLayout(getGraph(), getModel(), getPropertyManager());
			// layout = new GenericMDSLayout();
		}
		setGraphLayout(layout);
	}

	public void loadProperties(InputStream is) throws IOException {
		super.loadProperties(is);
		createGraphLayout();
		initDefaultAttributes();
	}

	public void setGraphLayout(GraphLayout layout) {
		if (graphLayout != null) {
			graphLayout.getGraphLayoutModel().removeLayoutEventListener(this);
		}
		graphLayout = layout;
		graphLayout.setGraph(graph);
		graphLayout.setProperties(getPropertyManager());
		graphLayout.setModel(getModel());
		GraphLayoutModel glm = new DefaultGraphLayoutModel();
		graphLayout.setGraphLayoutModel(glm);
		graphLayout.layout();
		graphLayout.getGraphLayoutModel().addLayoutEventListener(this);
	}

	public GraphLayout getGraphLayout() {
		return graphLayout;
	}

	void createGraphSelectionModel() {
		setGraphSelectionModel(new DefaultGraphSelectionModel(getGraph()));
	}

	public void setGraphSelectionModel(GraphSelectionModel gsm) {
		selectionModel = gsm;
	}

	public GraphSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setGraph(Graph g) {
		graph = g;
		if (graphLayout != null)
			graphLayout.setGraph(graph);
	}

	public Graph getGraph() {
		return graph;
	}

	public void valueChanged(GraphLayoutEvent e) {
		repaint();
	}

	protected void checkLayout() {
		if (!getGraphLayout().isValid())
			getGraphLayout().layout();
	}

	public Iterator getVisibleNodeIterator() {
		return graphLayout.getGraph().getNodes().iterator();
	}

	public Iterator getVisibleEdgeIterator() {
		return graphLayout.getGraph().getEdges().iterator();
	}

	public void paint(Graphics g) {
		synchronized (graph) {
			checkLayout();
			Graphics2D g2 = (Graphics2D) g;

			if (getUI().isDraft()) {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			} else {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
			super.paint(g);
			GraphLayoutModel glm;
			glm = getGraphLayout().getGraphLayoutModel();

			for (Iterator i = getVisibleEdgeIterator(); i.hasNext();) {
				Edge edge = (Edge) i.next();
				if (edge != hoverElement) {
					edgeRenderer.configure(this, edge);
					paintRenderer(g, edgeRenderer);
				}
			}
			for (Iterator i = getVisibleNodeIterator(); i.hasNext();) {
				Node node = (Node) i.next();
				if (node != hoverElement) {
					ModelPoint mp = glm.getNodePosition(node);
					nodeRenderer.configure(this, mp, node);
					paintRenderer(g, nodeRenderer);
				}
			}
			if (hoverElement != null) {
				if (hoverElement.getElementType() == Element.EDGE_ELEMENT) {
					edgeRenderer.configure(this, (Edge) hoverElement);
					paintRenderer(g, edgeRenderer);
				}
				if (hoverElement.getElementType() == Element.NODE_ELEMENT) {
					ModelPoint mp = glm.getNodePosition((Node) hoverElement);
					nodeRenderer.configure(this, mp, (Node) hoverElement);
					paintRenderer(g, nodeRenderer);
				}
			}
		}
		if (getWidth() > 300 && getHeight() > 300) {
			if (logo != null)
				g.drawImage(logo, getWidth() - logo.getWidth(this), getHeight() - logo.getHeight(this), this);
		} else {
			if (smallLogo != null)
				g.drawImage(smallLogo, getWidth() - smallLogo.getWidth(this), getHeight() - smallLogo.getHeight(this), this);
		}
	}

	/**
	 * @return
	 */
	public Element getHoverElement() {
		return hoverElement;
	}

	protected void setHoverElement(Element element, boolean repaint) {
		hoverElement = element;
		if (repaint)
			repaint();
	}

	/**
	 * Sets the standard version of the logo.
	 * 
	 * @param l
	 *            The logo.
	 */
	public void setLogo(Image l) {
		logo = l;
	}

	/**
	 * Sets a small version of the logo. This is shown if the graph panel is so small that the default logo would overlap with the data that is shown.
	 * 
	 * @param l
	 *            The small logo.
	 */
	public void setSmallLogo(Image l) {
		smallLogo = l;
	}

	/**
	 * Returns <code>true</code> if the point is contained in the logo.
	 * 
	 * @param p
	 *            A location within the graph panel, the coordinates are with respect to the graph panel.
	 * @return <code>True</code> if the specified point on in the logo.
	 */
	protected boolean isOnLogo(Point p) {
		if (logo != null)
			return p.getX() > getWidth() - logo.getWidth(this) && p.getY() > getHeight() - logo.getHeight(this);
		else
			return false;
	}

	/**
	 * This method is called when the user clicked on the logo.
	 * 
	 * @param event
	 *            The mouse event that repesents the mouse click on the logo.
	 */
	protected void logoClicked(MouseEvent event) {
	}

	public boolean hasExpander(Node node) {
		if (!getGraphLayout().isExpandingEnabled())
			return false;
		AttributeManager amgr = graphLayout.getGraph().getAttributeManager();
		ExpandAction action = (ExpandAction) amgr.getAttribute(NODE_EXPANDED, node);
		return action != null;
	}

	public boolean isExpanded(Node node) {
		AttributeManager amgr = graphLayout.getGraph().getAttributeManager();
		ExpandAction action = (ExpandAction) amgr.getAttribute(NODE_EXPANDED, node);
		if (action == null)
			return false;
		return action == shrinkAction;
	}

	public void expandNode(Node node) {
		AttributeManager amgr = graphLayout.getGraph().getAttributeManager();
		// get all outgoing edges in the original (!) graph.
		Iterator iter = graph.getEdges(node).iterator();
		while (iter.hasNext()) {
			Edge edge = (Edge) iter.next();
			if (edge.getSource() != node)
				continue; // only outgoing edges
			try {
				graphLayout.getGraph().addElement(edge);
			} catch (GraphException ge) {
			}
			Node target = edge.getOtherNode(node);
			if (graph.getEdges(target).size() != 0)
				amgr.setAttribute(NODE_EXPANDED, target, expandAction);
		}
		// Iterator iter = graph.getOutgoingEdgesIterator(node);
		// while (iter.hasNext()) {
		// Edge edge = (Edge) iter.next();
		// visibleGraph.addEdge(edge);
		// Node target = edge.getOtherNode(node);
		// if (graph.getOutgoingEdges(target).size() != 0)
		// amgr.setAttribute(NODE_EXPANDED,target,expandAction);
		// }
		amgr.setAttribute(NODE_EXPANDED, node, shrinkAction);
	}

	public void shrinkNode(Node node) {
		AttributeManager amgr = graphLayout.getGraph().getAttributeManager();
		// get all outgoing edges in the original (!) graph.
		Iterator iter = graph.getEdges(node).iterator();
		while (iter.hasNext()) {
			Edge edge = (Edge) iter.next();
			if (edge.getSource() != node)
				continue; // only outgoing edges
			Node target = edge.getOtherNode(node);
			graphLayout.getGraph().removeElement(target);
		}
		amgr.setAttribute(NODE_EXPANDED, node, expandAction);
	}

	/**
	 * Centers the given node.
	 * 
	 * @param node
	 *            The node that has to be moved to the center.
	 */
	public void centerNode(Node node) {
		GraphLayoutModel glm = getGraphLayout().getGraphLayoutModel();
		getUI().center(glm.getNodePosition(node), this);
	}

	/**
	 * Returns the element at the position <code>point</code>. This can be either a node or an edge. If a node and an edge are at the same position, the node is returned.
	 * 
	 * @param point
	 *            The position of the element
	 * @return The element that is located at the position <code>point</code>.
	 */
	public Element getElement(Point point) {
		GraphLayoutModel glm = getGraphLayout().getGraphLayoutModel();
		// make sure that neither the graph nor the graph layout are changed while getting the nearest node.
		synchronized (graph) {
			synchronized (glm) {
				// check nodes first
				NodeRenderer nr = getNodeRenderer();
				Point p = new Point();
				for (Iterator i = getVisibleNodeIterator(); i.hasNext();) {
					Node node = (Node) i.next();
					nr.configure(this, glm.getNodePosition(node), node);
					Component c = nr.getComponent();
					p.setLocation(point.getX() - c.getX(), point.getY() - c.getY());
					if (c.contains(p))
						return node;
				}
				// now check edges
				for (Iterator i = getVisibleEdgeIterator(); i.hasNext();) {
					Edge edge = (Edge) i.next();
					ModelPoint mp1 = glm.getNodePosition(edge.getSource());
					ModelPoint mp2 = glm.getNodePosition(edge.getTarget());
					ModelPoint mp3 = unProject(point);
					if (mp3 != null && getModel().getDistance(mp3, mp1, mp2, true, true) < 0.025) {
						return edge;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Is called when the user clicked on a node. The default implementation centers the node if it is clicked once.
	 */
	public void nodeClicked(int clickCount, Node node) {
		if (clickCount == 1) {
			lastMouseClickNode = node;
			if (lastMouseClickNode != null)
				centerNode(lastMouseClickNode);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (isOnLogo(e.getPoint())) {
			logoClicked(e);
			return;
		}
		setHoverElement(null, false);
		Element element = getElement(e.getPoint());
		if (element != null && element.getElementType() == Element.NODE_ELEMENT) {
			if (e.getClickCount() == 1) {
				nodeClicked(1, (Node) element);
				return;
			}
			if (e.getClickCount() == 2 && lastMouseClickNode != null) {
				nodeClicked(2, lastMouseClickNode);
				return;
			}
			NodeRenderer nr = getNodeRenderer();
			GraphLayoutModel glm = getGraphLayout().getGraphLayoutModel();
			nr.configure(this, glm.getNodePosition((Node) element), (Node) element);
		}

		// if (node != null) {
		// if (!e.isShiftDown())
		// getSelectionModel().clearSelection();
		// if ( getSelectionModel().isElementSelected(node) )
		// getSelectionModel().removeSelectionElement(node);
		// else
		// getSelectionModel().addSelectionElement(node);
		// return;
		// }
		// getSelectionModel().clearSelection();
		super.mouseClicked(e);
	}

	public void mouseMoved(MouseEvent e) {
		Element element = getElement(e.getPoint());
		setHoverElement(element, true);
	}

	public void valueChanged(GraphSelectionEvent e) {
		Iterator i = getSelectionModel().getSelectionElementIterator();
		repaint();
		// if (i.hasNext() )
		// centerNode( (Node) i.next() );
	}

	public class ExpandAction implements ActionListener {
		private boolean expand;

		public ExpandAction(boolean expand) {
			this.expand = expand;
		}

		public void actionPerformed(ActionEvent e) {
			Node node = (Node) e.getSource();
			if (expand)
				expandNode(node);
			else
				shrinkNode(node);
			repaint();
		}
	}

	/**
	 * @return
	 */
	public EdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}

	/**
	 * @param renderer
	 */
	public void setEdgeRenderer(EdgeRenderer renderer) {
		edgeRenderer = renderer;
	}

	public void setNodeRenderer(NodeRenderer nodeRenderer) {
		this.nodeRenderer = nodeRenderer;
	}

	public NodeRenderer getNodeRenderer() {
		return nodeRenderer;
	}

}
