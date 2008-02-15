/**
 * Created 29. jan.. 2008 10.10.09 by Steinar Overbeck Cook
 */
package org.usemon.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class DbmsHelperTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.usemon.service.DbmsHelper#getSqlStatement(java.lang.String)}.
	 */
	@Test
	public void testGetSqlStatement() {
		String sql = DbmsHelper.loadSqlStatementFromResource(DbmsHelper.LAST_30_METHOD_MEASUREMENTS_SQL);
				assertNotNull(sql);
	}

}
