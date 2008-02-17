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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;

/**
 *
 * <p>
 * This class manages attributes for nodes and edges.
 * Attributes can be anything,
 * usually program dependent data will be stored as an attribute,
 * an example would be to attach a java.io.File object to a node
 * when this node represents a file. It is also used to attach attributes like
 * colour, size etc. to a node or edge.
 * </p>
 *
 * <p>
 * In addition, this class is able to manage default values which is very
 * useful in connection with colours.
 * </p>
 *
 * <p>
 * Each attribute has a special name of type String. This name has to be
 * unique to identify the attribute.
 * To store for example the colour of an edge and a node, two different
 * attribute names should be used, e.g. "EDGE_COLOUR" and "NODE_COLOUR".
 * If the same attribute (e.g. "COLOUR") is used, it is not possible to
 * have two different default colours.
 * </p>
 * <p>
 * Some examples :
 * <dl>
 * <dt><code>setAttribute(NODE_FOREGROUND,node,new Color(Color.GREEN))</code></dt>
 * <dd>This sets the node's foreground colour to green. Only this single node is affected.</dd>
 * <dt><code>setAttribute(NODE_FOREGROUND,node.getGroup(),new Color(Color.GREEN))</code></dt>
 * <dd>This sets the foreground colour of all nodes in the <code>node</code>'s group to green.
 * If a node that is in this group has another colour, say blue, attached, it will be blue.
 * Not only the nodes will be green, but also all elements that belong to this group, at least
 * they have the attribute <code>NODE_FOREGROUND</code> set to green.
 * Of course you would read the attribute <code>EDGE_FOREGROUND</code>
 * when it comes to painting edges.</dd>
 * <dt><code>setAttribute(NODE_FOREGROUND,graph,new Color(Color.GREEN))</code></dt>
 * <dd>This sets the attribute <code>NODE_FOREGROUND</code> for all elements on this
 * graph to green as default.</dd>
 * </dl>
 * <dt><code>getAttribute(NODE_FOREGROUND,node)</code></dt>
 * <dd>This gets the attribute <code>NODE_FOREGROUND</code> for the node. If there
 * is no attribute with this name, the method tries whether there is the appropriate
 * attribute for the group of the node and finally the default, i.e. the attribute for the graph.
 * </dd>
 * </dl>
 * </p>
 *
 * @author Jens Kanschik
 *
 */
public class AttributeManagerImpl implements AttributeManager {

	/**
	 * <code>attributesForName</code> maps the name of the attribute to another map. The value map
	 * again maps the elements to the actual attribute.
	 */
	private Map 	attributesForName;
	private Graph	graph;

	AttributeManagerImpl(Graph graph) {
		attributesForName = null;
		setGraph(graph);
	}

	/** Returns a list of the names of all attributes.
	 * This can be used for example for a frontend.
	 * @return A list of the names of all used attributes.
	 */
	public Collection getAttributeNames() {
		return attributesForName.keySet();
	}

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
	public Object getAttribute(String name, Element element) {
		if (attributesForName != null) {
			// attributesForName maps the name of an attribute to another map.
			Map attributeMap = (Map) attributesForName.get(name);
			if (attributeMap != null) {
				// first get the attribute for this element, if possible :
				Object attribute = attributeMap.get(element);
				if (attribute != null)
					return attribute;
				// there was no attribute for this element,
				// try attributes associated with groups :
				attribute = attributeMap.get(element.getGroup());
				if (attribute != null)
					return attribute;
				// neither on element level, nor on group level; try graph itself (default)
				attribute = attributeMap.get(getGraph());
				if (attribute != null)
					return attribute;
			}
		}
		// we don't have this attribute, return null :
		return null;
	}

	/** Sets the attribute for the element <code>element</code>.
	 * @param name The name of the attribute.
	 * @param element The element, for which the attribute is to be set.
	 * @param attribute The attribute.
	 */
	public void setAttribute(String name, Element element, Object attribute) {
		if (attributesForName == null)
			attributesForName = new HashMap();
		Map attributeList = (Map) attributesForName.get(name);
		if (attributeList == null) {
			attributeList = new HashMap();
			attributesForName.put(name, attributeList);
		}
		attributeList.put(element, attribute);
	}

	/**
	 * @return A string representation, containig a list of attributes.
	 */
	public String toString() {
    	String result = "[ AttributeManager : \n";
    	if (attributesForName != null)
	    	result += "  Attribute names : " + attributesForName.keySet() + " ]\n";
    	result += " ]\n";
    	return result;
	}

	/**
	 * Returns the graph.
	 * @return Graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Sets the graph.
	 * @param g The graph to set
	 */
	public void setGraph(Graph g) {
		this.graph = g;
	}

}
