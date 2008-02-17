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
 * Interface that represents a vector in a non-euclidian geometry
 * modelled by an implementation of <code>Model</code>. Each vector is located
 * at a given point, the base of type <code>ModelPoint</code>.
 * Any constructor of any implementation
 * that doesn't have explicit information about the vector
 * must set the vector to 0.
 *
 * @author Jens Kanschik
 */
public interface ModelVector {

	/**Returns the base point of the vector.
	 * @return The base point.
	 */
	public ModelPoint getBase();

	/**Sets the base point of the vector. Since after changing the base point,
	 * the old vector information doesn't make sense anymore,
	 * any implementation has to set the vector to 0.
	 * @param mp The new base point.
	 */
	public void setBase(ModelPoint mp);

	/**Scales the vector with the specified double.
	 * This changes the length of the vector,
	 * but not the base point.
	 * @param r The scaling factor.
	 */
	public void scale(double r);

	/** Sets this <code>ModelVector</code> to the specified value.
	 * This should be used to avoid excessive creation of instances.
	 * If the specified value doesn't belong to the same model,
	 * the result is not defined.
	 * @param vector The new value of this <code>ModelVector</code>.
	 */
	public void setTo(ModelVector vector);

	/** Creates a deep copy of the ModelVector.
	 * @return A deep copy of the ModelVector.
	 */
	public Object clone();

}
