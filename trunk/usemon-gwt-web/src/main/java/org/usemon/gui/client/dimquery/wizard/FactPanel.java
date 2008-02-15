/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import java.util.ArrayList;
import java.util.Iterator;

import org.usemon.gui.client.QueryFactGUI;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * This panel offers the user to select the Fact on which this query is based upon.
 * @author t514257
 * 
 */
public class FactPanel extends WizardPanel {

	RadioButton avgRespTime;
	RadioButton invCount;
	RadioButton checkedExceptions;
	RadioButton uncheckedExceptions;
	RadioButton maxRespTimer;
	RadioButton noValue;

	ArrayList radioButtons;
	private final static String FACT_BUTTON_GROUP = "fact";
	private final static String HIGH_LOW_BUTTON_GROUP = "highLow";

	private FactDBConstants dbConstants;

	private QueryFactGUI queryFact;

	public QueryFactGUI getQueryValue() {
		return queryFact;
	}

	/**
	 * Cosntructor 
	 * @param optional
	 */
	public FactPanel(String panelTitle, boolean optional) {
		super(panelTitle, optional);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#initPanel()
	 */
	protected void initPanel() {
		dbConstants = FactDBConstants.createInstance();
		queryFact = new QueryFactGUI();


		radioButtons = generateRadioButtonFactList();
		Iterator i = radioButtons.iterator();
		while (i.hasNext()) {
			this.add(((RadioButton) i.next()));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#updateData(com.google.gwt.user.client.ui.Widget)
	 */
	protected void updateData(Widget widget) {
		if (widget instanceof RadioButton) {
			RadioButton updatedButton = (RadioButton) widget;
			if (updatedButton.isChecked()) {

				queryFact.setFactName(dbConstants.getTableNameForDescription(updatedButton.getText()));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#validate()
	 */
	public boolean validate() {
		return !noValue.isChecked();
	}

	private ArrayList generateRadioButtonFactList() {
		ArrayList buttons = new ArrayList();
		avgRespTime = new RadioButton(FACT_BUTTON_GROUP, FactDBConstants.AVG_RESP_TIME_DESC);
		invCount = new RadioButton(FACT_BUTTON_GROUP, FactDBConstants.INV_COUNT_DESC);
		checkedExceptions = new RadioButton(FACT_BUTTON_GROUP, FactDBConstants.CHECKED_EXCEPTIONS_DESC);
		uncheckedExceptions = new RadioButton(FACT_BUTTON_GROUP, FactDBConstants.UNCHECKED_EXCEPTIONS_DESC);
		maxRespTimer = new RadioButton(FACT_BUTTON_GROUP, FactDBConstants.MAX_RESP_TIME_DESC);

		noValue = new RadioButton(FACT_BUTTON_GROUP);
		noValue.setVisible(false);
		noValue.setChecked(true);

		buttons.add(avgRespTime);
		buttons.add(invCount);
		buttons.add(checkedExceptions);
		buttons.add(uncheckedExceptions);
		buttons.add(maxRespTimer);
		buttons.add(noValue);

		return buttons;
	}

}
