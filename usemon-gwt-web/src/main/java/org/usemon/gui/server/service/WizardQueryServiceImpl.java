package org.usemon.gui.server.service;

import java.util.ArrayList;
import java.util.Calendar;

import org.usemon.domain.DimensionalQueryService;
import org.usemon.domain.UsemonQueryObject;
import org.usemon.domain.UsemonQueryResult;
import org.usemon.domain.UsemonServiceLocator;
import org.usemon.domain.query.ArithmeticComparator;
import org.usemon.domain.query.Filter;
import org.usemon.domain.query.QueryFact;
import org.usemon.gui.client.FilterGUI;
import org.usemon.gui.client.QueryFactGUI;
import org.usemon.gui.client.UsemonQueryObjectGUI;
import org.usemon.gui.client.UsemonQueryResultSetGUI;
import org.usemon.gui.client.service.WizardQueryService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Invokes the dimensional query service in usemon based upon the input from the client.
 * Furthermore it transforms the results into somehting which the client can understand.
 * 
 * @author t514257
 * 
 */
public class WizardQueryServiceImpl extends RemoteServiceServlet implements WizardQueryService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6621037217858721400L;

	/**
	 * Generates and perfom the queries given by the queryObject
	 * 
	 * @param objectGUI query parameters from user collected in the dimensional query wizard
	 */
	public UsemonQueryResultSetGUI performQuery(UsemonQueryObjectGUI objectGUI) {
		// Transforms from application model to domain model
		UsemonQueryObject domainQueryObject = mapGUIObjectToDomainObject(objectGUI);
		// locates the service
		DimensionalQueryService dataFetcher = UsemonServiceLocator.getDimensionalQueryService();
		// invokes the service
		UsemonQueryResult queryResult = dataFetcher.fetchData(domainQueryObject);

		// transforms the results from domain model back into application model
		UsemonQueryResultSetGUI guiResult = new UsemonQueryResultSetGUI();
		guiResult.setColumnNames(queryResult.getColumnNames());
		guiResult.setResultMap(queryResult.getResultMap());
		
		// Attaches the original query object, may come in handy when additional queries is required
		guiResult.setQueryObject(objectGUI);

		return guiResult;
	}

	/**
	 * Maps the object from the GUI domain to the server side domain equivalent.
	 * 
	 * @param objectGUI
	 * @return the mapped object
	 */
	private UsemonQueryObject mapGUIObjectToDomainObject(UsemonQueryObjectGUI objectGUI) {
		UsemonQueryObject domainObject = new UsemonQueryObject();

		ArrayList<FilterGUI> primaryFilterGUI = objectGUI.getVerticalFilterList();
		for (FilterGUI guiFilter : primaryFilterGUI) {
			domainObject.addVerticalFilter(mapGUIFilterToDomainFilter(guiFilter));
		}

		ArrayList<FilterGUI> dim2FilterGUI = objectGUI.getHorizontalFilterList();
		for (FilterGUI guiFilter : dim2FilterGUI) {
			domainObject.addHorizontalFilter(mapGUIFilterToDomainFilter(guiFilter));

		}

		domainObject.setHorizontalDimension(objectGUI.getHorizontalDimension());

		domainObject.setHorizontalLimit(objectGUI.getHorizontalLimit());
		domainObject.setObservationType(objectGUI.getObservationType());
		domainObject.setVerticalDimensionList(objectGUI.getVerticalDimensionList());
		domainObject.setFact(mapGuiFactToDomainFact(objectGUI.getFactGUI()));
		domainObject.setVerticalLimit(objectGUI.getVerticalLimit());

		domainObject.setOrderHorizontalAlpha(objectGUI.isOrderHorizontalAlpha());
		domainObject.setOrderHorizontalDesc(objectGUI.isOrderHorizontalDesc());
		domainObject.setOrderHorizontalFact(objectGUI.isOrderHorizontalFact());

		domainObject.setOrderVerticalAlpha(objectGUI.isOrderVerticalAlpha());
		domainObject.setOrderVerticalDesc(objectGUI.isOrderVerticalDesc());
		domainObject.setOrderVerticalFact(objectGUI.isOrderVerticalFact());

		if (objectGUI.getFromDate() != null && objectGUI.getToDate() != null) {
			domainObject.setFromDate(objectGUI.getFromDate());
			domainObject.setToDate(objectGUI.getToDate());
		} else {
			Calendar toCal = Calendar.getInstance();
			toCal.add(Calendar.DAY_OF_MONTH, 1);
			domainObject.setToDate(toCal.getTime());
			Calendar fromCal = Calendar.getInstance();
			fromCal.setTimeInMillis(fromCal.getTimeInMillis() - objectGUI.getLastMillis());
			domainObject.setFromDate(fromCal.getTime());
		}

		return domainObject;
	}

	private Filter mapGUIFilterToDomainFilter(FilterGUI guiFilter) {
		ArithmeticComparator comparator = ArithmeticComparator.getByValue(guiFilter.getComparator());
		String tableColumn = guiFilter.getTableColumn();
		String value = guiFilter.getValue();

		Filter filter = new Filter(tableColumn, value, comparator);
		return filter;
	}

	private QueryFact mapGuiFactToDomainFact(QueryFactGUI guiFact) {
		if (guiFact == null)
			return null;

		QueryFact fact = new QueryFact(guiFact.getFactName(), guiFact.isHighestValue());
		return fact;
	}

}
