/**
 * 
 */
package org.usemon.gui.client.dimquery.wizard;

import java.util.ArrayList;
import java.util.Iterator;

import org.usemon.gui.client.utils.WidgetUtils;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A very specific implementation of a widget which calculates a certain amount of units
 * with a multiplier
 * @author t514257
 *
 */
public class RadioButtonTimeFrame extends Composite implements ClickListener, ChangeListener, SourcesChangeEvents {

	String prefixLabel;
	String postFixLabel;
	private final String tokenizer = "¤";
	private int value = 1;
	private long multiplier = 1;
	TextBox textbox;
	RadioButton radioButton;
	ArrayList changeListeners = new ArrayList();
	

	/**
	 * 
	 * @param group - A radiobutton group (for single select)
	 * @param caption - caption, use ¤ to place inputfield at location
	 */
	public RadioButtonTimeFrame(String group, String caption) {
		this(group, caption, 1, 1);
	}
	/**
	 * Constructor
	 * @param group - A radiobutton group (for single select)
	 * @param caption - caption, use ¤ to place inputfield at location
	 * @param multiplier - The multiplier for the unit
	 * @param defaultValue - default unit value (will be placed in inputfield)
	 * 
	 */
	public RadioButtonTimeFrame(String group, String caption, long multiplier, int defaultValue) {
		
		this.multiplier = multiplier;
		String[] captionArray = caption.split(tokenizer);
		prefixLabel = captionArray[0];
		value = defaultValue;
		if (captionArray.length >1) {
			postFixLabel = captionArray[1];
		}
		
		HorizontalPanel panel  = new HorizontalPanel();
		radioButton = new RadioButton(group, prefixLabel+":");
		textbox = WidgetUtils.getNumericTextbox();
		textbox.setWidth("20px");
		textbox.setText(String.valueOf(value));
		panel.add(radioButton);
		panel.setCellHorizontalAlignment(radioButton, HorizontalPanel.ALIGN_LEFT);
		panel.add(textbox);
		panel.setCellHorizontalAlignment(textbox, HorizontalPanel.ALIGN_RIGHT);
		if (postFixLabel != null) {
			panel.add(new Label(postFixLabel));
		}
		radioButton.addClickListener(this);
		textbox.addChangeListener(this);
		
		initWidget(panel);
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
	 */
	public void onClick(Widget widget) {
		value = Integer.parseInt(textbox.getText());
		notifyChangeListeners();

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
	 */
	public void onChange(Widget widget) {
		if (textbox.getText() != null && textbox.getText().length() > 0) {
			value = Integer.parseInt(textbox.getText());
			radioButton.setChecked(true);
		} else {
			textbox.setText("0");
			value= 0;
		}
		notifyChangeListeners();		
	
		
	}
	
	private void notifyChangeListeners() {
		Iterator i = changeListeners.iterator();
		while (i.hasNext())
		{
			ChangeListener listener = (ChangeListener)i.next();
			listener.onChange(this);
		}		
	}
	
	public long getTotalMillis() {
		return multiplier * value;
	}

	/**
	 * @param b
	 */
	public void setChecked(boolean checked) {
		radioButton.setChecked(checked);
		
	}
	/**
	 * @return
	 */
	public boolean isChecked() {
		return radioButton.isChecked();
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.SourcesChangeEvents#addChangeListener(com.google.gwt.user.client.ui.ChangeListener)
	 */
	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
		
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.SourcesChangeEvents#removeChangeListener(com.google.gwt.user.client.ui.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener listener) {
		changeListeners.remove(listener);
		
	}

}
