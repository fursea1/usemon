/**
 * 
 */
package org.usemon.gui.client.statusgraph;

import java.beans.PropertyChangeEvent;
import java.util.Date;

import org.usemon.gui.client.controller.UiController;
import org.usemon.gui.client.controller.UsemonController;
import org.usemon.gui.client.view.UiView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders the status graph.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class StatusGraphView extends Composite implements UiView {

	private DockPanel dockPanel = new DockPanel();
	// Holds the image
	private SimplePanel graphPanel = new SimplePanel();
	
	private Image image = new Image();
	private Label label = new Label();
	private LoadListener loadListener = null;
	public String serviceUrl;
	private Panel paramsPanel;			// Parameter panel on the right hand side
	private int graphWidth;
	private int graphHeight;
	private TextBox widthField;
	private TextBox heightField;
	
	private Date lastLoadTime = null;
	private final UsemonController usemonController;
	
	public StatusGraphView(UsemonController usemonController) {
		super();
		this.usemonController = usemonController;
		
		initWidget(dockPanel);
		setWidth("100%");
		setHeight("100%");
		
		dockPanel.add(label, DockPanel.NORTH);
		
		graphPanel.setWidget(new Label(" "));	// Forces the panel to be given a size (I hope)
		graphPanel.setHeight("100%");
		graphPanel.setWidth("100%");
		graphPanel.setStyleName("graphPanel");
		
		dockPanel.add(graphPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(graphPanel, "100%");
		dockPanel.setCellWidth(graphPanel, "100%");
		dockPanel.setCellHorizontalAlignment(graphPanel, DockPanel.ALIGN_CENTER);
		dockPanel.setCellVerticalAlignment(graphPanel, DockPanel.ALIGN_MIDDLE);
		
		// Listens for load events on the status graph image
		loadListener = new LoadListener() {

			public void onError(Widget w) {
				label.setText("Unable to load status image");
			}

			public void onLoad(Widget w) {
				lastLoadTime = new Date();
				label.setText("Loaded status graph from " + serviceUrl);
			}};
		image.addLoadListener(loadListener);
		
		// Adds the input fields for the parameters including the buttons
		paramsPanel = initParamsPanel();
		
		dockPanel.add(paramsPanel, DockPanel.EAST);
	}

	
	
	private Panel initParamsPanel() {
		Button reloadButton = new Button("Reload");
		reloadButton.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				loadGraph();
			}});
		FlexTable labelsAndInputFields = new FlexTable();
		int row = 0;

		Label widthLabel = new Label("Width:");
		labelsAndInputFields.setWidget(row, 0, widthLabel);
		
		widthField = new TextBox();
		widthField.setText(""+getGraphWidth());
		widthField.setTextAlignment(TextBox.ALIGN_RIGHT);
		widthField.setWidth("5em");
		
		// TODO: create reusable Integer field
		KeyboardListener integerKeyboardListener = new KeyboardListener() {

			public void onKeyDown(Widget arg0, char arg1, int arg2) {
			}

			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if (!Character.isDigit(keyCode))
					((TextBox)sender).cancelKey();	// cancel invalid characters
			}

			public void onKeyUp(Widget arg0, char arg1, int arg2) {
			}};
			
		widthField.addKeyboardListener(integerKeyboardListener);
		
		widthField.addChangeListener(new ChangeListener() {
			public void onChange(Widget widthFieldWidget) {
				int width = Integer.parseInt(widthField.getText().trim());
				setGraphWidth(width);
			}});
		labelsAndInputFields.setWidget(row, 1, widthField);
		
		Label heightLabel = new Label("Height:");
		labelsAndInputFields.setWidget(++row, 0, heightLabel);
		heightField = new TextBox();
		heightField.setText(""+getGraphHeight());
		heightField.setTextAlignment(TextBox.ALIGN_RIGHT);
		heightField.setWidth("5em");
		
		heightField.addKeyboardListener(integerKeyboardListener);
		heightField.addChangeListener(new ChangeListener() {

			public void onChange(Widget widget) {
				int height = Integer.parseInt(heightField.getText().trim());
				setGraphHeight(height);
			}});
		labelsAndInputFields.setWidget(row, 1, heightField);
		
		Panel p = new VerticalPanel();
		p.add(labelsAndInputFields);
		p.add(reloadButton);
		return p;
	}


	/** Loads the graph into the image */
	public void loadGraph() {

		if (graphPanel.getWidget() != image)
			graphPanel.setWidget(image);

		GWT.log("loadGraph(): - h=" + getOffsetHeight() + ", w=" + getOffsetWidth(), null);
		GWT.log("Window.height=" + Window.getClientHeight() + ", w=" + Window.getClientWidth(), null);
		GWT.log("Parent height="+ getParent().getOffsetHeight() + ", w=" + getParent().getOffsetWidth(), null);
		GWT.log("GraphPanel height="+ graphPanel.getOffsetHeight() + ", w=" + graphPanel.getOffsetWidth(), null);
		
		String params = "&width=" + graphWidth + "&height=" + graphHeight;
		serviceUrl = GWT.getModuleBaseURL() + "plotServlet?plot=status" + params;
		if (GWT.isScript()) {
			serviceUrl = "../plotServlet?plot=status" + params;
		}
		label.setText("Loading status graph....");
		image.setUrl("");
		image.setUrl(serviceUrl);
	}

	public int getGraphWidth() {
		return graphWidth;
	}

	public void setGraphWidth(int graphWidth) {
		this.graphWidth = graphWidth;
	}

	public int getGraphHeight() {
		return graphHeight;
	}

	public void setGraphHeight(int graphHeight) {
		this.graphHeight = graphHeight;
	}

	public void onWindowResized(int width, int height) {
		setGraphHeight(graphPanel.getOffsetHeight());
		heightField.setText(""+getGraphHeight());
		setGraphWidth(graphPanel.getOffsetWidth());
		widthField.setText("" + getGraphWidth());
	}

	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() != this) {
			if (evt.getPropertyName().equals(UiController.LATEST_OBSERVATION_TIMESTAMP)) {
				;
			}
		}		
	}
}
