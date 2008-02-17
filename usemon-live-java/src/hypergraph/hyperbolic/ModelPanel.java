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
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ModelPanel extends JComponent 
		implements ChangeListener, MouseListener, MouseMotionListener {

	private	Model		model;

	private LineRenderer	lineRenderer;
	private TextRenderer	textRenderer;

	private	Projector		projector;
	
	private PropertyManager properties;
	
	private ModelPoint	draggingStartPoint;
	
	public ModelPanel() {

		refreshProperties();

		// if there is no UI yet, set it to the ModelPanelUI per default
		if (UIManager.get("ModelPanelUI") == null)		
			UIManager.put("ModelPanelUI","hypergraph.hyperbolic.ModelPanelUI");
		addMouseListener(this);
		addMouseMotionListener(this);

		updateUI();
	}

	public void loadProperties(InputStream is) throws IOException {
		properties.load(is);
		refreshProperties();
	}
	public void refreshProperties() {
		if (properties == null)
			properties = new PropertyManager();
		Model m = null;
		try {
			m = (Model) properties.getClass("model.class").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setModel(m);
		getModel().addChangeListener(this);

		String colorString = getPropertyManager().getString("hypergraph.hyperbolic.background.color");
		if (colorString != null) {
			Color color = CSSColourParser.stringToColor(colorString);
			setBackground(color);
			setOpaque(true);
		} else
			setOpaque(false);
	}
	public PropertyManager getPropertyManager() {
		return properties;
	}
	public void stateChanged(ChangeEvent e) {
		repaint();
	}

	public Isometry getInversViewMatrix() {
		if (getModel() == null)
			return null;
		return getModel().getInversViewMatrix();
	}
	public Isometry getViewMatrix() {
		if (getModel() == null)
			return null;
		return getModel().getViewMatrix();
	}
	public void setViewMatrix(Isometry vm) {
		if (getModel() == null)
			return;
		getModel().setViewMatrix(vm);
	}

	public void setProjector(Projector p) {
		Projector old = projector;
		projector = p;
		firePropertyChange("Projector", old, projector);
	}
	public Projector getProjector() {
		if (projector != null)
			return projector;
		try {
			projector = (Projector) properties.getClass("projector.class").newInstance();
		} catch (Exception e) {
			System.out.println("Couldn't load projector");
			System.out.println("Classname for projector is : " + properties.getString("projector.class"));
			e.printStackTrace();
			return null;
		}
		return projector;
	}
	public void paintRenderer(Graphics g, Renderer r) {
		getUI().paintRenderer(g, this, r);
	}
	public void paintLine(Graphics g, ModelPoint from, ModelPoint to) {
		getUI().paintLine(g, this, from, to);
	}
	public void paintText(Graphics g, ModelPoint at, String text) {
		getUI().paintText(g, this, at, text);
	}

	public void setModel(Model m) {
		Model old = getModel();
		model = m;
		firePropertyChange("Model", old, m);
	}
	public Model getModel() {
		return model;
	}

    public ModelPanelUI getUI() {
        return (ModelPanelUI) ui;
    }
	public void setUI(ModelPanelUI ui) {
		super.setUI(ui);
	}
	public void updateUI() {
		setUI((ModelPanelUI) UIManager.getUI(this));
		invalidate();
	}
	public String getUIClassID() {
		return "ModelPanelUI";
	}

	/**
	 * Returns the scale of a point in the current model.
	 * All geometrical models distort sizes depending on the point.
	 * This method returns the distortion in x and y direction. 
	 * @param mp The location at which the scale or distortion is computed.
	 * @return The scale or distortion.
	 */
	public Point2D getScale(ModelPoint mp) {
		return getUI().getScale(mp, this);
	}
	/** 
	 * Maps a point in the model to screen coordinates.
	 * @param mp The point in the model.
	 * @return The projected point in screen coordinates.
	 */
	public Point project(ModelPoint mp) {
		return getUI().project(mp, this);
	}
	/**
	 * Maps a point from screen coordinates to the model.
	 * This is the invers of {@link #project(ModelPoint)}.
	 * @param p The point in screen coordinates.
	 * @return The projected point in the model.
	 */
	public ModelPoint unProject(Point p) {
		return getUI().unProject(p, this);
	}
	/**
	 * Moves the model in such way that the given point (in screen coordinates)
	 * is moved to the center of the panel.
	 * @param p The point that has to be centered.
	 */
	public void center(Point p) {
		getUI().center(p, this);
	}


	/**
	 * Returns the <code>LineRenderer</code> that is used in this model panel
	 * to draw lines according to the associated model.
	 * This property can be set using <code>setLineRenderer(LineRenderer)</code>.
	 * If the property hasn't been set explicitly or
	 * if it has been set to <code>null</code>,
	 * the property "linerenderer.class" is used to create a new <code>LineRenderer</code>.
	 * The value of this property has to be the fully qualified class name of
	 * an implementation of the interface <code>hypergraph.hyperbolic.LineRenderer</code>.
	 * If such a class doesn't exist or couldn't be instantiated,
	 * the method returns <code>null</code>.
	 * 
	 * @return The <code>LineRenderer</code> used in this model panel.
	 */
	public LineRenderer getLineRenderer() {
		if (lineRenderer == null) {
			try {
				lineRenderer = (LineRenderer) properties.getClass("linerenderer.class").newInstance();
			} catch (Exception e) {
				return null;
			}
		}
		return lineRenderer;
	}

	/**
	 * Returns the <code>TextRenderer</code> that is used
	 * in this model panel to show any kind of text.
	 * This property can be set using <code>setTextRenderer(TextRenderer)</code>.
	 * If the property hasn't been set explicitly or
	 * if it has been set to <code>null</code>,
	 * the property "textrenderer.class" is used to create a new <code>TextRenderer</code>.
	 * The value of this property has to be the fully qualified class name of
	 * an implementation of the interface <code>hypergraph.hyperbolic.TextRenderer</code>.
	 * If such a class doesn't exist or couldn't be instantiated,
	 * the method returns <code>null</code>.
	 * 
	 * @return The <code>TextRenderer</code> used in this model panel.
	 */
	public TextRenderer getTextRenderer() {
		if (textRenderer == null) {
			try {
				textRenderer = (TextRenderer) properties.getClass("textrenderer.class").newInstance();
			} catch (Exception e) {
				return null;
			}
		}
		return textRenderer;
	}

	/**
	 * Sets the line renderer for the model panel.
	 * If you set it to <code>null</code>, the default line renderer will be used.
	 * @see #getLineRenderer() 
	 * @param renderer The line renderer that has to be used.
	 */
	public void setLineRenderer(LineRenderer renderer) {
		lineRenderer = renderer;
	}

	/**
	 * Sets the text renderer for the model panel.
	 * If you set it to <code>null</code>, the default text renderer will be used.
	 * @see #getTextRenderer() 
	 * @param renderer The text renderer that has to be used.
	 */
	public void setTextRenderer(TextRenderer renderer) {
		textRenderer = renderer;
	}

	
	// ***********************************
	// 	Mouse handler methods
	// ***********************************
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
				center(e.getPoint());
//				getUI().center(e.getPoint(),(JComponent) e.getSource());
				//e.consume();
			}
		}
	}
	public void mousePressed(MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
//			oldDraggingPoint = e.getPoint();
			draggingStartPoint = getUI().unProject(e.getPoint(), (JComponent) e.getSource());
			getUI().setAnimation(false);
			getUI().setDraft(true);
		}
	}
	public void mouseReleased(MouseEvent e) {
/*	if (e.getSource() != this)
	    return; */
		JComponent c = (JComponent) e.getSource();
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			c.setCursor(Cursor.getDefaultCursor());
//			oldDraggingPoint = null;
			draggingStartPoint = null;
			getUI().setAnimation(true);
			getUI().setDraft(false);
		}
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
		/*	if (e.getSource() != this)
				return; */
		JComponent c = (JComponent) e.getSource();
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0  && draggingStartPoint != null) {
			c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			getUI().move(draggingStartPoint, e.getPoint(), c);
//			move(oldDraggingPoint,e.getPoint(),c);
//			oldDraggingPoint = e.getPoint();
		}
	}
    public void mouseMoved(MouseEvent e) {
/*	if (e.getSource() != this)
	    return;
 	Complex z = screenToModel(e.getPoint());
	if ((z.norm2() < 1) && (coordLabel != null))
	    coordLabel.setText("<html> ( " + (float)z.getReal() + "; " + (float)z.getImag() + ")" + 
			       "<br /> " + model.dist(z) +"</html>");
*/
	}

}
