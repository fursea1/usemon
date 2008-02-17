package com.usemon.live;

import hypergraph.graph.GraphSystemImpl;
import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphException;
import hypergraph.graphApi.GraphSystem;
import hypergraph.graphApi.GraphSystemFactory;
import hypergraph.graphApi.Group;
import hypergraph.visualnet.ArrowLineRenderer;
import hypergraph.visualnet.GenericMDSLayout;
import hypergraph.visualnet.GraphPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DependencyPanel extends GraphPanel implements Runnable {
	private static final long serialVersionUID = -853947217747054025L;
	private static Element edgeGroup;
	private Thread housekeepingThread;

	public DependencyPanel() throws GraphException {
		this(createEmptyGraph());
		housekeepingThread = new Thread(this, "Usemon - DependencyPanel Housekeeping Thread");
		housekeepingThread.setDaemon(true);
		housekeepingThread.start();
	}

	private static Graph createEmptyGraph() throws GraphException {
		GraphSystem graphSystem = GraphSystemFactory.createGraphSystem("hypergraph.graph.GraphSystemImpl", null);
		Graph graph = graphSystem.createGraph();

		edgeGroup = graph.createGroup("Edge Group");
		graph.getAttributeManager().setAttribute(GraphPanel.EDGE_TEXTCOLOR, edgeGroup, Color.darkGray);
		graph.getAttributeManager().setAttribute(GraphPanel.EDGE_LINECOLOR, edgeGroup, Color.lightGray);

		Group[] groups = new Group[1];
		groups[0] = graph.createGroup("Unknown");
		graph.getAttributeManager().setAttribute(GraphPanel.NODE_FOREGROUND, groups[0], Color.black);

		return graph;
	}

	public DependencyPanel(Graph graph) {
		super(graph);
		GenericMDSLayout layout = new GenericMDSLayout();
		setGraphLayout(layout);
		getEdgeRenderer().setLabelVisible(true);
		setLineRenderer(new ArrowLineRenderer());
		setNodeRenderer(new UsemonNodeRenderer());
		getViewMatrix().setToIdentity();
		Dimension d = new Dimension(800, 600);
		setPreferredSize(d);
		setMinimumSize(d);
		setSize(d);
	}

	@SuppressWarnings("unchecked")
	public void addInvocation(String source, String target, int count) throws GraphException {
		System.out.println(source+" -> "+target);
		InstanceNode s = findNode(source);
		InstanceNode t = findNode(target);

		s.touch();
		t.touch();

		if(!getGraph().isConnected(s, t)) {
			Edge e = getGraph().createEdge(GraphSystemImpl.createId(), s, t);
			e.setLabel(String.valueOf(count));
			e.setDirected(true);
		} else {
			Collection<Edge> edges = getGraph().getEdges(s);
			for (Edge e : edges) {
				if(e.getTarget()==t) {
					e.setLabel(String.valueOf(Integer.parseInt(e.getLabel())+count));
				}
			}
		}
	}
	
	private InstanceNode findNode(String name) throws GraphException {
		InstanceNode n = (InstanceNode) getGraph().getElement(name);
		if(n==null) {
			n = new InstanceNode(name);
			n.setLabel(name);
			getGraph().addElement(n);
		}
		return n;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		while(true) {
			List<InstanceNode> expiredList = new LinkedList<InstanceNode>();
			Collection<InstanceNode> nodes = (Collection<InstanceNode>) getGraph().getNodes();
			for(InstanceNode node : nodes) {
				if(node.isExpired()) {
					expiredList.add(node);
				}
			}
			for(InstanceNode node : expiredList) {
				System.out.println("Expired: "+node.getLabel());
				getGraph().removeElement(node);
			}
			repaint();			
			try {
				Thread.sleep(2500);
			} catch(InterruptedException e) {
				break;
			}
		}
	}

}