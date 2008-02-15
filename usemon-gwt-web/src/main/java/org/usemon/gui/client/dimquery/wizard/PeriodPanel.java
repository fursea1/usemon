package org.usemon.gui.client.dimquery.wizard;

import java.util.Date;

import org.usemon.gui.client.utils.DateTimeUtils;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Wizard panel that provides the user the ability to set the time span for which the query is to be performed.
 * 
 * @author t514257
 * 
 */
public class PeriodPanel extends WizardPanel {

	Date periodFromDate = null;
	Date periodToDate = null;
	long numberOfMilliSeconds = 0;

	private final static String RADIO_TIME_FRAME_GROUP = "RADIO_TIME_FRAME_GROUP";

	private RadioButtonTimeFrame hourRadio;
	private RadioButtonTimeFrame dayRadio;
	private RadioButtonTimeFrame weekRadio;
	private RadioButtonTimeFrame monthRadio;
	private RadioButtonTimeFrame noValue;
	private TextBox fromDateTextBox;
	private TextBox toDateTextBox;
	private Label fromDateLabel;
	private Label toDateLabel;

	private boolean initialized = false;

	/**
	 * Constructor
	 * 
	 * @param panelTitle
	 * @param optional
	 *            indicates whether this step is optional in the wizard or not
	 */
	PeriodPanel(String panelTitle, boolean optional) {
		super(panelTitle, optional);
	}

	// Invoked by the constructor of the super class
	protected void initPanel() {
		if (!initialized) {
			this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

			hourRadio = new RadioButtonTimeFrame(RADIO_TIME_FRAME_GROUP, "Last ¤ hours", DateTimeUtils.getMillisPerHour(), 1);
			dayRadio = new RadioButtonTimeFrame(RADIO_TIME_FRAME_GROUP, "Last ¤ days", DateTimeUtils.getMillisPerDay(), 1);
			weekRadio = new RadioButtonTimeFrame(RADIO_TIME_FRAME_GROUP, "Last ¤ weeks", DateTimeUtils.getMillisPerWeek(), 1);
			monthRadio = new RadioButtonTimeFrame(RADIO_TIME_FRAME_GROUP, "Last ¤ months", DateTimeUtils.getMillisPerMonth(), 1);

			noValue = new RadioButtonTimeFrame(RADIO_TIME_FRAME_GROUP, "noValue");
			noValue.setChecked(true);
			noValue.setVisible(false);

			fromDateTextBox = new TextBox();
			toDateTextBox = new TextBox();
			fromDateLabel = new Label("Fra dato: ");
			toDateLabel = new Label("Til dato: ");
			HorizontalPanel fromToPanel = new HorizontalPanel();
			fromToPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			fromToPanel.add(fromDateLabel);
			fromToPanel.add(fromDateTextBox);
			fromToPanel.add(toDateLabel);
			fromToPanel.add(toDateTextBox);

			// Adding these input widgets, will also
			// attach a click listener or change listener, since
			// this method is overridden in the parent class.
			this.add(hourRadio);
			this.add(dayRadio);
			this.add(weekRadio);
			this.add(monthRadio);
			this.add(noValue);
			this.add(fromToPanel);

			initialized = true;
		}
	}

	/**
	 * Invoked from the super class,
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#updateData()
	 */
	protected void updateData(Widget widget) {
		if (widget instanceof RadioButton) {
			periodFromDate = null;
			periodToDate = null;
			fromDateTextBox.setText(null);
			toDateTextBox.setText(null);
		} else if (widget instanceof RadioButtonTimeFrame) {
			periodFromDate = null;
			periodToDate = null;
			fromDateTextBox.setText(null);
			toDateTextBox.setText(null);
			numberOfMilliSeconds = ((RadioButtonTimeFrame) widget).getTotalMillis();
		} else {
			DateTimeFormat formatter = DateTimeFormat.getFormat("dd.MM.yyyy");
			String fromDate = fromDateTextBox.getText();
			if (fromDate != null && fromDate.length() > 0) {
				periodFromDate = formatter.parse(fromDate);
				noValue.setChecked(true);
			} else {
				periodFromDate = null;
			}

			String toDate = toDateTextBox.getText();
			if (toDate != null && toDate.length() > 0) {
				periodToDate = formatter.parse(toDate);
				noValue.setChecked(true);
			} else {
				periodToDate = null;
			}
		}
	}

	/**
	 * invoked from super class to validate the contents of this panel
	 * 
	 * @see org.usemon.gui.client.dimquery.wizard.WizardPanel#validate()
	 */
	public boolean validate() {
		boolean result = false;
		result = result || hourRadio.isChecked() || dayRadio.isChecked() || weekRadio.isChecked() || monthRadio.isChecked();
		result = result || (periodFromDate != null && periodToDate != null);
		return result;
	}

	public Date getPeriodFromDate() {
		return periodFromDate;
	}

	public Date getPeriodToDate() {
		return periodToDate;
	}

	public long getNumberOfMilliSeconds() {
		return numberOfMilliSeconds;
	}

}
