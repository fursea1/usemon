/**
 * Created 14. nov.. 2007 17.25.07 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class GraphTest {

	@Test
	public void testSampleGraph() {
		Graph g = new Graph();
		Long l = genUid();
		InstanceNode n1 = new InstanceNode("p" + l , "c" + l, "i" + l);
		l = genUid();
		InstanceNode n2 = new InstanceNode("p" + l , "c" + l, "i" + l);
		l = genUid();
		InstanceNode n3 = new InstanceNode("p" + l , "c" + l, "i" + l);

		g.addNode(n1);
		g.addNode(n2);
		
		g.addEdge(new Edge(n1,n2));
		g.addEdge(new Edge(n2,n3));
		g.addEdge(new Edge(n1,n3));
		assertEquals("Nodes",3, g.getNodes().size());
		assertEquals("Edges", 3, g.getEdges().size());
		
		for (InstanceNode node : g.getNodes()) {
			assertNotNull("Node has no id", node.getUid());
		}
	}

	/**
	 * @return
	 */
	private long genUid() {
		return Math.round(1000 * Math.random());
	}
}
