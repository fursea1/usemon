/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel that provides functionality to set order by clauses for the query.
 * The instantiation determines on which axis the order is applied to (vertical / horizontal)
 * @author t514257
 *
 */
public class OrderByPanel extends WizardPanel {

	RadioButton alphabeticalButton;
	RadioButton factDescendingButton;
	RadioButton factAscendingButton;
	RadioButton noValue;
	
	/**
	 * Constructor
	 * @param panelTitle
	 * @param optional
	 */
	public OrderByPanel(String panelTitle, boolean optional) {
		super(panelTitle, optional);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#initPanel()
	 */
	protected void initPanel() {
		alphabeticalButton = new RadioButton("orderByGroup", "Natural order");
		factAscendingButton = new RadioButton("orderByGroup", "Fact value Ascending");
		factDescendingButton = new RadioButton("orderByGroup", "Fact value Descending");
		noValue = new RadioButton("orderByGroup");
		noValue.setVisible(false);
		
		// set default value
		factDescendingButton.setChecked(true);

		this.add(factDescendingButton);
		this.add(factAscendingButton);
		this.add(alphabeticalButton);
		this.add(noValue);
	}

	/* (non-Javadoc)
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#updateData(com.google.gwt.user.client.ui.Widget)
	 */
	protected void updateData(Widget widget) {
		// Nothing to do here

	}

	/* (non-Javadoc)
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#validate()
	 */
	public boolean validate() {
		return !noValue.isChecked();
	}

	/**
	 * 
	 * @return true if sorting should be alphabetical
	 */
	public boolean isAlphabeticalOrder() {
		return alphabeticalButton.isChecked();
	}
	
	/**
	 * 
	 * @return if sorting should be on the chosen fact (set in FactPanel)
	 */
	public boolean isFactOrder() {
		return factAscendingButton.isChecked() || factDescendingButton.isChecked();
	}
	
	/**
	 * 
	 * @return true if descending order should be applied
	 */
	public boolean isDescending() {
		return factDescendingButton.isChecked();
	}
}
