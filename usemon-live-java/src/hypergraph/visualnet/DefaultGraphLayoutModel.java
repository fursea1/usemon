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
import hypergraph.hyperbolic.ModelPoint;
import java.util.*;
import javax.swing.event.EventListenerList;


/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultGraphLayoutModel implements GraphLayoutModel {
	
	Map nodePositions;
	
	EventListenerList listenerList;
	
	private boolean valid;
	
	public DefaultGraphLayoutModel() {
		clearNodePositions();
		listenerList = new EventListenerList();
	}
	
	public void setValid(boolean valid) {
		if (valid != this.valid)
			fireLayoutChanged();
		this.valid = valid;
	}
	public boolean isValid() {
		return valid;
	}
	
	public void addLayoutEventListener(GraphLayoutListener gll){
		listenerList.add(GraphLayoutListener.class, gll);
	}
	public void removeLayoutEventListener(GraphLayoutListener gll) {
		listenerList.remove(GraphLayoutListener.class, gll);
	}
	public void fireLayoutChanged() {
		Object[] list = listenerList.getListenerList();
		for (int index = list.length-2; index>=0; index-=2) {
			if (list[index] == GraphLayoutListener.class ) {
				((GraphLayoutListener) list[index+1]).valueChanged(
					new GraphLayoutEvent(this));
			}
		}
	}
	
	public void clearNodePositions() {
		nodePositions = new HashMap();
	}
	public void setNodePosition(Node node,ModelPoint mp) {
		nodePositions.put(node,mp);
		fireLayoutChanged();
	}
	public ModelPoint getNodePosition(Node node) {
		return (ModelPoint) nodePositions.get(node);
	}
	
}
