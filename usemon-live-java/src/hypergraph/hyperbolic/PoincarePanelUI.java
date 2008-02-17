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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Jens Kanschik
 *
 * This class is deprecated
 * 
 **/
public class PoincarePanelUI extends ModelPanelUI {
	
	public PoincarePanelUI() {
	}
	
	public static ComponentUI createUI(JComponent c) {
		return new PoincarePanelUI();
	}

	public void installUI(JComponent c) {
		super.installUI(c);
		panel.setProjector(new PoincareProjector());
		c.setBackground(Color.white);
	}

/*	public Renderer createDefaultRenderer(String name) {
//		if (name == ModelPanel.LINE_RENDERER ) 
//			return new PoincareLineRenderer();
		return super.createDefaultRenderer(name);
	}
*/		
	public Dimension getPreferredSize() {
		return new Dimension(200,200);
	}
	public Dimension getMinimumSize() {
		return new Dimension(200,200);
	}
	public void paint(Graphics g, JComponent c) {
		super.paint(g,c);
		
		Insets insets = c.getInsets();
		g.translate(insets.left,insets.top);
		int width = c.getWidth() - insets.left - insets.right;
		int height = c.getHeight() - insets.top - insets.bottom;
		
		g.setColor(Color.darkGray);
		g.fillOval(0,0,width,height);
		g.setColor(Color.lightGray);
		g.fillOval(1,1,width-1,height-1);
		
		g.translate(insets.left,insets.top);
	}
	
}
