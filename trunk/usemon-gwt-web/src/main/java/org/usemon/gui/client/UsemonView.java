package org.usemon.gui.client;

import java.beans.PropertyChangeEvent;

import org.usemon.gui.client.controller.UsemonController;
import org.usemon.gui.client.dimquery.DimensionalQuery;
import org.usemon.gui.client.dimquery.wizard.DimensionalWizardController;
import org.usemon.gui.client.dimquery.wizard.WizardController;
import org.usemon.gui.client.relationship.RelationshipPanel;
import org.usemon.gui.client.statusgraph.StatusGraphView;
import org.usemon.gui.client.view.UiView;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Main view of the application.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 */
public class UsemonView extends Composite implements TabListener, UiView {

	TabPanel menuPanel = new TabPanel();
	DimensionalQuery dimensionalQuery;
	boolean browserFirstTimeSelected = true;
	private final UsemonController usemonController;
	private StatusGraphView statusGraphView;

	public UsemonView(UsemonController usemonController) {

		this.usemonController = usemonController;
		initWidget(menuPanel);

		// Use entire space of cell
		setWidth("100%");
		setHeight("100%");

		// Lower half of tabbed panel should use all space.
		menuPanel.getDeckPanel().setHeight("100%");

		statusGraphView = new StatusGraphView(usemonController);
		usemonController.addView(statusGraphView);
		menuPanel.add(statusGraphView, "Status"); // Tab #0

		dimensionalQuery = new DimensionalQuery();
		menuPanel.add(dimensionalQuery.getBrowsePanel(), "Dimensional Query");
		menuPanel.add(new RelationshipPanel(), "Relationship analysis");
		menuPanel.add(new HTML("<h1>Information on the Usemon application</h1>"), "Info");
		menuPanel.selectTab(0);
		menuPanel.addTabListener(this);
	}

	/**
	 * Invoked before a tab is selected
	 * 
	 * @see com.google.gwt.user.client.ui.TabListener#onBeforeTabSelected(com.google.gwt.user.client.ui.SourcesTabEvents, int)
	 */
	public boolean onBeforeTabSelected(SourcesTabEvents sourcesTabEvents, int tabIndex) {
		return true;
	}

	/**
	 * Invoked when user has selected a tab. This handler is not invoked when you programatically select a tab.
	 * I.e. this handler is invoked when the user has selected the tab using the mouse or the keyboard.
	 *  
	 * @see com.google.gwt.user.client.ui.TabListener#onTabSelected(com.google.gwt.user.client.ui.SourcesTabEvents, int)
	 */
	public void onTabSelected(SourcesTabEvents tabPanel, int tabIndex) {
		switch (tabIndex) {
		case 0:
			onStatusGraphVisible();
			break;
		case 1:
			// First time in the life of the application, we automatically display the wizard.
			if (browserFirstTimeSelected) {
				WizardController wizard = new DimensionalWizardController(dimensionalQuery);
				wizard.startWizard();
				browserFirstTimeSelected = false;
			}
			break;
		}
	}

	/**
	 * Handles the fact that the Status graph has been made visible. Probably because the
	 * user clicked on the tab
	 */
	private void onStatusGraphVisible() {
		statusGraphView.loadGraph();
		// Invokes the remote service to check if more data is available
		// The callback handler will invoke this method upon completion.
		usemonController.refreshStatusGraph();
	}

	/** Indicates whether the tab containing the status graph is currently selected 
	 * and visible.
	 * @return <code>true</code> if the status graph is visible, <code>false</code> otherwise.
	 */
	public boolean isStatusGraphTabSelected() {
		int tabIndex = menuPanel.getTabBar().getSelectedTab();
		return menuPanel.getWidget(tabIndex) == statusGraphView;
	}

	/** Invoked by the main controller when the UI is ready */
	public void onUiReady() {
		// If the first tab (#0) is active with the status graph,
		// loads the data
		if (isStatusGraphTabSelected())
			onStatusGraphVisible();
	}

	/** Handles resizing of the window */
	public void onWindowResized(int width, int height) {
		statusGraphView.onWindowResized(width, height);
	}

	public void modelPropertyChange(PropertyChangeEvent evt) {

	}

	public void onMoreMethodObservationsAvailable() {
		if (isStatusGraphTabSelected())
			statusGraphView.loadGraph();
	}
}
