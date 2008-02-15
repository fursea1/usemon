/**
 * Created 14. nov.. 2007 17.47.56 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;


import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class GMLWriterVisitorTest {

	Graph g;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		g = DummyGraphGenerator.generateGraphWithClassNodes();
	}
	
	@Test
	public void testSimple() {
		GMLWriterVisitor writerVisitor = new GMLWriterVisitor();
		StringWriter sw = new StringWriter();
		writerVisitor.generateMarkup(sw, g);
		assertTrue(sw.getBuffer().length() > 0);
		assertTrue("null".equals(sw.toString()) == false); 
		System.out.println(sw.toString());
	}
}
