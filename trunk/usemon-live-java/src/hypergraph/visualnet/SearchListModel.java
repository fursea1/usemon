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
package hypergraph.visualnet;

import javax.swing.*;
import java.util.*;

public class SearchListModel extends AbstractListModel {

    SortedMap dataBase;
    public SearchListModel(SortedMap dataBase) {
	if (dataBase == null)
	    dataBase = new TreeMap();
 	this.dataBase = dataBase;
	fireIntervalAdded(this,0,dataBase.size()-1);
   }
    public void setDataBase(SortedMap dataBase) {
	fireIntervalRemoved(this,0,this.dataBase.size()-1);
	this.dataBase = dataBase;
	fireIntervalAdded(this,0,dataBase.size()-1);
    }
    public Object getElementAt(int i) {
	Object[] keys = dataBase.keySet().toArray();
	return keys[i];
    }
    public int getSize() {
	return dataBase.size();
    }

}
