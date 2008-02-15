package org.usemon.gui.client.dimquery.wizard;

import java.util.ArrayList;

import org.usemon.gui.client.UsemonQueryObjectGUI;
import org.usemon.gui.client.UsemonQueryResultSetGUI;
import org.usemon.gui.client.service.WizardQueryService;
import org.usemon.gui.client.service.WizardQueryServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Main controller for the dimensional query wizard. This controller is responsible for coordinating the dependencies between all
 * the wizard screen It is also responsible for collecting data from the screens and populate the query object before calling the
 * date retrieval service.
 * 
 * @author t514257
 * 
 */
public class DimensionalWizardController implements WizardController {

	WizardCallback dimensionalQueryResultCallBackHandler;

	WizardView wizardView;
	PeriodPanel periodPanel;
	DimensionPanel primaryDimensionPanel;
	DimensionPanel dimension2Panel;
	FactPanel primaryFactPanel;
	FactPanel dimension2FactPanel;
	FilterPanel primaryFilterPanel;
	FilterPanel dimension2FilterPanel;

	DimensionPanel horizontalAxisPanel;
	DimensionPanel verticalAxisPanel;
	RowCountPanel rowCountPanel;
	FactPanel factPanel;
	OrderByPanel columnOrderByPanel;
	OrderByPanel rowOrderByPanel;
	FilterPanel columnFilterPanel;
	FilterPanel rowFilterPanel;

	// Holds a panel for each step in the wizard
	ArrayList wizardContentPanels = new ArrayList();
	UsemonQueryObjectGUI dataObject;

	int numberOfScreens = 0;
	int currentScreen = 0;

	/**
	 * Default constructor, calls initialize method
	 * 
	 */
	public DimensionalWizardController(WizardCallback dimensionalQueryResultCallBackHandler) {
		this.dimensionalQueryResultCallBackHandler = dimensionalQueryResultCallBackHandler;
		initController();
	}

	/**
	 * Initializes and instantiate essential objects wizard
	 */
	private void initController() {
		// Holds the data entered during the course of the wizardry
		dataObject = new UsemonQueryObjectGUI();
		// Builds the panels we are going to use
		buildWizardPanelsList();

		wizardView = new WizardView(this);
	}

	/**
	 * Entry point for starting the wizard, assuming that proper set up and configuration has been performed.
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#startWizard()
	 */
	public boolean startWizard() {
		wizardView.add(getScreen(currentScreen));
		wizardView.center(); // Displays the dialogue box and centers it.
		return true;
	}

	/**
	 * This method sets up all the screens used by this wizard The order of adding the screens is essential with regards to the
	 * order of which they are displayed by the wizard.
	 */
	private void buildWizardPanelsList() {

		periodPanel = new PeriodPanel("Choose period", false);
		wizardContentPanels.add(periodPanel);

		horizontalAxisPanel = new DimensionPanel("Horizontal axis", false);
		horizontalAxisPanel.setPanelSecondaryTitle("Choose the horizontal axis (columns)");
		horizontalAxisPanel.setHelperImageURL("wizard/select_horizontal_columns.jpg");
		wizardContentPanels.add(horizontalAxisPanel);

		verticalAxisPanel = new DimensionPanel("Vertical axis", false);
		verticalAxisPanel.setPanelSecondaryTitle("Choose the Vertical axis (rows)");
		verticalAxisPanel.setHelperImageURL("wizard/select_vertical_rows.jpg");
		wizardContentPanels.add(verticalAxisPanel);

		rowCountPanel = new RowCountPanel("Row Count", false);
		rowCountPanel.setPanelSecondaryTitle("Set max number of: ");
		rowCountPanel.setHelperImageURL("wizard/row_column_count.jpg");
		wizardContentPanels.add(rowCountPanel);

		factPanel = new FactPanel("Fact selection", false);
		factPanel.setPanelSecondaryTitle("Pick fact (Cell value): ");
		factPanel.setHelperImageURL("wizard/select_fact.jpg");
		wizardContentPanels.add(factPanel);

		columnOrderByPanel = new OrderByPanel("Set Column sort order", false);
		columnOrderByPanel.setPanelSecondaryTitle("Order columns by: ");
		columnOrderByPanel.setHelperImageURL("wizard/order_horizontal_column.jpg");
		wizardContentPanels.add(columnOrderByPanel);

		rowOrderByPanel = new OrderByPanel("Set Row sort order", false);
		rowOrderByPanel.setPanelSecondaryTitle("Order rows by: ");
		rowOrderByPanel.setHelperImageURL("wizard/order_vertical_rows.jpg");
		wizardContentPanels.add(rowOrderByPanel);

		columnFilterPanel = new FilterPanel("Column Filters", true);
		columnFilterPanel.setPanelSecondaryTitle("Filter for the columns");
		columnFilterPanel.setHelperImageURL("wizard/select_horizontal_columns.jpg");
		wizardContentPanels.add(columnFilterPanel);

		rowFilterPanel = new FilterPanel("Row Filters", true);
		rowFilterPanel.setPanelSecondaryTitle("Filter for the rows: ");
		rowFilterPanel.setHelperImageURL("wizard/select_vertical_rows.jpg");
		wizardContentPanels.add(rowFilterPanel);

	}

	/**
	 * Invoked when the user decides that the entry of data has been completed.
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#finishWizard()
	 */
	public boolean finishWizard() {
		// This wizard only works on Method Measurements
		dataObject.setObservationType("method_measurement_fact");

		dataObject.setFromDate(periodPanel.getPeriodFromDate());
		dataObject.setToDate(periodPanel.getPeriodToDate());

		dataObject.setLastMillis(periodPanel.getNumberOfMilliSeconds());

		dataObject.setHorizontalDimension((String) horizontalAxisPanel.getSelectedDimensions().get(0));

		dataObject.setVerticalDimensionList(verticalAxisPanel.getSelectedDimensions());
		dataObject.setVerticalLimit(rowCountPanel.getRowCount());
		dataObject.setHorizontalLimit(rowCountPanel.getColumnCount());

		dataObject.setFactGUI(factPanel.getQueryValue());

		dataObject.setOrderVerticalAlpha(rowOrderByPanel.isAlphabeticalOrder());
		dataObject.setOrderVerticalDesc(rowOrderByPanel.isDescending());
		dataObject.setOrderVerticalFact(rowOrderByPanel.isFactOrder());

		dataObject.setOrderHorizontalAlpha(columnOrderByPanel.isAlphabeticalOrder());
		dataObject.setOrderHorizontalDesc(columnOrderByPanel.isDescending());
		dataObject.setOrderHorizontalFact(columnOrderByPanel.isFactOrder());

		dataObject.setHorizontalFilterList(columnFilterPanel.getFilterList());

		dataObject.setVerticalFilterList(rowFilterPanel.getFilterList());

		performQuery();
		return true;
	}

	/**
	 * Used to navigate the wizards panels
	 * 
	 * @param index -
	 *            the index of the screen we want
	 * @return the object representing this screen.
	 */
	private WizardPanel getScreen(int index) {
		return (WizardPanel) wizardContentPanels.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#goForward()
	 */
	public void goForward() {
		wizardView.add(getScreen(++currentScreen));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#goBack()
	 */
	public void goBack() {
		wizardView.add(getScreen(--currentScreen));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#hasNext()
	 */
	public boolean hasNext() {
		if (currentScreen == wizardContentPanels.size() - 1) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#hasNextOptional()
	 */
	public boolean hasNextOptional() {
		if (hasNext()) {
			return ((WizardPanel) wizardContentPanels.get(currentScreen + 1)).isOptional();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#hasPrevious()
	 */
	public boolean hasPrevious() {
		if (currentScreen == 0) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#getNumberOfScreens()
	 */
	public int getNumberOfScreens() {
		return wizardContentPanels.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardController#cancelWizard()
	 */
	public void cancelWizard() {
		// TODO should we do anything here?
	}

	/**
	 * Calls the service to fetch data as described by the Wizard.
	 */
	private void performQuery() {
		WizardQueryServiceAsync wizardQueryServiceAsync = (WizardQueryServiceAsync) GWT.create(WizardQueryService.class);
		AsyncCallback callback = new AsyncCallback() {

			/*
			 * If the call was successful we will get the result here
			 */
			public void onSuccess(Object s) {
				// Pass the query result to the wizard call back object.
				dimensionalQueryResultCallBackHandler.handleWizardResult((UsemonQueryResultSetGUI) s);
				GWT.log("returned from Query", null);
			}

			/*
			 * Something went wrong with the call. Handle the issue how you'd like.
			 */
			public void onFailure(Throwable ex) {
				Window.alert("Dimensional query failed: " + ex);
			}
		};

		ServiceDefTarget endPoint = (ServiceDefTarget) wizardQueryServiceAsync;
		// TODO: Review the end point URL
		endPoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "/wizardQuery");

		wizardQueryServiceAsync.performQuery(dataObject, callback);
	}
}
