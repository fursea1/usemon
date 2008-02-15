package org.usemon.gui.client.dimquery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.usemon.gui.client.UsemonQueryObjectGUI;
import org.usemon.gui.client.UsemonQueryResultSetGUI;
import org.usemon.gui.client.dimquery.wizard.DimensionalWizardController;
import org.usemon.gui.client.dimquery.wizard.WizardCallback;
import org.usemon.gui.client.dimquery.wizard.WizardController;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.Grid;
import com.gwtext.client.widgets.grid.GridConfig;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridRowListener;

/**
 * The visual component for performing various dimensional analysis.
 * 
 * It encapsulates the construction of the query wizard.
 * 
 * @author t514257
 * 
 */
public class DimensionalQuery implements ClickListener, 
	WizardCallback // Indicates that we handle call backs from the dimensional
				   // query wizard containing the dbms result set.
		,
	GridRowListener // gwt-ext interface describing various call back methods 
	{

	Button plotButton;
	Button wizardButton;
	VerticalPanel dimensionalQueryPanel;
	Grid gridui = null;
	UsemonQueryResultSetGUI currentResult = null;

	public DimensionalQuery() {

	}

	/** Provides the main panel of this visual component */
	public Widget getBrowsePanel() {
		dimensionalQueryPanel = new VerticalPanel();

		dimensionalQueryPanel.setSpacing(0);
		dimensionalQueryPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);

		// 
		HorizontalPanel searchPanel = new HorizontalPanel();
		searchPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		// Button for plotting the data which has been retrieved using the wizard.
		plotButton = new Button();
		plotButton.setText("Plot row");
		plotButton.addClickListener(this);
		plotButton.setEnabled(false);
		searchPanel.add(plotButton);

		// Button for starting the wizard
		wizardButton = new Button();
		wizardButton.addClickListener(this);
		wizardButton.setText("Start Wizard");
		searchPanel.add(wizardButton);

		dimensionalQueryPanel.add(searchPanel);
		dimensionalQueryPanel.setCellHeight(searchPanel, "1px");

		return dimensionalQueryPanel;
	}

	/** General onClick listener, keeps the code in one place */
	public void onClick(Widget source) {
		if (source == plotButton) {
			plotSelectedRow();
		} else if (source == wizardButton) {
			WizardController wizard = new DimensionalWizardController(this);
			// Starts the iteration through each step of the wizard
			wizard.startWizard();
		}

	}

	/**
	 * Supposed to plot the selected row.
	 * 
	 * TODO: complete the plotting of the selected row from dimensional analysis
	 */
	private void plotSelectedRow() {
		Record selectedRow = gridui.getSelectionModel().getSelected();
		ArrayList values = new ArrayList();

		Iterator i = currentResult.getColumnNames().iterator();
		while (i.hasNext()) {
			double value = selectedRow.getAsDouble((String) i.next());
			System.out.println("Value: " + value);
			values.add(new Double(value));
		}
	}

	/**
	 * Handles the call back from the dimensional query wizard containing the dbms result set
	 * produced by the wizard.
	 *  
	 * @see org.usemon.gui.client.dimquery.wizard.WizardCallback#handleWizardResult(java.util.List)
	 */
	public void handleWizardResult(UsemonQueryResultSetGUI result) {
		currentResult = result;	// saves it for future use.
		
		final String dimensionColumnName = "dimension";
		final String sumColumnName = "hiddenSum";
		
		// We need info on the actual query performed, in order to render the results
		// in a meaningful way
		UsemonQueryObjectGUI queryObject = result.getQueryObject();
		
		// < dimensionName, Array<factValues>>
		HashMap resultMap = result.getResultMap();
		
		// Holds array of gwt ext field definitions.
		List columnNames = result.getColumnNames();
		int counter = 0;

		if (resultMap != null) {
			int rowSize = resultMap.size();
			/*
			 * The grid need an extra column for the vertical dimension values, 
			 * and another for the hidden sum column (hence +2) which aggregates
			 * the fact values for a given dimensional value.
			 */
//			|   | Location1 | Location2 | sum (hidden) |  <<< 2 extra columns
//			|M2 |    999    |    9999   | 999999       |
			int columnSize = columnNames.size() + 2;
			
			// Organizes the data in a structure suitable for a grid.
			Object[][] gridData = new Object[rowSize][columnSize];
			Iterator keyIterator = resultMap.keySet().iterator();
			int rowCounter = 0;
			while (keyIterator.hasNext()) {
				// Retrieves the vertical dimension value
				String verticalDimensionValue = (String) keyIterator.next();
				// ... and sets that as the label in first column, for instance M2
				gridData[rowCounter][0] = verticalDimensionValue;
				
				// Processes the fact values
				Double[] rowValues = (Double[]) resultMap.get(verticalDimensionValue);
				for (int columnCounter = 0; columnCounter < columnSize - 1; columnCounter++) {
					// Stuff them directly into the grid from the result set.
					gridData[rowCounter][columnCounter + 1] = rowValues[columnCounter];
				}
				rowCounter++;
			}

			// Declare names and types of the columns
			FieldDef[] fieldDefArray = new FieldDef[columnSize];
			fieldDefArray[0] = new StringFieldDef(dimensionColumnName);
			for (int ci = 0; ci < columnNames.size(); ci++) {

				fieldDefArray[ci + 1] = new IntegerFieldDef((String) columnNames.get(ci));

			}
			// the aggregated column at the end.
			fieldDefArray[columnSize - 1] = new IntegerFieldDef(sumColumnName);

			// Stuff required by gwt-ext...
			MemoryProxy proxy = new MemoryProxy(gridData);
			RecordDef recordDef = new RecordDef(fieldDefArray);

			ArrayReader reader = new ArrayReader(recordDef);
			Store store = new Store(proxy, reader);
			
			/*
			 * Set the correct sorting order. if sort order somehow not set, use default This should never happen though.
			 */
			configSorting(dimensionColumnName, sumColumnName, queryObject, store);
			
			store.load();	// Loads data into the local data store

			// Sets up the visual explanation of the columns....
			ColumnConfig[] columnConfigs = new ColumnConfig[columnSize];
			for (int i = 0; i < columnConfigs.length; i++) {
				ColumnConfig cc = new ColumnConfig();
				// column header
				cc.setHeader(fieldDefArray[i].getName());
				
				// column width
				if (i == 0)
					cc.setWidth(200);
				else
					cc.setWidth(66);
				// Column may be sorted by clicking on the header
				cc.setSortable(true);
				// Columns may/may not be moved around with drag-and-drop
				cc.setLocked(false);
				// Which field def to use for this column
				cc.setDataIndex(fieldDefArray[i].getName());
				// Tooltip
				cc.setTooltip(fieldDefArray[i].getName());
				// The aggregate column should be hidden
				if (i == columnConfigs.length - 1) {
					cc.setHidden(true);
				}
				
				columnConfigs[i] = cc;
			}
			
			ColumnModel columnModel = new ColumnModel(columnConfigs);

			// creates and renders the grid
			GridConfig gridConfig = new GridConfig();
			// gridConfig.setAutoSizeColumns(true);
			// TODO: automatic resizing of the grid
			gridui = new Grid("completeGrid" + counter++, "800px", "300px", store, columnModel, new RowSelectionModel(true), gridConfig);

			gridui.addGridRowListener(this);	// handle clicks
			gridui.render();	
			// attach to mother panel
			dimensionalQueryPanel.add(gridui);
		}
	}

	/** convenience method for configuring the sorting parameters.
	 * @param dimensionColumnName
	 * @param sumColumnName
	 * @param queryObject
	 * @param store
	 */
	private void configSorting(final String dimensionColumnName, final String sumColumnName, UsemonQueryObjectGUI queryObject, Store store) {
		SortDir sortDir = SortDir.ASC;
		String sortColumn = dimensionColumnName;
		if (queryObject.isOrderVerticalDesc()) {
			sortDir = SortDir.DESC;
		} else {
			sortDir = SortDir.ASC;
		}
		if (queryObject.isOrderVerticalFact()) {
			sortColumn = sumColumnName;
		} else if (queryObject.isOrderVerticalAlpha()) {
			sortColumn = dimensionColumnName;
		}

		store.sort(sortColumn, sortDir);
	}

	/**
	 * Enables the plot button if any element has been selected. 
	 * 
	 * @see com.gwtext.client.widgets.grid.event.GridRowListener#onRowClick(com.gwtext.client.widgets.grid.Grid, int,
	 *      com.gwtext.client.core.EventObject)
	 */
	public void onRowClick(Grid grid, int rowIndex, EventObject e) {
		if (grid.getSelectionModel().getSelected() != null) {
			plotButton.setEnabled(true);
		} else {
			plotButton.setEnabled(false);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gwtext.client.widgets.grid.event.GridRowListener#onRowContextMenu(com.gwtext.client.widgets.grid.Grid, int,
	 *      com.gwtext.client.core.EventObject)
	 */
	public void onRowContextMenu(Grid grid, int rowIndex, EventObject e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gwtext.client.widgets.grid.event.GridRowListener#onRowDblClick(com.gwtext.client.widgets.grid.Grid, int,
	 *      com.gwtext.client.core.EventObject)
	 */
	public void onRowDblClick(Grid grid, int rowIndex, EventObject e) {
	}
}
