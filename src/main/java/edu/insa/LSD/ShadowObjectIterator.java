/*                        ShadowObjectIterator.java

  Copyright 2003, Bil Lewis

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA   
*/

package edu.insa.LSD;
import com.lambda.Debugger.*;
import java.util.*;

public class ShadowObjectIterator extends ObjectIterator implements Iterator {
    private RecordedState 	state;
    private int 		time;
    private ClassInformation	classInfo;
    private int			index;
    private Object[]		objects;

    public ShadowObjectIterator(RecordedState state) {
	super(state);
	this.time = state.getTime();
	//	this.iterator = Shadow.getIterator();
	ArrayList al = Shadow.getAllObjects();
	objects = new Object[al.size()];
	al.toArray(objects);
    }

    public ShadowObjectIterator(RecordedState state, Class c) {
	this(state);
	classInfo = ClassInformation.get(c);
	ArrayList al = Shadow.getAllObjects();
	for (int i = al.size(); i > -1; i--) if (al.get(i).getClass() != c) al.remove(i);
	objects = new Object[al.size()];
	al.toArray(objects);
    }

    public boolean hasMoreElements() {
	if (index == objects.length) return false;
	return true;
    }

    public Object nextElement() {
	Object o = objects[index];
	index++;
	return o;
    }

    /*
    public boolean hasMoreElements() {
	if (nextValue != null) return true;
	while(iterator.hasNext()) {
	    nextValue = iterator.next();
	    Shadow sh = (Shadow) nextValue;
	    if (time <= sh.creationTime) continue;
	    if (classInfo == null) return true;
	    if (sh.classInfo == classInfo) return true;
	}
	return false;
    }

    public Object nextElement() {
	if (nextValue  == null) {
	    if (!hasMoreElements()) throw new NoSuchElementException("MyHashtable Enumerator");
	}
	Shadow nv = (Shadow) nextValue;
	nextValue = null;
	hasMoreElements();
	return nv.obj;
    }
    */
    
}
