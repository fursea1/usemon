/**
 * 
 */
package org.usemon.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.Observation;
import org.usemon.service.DbmsHelper;
import org.usemon.service.DimensionCacheManager;
import org.usemon.service.util.DimField;
import org.usemon.service.util.FactField;

import com.google.inject.Inject;

/**
 * Supporting implementation of various methods, which apply to the persistence of Observations in a dimensional model.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public abstract class ObservationDaoSupport {

	/**
	 * Closure to be invoked for each object
	 */
	public interface ObservationClosure {
		void execute(Object object);
	}

	private static final Logger log = LoggerFactory.getLogger(ObservationDaoSupport.class);

	protected DimensionCacheManager dimensionCacheManager;
	protected DataSource ds;
	protected List<DimField> dimensionFields = null;
	protected List<FactField> factFields = null;
	protected boolean isPrepared = false;
	protected String preparedInsertSql = null;
	protected long elapsedTime;
	protected long insertCounter;

	@Inject
	protected ObservationDaoSupport(DataSource dataSource, DimensionCacheManager dimensionCacheManager) {
		this.ds = dataSource;
		this.dimensionCacheManager = dimensionCacheManager;
	}

	/** Provides a collection of meta data for the fact fields */
	protected abstract List<FactField> metaDataForFactFields();

	/** Provides a collection of meta data for the dimensional fields */
	protected abstract List<DimField> metaDataForDimensionalFields();

	/** Provides the name of the table into which the facts should be inserted */
	protected abstract String getFactTableName();

	/**
	 * Creates the meta data for this fact table
	 */
	protected void createMetaData() {

		// Dimensional fields
		dimensionFields = metaDataForDimensionalFields();

		factFields = metaDataForFactFields();
	}

	/**
	 * Generates the first part of an SQL insert statement: <code><pre>
	 * insert into method_measurement_fact
	 * 	(d_date_id, d_time_id, package_id, method_id, class_id, channel_id, principal_id, 
	 * 		location_id, invocation_count, max_response_time, avg_response_time, checked_exceptions, 
	 * 		unchecked_exceptions, period_length)
	 * 	values
	 * </pre>
	 * </code>
	 * 
	 * @return
	 */
	protected String generateInsertSqlPreamble() {
		// comma separated list of column names
		StringBuilder columnNames = new StringBuilder("");
		// comma separated list of question marks. Same number of question
		// marks as number of columns
		StringBuilder parameterPlaceHolders = new StringBuilder("");
		boolean firstElement = true;
		for (DimField dimField : dimensionFields) {
			if (!firstElement) {
				columnNames.append(", ");
				parameterPlaceHolders.append(", ");
			}
			columnNames.append(dimField.getDimensionalForeignKeyColName());
			parameterPlaceHolders.append('?');
			firstElement = false;
		}

		for (FactField factField : factFields) {
			columnNames.append(", ").append(factField.getColName());
			parameterPlaceHolders.append(", ?");
		}
		String result = String.format("insert into %s \n\t(%s)\n\tvalues ", getFactTableName(), columnNames);
		return result;
	}

	/**
	 * Generates the string <code><pre>
	 * (?,?,?,?,?,?.....)
	 * </pre></code> which may be used in SQL <em>insert</em> statements. The number of question marks generated is the sum of the
	 * number of dimension fields and fact fields.
	 * 
	 * @return
	 */
	protected String generateValuesExpressionWithParams() {
		StringBuilder sb = new StringBuilder("(?");
		int fieldCount = dimensionFields.size() + factFields.size();
		for (int i = 1; i < fieldCount; i++)
			sb.append(",?");
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Generates the SQL insert statement with positional parameters according to the meta information.
	 * <p>
	 * This typically looks something like this:
	 * 
	 * @return sql statement looking something like this: <code>
	 * <pre>
	 * insert into method_measurement_fact
	 * 	(d_date_id, d_time_id, package_id, method_id, class_id, channel_id, principal_id, 
	 * 	location_id, invocation_count, max_response_time, avg_response_time, checked_exceptions, 
	 * unchecked_exceptions, period_length)
	 * 	values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	 * </pre>
	 * </code>
	 */
	protected String generateSingleInsertSqlForPrepare() {
		String insertSqlPreamble = generateInsertSqlPreamble();
		String parameterPlaceHolders = generateValuesExpressionWithParams();
		return insertSqlPreamble + parameterPlaceHolders;
	}

	/**
	 * Prepares the SQL statements and initializes the meta data required for insertion of values selected from the dimensional
	 * tables.
	 * <p>
	 * Furthermore; the SQL statements are prepared (compiled).
	 */
	protected void prepare() {

		// dimension_fk, property, table or view
		createMetaData();
		preparedInsertSql = generateSingleInsertSqlForPrepare();
		isPrepared = true;
	}

	/**
	 * Copies the properties into a list of values according to the order of the meta data entries. <strong>Note!</strong> the
	 * dimensional cache manager will be consulted for each bean field being designated as a dimensional field.
	 * 
	 * @param observation
	 *            the bean holding our observation for which all properties should be extracted.
	 * @return list of property values
	 */
	protected List<Object> produceListOfBeanValues(Observation observation) {
		List<Object> result = new ArrayList<Object>();

		if (!isPrepared)
			prepare();
		DimField currentField = null;
		try {
			for (DimField dimField : dimensionFields) {
				currentField = dimField;
				// Retrieves the value of the property from our observation
				// object
				Object lookupValue = dimField.getValue(observation);

				// Finds the foreign key value of the dimensional value
				Integer dimFieldFk = dimensionCacheManager.getForeignKeyForValue(dimField.getDimension(), lookupValue);
				result.add(dimFieldFk); // Saves value in result list
			}

			for (FactField factField : factFields) {
				Object value = factField.getGetterMethod().invoke(observation, (Object[]) null);
				result.add(value);
			}
		} catch (Exception e) {
			throw new IllegalStateException("Unable to retrieve value for field " + (currentField != null ? currentField.getBeanPropertyName() : "unknown")
					+ "; " + e, e);
		}

		return result;
	}

	public void clearStatistics() {
		elapsedTime = insertCounter = 0;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public long getInsertCounter() {
		return insertCounter;
	}

	protected void addObservations(List<? extends Observation> observationList) {

		if (observationList == null || observationList.size() <= 0) {

			// Empty observation set
			return;

		} else {
			// meta data must be prepared....
			if (!isPrepared)
				prepare();

			// Generates: insert into ...... values
			StringBuilder sql = new StringBuilder(generateInsertSqlPreamble());
			for (int i = 0; i < observationList.size(); i++) {
				if (i > 0)
					sql.append(",\n\t");
				sql.append(generateValuesExpressionWithParams());
			}

			if (log.isDebugEnabled()) {
				Observation obs = observationList.get(0);
				log.debug("Inserting " + observationList.size() + " observations of type " + obs.getTypeName());
			}
			// Transforms each bean entry into a list of values and injects them
			// into the prepared statement
			PreparedStatement ps = null;
			Connection con = null;
			int paramsIndex = 1;
			int beanIndex = 0;
			try {
				con = ds.getConnection();
				log.debug("Connection properties: auto commit=" + con.getAutoCommit());
				ps = con.prepareStatement(sql.toString());

				// Iterates over each object and injects the value of each property
				// into
				// the sql statement
				for (Observation observation : observationList) {
					List<Object> values = produceListOfBeanValues(observation);
					for (Object objectValue : values) {
						ps.setObject(paramsIndex++, objectValue);
					}
					beanIndex++;
				}
				long start = System.currentTimeMillis();
				// Executes the insert.
				ps.executeUpdate();
				long elapsed = System.currentTimeMillis() - start;
				elapsedTime += elapsed;
				insertCounter += observationList.size();
				log.debug("Added " + observationList.size() + " entries in " + elapsed + "ms");
			} catch (SQLException e) {
				String sqlStatementInfo = stringifySql(observationList, sql);
				throw new IllegalStateException("Sqlerror: " + e + "\n" + sqlStatementInfo, e);
			} finally {
				DbmsHelper.close(ps);
				DbmsHelper.close(con);
			}
		}
	}

	/** This code has not been tested, so everything is wrapped in a try catch block */
	protected String stringifySql(List<? extends Observation> observationList, StringBuilder sql) {
		final StringBuilder sb = new StringBuilder("SQL: " + sql);
		try {
			sb.append("\n\nBean Values:\n");

			processAllValuesInAllObservations(observationList, new ObservationClosure() {
				int i = 0;

				public void execute(Object object) {
					sb.append("\n\t");
					sb.append(i);
					sb.append(" : ");
				}
			});
		} catch (Exception e) {
			sb.append(" >> no stringifed sql representation available (" + e + " <<");
		}
		return sb.toString();
	}

	/** Experimental implementation of closure */
	protected void processAllValuesInAllObservations(List<? extends Observation> observationList, ObservationClosure closure) {
		for (Observation observation : observationList) {
			List<Object> values = produceListOfBeanValues(observation);
			for (Object objectValue : values) {
				closure.execute(objectValue);
			}
		}
	}

}
