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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class ModelPanelUI extends ComponentUI implements PropertyChangeListener {

	// This variable is used to avoid excessive clones.
	// It is initialized as soon as the model is known to ModelPanelUI
	private ModelPoint  tempmp;

	private Isometry	viewMatrix;
	private Isometry	inversViewMatrix;
	private boolean		draft;
	private boolean		animation;

	protected ModelPanel	panel;
	private Hashtable	currentRenderer;
	private String		projection;	
	
	private CellRendererPane rendererPane;


	public ModelPanelUI() {
	}
	
	public static ComponentUI createUI(JComponent c) {
		return new ModelPanelUI();
	}

	public void installUI(JComponent c) {
		c.addPropertyChangeListener(this);
		panel = (ModelPanel) c;	
//		panel.setProjector(new PoincareProjector());
		rendererPane = new CellRendererPane();
		tempmp = ((ModelPanel)c).getModel().getOrigin();		
	}
	
	public void uninstallUI(JComponent c) {
	}

/*	public void setProjection(String name) {
		projection = name;
		if (projection.equals("Poincare")) {
			panel.setProjector(new PoincareProjector());
			updateRenderer(ModelPanel.BACKGROUND_RENDERER);
		} else if (projection.equals("HalfPlane")) {
			panel.setProjector(new HalfPlaneProjector());
		}		
	}
	*/
	public void propertyChange(PropertyChangeEvent e) {
/*		String name = e.getPropertyName();
		if ((name == ModelPanel.BACKGROUND_RENDERER) ||
			(name == ModelPanel.LINE_RENDERER) )
				updateRenderer(name);
				*/
	}

/*	public Renderer createDefaultRenderer(String name) {
		Renderer r = null;
		if (name == ModelPanel.BACKGROUND) {
			if (projection.equals("Poincare")) 
				r = new PoincareBackground();
		} else if (name == ModelPanel.LINE_RENDERER) {
			r =new DefaultLineRenderer();
		}
		if (r != null)
			panel.addComponentListener(r);
		return r;
	}
	public void updateRenderer(String name) {
		if(panel != null) {
		    Renderer     r;
	
		    r = panel.getRenderer(name);
		    if(r == null) {
		    	r = createDefaultRenderer(name);
		    	if (r != null) 		    	
					panel.setRenderer(name, r);
		    }
		    else {
		    	if (currentRenderer == null)
		    		currentRenderer = new Hashtable();
				currentRenderer.put(name,r);
		    }
		}
		else {
			// we don't have a panel yet
		    currentRenderer = null;
		}
	}
	public Component getRenderer(String name) {
		Component r;
		if (currentRenderer == null)
			updateRenderer(name);
		r = (Component) currentRenderer.get(name);
		return r;
	}
*/
	public CellRendererPane getRendererPane() {
		return rendererPane;
	}
	public void paintRenderer(Graphics g, JComponent c, Renderer r) {
		Component rcomp = r.getComponent();
		getRendererPane().paintComponent(g, rcomp, c,
			rcomp.getX(), rcomp.getY(), rcomp.getWidth(), rcomp.getHeight());
	}
	public void paintLine(Graphics g, JComponent c, ModelPoint from, ModelPoint to){
		LineRenderer lr = (LineRenderer) panel.getLineRenderer();
		lr.configure(panel, from, to);
		paintRenderer(g, c, lr);
	}
	public void paintText(Graphics g, JComponent c, ModelPoint at, String text) {
		TextRenderer tr = (TextRenderer) panel.getTextRenderer();
		tr.configure(panel, at, text);
		paintRenderer(g, c, tr);
	}

	public boolean isDraft() {
		return draft;
	}
	public void setDraft(boolean d) {
		draft = d;
	}

	public boolean isAnimation() {
		return animation;
	}
	public void setAnimation(boolean b) {
		animation = b;
	}


	public Point2D getScale(ModelPoint mp, JComponent c) {
		tempmp.setTo(mp);
		applyViewMatrix(tempmp, c);
		return ((ModelPanel) c).getProjector().getScale(tempmp, c);
	}

	public Point map(ModelPoint p, JComponent c) {
		return ((ModelPanel) c).getProjector().map(p, c);
	}

	public Point project(ModelPoint mp, JComponent c) {
		tempmp.setTo(mp);
		applyViewMatrix(tempmp, c);
		return map(tempmp, c);
	}

	public ModelPoint inversMap(Point mp, JComponent c) {
		return ((ModelPanel) c).getProjector().inversMap(mp, c);
	}

	public ModelPoint unProject(Point p, JComponent c) {
		ModelPoint mp = inversMap(p, c);
		if (mp == null)
			return null;
		applyInversViewMatrix(mp, c);
		return mp;
	}

	public void applyViewMatrix(ModelPoint mp, JComponent c) {
		Isometry viewMatrix = ((ModelPanel) c).getViewMatrix();
		viewMatrix.apply(mp);
	}

	public void applyInversViewMatrix(ModelPoint mp, JComponent c) {
		Isometry inversViewMatrix = ((ModelPanel) c).getInversViewMatrix();
		inversViewMatrix.apply(mp);
	}
	

class Animator implements ActionListener {
	javax.swing.Timer timer;
	int current;
	Isometry[] isometries;
	JComponent c;
	public void setComponent(JComponent c) {
		this.c = c;
	}
	public void setIsometries(Isometry[] isometries) {
		this.isometries = isometries;
		current = 0;
	}
	public void setTimer(javax.swing.Timer timer) {
		this.timer = timer;
	}
	public void actionPerformed(ActionEvent evt) {
		if (current < 0 || current >= isometries.length) {
			timer.stop();
			return;
		}
		if (current == 0)
			setDraft(true);
		if (current == isometries.length - 1) {
			setDraft(false);
			timer.stop();
		}
		((ModelPanel) c).setViewMatrix(isometries[current]);
		current++;
	}
}
//class Animator implements ActionListener {
//	javax.swing.Timer timer;
//	Iterator iterator;
//	JComponent c;
//	public void setComponent(JComponent c) {
//		this.c = c;
//	}
//	public void setIterator(Iterator iterator) {
//		this.iterator = iterator;
//	}
//	public void setTimer(javax.swing.Timer timer) {
//		this.timer = timer;
//	}
//	public void actionPerformed(ActionEvent evt) {
//		if (iterator.hasNext()) {
//			setDraft(true);
//			((ModelPanel) c).setViewMatrix( (Isometry) iterator.next() );
//		}
//		else
//		{
//			timer.stop();
//			setDraft(false);
//		}
//	}
//}
	javax.swing.Timer 	timer;
	Animator			animator = new Animator();

	public void animate(Isometry[] viewMatrices, JComponent c) {
		if (timer == null) {
			timer = new javax.swing.Timer(0, animator);
			animator.setTimer(timer);
		}
		if (!timer.isRunning()) {
//			animator.setIterator(viewMatrices.iterator());
			animator.setIsometries(viewMatrices);
			animator.setComponent(c);
			timer.setDelay(50);
			timer.setInitialDelay(50);
			timer.start();
		}
	}

	/**Computes the center of the specified modelpanel (could be any JComponent) and
	 * returns it.
	 * @param c The JComponent for which the center is computed.
	 * @return The center of the modelpanel (screen coordinates).
	 */
	public Point getCenter(JComponent c) {
		return new Point(c.getWidth() / 2, c.getHeight() / 2);
	}
	public void center(Point p, JComponent c) {
		move(p, getCenter(c), c);
	}
	public void center(ModelPoint mp, JComponent c) {
		move(mp, getCenter(c), c);
//		Model model = ((ModelPanel) c).getModel();
//		moveOnIntermediateLevel(mp,model.getOrigin(),c);
	}
	public void move(ModelPoint mp1, Point p2, JComponent c) {
		ModelPoint z1 = (ModelPoint) mp1.clone();
		applyViewMatrix(z1, c);
		ModelPoint z2 = inversMap(p2, c);
		if (z2 == null)
			return;
		moveOnIntermediateLevel(z1, z2, c);
	}
	public void move(Point p1, Point p2, JComponent c) {
		Model model = ((ModelPanel) c).getModel();
		ModelPoint mp1 = inversMap(p1, c);
		if (mp1 == null)
			return;
		ModelPoint mp2 = inversMap(p2, c);
		if (mp2 == null)
			return;
		moveOnIntermediateLevel(mp1, mp2, c);
	}
	private void moveOnIntermediateLevel(ModelPoint mp1, ModelPoint mp2, JComponent c) {
		Model model = ((ModelPanel) c).getModel();
		if (model.dist(mp1, mp2) < 0.01)
			return;
//		java.util.List viewMatrices = new LinkedList();
		int n = 1;
		if (isAnimation())
			n = 10;
		Isometry[] viewMatrices = new Isometry[n];
		for (int i = 0; i < viewMatrices.length; i++) {
			viewMatrices[i] = model.getTranslation(mp1, mp2, ((double) i + 1) / (double) n);
			viewMatrices[i].multiplyRight(((ModelPanel) c).getViewMatrix());
		}
//		Isometry isometry;
//		for ( int i=1;i<=n;i++) {
//			isometry = model.getTranslation(mp1,mp2,((double) i) / (double) n);
//			isometry.multiplyRight(((ModelPanel) c).getViewMatrix());
//			viewMatrices.add(isometry);
//		}
		animate(viewMatrices, c);
	}
}
