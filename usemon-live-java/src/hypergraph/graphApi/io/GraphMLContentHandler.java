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

import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphException;
import hypergraph.graphApi.Node;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GraphMLContentHandler extends DefaultHandler implements ContentHandler{

	private SAXReader reader;
	private Graph graph;
	private Element currentElement;
	private StringBuffer currentText;

	public void setReader(SAXReader reader) {
		this.reader = reader;
	}
	public SAXReader getReader() {
		return reader;
	}
	public void endDocument() throws SAXException {
		reader.setGraph(graph);
	}
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if (qName.equals("graph")) {
			graph = getReader().getGraphSystem().createGraph();
			return;
		}
		if (qName.equals("node")) {
			String name = null;
			for (int i = 0; i < atts.getLength(); i++)
				if (atts.getQName(i).equals("id")) {
					name = atts.getValue(i);
					continue;
				}
			try {
				currentElement = graph.createNode(name);
			} catch (GraphException ge) {
				ge.printStackTrace();
			}
			return;
		}
		if (qName.equals("edge")) {
			String name = null;
			Node source = null;
			Node target = null;
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getQName(i).equals("id")) {
					name = atts.getValue(i);
					continue;
				}
				if (atts.getQName(i).equals("source")) {
					String sourceName = atts.getValue(i);
					source = (Node) graph.getElement(sourceName);
					if (source == null)
						try {
							source = graph.createNode(sourceName);
						} catch (GraphException ge) {
							ge.printStackTrace();
						}
					continue;
				}
				if (atts.getQName(i).equals("target")) {
					String targetName = atts.getValue(i);
					target = (Node) graph.getElement(targetName);
					if (target == null)
						try {
							target = graph.createNode(targetName);
						} catch (GraphException ge) {
							ge.printStackTrace();
						}
					continue;
				}
			}
			try {
				currentElement = graph.createEdge(name, source, target);
			} catch (GraphException ge) {
				ge.printStackTrace();
			}
			return;
		}
		if (qName.equals("desc")) {
			currentText = new StringBuffer();
			return;
		}
	}
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equals("desc")) {
			if (currentText != null && currentElement != null) {
				if (currentElement instanceof Node) {
					((Node) currentElement).setLabel(currentText.toString());
				}
			}
		}
	}
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (currentText == null)
			currentText = new StringBuffer();
		currentText.append(ch, start, length);
	}
}
