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

/**
 * Interface that represents a point in a non-euclidian geometry
 * modelled by an implementation of <code>Model</code>.
 *
 * @author Jens Kanschik
 */
public interface ModelPoint extends Cloneable {
	/**
	 * Sets the modelpoint location to the passed modelpoint.
	 * @param mp The new location.
	 */
	public void setTo(ModelPoint mp);
	/** Creates a deep copy of the ModelPoint.
	 * @return A deep copy of the ModelPoint.
	 */
	public Object clone();
}
