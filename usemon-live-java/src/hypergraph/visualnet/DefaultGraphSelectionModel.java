/*
 *  Copyright (C) 2003  Jens Kanschik,
 * 	mail : jensKanschik@users.sourceforge.net
 *
 *  Part of <hypergraph>, an open source project at sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package hypergraph.visualnet;

import hypergraph.graphApi.*;
import java.util.*;
import javax.swing.event.EventListenerList;

/**
 * @author Jens Kanschik
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DefaultGraphSelectionModel implements GraphSelectionModel {
	
	Graph graph;
	
	Set selectedElements;
	
	EventListenerList listenerList;
	
	public DefaultGraphSelectionModel(Graph graph) {
		setGraph(graph);
		selectedElements = new  HashSet();
		listenerList = new EventListenerList();
	}
	
	void setGraph(Graph graph) {
		this.graph = graph;
	}
	public Graph getGraph() {
		return graph;
	}
	
	public void addSelectionEventListener(GraphSelectionListener gsl){
		listenerList.add(GraphSelectionListener.class, gsl);
	}
	public void removeSelectionEventListener(GraphSelectionListener gsl) {
		listenerList.remove(GraphSelectionListener.class, gsl);
	}
	void fireSelectionChanged() {
		Object[] list = listenerList.getListenerList();
		for (int index = list.length-2; index>=0; index-=2) {
			if (list[index] == GraphSelectionListener.class ) {
				((GraphSelectionListener) list[index+1]).valueChanged(
					new GraphSelectionEvent(this));
			}
		}
	}
	
	public void clearSelection() {
		selectedElements.clear();
		fireSelectionChanged();
	}
	
	public void addSelectionElement(Element e) {
		selectedElements.add(e);
		fireSelectionChanged();
	}
	public void removeSelectionElement(Element e) {
		selectedElements.remove(e);
		fireSelectionChanged();
	}
	public boolean isElementSelected(Element e) {
		return selectedElements.contains(e);
	}
	
	public Iterator getSelectionElementIterator() {
		return selectedElements.iterator();
	}
		
}
