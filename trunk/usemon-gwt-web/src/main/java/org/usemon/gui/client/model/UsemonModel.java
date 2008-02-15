/**
 * Created 29. jan.. 2008 13.03.48 by Steinar Overbeck Cook
 */
package org.usemon.gui.client.model;

import java.util.Date;

import org.usemon.gui.client.controller.UiController;


/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class UsemonModel extends AbstractUiModel {

	private Date latestObservationTimeStamp;		// time stamp of last time we loaded status graph data
	

	public UsemonModel() {
		// anything will be after this date :-)
		latestObservationTimeStamp = new Date(86400);
	}
	
	public Date getLatestObservationTimeStamp() {
		return latestObservationTimeStamp;
	}

	public void setLatestObservationTimeStamp(Date lastLoadTime) {
		Date oldDate = this.latestObservationTimeStamp;
		this.latestObservationTimeStamp = lastLoadTime;
		firePropertyChange(UiController.LATEST_OBSERVATION_TIMESTAMP, oldDate, lastLoadTime);
	}

}
