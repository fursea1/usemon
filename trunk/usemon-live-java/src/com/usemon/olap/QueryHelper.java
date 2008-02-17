package com.usemon.olap;

public class QueryHelper {
	
	public String buildQueryFor_ClassBackInTime(String packageName, String className, Fact[] facts, Period aggregationPeriod, int unitsBackInTime) {
		StringBuilder q = new StringBuilder();
		
		q.append("SELECT { ");
		for(int n=0;n<facts.length;n++) {
			q.append("["+facts[n].getFactName()+"]");
			if((n+1)<facts.length) {
				q.append(", ");
			}
		}
		q.append(" ON COLUMNS, ");
		
//		q.

		q.append("FROM Usemon ");
		q.append("WHERE [Package].["+packageName+"] ");
		q.append("[Class].["+className+"]");
/*
		"[Invocations], [Measures].[Average Response Time], [Measures].[Max Response Time], [Measures].[Checked Exceptions], [Measures].[Unchecked Exceptions], [Measures].[Exceptional Exits], [Measures].[CPU Time]} ON COLUMNS,
		  [Date.Date].[All Date.Dates].[2008].[2].Children ON ROWS
		from [Usemon]
		where [Class.Class].[All Class.Classs].[K2Bean]
*/
		
		
		return q.toString();
	}

}
