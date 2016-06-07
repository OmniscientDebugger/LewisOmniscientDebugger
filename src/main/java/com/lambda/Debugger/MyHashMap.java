package com.lambda.Debugger;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MyHashMap extends HashMap {

    public MyHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public MyHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MyHashMap() {
        super();
    }

    public MyHashMap(Map c) {
        putAll(c);
    }

    public synchronized Object put(Object key, Object value) {
        Object o = super.put(key, value);
        D.hashMapPut(1, this, key, value);
        return o;
    }

    public synchronized Object remove(Object key) {
        Object o = super.remove(key);
        D.hashMapRemove(1, this, key);
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
        D.hashMapClear(1, this);
    }

    public Object clone() {
        return new MyHashMap(this);
    }

}
