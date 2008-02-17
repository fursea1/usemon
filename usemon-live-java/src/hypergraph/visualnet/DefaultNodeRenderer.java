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

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.Node;
import hypergraph.hyperbolic.ModelPoint;
import hypergraph.hyperbolic.StubRenderer;
import hypergraph.hyperbolic.TextRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * @author Jens Kanschik
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DefaultNodeRenderer extends JComponent implements NodeRenderer {

	/** The <code>TextRenderer</code> that is used to render the node.*/
	private		TextRenderer 	textRenderer;

	/** The graph panel to which a node has to be drawn. */
	protected	GraphPanel 		graphPanel;

	/** The node that has to be drawn by this renderer. */
	protected	Node			node;

	/** This node renderer allows an icon to be renderer for a node.*/
	protected	Icon			icon;

	/**
	 * Sets the <code>TextRenderer</code> that is used to render the node.
	 * You may set it to any implementation of the interface {@link TextRenderer}.
	 * If you set it to <code>null</code>, the text renderer of the graph panel
	 * (resp. the inherited property from the ModelPanel) is used.
	 * @param tr The new text renderer or <code>null</code> if you want to use the
	 * renderer of the graph panel.
	 */
	public void setTextRenderer(TextRenderer tr) {
		textRenderer = tr;
	}
	/**
	 * @return The <code>TextRenderer</code> that is used to render the node.
	 */
	public TextRenderer getTextRenderer() {
		if (textRenderer == null)
			textRenderer = graphPanel.getTextRenderer();
		return textRenderer;
	}

	public void configure(GraphPanel c, ModelPoint mp, Node node) {
		graphPanel = c;
		this.node = node;
		if (node == null)
			getTextRenderer().configure(c, null, null);
		else {
			int iconWidth = 0;
			int iconHeight = 0;
			int expanderWidth = 0;
			// initializing call to configure of the text renderer
			getTextRenderer().configure(c, mp, node.getLabel());
			Component textComponent = getTextRenderer().getComponent();
			// reading some attributes for the node
			Graph graph = ((GraphPanel) c).getGraph();
			AttributeManager attrMgr = graph.getAttributeManager();
			Color textColour = (Color) attrMgr.getAttribute(GraphPanel.NODE_FOREGROUND, node);
			if (textColour != null)
				getTextRenderer().setColor(textColour);
			Color fillColour = (Color) attrMgr.getAttribute(GraphPanel.NODE_BACKGROUND, node);
			getTextRenderer().setBackground(fillColour);
			setBackground(fillColour);
			if (((GraphPanel) c).getSelectionModel().isElementSelected(node))
				setFont(getFont().deriveFont(Font.BOLD));
			// adjust the size to have space for the +/- sign to expand if necessary
			if (graphPanel.hasExpander(node))
				expanderWidth = 10;
			// adjust the size to have space for the icon if necessary
			icon = (Icon) attrMgr.getAttribute(GraphPanel.NODE_ICON, node);
			if (icon != null) {
				iconHeight = icon.getIconHeight();
				iconWidth = icon.getIconWidth();
			}
			setSize(iconWidth + textComponent.getWidth() + expanderWidth,
					Math.max(iconHeight, textComponent.getHeight()));
			int borderx = getWidth() - textComponent.getWidth();
			int bordery = getHeight() - textComponent.getHeight();
			setLocation(textComponent.getX() - borderx / 2, textComponent.getY() - bordery / 2);
			textComponent.setLocation(getX() + iconWidth, getY() + (getHeight() - textComponent.getHeight()) / 2);
 			if (((GraphPanel) c).getHoverElement() == node) {
 				if (getBackground() == null) {
 					Color back = c.getBackground();
 					setBackground(new Color(back.getRed(), back.getGreen(), back.getBlue(), 224));
 				}
				setFont(getFont().deriveFont(Font.BOLD));
 			}
		}
	}
	public Font getFont() {
		return getTextRenderer().getComponent().getFont();
	}
	public void setFont(Font font) {
		getTextRenderer().getComponent().setFont(font);
	}
	public Component getComponent() {
		if (getTextRenderer().getComponent() instanceof StubRenderer)
			return getTextRenderer().getComponent();
		return this;
	}
	public void paintExpander(Graphics g, int x, int y, int w, int h) {
		g.setColor(Color.lightGray);
		g.drawRect(x, y, w, h);
		g.drawLine(x + 2, y + h / 2, x + w - 2, y + h / 2); // horizontal line
		if (!graphPanel.isExpanded(node))
			g.drawLine(x + w / 2, y + 2, x + w / 2, y + h - 2); // vertical line
	}
	public void paintComponent(Graphics g) {
		if (getBackground() != null) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		if (graphPanel.hasExpander(node))
			paintExpander(g, getWidth()-9, 0, 8, 8);
		if (icon != null) {
			icon.paintIcon(this, g, 0, (getHeight() - icon.getIconHeight()) / 2);
			g.translate(icon.getIconWidth(), 0);
		}
		getTextRenderer().getComponent().paint(g);
	}
	public boolean expanderHit(Point p) {
		if (!graphPanel.hasExpander(node))
			return false;
		int x = p.x - getX() - getWidth();
		int y = p.y - getY();
		if (x >= -10 && x <= 0 && y >= 0 && y <= 10)
			return true;
		return false;
	}

}
