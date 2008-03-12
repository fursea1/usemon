package com.usemon.olap;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.olap4j.CellSet;

public class QueryHelper {
	
	private static final DecimalFormat df = new DecimalFormat("00");
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		String query = QueryHelper.buildQueryFor_ClassBackInTime("com.telenor.metro2.system.k2.ejb", "K2Bean", new Fact[] { Fact.INVOCATIONS, Fact.AVERAGE_RESPONSE_TIME }, Period.MINUTE, 10);
		System.out.println(query);
		OlapService olap = new OlapService();
		CellSet cs = olap.executeQuery(query);
		olap.dumpCellSetAndClose(System.out, cs);
		cs.close();
	}
	
	public static String buildQueryFor_ClassBackInTime(String packageName, String className, Fact[] facts, Period aggregationPeriod, int unitsBackInTime) {
		Calendar now = Calendar.getInstance();
		StringBuilder q = new StringBuilder();
		
		q.append("SELECT { ");
		for(int n=0;n<facts.length;n++) {
			q.append("["+facts[n].getFactName()+"]");
			if((n+1)<facts.length) {
				q.append(", ");
			}
		}
		q.append(" } ON COLUMNS, ");
		
		if(aggregationPeriod==Period.MINUTE) {
			q.append("CROSSJOIN( ");
			q.append("{[Date].["+now.get(Calendar.YEAR)+"].["+(now.get(Calendar.MONTH)+1)+"].["+now.get(Calendar.DAY_OF_MONTH)+"]}, ");
			
			int nowMinute = now.get(Calendar.MINUTE);
			if(unitsBackInTime<=nowMinute) {
				q.append("{");
				for(int minute=(nowMinute-unitsBackInTime)+1;minute<=nowMinute;minute++) {
					q.append("[Time].["+df.format(now.get(Calendar.HOUR_OF_DAY))+"].["+df.format(minute)+"]");
					if((minute+1)<=nowMinute) {
						q.append(",\n ");
					}
				}
				q.append("} ");
			} else {
				//hoursBack = unitsBackInTime/60;
				
			}
			q.append(") ");
			
		} else {
			new IllegalArgumentException("Period not implemented! Choose another.");
		}
		
		q.append("ON ROWS ");
		q.append("FROM Usemon ");
		q.append("WHERE ([Package].["+packageName+"], [Class].["+className+"])");

		return q.toString();
	}
}
