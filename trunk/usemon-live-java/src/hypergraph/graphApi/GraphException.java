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

/**
 * This exception (and possible subclasses) is used to report errors
 * in connection with graph algorithms. For example the methods of {@link Graph}
 * that create new objects with a given name throw such an exception
 * if the name is already used by another element.
 *
 * @author Jens Kanschik
 */
public class GraphException extends Exception {

	/** <code>GraphException</code> can be used to wrap other exception.
	 * In this case, this variable contains the original exception.*/
	private Exception wrapped;

	/** Creates a new <code>GraphException</code> with a given message.
	 * @param message The message for this exception.
	 */
	public GraphException(String message) {
		super(message);
	}
	/** Create a new <code>GraphEception</code> that wraps another exception.
	 * @param message The message for this exception.
	 * @param e The wrapped exception.
	 */
	public GraphException(String message, Exception e) {
		super(message);
		wrapped = e;
	}

}
