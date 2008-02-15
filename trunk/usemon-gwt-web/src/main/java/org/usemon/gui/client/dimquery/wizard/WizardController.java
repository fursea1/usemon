/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

/**
 * This interface contains the backbone of a Wizardcontroller 
 * @author t514257
 *
 */
public interface WizardController {

	/**
	 * Collect data and populate the query data object
	 * 
	 * @return true if everything went OK, false otherwise
	 * 
	 */
	public abstract boolean finishWizard();

	/**
	 * Initialize the wizard
	 * @return
	 */
	public abstract boolean startWizard();

	/**
	 * Go one screen forward
	 * 
	 */
	public abstract void goForward();

	/**
	 * Go one screen back
	 * 
	 */
	public abstract void goBack();

	/**
	 * Check if there is a screen after the currentWizardStepPanel
	 * Used to enable disable the navigation buttons of the
	 * wizard.
	 * @return true if a screen exist, false otherwise
	 */
	public abstract boolean hasNext();

	/**
	 * Check if there is a screen after the currentWizardStepPanel and if this screen is optional
	 * Used to enable disable the navigation buttons of the
	 * wizard. 
	 * @return true if optional screen exist, false otherwise
	 */
	public abstract boolean hasNextOptional();

	/**
	 * Check if there is a screen before the currentWizardStepPanel.
	 * Used to enable disable the navigation buttons of the
	 * wizard. 
	 * @return true if previous screen exists, false otherwise
	 */
	public abstract boolean hasPrevious();

	/**
	 * Get the total number of screens for this wizard
	 * @return
	 */
	public abstract int getNumberOfScreens();

	/**
	 * Clean up and terminate
	 */
	public abstract void cancelWizard();

}