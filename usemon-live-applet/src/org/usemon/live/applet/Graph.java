package org.usemon.live.applet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PFont;
import traer.animation.Smoother3D;
import traer.physics.Particle;
import traer.physics.ParticleSystem;

public class Graph {
	public static final float EDGE_LENGTH = 20*3;
	private static final float EDGE_STRENGTH = 0.2f;
	private static final float MIN_DISTANCE = 20;
	private static final float SPACER_STRENGTH = 1000;
	public static final int NODE_TTL = 1000*60*10; // 10 min TTL

	private Smoother3D centroid;	
	private ParticleSystem system;
	private List nodes;
	private List edges;
	private Map idNodes;
	private Map idEdges;
	private PApplet ctx;
	private PFont font;
	private Particle mouse;
	private Node mouseSelectedNode;
	private Particle blackHole;
	private int manualModeTimer;
	private boolean attentionMode;
	
	public Graph(PApplet ctx) {
		this.ctx = ctx;
		font = ctx.loadFont("SegoeUI-12.vlw");
		system = new ParticleSystem(0, 0.5f);
		centroid = new Smoother3D(0, 0, 5.0f, 0.8f);
		nodes = new LinkedList();
		edges = new LinkedList();
		idNodes = new HashMap();
		idEdges = new HashMap();
		mouse = system.makeParticle();
		mouse.makeFixed();
		mouseSelectedNode = null;
		blackHole = system.makeParticle();
		blackHole.makeFixed();
		manualModeTimer = 0;
		attentionMode = false;
	}

	public Node addNode(Node node) {
		Particle p = system.makeParticle(1.0f, ctx.random(-1, 1), ctx.random(-1, 1), 0.0f);
		system.makeSpring(p, blackHole, 0.05f, 0.4f , node.getDesiredLengthFromCentrum());
		node.setParticle(p);
		idNodes.put(node.getUuid(), node);
		nodes.add(node);
		return node;
	}
	
	public Edge addEdge(Edge edge) {
		// Add attraction between nodes
		for(int n=0;n<nodes.size();n++) {
			Node other = (Node) nodes.get(n);
			if(edge.getSource()!=other && edge.getSource()!=edge.getTarget()) {
				system.makeAttraction(edge.getSource().getParticle(), other.getParticle(), -SPACER_STRENGTH, MIN_DISTANCE);
			}
		}
		// Add spring between nodes
		edge.setSpring(system.makeSpring(edge.getSource().getParticle(), edge.getTarget().getParticle(), EDGE_STRENGTH, EDGE_STRENGTH, EDGE_LENGTH));
		// Move source particle into position
		//Particle p = edge.source.getParticle();
		//p.moveBy(ctx.random(-1, 1), ctx.random(-1, 1), 0);
		//source.particle.moveTo(target.particle.position().x() + random(-1, 1), target.particle.position().y() + random(-1, 1), 0);
		idEdges.put(makeEdgeKey(edge.source.getUuid(), edge.target.getUuid()), edge);
		edges.add(edge);
		return edge;
	}
		
	public void removeNode(Node node) {
		// Remove edges
		List removableEdges = new LinkedList();
		for(int n=0;n<edges.size();n++) {
			Edge edge = (Edge) edges.get(n);
			if(edge.contains(node)) {
				idEdges.remove(makeEdgeKey(edge.source.getUuid(), edge.target.getUuid()));
				removableEdges.add(edge);				
			}
		}
		for(int n=0;n<removableEdges.size();n++) {
			edges.remove(removableEdges.get(n));
		}
		removableEdges.clear();
		// Remove particle and node
		node.getParticle().kill();
		idNodes.remove(node.getUuid());
		nodes.remove(node);
	}
	
	public void draw() {
		ctx.textFont(font);
		ctx.textAlign(PApplet.CENTER);
		ctx.ellipseMode(PApplet.CENTER);
		ctx.rectMode(PApplet.CENTER);
		ctx.background(0, 0, 0);
		// Draw edges and nodes
		for(int n=0;n<edges.size();n++) {
			((Edge) edges.get(n)).draw(ctx);
		}
		for(int n=0;n<nodes.size();n++) {
			((Node) nodes.get(n)).draw(ctx);
		}
		ctx.stroke(255, 0, 0);
		ctx.ellipse(mouse.position().x(), mouse.position().y(), 10, 10);
	}

	public void zoom(float delta) {
		centroid.setZTarget(centroid.getZTarget()+delta);
		manualModeTimer = getManualModeDelay();
	}

	public void tick() {
		if(nodes.size()>0) {
			removeDeadNodes();
			system.tick();
			updateNodesAndEdges();
			if(nodes.size()>1 && ((manualModeTimer>0)?manualModeTimer--:0)==0 && !attentionMode) {
				updateCentroid();
			}
			centroid.tick();
			ctx.translate(ctx.width/2, ctx.height/2);
			ctx.scale(centroid.z());
			ctx.translate(-centroid.x(), -centroid.y());
			mouse.moveTo(screenToWorldX(ctx.mouseX), screenToWorldY(ctx.mouseY), 0);
			// Pan to the clicked spot
			if(ctx.mousePressed && mouseSelectedNode==null) {
				centroid.setTarget(screenToWorldX(ctx.mouseX), screenToWorldY(ctx.mouseY), centroid.getZTarget());
				manualModeTimer = getManualModeDelay();
			}
		}
	}

	public float screenToWorldX(int screenX) {
		return (screenX-(ctx.width/2))/centroid.z()+centroid.x();
	}

	public float screenToWorldY(int screenY) {
		return (screenY-(ctx.height/2))/centroid.z()+centroid.y();
	}

	public int size() {
		return nodes.size();
	}
	
	public Node getNode(int index) {
		return (Node) nodes.get(index);
	}
	
	public Node getNode(String id) {
		return (Node) idNodes.get(id);
	}
	
	public boolean containsNode(String id) {
		return idNodes.containsKey(id);
	}
	
	public boolean containsEdge(String sourceId, String targetId) {
		return idEdges.containsKey(makeEdgeKey(sourceId, targetId));
	}
	
	public Edge getEdge(String sourceId, String targetId) {
		return (Edge) idEdges.get(makeEdgeKey(sourceId, targetId));
	}

	private String makeEdgeKey(String sourceId, String targetId) {
		return sourceId+"->"+targetId;
	}

	private void updateNodesAndEdges() {
		attentionMode = false;
		for(int n=0;n<edges.size();n++) {
			Edge edge = (Edge) edges.get(n);
			edge.tick();
			// Pan to where the action is if we are in automatic pan and zoom mode
			if(manualModeTimer==0) {
				if(edge.isAttention()) {
					float x = (edge.source.getParticle().position().x() + edge.target.getParticle().position().x()) / 2;
					float y = (edge.source.getParticle().position().y() + edge.target.getParticle().position().y()) / 2;
					centroid.setTarget(x, y, 1);
					attentionMode = true;
				}
			}
		}
		for(int n=0;n<nodes.size();n++) {
			Node node = (Node) nodes.get(n);
			// Grab hold of the node under the mouse if the button is pressed
			if(ctx.mousePressed) {
				if(mouse.position().distanceTo(node.getParticle().position())<(node.getRadius()*2/3)) { // Half the radius is an exact match
					mouseSelectedNode = node;
				}
			} else { // Release the node if the button is not pressed
				mouseSelectedNode = null;
			}
			node.tick();
		}
		// Move the selected node to the same position as the mouse to allow dragging when mouse is pressed
		if(mouseSelectedNode!=null) {
			mouseSelectedNode.getParticle().position().set(mouse.position());
			manualModeTimer = getManualModeDelay();
		}
	}
	
	public Node getMouseSelectedNode() {
		return mouseSelectedNode;
	}

	private int getManualModeDelay() {
		return (int) ctx.frameRate*5; // Stay in manual pan mode for 5 times frameRate ticks (equal 5 secs)
	}

	private void removeDeadNodes() {
		List deadNodes = new LinkedList();
		for(int n=0;n<nodes.size();n++) {
			Node node = (Node) nodes.get(n);
			if(node.isDead()) {
				deadNodes.add(node);
			}
		}
		for(int n=0;n<deadNodes.size();n++) {
			removeNode((Node) deadNodes.get(n));
		}
	}

	private void updateCentroid() {
		float xMax = Float.NEGATIVE_INFINITY;
		float xMin = Float.POSITIVE_INFINITY;
		float yMin = Float.POSITIVE_INFINITY;
		float yMax = Float.NEGATIVE_INFINITY;
		for(int n=0;n<nodes.size();n++) {
			Particle p = ((Node)nodes.get(n)).getParticle();
//		for(int n=0;n<system.numberOfParticles();n++) {
//			Particle p = system.getParticle(n);
			xMax = PApplet.max(xMax, p.position().x());
			xMin = PApplet.min(xMin, p.position().x());
			yMin = PApplet.min(yMin, p.position().y());
			yMax = PApplet.max(yMax, p.position().y());
		}
		float deltaX = xMax - xMin;
		float deltaY = yMax - yMin;
		if(deltaY > deltaX) {
			centroid.setTarget(xMin+0.5f*deltaX, yMin+0.5f*deltaY, ctx.height/(deltaY+200));
		} else {
			centroid.setTarget(xMin+0.5f*deltaX, yMin+0.5f*deltaY, ctx.width/(deltaX+200));
		}		
	}

}
