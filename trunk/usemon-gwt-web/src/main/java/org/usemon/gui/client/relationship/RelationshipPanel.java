/**
 * Created 19. nov.. 2007 13.22.36 by Steinar Overbeck Cook
 */
package org.usemon.gui.client.relationship;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class RelationshipPanel extends Composite {
	DeckPanel wizardPanel = null;

	public RelationshipPanel() {
		DockPanel dockPanel = new DockPanel();
		initWizardPanel();

		dockPanel.add(wizardPanel, DockPanel.CENTER);
		
		dockPanel.add(navigationButtonPanel(), DockPanel.SOUTH);
		
		initWidget(dockPanel);
	}

	/**
	 * 
	 */
	private void initWizardPanel() {
		wizardPanel = new DeckPanel();
		// Page 0 contains the parameters..
		wizardPanel.add(getParameterInputPanel());
		// Page 1 contains the computed download link..
		wizardPanel.add(prepareForDownload());
		
		// Start widget on page 0
		wizardPanel.showWidget(0); // Display first page
	}

	private Widget prepareForDownload() {
		DockPanel dockPanel = new DockPanel();
		dockPanel.add(new HTML("<a href=\"relationshipServlet\">download relationship data</a>"), DockPanel.CENTER);
		return dockPanel;
	}
	private void replaceLastPage(DeckPanel wizardPanel) {
		// Removes last widget
		if (wizardPanel.getWidgetCount() > 1)
			wizardPanel.remove(wizardPanel.getWidgetCount()-1);
		// Adds new last page
		wizardPanel.add(prepareForDownload());
	}

	private Widget getParameterInputPanel() {
		DockPanel p = new DockPanel();

		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(8);
		// Date interval
		vp.add(dateParameterWidget());

		// Data format
		vp.add(dataFormatWidget());

		p.add(vp, DockPanel.CENTER);

		return p;
	}

	
	private Widget navigationButtonPanel() {
		DockPanel dockPanel = new DockPanel();
		
		final Button prevButton = new Button("<< Prev");
		final Button nextButton = new Button("Nex >>");

		prevButton.addClickListener(new ClickListener() {

			public void onClick(Widget widget) {
				int currentWizardPage = wizardPanel.getVisibleWidget();
				
				nextButton.setEnabled(true);

				if (currentWizardPage > 0) {
					wizardPanel.showWidget(--currentWizardPage);
				} 
				
				if (currentWizardPage == 0) {
					prevButton.setEnabled(false);
				}
			}});
		dockPanel.add(prevButton,DockPanel.WEST);
		prevButton.setEnabled(false);

		// handle clicks on next button
		nextButton.addClickListener(new ClickListener() {
			public void onClick(Widget widget) {
				int current = wizardPanel.getVisibleWidget();
				prevButton.setEnabled(true);	// Make previous available now
				int pages = wizardPanel.getWidgetCount();
				if (pages-1 >= current) {
					replaceLastPage(wizardPanel);
					wizardPanel.showWidget(++current);
				} 
				if (current >= pages-1) {
					nextButton.setEnabled(false);
					
				}
			}});
		dockPanel.add(nextButton, DockPanel.EAST);
		
		dockPanel.setWidth("100%");
		return dockPanel;
	}

	private Widget dataFormatWidget() {

		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(4);

		// Data format
		vp.add(new Label("Specify the format:"));
		RadioButton gmlFormatChoice = new RadioButton("dataForm", "GML");
		gmlFormatChoice.setChecked(true);
		vp.add(gmlFormatChoice);

		RadioButton graphMlFormatChoice = new RadioButton("dataForm", "GraphML");
		vp.add(graphMlFormatChoice);

		return vp;
	}

	private Widget dateParameterWidget() {
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(4);
		vp.add(new Label("Specify the time period"));
		RadioButton completeButton = new RadioButton("datePeriod", "Complete set");
		completeButton.setChecked(true);
		vp.add(completeButton);

		vp.add(new RadioButton("datePeriod", "Today"));
		vp.add(new RadioButton("datePeriod", "Last week"));
		return vp;
	}
}
