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
 * Represents a group of different elements;
 * value of the property <code>group</code> of an <code>Element</code>.
 * <p>
 * In some situations it is necessary or useful to group several elements of a graph.
 * This can be done with this class. Every single element has the property <code>group</code>.
 * If two elements have the same value of this property, they belong to the same group.
 * Since the group is stored as the value of a property, each element can at most belong to one
 * group. Since <code>Group</code> is an extension of <code>Element</code>,
 * each group can again belong to a group,
 * thus building up a hierarchival structure of groups.
 * The group itself doesn't have any further properties.
 * </p>
 * <p>
 * Say, you have a large graph which you want to cluster, either using some clustering algorithms
 * or some properties that define the cluster. Then you would create an instance of <code>Group</code>
 * or some suitable subclass representing each of the clusters and set the group property of each element
 * accordingly. Attaching attributes like colours to certain elements can also be done using
 * groups, i.e. you can attach an attribute to a group and all elements of this group
 * will have the same attributes as long as they aren't overwritten. For more information
 * about attributes, see <code>AttributeManager</code>.
 * </p>
 *
 * @see AttributeManager
 *
 * @author Jens Kanschik
 */
public interface Group extends Element {

}