package org.usemon.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.DimensionalQueryService;
import org.usemon.domain.UsemonQueryObject;
import org.usemon.domain.UsemonQueryResult;
import org.usemon.service.util.WizardQueryGenerator;

import com.google.inject.Inject;

/** Implementation of the dimensional query service.
 *  
 * @author t514257 (Jarle Dagestad Thu)
 *
 */
public class DimensionalQueryServiceImpl implements DimensionalQueryService {

	private DataSource dataSource = null;
	private static Logger log = LoggerFactory.getLogger(DimensionalQueryServiceImpl.class);

	/**
	 * Constructs and executes all necessary queries based upon the queryObject.
	 * Order of things are:
	 * 1) Query for the horizontal dimensional values, without the fact elements
	 * 2) For each horizontal value, query for the vertical dimensional values including the facts.
	 * 
	 * @param queryObject describes the query to be executed.
	 */
	public UsemonQueryResult fetchData(UsemonQueryObject queryObject) {

		Connection con = null;
		UsemonQueryResult queryResult;

		PreparedStatement ps = null;
		ResultSet horizontalResultSet = null;
		WizardQueryGenerator sqlGenerator = new WizardQueryGenerator(queryObject);
		String sql = "";
		ArrayList<String> verticalSQLQueryList = new ArrayList<String>();
		ArrayList<ResultSet> resultSetList = new ArrayList<ResultSet>();
		try {
			con = dataSource.getConnection();
			
			ArrayList<String> columnNames = new ArrayList<String>();
			
			if (sqlGenerator.isHorizontalQueryRequired()) {
				sql = sqlGenerator.generateHorizontalSQLQuery();
				log.debug("About to perform the following horizontal sql statement: " + sql);
				con = dataSource.getConnection();
				ps = con.prepareStatement(sql);
				horizontalResultSet = ps.executeQuery();
				
				// For each column, produces the sql required to retrieve the corresponding 
				// row values
				while (horizontalResultSet.next()) {
					// First column holds the id of domain value, second column holds the actual value
					// i.e. for dimension location, the location name is held in the second column
					int dimensionalValueIndex = sqlGenerator.getHorizontalDimensionalValueIndex();
					
					// generates the sql required for the associated row in the vertical dimension
					String verticalSql = sqlGenerator.generateVerticalQuery(horizontalResultSet.getString(dimensionalValueIndex));					
				
					// saves the column header
					columnNames.add(horizontalResultSet.getString(dimensionalValueIndex));
					// saves the vertical query
					verticalSQLQueryList.add(verticalSql);
				}
				
				for (String verticalSQL : verticalSQLQueryList) {
					log.debug("About to perform the following vertical sql statement: " + verticalSQL);
					ps = con.prepareStatement(verticalSQL);
					// Shoves the entire sql ResultSet into a list for processing further down in the code
					resultSetList.add(ps.executeQuery());
				}
			} else {
				// Only executed if you ever perform a non multidimensional query
				sql = sqlGenerator.generateVerticalQuery(null);
				ps = con.prepareStatement(sql);
				resultSetList.add(ps.executeQuery());
			}
			

			HashMap resultMap = new HashMap();
			int rsCounter = 0;	// result set counter happens also to represent the column

			
			// Processes the vertical result sets
			// in order to produce a complete row of data for a 
			// single vertical dimensional value
			for(ResultSet rs:resultSetList) {
				// one result set at a time.
				while(rs.next()) {
					// column 1 holds the dimensional value
					String dimensionValue = rs.getString(1);
					Double[] valueArray = null;
					
					if (!resultMap.containsKey(dimensionValue)) {
						/* If it's not there, create a new value Array, add one for the sum column */
						valueArray = new Double[resultSetList.size() + 1];
					} else {
						/* If this dimension is already added to the map, get the value array */
						valueArray = (Double[])resultMap.get(dimensionValue);
					}
					/* Put the current value in the correct position in the value Array */
					valueArray[rsCounter] = rs.getDouble(2);
					
					/* Put the value array back in  the map*/
					resultMap.put(dimensionValue, valueArray);
				}
				rsCounter++;
			}
			
			/*
			 * Now that we have collected all the data, we calculate the sum of all columns for each row
			 * This sum is then put in the last column of each row.
			 */
			boolean sumFunction=queryObject.getFact().getFactName().contains("sum(");
			boolean avgFunction =queryObject.getFact().getFactName().contains("avg(");				
			Iterator keyIterator = resultMap.keySet().iterator();
			while(keyIterator.hasNext()) {
				String key = (String)keyIterator.next();
				Double[] rowValues = (Double[])resultMap.get(key);
				double value = 0;
				for (int i=0;i < rowValues.length-1;i++) {
					if (rowValues[i] != null) {
							value+= rowValues[i];
					}
					
					
				}
				/* Apply the correct function on the sum column */
				if (sumFunction) {
					rowValues[rowValues.length-1] = value;
				} else if (avgFunction) {
					rowValues[rowValues.length-1] = value / rowValues.length-1;
				}
				
				resultMap.put(key, rowValues);
			}
			
			queryResult = new UsemonQueryResult();
			queryResult.setResultMap(resultMap);
			queryResult.setColumnNames(columnNames);
			queryResult.setQueryObject(queryObject);
			
			
		} catch (Exception e) {
			throw new IllegalStateException("Unable to execute SQL: " + sql + ";reason: " + e, e);
		} finally {
			DbmsHelper.close(ps);
			DbmsHelper.close(horizontalResultSet);
			for(ResultSet rs:resultSetList) {
				DbmsHelper.close(rs);
			}
			DbmsHelper.close(con);
		}
		
		return queryResult;

	}

	@Inject
	public void setDatasource(DataSource dataSource) {
		this.dataSource = dataSource;

	}

}
