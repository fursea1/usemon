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

import javax.swing.JComponent;
import java.awt.Point;
import java.awt.geom.Point2D;

public interface Projector {

	/** Contains second part of the project step. Maps the ModelPoint to Point
	 **/
	public Point map(ModelPoint mp, JComponent c);

	/** Contains first part of the project step. Inverses the map method, if possible
	 **/
	public	ModelPoint	inversMap(Point p, JComponent c);

	public Point2D	getScale(ModelPoint p, JComponent c);

	public Point[] getLineSegments(ModelPoint mp1, ModelPoint mp2, JComponent c);

}
