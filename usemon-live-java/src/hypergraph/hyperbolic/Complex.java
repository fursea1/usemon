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
 * Stores a complex number and provides standard methods.
 *
 * @author Jens Kanschik
 */
public class Complex implements ModelPoint {
	/** The real part of the complex number. */
    double real;
	/** The imaginary part of the complex number. */
    double imag;

	/**
	 * Returns <code>exp(it)</code>.
	 * @param t The angle
	 * @return The complex number <code>exp(it)</code>
	 */
    public static Complex getRotation(double t) {
		return new Complex(Math.cos(t), Math.sin(t));
    }

	/**
	 * Creates an instance of <code>Complex</code> that is set to 0.
	 *
	 */
    public Complex() {
		this(0, 0);
    }
    /**
     * Creates a new instance of <code>Complex</code>.
     * The first element in the array is the real part,
     * the second is the imaginary part.
     * @param v An array of two doubles that contains the real and imaginary part.
     */
    public Complex(double[] v) {
	if (v.length > 0)
	    real = v[0];
	if (v.length > 1)
	    imag = v[1];
    }
    /** Creates a copy of <code>v</code>.
     * @param v The complex number that has to be copied.
     */
    public Complex(Complex v) {
		this(v.real, v.imag);
    }
    /** Creates a complex number that represents a real number, i.e. the imaginary part is 0.
     * @param r The real part of the complex number.
     */
    public Complex(double r) {
		real = r;
		imag = 0;
    }
    /** Creates a complex number with the given real and imaginary part.
     * @param r The real part of the complex number.
     * @param i The imaginary part of the complex number.
     */
    public Complex(double r, double i) {
		real = r;
		imag = i;
    }
    /**
     * Sets the value of <code>this</code> to the value of the <code>ModelPoint</code>.
     * Of course this works only if the parameter is actually an instance of <code>Complex</code>
     * @param z The modelpoint with the new coordinates.
     */
    public void setTo(ModelPoint z) {
    	if (z == null)
    		return;
    	real = ((Complex) z).real;
    	imag = ((Complex) z).imag;
    }

	/** A small number used to avoid rounding errors in method {@link equals(Object)}. */
    private final double epsilon = 1e-10;

    /** Returns <code>true</code> if the parameter is a complex number that is equals to
     * <code>this</code> and <code>false</code> otherwise.
     * @param z Any object.
     * @return <code>True</code> if z equals <code>this</code>.
     */
    public boolean equals(Object z) {
    	return (Math.abs(((Complex) z).real - real) < epsilon &&
    			Math.abs(((Complex) z).imag - imag) < epsilon);
    }

	/** Returns the has code of this Complex object.
	 * The result is simply the sum of the hash code
	 * of the real and the imaginary part, both considered as Double objects.
	 * @return The hash code of the Complex object.
	 */
	public int hashCode() {
		return new Double(real).hashCode() + new Double(imag).hashCode();
	}

    /**
     * Returns an array of to doubles,
     * the first is the real part, the second the imaginary part.
     * @return The complex number as array.
     */
    public double[] toArray() {
		double[] array = {real, imag};
		return array;
    }
    /** Sets the real part of the complex number.
     * @param r The new real part.
     */
    public void setReal(double r) {
    	if (r == Double.NaN)
    		throw new IllegalArgumentException();
    	real = r;
    }
    /** Returns the real part of the complex number.
     * @return The real part of the complex number. */
    public double getReal() {
		return real;
    }
	/** Sets the imaginary part of the complex number.
	 * @param i The new imaginary part.
	 */
    public void setImag(double i) {
		if (i == Double.NaN)
			throw new IllegalArgumentException();
    	imag = i;
    }
	/** Returns the imaginary part of the complex number.
	 * @return The imaginary part of the complex number. */
    public double getImag() {
		return imag;
    }
    /** Returns the radiant of a complex number.
     * @return The radiant of the complex number.
     */
    public double getRad() {
		double r = Math.acos(real / norm());
		if (imag < 0)
	    	r = -r;
		return r;
    }
    /** Adds a real number to the complex number.
     * @param x The real number that is added.
     */
    public void add(double x) {
		real += x;
    }
	/** Adds a complex number to this complex number.
	 * @param v The complex number that is added.
	 */
    public void add(Complex v) {
		real += v.real;
		imag += v.imag;
    }
	/** Subtracts a complex number from this complex number.
	 * @param v The complex number that is subtracted.
	 */
	public void subtract(Complex v) {
		real -= v.real;
		imag -= v.imag;
    }
	/** Multiplies a real number with this complex number.
	 * @param r The real factor.
	 */
    public void multiply(double r) {
		real *= r;
		imag *= r;
    }
	/** Multiplies a complex number with this complex number.
	 * @param v The complex factor.
	 */
    public void multiply(Complex v) {
		double r = real * v.real - imag * v.imag;
		imag = real * v.imag + imag * v.real;
		real = r;
    }
    /** Sets <code>this</code> to <code>1/this</code>. */
    public void reciprocal() {
		imag = -imag;
		double n = norm2();
		divide(n);
    }
    /** Returns the invers of <code>this</code>.
     * @return A new complex that is the invers of <code>this</code>.
     */
    public Complex getReciprocal() {
		Complex z = new Complex(this);
		z.reciprocal();
		return z;
    }
    /** Divides the complex number by a real number.
     * @param r The divisor.
     */
    public void divide(double r) {
		real /= r;
		imag /= r;
    }
	/** Divides the complex number by another complexnumber.
	 * @param z The divisor.
	 */
    public void divide(Complex z) {
		z.conjugate();
		multiply(z);
		z.conjugate();
		divide(z.norm2());
    }
    /** Conjugates the complex number (negates the imaginary part.) */
    public void conjugate() {
		imag = -imag;
    }
    /** Normalizes the complex number so that it's length is 1. */
    public void normalize() {
		multiply(1 / norm());
    }
    /** Return the distance from <code>this</code> to the passed complex number.
     * @param z Another complex number.
     * @return The distance from the complex number to <code>z</code>.
     */
    public double dist(Complex z) {
		if (z == null)
			return Double.NaN;
		return Math.sqrt((real - z.real) * (real - z.real) + (imag - z.imag) * (imag - z.imag));
    }
	/** Returns the squared distance from <code>this</code> to the passed complex number.
	 * @param z Another complex number.
	 * @return The squared distance from the complex number to <code>z</code>.
	 */
    public double dist2(Complex z) {
    	if (z == null)
    		return Double.NaN;
		return (real - z.real) * (real - z.real) + (imag - z.imag) * (imag - z.imag);
    }
	/** Return the squared norm of the complex number.
	 * @return The squared norm of the complex number.
	 */
    public double norm2() {
		return real * real + imag * imag;
    }
	/** Return the norm of the complex number.
	 * @return The norm of the complex number.
	 */
    public double norm() {
		return Math.sqrt(real * real + imag * imag);
    }
	/** Return the euclidian scalar product of the complex number and another complex number.
	 * @param z Another complex number.
	 * @return The euclidian scalar product of the two complex numbers.
	 */
    double scalarProduct(Complex z) {
		return real * z.real + imag * z.imag;
    }
    /** Creates a copy of the instance.
     * @return Another instance of Complex representing the same complex number.
     */
	public Object clone() {
		return new Complex(this);
	}
	/** Returns a String representation of a complex number.
	 * @return A String of the form x+i*y, where x,y are real.
	 */
    public String toString() {
		return real + "+i*" + imag;
    }
}
