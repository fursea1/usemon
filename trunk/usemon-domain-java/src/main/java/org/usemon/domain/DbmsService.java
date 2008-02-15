/**
 * Created 15. nov.. 2007 11.25.25 by Steinar Overbeck Cook
 */
package org.usemon.domain;

import javax.sql.DataSource;

/** Services related to the DBMS
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface DbmsService {
	
	DataSource dataSource();
	
}
