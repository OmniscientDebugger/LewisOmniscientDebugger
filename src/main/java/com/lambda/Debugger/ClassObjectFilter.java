/*                        ClassObjectFilter.java

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

//              ClassObjectFilter/ClassObjectFilter.java

/*
 */


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ClassObjectFilter {

  static boolean 		DEBUG = false;
  static HashMap		table = new HashMap();	// "frob" -> <Set{Object, MyThing, ...}>, "twock" -> <Set{"*"}>, "tik" -> <Set{"Internals"}>, 
  static private MethodLine 	firstLine, lastLine;


    public static VectorD getFilteredMethods() {
	VectorD v = new VectorD();
	Iterator iter =  table.keySet().iterator();
	while (iter.hasNext()) {
	    String key = (String) iter.next();
	    HashSet values = (HashSet) table.get(key);
	    Iterator iter2 =  values.iterator();
	    while (iter2.hasNext()) {
		String s;
		Object o = iter2.next();
		if (o instanceof Class)
		    s = ((Class) o).getName();
		else
		    s = (String) o;
		v.add(new String[] {s, key});
	    }
	}
	return v;
    }


  public static void put(Class clazz, String method) {		// It's a set, so repeated add()'s are OK.
    Set set = (Set)table.get(method);
    if (set == null) {
      set = new HashSet();
      set.add(clazz);
      table.put(method, set);
      return;
    }
    set.add(clazz);
  }

  public static void put(String method) {		// It's a set, so repeated add()'s are OK.
    Set set = (Set)table.get(method);
    if (set == null) {
      set = new HashSet();
      set.add("*");
      table.put(method, set);
      return;
    }
    set.add("*");
  }
	  
  public static void put(String method, String s) {		// It's a set, so repeated add()'s are OK.
    Set set = (Set)table.get(method);
    if (set == null) {
      set = new HashSet();
      set.add(s);
      table.put(method, set);
      return;
    }
    set.add(s);
  }
	    
	
  public static void put(String s, MethodLine ml) {
    if (s.equals("First")) firstLine = ml;
    if (s.equals("Last")) lastLine = ml;
  }

  public static void clear(String s) {
    if (s.equals("First")) firstLine = null;
    if (s.equals("Last")) lastLine = null;
  }

  public static MethodLine getFirst() {
    return firstLine;
  }
  public static MethodLine getLast() {
    return lastLine;
  }
	
  public static boolean contains(Class clazz, String method) {
    Set set = (Set)table.get("*");
    if (set != null) {
      if (set.contains(clazz)) return true;
    }
    set = (Set)table.get(method);
    if (set == null) return false;
    return( set.contains(clazz) || set.contains("*") || set.contains("Internals") );
  }

	
  public static boolean internals(String method) {
    Set set = (Set)table.get(method);
    if (set == null) return false;
    return(set.contains("Internals"));
  }
    

  public static void clear() {
    table.clear();
    lastLine = firstLine = null;
  }

  public String toString() {
    return("<ClassObjectFilter>");
  }

    

  public static void main(String[] args) {
    D.println("Should be false: " + contains( Object.class, "frob1"));
    put( Object.class, "frob1");
    put( ClassObjectFilter.class, "frob2");
    D.println("Should be false: " + contains( ClassObjectFilter.class, "frob1"));
    D.println("Should be true:  " + contains( ClassObjectFilter.class, "frob2"));
    D.println("Should be true:  " + contains( Object.class, "frob1"));
    put( Object.class, "frob2");
    D.println("Should be true:  " + contains( Object.class, "frob2"));
  }

}
