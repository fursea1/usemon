package org.usemon.live.applet;

import processing.core.PApplet;
import traer.physics.Spring;

public abstract class Edge {
	protected Spring spring;
	protected Node source;
	protected Node target;
	protected boolean attention;

	public Edge(Node source, Node target) {
		this.source = source;
		this.target = target;
	}

	void setSpring(Spring spring) {
		this.spring = spring;
	}

	public abstract void tick();
	public abstract void draw(PApplet g);

	public boolean contains(Node node) {
		return source==node || target==node;
	}

	public Node getSource() {
		return source;
	}
	
	public Node getTarget() {
		return target;
	}
	
	public boolean isAttention() {
		return attention;
	}
}
