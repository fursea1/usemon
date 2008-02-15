package com.usemon.live;

import hypergraph.graph.NodeImpl;

import java.awt.Color;

public class InstanceNode extends NodeImpl {
	private static final int TOUCH_DURATION = 1000*5;
	private static final int TTL = 1000*60*10;
	
	private long touchTime;

	protected InstanceNode(String name) {
		super(name);
	}
	
	public void touch() {
		touchTime = System.currentTimeMillis();
	}
	
	public Color getColor() {
		if(System.currentTimeMillis()<(touchTime+TOUCH_DURATION)) {
			return Color.red;
		}
		return Color.lightGray;
	}

	public boolean isExpired() {
		return System.currentTimeMillis()>(touchTime+TTL);
	}
}
