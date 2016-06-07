/*                        MyArrayList.java

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


package com.lambda.Debugger;

import java.util.*;


public class MyArrayList extends  ArrayList 
{

    public MyArrayList(int initialCapacity) {
	super(initialCapacity);
    }

    public MyArrayList() {
	super();
    }

    public MyArrayList(Collection c) {
	super(c);
	int size = c.size();
	Object[] objs = c.toArray();
	Shadow.record(this, true);
    }

    public Object clone() {
	    MyArrayList v = (MyArrayList)super.clone();
	    Shadow.record(v, true);
	    return v;
    }

    public Object[] toArray() {
	Object[] result = super.toArray();
	Shadow.record(result, true);
	return result;
    }

    public Object[] toArray(Object a[]) {
	a = super.toArray(a);
	Shadow.record(a, true);
        return a;
    }

    public Object set(int index, Object element) {
	Object oldValue = super.set(index, element);
	D.arraylistChange(1, this, index, element);
	return oldValue;
    }

    public boolean add(Object o) {
	super.add(o);
	D.arraylistInsert(1, this, size()-1, o);
	return true;
    }

    public void add(int index, Object element) {
	super.add(index,element);
	D.arraylistInsert(1, this, index, element);
    }

    public Object remove(int index) {
	Object oldValue = super.remove(index);
	D.arraylistRemove(1, this, index, 1);
	return oldValue;
    }

    public void clear() {
	D.arraylistRemove(1, this, 0, size());
	super.clear();
    }

    public boolean addAll(Collection c) {
	boolean z = addAll(size(), c);
	return z;
    }

    public boolean addAll(int index, Collection c) {
	boolean z = super.addAll(index, c);
	int numNew = c.size();

	Iterator e = c.iterator();
	for (int i=0; i<numNew; i++) {
	    Object element = e.next();
	    D.arraylistInsert(1, this, index, element);
	}
	return z;
    }



    //    private synchronized void readObject(java.io.ObjectInputStream s)  PROBLEM!
}
