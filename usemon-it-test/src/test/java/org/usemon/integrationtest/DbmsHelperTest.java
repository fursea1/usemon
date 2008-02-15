/**
 * 
 */
package org.usemon.integrationtest;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.usemon.service.DbmsHelper;

/**
 * @author t547116
 *
 */
public class DbmsHelperTest {

	/**
	 * Test method for {@link org.usemon.service.DbmsHelper#getConnection()}.
	 */
	@Test
	public void testGetConnection() {
		for (int i = 0; i < 10; i++) {
			Connection con = DbmsHelper.getConnection();
			DbmsHelper.close(con);
		}
	}
	
	@Test
	public void testCommitStatus() throws SQLException {
		Connection con = DbmsHelper.getConnection();
		int txIsolation = con.getTransactionIsolation();
		String s = null;
		switch (txIsolation) {
		case Connection.TRANSACTION_NONE:
				System.err.println("NONE");
			break;
		case Connection.TRANSACTION_READ_COMMITTED:
				System.err.println("READ_COMMITED");
				break;
		case
			Connection.TRANSACTION_READ_UNCOMMITTED:
				System.err.println("Read uncommited");
				break;
		case Connection.TRANSACTION_REPEATABLE_READ:
				System.err.println("Repeatable read");
				break;
		case Connection.TRANSACTION_SERIALIZABLE:
				System.err.println("Serializable");
				break;
				
		default:
				System.err.println("Unknown isloation level");
			break;
		}
		assertTrue(con.getAutoCommit());
	}
}
