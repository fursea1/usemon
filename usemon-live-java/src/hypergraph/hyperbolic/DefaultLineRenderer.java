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

package hypergraph.hyperbolic;

import hypergraph.graphApi.io.CSSColourParser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;

/**
 * @author Jens Kanschik
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DefaultLineRenderer extends JComponent implements LineRenderer {

	protected	ModelPoint 	start;
	protected	ModelPoint 	end;
	protected	ModelPanel	panel;
	private		Color		color;
	
	public DefaultLineRenderer() {
	}
	public void configure(ModelPanel mp) {
		setModelPanel(mp);
		setColor(null);
	}
	public void configure(ModelPanel panel, ModelPoint s, ModelPoint e) {
		configure(panel);
		
		if (start == null)
			start = (ModelPoint) s.clone();
		else
			start.setTo(s);

		if (end == null)
			end = (ModelPoint) e.clone();
		else
			end.setTo(e);

		setBounds(0, 0, panel.getWidth(), panel.getHeight());
	}
	public Component getComponent() {
		return this;
	}

	public void setModelPanel(ModelPanel panel) {
		this.panel = panel;
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
	}

	/**
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		if (color != null)
			return color;
		String colorString = panel.getPropertyManager().getString("hypergraph.hyperbolic.line.color");
		return CSSColourParser.stringToColor(colorString);
	}
}
