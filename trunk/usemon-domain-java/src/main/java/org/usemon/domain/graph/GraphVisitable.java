/**
 * Created 14. nov.. 2007 17.34.52 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface GraphVisitable {
	public Object accept(GraphVisitor visitor);
}
