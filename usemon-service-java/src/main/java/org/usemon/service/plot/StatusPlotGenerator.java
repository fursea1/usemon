/**
 * 
 */
package org.usemon.service.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

/**
 * This implementation will generate a chart in which the following facts are
 * plotted agains the time axis (X-axis):
 * <ul>
 * <li>invocation_count</li>
 * <li>max_reponse_time</li>
 * <li>avg_response_time</li>
 * <li>checked_exceptions
 * </ul>
 * <code>
 * <p>This is the SQL used to select the data:
 * <pre>
 * select d_time.hh,mm,ss, 
 * 		invocation_count, 
 * 		max_response_time, 
 * 		avg_response_time, 
 * 		checked_exceptions, 
 * 		unchecked_exceptions 
 * from 
 * 	d_time join method_measurement_fact on d_time.id=method_measurement_fact.d_time_id;
 * </pre>
 * </code>
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class StatusPlotGenerator implements PlotGenerator {

	StatusDatasetProducer datasetProducer = null;
	private BasicStroke basicStroke = 	new BasicStroke(4.0f);;
	
	/**
	 * @return the datasetProducer
	 */
	public DatasetProducer getDatasetProducer() {
		return datasetProducer;
	}

	/**
	 * @param datasetProducer the datasetProducer to set
	 */
	public void setDatasetProducer(DatasetProducer datasetProducer) {
		if (datasetProducer instanceof StatusDatasetProducer)
			this.datasetProducer = (StatusDatasetProducer) datasetProducer;
		else
			throw new IllegalArgumentException("Provided dataset producer must implement interface " + StatusDatasetProducer.class.getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.usemon.service.plot.PlotGenerator#generateChart()
	 */
	public JFreeChart generateChart() {
		StatusDataset statusDataset =  (StatusDataset) datasetProducer.produceData();
		
		// Exceptions
		XYPlot exceptionsPlot = exceptionsPlot(statusDataset);

		// Invocation count
		XYPlot invocationCountPlot = invocationCountPlot(statusDataset);

		// average response time
		XYPlot avgResponseTimePlot = responseTimePlot(statusDataset);
		
		// Domain (x) axis to be shared by all plots
		DateAxis domainAxis = new DateAxis("Time");

		// Indicates the ticks on the domain axis
		// Every second minute, this seems to be the default anyway
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		DateTickUnit dateTickUnit = new DateTickUnit(DateTickUnit.MINUTE,2,dateFormat);
		domainAxis.setTickUnit(dateTickUnit);
		
		// Creates the chart in which all plots share the domain axis
		CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(domainAxis);
		cplot.add(invocationCountPlot, 1);
		cplot.add(avgResponseTimePlot,1);
		cplot.add(exceptionsPlot, 1);
		cplot.setGap(8.0);
		cplot.setDomainGridlinesVisible(true);

		// return a new chart containing the overlaid plot...
		JFreeChart chart = new JFreeChart("Reponse times, exceptions, invocations vs. time", JFreeChart.DEFAULT_TITLE_FONT, cplot, false);
		chart.setBackgroundPaint(new Color(235,235,235));	// gray 8%
		
		// Sets the source reference
		// FIXME: make source title dynamic
		TextTitle source = new TextTitle("Source: http://localhost/service/plot?type=status", new Font("Dialog", Font.PLAIN, 10));
		source.setPosition(RectangleEdge.BOTTOM);
		source.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		chart.addSubtitle(source);
		
		
		TextTitle periodTitle = new TextTitle(statusDataset.getStart() + " to " + statusDataset.getEnd());
		chart.addSubtitle(periodTitle);
		
		LegendTitle legend = new LegendTitle(cplot);
		legend.setMargin(0, 20, 0, 0);
		legend.setPosition(RectangleEdge.BOTTOM);
		legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
		chart.addSubtitle(legend);
		
		return chart;
	}

	private XYPlot responseTimePlot(StatusDataset statusDataset) {
		TimeSeriesCollection responseTimeDataset = statusDataset.getResponseTimeDataset();
		XYItemRenderer responseTimeRenderer = new XYLineAndShapeRenderer(true,true);
		responseTimeRenderer.setBaseStroke(basicStroke);
		XYPlot responseTimePlot = new XYPlot(responseTimeDataset, null, new NumberAxis("Reponse times"), responseTimeRenderer);
		responseTimePlot.setBackgroundPaint(Color.WHITE);
		responseTimePlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		responseTimePlot.setRangeGridlinePaint(Color.GRAY);
		
		return responseTimePlot;
	}

	/**
	 * @param statusDataset
	 * @return
	 */
	private XYPlot invocationCountPlot(StatusDataset statusDataset) {
		TimeSeriesCollection invocationCountDataset = statusDataset.getInvocationCountDataset();
		XYItemRenderer invocationCountRederer = new XYLineAndShapeRenderer(true,true);
		invocationCountRederer.setBaseStroke(basicStroke);
		
		XYPlot invocationCountPlot = new XYPlot(invocationCountDataset, null, new NumberAxis("invocation count"), invocationCountRederer);
		invocationCountPlot.setBackgroundPaint(Color.WHITE);
		invocationCountPlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		invocationCountPlot.setRangeGridlinePaint(Color.GRAY);
		return invocationCountPlot;
	}

	/** Creates the plot for the exceptions.
	 * 
	 * @param statusDataset
	 * @return
	 */
	private XYPlot exceptionsPlot(StatusDataset statusDataset) {
		// Retrieves data for the exceptions (2 series: checked and unchecked)
		TimeSeriesCollection exceptionsDataset = statusDataset.getExceptionDataset();
		
		XYItemRenderer exceptionsRenderer = new XYLineAndShapeRenderer(true, true);
		
		exceptionsRenderer.setSeriesStroke(0, new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		exceptionsRenderer.setSeriesStroke(1, new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
//		exceptionsRenderer.setSeriesPaint(0, Color.blue);

		exceptionsRenderer.setSeriesPaint(0, Color.ORANGE);
		exceptionsRenderer.setSeriesPaint(1, Color.MAGENTA);

		ValueAxis exceptionsCountAxis = new NumberAxis("exception count");
		
		XYPlot exceptionsPlot = new XYPlot(exceptionsDataset, null, exceptionsCountAxis, exceptionsRenderer);
		exceptionsPlot.setBackgroundPaint(Color.WHITE);
		exceptionsPlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		exceptionsPlot.setRangeGridlinePaint(Color.GRAY);
		return exceptionsPlot;
	}

	/** 
	 * Injects the parameters for modifying the production of this plot.
	 * 
	 * @see org.usemon.service.plot.PlotGenerator#setParametersMap(java.util.Map)
	 */
	public void setParametersMap(Map<String,?> params) {
	}



}
