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

package hypergraph.graphApi;

import java.util.Collection;

public interface Graph extends Element, GraphListener {
	
	// creating elements :		
	Node createNode();
	Node createNode(String name) throws GraphException;
	Group createGroup();
	Group createGroup(String name) throws GraphException;
	Edge createEdge(Node source, Node target);
	Edge createEdge(String name, Node source, Node target) throws GraphException;

	// getting elements :
	Element getElement(String name);
	Collection getEdges();
	Collection getEdges(Node node);
	Collection getNodes();
	Collection getGroups();

	// adding/removing :
	void addElement(Element element) throws GraphException;
	void removeElement(Element element);
	void removeAll();
	
	boolean isConnected(Node node1, Node node2);

	GraphSystem getGraphSystem();
	
	AttributeManager getAttributeManager();

	Graph getSpanningTree();

	Graph getSpanningTree(Node root);

	void addGraphListener(GraphListener listener);

	void removeGraphListener(GraphListener listener);

}