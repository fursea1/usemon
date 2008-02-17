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
import java.util.Map;
import java.util.Set;


/**
 * @author Jens Kanschik
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface Map2D {

	public interface Entry {
		public Object getValue();	
		public Object getKey1();
		public Object getKey2();
	}
	
	public void clear();
	public boolean containsKey(Object key1, Object key2);
	public boolean containsKey(Object key);
	public boolean containsValue(Object value);
	public Set entrySet();
	public Object get(Object key1, Object key2);
	public Map get(Object key1);
	public boolean isEmpty();
	public Set keySet();
	public Object put(Object key1, Object key2, Object value);
	public void putAll(Map2D map);
	public void remove(Object key1, Object key2);
	public void remove(Object key);
	public int size();
	public Collection values();
	
}
