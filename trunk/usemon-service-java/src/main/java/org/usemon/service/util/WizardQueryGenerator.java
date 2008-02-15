package org.usemon.service.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.usemon.domain.UsemonQueryObject;
import org.usemon.domain.query.Filter;

/**
 * This class generates sql statements required for dimensaional queries based upon the values in the UsemonQueryObject
 * 
 * @author t514257 (Jarle Dagestad Thu)
 * 
 */
public class WizardQueryGenerator {

	UsemonQueryObject queryDataObject;

	boolean isInitialized = false;

	// TODO: Need access to the dimension Enum
	String factTableIDAppend = "_id";
	String dimtable_id_column = ".id";

	/**
	 * Constructor Initialize with the data object.
	 * 
	 * @param queryDataObject
	 */
	public WizardQueryGenerator(UsemonQueryObject queryDataObject) {
		this.queryDataObject = queryDataObject;
	}

	public String generateVerticalQuery(String horizontalValue) {
		StringBuffer sql = new StringBuffer();

		sql.append(generateVerticalSelectPart());
		sql.append(generateVerticalFromPart());
		sql.append(generateVerticalWhereClause(horizontalValue));
		sql.append(generateVerticalGroupBy());
		sql.append(generateVerticalOrderBy());
		sql.append(generateLimitClause(queryDataObject.getVerticalLimit()));

		return sql.toString();
	}

	/**
	 * Generates a horizontal query used for retrieving the filter criteria for the vertical dimension.
	 * 
	 * @return
	 */
	public String generateHorizontalSQLQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append(generateHorizontalSelectPart());
		sql.append(generateHorizontalFromPart());

		sql.append(generateHorizontalWhereClause());

		sql.append(generateHorizontalGroupBy());
		sql.append(generateHorizontalOrderBy());
		sql.append(generateLimitClause(queryDataObject.getHorizontalLimit()));
		return sql.toString();
	}

	public boolean isHorizontalQueryRequired() {
		String horizontalDimension = queryDataObject.getHorizontalDimension();
		if (horizontalDimension != null && horizontalDimension.length() > 0) {
			return true;
		}
		return false;
	}

	protected String generateVerticalSelectPart() {
		StringBuffer sql = new StringBuffer();
		sql.append(" select ");
		sql.append(" concat( ");
		StringBuffer sb = new StringBuffer();
		for (String dimension : queryDataObject.getVerticalDimensionList()) {
			if (sb.length() > 0)
				sb.append(", '.',");
			sb.append(dimension);
		}
		sql.append(sb);
		
		sql.append("), ");
		sql.append(queryDataObject.getFact().getFactName());

		return sql.toString();
	}

	protected String generateHorizontalSelectPart() {
		StringBuffer sql = new StringBuffer();
		sql.append(" select ");

		sql.append(extractTableNameFromTableAndColumn(queryDataObject.getHorizontalDimension()) + factTableIDAppend);
		sql.append(", " + queryDataObject.getHorizontalDimension());

		return sql.toString();
	}

	protected String generateVerticalFromPart() {
		StringBuffer sql = new StringBuffer();
		sql.append(" from ");
		sql.append(queryDataObject.getObservationType()); // the fact table
		sql.append(", ");

		ArrayList<String> dimTables = getDimTables();
		if (!dimTables.contains("d_date")) {
			dimTables.add("d_date");
		}
		if (!dimTables.contains("d_time")) {
			dimTables.add("d_time");
		}

		for (String tableName : dimTables) {
			sql.append(tableName);
			sql.append(", ");
		}
		// Strip the last comma
		sql.setLength(sql.length() - 2);

		return sql.toString();
	}

	protected String generateHorizontalFromPart() {
		ArrayList<String> tables = new ArrayList<String>();

		tables.add(queryDataObject.getObservationType());
		tables.add(extractTableNameFromTableAndColumn(queryDataObject.getHorizontalDimension()));

		if (queryDataObject.horizontalDimensionHasFilter()) {
			for (Filter filter : queryDataObject.getHorizontalFilterList()) {
				String tableName = extractTableNameFromTableAndColumn(filter.getTableColumn());
				if (!tables.contains(tableName)) {
					tables.add(tableName);
				}

			}

		}
		if (!tables.contains("d_date")) {
			tables.add("d_date");
		}
		if (!tables.contains("d_time")) {
			tables.add("d_time");
		}

		StringBuffer sql = new StringBuffer();
		sql.append(" from ");
		for (String tableName : tables) {
			sql.append(tableName + ", ");
		}

		sql.setLength(sql.length() - 2);

		return sql.toString();
	}

	protected String generateVerticalWhereClause() {
		return this.generateVerticalWhereClause(null);
	}

	/**
	 * Generates a where clause using the horizontalDimensionValue as a filter
	 * 
	 * @param horizontalDimensionValue -
	 *            filter value, ignored if null
	 * @return
	 */
	protected String generateVerticalWhereClause(String horizontalDimensionValue) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where ");

		// First the joins

		ArrayList<String> tableList = getDimTables();
		for (String tableName : tableList) {
			sql.append(tableName + factTableIDAppend + " = " + tableName + dimtable_id_column + " and ");
		}

		// Then add the horizontal Dimension clause if applicable
		if (queryDataObject.getHorizontalDimension() != null && horizontalDimensionValue != null) {

			sql.append(queryDataObject.getHorizontalDimension());
			sql.append(" = '");
			sql.append(horizontalDimensionValue);
			sql.append("' and ");
		}

		// Then the filters
		for (Filter filter : queryDataObject.getVerticalFilterList()) {
			sql.append(filter.getTableColumn() + " ");
			sql.append(filter.getComparator().getComparator() + " ");
			sql.append("'" + filter.getValue() + "' and ");

		}

		// Then the date
		sql.append(generateDateWhereClause());

		return sql.toString();
	}

	private String generateDateWhereClause() {
		StringBuffer sql = new StringBuffer();
		sql.append(" d_date_id = d_date.id and ");
		sql.append("d_time_id = d_time.id and ");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		sql.append("timestamp(date_v, time_v) between '" + formatter.format(queryDataObject.getFromDate()) + "' and '"
				+ formatter.format(queryDataObject.getToDate()) + "' ");
		return sql.toString();
	}

	protected String generateHorizontalWhereClause() {
		StringBuffer sql = new StringBuffer();
		String dim2TableName = extractTableNameFromTableAndColumn(queryDataObject.getHorizontalDimension());
		sql.append(" where ");
		sql.append(dim2TableName + factTableIDAppend + " = " + dim2TableName + dimtable_id_column);
		if (queryDataObject.horizontalDimensionHasFilter()) {
			sql.append(" and ");
			for (Filter filter : queryDataObject.getHorizontalFilterList()) {
				String filtertable = extractTableNameFromTableAndColumn(filter.getTableColumn());
				sql.append(filtertable + factTableIDAppend);
				sql.append(" = ");
				sql.append(" " + filtertable + dimtable_id_column + " and");

				sql.append(" " + filter.getTableColumn());
				sql.append(" " + filter.getComparator().getComparator() + " ");
				sql.append("'" + filter.getValue() + "' and ");
			}
			sql.setLength(sql.length() - 4);
		}

		sql.append(" and " + generateDateWhereClause());
		return sql.toString();

	}

	protected String generateVerticalGroupBy() {
		StringBuffer sql = new StringBuffer();
		sql.append(" group by ");
		for (String dimension : queryDataObject.getVerticalDimensionList()) {
			sql.append(dimension);
			sql.append(", ");
		}
		sql.setLength(sql.length() - 2);
		return sql.toString();
	}

	protected String generateHorizontalGroupBy() {
		StringBuffer sql = new StringBuffer();
		sql.append(" group by ");
		sql.append(queryDataObject.getHorizontalDimension());
		return sql.toString();
	}

	public ArrayList<String> getDimTables() {

		ArrayList<String> dimTableNames = new ArrayList<String>();
		for (String dimFields : queryDataObject.getVerticalDimensionList()) {
			String tableName = extractTableNameFromTableAndColumn(dimFields);
			if (tableName != null) {
				dimTableNames.add(tableName);
			}
		}
		// Horizontal dimension may be null
		String tableName = extractTableNameFromTableAndColumn(queryDataObject.getHorizontalDimension());
		if (tableName != null) {
			// add if not already added
			if (!dimTableNames.contains(tableName)) {
				dimTableNames.add(tableName);
			}

		}

		return dimTableNames;
	}

	protected String generateHorizontalOrderBy() {
		StringBuffer sql = new StringBuffer();
		sql.append(" order by ");
		if (queryDataObject.isOrderHorizontalAlpha()) {
			String convertedHorizontalDimension = convertToSpecialSortingIfNeeded(queryDataObject.getHorizontalDimension());
			sql.append(convertedHorizontalDimension);
		} else if (queryDataObject.isOrderHorizontalFact()) {
			sql.append(queryDataObject.getFact().getFactName());
		}
		if (queryDataObject.isOrderHorizontalDesc()) {
			sql.append(" desc ");
		} else {
			sql.append(" asc ");
		}
		return sql.toString();
	}

	/**
	 * This method handles special types that do not sort alphabetically in a normal fashion.
	 * 
	 * @param dimension
	 * @return cast expression for the given dimension
	 */
	private String convertToSpecialSortingIfNeeded(String dimension) {
		String returnValue = dimension;
		/* The hour in time dimension should be ordered as a numeric, not string */
		if ("d_time.hh".equalsIgnoreCase(dimension)) {
			returnValue = "cast(" + dimension + " as SIGNED)";
		} else if ("d_date.day_of_week_v".equalsIgnoreCase(dimension)) {
			/* Day of week, actually needs the DAYOFWEEK function on date_v to get the correct order */
			returnValue = "DAYOFWEEK(d_date.date_v)";
		}

		return returnValue;
	}

	protected String generateVerticalOrderBy() {
		StringBuffer sql = new StringBuffer();
		sql.append(" order by ");
		if (queryDataObject.isOrderVerticalAlpha()) {
			int verticalDimensionSize = queryDataObject.getVerticalDimensionList().size();
			for (int i = 0; i < verticalDimensionSize; i++) {
				sql.append(queryDataObject.getVerticalDimensionList().get(i));
				/* Add separator only if there are more dimensions left */
				if (i < verticalDimensionSize - 1) {
					sql.append(", ");
				}
			}

		} else if (queryDataObject.isOrderVerticalFact()) {
			sql.append(queryDataObject.getFact().getFactName());
		}
		if (queryDataObject.isOrderVerticalDesc()) {
			sql.append(" desc ");
		} else {
			sql.append(" asc ");
		}
		return sql.toString();
	}

	protected String generateLimitClause(int limit) {
		StringBuffer sql = new StringBuffer();
		if (limit > 0) {
			sql.append(" limit ");
			sql.append(limit);
		}
		return sql.toString();
	}

	private String extractTableNameFromTableAndColumn(String tableAndColumn) {
		if (tableAndColumn != null) {
			String[] tokens = tableAndColumn.split("[.]");
			if (tokens.length > 0) {
				return tokens[0];
			}
		}

		return null;
	}

	/**
	 * Provides the ResultSet index number for the column holding the value of the dimension. This number depends highly upon the
	 * field order of the SQL Select statement
	 * 
	 * @return the column number
	 */
	public int getHorizontalDimensionalValueIndex() {
		return 2;
	}

}
