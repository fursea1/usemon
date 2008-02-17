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

/**
 * @author Jens Kanschik
 */
public interface AttributeManager {

	/** The attribute name for the root of a tree (if applicable). */
	public static final String GRAPH_ROOT = "GRAPH_ROOT";

	/** 
	 * Gets a collection of the names of all attributes that are used
	 * by this attribute manager resp. an element of the associated graph.
	 * @return A collection of attribute names.
	 */
	public Collection getAttributeNames();

	/**
	 * Gets the attribute with the name <code>name</code>
	 * for the element <code>element</code>. It first looks, whether there is an attribute attached
	 * directly to the element, because this is the most important and overwrites all other
	 * attributes. If there is none, check whether there is an attribute
	 * attached to the group of the element and return it. As a last try, the attribute for
	 * the entire graph is read. This is the default value.
	 * If at no level an attribute could be found, <code>null</code> is returned.
	 *
	 * @return  The attribute with name <code>name</code> for <code>element</code>
	 * @param name The name of the attribute.
	 * @param element The element the attribute is attached to.
	 */
	public Object getAttribute(String name, Element element);

	/**
	 * <p>
	 * Sets the attribute for the element <code>element</code>.
	 * </p>
	 *
	 * @param name The name of the attribute.
	 * @param element The element, for which the attribute is to be set.
	 * @param attribute The attribute.
	 */
	public void setAttribute(String name, Element element, Object attribute);
}
