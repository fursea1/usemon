package org.usemon.gui.client;

import org.usemon.gui.client.controller.UsemonController;
import org.usemon.gui.client.model.UsemonModel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class UsemonApp implements EntryPoint, WindowResizeListener
{
	private UsemonView usemonView;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		UsemonController usemonController = new UsemonController(); 
		
		usemonView = new UsemonView(usemonController);
		usemonController.setView(usemonView);
		Window.addWindowResizeListener(this);
		RootPanel.get().add(usemonView);
		
		 // Get rid of scroll bars, and clear out the window's built-in margin,
	    // because we want to take advantage of the entire client area.
//	    Window.enableScrolling(false);
	    Window.setMargin("0px");

		// Calls the window resized handler to get the initial sizes setup. Doing
	    // this in a deferred command causes it to occur after all widgets' sizes
	    // have been computed by the browser.
	    DeferredCommand.addCommand(new Command() {
	      public void execute() {
	        onWindowResized(Window.getClientWidth(), Window.getClientHeight());
	      }
	    });
	    
	    onWindowResized(Window.getClientWidth(), Window.getClientHeight());
	    // Informs the controller that the UI has been rendered and is ready for use
	    usemonController.uiIsReady();
	}

	public void onWindowResized(int width, int height) {
		usemonView.onWindowResized(width, height);
	}
}
