package org.usemon.live.applet.nodes;

import java.util.Map;

import org.usemon.live.applet.Graph;
import org.usemon.live.applet.Node;

import processing.core.PApplet;

public class BeanNode extends Node {
	private static final Object[][] DISTANCE  = new Object[][] {
		{ "DAO", new Integer(100) },
		{ "DB", new Integer(100) },
		{ "SystemBean", new Integer(200) },
		{ "ServiceBean", new Integer(300) },
		{ "Servlet", new Integer(500) },
		{ "MDB", new Integer(50) },
		{ "MessageDrivenBean", new Integer(50) },
		{ "MQ", new Integer(50) },
	};
	private static final int DISTANCE_DEFAULT = 400;
	private String className;
	
	public BeanNode(Map metaData, String uuid, String className) {
		super(metaData, uuid);
		this.className = className;
	}

	public void tick() {
		if(System.currentTimeMillis()>updated+Graph.NODE_TTL) dead = true;
	}

	public void draw(PApplet g) {
		g.noStroke();
		g.fill(128, 128, 128);
		g.ellipse(particle.position().x(), particle.position().y(), 10, 10);
		g.fill(255, 255, 255);
		g.text(className, particle.position().x(), particle.position().y());
	}
	
	public float getRadius() {
		return 10;
	}

	public float getDesiredLengthFromCentrum() {
		for(int n=0;n<DISTANCE.length;n++) {
			if(className.indexOf((String) DISTANCE[n][0])!=-1) {
				return ((Integer) DISTANCE[n][1]).intValue();
			}
		}
		return DISTANCE_DEFAULT;
	}
	
	public String toString() {
		return className;
	}
}
