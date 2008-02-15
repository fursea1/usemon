/**
 * Created 15. nov.. 2007 17.05.28 by Steinar Overbeck Cook
 */
package org.usemon.integrationtest;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.usemon.domain.InvocationService;
import org.usemon.domain.UsemonServiceLocator;
import org.usemon.domain.graph.GMLWriterVisitor;
import org.usemon.domain.graph.Graph;
import org.usemon.domain.graph.InvocationDetailLevel;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class InvocationServiceImplTest {

	/**
	 * Test method for {@link org.usemon.service.InvocationServiceImpl#loadGraph(org.usemon.domain.graph.InvocationDetailLevel)}.
	 * @throws IOException 
	 */
	@Test
	public void testLoadGraph() throws IOException {
		InvocationService invocationService = UsemonServiceLocator.getInvocationService();
		Graph graph = invocationService.loadGraph(InvocationDetailLevel.CLASS);
		System.getProperty("java.io.tmpdir");
		FileWriter fileWriter = new FileWriter( System.getProperty("java.io.tmpdir") + "/t.gml");
		
		GMLWriterVisitor writerVisitor = new GMLWriterVisitor();
		writerVisitor.generateMarkup(fileWriter, graph);
		fileWriter.close();
	}

}
