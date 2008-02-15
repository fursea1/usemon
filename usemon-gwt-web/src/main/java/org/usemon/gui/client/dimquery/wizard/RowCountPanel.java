/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import org.usemon.gui.client.utils.WidgetUtils;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel that provides functionality for setting the maximum number of rows and columns that
 * should be presented to the user.
 * @author t514257
 *
 */
public class RowCountPanel extends WizardPanel {
	
	TextBox columnCountTextBox;
	TextBox rowCountTextBox;
	
	int columnCount;
	int rowCount;

	/**
	 * Constructor
	 * @param panelTitle
	 * @param optional
	 */
	RowCountPanel(String panelTitle, boolean optional) {
		super(panelTitle, optional);
	}

	/* (non-Javadoc)
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#initPanel()
	 */
	protected void initPanel() {
		columnCount = 3;
		rowCount = 10;
		VerticalPanel labelPanel = new VerticalPanel();
		labelPanel.setSpacing(4);
		VerticalPanel textBoxPanel = new VerticalPanel();
		Label columnLabel = new Label("Columns: ");
		columnCountTextBox = WidgetUtils.getNumericTextbox();
		columnCountTextBox.setText(String.valueOf(columnCount));
		
		labelPanel.add(columnLabel);
		textBoxPanel.add(columnCountTextBox);
		
		HorizontalPanel horizontalPanel  = new HorizontalPanel();
		Label rowLabel = new Label("Rows: ");
		rowCountTextBox = WidgetUtils.getNumericTextbox();
		rowCountTextBox.setText(String.valueOf(rowCount));
		labelPanel.add(rowLabel);
		textBoxPanel.add(rowCountTextBox);
		horizontalPanel.add(labelPanel);
		horizontalPanel.add(textBoxPanel);
		
		this.add(horizontalPanel);
		setPanelSecondaryTitle("Choose max number of: ");
		
		

	}

	/* (non-Javadoc)
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#updateData(com.google.gwt.user.client.ui.Widget)
	 */

	protected void updateData(Widget widget) {
		if (widget instanceof TextBox) {
			TextBox box = (TextBox) widget;
			if (box == columnCountTextBox) {
				columnCount = Integer.parseInt(box.getText());
			} else if (box == rowCountTextBox) {
				rowCount = Integer.parseInt(box.getText());
			}
		}


	}
	
	/**
	 * 
	 * @return maximum number of columns
	 */
	public int getColumnCount() {
		return columnCount;
	}
	
	/**
	 * 
	 * @return maximum number of rows
	 */
	public int getRowCount() {
		return rowCount;
	}

	/* (non-Javadoc)
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#validate()
	 */
	public boolean validate() {
		return (columnCount > 0 && rowCount > 0);
	}

}
