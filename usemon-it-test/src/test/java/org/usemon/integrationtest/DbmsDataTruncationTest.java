/**
 * Created 23. jan.. 2008 15.59.42 by Steinar Overbeck Cook
 */
package org.usemon.integrationtest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.usemon.service.DbmsHelper;

import com.mysql.jdbc.MysqlDataTruncation;

import static org.junit.Assert.*;
/**
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class DbmsDataTruncationTest {

	Connection con;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		con = DbmsHelper.getConnection();
	}

	@After
	public void tearDown() throws SQLException {
		if (con != null)
			con.close();
	}

	@Test
	public void testDataTrunction()  {
		String stringValue = " value=db://com.ibm.ws.rsadapter.jdbc.WSJdbcConnection@5fcbbc80/KAP3/sql://" +
				"insert into AbnInfo1 " +
				"(reqId,nummer,nrtyp,linjenr,fyslinid,katopf,linjetils,speskobl,part,bkat,penavn," +
				"kundesystem,komnr,vktyp,vkode,veinavn,husnr,husboks,etg,leilnr,ponr,poststed,farid,gslsign," +
				"tlkmelding,tlkreturkode,lagretTid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
				"insert into AbnLinje1 (reqId,nummer,nrtyp,linjenr,linjestykke,fnavn,ftype,insasign,bruksm," +
				"lengde,traad,mks,pupin,vern,tapp,lagretTid, lkhastighetned) " +
				"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null) " +
				"insert into AbnLinje1 (reqId,nummer,nrtyp,linjenr,linjestykke,fnavn,ftype,insasign,bruksm," +
				"lengde,traad,mks,pupin,vern,tapp,lagretTid, lkhastighetned) " +
				"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null) " +
				"insert into AbnLinje1 (reqId,nummer,nrtyp,linjenr,linjestykke,fnavn,ftype,insasign,bruksm," +
				"lengde,traad,mks,pupin,vern,tapp,lagretTid, lkhastighetned) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null)";
		try {
			PreparedStatement ps = con.prepareStatement("insert into instance (instance) values(?)");
			ps.setString(1, stringValue);
			ps.execute();
		} catch (SQLException e) {
			assertTrue("Exception does not contain 'Data truncation'", e.getMessage().indexOf("Data truncation") > -1);
		}
		
	}

}
