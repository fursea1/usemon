package org.usemon.live.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.usemon.live.applet.Node;

public class DataService implements Runnable {
	private Connection connection;
	private Thread updateThread;
	private InvocationListener dataListener;

	public DataService(InvocationListener dataListener) {
		this.dataListener = dataListener;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/usemon?autoReconnect=true", "usemonmonitor", "usemonmonitor");
			updateThread = new Thread(this, "Usemon DataService Update Thread");
			updateThread.setDaemon(true);
			updateThread.start();
		} catch (ClassNotFoundException e) {
		} catch (SQLException e) {
		}
	}

	public void run() {
		while (true) {
			try {
				int lastMaxId = getCurrentMaxId()-100;
				while (true) {
					System.out.print("Checking for new invocations.. ");
					int currentMaxId = getCurrentMaxId();
					if (currentMaxId > lastMaxId) {
						System.out.println("Found " + (currentMaxId - lastMaxId) + " new invocation(s)!");
						fetchAndProcess(lastMaxId, currentMaxId);
						lastMaxId = currentMaxId;
					} else {
						System.out.println("No new invocation(s) found.");
						try {
							Thread.sleep(1000 * 15);
						} catch (InterruptedException e) {
							break; // Allow us to close the connection and exit cleanly
						}
					}
				}
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
	}

	private void fetchAndProcess(int lastMaxId, int currentMaxId) throws SQLException {
		Statement stmt = connection.createStatement();
		StringBuffer sql = new StringBuffer();
		sql.append("select c1.id, c1.class sourceClass, c2.id, c2.class targetClass, p1.id, p1.package, p2.id, p2.package, i1.instance sourceInstance, i2.instance targetInstance, invocation_count invocations from invocation_fact ");
		sql.append("left join class c1 on src_class_id=c1.id ");
		sql.append("left join class c2 on target_class_id=c2.id ");
		sql.append("left join package p1 on src_package_id=p1.id ");
		sql.append("left join package p2 on target_package_id=p2.id ");
		sql.append("left join instance i1 on src_instance_id=i1.id ");
		sql.append("left join instance i2 on target_instance_id=i2.id ");
		sql.append("where invocation_fact.id>" + lastMaxId + " && invocation_fact.id<=" + currentMaxId);
		ResultSet rs = stmt.executeQuery(sql.toString());
		while (rs.next()) {
			process(rs);
		}
		rs.close();
		stmt.close();
	}

	private void process(ResultSet rs) throws SQLException {
		String source = rs.getString("sourceInstance");
		Map sourceMetaData = new HashMap();
		sourceMetaData.put("classId", new Integer(rs.getInt("c1.id")));
		sourceMetaData.put("packageId", new Integer(rs.getInt("p1.id")));
		if (source == null || "unknown".equals(source)) {
			source = rs.getString("sourceClass");
		} else {
			source = fixSql(source);
		}
		String target = rs.getString("targetInstance");
		Map targetMetaData = new HashMap();
		targetMetaData.put("classId", new Integer(rs.getInt("c2.id")));
		targetMetaData.put("packageId", new Integer(rs.getInt("p2.id")));
		if (target == null || "unknown".equals(target)) {
			target = rs.getString("targetClass");
		} else {
			target = fixSql(target);
		}
		dataListener.addInvocation(source, target, rs.getInt("invocations"), sourceMetaData, targetMetaData);
	}

	private String fixSql(String str) {
		if(str.startsWith("db://")) {
			int i = str.indexOf("sql://");
			if(i!=-1) {
				str = str.substring(i);
				if(str.length()>32) {
					str = str.substring(0, 32)+"..";
				}
				return str;
			} else {
				i = str.indexOf("/", 6);
				return "db://"+str.substring(i+1);
			}
		}
		return str;
	}

	private int getCurrentMaxId() throws SQLException {
		int maxId = -1;
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select max(id) from invocation_fact");
		if (rs.next()) {
			maxId = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return maxId;
	}
	
	public TimeSeriesCollection getTest(Node node) {
		int packageId = ((Integer) node.getMetaData().get("packageId")).intValue();
		int classId = ((Integer) node.getMetaData().get("classId")).intValue();
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT d_date.year_v, d_date.month_v, d_date.day_v, d_time.hh, d_time.mm, ");
		sql.append("method_measurement_fact.class_id, method_measurement_fact.package_id, sum(invocation_count), ");
		sql.append("avg(avg_response_time), max(max_response_time), sum(unchecked_exceptions), sum(checked_exceptions) ");
		sql.append("FROM method_measurement_fact ");
		sql.append("INNER JOIN d_date ON d_date.id = method_measurement_fact.d_date_id ");
		sql.append("INNER JOIN d_time ON d_time.id = method_measurement_fact.d_time_id ");
		sql.append("WHERE ");
		sql.append("d_date.year_v = "+now.get(Calendar.YEAR)+" AND ");
		sql.append("d_date.month_v = "+now.get(Calendar.MONTH)+" AND ");
		sql.append("d_date.day_v = "+now.get(Calendar.DATE)+" AND ");
//		sql.append("d_time.hh >= 12 AND ");
		sql.append("method_measurement_fact.class_id = "+classId+" AND ");
		sql.append("method_measurement_fact.package_id = "+packageId+" ");
		sql.append("GROUP BY 1,2,3,4,5,6,7 LIMIT 1440;");	
	
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			TimeSeries ts1 = new TimeSeries("Invocation Count", Minute.class);
			TimeSeries ts2 = new TimeSeries("Average Response Time", Minute.class);
			TimeSeries ts3 = new TimeSeries("Max Response Time", Minute.class);
			TimeSeries ts4 = new TimeSeries("Uncecked Exceptions", Minute.class);
			TimeSeries ts5 = new TimeSeries("Checked Exceptions", Minute.class);
			while(rs.next()) {
				int year = i(rs.getString(1));
				int month = i(rs.getString(2));
				int day = i(rs.getString(3));
				int hour = i(rs.getString(4));
				int minute = i(rs.getString(5));
				double invocationCount = rs.getDouble(8);
				double avgResponseTime = rs.getDouble(9);
				double maxResponseTime = rs.getDouble(10);
				double uncheckedExceptions = rs.getDouble(11);
				double checkedExceptions = rs.getDouble(12);
				if(year!=-1 && month!=-1 && day!=-1 && hour!=-1 && minute!=-1) {
					ts1.add(new Minute(minute, hour, day, month, year), invocationCount);
					ts2.add(new Minute(minute, hour, day, month, year), avgResponseTime);
					ts3.add(new Minute(minute, hour, day, month, year), maxResponseTime);
					ts4.add(new Minute(minute, hour, day, month, year), uncheckedExceptions);
					ts5.add(new Minute(minute, hour, day, month, year), checkedExceptions);
				}
			}
			rs.close();
			stmt.close();
			TimeSeriesCollection tsc = new TimeSeriesCollection();
			tsc.addSeries(ts1);
			tsc.addSeries(ts2);
			tsc.addSeries(ts3);
			tsc.addSeries(ts4);
			tsc.addSeries(ts5);
			return tsc;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private int i(String s) {
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return -1;
		}
	}
}
