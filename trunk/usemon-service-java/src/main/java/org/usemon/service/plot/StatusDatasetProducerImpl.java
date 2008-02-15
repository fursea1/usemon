/**
 * Created 29. okt.. 2007 14.27.36 by Steinar Overbeck Cook
 */
package org.usemon.service.plot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.jfree.data.time.Minute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.service.DbmsHelper;

/**
 * @author t547116 (Steinar Overbeck Cook)
 * TODO: Externalize the SQL script into a file to enable easy modifications
 */
public class StatusDatasetProducerImpl implements DatasetProducer, StatusDatasetProducer {
	
	private DataSource dataSource = null;
	private static Logger log = LoggerFactory.getLogger(StatusDatasetProducerImpl.class);

	/** {@inheritDoc}
	 * @see org.usemon.service.plot.StatusDatasetProducer#produceData()
	 */
	public DatasetCollection produceData() {
			StatusDataset statusDataset = new StatusDataset();
			Connection con = null;
			
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			String sql = DbmsHelper.loadSqlStatementFromResource(DbmsHelper.LAST_30_METHOD_MEASUREMENTS_SQL);
				
			try {
				
				con = dataSource.getConnection();
				
				ps = con.prepareStatement(sql);
				if (log.isDebugEnabled()) {
					log.debug("Executing this SQL to provide status data:\n" + sql);
				}
				rs = ps.executeQuery();
				java.sql.Timestamp currentDateTime = null;
				int i = 0;
				while (rs.next()) {
					
					currentDateTime = rs.getTimestamp(1);
					if (i == 0) {
						if (log.isDebugEnabled()) {
							log.debug("Setting end to " + currentDateTime);
						}
						statusDataset.setEnd(currentDateTime);
					}
					Minute minute = new Minute(currentDateTime);
					int invocationCount = rs.getInt(2);
					int maxResponseTime = rs.getInt(3);
					int avgResponseTime = rs.getInt(4);
					int checkedExceptions = rs.getInt(5);
					int uncheckedExceptions = rs.getInt(6);
					
					statusDataset.addInvocationCount(minute,invocationCount);
					statusDataset.addMaxResponseTimeDataset(minute, maxResponseTime);
					statusDataset.addAvgResponseTimeDataset(minute, avgResponseTime);
					statusDataset.addCheckedExceptionsDataset(minute, checkedExceptions);
					statusDataset.addUncheckedExceptionsDataset(minute, uncheckedExceptions);
					i++;
				}
				statusDataset.setStart(currentDateTime);
			} catch (Exception e) {
				throw new IllegalStateException("Unable to execute SQL: " + sql + ";reason: " + e,e);
			} finally {
				DbmsHelper.close(ps);
				DbmsHelper.close(rs);
				DbmsHelper.close(con);
			}
			return statusDataset;
	}

	/**
	 * @see org.usemon.service.plot.StatusDatasetProducer#setDatasource(javax.sql.DataSource)
	 */
	public void setDatasource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
