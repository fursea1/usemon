/**
 * 
 */
package org.usemon.integrationtest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.service.DbmsHelper;

/**
 * Compares various methods of inserting a bunch of rows into a table.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class PerformanceTest {
	Logger log = LoggerFactory.getLogger(PerformanceTest.class);

	private long start;

	// Number of rows to insert into the table
	private static final int N = 10;
	// Block size to use for optimized inserts.
	private static final int BLOCK_SIZE = 10;

	protected DataSource ds;

	/**
	 * Re-creates the table and starts our stop watch.
	 * 
	 * @throws SQLException
	 */
	@Before
	public void createTable() throws SQLException {
		String sqls[] = { "drop table if exists t_test", " create table t_test (id integer auto_increment primary key, name varchar(25))" };
		ds = DbmsHelper.getDataSource();
		Connection con = ds.getConnection();
		Statement stmt = con.createStatement();
		for (int i = 0; i < sqls.length; i++) {
			stmt.addBatch(sqls[i]);
		}
		stmt.executeBatch();
		con.close();

		start = System.currentTimeMillis();
	}

	/**
	 * Stop the clock and emit the elapsed time for the current test which completed.
	 */
	@After
	public void stop() {
		long elapsed = System.currentTimeMillis() - start;
		log.info("Elapsed " + elapsed);
	}

	/**
	 * Opens the database connection, prepares the statement, executes it
	 * and closes the connection. This is supposed to be the very slowest.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testManual() throws SQLException {
		log.info("Cumbersome variant");
		for (int i = 0; i < N; i++) {
			Connection con = ds.getConnection();
			PreparedStatement ps = con.prepareStatement(getInsert());
			ps.setObject(1, "TEST" + i);
			ps.executeUpdate();
			con.close();
		}

	}

	/** Semi optimized, attempting to keep the prepared statement open
	 * between executions.
	 * @throws SQLException
	 */
	@Test
	public void testOptimized() throws SQLException {
		log.info("Optimized variant");
		Connection con = ds.getConnection();
		PreparedStatement ps = con.prepareStatement(getInsert());
		for (int i = 0; i < N; i++) {
			ps.setObject(1, "TEST" + i);
			ps.executeUpdate();
			ps.clearParameters();
		}
		con.close();
	}

	/**
	 * Highly optimized version which attempts to create insert statements in which
	 * several rows are blocked together according to the special mysql syntax:
	 * <pre>
	 * <code>insert into t_test (id,name) values(0,?),(0,?).....</code>
	 * </pre>
	 * @throws SQLException
	 */
	@Test
	public void blockInsert() throws SQLException {
		int blockSize = N / BLOCK_SIZE;
		log.info("block insert. Inserting " + blockSize + " blocks");
		
		for (int blockNumber = 0; blockNumber < blockSize; blockNumber++) {
			
			StringBuilder sb = initializeInsertSql();
			for (int i = 0; i < BLOCK_SIZE; i++) {
				if (i > 0)
					sb.append(",");
				sb.append("(0,?)");
			}
			Connection con = ds.getConnection();
			PreparedStatement ps = con.prepareStatement(sb.toString());
			for (int i = 0; i < BLOCK_SIZE; i++)
				ps.setObject(i + 1, "TEST" + ((blockNumber*BLOCK_SIZE)+i));
			ps.executeUpdate();
			con.close();
		}
	}

	private StringBuilder initializeInsertSql() {
		StringBuilder sb = new StringBuilder("insert into t_test (id, name) values");
		return sb;
	}

	private String getInsert() {
		return "insert into t_test (id,name) values(0,?)";
	}

}
