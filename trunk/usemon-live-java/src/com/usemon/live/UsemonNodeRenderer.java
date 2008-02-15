package com.usemon.live;

import hypergraph.visualnet.DefaultNodeRenderer;

import java.awt.Graphics;

public class UsemonNodeRenderer extends DefaultNodeRenderer {
	private static final long serialVersionUID = -1607748423562178551L;

	public void paintComponent(Graphics g) {
		if (node instanceof InstanceNode) {
			InstanceNode in = (InstanceNode) node;
			g.setColor(in.getColor());
			//g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
			g.drawLine(0, getHeight()-1, getWidth()-1, getHeight()-1);
			super.paintComponent(g);
		} else {
			super.paintComponent(g);
		}
	}
}
