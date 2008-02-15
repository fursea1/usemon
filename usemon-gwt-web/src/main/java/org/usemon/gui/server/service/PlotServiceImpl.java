package org.usemon.gui.server.service;

import java.util.Date;

import org.usemon.domain.DeputoService;
import org.usemon.domain.UsemonServiceLocator;
import org.usemon.gui.client.service.PlotService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of various services related to plotting of data.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class PlotServiceImpl extends RemoteServiceServlet implements PlotService {

	public Date waitForDataOlderThan(Date date, int waitForSeconds) {
		try {
			DeputoService deputoService = UsemonServiceLocator.getDeputoService();
			Date lastMethodMeasurementObservation = deputoService.getLastMethodObservation();
			
			System.err.println("Sleeping for " + waitForSeconds + " seconds");
			Thread.currentThread().sleep(waitForSeconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Date();
	}

}
