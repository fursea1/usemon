/**
 * 
 */
package org.usemon.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.service.DbmsHelper;
import org.usemon.service.util.Dimension;
import org.usemon.service.util.Dimension.JdbcParams;

import com.google.inject.Inject;

/**
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class DimensionalDaoImpl implements DimensionalDao {

	private static Logger log = LoggerFactory.getLogger(DimensionalDaoImpl.class);

	// Database connection, which is never closed in this class.
	Connection con = null;

	private DataSource ds;

	/**
	 * No argument constructor, which requires you to set the data source in a
	 * separate operation.
	 */
	public DimensionalDaoImpl() {
	}

	/**
	 * Recommended construct, which will initialise everything you need for a
	 * comfortable ride.
	 * 
	 * @param mockDs
	 *            reference to datasource from which connections will be
	 *            obtained.
	 */
	public DimensionalDaoImpl(DataSource ds) {
		setDataSource(ds);
	}

	/**
	 * Inserts a new value for a given dimension.
	 * 
	 * @see org.usemon.service.dao.DimensionalDao#createDimensionalValue(org.usemon.service.util.Dimension,
	 *      java.lang.Object)
	 */
	public int createDimensionalValue(Dimension dimension, Object newEntry) {
		PreparedStatement ps = null;
		int newKey = 0;
		ResultSet rs = null;
		Connection con = null;
		try {
			con = ds.getConnection();
			// Generates the sql and extracts the parameters required
			JdbcParams jdbcParams = dimension.sqlDataForInsert(newEntry);
			ps = con.prepareStatement(jdbcParams.sql);

			Object[] params = jdbcParams.params;
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			ps.execute();
			rs = ps.getGeneratedKeys();
			if (rs.next())
				newKey = rs.getInt(1);

		} catch (Exception e) {
			throw new IllegalStateException("Dimension: " + dimension + ", value=" + newEntry + ", SQLException: " + e.getMessage(), e);
		} finally {
			DbmsHelper.close(rs);
			DbmsHelper.close(ps);
			DbmsHelper.close(con);
		}
		return newKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.service.dao.DimensionalDao#findForeginKeyInDbms(org.usemon.service.util.Dimension,
	 *      java.lang.Object)
	 */
	public int findForeginKeyInDbms(Dimension dimension, Object lookupValue) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con = null;
		int fkValue = 0;
		JdbcParams jdbcParams = null;
		try {
			jdbcParams = dimension.jdbcParamsForSelect(lookupValue);
			con = ds.getConnection();
			if (con == null)
				throw new IllegalStateException("Unable to obtain connection from datasource");
			ps = con.prepareStatement(jdbcParams.sql);

			// Transforms our property value into one or more sql parameters.
			Object[] params = jdbcParams.params;
			int i = 1;
			for (Object v : params) {
				ps.setObject(i++, v);
			}

			rs = ps.executeQuery();
			if (rs.next())
				fkValue = rs.getInt(1);
		} catch (Exception e) {
			String sql = jdbcParams != null ? jdbcParams.sql : "unknown sql";
			throw new IllegalStateException("Dimension:" + dimension + ", value=" + lookupValue + ", sql:" + sql + ", SQLException:" + e.getMessage(), e);
		} finally {
			DbmsHelper.close(rs);
			DbmsHelper.close(ps);
			DbmsHelper.close(con);
		}
		return fkValue;
	}


	/**
	 * Injects data source
	 *  
	 * @see org.usemon.service.dao.DimensionalDao#setDataSource(javax.sql.DataSource)
	 */
	@Inject
	public void setDataSource(DataSource dataSource) {
		ds = dataSource;
	}
}
