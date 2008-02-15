/**
 * Created 15. nov.. 2007 13.11.08 by Steinar Overbeck Cook
 */
package org.usemon.service.guice;

import javax.sql.DataSource;

import org.usemon.service.DbmsHelper;

import com.google.inject.Provider;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class DataSourceProvider implements Provider<DataSource> {

	/** 
	 * @see com.google.inject.Provider#get()
	 */
	public DataSource get() {
		return DbmsHelper.getDataSource();
	}

}
