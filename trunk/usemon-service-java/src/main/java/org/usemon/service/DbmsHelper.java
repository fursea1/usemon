/**
 * 
 */
package org.usemon.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.DbmsService;
import org.usemon.service.util.ResultSetProcessor;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * @author t547116
 * 
 */
public class DbmsHelper implements DbmsService {
	public static boolean driverLoaded = false;
	private static final Logger log = LoggerFactory.getLogger(DbmsHelper.class);

	public static final String LAST_30_METHOD_MEASUREMENTS_SQL = "sql/last_30_method_measurements.sql";
	public static final String LAST_METHOD_OBSERVATION = "sql/last_method_observation.sql";

	private static DataSource ds = null;

	/**
	 * Provides a connection to the database by loading the driver and
	 * performing the usual JDBC stuff.
	 * 
	 * @return
	 */
	public static Connection getConnection() {

		try {
			return getDataSource().getConnection();
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to obtain connection to DBMS", e);
		}
	}

	/**
	 * @return
	 */
	private static Connection getConnectionViaDriverManager() {
		registerDriver();

		String jdbcUrl = PropertyHelper.getJdbcUrl();
		String jdbcUserName = PropertyHelper.getJdbcUserName();
		String jdbcPassword = PropertyHelper.getJdbcPassword();
		Connection con;
		try {
			con = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to obtain connection to database", e);
		}
		return con;
	}

	private synchronized static void registerDriver() {
		if (driverLoaded)
			return;

		String jdbcDriverClassName = PropertyHelper.getJdbcDriverClassName();
		try {
			Class.forName(jdbcDriverClassName).newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to instantiate the JDBC driver for " + jdbcDriverClassName, e);
		}
	}

	public static void close(Connection con) {
		try {
			if (con != null && !con.isClosed()) 
				con.close();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to close connection; " + e.getMessage(), e);
		}
	}

	/** Lazy creates the data source upon first invocation, after which 
	 * the same data source will be returned upon successive calls.
	 * 
	 * @return
	 */
	public synchronized static DataSource getDataSource() {
		if (ds == null) {
			BasicDataSource basicDataSource = new BasicDataSource();
			
			String jdbcUrl = PropertyHelper.getJdbcUrl();
			log.info("DataSource will connect to " + jdbcUrl);
			basicDataSource.setDriverClassName(PropertyHelper.getJdbcDriverClassName());
			basicDataSource.setUrl(PropertyHelper.getJdbcUrl());
			basicDataSource.setUsername(PropertyHelper.getJdbcUserName());
			basicDataSource.setPassword(PropertyHelper.getJdbcPassword());
			basicDataSource.setInitialSize(PropertyHelper.getJdbcIntialPoolSize());
			basicDataSource.setMaxActive(50);
			ds = basicDataSource;
		}
		return ds;
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to close result set; " + e.getMessage(), e);
		}
	}

	public static void close(PreparedStatement ps) {
		try {
			if (ps != null)
				ps.close();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to close prepared statement; " + e.getMessage(), e);
		}

	}
	
	/**
	 * Provices access to a data source connected to our default database.
	 */
	public DataSource dataSource() {
		return getDataSource();
	}

	public static void executeUpdate(String sql) {
		Connection con = null;
		try {
			con = getDataSource().getConnection();
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to execute " + sql + ";" + e,e); 
		} finally {
			close(con);
		}
	}

	public static void executeQuery(String selectSql, ResultSetProcessor resultSetProcessor) {
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = getDataSource().getConnection();
			 stmt = con.createStatement();
			 rs = stmt.executeQuery(selectSql);
			while (rs.next())
				resultSetProcessor.forEachRow(rs);
			rs.close();
		} catch (SQLException e) {
			throw new IllegalStateException(e.getMessage() + ": " + selectSql, e);
		} finally {
			close(rs);
			close(con);
		}
	}

	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				throw new IllegalStateException("Unable to close statement");
			}
		}
	}

	/** Loads a SQL statement from the given resource in the class path */
	public static String loadSqlStatementFromResource(String resourceName) {
		StringBuilder sb = new StringBuilder();
		
		InputStream is = DbmsHelper.class.getResourceAsStream("/"+resourceName);
		if (is == null)
			throw new IllegalArgumentException("Resource '" + resourceName + "' not found in classpath");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {
			while ( (line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			is.close();
		} catch (IOException e) {
			throw new IllegalStateException("I/O error while reading from '" + resourceName + "'", e);
		}
		return sb.toString();
	}
	
}
