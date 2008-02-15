/**
 * 
 */
package org.usemon.gui.client.service;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author t547116
 *
 */
public interface PlotServiceAsync extends  RemoteService {

	public void waitForDataOlderThan(Date date, int waitForSeconds, AsyncCallback asyncCallback);

}
