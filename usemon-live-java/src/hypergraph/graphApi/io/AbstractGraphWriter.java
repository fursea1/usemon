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

package hypergraph.graphApi.io;

import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.Group;
import hypergraph.graphApi.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public abstract class AbstractGraphWriter implements GraphWriter {
	/** Writer into which the graph is written. **/
	protected Writer writer;

	/** The graph that is going to be written. */
	protected Graph graph;

	public AbstractGraphWriter(Writer writer) throws IOException {
		setWriter(writer);
	}
	public AbstractGraphWriter(String fileName) throws IOException {
		setWriter(new FileWriter(new File(fileName)));
	}
	public AbstractGraphWriter(File outputFile) throws IOException {
		setWriter(new FileWriter(outputFile));
	}
	
	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public abstract void writeHeader() throws IOException;
	public abstract void writeGroup(Group group) throws IOException;
	public abstract void writeNode(Node node) throws IOException;
	public abstract void writeEdge(Edge edge) throws IOException;
	public abstract void writeFooter() throws IOException;
	
	public void write(Graph graph) throws IOException {
		this.graph = graph;
		writeHeader();
		writer.write("  <!--Group section-->\n");
		for (Iterator i = graph.getGroups().iterator(); i.hasNext(); ) {
			Group group = (Group) i.next();
			writeGroup(group);
		}
		writer.write("  <!--Node section-->\n");
		for (Iterator i = graph.getNodes().iterator(); i.hasNext(); ) {
			Node node = (Node) i.next();
			writeNode(node);
		}
		writer.write("  <!--Edge section-->\n");
		for (Iterator i = graph.getEdges().iterator(); i.hasNext(); ) {
			Edge edge = (Edge) i.next();
			writeEdge(edge);
		}
		writeFooter();
	}
}
