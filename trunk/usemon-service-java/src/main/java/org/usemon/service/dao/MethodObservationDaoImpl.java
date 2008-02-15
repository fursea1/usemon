/**
 * 
 */
package org.usemon.service.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.MethodObservation;
import org.usemon.service.DbmsHelper;
import org.usemon.service.DimensionCacheManager;
import org.usemon.service.util.DimField;
import org.usemon.service.util.Dimension;
import org.usemon.service.util.FactField;

import com.google.inject.Inject;

/**
 * Data access object for the <code>method_measurement_fact</code> table. This
 * implementation is not thread safe.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class MethodObservationDaoImpl extends ObservationDaoSupport implements MethodObservationDao  {
	private static final Logger log = LoggerFactory.getLogger(MethodObservationDaoImpl.class);

	private static final String FACT_TABLE_NAME = "method_measurement_fact";

	/** Private constructor invoked by <code>getInstance</code> method */
	@Inject
	private MethodObservationDaoImpl(DataSource ds, DimensionCacheManager dimensionCacheManager) {
		super(ds, dimensionCacheManager);
	}

	/** Instance method preferred over constructor */
	public static MethodObservationDao getInstance(DataSource ds, DimensionCacheManager dimensionCacheManager) {
		// Simply invokes our private constructor
		MethodObservationDaoImpl m = new MethodObservationDaoImpl(ds, dimensionCacheManager);
		return m;
	}

	/**
	 * Implementation of the abstract method inherited.
	 * 
	 * @return list of meta data for the fact fields.
	 */
	protected List<FactField> metaDataForFactFields() {
		// Fact fields
		List<FactField> factFieldList = new ArrayList<FactField>();
		factFieldList.add(new FactField(MethodObservation.class,"invocation_count", "invocationCount"));
		factFieldList.add(new FactField(MethodObservation.class,"max_response_time", "maxResponseTime"));
		factFieldList.add(new FactField(MethodObservation.class,"avg_response_time", "avgResponseTime"));
		factFieldList.add(new FactField(MethodObservation.class,"checked_exceptions", "checkedExceptions"));
		factFieldList.add(new FactField(MethodObservation.class,"unchecked_exceptions", "uncheckedExceptions"));
		factFieldList.add(new FactField(MethodObservation.class,"period_length", "periodLength"));
		return factFieldList;
	}

	/**
	 * Implementation providing the meta data for the dimensional fields as expected
	 * by the super class.
	 */
	protected List<DimField> metaDataForDimensionalFields() {
		List<DimField> dimFields = new ArrayList<DimField>();
		dimFields.add(new DimField(MethodObservation.class, Dimension.DATE, "timeStamp", "d_date_id"));
		dimFields.add(new DimField(MethodObservation.class, Dimension.TIME, "timeStamp", "d_time_id"));
		dimFields.add(new DimField(MethodObservation.class, Dimension.PACKAGE, "packageName", "package_id"));
		dimFields.add(new DimField(MethodObservation.class, Dimension.METHOD, "methodName", "method_id"));
		dimFields.add(new DimField(MethodObservation.class, Dimension.CLASS, "className", "class_id"));
		dimFields.add(new DimField(MethodObservation.class, Dimension.CHANNEL, "channel", "channel_id"));
		dimFields.add(new DimField(MethodObservation.class, Dimension.PRINCIPAL, "principal", "principal_id"));
		dimFields.add(new DimField(MethodObservation.class, Dimension.LOCATION, "location", "location_id"));
		return dimFields;
	}

	protected String getFactTableName() {
		return FACT_TABLE_NAME;
	}

	/**
	 * {@inheritDoc} This implementation will copy the properties of the given
	 * method measurement into a list having the same order as the dimensional
	 * and fact meta data entries. Finally these values are set on the prepared
	 * sql statement and inserted into the database.
	 */
	public void add(MethodObservation methodObservation) {
		List<MethodObservation> mlist = new ArrayList<MethodObservation>();
		mlist.add(methodObservation);

		addMethodObservations(mlist);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Persists all the method measurements by invoking the general parent method.
	 */
	public void addMethodObservations(List<MethodObservation> observationList) {
		addObservations(observationList);
	}

	public Date getLastMethodObservation() {
		Date latestObservation = null;
		String sql = DbmsHelper.loadSqlStatementFromResource(DbmsHelper.LAST_METHOD_OBSERVATION);
		return null;
	}
}
