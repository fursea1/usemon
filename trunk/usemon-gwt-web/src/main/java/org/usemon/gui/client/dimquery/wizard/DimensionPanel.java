package org.usemon.gui.client.dimquery.wizard;

import java.util.ArrayList;
import java.util.Iterator;


import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * This panel offer the user a possibility to select a dimension in the query,
 * The instantiation determines the axis direction.
 * @author t514257
 * 
 */
public class DimensionPanel extends WizardPanel {

	ArrayList radioButtonList;
	boolean selectMultiple = true;

	private final static String RADIO_DIMENSION_GROUP = "RADIO_DIMENSION_GROUP";

	protected RadioButton locationRadioButton;
	protected RadioButton packageRadioButton;
	protected RadioButton classRadioButton;
	protected RadioButton methodRadioButton;
	protected RadioButton principalRadiobutton;
	protected RadioButton dayOfWeekRadioButton;
	protected RadioButton dateRadioButton;
	protected RadioButton timeRadioButton;
	protected RadioButton hourOfDayRadioButton;

	private boolean initialized = false;
	/* A list of the dimensions that are selected */
	ArrayList selectedDimensions = new ArrayList();
	
	DimensionDBConstants dbConstants;

	/**
	 * @param controller
	 */
	DimensionPanel(String panelTitle, boolean optional) {
		super(panelTitle, optional);
	}

	protected void initPanel() {
		if (!initialized) {
			dbConstants = DimensionDBConstants.createInstance();

			radioButtonList = generateDimensionRadioButtonList();

			this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			Grid grid  =  new Grid((int)Math.ceil((double)radioButtonList.size()/2), 2);
			Iterator i = radioButtonList.iterator();
			int row = 0;
			int column = 0;
			while (i.hasNext()) {
				RadioButton radioButton = (RadioButton) i.next();
				grid.setWidget(row, column, radioButton);
				row = row+column;
				column = column==1?0:1;
			}
			this.add(grid);
			initialized = true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#updateData()
	 */
	protected void updateData(Widget widget) {
		if (widget instanceof RadioButton) {
			RadioButton updatedRadioButton = (RadioButton) widget;
			selectedDimensions.clear();
			/*
			 * This is a hack to describe the hierarchical dependencies
			 * between package.class.method
			 */
			if (widget == classRadioButton) {
				selectedDimensions.add(DimensionDBConstants.PACKAGE);
			}
			if (widget == methodRadioButton) {
				selectedDimensions.add(DimensionDBConstants.PACKAGE);
				selectedDimensions.add(DimensionDBConstants.CLASS);
			}
			selectedDimensions.add(dbConstants.getTableNameForDescription(updatedRadioButton.getText()));
		} 


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#validate()
	 */
	public boolean validate() {
		boolean validationOk = false;
		Iterator i = radioButtonList.iterator();
		while (i.hasNext()) {
			CheckBox checkBox = (CheckBox) i.next();
			validationOk = validationOk || checkBox.isChecked();
		}
		return validationOk;
	}

	/**
	 * Creates radiobuttons for the different dimensions
	 * @return
	 */
	private ArrayList generateDimensionRadioButtonList() {
		ArrayList radioButtonList = new ArrayList();

		locationRadioButton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.LOCATION_DESC);
		radioButtonList.add(locationRadioButton);

		packageRadioButton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.PACKAGE_DESC);
		radioButtonList.add(packageRadioButton);

		classRadioButton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.CLASS_DESC);
		radioButtonList.add(classRadioButton);

		methodRadioButton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.METHOD_DESC);
		radioButtonList.add(methodRadioButton);

		principalRadiobutton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.PRINCIPAL_DESC);
		radioButtonList.add(principalRadiobutton);

		dayOfWeekRadioButton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.DAY_OF_WEEK_DESC);
		radioButtonList.add(dayOfWeekRadioButton);
		
		hourOfDayRadioButton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.HOUR_OF_DAY_DESC);
		radioButtonList.add(hourOfDayRadioButton);
		
		dateRadioButton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.DATE_DESC);
		radioButtonList.add(dateRadioButton);
		
		timeRadioButton = new RadioButton(RADIO_DIMENSION_GROUP, DimensionDBConstants.TIME_DESC);
		radioButtonList.add(timeRadioButton);
		

		return radioButtonList;
	}

	public ArrayList getSelectedDimensions() {
		return selectedDimensions;
	}

}
