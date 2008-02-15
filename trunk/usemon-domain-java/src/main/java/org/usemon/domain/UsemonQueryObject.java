package org.usemon.domain;

import java.util.ArrayList;
import java.util.Date;

import org.usemon.domain.query.Filter;
import org.usemon.domain.query.QueryFact;

public class UsemonQueryObject {

	/* The table to select from */
	private String observationType;
	/* A list containing the vertical dimensions, the ones used in the primary query (Dimension1) */
	private ArrayList<String> verticalDimensionList = new ArrayList<String>();
	/* Filter for the vertical query */
	private ArrayList<Filter> verticalFilterList = new ArrayList<Filter>();
	/* Limit for the vertical query */
	private int verticalLimit = 0;

	/* one fact only, holds asc desc info */
	private QueryFact fact;

	/* Dimension for the initial horizontal query who's result is used as parameters for the vertical query */
	private String horizontalDimension;
	/* Filters for the horizontal query (initial) who's result is used as parameters for the vertical query */
	private ArrayList<Filter> horizontalFilterList = new ArrayList<Filter>();
	/* Limit for the horizontal query */
	private int horizontalLimit = 0;

	private Date fromDate;
	private Date toDate;

	
	private boolean orderVerticalAlpha = false;
	private boolean orderVerticalFact = false;
	private boolean orderVerticalDesc = false;
	
	private boolean orderHorizontalAlpha = false;
	private boolean orderHorizontalFact = false;
	private boolean orderHorizontalDesc = false;
	
	
	public UsemonQueryObject() {

	}


	public void addHorizontalFilter(Filter filter) {
		horizontalFilterList.add(filter);
	}

	public void addVerticalFilter(Filter filter) {
		verticalFilterList.add(filter);
	}

	public void addVerticalDimension(String id) {
		verticalDimensionList.add(id);
	}


	public String getObservationType() {
		return observationType;
	}

	public void setObservationType(String observationType) {
		this.observationType = observationType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public ArrayList<String> getVerticalDimensionList() {
		return verticalDimensionList;
	}

	public boolean horizontalDimensionHasFilter() {
		return (getHorizontalFilterList() != null && getHorizontalFilterList().size() > 0);
	}

	public int getVerticalLimit() {
		return verticalLimit;
	}

	public void setVerticalLimit(int verticalLimit) {
		this.verticalLimit = verticalLimit;
	}

	public int getHorizontalLimit() {
		return horizontalLimit;
	}

	public void setHorizontalLimit(int horizontalLimit) {
		this.horizontalLimit = horizontalLimit;
	}

	public String getHorizontalDimension() {
		return horizontalDimension;
	}

	public void setHorizontalDimension(String dimension) {
		this.horizontalDimension = dimension;
	}

	public QueryFact getFact() {
		return fact;
	}

	public void setFact(QueryFact primaryFact) {
		this.fact = primaryFact;
	}


	public ArrayList<Filter> getVerticalFilterList() {
		return verticalFilterList;
	}

	public ArrayList<Filter> getHorizontalFilterList() {
		return horizontalFilterList;
	}

	public void setVerticalDimensionList(ArrayList<String> verticalDimensionList) {
		this.verticalDimensionList = verticalDimensionList;
	}


	public boolean isOrderVerticalAlpha() {
		return orderVerticalAlpha;
	}


	public void setOrderVerticalAlpha(boolean orderVerticalAlpha) {
		this.orderVerticalAlpha = orderVerticalAlpha;
	}


	public boolean isOrderVerticalFact() {
		return orderVerticalFact;
	}


	public void setOrderVerticalFact(boolean orderPrimaryFact) {
		this.orderVerticalFact = orderPrimaryFact;
	}


	public boolean isOrderVerticalDesc() {
		return orderVerticalDesc;
	}


	public void setOrderVerticalDesc(boolean orderVerticalDesc) {
		this.orderVerticalDesc = orderVerticalDesc;
	}


	public boolean isOrderHorizontalAlpha() {
		return orderHorizontalAlpha;
	}


	public void setOrderHorizontalAlpha(boolean orderHorizontalAlpha) {
		this.orderHorizontalAlpha = orderHorizontalAlpha;
	}


	public boolean isOrderHorizontalFact() {
		return orderHorizontalFact;
	}


	public void setOrderHorizontalFact(boolean orderHorizontalFact) {
		this.orderHorizontalFact = orderHorizontalFact;
	}


	public boolean isOrderHorizontalDesc() {
		return orderHorizontalDesc;
	}


	public void setOrderHorizontalDesc(boolean orderHorizontalDesc) {
		this.orderHorizontalDesc = orderHorizontalDesc;
	}

}
