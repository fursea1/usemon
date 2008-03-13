package org.usemon.live.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class LiveService extends Thread {
	private Connection connection;
	private LiveListener liveListener;

	public LiveService(LiveListener liveListener) {
		this.liveListener = liveListener;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/usemon?autoReconnect=true", "usemonmonitor", "usemonmonitor");
			setName("LiveService Thread");
			setDaemon(true);
			start();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
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
		liveListener.addInvocation(source, target, rs.getInt("invocations"), sourceMetaData, targetMetaData);
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
}
