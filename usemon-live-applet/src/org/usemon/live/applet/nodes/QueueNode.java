package org.usemon.live.applet.nodes;

import java.util.Map;

import org.usemon.live.applet.Graph;
import org.usemon.live.applet.Node;

import processing.core.PApplet;

public class QueueNode extends Node {
	private String queueName;

	public QueueNode(Map metaData, String uuid, String queueName) {
		super(metaData, uuid);
		this.queueName = queueName;
	}

	public void tick() {
		if(System.currentTimeMillis()>updated+Graph.NODE_TTL) dead = true;
	}

	public void draw(PApplet g) {
		g.stroke(255, 255, 255);
		g.fill(0, 0, 0);
		g.rect(particle.position().x(), particle.position().y(), 10, 10);
		g.fill(255, 255, 255);
		g.text(queueName, particle.position().x(), particle.position().y());
	}

	public float getDesiredLengthFromCentrum() {
		return 100;
	}

	public float getRadius() {
		return 10;
	}

	public String toString() {
		return queueName;
	}
}
