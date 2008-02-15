/**
 * Created 28. nov.. 2007 16.00.12 by Steinar Overbeck Cook
 */
package org.usemon.service.util;

import java.sql.ResultSet;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface ResultSetProcessor {

	Object forEachRow(ResultSet rs);
}
