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

import java.util.Hashtable;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.JComponent;

public class HalfPlaneProjector implements Projector {

	public Point[] getLineSegments(ModelPoint mp1, ModelPoint mp2, JComponent c) {
		Point[] lineSegments;
		ModelPanelUI ui = (ModelPanelUI) ((ModelPanel) c).getUI();
		ui.applyViewMatrix(mp1,c);
		ui.applyViewMatrix(mp2,c);
		if ( ui.isDraft() || ((Complex) mp1).dist((Complex) mp2) < 0.01 ) {
			lineSegments = new Point[2];
			lineSegments[0] = ui.map(mp1,c);
			lineSegments[1] = ui.map(mp2,c);
			return lineSegments;
		} 

		Complex current;
		current = (Complex) mp1;
	
		int n=6;
		if ( ((Complex) mp1).dist((Complex) mp2) > 1 ) 
			n = 30;
		lineSegments = new Point[n+1];
		lineSegments[0] = ui.map(current,c);
		Isometry trans = ((ModelPanel) c).getModel().getTranslation(mp1,mp2,1/(double)n);
		for ( int i=1;i<n+1;i++) {
			trans.apply(current);
			lineSegments[i] = ui.map(current,c);
		}

		
		return lineSegments; 
	}

	
	private Hashtable rendererClassNames;
	
	public HalfPlaneProjector() {
	}
	
	

	public	Point	map(ModelPoint mp, JComponent c) {
		Complex z1 = new Complex(1,0);
		z1.add((Complex) mp);				// 1+mp
		Complex z2 = new Complex(-1,0);
		z2.add((Complex) mp);				// mp-1
		z2.multiply(-1);	 				// 1-mp
		z1.divide(z2);						// (1+mp) / (1-mp)
		return new Point(	(int) Math.round( z1.getReal() * 0.5*c.getWidth() ),
							(int) Math.round( z1.getImag() * 0.5*c.getHeight() + c.getHeight() *0.5) );
	}
	
	public	ModelPoint	inversMap(Point p, JComponent c) {
		Complex z1 = new Complex( p.getX() * 2 / (double) c.getWidth(),
								 (p.getY()-c.getHeight()*0.5) * 2 / (double) c.getHeight() );
		if (z1.getReal() <= 0)
			return null;
		Complex z2 = (Complex) z1.clone();
		z1.add(-1);
		z2.add(1);
		z1.divide(z2);						// (z-1) / (z+1)
		return z1;			
	}
	
	public Point2D getScale(ModelPoint mp, JComponent c) {
		double scale = 1- ((Complex) mp).norm2();
		return new Point2D.Double(scale,scale);
	} 
	
}
