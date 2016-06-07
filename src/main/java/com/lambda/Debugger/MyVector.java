/*                        MyVector.java

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

public class MyVector extends Vector {

    public Object clone() {
        Object o = super.clone();
        Shadow.record(o);
        return o;
    }

    public MyVector(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    public MyVector(int initialCapacity) {
        this(initialCapacity, 0);
    }

    public MyVector() {
        this(10);
    }

    public MyVector(Collection c) {
        super(c);
        // elementCount = c.size();
        // for (int i = 0; i < elementCount; i++) D.vectorChange(0, this, i,
        // elementData[i]);
        Shadow.record(this, true);
    }

    public synchronized Object[] toArray() {
        Object[] a = new Object[size()];
        return toArray(a);
    }

    public synchronized Object[] toArray(Object[] a) {
        Object[] a1 = super.toArray(a);
        Shadow.record(a1);
        return a;
    }

    public synchronized void copyInto(Object anArray[]) {
        super.copyInto(anArray);
        Shadow.record(anArray);
    }

    public synchronized void setElementAt(Object obj, int index) {
        super.setElementAt(obj, index);
        D.vectorChange(1, this, index, obj);
    }

    public synchronized void removeElementAt(int index) {
        super.removeElementAt(index);
        D.vectorRemove(1, this, index, 1);
    }

    public synchronized void insertElementAt(Object obj, int index) {
        super.insertElementAt(obj, index);
        D.vectorInsert(1, this, index, obj);
    }

    public synchronized void addElement(Object obj) {
        super.addElement(obj);
        D.vectorInsert(1, this, elementCount - 1, obj);
    }

    public synchronized void removeAllElements() {
        D.vectorRemove(1, this, 0, elementCount);
        super.removeAllElements();
    }

    public void setSize(int newSize) {
        if (newSize == elementCount)
            return;
        if (newSize > elementCount) {
            for (int i = elementCount; i < newSize; i++) {
                add(null);
            }
        } else {
            removeRange(newSize, elementCount - 1);
        }
        elementCount = newSize;
    }

    public synchronized Object set(int index, Object element) {
        Object o = super.set(index, element);
        D.vectorChange(1, this, index, element);
        return o;
    }

    public synchronized boolean add(Object o) {
        boolean b = super.add(o);
        D.vectorInsert(1, this, elementCount - 1, o);
        return b;
    }

    public synchronized void add(int index, Object o) {
        super.add(index, o);
        D.vectorInsert(1, this, index, o);
    }

    public synchronized boolean addAll(int index, Collection c) {
        boolean z = super.addAll(index, c);
        int numNew = c.size();

        Iterator e = c.iterator();
        for (int i = 0; i < numNew; i++) {
            Object o = e.next();
            D.vectorInsert(1, this, index++, o);
        }
        return z;
    }

    public synchronized boolean addAll(Collection c) {
        boolean b = addAll(size(), c);
        return b;
    }

    public synchronized Object remove(int index) {
        Object o = super.remove(index);
        D.vectorRemove(1, this, index, 1);
        return o;
    }

    // public synchronized boolean removeElement(Object obj) { CALLS
    // removeElementAt()
    // public boolean retainAll(Collection c) CALLS remove()

    // Where does this get used?
    protected void removeRange(int fromIndex, int toIndex) {
        // super.removeRange(fromIndex, toIndex);
        int numMoved = toIndex - fromIndex + 1;
        for (int i = 0; i < numMoved; i++)
            removeElementAt(fromIndex);
    }

    public synchronized List subList(int fromIndex, int toIndex) {
        return super.subList(fromIndex, toIndex); // Calls though add(), etc.
    }

}
