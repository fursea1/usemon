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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;


/**
 * @author Jens Kanschik
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DefaultTextRenderer extends JComponent implements TextRenderer {

	protected	ModelPanel	modelPanel;
	protected	String		text;
	protected	JComponent	stubRenderer = new EmptyComponent();
	protected	boolean		useStubRenderer;

	/** The maximum length for a displayed text. */
	private		int			maxTextLength = -1;
	private		Color		color;
	private		Color		background;
	private		Font		font1;
	private		Font		font2;
	private		Font		font3;
	private		Font		font4;
	private		double		scale1;
	private		double		scale2;
	private		double		scale3;
	private		double		scale4;

	private		double		optimalRatio = 8;
	private		List		lines;

	public DefaultTextRenderer() {
		super();
		setOpaque(false);
		useStubRenderer = true;
		lines = new ArrayList();
	}
	public void configure(ModelPanel mp) {
		configure(mp, null, null);
	}
	/** Returns the maximal allowed length of the displayed text.
	 * If it is less than 0, there are no restrictions,
	 * if it is nonnegative and the text is longer than this number,
	 * only the first maxTextLength -2 characters are shown plus two dots ("..").
	 * <br />
	 * The default value is -1, it can be overwritten by the property
	 * "hypergraph.hyperbolic.text.maxLength" or by the method {@link setMaxTextLength(int)}.
	 * 
	 * @return The maximal allowed text length.
	 */ 
	public int getMaxTextLength() {
		return maxTextLength;
	}
	/** Sets the maximal allowed length of displayed text.
	 * @see #getMaxTextLength()
	 * @param mtl The maximal allowed length of displayed text.
	 */
	public void setMaxTextLength(int mtl) {
		maxTextLength = mtl;
	}
	/** Returns the font that has a size that corresponds to the distortion
	 * that is described by the parameter <code>scale</code>.
	 * @param scale The distortion factor.
	 * @return The font with the correct size.
	 */
	public Font getScaledFont(Point2D scale) {
		double maxScale = Math.max(scale.getX(), scale.getY());
		Font font = null;
		if (maxScale > scale1) 
			font = font1;
		if (maxScale <= scale1 && maxScale > scale2)
			font = font2;
		if (maxScale <= scale2 && maxScale > scale3)
			font = font3;
		if (maxScale <= scale3)
			font = font4;
		return font;
	}
	protected boolean isDelimiter(char ch) {
		return ch == ' ' || ch == ',' || ch == ';' || ch == '-' || ch == '/' || ch == '\\';
	}
	public void configure(ModelPanel mp, ModelPoint at, String text) {
		modelPanel = mp;
		setColor(null);
		if (at == null || text == null) {
			useStubRenderer = true;
			return;
		}
		String l = (String) mp.getPropertyManager().getProperty("hypergraph.hyperbolic.text.maxLength");
		if (maxTextLength == -1 && l != null)
			maxTextLength = Integer.parseInt(l);
		if (font1 == null) {
			float fontSize;
			String fontName;
			fontName = mp.getPropertyManager().getString("hypergraph.hyperbolic.text.fontName");
			if (fontName == null)
				fontName = "Arial"; 
			Font font = new Font(fontName, Font.PLAIN, 1);
			fontSize = (float) mp.getPropertyManager().getDouble("hypergraph.hyperbolic.text.size1", new Double(12)).doubleValue();
			font1 = font.deriveFont(fontSize);
			fontSize = (float) mp.getPropertyManager().getDouble("hypergraph.hyperbolic.text.size2", new Double(10)).doubleValue();
			font2 = font.deriveFont(fontSize);
			fontSize = (float) mp.getPropertyManager().getDouble("hypergraph.hyperbolic.text.size3", new Double(8)).doubleValue();
			font3 = font.deriveFont(fontSize);
			fontSize = (float) mp.getPropertyManager().getDouble("hypergraph.hyperbolic.text.size4", new Double(0)).doubleValue();
			font4 = font.deriveFont(fontSize);
			scale1 = mp.getPropertyManager().getDouble("hypergraph.hyperbolic.text.scale1", new Double(0.75)).doubleValue();
			scale2 = mp.getPropertyManager().getDouble("hypergraph.hyperbolic.text.scale2", new Double(0.5)).doubleValue();
			scale3 = mp.getPropertyManager().getDouble("hypergraph.hyperbolic.text.scale3", new Double(0.25)).doubleValue();
		}
		Point p = mp.project(at);
		Point2D scale = mp.getScale(at);
		Font font = getScaledFont(scale);
		if (font == null) {
			useStubRenderer = true;
			return;
		}
		useStubRenderer = false;
		setFont(font);

		// restrict the text to a maximum if requested.
		text = text.trim();
		if (maxTextLength >= 2 && text.length() > maxTextLength)
			text = text.substring(0, maxTextLength - 2) + "..";
		this.text = text;

		FontMetrics fm = getFontMetrics(getFont().deriveFont(Font.BOLD));
		double textWidth = SwingUtilities.computeStringWidth(fm, text);
		double height = fm.getHeight();

		double width = 0;
		lines.clear();
		// check whether the text fits in one line
		if (textWidth / height < optimalRatio) {
			// ok, one line seems to be enough
			lines.add(0, text);
			width = textWidth;
		} else {
			double area = textWidth * height;
			textWidth = Math.sqrt(area * optimalRatio);
			int prevPos = 0;
			double runningWidth = 0;
			StringBuffer buffer = new StringBuffer();
			for (int pos = 0; pos < text.length(); pos++) {
				char ch = text.charAt(pos);
				if (isDelimiter(ch)) {
					String word;
					word = text.substring(prevPos, pos + 1);
					prevPos = pos + 1; 
					double wordWidth = SwingUtilities.computeStringWidth(fm, word);
					if (wordWidth + runningWidth > textWidth) {
						// the word doesn't fit in the current line,
						// therefore save the previous and start a new one.
						lines.add(buffer.toString().trim());
						buffer.setLength(0);
						runningWidth = 0;
					}
					buffer.append(word);
					runningWidth += wordWidth;
					if (runningWidth > width)
						width = runningWidth;
				}
			}
			// if there hasn't been any delimiter, the buffer is empty and the text is shown in one line.
			// otherwise, we have to check, whether the last word fits in the last line or not.
			if (buffer.length() == 0) {
				buffer.append(text);
				width = SwingUtilities.computeStringWidth(fm, text);
			} else {
				String word;
				word = text.substring(prevPos, text.length());
				double wordWidth = SwingUtilities.computeStringWidth(fm, word);
				if (wordWidth + runningWidth > textWidth) {
					// the word doesn't fit in the current line,
					// therefore save the previous and start a new one.
					lines.add(buffer.toString().trim());
					buffer.setLength(0);
					runningWidth = 0;
				}
				buffer.append(word);
				runningWidth += wordWidth;
				if (runningWidth > width)
					width = runningWidth;
			}
			lines.add(buffer.toString().trim());
		}

		height = fm.getHeight() * lines.size();

		setSize(new Dimension((int) Math.round(width), (int) Math.round(height)));

		p.translate(-getWidth() / 2, -getHeight() / 2);						
		setLocation(p);
	}

	public Color getColor() {
		if (color != null)
			return color;
		String colorString = modelPanel.getPropertyManager().getString("hypergraph.hyperbolic.text.color");
		return CSSColourParser.stringToColor(colorString);
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getBackground() {
		if (background != null)
			return background;
		String colorString = modelPanel.getPropertyManager().getString("hypergraph.hyperbolic.text.backgroundColor");
		if (colorString == null)
			return null;
		return CSSColourParser.stringToColor(colorString);
	}
	public void setBackground(Color color) {
		this.background = color;
	}
	public void paintComponent(Graphics g) {	
		g.setColor(getColor());
		if (getBackground() != null) {
			((Graphics2D) g).setBackground(getBackground());
			g.clearRect(0,0,getWidth(),getHeight());
		}
		FontMetrics fm = getFontMetrics(getFont());
		int y = 0;
		for (Iterator iter = lines.iterator(); iter.hasNext();) {
			String text = (String) iter.next();
			int textWidth = SwingUtilities.computeStringWidth(fm, text);
			y += fm.getAscent();
			g.drawString(text, (getWidth() - textWidth) / 2, y);
		}

	}

	public Component getComponent() {
		if (useStubRenderer)
			return stubRenderer;
		return this;
	}

}
