package org.usemon.service.util;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.usemon.domain.UsemonQueryObject;
import org.usemon.domain.query.ArithmeticComparator;
import org.usemon.domain.query.Filter;
import org.usemon.domain.query.QueryFact;
/**
 * @author t514257 (Jarle Dagestad Thu)
 *
 */
public class WizardQueryGeneratorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testGenerateInitialSQLQuery() {
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
		
//		queryObject.setDimension2("location.location");
//		queryObject.setDimension2Limit(3);
//		queryObject.setDimension2Fact(new QueryFact("sum(invocation_count)", true));
//		queryObject.addDimension2Filter(new Filter("location.platform", "default", ArithmeticComparator.EQUALS));
		
		WizardQueryGenerator wizardQueryGenerator = new WizardQueryGenerator(queryObject);
		System.out.println(wizardQueryGenerator.generateHorizontalSQLQuery());
		
		System.out.println(wizardQueryGenerator.generateVerticalQuery("2"));
	}

}
