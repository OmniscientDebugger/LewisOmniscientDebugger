/*                        Penumbra.java

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
import edu.insa.LSD.*;



public class Penumbra {
    public static String	NONAME = new String("NONAME");		// NOT interned
    static Hashtable		classInfos = new Hashtable();
    static Hashtable		names = new Hashtable();
    //    static WeakHashMap		classInfos = new WeakHashMap();
    //    static WeakHashMap		names = new WeakHashMap();
    static int 			id = 1000;


    public static void record(Object o) {
	String name = (String) names.get(o);
	if (name != null) return;
	names.put(o, NONAME);
    }



    public static Object[] getAllObjects() {
	ArrayList al = new ArrayList();
	Iterator iter =  names.keySet().iterator();
	while (iter.hasNext()) {
	    Object o = iter.next();
	    al.add(o);
	}
	Object[] os = new Object[al.size()];
	al.toArray(os);
	return os;
    }


    public static Object[] getAllObjects(Class c) {
	ArrayList al = new ArrayList();
	Iterator iter =  names.keySet().iterator();
	while (iter.hasNext()) {
	    Object o = iter.next();
	    if (o.getClass() == c) al.add(o);
	}
	Object[] os = new Object[al.size()];
	al.toArray(os);
	return os;
    }


    public static ClassInformation getClassInfo(Object o) {
	ClassInformation ci = (ClassInformation) classInfos.get(o);
	if (ci != null) return ci;
	ci = ClassInformation.get(o);
	classInfos.put(o, ci);
	return ci;
    }


    public static String getPrintName(Object o) {
	String name = (String) names.get(o);
	if ((name != null) && (name != NONAME)) return name;				// Never null
	name =  createPrintString(o);
	names.put(o, name);
	return name;
    }


    public static Iterator getIterator() {return names.keySet().iterator();}


    public static String createPrintString(Object obj) {
	if (obj instanceof Class) return createClassPrintString(obj);			// -> "int" "String" "int[][]"

	Class c = obj.getClass();
	String cs = createClassTypePrintString(obj);					// -> "int" "String" "int"

	if (c.isArray()) return createArrayPrintString(obj, cs);			//  -> "int[3]" "String[2][5][6]"

	if (obj instanceof Thread) return createThreadPrintString(obj);			// -> "<Thread-4>"	


	String tostring = "<" + cs + "_" + id++ + ">";				// <MyObject_123>
	return tostring;
    }


    private static String createClassPrintString(Object obj) {					// -> "int" "String" "int[][]"
	String s = ((Class)obj).getName();						// "I" "[I" "[[Ljava.lang.String;"
	if (s.endsWith(";")) s = s.substring(0, s.length()-1);			// "I" "[I" "[[Ljava.lang.String"

	while(s.startsWith("[")) s = s.substring(1, s.length()) + "[]";		// "I" "I[]" "Ljava.lang.String[][]"

	if (s.startsWith("Z")) return "boolean" + s.substring(1, s.length());	// "boolean" "boolean[]"
	if (s.startsWith("B")) return "byte" + s.substring(1, s.length());		// "byte" "byte[]"
	if (s.startsWith("C")) return "char" + s.substring(1, s.length());		// "char" "char[]"
	if (s.startsWith("S")) return "short" + s.substring(1, s.length());		// "short" "short[]"
	if (s.startsWith("I")) return "int" + s.substring(1, s.length());		// "int" "int[]"
	if (s.startsWith("J")) return "long" + s.substring(1, s.length());		// "long" "long[]"
	if (s.startsWith("F")) return "float" + s.substring(1, s.length());		// "float" "float[]"
	if (s.startsWith("D")) return "double" + s.substring(1, s.length());	// "double" "double[]"   

	int i = s.lastIndexOf('.');
	if (i != -1) return s.substring(i+1, s.length());				// Trim off packages "Llambda.Debugger.Debugger[]" -> "Debugger[]"
	if (s.endsWith("[]")) return s.substring(1, s.length());			// Trim off "L"  "LDebugger" -> "Debugger"
	return s;									// Trim off "L"  "LDebugger" -> "Debugger"
    }
	
    private static String createClassTypePrintString(Object obj) {					// -> "int" "String" "int"
	String s = obj.getClass().getName();						// "I" "[I" "[[Ljava.lang.String;"
	if (s.endsWith(";")) s = s.substring(0, s.length()-1);			// "I" "[I" "[[Ljava.lang.String"

	while(s.startsWith("[")) s = s.substring(1, s.length());			// "I" "I" "Ljava.lang.String"

	if (s.startsWith("Z")) return "boolean";
	if (s.startsWith("B")) return "byte";
	if (s.startsWith("C")) return "char";
	if (s.startsWith("S")) return "short";
	if (s.startsWith("I")) return "int";
	if (s.startsWith("J")) return "long";
	if (s.startsWith("F")) return "float";
	if (s.startsWith("D")) return "double";

	int i = s.lastIndexOf('.');
	if (i != -1) return s.substring(i+1, s.length());				// Trim off packages "Llambda.Debugger.Debugger" -> "Debugger"
	if (s.endsWith("[]")) return s.substring(1, s.length());			// Trim off "L"  "LDebugger" -> "Debugger"
	return s;									// Trim off "L"  "LDebugger" -> "Debugger"
    }
	


    private static String createArrayPrintString(Object obj, String cs) {				//  -> "int[3]" "String[2][5][6]"
	String tostring;
	if (obj instanceof int[][]) {
	    int[][] array = (int[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		int[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = "int[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof int[]) {
	    int[] array = (int[])obj;	// Try out this style.
	    tostring = "int[" + array.length + "]_"+id++;
	    return tostring;
	}
	if (obj instanceof short[][]) {
	    short[][] array = (short[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		short[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = "short[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof short[]) {
	    short[] array = (short[])obj;	// Try out this style.
	    tostring = "short[" + array.length + "]_"+id++;
	    return tostring;
	}
	if (obj instanceof byte[][]) {
	    byte[][] array = (byte[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		byte[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = "byte[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof byte[]) {
	    byte[] array = (byte[])obj;	// Try out this style.
	    tostring = "byte[" + array.length + "]_"+id++;
	    return tostring;
	}
	if (obj instanceof char[][]) {
	    char[][] array = (char[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		char[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = "char[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof char[]) {
	    char[] array = (char[])obj;	// Try out this style.
	    tostring = "char[" + array.length + "]_"+id++;
	    return tostring;
	}
	if (obj instanceof boolean[][]) {
	    boolean[][] array = (boolean[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		boolean[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = "boolean[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof boolean[]) {
	    boolean[] array = (boolean[])obj;	// Try out this style.
	    tostring = "boolean[" + array.length + "]_"+id++;
	    return tostring;
	}
	if (obj instanceof long[][]) {
	    long[][] array = (long[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		long[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = "long[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof long[]) {
	    long[] array = (long[])obj;	// Try out this style.
	    tostring = "long[" + array.length + "]_"+id++;
	    return tostring;
	}
	if (obj instanceof double[][]) {
	    double[][] array = (double[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		double[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = "double[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof double[]) {
	    double[] array = (double[])obj;	// Try out this style.
	    tostring = "double[" + array.length + "]_"+id++;
	    return tostring;
	}
	if (obj instanceof float[][]) {
	    float[][] array = (float[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		float[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = "float[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof float[]) {
	    float[] array = (float[])obj;	// Try out this style.
	    tostring = "float[" + array.length + "]_"+id++;
	    return tostring;
	}
	if (obj instanceof Object[][]) {
	    Object[][] array = (Object[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		Object[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring = cs+"[" + array.length + "]["+len2+"]_"+id++;
	    return tostring;
	}
	if (obj instanceof Object[]) {					// class [Ljava.lang.Object;@49cf9f
	    Object[] array = (Object[])obj;	// Try out this style.
	    tostring = cs+"[" + array.length + "]_"+id++;
	    return tostring;
	}
	return cs+"[n][n]?";
    }



    public static String createThreadPrintString(Object obj) {
	String s = ((Thread)obj).getName();
	String tostring;
	if ((s == null) || (s.length() == 0)) {s = "Unnamed Thread";}
	if (Character.isDigit(s.charAt(s.length()-1))) 				// Allow <Thread-2> etc.
	    tostring =  "<"+s+">";
	else
	    tostring  = "<"+s+"_"+id++ +">";
	return tostring;
    }

}
