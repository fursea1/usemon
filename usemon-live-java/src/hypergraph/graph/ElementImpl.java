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

import hypergraph.graphApi.Element;
import hypergraph.graphApi.Group;

/**
 * <p>
 * This class is the default implementation of the interface <code>Node</code>.
 * </p>
 * @author Jens Kanschik
 */
public abstract class ElementImpl implements hypergraph.graphApi.Element {

	private Group group;
	private String name;

    protected ElementImpl(String name) {
    	this.name = name;
    }

    public boolean equals(Object o) {
   		return o != null && getName().equals(((Element) o).getName());
    }
    public int hashCode() {
    	return getName().hashCode();
    }
    public int compareTo(Object o) {
    	return getName().compareTo(((Element) o).getName());
    }

	public String getName() {
		return name;
	}

	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}

}