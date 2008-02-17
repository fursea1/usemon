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
 * This factory class provides access to a graph system.
 * A new GraphSystem object is created by specifying the
 * class of the GraphSystem interface implementation and
 * the initialisation properties to be used for it.
 * The interface implementation class may be specified directly
 * (using a Class object), by name or as by specifying the
 * class name as a value of the property
 * <code>hypergraph.graphapi.graphsystem</code> in the initialisation
 * properties.
 */

public final class GraphSystemFactory {

	/** Private constructor to prevent this class from being instantiated. */
	private GraphSystemFactory() {
	}

	/**
	 * Creates a new GraphSystem
	 * @param className The name of the Java class which implements
	 *                  the GraphSystem interface. This parameter
	 *                  overrides the hypergraph.graphapi.graphsystem property
	 *                  if it is also specified.
	 * @param props The initialisation properties for the GraphSystem.
	 * @throws GraphException A <code>GraphException</code> if the GraphSystem couldn't be instantiated.
	 * @return An implementation of the GraphSystem interface.
	 */
	public static GraphSystem createGraphSystem(String className, java.util.Properties props) throws GraphException {
		try {
			Class klass = Class.forName(className);
			return createGraphSystem(klass, props);
		} catch (ClassNotFoundException ex) {
			throw new GraphException("Cannot find class for GraphSystem. " + ex.getMessage());
		}
	}

	/**
	 * Creates a new GraphSystem
	 * @param klass The  Java class which implements
	 *              the GraphSystem interface. This parameter
	 *              overrides the hypergraph.graphapi.graphsystem property
	 *              if it is also specified.
	 * @param props The initialisation properties for the TopicMapSystem.
	 * @throws GraphException A <code>GraphException</code> if the GraphSystem couldn't be instantiated.
	 * @return An implementation of the GraphSystem interface.
	 */
	public static GraphSystem createGraphSystem(Class klass, java.util.Properties props) throws GraphException {
		try {
			GraphSystem ret = (GraphSystem) klass.newInstance();
			ret.setProperties(props);
			return ret;
		} catch (IllegalAccessException ex) {
			throw new GraphException("Cannot instantiate GraphSystem class " + klass.getName() + " - " + ex.getMessage());
		} catch (InstantiationException ex) {
			throw new GraphException("Cannot instantiate GraphSystem class " + klass.getName() + " - " + ex.getMessage());
		}
	}

	/**
	 * Creates a new GraphSystem
	 * @param props The initialisation properties for the GraphSystem.
	 *              The property <code>hypergraph.graphapi.graphsystem</code>
	 *              must be defined and its value must be the name
	 *              of a Java class which implements the GraphSystem
	 *              interface.
	 * @throws GraphException A <code>GraphException</code> if the GraphSystem couldn't be instantiated.
	 * @return An implementation of the GraphSystem interface.
	 */
	public static GraphSystem createGraphSystem(java.util.Properties props) throws GraphException {
		String className = props.getProperty("hypergraph.graphapi.graphsystem", null);
		if (className == null)
			throw new GraphException("No value specified for property hypergraph.graphapi.graphsystem");
		return createGraphSystem(className, props);
	}
}