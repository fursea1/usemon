/**
 * Created 14. nov.. 2007 17.34.40 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface GraphVisitor {

	Object visit(Edge edge);

	Object visit(Graph graph);

	Object visit(Node node);

	Object visit(InstanceNode node);
}
