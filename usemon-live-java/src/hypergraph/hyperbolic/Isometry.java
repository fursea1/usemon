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
 * The interface <code>Isometry</code> provides a setup for all isometries of a <code>Model</code>.
 * The actual implementation depends strongly on the model and is hence done by the different models.
 * It is supposed that each implementation provides at least a default constructor
 * that creates the identity transformation.
 *
 * @author Jens Kanschik
 */
public interface Isometry {

	/**
	 * Sets this isometry to the identity transformation, which is of course
	 * always an isometry.
	 */
	public void setToIdentity();
	/**
	 * Multiplies this isometry with the passed isometry from the right and sets this
	 * isometry to the result, i.e.
	 * <code>this := this * isometry</code>
	 *
	 * @param isometry The factor from the right.
	 */
	public void multiplyRight(Isometry isometry);
	/**
	 * Multiplies this isometry with the passed isometry from the left and sets this
	 * isometry to the result, i.e.
	 * <code>this := isometry * this</code>
	 *
	 * @param isometry The factor from the left.
	 */
	public void multiplyLeft(Isometry isometry);

	/**
	 * Applies the isometry to a <code>ModelPoint</code>.
	 * The parameter will be changed by this.
	 *
	 * @param z The modelpoint that is moved.
	 */
	public void apply(ModelPoint z);
	/**
	 * Applies the isometry to a <code>ModelVector</code>.
	 * The parameter will be changed by this.
	 * To be exact, the differential of the isometry is applied to the vector.
	 *
	 * @param v The vector that is moved.
	 */
	public void apply(ModelVector v);

	/**
	 * Returns a new instance of an isometry that represents the invers of the isometry.
	 * The isometry itself is not changed.
	 * ( this * this.getinvers() = identity )
	 * @return The invers of the isometry.
	 */
	public Isometry getInvers();
	/**
	 * Inverts the isometry. Similar to <code>getInvers()</code>,
	 * but here the instance itself is changed.
	 */
	public void invert();

	/** Sets the isometry to the specified value.
	 * If the specified isometry doesn't belong to the same
	 * model, the result is undefined.
	 * @param isom The new value.
	 */
	public void setTo(Isometry isom);

	/** Creates a deep copy of this isometry.
	 * @return A deep copy of this isometry.
	 */
	public Object clone();

	/**
	 * @return A String representation of the isometry.
	 */
	public String toString();

}

