package org.usemon.live.applet;

import java.util.Map;

import processing.core.PApplet;
import traer.physics.Particle;

public abstract class Node {
	protected Particle particle;
	protected boolean dead;
	protected long updated;
	private String uuid;
	private Map metaData;

	public Node(Map metaData, String uuid) {
		this.metaData = metaData;
		this.uuid = uuid;
		touch();
	}

	public String getUuid() {
		return uuid;
	}

	public Map getMetaData() {
		return metaData;
	}

	public void tick() {
		if(System.currentTimeMillis()>updated+Graph.NODE_TTL) dead = true;
	}

	public abstract void draw(PApplet g);
	public abstract float getRadius();
	public abstract float getDesiredLengthFromCentrum();

	
	public Particle getParticle() {
		return particle;
	}
	
	void setParticle(Particle particle) {
		this.particle = particle;
	}
	
	public boolean isDead() {
		return dead;
	}

	public void touch() {
		updated = System.currentTimeMillis();
	}

}
