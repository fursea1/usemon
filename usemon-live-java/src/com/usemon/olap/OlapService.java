package com.usemon.olap;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

public class OlapService {

	private OlapConnection olapConnection;

	public OlapService() throws ClassNotFoundException, SQLException {
		Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
	}

	public void dumpCellSetAndClose(PrintStream out, CellSet cellSet) throws SQLException {
		for (Position row : cellSet.getAxes().get(1)) {
			for (Position column : cellSet.getAxes().get(0)) {
				for (Member member : row.getMembers()) {
					out.println(member.getUniqueName());
				}
				for (Member member : column.getMembers()) {
					out.println(member.getUniqueName());
				}
				Cell cell = cellSet.getCell(column, row);
				out.println(cell.getFormattedValue());
				out.println();
			}
		}
		cellSet.close();
	}

	public CellSet executeQuery(String mdxQuery) throws SQLException {
		ensureConnection();
		OlapStatement stmt = olapConnection.createStatement();
		return stmt.executeOlapQuery(mdxQuery);
	}

	private void ensureConnection() throws SQLException {
		if (olapConnection == null || (olapConnection != null && olapConnection.isValid(3))) {
			if (olapConnection != null)
				closeConnection();
			Connection connection = DriverManager.getConnection("jdbc:mondrian:Jdbc='jdbc:mysql://localhost:3307/usemon';Catalog='file://C:\\java\\eclipse-workspaces\\workspace-usemonclient\\usemon-live-java\\src\\usemon-schema.xml';JdbcDrivers=com.mysql.jdbc.Driver;JdbcUser=usemonmonitor;JdbcPassword=usemonmonitor");
			olapConnection = connection.unwrap(OlapConnection.class);
		}
	}

	private void closeConnection() throws SQLException {
		if (olapConnection != null) {
			olapConnection.close();
		}
		olapConnection = null;
	}

	protected void finalize() throws Throwable {
		closeConnection();
	}
}