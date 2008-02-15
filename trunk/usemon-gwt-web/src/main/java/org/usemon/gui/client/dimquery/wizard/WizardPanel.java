/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import java.util.Iterator;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract superclass wizard panels (aka Wizard screens). Provides automatic listener and update functionality for Widgets.
 * Provides communication and update of to <code>WizardView</code>s navigation button panel All wizard screens must extend this
 * class.
 * 
 * @author t514257
 * 
 */
public abstract class WizardPanel extends VerticalPanel implements ClickListener, ChangeListener {

	WizardView wizardContainer = null;
	boolean optional;
	String panelTitle = "Please add a title";
	Label panelSecondaryTitle;
	Image helperImage;

	/**
	 * Constructor pre-initializes and then call the abstract method intitalize
	 * 
	 * @param panelTitle -
	 *            The title of this panel.
	 * @param optional -
	 *            True if this panel is optional, false if it should be mandatory to complete.
	 */
	WizardPanel(String panelTitle, boolean optional) {
		this.optional = optional;
		this.panelTitle = panelTitle;
		preInitialize();
		initPanel(); // Invokes the concrete implementation
	}

	/**
	 * Performs initialization that should precede the implementing class' initialization
	 */
	private void preInitialize() {
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setWidth("600px");
		titlePanel.setSpacing(20);
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		panelSecondaryTitle = new Label("", true);
		titlePanel.add(panelSecondaryTitle);
		helperImage = new Image();
		helperImage.setSize("270px", "170px");
		helperImage.setVisible(false);
		titlePanel.add(helperImage);
		titlePanel.setCellHorizontalAlignment(helperImage, HasHorizontalAlignment.ALIGN_RIGHT);

		super.add(titlePanel);
	}

	/**
	 * Locates the wizardContainer (<code>WizardView</code>) and updates the navigation panel.
	 * 
	 * @param parent -
	 *            the widget to start looking for the wizardContainer. Only used by first invocation
	 */
	private void updateWizardViewButtons(Widget parent) {
		findTheWizardContainer(parent);

		if (wizardContainer != null) {
			wizardContainer.enableDisableButtons();
		}
	}

	/**
	 * @param parent
	 */
	private void findTheWizardContainer(Widget parent) {
		if (wizardContainer == null) {
			if (parent.getParent() instanceof WizardView) {
				wizardContainer = (WizardView) parent.getParent();
			} else {
				updateWizardViewButtons(parent.getParent());
			}
		}
	}

	/**
	 * Determine if this is an optional panel
	 * 
	 * @return true if optional, false if mandatory
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Get the title of this panel.
	 * 
	 * @return
	 */
	public String getPanelTitle() {
		return panelTitle;
	}

	/**
	 * Invoked by all input widgets inside this panel and decessors
	 * 
	 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
	 */
	public final void onClick(Widget widget) {
		updateData(widget);
		updateWizardViewButtons(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
	 */
	public final void onChange(Widget widget) {
		updateData(widget);
		updateWizardViewButtons(getParent());

	}

	/**
	 * Adds the widget to this panel, also add <code>this</code> as a listener on the widget if applicable
	 */
	public void add(Widget widget) {
		super.add(widget);
		addListener(widget);
	}

	/**
	 * Iterates through any child widgets for this widget and add listeners if applicable.
	 * 
	 * @param widget -
	 *            The base widget used as a starting point for finding "listenable" widget.
	 */
	protected void addListener(Widget widget) {
		if (widget instanceof HasWidgets) {
			Iterator i = ((HasWidgets) widget).iterator();
			while (i.hasNext()) {
				addListener((Widget) i.next());
			}
		}
		if (widget instanceof SourcesChangeEvents) {
			((SourcesChangeEvents) widget).addChangeListener(this);
		} else // dont need click events if this is a SourceChangeEvent
		if (widget instanceof SourcesClickEvents) {
			((SourcesClickEvents) widget).addClickListener(this);
		}
	}

	/**
	 * This method should implement the validation rules of the panel.
	 * 
	 * @return true if validation OK, false otherwise
	 */
	public abstract boolean validate();

	/**
	 * If a subscribed data-change event occurs on any of the widgets this method will eventually be called. Implement any logic
	 * deemed necessary to handle this event.
	 * 
	 */
	protected abstract void updateData(Widget widget);

	/**
	 * This method should implement initialization code for your panel. This is where you create and build your GUI elements
	 */
	protected abstract void initPanel();

	/**
	 * Sets the secondary title for this panel. Usually this is a text that provides the user with a hint of what to do with this
	 * screen
	 * 
	 * @param panelSecondaryTitle
	 */
	public void setPanelSecondaryTitle(String panelSecondaryTitle) {
		this.panelSecondaryTitle.setText(panelSecondaryTitle);
	}

	/**
	 * Set the URL to the helper image. This image will be placed to the left of the secondary title, above the actual content of
	 * the panel.
	 * 
	 * @param url
	 */
	public void setHelperImageURL(String url) {
		helperImage.setUrl(url);
		helperImage.setVisible(true);
	}

}
