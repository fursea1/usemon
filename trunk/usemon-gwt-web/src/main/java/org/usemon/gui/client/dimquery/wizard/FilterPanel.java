/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import java.util.ArrayList;
import java.util.Iterator;

import org.usemon.gui.client.FilterGUI;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel that provides functionality for adding filters.
 * The instantiation determines on which axis the filters are applied (Horizontal / Vertical) 
 * @author t514257
 * 
 */
public class FilterPanel extends WizardPanel implements TableListener {

	private static final String DIM_LIST_BOX_NAME = "DIM_LIST_BOX_NAME";
	private static final String COMP_LIST_BOX_NAME = "COMP_LIST_BOX_NAME";

	private ArrayList filterList;
	private Button deleteRowButton;
	private Button addRowButton;
	
	/* The grid where all the filter lines are placed*/
	Grid table;

	/**
	 * Constructor
	 * @param optional
	 */
	public FilterPanel(String panelTitle, boolean optional) {
		super(panelTitle, optional);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#initPanel()
	 */
	protected void initPanel() {
		addRowButton = new Button("Add row");
		deleteRowButton = new Button("Delete row");
		filterList = new ArrayList();
		table = new Grid(1, 1);
		table.addTableListener(this);
		table.setWidget(0, 0, generateCellPanel());
		this.add(table);
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(addRowButton);
		buttonPanel.add(deleteRowButton);
		this.add(buttonPanel);
		;

	}

	/**
	 * Create a panel representing one cell in the table
	 * @return
	 */
	private Panel generateCellPanel() {

		HorizontalPanel cellPanel = new HorizontalPanel();
		ListBox dimensionListBox = generatePopulatedDimensionListBox();
		dimensionListBox.setItemSelected(0, true);
		ListBox comparatorListBox = generatePopulatedComparatorListBox();
		comparatorListBox.setItemSelected(0, true);
		TextBox filterValueTextBox = new TextBox();

		cellPanel.add(dimensionListBox);
		cellPanel.add(comparatorListBox);
		cellPanel.add(filterValueTextBox);

		return cellPanel;
	}

	private ListBox generatePopulatedDimensionListBox() {
		ListBox dimensionListBox = new ListBox();
		dimensionListBox.addItem("");
		dimensionListBox.addItem(DimensionDBConstants.LOCATION);
		dimensionListBox.addItem(DimensionDBConstants.PACKAGE);
		dimensionListBox.addItem(DimensionDBConstants.CLASS);
		dimensionListBox.addItem(DimensionDBConstants.METHOD);
		dimensionListBox.addItem(DimensionDBConstants.PRINCIPAL);
		dimensionListBox.addItem(DimensionDBConstants.DAY_OF_WEEK);
		dimensionListBox.setName(DIM_LIST_BOX_NAME);
		return dimensionListBox;

	}

	private ListBox generatePopulatedComparatorListBox() {
		ListBox comparatorListBox = new ListBox();
		comparatorListBox.addItem("");
		comparatorListBox.addItem("like");
		comparatorListBox.addItem("not like");
		comparatorListBox.addItem(">");
		comparatorListBox.addItem("<");
		comparatorListBox.setName(COMP_LIST_BOX_NAME);
		return comparatorListBox;
	}

	/**
	 * Adds a filter row to the view
	 */
	private void addFilterRow() {
		table.resizeRows(table.getRowCount() + 1);
		
		Panel cellPanel = generateCellPanel();
		/*
		 * Must add listener to this panel and all sub-widgets
		 * This is usually done implicitly but since these components are 
		 * added dynamically during the panels life cycle, extra attention is needed.
		 * 
		 */
		this.addListener(cellPanel);
		table.setWidget(table.getRowCount() - 1, 0, cellPanel);
	}

	/**
	 * Removes a filter row from the view
	 */
	private void removerFilterRow() {
		if (table.getRowCount() > 0) {
			table.clearCell(table.getRowCount() - 1, 0);
			table.resizeRows(table.getRowCount() - 1);
		}
		validate();
	}

	/*
	 * Updates data and performs actions depending on which widget that caused the event.
	 *  
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#updateData(com.google.gwt.user.client.ui.Widget)
	 */
	protected void updateData(Widget widget) {
		if (widget == deleteRowButton) {
			if (filterList.size() > 0) {
				filterList.remove(filterList.size() - 1);
				removerFilterRow();
			}
		} else if (widget == addRowButton) {
			filterList.add(new FilterGUI());
			addFilterRow();
		} else		
		if (selectedRow >= 0) {
			if (filterList.size() < selectedRow + 1) {
				filterList.add(new FilterGUI());
			}
			FilterGUI currentFilter = (FilterGUI) filterList.get(selectedRow);
			if (widget instanceof ListBox) {
				ListBox listBox = (ListBox) widget;

				String name = listBox.getName();
				int index = listBox.getSelectedIndex();
				String value = index > -1 ? listBox.getItemText(listBox.getSelectedIndex()) : null;
				if (value.length() > 0) {
					if (DIM_LIST_BOX_NAME.equals(name)) {
						currentFilter.setTableColumn(value);
					} else if (COMP_LIST_BOX_NAME.equals(name)) {
						currentFilter.setComparator(value);
					}
				}

			} else if (widget instanceof TextBox) {
				TextBox textBox = (TextBox) widget;
				currentFilter.setValue(textBox.getText());
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#validate()
	 */
	public boolean validate() {
		boolean result = true;
		Iterator i = filterList.iterator();
		while (i.hasNext()) {
			FilterGUI filter = (FilterGUI) i.next();
			String tableColumn = filter.getTableColumn();
			String comparator = filter.getComparator();
			String value = filter.getValue();
			if (tableColumn != null && comparator != null && value != null && value.length() > 0) {
				// readability
			} else {
				result = false;
			}

		}
		return result;
	}

	int selectedRow = -1;
	int selectedCell = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.TableListener#onCellClicked(com.google.gwt.user.client.ui.SourcesTableEvents, int, int)
	 */
	public void onCellClicked(SourcesTableEvents arg0, int row, int cell) {
		selectedRow = row;
		selectedCell = cell;

	}

	/**
	 * Returns the List containing all the filters.
	 * @return
	 */
	public ArrayList getFilterList() {
		return filterList;
	}

}
