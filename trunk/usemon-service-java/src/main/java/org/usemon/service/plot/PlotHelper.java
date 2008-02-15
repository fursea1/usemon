/**
 * 
 */
package org.usemon.service.plot;

import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.service.DbmsHelper;

/**
 * Creates a JFreeChart plot based upon various parameters set in a supplied set
 * of parameters which are typically obtained from the HttpServletRequest
 * 
 * @author t547116
 * 
 */
public class PlotHelper {

	private static final Logger log = LoggerFactory.getLogger(PlotHelper.class);

	public static final String PLOT_PARAM = "plot";

	public static JFreeChart createPlot(final Map<String, ?> parameterMap) {

		String plotTypeName = fetchParamValue(parameterMap, PLOT_PARAM);
		PlotType plotType = plotTypeName != null ? PlotType.valueOf(plotTypeName.toUpperCase()) : PlotType.STATUS;
		JFreeChart chart = null;
		if (log.isDebugEnabled()) {
			log.debug("Plotting chart of type " + plotType);
		}
		switch (plotType) {
		case STATUS:
			chart = createStatusPlot(parameterMap);
			break;
		default:
			chart = sampleChart(parameterMap);
			break;
		}
		return chart;
	}

	private static JFreeChart createStatusPlot(Map<String, ?> parameterMap) {
		PlotGenerator plotGenerator = new StatusPlotGenerator();
		plotGenerator.setParametersMap(parameterMap);

		StatusDatasetProducerImpl statusDatasetProducerImpl = new StatusDatasetProducerImpl();
		statusDatasetProducerImpl.setDatasource(DbmsHelper.getDataSource());

		plotGenerator.setDatasetProducer(statusDatasetProducerImpl);
		JFreeChart chart = plotGenerator.generateChart();

		return chart;
	}

	private static JFreeChart sampleChart(Map<String, ? extends Object> parameterMap) {
		/*
		 * Create the data for this example.
		 */
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("One", 10.0);
		dataset.setValue("Two", 50.0);
		dataset.setValue("Three", 30.0);
		dataset.setValue("Four", 40.0);

		/*
		 * Generate the generic pie chart.
		 */
		JFreeChart chart = ChartFactory.createPieChart("Usemon Sample Pie Chart", dataset, false, false, false);
		return chart;

	}

	/**
	 * @param parameterMap
	 * @return
	 */
	private static String fetchParamValue(Map<String, ? extends Object> parameterMap, String paramName) {
		String result = null;
		if (parameterMap.containsKey(paramName)) {
			Object value = parameterMap.get(paramName);
			if (value instanceof String[]) {
				result = ((String[]) value)[0];
			}
		}
		return result;
	}
}
