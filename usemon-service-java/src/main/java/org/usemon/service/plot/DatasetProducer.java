/**
 * 
 */
package org.usemon.service.plot;

import javax.sql.DataSource;


/**
 * Produces the data sets required by the various plot generators.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface DatasetProducer {
	void setDatasource(DataSource dataSource);
	DatasetCollection produceData();
}
