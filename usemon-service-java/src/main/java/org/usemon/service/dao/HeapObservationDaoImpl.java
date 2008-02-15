/**
 * 
 */
package org.usemon.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.usemon.domain.HeapObservation;
import org.usemon.service.DimensionCacheManager;
import org.usemon.service.util.DimField;
import org.usemon.service.util.Dimension;
import org.usemon.service.util.FactField;

import com.google.inject.Inject;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class HeapObservationDaoImpl extends ObservationDaoSupport implements HeapObservationDao {

	/**
	 * @param dataSource
	 * @param dimensionCacheManager
	 */
	@Inject
	public HeapObservationDaoImpl(DataSource dataSource, DimensionCacheManager dimensionCacheManager) {
		super(dataSource, dimensionCacheManager);
	}

	/**
	 * @see org.usemon.service.dao.ObservationDaoSupport#getFactTableName()
	 */
	@Override
	protected String getFactTableName() {
		return "heap_fact";
	}

	/**
	 * Provides information about what fields should be transformed from their current value
	 * into a foreign key reference.
	 * @see org.usemon.service.dao.ObservationDaoSupport#metaDataForDimensionalFields()
	 */
	@Override
	protected List<DimField> metaDataForDimensionalFields() {
		List<DimField> list = new ArrayList<DimField>();
		list.add(new DimField(HeapObservation.class,Dimension.DATE,"timeStamp","d_date_id"));
		list.add(new DimField(HeapObservation.class,Dimension.TIME,"timeStamp","d_time_id"));
		list.add(new DimField(HeapObservation.class,Dimension.LOCATION,"location","location_id"));
		return list;
	}

	/** Provides information about the fact fields and how they should be inserted.
	 * 
	 * @see org.usemon.service.dao.ObservationDaoSupport#metaDataForFactFields()
	 */
	@Override
	protected List<FactField> metaDataForFactFields() {
		List<FactField> list = new ArrayList<FactField>();
		list.add(new FactField(HeapObservation.class,"free", "free"));
		list.add(new FactField(HeapObservation.class,"total", "total"));
		list.add(new FactField(HeapObservation.class,"max_mem", "maxMem"));
		return list;
	}

	/** Factory method is generally preferred over constructors */
	public static HeapObservationDao getInstance(DataSource ds, DimensionCacheManager dimCacheMgr) {
		return new HeapObservationDaoImpl(ds, dimCacheMgr);
	}

	public void addHeapObservations(List<HeapObservation> list) {
		addObservations(list);
	}

}
