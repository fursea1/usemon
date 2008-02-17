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

import javax.swing.event.ChangeListener;

/** Dieses Interface ist Grundlage für alle Beschreibungen der hyperbolischen Ebene.
 * @author Jens Kanschik
 */

public interface Model {

    /** Returns the origin of the model.
     * A model doesn't present a riemannian manifold, but the pair (M, p) of a
     * riemannian manifold and a fixed point, the origin.
     * For a vector space, the origin is naturally 0.
     * @return The origin of the model.
     */
    public ModelPoint getOrigin();

	/** Returns a "default" vector at the origin.
	 * In most models, there is no special vector,
	 * but it is very convenient to have such a default vector to start with.
	 * This vector is always located at the origin, is always normalized (i.e. has length 1)
	 * and it always has to be the same.
	 *
	 * @return A normalized vector at the origin.
	 */
    public ModelVector getDefaultVector();

    /** Returns the identity transformation.
     * @return The identity transformation.
     */
    public Isometry getIdentity();

    /** Returns a rotation of <code>alpha</code> at the origin.
     * Only well defined in two dimensional, oriented models.
     * @param alpha The angle of the rotation.
     * @return The rotation at the origin with angle <code>alpha</code>.
     */
    public Isometry getRotation(double alpha);

	/** Returns a rotation of <code>alpha</code> at the point <code>mp</code>.
	 * Only well defined in two dimensional, oriented models.
	 * @param mp The basepoint of the rotation.
	 * @param alpha The angle of the rotation.
	 * @return The rotation at <code>mp</code> with angle <code>alpha</code>.
	 */
    public Isometry getRotation(ModelPoint mp, double alpha);

    /** Erzeugt eine Verschiebung, die den Nullpunkt auf einen gegebenen Punkt abbildet.
     * @param z Der Zielpunkt.
     */
    public Isometry getTranslation(ModelPoint z); 

    /** Erzeugt die n-te Wurzel der Verschiebung, die den Nullpunkt auf einen gegebenen Punkt abbildet.
     * @param z Der Zielpunkt.
     */
    public Isometry getTranslation(ModelPoint z, double t);

	public void getTranslation(Isometry isom, ModelPoint mp, double t);

    /** Erzeugt eine Verschiebung, die einen Punkt auf einen anderen Punkt abbildet.
     * @param z1 Wird nach @param z2 verschoben.
     */
    public Isometry getTranslation(ModelPoint z1,ModelPoint z2);

    /** Erzeugt die n-te Wurzel der Verschiebung, die einen Punkt auf einen anderen Punkt abbildet.
     * @param z1 Wird nach @param z2 verschoben.
     */
    public Isometry getTranslation(ModelPoint z1,ModelPoint z2,double t);

    /** Erzeugt die Verschiebung, die den Fusspunkt von @param vector in die Richtung @param vector verschiebt. @param t gibt den Abstand vom Fusspunkt zum Zielpunkt an.
     */
	public Isometry getTranslation(ModelVector vector, double t);

	public void getTranslation(Isometry isom,ModelVector vector, double t);

    /** Computes the distance from <code>z</code> to the origin.
     * @param z A point in the model.
     * @return The distance from <code>z</code> to the origin.
     */
    public double dist(ModelPoint z);

	/** Computes the squared distance from <code>z</code> to the origin.
	 * @param z A point in the model.
	 * @return The squared distance from <code>z</code> to the origin.
	 */
    public double dist2(ModelPoint z);

     /** Computes the distance between two points.
     * @param z1 A point in the model.
     * @param z2 A point in the model.
     * @return The distance of the two points.
     */
	public double dist(ModelPoint z1, ModelPoint z2);

	/** Computes the squared distance between two points.
	* @param z1 A point in the model.
	* @param z2 A point in the model.
	* @return The squared distance of the two points.
	*/
	public double dist2(ModelPoint z1, ModelPoint z2);

	/** Returns the distance of <code>p</code> to the line, ray or line segment
	 * from <code>z1</code> to <code>z2</code>.
	 * The parameters <code>cutoff1</code> and <code>cutoff2</code>
	 * define whether the whole line should be considered or only parts.
	 * <ul>
	 * <li><code>getDistance(p, z1, z2, true, true)</code>
	 * returns the distance to the line segment.</li>
	 * <li><code>getDistance(p, z1, z2, false, true)</code>
	 * returns the distance to the ray starting in <code>z1</code>.</li>
	 * <li><code>getDistance(p, z1, z2, false, false)</code>
	 * returns the distance to the whole (infinite) line.</li>
	 * </ul>
	 * If the geometry of the model
	 * allows to give the distance a sign using a well defined orientation,
	 * (e.g. for most two dimensional models) it should do so, but it is not required.
	 *
	 * @param p        The point from which the distance is computed.
	 * @param z1       The starting point of the line segment.
	 * @param z2       The end point of the line segment.
	 * @param cutoff1  If true, the line is cut off at <code>z1</code>.
	 * @param cutoff2  If true, the line is cut off at <code>z2</code>.
	 * @return         The distance from <code>p</code> to the line,
	 *                 ray or linesegment defined by the other parameters.
	 */
	public double getDistance(
					ModelPoint p,
					ModelPoint z1, ModelPoint z2,
					boolean cutoff1, boolean cutoff2);

	/** Returns the projection of point <code>p</code> on the line defined by
	 * the points <code>z1</code> and <code>z2</code>.
	 * @param p The point to project.
	 * @param z1 A point on the line on which to project.
	 * @param z2 A point on the line on which to project.
	 * @return The projection.
	 */
	public ModelPoint getProjection(ModelPoint p, ModelPoint z1, ModelPoint z2, boolean cutoff1, boolean cutoff2);

	/** Returns the angle between the line segments
	 * starting at <code>p</code> and ending in <code>z1</code>
	 * and <code>z2</code> respectively.
	 * If the model is orientated, the angle may have a sign, but this is not required.
	 *
	 * @param p        The commong starting point of the two segments.
	 * @param z1       The end point of the first line segment.
	 * @param z2       The end point of the first line segment.
	 * @return         The angle of the hinge defined by the three parameters.
	 */
	public double getAngle(ModelPoint p, ModelPoint z1, ModelPoint z2);

	/** Returns the scalar product of the two vectors.
	 * They have to have the same base point, otherwise, a computation is not possible.
	 * <code>NaN</code> is returned in this situation.
	 * @param v1 A vector in the tangentspace of the model.
	 * @param v2 A vector in the tangentspace of the model.
	 * @return The scalar product of both vectors.
	 */
	public double product(ModelVector v1, ModelVector v2);
	/** Returns the squared length of the vector.
	 * @param v A vector in the tangentspace of the model.
	 * @return The squared length of the vector.
	 */
	public double length2(ModelVector v);
	/** Returns the length of the vector.
	 * @param v A vector in the tangentspace of the model.
	 * @return The length of the vector.
	 */
	public double length(ModelVector v);

	public ModelPoint exp(ModelVector v,double length);

	/**
	 * Computes the gradient at <code>basePoint</code> of the distance functions to
	 * <code>z</code>, i.e.
	 * <pre>
	 *       grad           d(*,z)
	 *           |basepoint
	 * </pre>
	 * or "grad_{|basepoint}d(\cdot,z)" in tex.
	 *
	 * @param base
	 * @param z
	 * @return
	 */
	public ModelVector distanceGradient(ModelPoint base,ModelPoint z);
	public void distanceGradient(ModelPoint base,ModelPoint z,ModelVector gradient);

	public Isometry getInversViewMatrix();
	public Isometry getViewMatrix();
	public void setViewMatrix(Isometry vm);

	public void addChangeListener(ChangeListener l);
	public void removeChangeListener(ChangeListener l);
	public ChangeListener[] getChangeListeners();

}



