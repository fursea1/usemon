package org.usemon.live.applet.nodes;

import java.util.Map;

import org.usemon.live.applet.Node;

import processing.core.PApplet;

public class HttpNode extends Node {

	private String url;

	public HttpNode(Map metaData, String uuid, String url) {
		super(metaData, uuid);
		this.url = url;
	}

	public void draw(PApplet g) {
		g.noStroke();
		g.fill(128, 128, 128);
		g.rect(particle.position().x(), particle.position().y(), 10, 10);
		g.fill(255, 255, 255);
		g.text(url, particle.position().x(), particle.position().y());
	}
	
	public float getRadius() {
		return 10;
	}	

	public float getDesiredLengthFromCentrum() {
		return 600;
	}
	
	public String toString() {
		return url;
	}
}
