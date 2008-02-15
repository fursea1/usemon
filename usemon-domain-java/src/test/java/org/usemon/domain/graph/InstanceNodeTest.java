/**
 * Created 20. nov.. 2007 17.21.27 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class InstanceNodeTest {

	/**
	 * Test method for {@link org.usemon.domain.graph.InstanceNode#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		InstanceNode node1 = new InstanceNode("p1","c1","i1");
		InstanceNode node2 = new InstanceNode("p1","c1","i1");
		InstanceNode node3 = new InstanceNode("p1","c1","i2");
		
		assertEquals(node1,node2);
		assertTrue(node1.equals(node3) == false);
	}
}
