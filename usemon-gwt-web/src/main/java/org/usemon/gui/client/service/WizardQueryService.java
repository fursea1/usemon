package org.usemon.gui.client.service;

import org.usemon.gui.client.UsemonQueryObjectGUI;
import org.usemon.gui.client.UsemonQueryResultSetGUI;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * 
 * @author t514257
 *
 */
public interface WizardQueryService extends RemoteService {

	/**
	 * Generates and perfoms the queries given by the queryObject
	 * @param objectGUI
	 */
	public UsemonQueryResultSetGUI performQuery(UsemonQueryObjectGUI objectGUI);

}
