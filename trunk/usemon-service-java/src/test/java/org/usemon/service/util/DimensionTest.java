/**
 * 
 */
package org.usemon.service.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class DimensionTest {

	/**
	 * Test method for {@link org.usemon.service.util.Dimension#getParams(java.lang.Object)}.
	 */
	@Test
	public void testGetInsertParams() {
		Object[] objects = Dimension.DATE.getInsertParams(new Date());
		assertEquals("Expected data to be split into 5 components",5,objects.length);
		
	}

	@Test
	public void testGetSelectParams() {
		Object[] objects = Dimension.DATE.getSelectParams(new Date());
		assertEquals("Expected data to be split into 4 components",4,objects.length);
	}
	
	@Test
	public void testInsertSqlForLocation() {
		String sql = Dimension.LOCATION.generateInsertSql();
		System.out.println(sql);
	}
	
	public void testTrimToMax() {
		String s = "abc";
		Dimension.trimToMax(s, 254);
		assertEquals(3, s.length());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 300; i++) {
			sb.append('x');
		}
		assertEquals(300, sb.length());
		s = Dimension.trimToMax(sb.toString(), 254);
		assertEquals(254, s.length());
	}
}
