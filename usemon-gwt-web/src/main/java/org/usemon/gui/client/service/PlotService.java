/**
 * 
 */
package org.usemon.gui.client.service;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author t547116
 *
 */
public interface PlotService extends  RemoteService {


	public Date waitForDataOlderThan(Date date, int waitForSeconds);
}
