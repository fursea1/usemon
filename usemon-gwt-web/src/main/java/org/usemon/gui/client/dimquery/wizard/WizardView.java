package org.usemon.gui.client.dimquery.wizard;

import java.util.ArrayList;

import org.gwtwidgets.client.ui.ProgressBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * This is the main view for the the dimensional query wizard.
 * It provides a navigation panel, progress bar as well as a place holder for
 * the wizardContentpanels (aka wizard screens).
 * @author t514257
 *
 */
public class WizardView extends DialogBox implements ClickListener {

	WizardController controller;
	ArrayList panelList = new ArrayList();
	WizardPanel currentWizardStepPanel;
	VerticalPanel rootPanel = new VerticalPanel();;
	VerticalPanel wizardContentPanel = new VerticalPanel();
	HorizontalPanel navigationPanel = new HorizontalPanel();
	ProgressBar progressBar;

	Button previousButton = new Button("", this);
	Button nextButton = new Button("", this);
	Button finishButton = new Button("", this);
	Button cancelButton = new Button("", this);

	boolean initialized = false;
	int numberOfScreens = 0;

	/**
	 * Constructor
	 * @param controller The main wizard controller
	 */
	public WizardView(WizardController controller) {
		super();
		this.controller = controller;
		numberOfScreens = controller.getNumberOfScreens();
		initView();
	}

	/**
	 * Initialize and build the various parts of the wizard view
	 */
	private void initView() {
		if (!initialized) {

			setText("The Wizard of *Usemon*");
			
			setWidget(rootPanel);

			previousButton.setText("<< Back");
			nextButton.setText("Next >>");
			finishButton.setText("Finish");
			cancelButton.setText("Cancel");


			if (numberOfScreens <= 1) {
				nextButton.setEnabled(false);
			}

			navigationPanel.add(previousButton);
			navigationPanel.add(nextButton);
			navigationPanel.add(finishButton);
			navigationPanel.add(cancelButton);

			navigationPanel.setSpacing(5);
			navigationPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);

			// The graphical progress bar
			rootPanel.add(generateProgressPanel());
			// container for the currentWizardStepPanel active screen for currentWizardStepPanel step of the wizard
			rootPanel.add(wizardContentPanel);
			
			rootPanel.setCellHeight(wizardContentPanel, "300px");
			rootPanel.setCellWidth(wizardContentPanel, "600px");
			
			rootPanel.add(navigationPanel);
			rootPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
			initialized = true;
		}

	}

	/**
	 * Evaluate and enable / disable navigation buttons
	 */
	protected void enableDisableButtons() {
		if (currentWizardStepPanel == null) {
			finishButton.setEnabled(false);
			nextButton.setEnabled(false);
			previousButton.setEnabled(false);
		} else {
			// call back into the panel for validation
			boolean validationResult = currentWizardStepPanel.validate();
			// enables the next button if validation passed and there is a consecutive panel
			nextButton.setEnabled(validationResult && controller.hasNext());
			// enables the previous button if there is a preceeding panel
			previousButton.setEnabled(controller.hasPrevious());
			
			finishButton.setEnabled(validationResult && 
					(!controller.hasNext() || controller.hasNextOptional()));
		}
	}
	
	/**
	 * Initialize and lay out a progress bar for the wizard
	 * @return
	 */
	private Panel generateProgressPanel() {
		HorizontalPanel panel  = new HorizontalPanel();
		progressBar = new ProgressBar(numberOfScreens, ProgressBar.SHOW_TEXT);
		
		progressBar.addStyleName("progressbar-solid");
		updateProgressBar(Math.ceil((100/(double)numberOfScreens)));
		return progressBar;
	}
	
	/**
	 * Update completion progress in for the wizard
	 * @param progress incremental percent progress value
	 */
	private void updateProgressBar(double progress) {
		progressBar.setProgress(progressBar.getProgress() + (int)progress);
		int currentPage = (int)((double)progressBar.getProgress() / Math.round((100/(double)numberOfScreens)));
		progressBar.setText("Side " + currentPage + " av " + numberOfScreens);
	}

	/**
	 * Replaces the currentWizardStepPanel screen with the new one
	 * Add it to the internal panelList
	 * @param w the new screen to add and show
	 */
	public void add(WizardPanel w) {
		remove(currentWizardStepPanel);
		wizardContentPanel.add(w);
		panelList.add(w);
		currentWizardStepPanel = w;
		setText(w.getPanelTitle());
		enableDisableButtons();
	}

	/**
	 * Removes the widget from the internal panelList
	 * @param w the widget to remove
	 */
	public boolean remove(Widget w) {
		if (panelList.contains(w)) {
			panelList.remove(w);
			return wizardContentPanel.remove(w);
		}
		return false;
	}

	/**
	 * Returns the "panel" currently on display
	 */
	public Widget getCurrent() {
		return currentWizardStepPanel;
	}

	/**
	 * Handle click events for the navigation panel
	 */
	public void onClick(Widget w) {
		if (w == previousButton) {
			controller.goBack();
			updateProgressBar(-Math.floor((100/(double)numberOfScreens)));
			GWT.log("Go back " + w.toString(), null);
		} else if (w == nextButton) {
			controller.goForward();
			updateProgressBar(Math.ceil((100/(double)numberOfScreens)));
			GWT.log("Go forward " + w.toString(), null);
		} else if (w == finishButton) {
			if (controller.finishWizard()) {
				this.hide();	// TODO: how to dismiss a modal dialogue box
			}
		} else if (w == cancelButton) {
			controller.cancelWizard();
			this.hide();
		}
		enableDisableButtons();
	}

}
