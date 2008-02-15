package org.usemon.service.plot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.jfree.data.general.Dataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Holds the data set which in turn holds the data sets used for
 * plotting the status of the system.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class StatusDataset implements DatasetCollection {
	TimeSeriesCollection invocationCountDataset = null;
	TimeSeriesCollection responseTimeDataset = null;
	TimeSeries maxResponseTimeSeries = null;
	TimeSeries avgResponseTimeSeries = null;
	TimeSeries uncheckedExceptionsDataset = null;
	TimeSeries checkedExceptionsDataset = null;
	TimeSeriesCollection exceptionDataset = null;
	
	Date end;
	Date start;
	public Date getEnd() {
		return end;
	}

	public Date getStart() {
		return start;
	}

	public StatusDataset() {
		invocationCountDataset = new TimeSeriesCollection(new TimeSeries("Invocation count", Minute.class));

		// Maximum and average response time are placed in a collection together
		maxResponseTimeSeries = new TimeSeries("Max response time", Minute.class);
		avgResponseTimeSeries = new TimeSeries("Avg response time", Minute.class);
		responseTimeDataset = new TimeSeriesCollection(maxResponseTimeSeries);
		responseTimeDataset.addSeries(avgResponseTimeSeries);
		
		// Checked and unchecked are also placed in same TimeSeriesCollection
		uncheckedExceptionsDataset = new TimeSeries("Unchecked exceptions", Minute.class);
		checkedExceptionsDataset = new TimeSeries("Checked exceptions", Minute.class);
		exceptionDataset = new TimeSeriesCollection(uncheckedExceptionsDataset);
		exceptionDataset.addSeries(checkedExceptionsDataset);
	}
	
	public Collection<Dataset> getDatasetCollection() {
		Dataset[] datasets = new Dataset[] {
				invocationCountDataset, 			// invocation count
				responseTimeDataset,				// max and average response times
				exceptionDataset
				};
		return Arrays.asList(datasets);
	}

	/**
	 * @return the invocationCountDataset
	 */
	public TimeSeriesCollection getInvocationCountDataset() {
		return invocationCountDataset;
	}

	public TimeSeries getMaxResponseTimeDataset() {
		return maxResponseTimeSeries;
	}
	public TimeSeries getAvgResponseTimeDataset() {
		return avgResponseTimeSeries;
	}
	public TimeSeries getCheckedExceptionsDataset() {
		return checkedExceptionsDataset;
	}
	public TimeSeries getUncheckedExceptionsDataset() {
		return uncheckedExceptionsDataset;
	}

	public void addInvocationCount(Minute minute, int invocationCount) {
		getInvocationCountDataset().getSeries(0).add(minute,(Number)invocationCount);
	}

	public void addMaxResponseTimeDataset(Minute minute, int maxResponseTime) {
		getMaxResponseTimeDataset().add(minute,maxResponseTime);
	}

	public void addAvgResponseTimeDataset(Minute minute, int avgResponseTime) {
		getAvgResponseTimeDataset().add(minute,avgResponseTime);
	}

	public void addCheckedExceptionsDataset(Minute minute, int checkedExceptions) {
		getCheckedExceptionsDataset().add(minute,checkedExceptions);
	}

	public void addUncheckedExceptionsDataset(Minute minute, int uncheckedExceptions) {
		getUncheckedExceptionsDataset().add(minute,uncheckedExceptions);
	}

	/** Provides the max and avg response time data in one single dataset collection.
	 * 
	 * @return the responseTimeDataset
	 */
	public TimeSeriesCollection getResponseTimeDataset() {
		return responseTimeDataset;
	}

	/** The data set holding the exceptions, contains two series. One for the checked
	 * and another for the unchecked exceptions.
	 * 
	 * @return the exceptionDataset
	 */
	public TimeSeriesCollection getExceptionDataset() {
		return exceptionDataset;
	}

	protected void setEnd(Date end) {
		this.end = end;
	}

	protected void setStart(Date start) {
		this.start = start;
	}

	
}