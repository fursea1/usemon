package org.usemon.gui.client.controller;

import java.util.Date;

import org.usemon.gui.client.UsemonView;
import org.usemon.gui.client.model.UsemonModel;
import org.usemon.gui.client.service.PlotService;
import org.usemon.gui.client.service.PlotServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


/**
 * Main Controller for the application.
 *  
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class UsemonController extends AbstractController  {

	private UsemonView usemonView;
	private PlotServiceAsync plotServiceAsync = null;
	private UsemonModel usemonModel;
	

	public UsemonController() {
		usemonModel = new UsemonModel();
		addModel(usemonModel);
	}
	
	public void uiIsReady() {
		usemonView.onUiReady();
	}

	public void setView(UsemonView usemonView) {
		this.usemonView = usemonView;
		addView(usemonView);	// Will enable automagic property change support
	}

	public PlotServiceAsync getPlotService() {
		if (plotServiceAsync == null) {
			plotServiceAsync = (PlotServiceAsync) GWT.create(PlotService.class);
			ServiceDefTarget serviceDefTarget = (ServiceDefTarget) plotServiceAsync;
			String serviceUrl = GWT.getModuleBaseURL() + "/plotService";
			serviceDefTarget.setServiceEntryPoint(serviceUrl);
		}
		return plotServiceAsync;
	}

	public void refreshStatusGraph() {
		GWT.log("refreshStatusGraph() :- being invoked ",null);
		AsyncCallback asyncCallback = new AsyncCallback() {

			public void onFailure(Throwable arg0) { }

			public void onSuccess(Object dateObj) {
				Date dt = (Date) dateObj;
				boolean redraw = dt.after(usemonModel.getLatestObservationTimeStamp());
				usemonModel.setLatestObservationTimeStamp(dt);
				if (usemonView.isStatusGraphTabSelected() && redraw) {
					GWT.log("refreshStatusGraph() :- redrawing the graph", null);
					usemonView.onMoreMethodObservationsAvailable();
					// NOTE! We are invoking the service again and again...
					refreshStatusGraph();
				}
			}};
			
		if (getPlotService() == null)
			throw new IllegalStateException("plotService() is null");
		
		if (usemonModel == null || usemonModel.getLatestObservationTimeStamp() == null)
			throw new IllegalStateException("Ooops, null pointer error in refreshStatusGraph");
		
		// Waits for 60 seconds to see if there is more data available
		GWT.log("Invoking service to check for more data", null);
		getPlotService().waitForDataOlderThan(usemonModel.getLatestObservationTimeStamp(), 60, asyncCallback);
	}

}
