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

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface GraphSystem {

	public static final int FORMAT_GML 			= 0;
	public static final int FORMAT_GRAPHXML		= 1;
	public static final int FORMAT_DOT			= 2;

	Graph createGraph();
	
	Graph readGraph(InputStream is, int format);
	
	Graph readGraph(File file);
	
	/**
	 * This method is the service-provider interface
	 * for the GraphSystem. This method is invoked
	 * by the GraphSystemFactory class when a new
	 * GraphSystem is instantiated.
	 *
	 * A GraphSystem may have predefined properties which can be overwritten
	 * by these properties.
	 *
	 * @param props The properties to be used to initialise the
	 *              system.
	 */
	public void setProperties(Properties props);
}
