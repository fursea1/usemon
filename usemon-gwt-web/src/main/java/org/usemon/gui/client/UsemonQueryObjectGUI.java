package org.usemon.gui.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


/**
 *
 * @author t514257
 *
 */
public class UsemonQueryObjectGUI implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1233504703275348237L
	;
	/* The table to select from */
	private String observationType;
	
	/* A list containing the vertical dimensions, the ones used in the primary query */
	/**
	 * @gwt.typeArgs <java.lang.String>	
	 */
	private ArrayList verticalDimensionList = new ArrayList();
	/* FilterGUI for the vertical query */
	/**
	 * @gwt.typeArgs <org.usemon.gui.client.FilterGUI>	
	 */	
	private ArrayList verticalFilterList = new ArrayList();
	/* Limit for the vertical query */
	private int verticalLimit = 0;

	/* one fact only, holds asc desc info */
	private QueryFactGUI factGUI;

	/* The horizontal dimension */
	private String horizontalDimension;
	
	
	/* Filters for the horizontal (initial) query who's result is used as parameters for the primary query */
	/**
	 * @gwt.typeArgs <org.usemon.gui.client.FilterGUI>	
	 */		
	private ArrayList horizontalFilterList = new ArrayList();
	
	/* Limit for the initial query */
	private int horizontalLimit = 0;

	private Date fromDate;
	private Date toDate;
	private long lastMillis;
	
	
	private boolean orderVerticalAlpha = false;
	private boolean orderVerticalFact = false;
	private boolean orderVerticalDesc = false;
	
	private boolean orderHorizontalAlpha = false;
	private boolean orderHorizontalFact = false;
	private boolean orderHorizontalDesc = false;

	public UsemonQueryObjectGUI() {

	}


	public void addHorizontalFilterGUI(FilterGUI filterGUI) {
		horizontalFilterList.add(filterGUI);
	}

	public void addVerticalFilterGUI(FilterGUI filterGUI) {
		verticalFilterList.add(filterGUI);
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

	public ArrayList getVerticalDimensionList() {
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

	public void setHorizontalLimit(int dimension2Limit) {
		this.horizontalLimit = dimension2Limit;
	}

	public String getHorizontalDimension() {
		return horizontalDimension;
	}

	public void setHorizontalDimension(String dimension2) {
		this.horizontalDimension = dimension2;
	}

	public QueryFactGUI getFactGUI() {
		return factGUI;
	}

	public void setFactGUI(QueryFactGUI factGUI) {
		this.factGUI = factGUI;
	}

	public ArrayList getVerticalFilterList() {
		return verticalFilterList;
	}

	public ArrayList getHorizontalFilterList() {
		return horizontalFilterList;
	}

	public void setVerticalDimensionList(ArrayList verticalDimensionList) {
		this.verticalDimensionList = verticalDimensionList;
	}


	public void setVerticalFilterList(ArrayList verticalFilterList) {
		this.verticalFilterList = verticalFilterList;
	}


	public void setHorizontalFilterList(ArrayList dimension2FilterList) {
		this.horizontalFilterList = dimension2FilterList;
	}


	/**
	 * @param millis
	 */
	public void setLastMillis(long millis) {
		lastMillis = millis;
		
	}
	
	public long getLastMillis() {
		return lastMillis;
	}


	public boolean isOrderVerticalAlpha() {
		return orderVerticalAlpha;
	}


	public void setOrderVerticalAlpha(boolean orderPrimaryAlpha) {
		this.orderVerticalAlpha = orderPrimaryAlpha;
	}


	public boolean isOrderVerticalFact() {
		return orderVerticalFact;
	}


	public void setOrderVerticalFact(boolean orderVerticalFact) {
		this.orderVerticalFact = orderVerticalFact;
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
