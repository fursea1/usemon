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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphSystem;

/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SAXReader {
	private GraphSystem graphSystem;
	private InputSource inputSource;
	private Graph graph;
	private LexicalHandler lexicalHandler;
	private XMLReader reader;
	private ContentHandlerFactory contentHandlerFactory;

	public SAXReader(GraphSystem graphSystem, String filename) throws FileNotFoundException, IOException{
		this.graphSystem = graphSystem; 
		inputSource = new InputSource(filename);
		createLexicalHandler();
	}
	public SAXReader(GraphSystem graphSystem, URL url) throws IOException {
		this.graphSystem = graphSystem;
		inputSource = new InputSource(url.openStream());
		inputSource.setSystemId(url.toString());
		createLexicalHandler();
	}
	void createLexicalHandler() {
		lexicalHandler = new DefaultLexicalHandler();
	}
	public GraphSystem getGraphSystem() {
		return graphSystem;
	}
	public XMLReader getReader() {
		return reader;
	}
	public void setContentHandlerFactory(ContentHandlerFactory contentHandlerFactory) {
		this.contentHandlerFactory = contentHandlerFactory;
	}
	public ContentHandlerFactory getContentHandlerFactory() {
		return contentHandlerFactory;
	}
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	public Graph getGraph() {
		return graph;
	}
	public Graph parse() throws SAXException, IOException, ParserConfigurationException {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		reader = parser.getXMLReader();
		lexicalHandler.setSAXReader(this);
		reader.setProperty("http://xml.org/sax/properties/lexical-handler", lexicalHandler);
		reader.parse(inputSource);
		return getGraph();
	}
}
