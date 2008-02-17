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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * This class provides a skeleton implementation of the <code>Model</code> interface
 * to minimize the effort to implement a new model. It provides implementation
 * of a view matrix, handling of listeners and some of isometry providing methods.
 * For a given model, it is usually much more efficient to implement the isometry
 * providing methods directly.
 * @author Jens Kanschik
 */
public abstract class AbstractModel implements Model {

	protected	Isometry	viewMatrix;
	private		Isometry	inversViewMatrix;
	// The differential of the viewmatrix will always be 1 at the orientationRoot
	protected ModelPoint	orientationRoot;

	protected Isometry	isom1 = getIdentity();
	protected Isometry	isom2 = getIdentity();
	protected Isometry	isom3 = getIdentity();
	protected ModelPoint z1 = getOrigin();
	protected ModelPoint z2 = getOrigin();
	protected ModelPoint z3 = getOrigin();
	protected ModelPoint z4 = getOrigin();
	protected ModelPoint zero = getOrigin();

	protected transient ChangeEvent changeEvent = null;
	protected EventListenerList listenerList = new EventListenerList();

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	public ChangeListener[] getChangeListeners() {
		return (ChangeListener[]) listenerList.getListeners(ChangeListener.class);
	}
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

	/**
	 * Returns the invers of the view matrix.
	 * @return The invers of the view matrix.
	 */
	public Isometry getInversViewMatrix() {
		return inversViewMatrix;
	}
	/**
	 * Returns the view matrix.
	 * @return The view matrix.
	 */
	public Isometry getViewMatrix() {
		return viewMatrix;
	}
	/**
	 * Sets the view matrix and computes the invers view matrix.
	 * If the new view matrix is <code>null</code>,
	 * the view matrix is set to the identity transformation.
	 * This method informs all change listeners about this change.
	 *
	 * @param vm The new view matrix.
	 */
	public void setViewMatrix(Isometry vm) {
		if (vm != null) {
			viewMatrix = vm;
			inversViewMatrix = viewMatrix.getInvers();
		} else {
			viewMatrix = getIdentity();
			inversViewMatrix = getIdentity();
		}
		fireStateChanged();
	}
	public void setOrientationRoot(ModelPoint mp) {
		orientationRoot = mp;
		setViewMatrix(getViewMatrix());
	}


	/**
	 * {@inheritDoc}
	 * The implementation uses method <code>getRotation(double)</code>, because
	 * a rotation of 0 degree around any point is nothing but the identity transformation.
	 *@return {@inheritDoc}
	 */
	public Isometry getIdentity() {
		return getRotation(0);
	}
	/**
	 * {@inheritDoc}
	 * The implementation builds the isometry using methods <code>getRotation(double)</code>,
	 * <code>getTranslation(ModelPoint)</code> and <code>Isometry#invert()</code>, i.e.
	 * it conjugates <code>getRotation(double)</code>.
	 *
	 *@return {@inheritDoc}
	 */
	public hypergraph.hyperbolic.Isometry getRotation(ModelPoint mp, double alpha) {
		Isometry translation1 = getTranslation(mp);
		Isometry translation3 = (Isometry) translation1.clone();
		translation1.invert();

		translation3.multiplyRight(getRotation(alpha));
		translation3.multiplyRight(translation1);

		return translation3;
	}

	/**
	 * {@inheritDoc}
	 * Calls <code>getTranslation(ModelPoint,double)</code>.
	 *
	 *@return {@inheritDoc}
	 */
	public Isometry getTranslation(ModelPoint mp1) {
		Isometry isom = getIdentity();
		getTranslation(isom, mp1);
		return isom;
	}
	public void getTranslation(Isometry isom, ModelPoint mp1) {
		getTranslation(isom, mp1, 1);
	}
	/**
	 * {@inheritDoc}
	 * Calls <code>getTranslation(ModelPoint,ModelPoint,double)</code>.
	 */
	public void getTranslation(Isometry isom, ModelPoint mp1, ModelPoint mp2) {
		getTranslation(isom, mp1, mp2, 1);
	}
	public Isometry getTranslation(ModelPoint mp1, ModelPoint mp2) {
		Isometry isom = getIdentity();
		getTranslation(isom, mp1, mp2);
		return isom;
	}

	public Isometry getTranslation(ModelPoint mp, double t) {
		Isometry isom = getIdentity();
		getTranslation(isom, mp, t);
		return isom;
	}

	/**
	 * {@inheritDoc}
	 * The implementation builds the isometry using methods <code>getTranslation(ModelPoint,double)</code>,
	 * <code>getTranslation(ModelPoint)</code> and <code>Isometry#invert()</code>, i.e.
	 * it conjugates <code>getRotation(double)</code>.
	 *
	 *@return {@inheritDoc}
	 */
	public Isometry getTranslation(ModelPoint mp1, ModelPoint mp2, double t) {
		Isometry isom = getIdentity();
		getTranslation(isom, mp1, mp2, t);
		return isom;
	}
	public void getTranslation(Isometry isom, ModelPoint mp1, ModelPoint mp2, double t) {
		z1.setTo(mp2);
		try {
			getTranslation(isom1, mp1);
			isom.setTo(isom1);
			isom1.invert();

			isom1.apply(z1);

			getTranslation(isom2, z1, t);

			isom2.multiplyRight(isom1);
			isom.multiplyRight(isom2);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(mp1);
			System.out.println(mp2);
			System.out.println(t);
			System.out.println(isom1);
			System.out.println(isom2);
			System.out.println(isom);
		}
	}
	public void getTranslation(Isometry isom, ModelVector vector, double t) {
		getTranslation(isom, vector.getBase(), exp(vector, t));
	}
	public hypergraph.hyperbolic.Isometry getTranslation(ModelVector vector, double t) {
		Isometry isom = getIdentity();
		getTranslation(isom, vector, t);
		return isom;
	}

	// Obviously, one of the two following methods has to be overwritten
	// It's usually the first
	public double dist2(ModelPoint z) {
		double d = dist(z);
		return d * d;
	}
	public double dist(ModelPoint z) {
		return Math.sqrt(dist2(z));
	}
	public double dist(ModelPoint mp1, ModelPoint mp2) {
		return Math.sqrt(dist2(mp1, mp2));
	}
	public double dist2(ModelPoint mp1, ModelPoint mp2) {
		double d = dist(mp1, mp2);
		return d * d;
	}
	public double length2(ModelVector v) {
		return product(v, v);
	}
	public double length(ModelVector v) {
		return Math.sqrt(length2(v));
	}

}
