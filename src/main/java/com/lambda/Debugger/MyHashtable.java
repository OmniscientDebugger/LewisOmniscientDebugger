/*                        MyHashtable.java

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
import java.util.Map.Entry;

public class MyHashtable extends Hashtable {

    public MyHashtable(int initialCapacity) {
        super(initialCapacity);
    }

    public MyHashtable(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MyHashtable() {
        super();
    }

    public MyHashtable(Map c) {
        putAll(c);
    }

    public synchronized Object put(Object key, Object value) {
        Object o = super.put(key, value);
        D.hashtablePut(1, this, key, value);
        return o;
    }

    public synchronized Object remove(Object key) {
        Object o = super.remove(key);
        D.hashtableRemove(1, this, key);
        return o;
    }

    public synchronized void putAll(Map t) {
        Set set = t.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Entry v = (Entry) it.next();
            put(v.getKey(), v.getValue());
        }
    }

    public synchronized void clear() {
        super.clear();
        D.hashtableClear(1, this);
    }

    public Object clone() {
        return new MyHashtable(this);
    }

    // Enumerator.remove() etc., not handled!
    // private synchronized void readObject(java.io.ObjectInputStream s)
    // PROBLEM!

    /*
     * 
     * public class MyEnumerator extends Enumerator { public void remove() {
     * super.remove(); } }
     */
}
