/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import org.usemon.gui.client.UsemonQueryResultSetGUI;

/**
 * This interface is used to handle the dbms result set produced by the dimensional query wizard query
 * 
 * @author t514257
 * 
 */
public interface WizardCallback {

	/** Handles the call backs 
	 * @param result the dbms result set
	 */
	public void handleWizardResult(UsemonQueryResultSetGUI result);

}
