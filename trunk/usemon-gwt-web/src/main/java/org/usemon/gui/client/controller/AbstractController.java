package org.usemon.gui.client.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import org.usemon.gui.client.model.AbstractUiModel;
import org.usemon.gui.client.view.UiView;


public class AbstractController implements UiController, PropertyChangeListener {

	private ArrayList registeredViews;
	private ArrayList registeredModels;

	public AbstractController() {
		registeredViews = new ArrayList();
        registeredModels = new ArrayList();
	}

	 /**
     * Binds a model to this controller. Once added, the controller will listen for all 
     * model property changes and propagate them on to registered views. In addition,
     * it is also responsible for resetting the model properties when a view changes
     * state.
     * @param model The model to be added
     */
	public void addModel(AbstractUiModel model) {
        registeredModels.add(model);
        model.addPropertyChangeListener(this);
    }

    /**
     * Unbinds a model from this controller.
     * @param model The model to be removed
     */
    public void removeModel(AbstractUiModel model) {
        registeredModels.remove(model);
        model.removePropertyChangeListener(this);
    }
    
    
    /**
     * Binds a view to this controller. The controller will propagate all model property
     * changes to each view for consideration.
     * @param view The view to be added
     */
	public void addView(UiView view) {
        registeredViews.add(view);
    }

    /**
     * Unbinds a view from this controller.
     * @param view The view to be removed
     */
    public void removeView(UiView view) {
        registeredViews.remove(view);
    }



    //  Used to observe property changes from registered models and propagate
    //  them on to all the views.
    
    /**
     * This method is used to implement the PropertyChangeListener interface. Any model
     * changes will be sent to this controller through the use of this method.
     * @param evt An object that describes the model's property change.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        
    	for (Iterator iterator = registeredViews.iterator(); iterator.hasNext();) {
			UiView view = (UiView) iterator.next();
            view.modelPropertyChange(evt);
        }
    }
        
}
