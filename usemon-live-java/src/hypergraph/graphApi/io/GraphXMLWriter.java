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
import hypergraph.graphApi.Group;
import hypergraph.graphApi.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class GraphXMLWriter extends AbstractGraphWriter {
	public GraphXMLWriter(String fileName) throws IOException {
		super(fileName);
	}
	public GraphXMLWriter(File outputFile) throws IOException {
		super(outputFile);
	}
	public GraphXMLWriter(Writer writer) throws IOException {
		super(writer);
	}

	public void writeHeader() throws IOException {
		writer.write("<?xml version=\"1.0\"?>\n");
		writer.write("<!DOCTYPE GraphXML SYSTEM \"GraphXML.dtd\">\n");
		writer.write("<!--This file has been automatically created by HyperGraph.-->\n");
		writer.write("<GraphXML>\n");
		writer.write("  <graph>\n");
	}
	public void writeGroup(Group group) throws IOException {}
	public void writeNode(Node node) throws IOException {
		writer.write(
			"    <node " + 
			"name=\"" + node.getName() + "\" ");
		if (node.getGroup() != null) 
			writer.write("class=\"" + node.getGroup().getName() + "\" ");
		writer.write(">\n");
		writer.write("      <label>" + node.getLabel() + "</label>\n");
		writer.write("    </node>\n");
	}
	public void writeEdge(Edge edge) throws IOException {
		writer.write(
			"    <edge " + 
			"name=\"" + edge.getName() + "\" " + 
			"source=\"" + edge.getSource().getName() + "\" " +
			"target=\"" + edge.getTarget().getName() + "\" ");
		if (edge.getGroup() != null) 
			writer.write("class=\"" + edge.getGroup().getName() + "\" ");
		writer.write(">\n");

		if (edge.getLabel() != null)
			writer.write("      <label>" + edge.getLabel() + "</label>\n");
		writer.write("    </edge>\n");
	}
	public void writeFooter() throws IOException {
		writer.write("  </graph>\n");
		writer.write("</GraphXML>\n");
		writer.close();
	}
}
