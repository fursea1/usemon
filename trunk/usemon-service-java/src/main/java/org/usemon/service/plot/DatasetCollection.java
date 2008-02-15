/**
 * 
 */
package org.usemon.service.plot;

import java.util.Collection;
import java.util.Date;

import org.jfree.chart.axis.DateTickUnit;
import org.jfree.data.general.Dataset;

/**
 * Holds a collection of {@link Dataset} instances, which may be used for charting.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface DatasetCollection {
	/** Provides access to all the data sets through a collection. If you need to 
	 * understand the specific semantics for each data set, you need to cast the
	 * instance to the actual implementation.
	 * 
	 * @return collection of datasets 
	 */
	Collection<Dataset> getDatasetCollection();
	
	/** Assuming that all data have a time dimension, this is the start */
	Date getStart();
	/** Assuming that all data have a time dimension, this is the end */
	Date getEnd();
}
