package org.usemon.gui.client.service;



import org.usemon.gui.client.UsemonQueryObjectGUI;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
/**
 * 
 * @author t514257
 *
 */
public interface WizardQueryServiceAsync extends RemoteService {
	/**
	 * Generates and perfoms the queries given by the queryObject
	 * @param objectGUI
	 * @param asyncCallback
	 */
	public void performQuery(UsemonQueryObjectGUI objectGUI, AsyncCallback asyncCallback);

}
