/**
 * 
 */
package org.usemon.service.plot;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class StatusPlotGeneratorTest extends TestCase {

	DataSource ds = null;
	StatusDatasetProducer statusDatasetProducer = null;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		ds = EasyMock.createMock(DataSource.class);
		statusDatasetProducer = EasyMock.createMock(StatusDatasetProducer.class);
	}

	/**
	 * Test method for {@link org.usemon.service.plot.StatusPlotGenerator#generateChart()}.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void testGenerateChart() throws IOException, InterruptedException {
		generateSample();
	}

	/**
	 * 
	 */
	private JFreeChart generateSample() {
		expect(statusDatasetProducer.produceData()).andReturn(StatusDatasetTest.generateDummyStatusDataSet());
		replay(statusDatasetProducer);
		StatusPlotGenerator statusPlotGenerator = new StatusPlotGenerator();
		statusPlotGenerator.setDatasetProducer(statusDatasetProducer);
		JFreeChart chart = statusPlotGenerator.generateChart();
		return chart;
	}
	

	public static void main(String[] args) throws Exception {
		StatusPlotGeneratorTest plotGeneratorTest = new StatusPlotGeneratorTest();
		plotGeneratorTest.setUp();
		JFreeChart chart = plotGeneratorTest.generateSample();
		
		ChartFrame frame = new ChartFrame("testStatusPlot", chart);
		frame.pack();
		frame.setVisible(true);
	}
}
