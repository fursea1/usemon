package org.usemon.domain;

import java.util.Calendar;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.usemon.domain.query.ArithmeticComparator;
import org.usemon.domain.query.Filter;
import org.usemon.domain.query.QueryFact;

public class UsemonQueryObjectTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Ignore
	public void testGenerateSQL() {
		UsemonQueryObject queryObject = new UsemonQueryObject();
		
		queryObject.addVerticalDimension("d_date.date_v");
		queryObject.addVerticalDimension("method.method");
		
		
		queryObject.setFromDate(new Date());
		queryObject.setToDate(new Date());
		queryObject.setObservationType("method_measurement_fact");
		
//		System.out.println(queryObject.generateSQL());
		
		
		
	}

	@Test
	public void testGenerateSQL2() {
		UsemonQueryObject queryObject = new UsemonQueryObject();
		queryObject.setObservationType("method_measurement_fact");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, -5);
		queryObject.setFromDate(cal.getTime());
		
		queryObject.setToDate(new Date());
		queryObject.setFact(new QueryFact("avg(avg_response_time)", true));
		queryObject.addVerticalDimension("package.package");
		queryObject.addVerticalDimension("class.class");		
		queryObject.addVerticalDimension("method.method");		
		queryObject.addVerticalDimension("principal.principal");

		queryObject.setVerticalLimit(10);
		queryObject.addVerticalFilter(new Filter("principal.principal","unknown",ArithmeticComparator.NOT_EQUALS));
		
		queryObject.setHorizontalDimension("location.location");
		queryObject.setHorizontalLimit(3);
		queryObject.addHorizontalFilter(new Filter("location.platform", "default", ArithmeticComparator.EQUALS));
//		System.out.println(queryObject.generateSelectPart());
//		System.out.println(queryObject.generateFromPart());
//		System.out.println(queryObject.generateWhereClause("2"));
//		System.out.println(queryObject.generateGroupBy());
//		System.out.println(queryObject.generateOrderBy(queryObject.getPrimaryFact()));;
//		System.out.println(queryObject.generateLimitClause(queryObject.getPrimaryLimit()));
//		
//		
//		
//		System.out.println(queryObject.generateDimension2SelectPart());
//		System.out.println(queryObject.generateDimension2FromPart());
//		System.out.println(queryObject.generateDimension2WhereClause());
//		System.out.println(queryObject.generateDimension2GroupBy());
//		System.out.println(queryObject.generateOrderBy(queryObject.getDimension2Fact()));
//		System.out.println(queryObject.generateLimitClause(queryObject.getDimension2Limit()));
		
//		System.out.println(queryObject.generateSQL());

		
				
		
		
	}
}
