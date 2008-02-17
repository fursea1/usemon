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
package hypergraph.graphApi.algorithms;

import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BFSGraphWalker extends GraphWalker {

	private Node		startNode;
	private boolean		ignoreVisitedNodes;
	private Set			visitedNodes;
	private Set			visitedEdges;
	private Set 		currentNodes;
	private Set 		nextNodes;
	private int			iteration;

	public BFSGraphWalker() {
		ignoreVisitedNodes = true;
		visitedNodes = new HashSet();
		visitedEdges = new HashSet();
		currentNodes = new HashSet();
		nextNodes = new HashSet();
	}
	public int getIteration() {
		return iteration;
	}
	public void setStartNode(Node node) {
		startNode = node;
	}
	public void setIgnoreVisited(boolean iv) {
		ignoreVisitedNodes = iv;
	}
	public boolean isIgnoreVisited() {
		return ignoreVisitedNodes;
	}
	public void walk() {
		if (startNode == null)
			return;
		iteration = 0;
		visitedNodes.clear();
		visitedEdges.clear();
		currentNodes.clear();
		nextNodes.clear();
		visitElement(startNode);
		visitedNodes.add(startNode);
		currentNodes.add(startNode);
		while (!currentNodes.isEmpty()) {
			iteration++;
			for (Iterator currentIter = currentNodes.iterator(); currentIter.hasNext();) {
				Node currentNode = (Node) currentIter.next();
				Collection edges = getGraph().getEdges(currentNode);
				if (edges != null) {
					for (Iterator i = edges.iterator(); i.hasNext();) {
						Edge edge = (Edge) i.next();
						if (visitedEdges.contains(edge))
							continue;
						visitedEdges.add(edge);
						Node n = edge.getOtherNode(currentNode);
						if (ignoreVisitedNodes && visitedNodes.contains(n))
							continue;
						visitElement(edge);
						visitElement(n);
						visitedNodes.add(n);
						nextNodes.add(n);
					}
				}
			}
			currentNodes.clear();
			Set temp = currentNodes;
			currentNodes = nextNodes;
			nextNodes = temp;
		}
	}
}