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

/** This is an implementation of the interface <code>ModelVector</code>
 * for the complex plane, i.e. the base point is a complec number.
 * Since the set of complex numbers is a real vector space,
 * the direction is a complex number as well.
 *
 * @author Jens Kanschik
 */
public class ComplexVector implements hypergraph.hyperbolic.ModelVector {
	/** The base point of the vector. */
	public Complex base;
	/** The direction of the vector. */
	public Complex v;
	/** Default constructor, creates the vector at 0 with length 0. */
	public ComplexVector() {
		this(null, null);
	}
	public ComplexVector(Complex base, Complex v) {
		if (base == null)
			base = new Complex();
		if (v == null)
			v = new Complex();
		this.base = base;
		this.v = v;
	}

	public void setBase(ModelPoint base) {
		this.base = (Complex) base;
		v = new Complex();
	}
	/** @inheritDoc */
	public ModelPoint getBase() {
		return base;
	}
	public void scale(double r) {
		v.real *= r;
		v.imag *= r;
	}
	public void setTo(ModelVector vector) {
		base.real = ((ComplexVector) vector).base.real;
		base.imag = ((ComplexVector) vector).base.imag;
		v.real = ((ComplexVector) vector).v.real;
		v.imag = ((ComplexVector) vector).v.imag;
	}
	public Object clone() {
		return new ComplexVector((Complex) base.clone(), (Complex) v.clone());
	}

	public String toString() {
		return "(" + base + ";" + v + ")";
	}
}