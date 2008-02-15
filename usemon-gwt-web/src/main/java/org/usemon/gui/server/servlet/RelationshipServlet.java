/**
 * Created 19. nov.. 2007 10.07.52 by Steinar Overbeck Cook
 */
package org.usemon.gui.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.InvocationService;
import org.usemon.domain.UsemonServiceLocator;
import org.usemon.domain.graph.GMLWriterVisitor;
import org.usemon.domain.graph.Graph;
import org.usemon.domain.graph.InvocationDetailLevel;


/**
 * Downloads the raw data for a relationship graph.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class RelationshipServlet extends javax.servlet.http.HttpServlet {
	Logger log = LoggerFactory.getLogger(RelationshipServlet.class);
			
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Most browsers will propmt the user to save the downloaded
		// data into a file.
		log.info("Producing relationship data...");
		resp.setContentType("application/data");
		InvocationService invocationService = UsemonServiceLocator.getInvocationService();
		Graph graph = invocationService.loadGraph(InvocationDetailLevel.CLASS);
		
		GMLWriterVisitor writerVisitor = new GMLWriterVisitor();
		log.debug("Writing markup to http stream...");
		writerVisitor.generateMarkup(resp.getWriter(), graph);
		log.debug("Completed writing markup to http stream");
	}
}
