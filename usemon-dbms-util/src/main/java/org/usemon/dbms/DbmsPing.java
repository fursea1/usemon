/**
 * Created 31. jan.. 2008 11.08.28 by Steinar Overbeck Cook
 */
package org.usemon.dbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.usemon.service.DbmsHelper;
import org.usemon.service.PropertyHelper;
import org.usemon.service.util.ResultSetProcessor;

/**
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class DbmsPing {
	public static void main(String[] args) {
		String jdbcUrl = PropertyHelper.getJdbcUrl();
		System.out.format("Properties in effect (override with -D option):\n");
		System.out.format("%s=%s\n" , PropertyHelper.JDBC_URL_PROP_NAME,jdbcUrl);
		System.out.format("%s=%s\n", PropertyHelper.JDBC_USER_NAME, PropertyHelper.getJdbcUserName());
		System.out.format("%s=%s\n", PropertyHelper.JDBC_USER_PASSWORD, "**************");
		
		DbmsPing dbmsPing = new DbmsPing();
		try {
			dbmsPing.checkObtainingAConnection();
			dbmsPing.checkTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	private DataSource ds;

	public DbmsPing() {
		ds =  DbmsHelper.getDataSource();
	}
	private void checkObtainingAConnection() throws SQLException {
		
		Connection con = null;
		long start = System.currentTimeMillis();
		long first = 0;
		for (int i=0; i < 20; i++ ) {
			System.out.format("#");
			con = ds.getConnection();
			if (i==0)
				first = System.currentTimeMillis() - start;
			DbmsHelper.close(con);
		}
		long elapsed = System.currentTimeMillis() - start;
		long avg = elapsed / 10;
		System.out.format("\nAverage time for obtaining 10 connections, per connection: %dms\n", avg);
		System.out.format("First connection took %dms\n", first);
	}
	
	private void checkTables() throws SQLException {

		String selectCountOfClasses = "Select count(*) from class";
		String selectAllClasses = "Select class from class";
		List<String> allClassNames = new ArrayList<String>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			int count;
			long start = System.currentTimeMillis();
			con = ds.getConnection();
			long elapsed = System.currentTimeMillis() - start;
			System.out.format("Obtaining a connection took %dms\n", elapsed);
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(selectCountOfClasses);
			rs.next();
			count = rs.getInt(1);
			System.out.format("There are %d rows in table 'class'\n", count);
			DbmsHelper.close(rs);
			System.out.format("Loading all class names....");
			start = System.currentTimeMillis();
			rs = stmt.executeQuery(selectAllClasses);
			while (rs.next()) {
				String s = rs.getString(1);
				allClassNames.add(s);
			}
			elapsed = System.currentTimeMillis() - start;
			System.out.format("done in %dms\n", elapsed);
			
		} finally {
			DbmsHelper.close(con);
		}
	}
	
}
