/**
 * Created 14. nov.. 2007 17.49.47 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;

/**
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class DummyGraphGenerator {


	public static Graph generateGraphWithClassNodes() {
		Graph g = new Graph();
		Long l = genUid();
		InstanceNode n1 = new InstanceNode(l,"pkg" + l,"c" + l, "obj" + l);
		l = genUid();
		InstanceNode n2 = new InstanceNode(l,"pkg" + l,"c" + l, "obj" + l);
		l = genUid();
		InstanceNode n3 = new InstanceNode(l,"pkg" + l,"c" + l, "obj" + l);
		g.addNode(n1);
		g.addNode(n2);

		g.addEdge(new Edge(n1, n2));
		g.addEdge(new Edge(n2, n3));
		return g;
		
	}
	/**
	 * @return
	 */
	private static long genUid() {
		return Math.round(1000 * Math.random());
	}
}
