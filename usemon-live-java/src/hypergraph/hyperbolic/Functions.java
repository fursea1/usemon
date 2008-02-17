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
 * Utility class that provides special functions that are not available in standard
 * java.
 * @author Jens Kanschik
 */
public class Functions {

	/**
	 * Returns sinus hyperbolicus :
	 * <code>sinh(x) = 0.5*(e^x-e^(-x))</code>.
	 * @param x
	 * @return
	 */
    public static double sinh(double x) {
    	double expX = Math.exp(x);
		return 0.5 * (expX - 1/expX);
    }
	/**
	 * Returns cosinus hyperbolicus :
	 * <code>cosh(x) = 0.5*(e^x+e^(-x))</code>.
	 * @param x
	 * @return
	 */
    public static double cosh(double x) {
    	double expX = Math.exp(x);
		return 0.5 * (expX + 1/expX);
    }
	/**
	 * Returns invers of sinus hyperbolicus :
	 * <code>arsinh(x) = ln(x+sqrt(x^2+1))</code>.
	 * @param x
	 * @return
	 */
    public static double arsinh(double x) {
		return Math.log(x + Math.sqrt(x * x + 1));
    }
	/**
	 * Returns invers of cosinus hyperbolicus :
	 * <code>arcosh(x) = ln(x+sqrt(x^2-1))</code>.
	 * @param x
	 * @return
	 */
    public static double arcosh(double x) {
		return Math.log(x + Math.sqrt(x * x - 1));
    }

}
