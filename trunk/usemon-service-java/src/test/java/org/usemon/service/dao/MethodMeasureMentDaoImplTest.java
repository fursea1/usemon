/**
 * 
 */
package org.usemon.service.dao;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.usemon.domain.MethodObservation;
import org.usemon.service.DimensionCacheManager;
import org.usemon.service.util.Dimension;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class MethodMeasureMentDaoImplTest {

	MethodObservationDaoImpl daoImpl = null;
	private DataSource mockDataSource = null;
	private DimensionCacheManager mockDimensionCacheMananger;
	@Before
	public void setUp() throws SQLException {
		mockDataSource = createMock(DataSource.class	);
		mockDimensionCacheMananger = createMock(DimensionCacheManager.class);
		
		daoImpl = (MethodObservationDaoImpl) MethodObservationDaoImpl.getInstance(mockDataSource, mockDimensionCacheMananger);
	}

	
	
	/**
	 * Test method for {@link org.usemon.service.dao.MethodObservationDaoImpl#generateSingleInsertSqlForPrepare()}.
	 * @throws SQLException 
	 */
	@Test
	public void testGenerateSqlForPrepare() throws SQLException {
		daoImpl.createMetaData();
		String sql = daoImpl.generateSingleInsertSqlForPrepare();
		assertNotNull(sql);
		System.err.println("testGenerateSqlForPrepare() :- " +sql);
	}

	@Test
	public void compareSqlParameterList() {
		daoImpl.prepare();
		String sql1 = daoImpl.generateValuesExpressionWithParams();
		String sql2 = daoImpl.generateSingleInsertSqlForPrepare();
		String[] s1 = sql1.split("\\?");
		String[] s2 = sql2.split("\\?");
		assertEquals(s1.length, s2.length);
	}
	
	@Ignore
	public void testProduceListOfBeanValues(){
		MethodObservation m = (MethodObservation) TestDataGenerator.generateMethodObservation();
		List<Object> values = daoImpl.produceListOfBeanValues(m);
	}
	
	@Ignore
	public void testInsertBunch() {
		List<MethodObservation> mlist = new ArrayList<MethodObservation>();
		for (int n= 0; n < 100; n++) {
			mlist.add((MethodObservation) TestDataGenerator.generateMethodObservation());
		}
		daoImpl.addMethodObservations(mlist);
	}
}
