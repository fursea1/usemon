/**
 * 
 */
package org.usemon.integrationtest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.usemon.service.plot.PlotHelper;
import org.usemon.service.plot.PlotType;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class PlotHelperTest extends TestCase {

	public void testStatusPlot() throws InterruptedException, IOException {
		generatePlot();
	}

	/**
	 * @return 
	 * 
	 */
	private JFreeChart generatePlot() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PlotHelper.PLOT_PARAM, PlotType.STATUS.toString());
		
		JFreeChart chart = PlotHelper.createPlot(params);
		return chart;
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PlotHelperTest plotHelperTest = new PlotHelperTest();
		plotHelperTest.setUp();
		JFreeChart chart = plotHelperTest.generatePlot();
		ChartFrame frame = new ChartFrame("PlotHelperTest", chart);
		frame.pack();
		frame.setVisible(true);
		
	}
}
