/**
 * 
 */
package org.usemon.service.plot;

import java.util.Map;

import org.jfree.chart.JFreeChart;

/**
 * Provides for generating a usemon specific JFreeChart plot.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface PlotGenerator {

	/** Generates the chart */
	JFreeChart generateChart();
	
	/** Injects the parameters map obtained from the HTTP request. Note! each 
	 * entry has a string key and an array of string objects as values 
	 */
	void setParametersMap(Map<String, ?> params);

	/** Injects the data set producer
	 * @param datasetProducer the datasetProducer to set
	 */
	public void setDatasetProducer(DatasetProducer datasetProducer);
}
