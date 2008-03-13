package org.usemon.live.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.usemon.live.applet.Node;
import org.usemon.live.applet.nodes.BeanNode;

public class FactService extends Thread {
	private Connection connection;
	private FactListener factListener;
	private LinkedList queue;
	
	public static void main(String[] args) throws InterruptedException {
		FactService fs = new FactService(new FactListener() {
			public void factsArrived(Node node, JFreeChart chart) {
				try {
					ChartUtilities.saveChartAsPNG(new File("c:\\chart.png"), chart, 640, 480);
					System.out.println("Done");
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		Map md = new HashMap();
		md.put("packageId", new Integer(12));
		md.put("classId", new Integer(13));
		Node n = new BeanNode(md, "", "");
		fs.orderFacts(n);
		Thread.sleep(1000*60*10);
	}
	
	public FactService(FactListener factListener) {
		this.factListener = factListener;
		this.queue = new LinkedList();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/usemon?autoReconnect=true", "usemonmonitor", "usemonmonitor");
			setName("FactService Thread");
			setDaemon(true);
			start();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void orderFacts(Node node) {
		queue.add(node);
		interrupt();
	}
	
	public void run() {
		while(true) {
			while(!queue.isEmpty()) {
				process((Node) queue.removeFirst());
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// Safe to ignore
			}
		}
	}
	
	private void process(Node node) {
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
		sql.append("d_time.hh <= "+now.get(Calendar.HOUR)+" AND ");
		sql.append("d_time.mm <= "+now.get(Calendar.MINUTE)+" AND ");
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
			factListener.factsArrived(node, createChart(node.toString(), tsc));
		} catch(SQLException e) {
			e.printStackTrace();
		}
		factListener.factsArrived(node, null);
	}

	private JFreeChart createChart(String title, TimeSeriesCollection tsc) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, null, null, tsc, true, true, false);

//		XYBarRenderer renderer = new XYBarRenderer();

		XYPlot plot = chart.getXYPlot();
		System.out.println(plot.getClass().getName());
//		plot.setRenderer(renderer);
		
		return chart;
	}

	private int i(String s) {
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
}
