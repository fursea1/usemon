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

import org.xml.sax.SAXException;


/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultLexicalHandler implements LexicalHandler {
	SAXReader reader;
	ContentHandlerFactory factory;
	public DefaultLexicalHandler() {
		this(null);
	}
	public DefaultLexicalHandler(SAXReader reader) {
		this(reader,null);
	}
	public DefaultLexicalHandler(SAXReader reader, ContentHandlerFactory factory) {
		this.reader = reader;
		this.factory = factory;
	}
	public void setSAXReader(SAXReader reader) {
		this.reader = reader;
	}
	public void startDTD(String name, String publicID, String systemID) throws SAXException {
		ContentHandler contentHandler;
		ContentHandlerFactory factory = this.factory;
		if (factory == null)
			factory = reader.getContentHandlerFactory();
		if (factory == null)
			factory = new ContentHandlerFactory();
		contentHandler = factory.createContentHandler(name);
		contentHandler.setReader(reader);
		reader.getReader().setContentHandler(contentHandler);
	}
	public void endDTD() throws SAXException {
	}
	public void startEntity(String name) throws SAXException {
	}
	public void endEntity(String name) throws SAXException {
	}
	public void startCDATA() throws SAXException {
	}
	public void endCDATA() throws SAXException {
	}
	public void comment(char[] ch,int start, int length) throws SAXException {
	}
}