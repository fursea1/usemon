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

package hypergraph.hyperbolic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

/**
 * The class <code>PropertyManager</code> stores properties like renderer class names,
 * default text size etc. in a <code>Hashtable</code>.
 * A certain set of properties are set to default values. They can be overwritten
 * by user defined values using the method <code>setProperty</code>.
 * @author Jens Kanschik
 */
public class PropertyManager {
	/** Stores all properties, default and user defined. */
	private Properties properties = initProperties();
	/** Stores the default properties */
	private Properties defaultProperties;

	/**
	 * Creates the <code>propertyMap</code> and sets the basic properties.
	 * @return The propertyMap.
	 */
	private Properties initProperties() {
		defaultProperties = new Properties();
		defaultProperties.put("model.name", "Poincare model");
		defaultProperties.put("model.class", "hypergraph.hyperbolic.PoincareModel");
		defaultProperties.put("projector.class", "hypergraph.hyperbolic.PoincareProjector");
		defaultProperties.put("linerenderer.class", "hypergraph.hyperbolic.DefaultLineRenderer");
		defaultProperties.put("textrenderer.class", "hypergraph.hyperbolic.DefaultTextRenderer");
		defaultProperties.put("hypergraph.hyperbolic.background.color", "white");
		defaultProperties.put("hypergraph.hyperbolic.text.color", "black");
		return new Properties(defaultProperties);
	}

	/**
	 * Returns the property with name <code>name</code>
	 * @param name The name of the property.
	 * @return The property.
	 */
	public Object getProperty(String name) {
		Object property = properties.get(name);
		if (property == null)
			property = properties.getProperty(name);
		return property;
	}
	/**
	 * Sets a property with the given name. If the property with this name has already
	 * been set before, it returns the old value.
	 * @param name The name of the property.
	 * @param property The new value of the property.
	 * @return The old value of the property.
	 */
	public Object setProperty(String name, Object property) {
		return properties.put(name, property);
	}
	/**
	 * Retrieves a class, usually a renderer.
	 * The parameter is the propertyname of the classname.
	 * If the class has already been loaded before, it's read directly from
	 * the propertymap. Otherwise it's loaded and saved in the map using the classname
	 * as a key.
	 *
	 * @param name The property name of the class.
	 * @return The class.
	 * @throws ClassNotFoundException
	 * Thrown if the specified class could not be loaded by the ClassLoader.
	 */
	public Class getClass(String name) throws ClassNotFoundException {
		String className = getString(name);
		Class cls = null;
		try {
			cls = (Class) getProperty(className);
			if (cls != null)
				return cls;
		} catch (ClassCastException cce) {
		}
		cls = Class.forName(className);
		if (cls != null)
			properties.put(className, cls);
		return cls;
	}
	/**
	 * Type-safe version of <code>getProperty(String)</code>. If the property can be casted
	 * to a <code>String</code>, it is returned. If not, <code>null</code> is returned.
	 * @param name The name of the property.
	 * @return The value of the property if it is a String.
	 */
	public  String getString(String name) {
		Object value = getProperty(name);
		return (value instanceof String) ? (String) value : null;
	}
	/**
	 * Type-safe version of <code>getProperty(String)</code>. If the property can be casted
	 * to a <code>Double</code>, it is returned. If the property is a <code>String</code>,
	 * is is parsed and the resulting Double is returned.
	 * In any other case or if the <code>String</code> cannot be parsed, the method returns the <code>defaultValue</code>.
	 *
	 * @param name The name of the property.
	 * @param defaultValue The default value for this property.
	 * This value is returned, if the property could not be found or if the property cannot be inerpreted as a <code>Double</code>.
	 * @return The value of the property if it's set, otherwise the default value.
	 */
	public  Double getDouble(String name, Double defaultValue) {
		Object value = getProperty(name);
		if (value instanceof Double)
			return (Double) value;
		try {
			if (value instanceof String)
				return new Double((String) value);
		} catch (Exception e) {
		}
		return defaultValue;
	}



	/**
	 * Prints the property list out to the specified output stream.
	 * @param out an output stream
	 */
	public void list(PrintStream out) {
		properties.list(out);
	}

	/**
	 * Prints the property list out to the specified output stream.
	 * @param out an output stream
	 */
	public  void list(PrintWriter out) {
		properties.list(out);
	}

	/**
	 * @param inStream
	 * @throws java.io.IOException
	 */
	public  synchronized void load(InputStream inStream) throws IOException {
		properties.load(inStream);
	}

	/**
	 * @return
	 */
	public  Enumeration propertyNames() {
		return properties.propertyNames();
	}

	/**
	 * @param out
	 * @param header
	 * @throws java.io.IOException
	 */
	public  synchronized void store(OutputStream out, String header)
		throws IOException {
		properties.store(out, header);
	}

}
