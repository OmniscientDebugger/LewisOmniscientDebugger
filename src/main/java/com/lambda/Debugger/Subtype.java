/*                        Subtype.java

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

//              Subtype.java

/*
 */


import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class Subtype {

  public static void overload(Subtype s) {}
  public static void overload(Object o) {}


  public static Method getDeclaredMethod(Class objClass, String methodName, Class[] argClasses) throws NoSuchMethodException {
    Method[] methods = objClass.getMethods();
    int len = methods.length;
    ArrayList possibleMethods = new ArrayList();

    for (int i = 0; i < len; i++) {
      if (argsMatch(methods[i], methodName, argClasses)) possibleMethods.add(methods[i]);
    }

    if (possibleMethods.size() == 0) throw new NoSuchMethodException();
    if (possibleMethods.size() > 1) {System.out.println("Multiple Matches: " + possibleMethods);}
    return((Method)possibleMethods.get(0));
  }

  public static boolean argsMatch(Method method, String name,  Class[] argClasses) {
    Class[] parameters = method.getParameterTypes();
    int len = parameters.length;
    
    if (!name.equals(method.getName())) return false;
    if (parameters.length != argClasses.length) return false;

    for (int i = 0; i < len; i++) {
      if (!subtype(argClasses[i], parameters[i])) return false;
    }
    return true;
  }



  // Does not deal with sub == short, char, byte, long, float double, boolean
  public static boolean subtype(Class sub, Class sup) {
    if (sub == sup) return true;
    if (sub == null) {
      if ((sup != int.class) && (sup != short.class) && (sup != byte.class) && (sup != char.class) &&
	  (sup != long.class) && (sup != double.class) && (sup != boolean.class) && (sup != float.class))
	return true;
      else
	return false;
    }
    if (sup == null) return false;

    Class[] interfaces = sub.getInterfaces();

    int len = interfaces.length;
    for (int i = 0; i < len; i++) {
      if (subinterface(interfaces[i], sup)) return true;
    }
    return subclass(sub, sup);
  }


  private static boolean subinterface(Class sub, Class sup) {
    if (sub == sup) return true;
    if (sup == null) return false;

    Class[] interfaces = sub.getInterfaces();

    int len = interfaces.length;
    for (int i = 0; i < len; i++) {
      if (subinterface(interfaces[i], sup)) return true;
    }
    return false;
  }

  private static boolean subclass(Class sub, Class sup) {
    if (sub == null) return false;
    Class directSup = sub.getSuperclass();

    if (sup == directSup) return true;
    return subclass(directSup, sup);
  }

 

  private static Method test(Class objClass, String methodName, Class[] argClasses, Class[] argClassesSub) {
    Method method, methodActual = null;

    try {methodActual = objClass.getDeclaredMethod(methodName, argClasses);}
    catch (NoSuchMethodException e) {
      System.out.println("No such method: " + objClass + "." + methodName + "( " + argClasses + ")");
    }
 
    try {method = Subtype.getDeclaredMethod(objClass, methodName, argClassesSub);}
    catch (NoSuchMethodException e) {
      System.out.println("Didn't find method: "  + objClass + "." + methodName + "( " + argClassesSub + ")");
      return null;
    }

    if (!method.equals(methodActual)) System.out.println("Different method: " +method + " " + methodActual);

    return method;
  }


  // C1 is-a C1, C1 is-a C2, C1 isn't-a C2, C1 is-a I1, C1 isn't-a I1, I1 is-a I2, I1 isn't-a I2 
  public static void main(String[] argv) {
    /*
    System.out.println("Subtype is-a Subtype (true): " + subtype(Subtype.class, Subtype.class));
    System.out.println("Subtype is-a Object (true): " + subtype(Subtype.class, Object.class));
    System.out.println("Object is-a Subtype (false): " + subtype(Object.class, Subtype.class));
    System.out.println("Vector is-a Subtype (false): " + subtype(Vector.class, Subtype.class));
    System.out.println("Subtype is-a Vector (false): " + subtype(Subtype.class, Vector.class));
    System.out.println("Vector is-a Object (true): " + subtype(Vector.class, Object.class));
    System.out.println("Vector is-a Collection (true): " + subtype(Vector.class, Collection.class));
    System.out.println("Collection is-a Vector (false): " + subtype(Collection.class, Vector.class));
    System.out.println("Collection is-a AbstractList (false): " + subtype(Collection.class, AbstractList.class));
    System.out.println("AbstractList is-a Collection (true): " + subtype(AbstractList.class, Collection.class));
    */

    short s = 0;
    long l = 0;
    char c = 0;
    byte b = 0;
    ArrayList v = new ArrayList();
    v.add(v);
    v.add(v);
    v.add(1, v);
    v.add(s, v);
    //    v.add(l, v);
    v.add(c, v);
    v.add(b, v);


    Class[] classes;
    Class[] classesActual;
    Method method;
    
    classesActual = new Class[1];
    classesActual[0] = Subtype.class;
    classes = new Class[1];
    classes[0] = Object.class;

    method = test(ArrayList.class, "add", classes, classes);
    System.out.println("ArrayList.add(Object) \t=> <ArrayList_1>.add(<Object_1>): " + method);

    method = test(ArrayList.class, "add", classes, classesActual);
    System.out.println("ArrayList.add(Object) \t=> <ArrayList_1>.add(<Subtype_1>): " + method);

    classesActual[0] = Object.class;
    method = test(Subtype.class, "overload", classes, classesActual);
    System.out.println("Subtype.overload(Object) \t=> <Subtype_1>.overload(<Object_1>): " + method);

    classesActual[0] = Subtype.class;
    method = test(Subtype.class, "overload", classes, classesActual);
    System.out.println("Subtype.overload(Subtype) \t=> <Subtype_1>.overload(<Subtype_1>): " + method);


    classesActual = new Class[2];
    classesActual[0] = int.class;
    classesActual[1] = Subtype.class;
    classes = new Class[2];
    classes[0] = int.class;
    classes[1] = Object.class;

    method = test(ArrayList.class, "add", classes, classes);
    System.out.println("ArrayList.add(int, Object) \t=> <ArrayList_1>.add(1, <Object_1>): " + method);

    method = test(ArrayList.class, "add", classes, classesActual);
    System.out.println("ArrayList.add(int, Object) \t=> <ArrayList_1>.add(1, <Subtype_1>): " + method);
    /*
    classesActual[1] = short.class;
    method = test(ArrayList.class, "add", classes, classesActual);
    System.out.println("ArrayList.add(int, Object) \t=> <ArrayList_1>.add(1, (s)1)X: " + method);
    classesActual[1] = Object.class;
    classesActual[0] = short.class;

    method = test(ArrayList.class, "add", classes, classesActual);
    System.out.println("ArrayList.add(int, Object) \t=> <ArrayList_1>.add((s)1, <Object_1>): " + method);

    */

    classesActual[1] = Object.class;
    classesActual[0] = null;

    method = test(ArrayList.class, "add", classes, classesActual);
    System.out.println("ArrayList.add(int, Object) \t=> <ArrayList_1>.add(null, <Object_1>) X: " + method);

    classesActual[1] = null;
    classesActual[0] = int.class;

    method = test(ArrayList.class, "add", classes, classesActual);
    System.out.println("ArrayList.add(int, Object) \t=> <ArrayList_1>.add(1, null): " + method);




  }
}
