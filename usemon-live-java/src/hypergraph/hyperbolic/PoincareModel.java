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

public class PoincareModel extends AbstractModel {

	public PoincareModel() {
		setViewMatrix(null);
	}

	public ModelPoint getOrigin() {
		return new Complex();
	}
	public hypergraph.hyperbolic.Isometry getIdentity() {
		return new Isometry();
	}
	public hypergraph.hyperbolic.Isometry getRotation(double alpha) {
		return new Isometry(new Complex(Complex.getRotation(alpha/2)), new Complex()); 
	}
	public void getTranslation(hypergraph.hyperbolic.Isometry isom,hypergraph.hyperbolic.ModelPoint mp, double t) {
		Complex z = (Complex) mp;
		double d=1;
		if ( Math.abs(t-1.0) > 0.01) {
			d = dist(z) * t;
			d = (Math.exp(d)-1) / (Math.exp(d)+1);
			d = d / z.norm();
		}
		((Isometry) isom).a.real = 1;
		((Isometry) isom).a.imag = 0;
		((Isometry) isom).c.real = z.real * d;
		((Isometry) isom).c.imag = -z.imag * d;
	}

	public void setOrientation() {
//		Isometry vm = (Isometry) viewMatrix;
//		// compute current differential
//		Complex v; // differenial of viewmatrix at orientationRoot
//		v = (Complex) orientationRoot.clone();
//		v.multiply(vm.c);
//		vm.a.conjugate();
//		v.add(vm.a);
//		vm.a.conjugate();
//		v.reciprocal();
//		// v is now differential
//		Complex z = (Complex) orientationRoot.clone();
//		vm.apply(z);
//		// z is the image of orientationRoot
///		viewMatrix.multiplyLeft(getRotation(z,-v.getRad()));
	}

	/** {@inheritDoc} */
	public double getDistance(
					ModelPoint p,
					ModelPoint z1, ModelPoint z2,
					boolean cutoff1, boolean cutoff2) {
		return dist(p, getProjection(p, z1, z2, cutoff1, cutoff2));
	}

	/** {@inheritDoc} */
	public double getAngle(ModelPoint p, ModelPoint z1, ModelPoint z2) {
		double d1 = dist(p, z1);
		if (d1 == 0)
			return 0;
		double d2 = dist(p, z2);
		if (d2 == 0)
			return 0;
		double d3 = dist(z1, z2);
		if (d3 == 0)
			return 0;
		double cos = (Functions.cosh(d1) * Functions.cosh(d2) - Functions.cosh(d3))
					/ (Functions.sinh(d1) * Functions.sinh(d2));
		if (cos > 1)
			cos = 1;
		if (cos < -1)
			cos = -1;
		double angle = Math.acos(cos);
		return angle;
	}

	/** {@inheritDoc} */
	public ModelPoint getProjection(ModelPoint p, ModelPoint z1, ModelPoint z2, boolean cutoff1, boolean cutoff2) {
		double alpha = getAngle(z1, p, z2);
		ModelPoint z;
		double t;
		if (Math.abs(alpha) > 1e-7) {
			t = Functions.arsinh(Functions.sinh(dist(z1, p)) * Math.sin(alpha));
			double s = Functions.arcosh(Functions.cosh(dist(p, z1)) / Functions.cosh(t));
			ModelPoint p1 = (ModelPoint) z1.clone();
			getTranslation(z1,z2,s / dist(z1, z2)).apply(p1);
			ModelPoint p2 = (ModelPoint) z1.clone();
			getTranslation(z1,z2,-s / dist(z1, z2)).apply(p2);
			if (dist(p, p1) > dist(p, p2))
				z = p2;
			else
				z = p1;
		} else {
//			s = 0;
			z = p;
		}
		// if projection to whole, infinite line :
		if (!cutoff1 && !cutoff2)
			return z;
		// projection z between z1 and z2 :
		if (getAngle(z, z1, z2) > Math.PI / 2)
			return z;
		if (dist(z, z1) < dist(z, z2))
			if (cutoff1)
				return z1;
			else
				return z;
		else
			if (cutoff2)
				return z2;
			else
				return z;
	}
	/** {@inheritDoc} */
	public double dist(ModelPoint z) {
		double r = ((Complex) z).norm();
		return Math.log((1 + r) / (1 - r));
	}

	/** {@inheritDoc} */
	public double dist(ModelPoint mp1, ModelPoint mp2) {
		double xr, xi, yr, yi;
		xr = ((Complex) mp1).real;
		xi = ((Complex) mp1).imag;
		yr = ((Complex) mp2).real;
		yi = ((Complex) mp2).imag;
		double a1 = xr - yr;
		double a2 = xi - yi;
		double b1 = 1 - xr * yr - xi * yi;
		double b2 = xr * yi - xi * yr;
		double r = Math.sqrt((a1 * a1 + a2 * a2) / (b1 * b1 + b2 * b2));
		return Math.log((1 + r) / (1 - r));
	}

	double getReciprocalScale(ModelPoint mp) {
		return (1 - ((Complex) mp).norm2()) / 2;
	}
	double getScale(ModelPoint mp) {
		return 2 / (1 - ((Complex) mp).norm2());
	}

	public double length2(ModelVector v) {
		double n2 = ((ComplexVector) v).v.real * ((ComplexVector) v).v.real  +  ((ComplexVector) v).v.imag * ((ComplexVector) v).v.imag;
		double baseN2 = ((ComplexVector) v).base.real * ((ComplexVector) v).base.real  +  ((ComplexVector) v).base.imag * ((ComplexVector) v).base.imag;
		double scale = 2 / (1 - baseN2);
		return scale * scale * n2;
	}
	public double length(ModelVector v) {
		double n2 = ((ComplexVector) v).v.real * ((ComplexVector) v).v.real  +  ((ComplexVector) v).v.imag * ((ComplexVector) v).v.imag;
		double baseN2 = ((ComplexVector) v).base.real * ((ComplexVector) v).base.real  +  ((ComplexVector) v).base.imag * ((ComplexVector) v).base.imag;
		double scale = 2 / (1 - baseN2);
		return scale * Math.sqrt(n2);
	}

	public double product(hypergraph.hyperbolic.ModelVector v1, hypergraph.hyperbolic.ModelVector v2) {
		if (v1.getBase().equals(v2.getBase())) {
			double scale = 2 / (1 - ((ComplexVector) v1).base.norm2());
			return scale * scale * (((ComplexVector) v1).v.real * ((ComplexVector) v2).v.real  +  ((ComplexVector) v1).v.imag * ((ComplexVector) v2).v.imag);
		} else
			return Double.NaN;
	}

	/** Liefert exp(tv) mit t derart, dass d(v.base(),exp(tv)) = length,
	 *  und nicht exp(length*v) !!
	 **/
	public ModelPoint exp(hypergraph.hyperbolic.ModelVector vector,double length) {
	// First compute exp at 0 and than use an isometry (isom) to move the result.
	// The pullback isom^*(v) has the same direction as v 
	// but a different length( as a complex number )
		ComplexVector v = (ComplexVector) vector;
		if (v.v.norm2() == 0)
			return (ModelPoint) vector.getBase().clone();
		Complex result;
	
		double t;
		
		t = Math.exp( length );
		t = (t-1) / (t+1);
		
		result = new Complex(v.v);
		result.multiply(t / v.v.norm());
		// at this point, result contains exp at 0
		
		getTranslation(isom1,v.base);		
		isom1.apply(result);	
		return result;	
	}
	
	public hypergraph.hyperbolic.ModelVector getDefaultVector() {
		ComplexVector v = new ComplexVector(new Complex(),new Complex(0.5,0));
		return v;
	}
	
	private ComplexVector v1 = new ComplexVector();
	public void distanceGradient(ModelPoint base,ModelPoint z,ModelVector gradient) {
		((Isometry) isom1).a.real = 1;
		((Isometry) isom1).a.imag = 0;
		((Isometry) isom1).c.real = -((Complex) base).real;
		((Isometry) isom1).c.imag = ((Complex) base).imag;
		
		((Complex) z1).real = ((Complex) z).real;
		((Complex) z1).imag = ((Complex) z).imag;
		
		isom1.apply(z1);

		double length = 2 * Math.sqrt( ((Complex) z1).real * ((Complex) z1).real  +  ((Complex) z1).imag * ((Complex) z1).imag );
		
		v1.base.real = 0;
		v1.base.imag = 0;
		v1.v.real = - ((Complex) z1).real / length; 
		v1.v.imag = - ((Complex) z1).imag / length; 

		((Isometry) isom1).a.real = 1;
		((Isometry) isom1).a.imag = 0;
		((Isometry) isom1).c.real = ((Complex) base).real;
		((Isometry) isom1).c.imag = -((Complex) base).imag;
		
		isom1.apply(v1);
		
		gradient.setTo(v1);
//		getTranslation(isom1,base);
//		z1.setTo(z);
//		isom1.invert();
//		isom1.apply(z1);
//		v1.base.setTo(zero);
//		v1.v.setTo(z1);
//		v1.scale(-1/length(v1));
//		isom1.invert();
//		isom1.apply(v1);
//		gradient.setTo(v1);
	}
	public ModelVector distanceGradient(ModelPoint base,ModelPoint z) {
		ModelVector gradient = new ComplexVector();
		distanceGradient(base,z,gradient);
		return gradient;
	}


public class Isometry implements hypergraph.hyperbolic.Isometry {

	Complex a; // b = \bar c
	Complex c; // d = \bar a


	public Isometry() {
		a = new Complex(1);
		c = new Complex();
	}
	public Isometry(Isometry i) {
		this.a = new Complex(i.a);
		this.c = new Complex(i.c);
	}
	public Isometry(ModelPoint a, ModelPoint c) {
		this(((Complex) a).getReal(),((Complex)a).getImag(),((Complex)c).getReal(),((Complex)c).getImag());
	}
	public Isometry(double ar,double ai, double cr,double ci) {
		// this factor is used to normalise the isometry. 
		// In theory this is not necessary, but it prevents the values from becoming extremely small or large
//		double d2 = ar*ar+ai*ai-cr*cr-ci*ci;
//		//((Complex)a).norm2() - ((Complex) c).norm2();
//		if (d2 > 0) {
//			double d = 1/Math.sqrt(d2);
//			this.a = new Complex(ar*d,ai*d);
//			this.c = new Complex(cr*d,ci*d);
//		}
		this.a = new Complex(ar,ai);
		this.c = new Complex(cr,ci);
		
	}
	public boolean equals(Object isom) {
		return a.equals(((Isometry)isom).a) && c.equals(((Isometry)isom).c);
	}
	public void setToIdentity() {
		a.real = 1;
		a.imag = 0;
		c.real = 0;
		c.imag = 0;
	}
	public void multiplyRight(hypergraph.hyperbolic.Isometry isometry) { // this = this*isometry
		Isometry m = (Isometry) isometry;
	
		double ar = m.a.real*a.real - m.a.imag*a.imag + m.c.real*c.real + m.c.imag*c.imag;
		double ai = m.a.real*a.imag + m.a.imag*a.real - m.c.real*c.imag + m.c.imag*c.real;
		double cr = m.a.real*c.real - m.a.imag*c.imag + m.c.real*a.real + m.c.imag*a.imag;
		double ci = m.a.real*c.imag + m.a.imag*c.real - m.c.real*a.imag + m.c.imag*a.real;
		
		a.real = ar;
		a.imag = ai;
		c.real = cr;
		c.imag = ci;
	}		
	public void multiplyLeft(hypergraph.hyperbolic.Isometry isometry) { // this = isometry * this
		Isometry temp = new Isometry((Isometry) isometry);
		temp.multiplyRight(this);
		this.a = new Complex(temp.a);
		this.c = new Complex(temp.c);
	}
	public void apply(ModelPoint mp1) {
		if (mp1==null)
			return;
		// Multiplication of the isometry with mp1, 
		// which has to be a Complex number, is given by :
		// a * mp1 + conjugate of c
		// ------------------------
		// c * mp1 + conjugate of a
		// Division	of a complex number z by another complex number w is given by :
		// z/w = z*conjugate of w / norm(w)^2
		// Re(z)*Re(w)+Im(z)*Im(w)  +  i*( -Re(z)*Im(w)+Im(z)*Re(w) )
		// ---------------------------------------------------------
		//			Re(w)*Re(w)  +  Im(w)*Im(w)
		double zr,zi; 
		double wr,wi;
		zr = a.real*((Complex) mp1).real - a.imag*((Complex) mp1).imag + c.real;
		zi = a.real*((Complex) mp1).imag + a.imag*((Complex) mp1).real - c.imag;
		wr = c.real*((Complex) mp1).real - c.imag*((Complex) mp1).imag + a.real;
		wi = c.real*((Complex) mp1).imag + c.imag*((Complex) mp1).real - a.imag;
		double n2 = wr*wr + wi*wi;
		((Complex) mp1).real = (zr*wr+zi*wi) / n2 ;
		((Complex) mp1).imag = (-zr*wi+zi*wr) / n2 ;
	}
	public void apply(hypergraph.hyperbolic.ModelVector vector) {
//		ComplexVector v = (ComplexVector) vector;
//		double zr,zi; 
//		double wr,wi;
//		zr = a.real*((Complex) v.base).real - a.imag*((Complex) v.base).imag + c.real;
//		zi = a.real*((Complex) v.base).imag + a.imag*((Complex) v.base).real - c.imag;
//		wr = c.real*((Complex) v.base).real - c.imag*((Complex) v.base).imag + a.real;
//		wi = c.real*((Complex) v.base).imag + c.imag*((Complex) v.base).real - a.imag;
//		double n2 = wr*wr + wi*wi;
//
//		double ar = (wr*wr-wi*wi) / n2;
//		double ai = -2*wi*wr / n2;
//
//		v.v.real = ar*v.v.real - ai*v.v.imag;
//		v.v.imag = ar*v.v.imag + ai*v.v.real;
//		
//		v.base.real =  (zr*wr+zi*wi) / n2 ;
//		v.base.imag = (-zr*wi+zi*wr) / n2 ;

		ComplexVector v = (ComplexVector) vector;

		z3.setTo(c);
		((Complex)z3).multiply(v.base);
		a.conjugate();
		((Complex)z3).add(a);
		a.conjugate();
		((Complex)z3).multiply(((Complex)z3));
		((Complex)z3).reciprocal();
		((Complex)z3).multiply(a.norm2()-c.norm2());
		v.v.multiply(((Complex)z3));

		apply(v.base);

	}

	public hypergraph.hyperbolic.Isometry getInvers() {
		return new Isometry(new Complex(a.getReal(),-a.getImag()),
							new Complex(-c.getReal(),-c.getImag()) );
	}
	public void invert() {
		a.imag = -a.imag;
		c.real = -c.real;
		c.imag = -c.imag;
	}

	public void setTo(hypergraph.hyperbolic.Isometry isom) {
		a.real = ((Isometry)isom).a.real;
		a.imag = ((Isometry)isom).a.imag;
		c.real = ((Isometry)isom).c.real;
		c.imag = ((Isometry)isom).c.imag;
	}
	public Object clone() {
		return new Isometry(this);
	}
	public String toString() {
		return "( " + a + " ; " + c + " )";
	}
} // end of class Isometry

} // end of class PoincareModel
