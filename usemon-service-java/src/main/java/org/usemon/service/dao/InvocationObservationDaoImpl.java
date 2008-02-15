/**
 * 
 */
package org.usemon.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodObservation;
import org.usemon.service.DimensionCacheManager;
import org.usemon.service.util.DimField;
import org.usemon.service.util.Dimension;
import org.usemon.service.util.FactField;

import com.google.inject.Inject;

/**
 * Implementation of the code required to handle persistent instances 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class InvocationObservationDaoImpl extends ObservationDaoSupport implements InvocationObservationDao{

	@Inject
	private InvocationObservationDaoImpl(DataSource ds, DimensionCacheManager dimCacheMgr) {
		super(ds,dimCacheMgr);
	}
	
	/** Favoring static factory method over public constructor for creating new instances :-) */
	public static InvocationObservationDao getInstance(DataSource ds, DimensionCacheManager dimCacheMgr) {
		
		return new InvocationObservationDaoImpl(ds, dimCacheMgr);
	}

	@Override
	protected String getFactTableName() {
		return "invocation_fact";
	}

	@Override
	protected List<DimField> metaDataForDimensionalFields() {
		List<DimField> list = new ArrayList<DimField>();
		list.add(new DimField(InvocationObservation.class,Dimension.DATE, "timeStamp", "d_date_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.TIME, "timeStamp", "d_time_id"));		
		list.add(new DimField(InvocationObservation.class,Dimension.LOCATION, "location","location_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.PACKAGE,"targetPackageName","target_package_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.CLASS,"targetClassName","target_class_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.METHOD,"targetMethodName","target_method_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.INSTANCE,"targetInstanceId","target_instance_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.PACKAGE,"srcPackageName","src_package_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.CLASS,"srcClassName","src_class_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.METHOD,"srcMethodName","src_method_id"));
		list.add(new DimField(InvocationObservation.class,Dimension.INSTANCE,"srcInstanceId","src_instance_id"));
		
		return list ;
	}

	@Override
	protected List<FactField> metaDataForFactFields() {
		List<FactField> facts = new ArrayList<FactField>();
		facts.add(new FactField(InvocationObservation.class, "invocation_count", "invocationCount"));
		facts.add(new FactField(InvocationObservation.class, "period_length", "periodLength"));
		return facts;
	}

	public void addInvocationObservations(List<InvocationObservation> invocationObservationList) {
		super.addObservations(invocationObservationList);
	}
	
	/**
	 * {@inheritDoc} This implementation will copy the properties of the given
	 * method invocation into a list having the same order as the dimensional
	 * and fact meta data entries. Finally these values are set on the prepared
	 * sql statement and inserted into the database.
	 */
	public void add(InvocationObservation invocationObservation) {
		List<InvocationObservation> mlist = new ArrayList<InvocationObservation>();
		mlist.add(invocationObservation);

		addInvocationObservations(mlist);
	}	
}
