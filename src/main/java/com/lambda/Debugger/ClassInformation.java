/*                        ClassInformation.java

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

//              ClassInformation.java

/*
 */


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;


public class ClassInformation {
    static private ClassInformation	arrayCI, vectorCI, arraylistCI, hashtableCI, hashMapCI;
    static private HashMapEq		table = new HashMapEq(20);		// Class -> <CI varNames>
    static private HashMapEq		classTable = new HashMapEq(200);	// Class -> <CI staticVarNames>
    static private HashMapEq		usfTable = new HashMapEq(20);		// ClassName -> fieldName

    public String			className;				// "com.lambda.Debugger.Demo"
    public Class	 		clazz;
    protected String 			userSelectedField = null;
    private String[]			varNames;
    private Field[]			varFields;
    private Class[]			varClasses;
    private int				id=0;







    public int nextID() {
	return id++;
    }

    static {initialize();}

    public ClassInformation(Class c, String[] vn, Field[] fs, Class[] cs) {
	varFields = fs;
	varClasses = cs;
	varNames = vn;
	clazz = c;
	className=clazz.getName().intern();
    }

    public int size() {return varNames.length;}

    public boolean isArray() {
	return (this == arrayCI) ;
    }
    public boolean isVector() {
	return (this == vectorCI) ;
    }
    public boolean isArrayList() {
	return (this == arraylistCI) ;
    }
    public boolean isHashtable() {
        return (this == hashtableCI) ;
    }
    public boolean isHashMap() {
        return (this == hashMapCI) ;
    }

    public int getnInstanceVars() {
	if (isArray()) return 0;
	if (isVector()) return 0;
	if (isArrayList()) return 0;
        if (isHashtable()) return 0;
        if (isHashMap()) return 0;
	return varNames.length;
    }


    public Class getVarClass(int i) {
	return varClasses[i];
    }

    public Field getVarField(int i) {
	return varFields[i];
    }

    public String[] getVarNames() {
	return varNames;
    }


    public String getVarName(int i) {
	if (isArray()) return ""+i;
	if (isVector()) return ""+i;
	if (isArrayList()) return ""+i;
        if (isHashtable()) return ""+i;
        if (isHashMap()) return ""+i;
	if (i >= varNames.length) throw new DebuggerException("getVarName("+i+") " + this);
	return varNames[i];
    }

    public int getVarIndex(String s) {
	if (varNames == null) return -1;
	int len = varNames.length;
	for (int j = 0; j < len; j++) {
	    if (varNames[j] == s) return(j);
	}
	//	throw new DebuggerException("gvi " + s);
	return -1;// If a variable is private from an inherited class
    }


    public static void initialize() {
	arrayCI = new ClassInformation(Object[].class, null, null, null);
	vectorCI = new ClassInformation(MyVector.class, null, null, null);
	arraylistCI = new ClassInformation(MyArrayList.class, null, null, null);
        hashtableCI = new ClassInformation(Hashtable.class, null, null, null);
        hashMapCI = new ClassInformation(HashMap.class, null, null, null);
	Class c;
	c = Object[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = int[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = int[][].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = short[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = byte[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = char[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = boolean[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = long[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = double[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = float[].class; table.put(c, arrayCI); classTable.put(c, arrayCI);
	c = MyVector.class; table.put(c, vectorCI); classTable.put(c, vectorCI);
	c = MyArrayList.class; table.put(c, arraylistCI); classTable.put(c, arraylistCI);
	c = LocksList.class; table.put(c, vectorCI); classTable.put(c, vectorCI);
        c = MyHashtable.class; table.put(c, hashtableCI); classTable.put(c, hashtableCI);
        c = MyHashMap.class; table.put(c, hashMapCI); classTable.put(c, hashMapCI);
    }
  
    public static ClassInformation get(Object o) {
	Class c;
	ClassInformation ci;
	boolean isClass = false;

	if (o instanceof Class) {
	    isClass = true;
	    c = (Class) o;
	    ci = (ClassInformation) classTable.get(c);
	}
	else {
	    if (o instanceof Object[]) return arrayCI;
	    c = o.getClass();
	    ci = (ClassInformation) table.get(c);
	}

	if (ci != null) return ci;

	Field[] f;
	int nFields = 0, nsFields = 0, index = 0, sindex = 0;
	String[] varNames;
	String[] staticVarNames;
	Field[] varFields;
	Field[] staticVarFields;
	Class[] varClasses;
	Class[] staticVarClasses;

	{
	    f = c.getFields();

		Comparator comp = new Comparator() {public int compare(Object o1, Object o2) {
					Field f1 = (Field) o1, f2 = (Field) o2;
					String s1 = f1.getName(), s2 = f2.getName();
					return s1.compareTo(s2);
					}
				};
		Arrays.sort(f, comp);
	    int len = f.length;
	    for (int i = 0; i< len; i++) {if (Modifier.isStatic(f[i].getModifiers())) nsFields++;}
	    nFields = len-nsFields;
	    varNames = new String[nFields];
	    staticVarNames = new String[nsFields];
	    varFields = new Field[nFields];
	    staticVarFields = new Field[nsFields];
	    varClasses = new Class[nFields];
	    staticVarClasses = new Class[nsFields];
	    for (int i = 0; i< len; i++) {
		String n = f[i].getName().intern();		    
		Class cl = f[i].getType();		    
		if (! Modifier.isStatic(f[i].getModifiers())) {
		    varNames[index] = n;
		    varFields[index] = f[i];
		    varClasses[index] = cl;
		    index++;
		}
		else {
		    staticVarNames[sindex] = n;
		    staticVarFields[sindex] = f[i];
		    staticVarClasses[sindex] = cl;
		    sindex++;
		}
	    }

	    ci = new ClassInformation(c, varNames, varFields, varClasses);
	    String fieldName = (String)usfTable.get(ci.className);
	    if (fieldName != null) ci.addUserSelectedField(fieldName);
	    ClassInformation sci = new ClassInformation(c, staticVarNames, staticVarFields, staticVarClasses);
	    table.put(c, ci);
	    classTable.put(c, sci);
	    if (isClass)
		return sci;
	    else
		return ci;
	}
    }


    public void setUserSelectedField(String name) {// Used by Launch for Demo only.
	userSelectedField = name;
	ClassInformation ci = (ClassInformation) table.get(clazz);// Assumes get() called with Class Object
	ci.userSelectedField = name;
    }
    

    public static void addUserSelectedField(String className, String fieldName) {
	usfTable.put(className.intern(), fieldName);
    }
    public static VectorD getUserSelectedFields() {
	VectorD v = new VectorD();
	Iterator iter =  usfTable.keySet().iterator();
	while (iter.hasNext()) {
	    String key = (String) iter.next();
	    String value = (String) usfTable.get(key);
	    v.add(new String[] {key, value});
	}
	return v;
    }

    public void addUserSelectedField(String fieldName) {
	userSelectedField = fieldName.intern();
	}
    

    public static void main(String[] args) {
	System.out.println("-------------CI test-----------");
	get(int[].class);
	get(int[][].class);
	get(Object[].class);
	get(String[].class);
	get(ClassInformation[].class);
	get(ClassInformation.class);
	get(Class.class);
	get(Thread.class);
	get(Shadow.class);
    }

    public String toString() {
	String s = "[";
	if (varNames != null)
	    for (int i = 0; i < varNames.length; i++) {s+=varNames[i]+", ";}
	return "<ClassInformation: "+ clazz +s + "]>";
    }

}
