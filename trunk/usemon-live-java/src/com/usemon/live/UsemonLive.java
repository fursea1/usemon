package com.usemon.live;

import hypergraph.graphApi.GraphException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;

public class UsemonLive {

	private Connection connection;
	private DependencyPanel panel;

	private UsemonLive() throws ClassNotFoundException, SQLException, GraphException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://metromon2.corp.telenor.no:3306/usemon?autoReconnect=true",
				"usemonmonitor", "usemonmonitor");
		
		panel = new DependencyPanel();
        JFrame f = new JFrame("U s e m o n | L i v e");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(panel);
        f.setSize(800, 600);
        f.setVisible(true);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, GraphException {
		UsemonLive ur = new UsemonLive();
		ur.start();
	}

	private void start() throws SQLException, GraphException {
		int lastMaxId = getCurrentMaxId();
		while(true) {
			System.out.print("Checking for new invocations.. ");
			int currentMaxId = getCurrentMaxId();
			if(currentMaxId>lastMaxId) {
				System.out.println("Found "+(currentMaxId-lastMaxId)+" new invocation(s)!");
				fetchAndProcess(lastMaxId, currentMaxId);
				lastMaxId = currentMaxId;
			} else {
				System.out.println("No new invocation(s) found.");
				try {
					Thread.sleep(1000*15);
				} catch(InterruptedException e) {
					break; // Allow us to close the connection and exit cleanly
				}
			}
		}
		connection.close();
	}

	private int getCurrentMaxId() throws SQLException {
		int maxId = -1;
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select max(id) from invocation_fact");
		if(rs.next()) {
			maxId = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return maxId;
	}
	
	private void fetchAndProcess(int lastMaxId, int currentMaxId) throws SQLException, GraphException {
		Statement stmt = connection.createStatement();
		StringBuffer sql = new StringBuffer();
		sql.append("select c1.class sourceClass, c2.class targetClass, i1.instance sourceInstance, i2.instance targetInstance, invocation_count invocations from invocation_fact ");
		sql.append("left join class c1 on src_class_id=c1.id ");
		sql.append("left join class c2 on target_class_id=c2.id ");
		sql.append("left join instance i1 on src_instance_id=i1.id ");
		sql.append("left join instance i2 on target_instance_id=i2.id ");
		sql.append("where invocation_fact.id>"+lastMaxId+" && invocation_fact.id<="+currentMaxId);
		ResultSet rs = stmt.executeQuery(sql.toString());
		while(rs.next()) {
			process(rs);
		}
		rs.close();
		stmt.close();
	}

	private void process(ResultSet rs) throws SQLException, GraphException {
		String source = rs.getString("sourceInstance");
		if(source==null || "unknown".equals(source)) {
			source = rs.getString("sourceClass");
		}
		String target = rs.getString("targetInstance");
		if(target==null || "unknown".equals(target)) {
			target = rs.getString("targetClass");
		}
		if(!source.startsWith("db://") && !target.startsWith("db://")) {
			panel.addInvocation(source, target, rs.getInt("invocations"));
		}
	}


}
