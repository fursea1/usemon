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

package hypergraph.graph;

import hypergraph.graphApi.Group;

/** Default implementation of the interface <code>Group</code>.*/
public class GroupImpl  extends ElementImpl implements Group {
	/** Creates a new group with a given name.
	 * @param name The name of this group. */
	protected GroupImpl(String name) {
		super(name);
	}
	/** Implements the method from <code>Element</code> to return <code>GROUP_ELEMENT</code>.
	 * @return Always <code>GROUP_ELEMENT</code>.
	 */
	public int getElementType() {
		return GROUP_ELEMENT;
	}
}
