/**
 * 
 */
package org.usemon.service.util;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;

import org.usemon.domain.J2eeLocation;
import org.usemon.domain.TimeStampHelper;
import org.usemon.service.dao.DimensionalDaoImpl;

/**
 * Declares the dimensions which are known to us together with various methods
 * to be invoked by the {@link DimensionalDaoImpl} when creating new values for
 * a dimension.
 * <p>TODO: Refactor this into separate classes for the various types of dimensions. I.e. special treatment 
 * of the date and time dimensions.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * @author t514257 (Jarle Dagestad Thu)
 * 
 */
public enum Dimension {
	CHANNEL("channel", "id"), 
	CLASS("class", "id"), 
	DATE("d_date", "id"), 
	TIME("d_time", "id"), 
	LOCATION("location", "id"), 
	METHOD("method", "id"), 
	INSTANCE("instance", "id"), 
	PACKAGE("package", "id"), 
	PRINCIPAL("principal", "id");

	String keyColName;
	String tableName;

	/**
	 * Constructor, which is private of course.
	 * 
	 * @param tableName
	 *            name of table holding the dimension
	 * @param keyColName
	 *            name of column holding the primary key
	 */
	private Dimension(String tableName, String keyColName) {
		this.tableName = tableName;
		this.keyColName = keyColName;
	}

	public String getKeyColName() {
		return keyColName;
	}

	public String getTableName() {
		return tableName;
	}

	/**
	 * Generates the SQL for inserting another instance of this dimension and
	 * the parameters required to insert the dimensional value specified.
	 * 
	 * @param dimensionValue
	 *            value for the new dimension value to be inserted
	 * @return sql and jdbc parameters
	 */
	public JdbcParams sqlDataForInsert(Object dimensionValue) {
		String sql = generateInsertSql();
		Object[] params = getInsertParams(dimensionValue);
		return new JdbcParams(sql, params);
	}

	/**
	 * Generates the sql and parameters required to locate the given dimension
	 * value
	 */
	public JdbcParams jdbcParamsForSelect(Object dimensionValue) {
		String sql = String.format("select %s from %s where %s", getKeyColName(), getTableName(), getWhereClauseForSelect());
		Object[] params = getSelectParams(dimensionValue);
		return new JdbcParams(sql, params);
	}

	/** Simple DTO for returning SQL string and associated parameters */
	public static class JdbcParams {
		public final String sql;
		public final Object[] params;

		protected JdbcParams(String sql, Object[] params) {
			this.sql = sql;
			this.params = params;
		}
	}

	/**
	 * Produces the required where clause for this dimension. The where clause
	 * contains positional parameters, i.e. '?'
	 * <p>
	 * The where-clause is only used when finding a dimensional foreign key by
	 * executing a SQL select statement.
	 * <p>
	 * NOTE! This method needs to be kept in synchronization with the
	 * {@link #getParams(Object, boolean)} method, which is kind of bad ;-)
	 * FIXME: refactor this piece of code to prevent the kludge mentioned above.
	 */
	protected String getWhereClauseForSelect() {
		String whereClause = null;
		switch (this) {
		case DATE:
			whereClause = String.format("year_v = ? and month_v = ? and day_v = ? and day_of_week_v = ?");
			break;
		case TIME:
			whereClause = String.format("hh = ? and mm = ? and ss = ?");
			break;
		case LOCATION:
		default:
			whereClause = this.getTableName() + " = " + "?";
			break;
		}
		return whereClause;
	}

	/**
	 * Transforms a lookup value into the appropriate number of objects,
	 * suitable for invocation of
	 * {@link PreparedStatement#setObject(int, Object)}
	 * 
	 * @param lookupValue
	 * @return
	 */
	protected Object[] getInsertParams(Object lookupValue) {
		return getParams(lookupValue, true);
	}

	/**
	 * Transforms a lookup value into the appropriate number of objects,
	 * suitable for invocation of
	 * {@link PreparedStatement#setObject(int, Object)}
	 * 
	 * @param lookupValue
	 * @return
	 */
	protected Object[] getSelectParams(Object lookupValue) {
		return getParams(lookupValue, false);
	}

	/**
	 * Transforms a lookup value into the appropriate number of objects,
	 * suitable for invocation of
	 * {@link PreparedStatement#setObject(int, Object)}
	 * 
	 * @param dimensionValue
	 * @param insertParams -
	 *            true if parameters are fetched for insert purposes
	 * @return
	 */
	private Object[] getParams(Object dimensionValue, boolean insertParams) {
		Object[] result = null;
		Calendar cal = null;
		switch (this) {
		case DATE:
			cal = Calendar.getInstance();
			cal.setTime((Date) dimensionValue);
			if (insertParams) {
				result = new Object[] { cal.get(Calendar.YEAR), // year
						cal.get(Calendar.MONTH) + 1, // month (Calendar uses
														// start index of 0)
						cal.get(Calendar.DAY_OF_MONTH), // day
						WeekDay.valueOf(cal.get(Calendar.DAY_OF_WEEK)).toString(), // mon,
																					// tue,
																					// wed
																					// etc....
						TimeStampHelper.clearTimeAttributes(cal.getTime()) // the
																			// date
																			// without
																			// the
																			// time
																			// part
				};
			} else {
				result = new Object[] { cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), WeekDay.valueOf(cal.get(Calendar.DAY_OF_WEEK)).toString() };
			}

			break;
		case TIME:
			cal = Calendar.getInstance();
			cal.setTime((Date) dimensionValue);
			if (insertParams) {
				result = new Object[] { cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND),
						TimeStampHelper.modifyTimeStampGranularity(cal.getTime()) };
			} else {
				result = new Object[] { cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND) };
			}

			break;
		case LOCATION:
			if (insertParams) {
				J2eeLocation l = (J2eeLocation) dimensionValue;
				// Insertion of a location requires us to split the location
				// into its separate components:
				result = new Object[] { l.getLocationName(), l.getPlatformName(), l.getClusterName(), l.getServerName() };
			} else {
				// Lookup is done using the value of the location
				result = new Object[] { dimensionValue.toString() };
			}
			break;
		default:
			// No dimensional value should exceed 255 less 1, just in case
			result = new Object[] { trimToMax(dimensionValue.toString(),254) };
			break;
		}
		return result;
	}

	protected static String trimToMax(String s, int max) {
		if (s.length() <= max)
			return s;
		else {
			String newString = s.substring(0,max-1);
			return newString;
		}
	}

	public int getForeignKeyForUnknownValue() {
		return 1;
	}

	/**
	 * Generates the SQL required to insert a new dimensional value of this type
	 * <p>
	 * NOTE! this method must be kept in synch with
	 * {@link #getInsertParams(Object)}
	 */
	protected String generateInsertSql() {
		String result = null;
		switch (this) {
		case DATE:
			result = String.format("insert into %s values(0,?,?,?,?,?)", getTableName());
			break;
		case TIME:
			result = String.format("insert into %s values(0,?,?,?,?)", getTableName());
			break;
		case LOCATION:
			result = String.format("insert into %s values(0,?,?,?,?)", getTableName());
			break;
		default:
			result = String.format("insert into %s values(0,?)", getTableName());
		}
		return result;
	}

	/** Retrieves the property value of a bean and transforms it according to built in rules.
	 * The reson for coding this method is the fact that the time part should be knocked of
	 * for the DATE dimension, while the date part should be knocked off for the TIME dimension.  
	 * 
	 * @param getterMethod references the getter method for the property
	 * @param bean references the bean
	 * @return transformed value of the property
	 */ 
	protected Object getValue(Method getterMethod, Object bean) {
		if (getterMethod == null)
			throw new IllegalArgumentException("getterMethod is a required argument");
		if (bean == null)
			throw new IllegalArgumentException("Can not invoke getter method on null");

		Object lookupValue = null;
		try {
			lookupValue = getterMethod.invoke(bean, (Object[]) null);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to invoke method " + getterMethod.getName() + " on bean ");
		}

		switch (this) {
		case DATE:
			Date dt = TimeStampHelper.clearTimeAttributes((Date) lookupValue);
			lookupValue = dt;
			break;
		case TIME:
			Date time = TimeStampHelper.clearDateAttributes((Date) lookupValue);
			lookupValue = time;
			break;
		default:

		}
		return lookupValue;
	}
}