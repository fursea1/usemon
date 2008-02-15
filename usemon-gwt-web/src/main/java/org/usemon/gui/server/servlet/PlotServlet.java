/**
 * 
 */
package org.usemon.gui.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.service.plot.PlotHelper;

/**
 * Generates a PNG graph Based upon the supplied HTTP parameters. This is done by passing the
 * map of HTTP parameters to {@link PlotHelper#createPlot(java.util.Map)}, which will
 * create a plot according to the parameters.
 * <p>The resulting {@link JFreeChart} instance is converted to a PNG-image having a width and height in pixels as specified
 * by the HTTP parameters <code>width</code> and <code>height</code>.
 *  
 * @author t547116
 *
 */
public class PlotServlet extends  javax.servlet.http.HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(PlotServlet.class);
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("Query string:" + req.getQueryString());
		}
		// Creates the plot based upon the supplied parameters
		JFreeChart chart = PlotHelper.createPlot(req.getParameterMap());
		
		// Transforms a plot into a PNG and writes it into the output stream
		resp.setContentType("image/png");
		
		// Retrieves the height and width from the request using reasonable defaults
		int width = getParameterAsInt(req, "width", 800);
		int heigth = getParameterAsInt(req, "height", 640);		
		if (log.isDebugEnabled()) {
			log.debug("Creating plot " + width + "x" + heigth);
		}
		ChartUtilities.writeChartAsPNG(resp.getOutputStream(), chart, width, heigth);
	}

	/** Attempts to find the named parameter and parse it into an integer handling any problems
	 * that might occur, like invalid number, etc.
	 * @param req currentWizardStepPanel http request
	 * @param paramName name of http parameter
	 * @param defaultValue value to return of something goes wrong
	 * @return either the parsed value of the named parameter or the defaultValue.
	 */
	private int getParameterAsInt(HttpServletRequest req, String paramName, int defaultValue) {
		try {
			String paramValue = req.getParameter(paramName);
			int result = paramValue != null ? Integer.parseInt(paramValue) : defaultValue;
			return result;
		} catch (NumberFormatException e) {
			// ignore this, somebody probably specified invalid value for height
			return defaultValue;
		}
	}
	
}
