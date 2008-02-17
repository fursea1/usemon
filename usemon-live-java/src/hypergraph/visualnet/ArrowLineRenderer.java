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

import hypergraph.hyperbolic.DefaultLineRenderer;
import hypergraph.hyperbolic.ModelPanelUI;
import hypergraph.hyperbolic.ModelPoint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Special line renderer that also shows an arrow. Showing the arrow can be switched on and off
 */
public class ArrowLineRenderer extends DefaultLineRenderer {

	/** Used to switch rendering of the arrow on and off. */
	private boolean _showArrows = true;

	/**
	 * Set this property to <code>true</code> to show an arrow on each line.
	 * 
	 * @param showArrows
	 */
	public void setShowArrows(boolean showArrows) {
		_showArrows = showArrows;
	}

	/**
	 * Indicates whether the renderer shows an arrow.
	 * 
	 * @return <code>True</code>, if the renderer shows arrows.
	 */
	public boolean isShowArrows() {
		return _showArrows;
	}

	public void paint(Graphics g) {

		if (start == null || end == null)
			return;

		g.setColor(getColor());

		ModelPanelUI ui = (ModelPanelUI) panel.getUI();
		Point[] lineSegments = panel.getProjector().getLineSegments(start, end, panel);
		int[] xPoints = new int[lineSegments.length];
		int[] yPoints = new int[lineSegments.length];
		for (int i = 0; i < lineSegments.length; i++) {
			xPoints[i] = lineSegments[i].x;
			yPoints[i] = lineSegments[i].y;
		}
		g.drawPolyline(xPoints, yPoints, lineSegments.length);

		if (_showArrows) {
			int fromIndex = lineSegments.length / 2;
			int toIndex = fromIndex + 1;
			if (toIndex >= lineSegments.length)
				return;
			ModelPoint toModelPoint = panel.unProject(lineSegments[toIndex]);
			drawArrow(g, xPoints[fromIndex], yPoints[fromIndex], xPoints[toIndex], yPoints[toIndex], panel.getScale(toModelPoint).getX(), panel.getScale(toModelPoint).getY());
		}
	}

	protected void drawArrow(Graphics g, int fromX, int fromY, int toX, int toY, double scaleX, double scaleY) {
		int width = (int) Math.round(15 * scaleX);
		int height = (int) Math.round(15 * scaleY);
		Color headColor = getColor();
		Color currentcolor = getColor();
		int startAngle;
		int angle = 60;
		g.drawLine(fromX, fromY, toX, toY);
		double lengthX = toX - fromX;
		double lengthY = toY - fromY;
		double lineLength = Math.sqrt(lengthX * lengthX + lengthY * lengthY);
		startAngle = (int) Math.toDegrees(Math.asin(lengthY / lineLength));
		if (lengthX > 0) {
			startAngle = (180 - startAngle - 30);
		} else {
			startAngle = (startAngle - 30);
		}
		// Draw the arrow head
		g.setColor(headColor);
		g.fillArc(toX - (width / 2), toY - (height / 2) + 1, width, height, startAngle, angle);
		g.setColor(currentcolor);
	}
}
