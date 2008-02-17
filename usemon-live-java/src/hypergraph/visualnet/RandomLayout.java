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

import java.util.Iterator;

import hypergraph.graphApi.*;
import hypergraph.hyperbolic.*;

/**
 * @author Jens Kanschik
 */
public class RandomLayout extends AbstractGraphLayout {

	
	public RandomLayout() {
	}
	public RandomLayout(Graph graph) {
		setGraph(graph);
	}

	public void elementsAdded(GraphEvent ge) {
		invalidate();
		Element element = ge.getElement();
		if (element instanceof Node)
			getGraphLayoutModel().setNodePosition((Node) element,
				new Complex(Math.random()*1.4-0.7, Math.random()*1.4-0.7));
	}
	public void layout() {
		if (getGraphLayoutModel() == null)
			setGraphLayoutModel(new DefaultGraphLayoutModel());
		for (Iterator i = getGraph().getNodes().iterator(); i.hasNext();) {
			Node node = (Node) i.next();
			getGraphLayoutModel().setNodePosition(node,
				new Complex(Math.random()*1.4-0.7, Math.random()*1.4-0.7));
		}
		getGraphLayoutModel().setValid(true);
	}

	public boolean isExpandingEnabled() {
		return false;
	}


}
