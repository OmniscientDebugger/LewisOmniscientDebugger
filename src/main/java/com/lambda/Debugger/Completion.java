/*                        Completion.java

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

//              Completion.java

/*
 */


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class Completion {

  static private HashMap		objectTable, methodTable;



    public static void printTable() {
    Set set =  methodTable.keySet();
    Iterator e = set.iterator();

    while (e.hasNext()) {
      Object key = e.next();
      Object o = methodTable.get(key);
      System.out.println(""+key +"\t\t\t"+o);
    }
  }

  public static String completeObject(String s) {
    Object result = complete(s, objectTable);
    if (result instanceof String) return((String) result);
    if (result == null) return "";
    if (result instanceof ObjectCompletionPair) return( ((ObjectCompletionPair)result).mutual );
    throw new NullPointerException("completeObject IMPOSSIBLE "+result);
  }

  public static Object completedObject(String s) throws CompletionException {
    Object result = complete(s, objectTable);
    if ( (result instanceof String) || (result == null) ) throw new CompletionException(""+result);
    if (result instanceof ObjectCompletionPair) return( ((ObjectCompletionPair)result).obj );
    throw new CompletionException("completedObject IMPOSSIBLE "+result);
  }


  public static Object complete(String s, HashMap table) {		// => "int[2]_", <Integer 23>, (OCP)<"Demo", Demo>
    if (s.length() == 0) return "";
    char ch = s.charAt(0);

    if (Character.isDigit(ch)) {
      try {
	Integer i = new Integer(Integer.parseInt(s));
	return(new ObjectCompletionPair(s, i));
      }				// Just verify it's an int
      catch (NumberFormatException e) {return(s);}
    }

    if (ch == '"') {
      if (s.charAt(s.length()-1) == '"')
	return(new ObjectCompletionPair(s, s.substring(1, s.length()-1)));
      else
	return(s);
    }

    Object o = table.get(s);

    if (o != null) return o;

    int len = s.length();					// If they type an error, return longest possible substring
    for (int i = len; i >= 0; i--) {
      String sub = s.substring(0, i);
      o = table.get(sub);
      if (o != null) return o;
    }
    return null;    
  }


  public static void createCompletionTable() {
      HashMapEq hm = Shadow.getTable();
      if ((objectTable != null) && (objectTable.size() == hm.size())) return;
    objectTable = new HashMap(hm.size()*3);			// Correct length?
    
	Iterator iter =  hm.values().iterator();

    while (iter.hasNext()) {
	Shadow sh = (Shadow) iter.next();	
	Object key = sh.obj;
	if (key instanceof String) continue;
	String name = sh.printString(); //printString();
	if (name == null) {continue;}			// If they can't see it, they can't type it
	mapCharsObject(name, key, objectTable);
    }

  }


  public static void mapCharsMethod(String name, Method method, HashMap table) {
    int len = name.length();

    for (int i = 1; i <= len; i++) {
      String sub = name.substring(0, i);
      MethodCompletionPair mcp = (MethodCompletionPair) table.get(sub);

      if (mcp == null) {
	table.put(sub, new MethodCompletionPair(name, method));
	continue;
      }

      String mutual = longest(name, mcp.mutual);
      if (mcp.mutual.equals(mutual)) continue;
      mcp.mutual = mutual;
      mcp.methods.clear();
    }
    
    MethodCompletionPair mcp  = (MethodCompletionPair) table.get(name);
    mcp.methods.add(method);
  }


  public static void mapCharsObject(String name, Object obj, HashMap table) {
    int len = name.length();

    for (int i = 1; i <= len; i++) {
      String sub = name.substring(0, i);
      ObjectCompletionPair ocp = (ObjectCompletionPair) table.get(sub);

      if (ocp == null) {
	table.put(sub, new ObjectCompletionPair(name, obj));
	continue;
      }

      String mutual = longest(name, ocp.mutual);
      if (ocp.mutual == mutual) continue;
      ocp.mutual = mutual;
      ocp.obj = null;
    }
    
    ObjectCompletionPair ocp  = (ObjectCompletionPair) table.get(name);
    ocp.obj = obj;
  }


  public static String longest(String s1, String s2) {
    int len = Math.min(s1.length(), s2.length());

    for (int i = 0; i < len; i++) {
      if (s1.charAt(i) == s2.charAt(i)) continue;
      return s1.substring(0, i);
    }

    if (s1.length() < s2.length())
      return s1;
    else
      return s2;
  }
    


  public static String completeCall(String s) {				// Do completion of a whole line: <Demo_0>.quick(1, 2)
    int len = s.length();
    int dotIndex = s.indexOf('.');
    int objIndex = dotIndex-1;
    String objString, sub, methodString;
    int closeIndex = s.indexOf(')');

    if (objIndex < 0)
      sub = s;
    else
      sub = s.substring(0, objIndex+1);

    Object obj = complete(sub, objectTable);
    if (obj instanceof String) {
      if (dotIndex > 0) return(s+" NO SUCH OBJECT: " + obj);					//
      return (String)obj;
    }
    else if (obj instanceof ObjectCompletionPair) {
      objString = ((ObjectCompletionPair)obj).mutual;
      obj = ((ObjectCompletionPair)obj).obj;
    }
    else {
      throw new NullPointerException("completeCall IMPOSSIBLE1 "+s+" -> "+obj);      
    }

    if (dotIndex < 0) return objString;
    if (len == dotIndex) return objString+".";

    if (obj instanceof Class)
      createMethodTable((Class)obj);
    else
      createMethodTable(obj.getClass());

    int openIndex = s.indexOf('(');
    if (openIndex == -1)
      sub = s.substring(dotIndex+1);
    else
      sub = s.substring(dotIndex+1, openIndex);
    
    Object methodObj = complete(sub, methodTable);
    if (methodObj == null) return(objString + ".");					// No possible completionom
    
    if (methodObj instanceof String) return(objString + "." + methodObj);
    if (methodObj instanceof MethodCompletionPair) {
      methodString = ((MethodCompletionPair)methodObj).mutual;
      if (openIndex < 0) return(objString + "." + methodString);
      if (openIndex+1 == len) return(objString + "." + methodString + "(");
    }
    else {
      throw new NullPointerException("completeCall IMPOSSIBLE2 "+s);   
    }



    // NO ARGS

    if (closeIndex == openIndex+1) return s;



    // FIRST ARG    
    int arg1Index;
    int comma1Index= s.indexOf(',');

    if (comma1Index < 0) {
      if (closeIndex > 0)
	arg1Index = closeIndex-1;
      else
	arg1Index = len-1;
    }
    else
      arg1Index = comma1Index-1;

    sub = s.substring(openIndex+1, arg1Index+1);
    String arg1String = sub;

    if (sub.length() == 0) return(objString + "." + methodString + "(");
    char ch = sub.charAt(0);
    if (Character.isDigit(ch)) {
      try {Integer.parseInt(sub);}				// Just verify it's an int
      catch (NumberFormatException e) {return(objString + "." + methodString + "("+ch);}
    }

    else if (ch == '"') {
      if (sub.charAt(sub.length()-1) != '"') {return(objString + "." + methodString + "("+sub);}		// If incomplete quote
    }

    // Must be <MyObj_1> or MyObj, or MyObj[23]_1
    else if (! (Character.isLetter(ch) || ch == '<') )
      return(objString + "." + methodString + "(");
    else {
      Object arg1 = complete(sub, objectTable);

      if (arg1 instanceof String) {
	arg1String = (String) arg1;
	if (arg1Index+1 == len) return(objString + "." + methodString + "(" + arg1String);
	if ((comma1Index+1 == len) || (comma1Index+2 == len)) return(objString + "." + methodString + "(" + arg1String + ", "); // ", " required
	if (closeIndex == arg1Index+1) return(objString + "." + methodString + "(" + arg1String + ")"); // ", " required
      }
      else {
	if (arg1 instanceof ObjectCompletionPair) {
	  arg1String = ((ObjectCompletionPair) arg1).mutual;
	}
	else {
	  Shadow ss = (Shadow) Shadow.getAlternate(arg1);
	  if (ss == null) return(objString + "." + methodString + "(");
	  arg1String = ss.tostring;
	}
	if ((comma1Index<0) && (closeIndex<0)) return(objString + "." + methodString + "(" + arg1String);
	if ((comma1Index+1 == len) || (comma1Index+2 == len)) return(objString + "." + methodString + "(" + arg1String + ", "); // ", " required
	if (closeIndex == arg1Index+1) return(objString + "." + methodString + "(" + arg1String + ")"); // ", " required
      }

    }

    if (closeIndex == arg1Index+1) return(objString + "." + methodString + "(" + arg1String + ")"); // ", " required
    if (comma1Index<0) return(objString + "." + methodString + "(" + arg1String);
    if ((comma1Index+1 == len) || (comma1Index+2 == len)) return(objString + "." + methodString + "(" + arg1String + ", "); // ", " required
    if (s.charAt(comma1Index+1) != ' ') return(objString + "." + methodString + "(" + arg1String + ", "); // ", " required




    // SECOND ARG    
    int arg2Index;
    int comma2Index= s.indexOf(',', comma1Index+1);


    if (comma2Index < 0) {
      if (closeIndex > 0)
	arg2Index = closeIndex-1;
      else
	arg2Index = len-1;
    }
    else
      arg2Index = comma2Index-1;

    sub = s.substring(comma1Index+2, arg2Index+1);
    String arg2String = sub;					// if number, boolean, or string: don't complete

    if (sub.length() == 0) return(objString + "." + methodString + "("+ arg1String + ", ");
    ch = sub.charAt(0);
    if (Character.isDigit(ch)) {
      try {Integer.parseInt(sub);}				// Just verify it's an int
      catch (NumberFormatException e) {return(objString + "." + methodString + "("+ arg1String + ", "+ch);}
    }

    else if (ch == '"') {
      if (sub.charAt(sub.length()-1) != '"') {return(objString + "." + methodString + "("+ arg1String + ", "+sub);}		// If incomplete quote
    }

    // Must be <MyObj_1> or MyObj, or MyObj[23]_1
    else if (! (Character.isLetter(ch) || ch == '<') )
      return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", ");
    else {
      Object arg2 = complete(sub, objectTable);

      if (arg2 instanceof String) {
	arg2String = (String) arg2;
	if (arg2Index+1 == len) return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String);
	if ((comma2Index+1 == len) || (comma2Index+2 == len))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "); // ", " required
	if (closeIndex == arg2Index+1)
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ")"); // ", " required
      }
      else {
	if (arg2 instanceof ObjectCompletionPair) {
	  arg2String = ((ObjectCompletionPair) arg2).mutual;
	}
	else {
	  Shadow ss = (Shadow) Shadow.getAlternate(arg2);
	  if (ss == null) return(objString + "." + methodString + "("+ arg1String + ", ");
	  arg2String = ss.tostring;
	}
	if ((comma2Index<0) && (closeIndex<0))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String);
	if ((comma2Index+1 == len) || (comma2Index+2 == len))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "); // ", " required
	if (closeIndex == arg2Index+1)
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ")"); // ", " required
      }

    }

    if (closeIndex == arg2Index+1)
      return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ")"); // ", " required
    if (comma2Index<0) return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String);
    if ((comma2Index+1 == len) || (comma2Index+2 == len))
      return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "); // ", " required
    if (s.charAt(comma2Index+1) != ' ') return(objString + "." + methodString + "(" + arg1String + ", "); // ", " required







    // THIRD ARG
    
    int arg3Index;
    int comma3Index= s.indexOf(',', comma2Index+1);


    if (comma3Index < 0) {
      if (closeIndex > 0)
	arg3Index = closeIndex-1;
      else
	arg3Index = len-1;
    }
    else
      arg3Index = comma3Index-1;

    sub = s.substring(comma2Index+2, arg3Index+1);
    String arg3String = sub;					// if number, boolean, or string: don't complete

    if (sub.length() == 0) return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", ");
    ch = sub.charAt(0);
    if (Character.isDigit(ch)) {
      try {Integer.parseInt(sub);}				// Just verify it's an int
      catch (NumberFormatException e) {return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", "+ch);}
    }

    else if (ch == '"') {
      if (sub.charAt(sub.length()-1) != '"') {return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", "+sub);}
    }

    // Must be <MyObj_1> or MyObj, or MyObj[33]_1
    else if (! (Character.isLetter(ch) || ch == '<') )
      return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", "+ arg3String + ", ");
    else {
      Object arg3 = complete(sub, objectTable);

      if (arg3 instanceof String) {
	arg3String = (String) arg3;
	if (arg3Index+1 == len) return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String);
	if ((comma3Index+1 == len) || (comma3Index+2 == len))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String + ", "); // ", " required
	if (closeIndex == arg3Index+1)
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String + ")"); // ", " required
      }
      else {
	if (arg3 instanceof ObjectCompletionPair) {
	  arg3String = ((ObjectCompletionPair) arg3).mutual;
	}
	else {
	  Shadow ss = (Shadow) Shadow.getAlternate(arg3);
	  if (ss == null) return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", ");
	  arg3String = ss.tostring;
	}
	if ((comma3Index<0) && (closeIndex<0))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String);
	if ((comma3Index+1 == len) || (comma3Index+2 == len))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String + ", "); // ", " required
	if (closeIndex == arg3Index+1)
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String + ")"); // ", " required
      }

    }

    if (closeIndex == arg3Index+1)
      return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String + ")"); // ", " required
    if (comma3Index<0) return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String);
    if ((comma3Index+1 == len) || (comma3Index+2 == len))
      return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", "+ arg3String + ", "); // ", " required
    if (s.charAt(comma3Index+1) != ' ') return(objString + "." + methodString + "(" + arg1String + ", "); // ", " required



    // FOURTH ARG
    
    int arg4Index;
    int comma4Index= s.indexOf(',', comma3Index+1);


    if (comma4Index < 0) {
      if (closeIndex > 0)
	arg4Index = closeIndex-1;
      else
	arg4Index = len-1;
    }
    else
      arg4Index = comma4Index-1;

    sub = s.substring(comma3Index+2, arg4Index+1);
    String arg4String = sub;					// if number, boolean, or string: don't complete

    if (sub.length() == 0) return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", " + arg3String + ", ");
    ch = sub.charAt(0);
    if (Character.isDigit(ch)) {
      try {Integer.parseInt(sub);}				// Just verify it's an int
      catch (NumberFormatException e) {return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", " + arg3String + ", "+ch);}
    }

    else if (ch == '"') {
      if (sub.charAt(sub.length()-1) != '"') {return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", " + arg3String + ", "+sub);}
    }

    // Must be <MyObj_1> or MyObj, or MyObj[44]_1
    else if (! (Character.isLetter(ch) || ch == '<') )
      return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String + ", ");
    else {
      Object arg4 = complete(sub, objectTable);

      if (arg4 instanceof String) {
	arg4String = (String) arg4;
	if (arg4Index+1 == len) return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String);
	if ((comma4Index+1 == len) || (comma4Index+2 == len))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String + ", "); // ", " required
	if (closeIndex == arg4Index+1)
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String + ")"); // ", " required
      }
      else {
	if (arg4 instanceof ObjectCompletionPair) {
	  arg4String = ((ObjectCompletionPair) arg4).mutual;
	}
	else {
	  Shadow ss = (Shadow) Shadow.getAlternate(arg4);
	  if (ss == null) return(objString + "." + methodString + "("+ arg1String + ", "+ arg2String + ", " + arg3String + ", ");
	  arg4String = ss.tostring;
	}
	if ((comma4Index<0) && (closeIndex<0))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String);
	if ((comma4Index+1 == len) || (comma4Index+2 == len))
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String + ", "); // ", " required
	if (closeIndex == arg4Index+1)
	  return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String + ")"); // ", " required
      }

    }

    if (closeIndex == arg4Index+1)
      return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String + ")"); // ", " required
    if (comma4Index<0) return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String);
    if ((comma4Index+1 == len) || (comma4Index+2 == len))
      return(objString + "." + methodString + "(" + arg1String + ", "+ arg2String + ", " + arg3String + ", "+ arg4String + ", "); // ", " required


    // REST IGNORED
    String rest = s.substring(arg1Index+2);
    return("MAX FOUR ARGS ACCEPTED: " + objString + "." + methodString + "(" + arg1String + ", " + rest); // ", " required

  }




  public static ParsePair parse(String s) throws CompletionException {
    ParsePair pp;
    Class objClass;

    pp = parse1(s);

    MethodCompletionPair cp = pp.cPair;

    Object obj = pp.obj;
    if (obj instanceof Class)
      objClass = (Class) obj;
    else
      objClass = obj.getClass();
    
    String methodName = cp.mutual;

    int n = pp.objects.length;
    Class[] classes = new Class[n];
    for (int i = 0; i < n; i++) {
      Object o = pp.objects[i];
      if (o == null) o = ShadowNull.NULL;
      Class c = o.getClass();
      if (c == Integer.class) c = int.class;
      if (c == Boolean.class) c = boolean.class;
      if (c == ShadowNull.class) c = null;			// WILL THIS WORK?
      classes[i] = c;
    }

    Method method;


    //    try {method = objClass.getDeclaredMethod(methodName, classes);}
    try {method = Subtype.getDeclaredMethod(objClass, methodName, classes);}
    catch (NoSuchMethodException e) {
      //System.out.println("No such method: " + methodName);
      throw new CompletionException("No such method: " + methodName);
    }

    pp.method = method;			// pp may chose arbitrary methods here.

    verifyCreation(pp);
    return pp;
  }

  public static void verifyCreation(ParsePair pp) throws CompletionException {
    int len = pp.objects.length;

    Object obj = pp.obj;
    if (obj instanceof ObjectCompletionPair) obj = ((ObjectCompletionPair)obj).obj;
    verifyCreation(Shadow.get(obj));

    for (int i = 0; i < len; i++) {
      Object o = pp.objects[i];
      if (o instanceof ObjectCompletionPair) o = ((ObjectCompletionPair)o).obj;
      verifyCreation(Shadow.get(o));
    }
  }


  public static void verifyCreation(Shadow s) throws CompletionException {
    if (s.size() == 0) return;
    TimeStamp ts = s.getShadowVar(0).getTS(0);
    if (ts.laterThan(TimeStamp.currentTime())) throw new CompletionException(s, ts);
    return;
  }


  public static ParsePair parse1(String s)  throws CompletionException {
    String objString="", sub, methodString;
    MethodCompletionPair cp;
    int len = s.length();
    int dotIndex = s.indexOf('.');
    int objIndex = dotIndex-1;
    int openIndex = s.indexOf('(');
    int closeIndex = s.indexOf(')');

    if (openIndex < 0) throw new CompletionException("Parse failure: No '(' " + s);
    if (closeIndex != len-1) throw new CompletionException("Parse failure: ')' not final character " + s);
    if (objIndex < 0) throw new CompletionException("Parse failure: No '(' " + s);

    sub = s.substring(0, objIndex+1);
    Object obj = complete(sub, objectTable);

    if (obj instanceof String) throw new CompletionException("Parse failure: Incomplete Expression " + s);
    if (len == objIndex+1) throw new CompletionException("Parse failure: Incomplete Expression " + s);
    objString = sub;

    if (obj instanceof ObjectCompletionPair) obj = ((ObjectCompletionPair)obj).obj;
    Class c = obj.getClass();
    if (obj instanceof Class) c = (Class)obj;
    createMethodTable(c);
    Method m;

    sub = s.substring(objIndex+2, openIndex);
    Object methodObj = complete(sub, methodTable);
    if (methodObj == null) throw new CompletionException("Parse failure: Incomplete Expression " + s);
    if (methodObj instanceof String) throw new CompletionException("Parse failure: Incomplete Expression " + s);
    
    if (methodObj instanceof MethodCompletionPair) {
      cp = (MethodCompletionPair)methodObj;
      methodString = cp.mutual;
    }
    else if (methodObj instanceof Method) {
      m = (Method) methodObj;
      methodString = m.getName();
      cp = new MethodCompletionPair(methodString, m);
    }
    else
      throw new CompletionException("IMPOSSIBLE Parse failure: " + s);


    // NO ARGS

    if (closeIndex == openIndex+1) return new ParsePair(s, obj, cp);


    // FIRST ARG    

    char ch;
    Object arg1;
    String arg1String = sub;
    int arg1Index;
    int comma1Index= s.indexOf(',');

    if (comma1Index < 0) {
      arg1Index = closeIndex-1;
    }
    else
      arg1Index = comma1Index-1;

    sub = s.substring(openIndex+1, arg1Index+1);

    arg1String = sub;					// if number, boolean, or string: don't verify
    if (sub.length() < 1) throw new CompletionException("IMPOSSIBLE Parse failure: " + s);
    ch = sub.charAt(0);


    if ( Character.isDigit(ch) || (ch == '"') ) {
      arg1 = parsePrimitive(sub);
    }
    else if ( (Character.isLetter(ch) || ch == '<') ) {		    // Must be <MyObj_1> or MyObj, or MyObj[23]_1
      arg1 = complete(sub, objectTable);
      if (arg1 instanceof String) throw new CompletionException("Parse failure: incomplete arg #1 '" + sub + "' in " + s);
      if (arg1 instanceof ObjectCompletionPair) 
	arg1 = ((ObjectCompletionPair)arg1).obj;
      else
	 throw new CompletionException("IMPOSSIBLE Parse failure: incomplete arg #1 '" + sub + "' in " + s);
      Shadow ss = (Shadow) Shadow.getAlternate(arg1);
      if (ss == null) throw new CompletionException("IMPOSSIBLE Parse failure: incomplete arg #1 '" + sub + "' in " + s);
      arg1String = ss.tostring;
    }
    else 
      throw new CompletionException("Parse failure: incorrect arg #1 '" + sub + "' in " + s);

    if (closeIndex == arg1Index+1) return new ParsePair(s, obj, cp, arg1);
    if (comma1Index<0) throw new CompletionException("Parse failure: missing ','"+s);


    // SECOND ARG    

    Object arg2;
    String arg2String = sub;
    int arg2Index;
    int comma2Index= s.indexOf(',', comma1Index+1);

    if (comma2Index < 0) {
      arg2Index = closeIndex-1;
    }
    else
      arg2Index = comma2Index-1;

    if (comma1Index+2 >= arg2Index+1) throw new CompletionException("IMPOSSIBLE Parse failure: " + s);
    sub = s.substring(comma1Index+2, arg2Index+1);

    arg2String = sub;					// if number, boolean, or string: don't verify
    if (sub.length() < 1) throw new CompletionException("IMPOSSIBLE Parse failure: " + s);
    ch = sub.charAt(0);


    if ( Character.isDigit(ch) || (ch == '"') ) {
      arg2 = parsePrimitive(sub);
    }
    else if ( (Character.isLetter(ch) || ch == '<') ) {		    // Must be <MyObj_1> or MyObj, or MyObj[23]_1
      arg2 = complete(sub, objectTable);
      if (arg2 instanceof String) throw new CompletionException("Parse failure: incomplete arg #2 '" + sub + "' in " + s);
      if (arg2 instanceof ObjectCompletionPair) 
	arg2 = ((ObjectCompletionPair)arg2).obj;
      else
	 throw new CompletionException("IMPOSSIBLE Parse failure: incomplete arg #2 '" + sub + "' in " + s);
      Shadow ss = (Shadow) Shadow.getAlternate(arg2);
      if (ss == null) throw new CompletionException("IMPOSSIBLE Parse failure: incomplete arg #2 '" + sub + "' in " + s);
      arg2String = ss.tostring;
    }
    else 
      throw new CompletionException("Parse failure: incorrect arg #2 '" + sub + "' in " + s);

    if (closeIndex == arg2Index+1) return new ParsePair(s, obj, cp, arg1, arg2);
    if (comma2Index<0) throw new CompletionException("Parse failure: missing ','"+s);

    // THIRD ARG    

    Object arg3;
    String arg3String = sub;
    int arg3Index;
    int comma3Index= s.indexOf(',', comma2Index+1);

    if (comma3Index < 0) {
      arg3Index = closeIndex-1;
    }
    else
      arg3Index = comma3Index-1;

    if (comma2Index+2 >= arg3Index+1) throw new CompletionException("IMPOSSIBLE Parse failure: " + s);
    sub = s.substring(comma2Index+2, arg3Index+1);

    arg3String = sub;					// if number, boolean, or string: don't verify
    if (sub.length() < 1) throw new CompletionException("IMPOSSIBLE Parse failure: " + s);
    ch = sub.charAt(0);


    if ( Character.isDigit(ch) || (ch == '"') ) {
      arg3 = parsePrimitive(sub);
    }
    else if ( (Character.isLetter(ch) || ch == '<') ) {		    // Must be <MyObj_1> or MyObj, or MyObj[33]_1
      arg3 = complete(sub, objectTable);
      if (arg3 instanceof String) throw new CompletionException("Parse failure: incomplete arg #3 '" + sub + "' in " + s);
      if (arg3 instanceof ObjectCompletionPair) 
	arg3 = ((ObjectCompletionPair)arg3).obj;
      else
	 throw new CompletionException("IMPOSSIBLE Parse failure: incomplete arg #3 '" + sub + "' in " + s);
      Shadow ss = (Shadow) Shadow.getAlternate(arg3);
      if (ss == null) throw new CompletionException("IMPOSSIBLE Parse failure: incomplete arg #3 '" + sub + "' in " + s);
      arg3String = ss.tostring;
    }
    else 
      throw new CompletionException("Parse failure: incorrect arg #3 '" + sub + "' in " + s);

    if (closeIndex == arg3Index+1) return new ParsePair(s, obj, cp, arg1, arg2, arg3);
    if (comma3Index<0) throw new CompletionException("Parse failure: missing ','"+s);


    // FOURTH ARG     

    Object arg4;
    String arg4String = sub;
    int arg4Index;
    int comma4Index= s.indexOf(',', comma3Index+1);

    if (comma4Index < 0) {
      arg4Index = closeIndex-1;
    }
    else
      arg4Index = comma4Index-1;
    if (comma3Index+2 >= arg4Index+1) throw new CompletionException("IMPOSSIBLE Parse failure: " + s);
    sub = s.substring(comma3Index+2, arg4Index+1);

    arg4String = sub;					// if number, boolean, or string: don't verify
    if (sub.length() < 1) throw new CompletionException("IMPOSSIBLE Parse failure: " + s);
    ch = sub.charAt(0);


    if ( Character.isDigit(ch) || (ch == '"') ) {
      arg4 = parsePrimitive(sub);
    }
    else if ( (Character.isLetter(ch) || ch == '<') ) {		    // Must be <MyObj_1> or MyObj, or MyObj[44]_1
      arg4 = complete(sub, objectTable);
      if (arg4 instanceof String) throw new CompletionException("Parse failure: incomplete arg #4 '" + sub + "' in " + s);
      if (arg4 instanceof ObjectCompletionPair) 
	arg4 = ((ObjectCompletionPair)arg4).obj;
      else
	 throw new CompletionException("IMPOSSIBLE Parse failure: incomplete arg #4 '" + sub + "' in " + s);
      Shadow ss = (Shadow) Shadow.getAlternate(arg4);
      if (ss == null) throw new CompletionException("IMPOSSIBLE Parse failure: incomplete arg #4 '" + sub + "' in " + s);
      arg4String = ss.tostring;
    }
    else 
      throw new CompletionException("Parse failure: incorrect arg #4 '" + sub + "' in " + s);

    if (closeIndex == arg4Index+1) return new ParsePair(s, obj, cp, arg1, arg2, arg3, arg4);
    if (comma4Index<0) throw new CompletionException("Parse failure: missing ','"+s);
  
    throw new CompletionException("Parse failure: Too many arguments " + s);
  }




  public static Object parsePrimitive(String s) throws CompletionException {
    if (s.length() == 0) throw new CompletionException("Parse failure: not a primitive or String " + s);
    char ch = s.charAt(0);

    if (Character.isDigit(ch)) {
      try {
	Integer i = new Integer(Integer.parseInt(s));
	return(i);
      }				// Just verify it's an int
      catch (NumberFormatException e) {throw new CompletionException("Parse failure: bad int " + s);}
    }

    if (ch == '"') {
      if (s.charAt(s.length()-1) == '"')
	return(s.substring(1, s.length()-1));
      else
	throw new CompletionException("Parse failure: bad String " + s);
    }
    throw new CompletionException("IMPOSSIBLE Parse failure " + s);
  }



  public static void createMethodTable(Class c) {
    Method[] methods = c.getMethods();
    methodTable = new HashMap();
    int len = methods.length;

    for (int i = 0; i < len; i++) {			//System.out.println(methods[i]);
      String name = methods[i].getName();
      mapCharsMethod(name, methods[i], methodTable);
    }
  }

    


  public static void main(String[] args) {
    /*
      Shadow.put(new Thing(), new Shadow("Thing"));		// 0
      Shadow.put(new Thing(), new Shadow("Thing"));		// 1
      Shadow.put(new Thing(), new Shadow("Thing"));		// 2
      Shadow.put(new Thing(), new Shadow("Thing"));		// 3
      Shadow.put(new SubThing(), new Shadow("SubThing"));	// 4
      Shadow.put(new SubThing(), new Shadow("SubThing"));	// 5
      Shadow.put(new MyThing(), new Shadow("MyThing"));		// 6
      Shadow.put(new MyThing(), new Shadow("MyThing"));		// 7
      Shadow.put(new MyThing(), new Shadow("MyThing"));		// 8

      Shadow.put(new Boolean(false), new Shadow("false"));
      Shadow.put(new Boolean(true), new Shadow("true"));
      */
    //System.out.println(Shadow.table);

    //createCompletionTable(Shadow.table);
    
    //System.out.println(objectTable);


    System.out.println("Completing " + "<" + " -> " + complete("<", objectTable));
    System.out.println("Completing " + "<T" + " -> " + complete("<T", objectTable));
    System.out.println("Completing " + "<Th" + " -> " + complete("<Th", objectTable));
    System.out.println("Completing " + "<Te" + " -> " + complete("<Te", objectTable));
    System.out.println("Completing " + "<Thing_2" + " -> " + complete("<Thing_2", objectTable));
    System.out.println("Completing " + "<MyT" + " -> " + complete("<MyT", objectTable));
    System.out.println("Completing " + "<MyThing_1" + " -> " + complete("<MyThing_1", objectTable));
    System.out.println("Completing " + "<MyThing_12" + " -> " + complete("<MyThing_12", objectTable));
    System.out.println("Completing " + "<MyTheng_" + " -> " + complete("<MyTheng_", objectTable));
    System.out.println("Completing " + "t" + " -> " + complete("t", objectTable));
    System.out.println("Completing " + "tr" + " -> " + complete("tr", objectTable));
    System.out.println("Completing " + "falx" + " -> " + complete("falx", objectTable));

    System.out.println("----------------------------------------------------------------");

    System.out.println("Completing " + "<Thi" + " -> " + completeCall("<Thi"));
    System.out.println("Completing " + "<Thing_1>." + " -> " + completeCall("<Thing_1>."));
    System.out.println("Completing " + "<Thing_1>.to" + " -> " + completeCall("<Thing_1>.to"));
    System.out.println("Completing " + "<Thing_1>.doo" + " -> " + completeCall("<Thing_1>.doo"));
    System.out.println("Completing " + "<Thing_1>.doo(" + " -> " + completeCall("<Thing_1>.doo("));
    System.out.println("Completing " + "<Thing_1>.doo()" + " -> " + completeCall("<Thing_1>.doo()"));
    System.out.println("Completing " + "<Thing_1>.doo(\"Test\")" + " -> " + completeCall("<Thing_1>.doo(\"Test\")"));
    System.out.println("Completing " + "<Thing_1>.d" + " -> " + completeCall("<Thing_1>.d"));
    System.out.println("Completing " + "<Thing_1>.di" + " -> " + completeCall("<Thing_1>.di"));
    System.out.println("Completing " + "<Thing_1>.doon" + " -> " + completeCall("<Thing_1>.doon"));


    System.out.println("Completing " + "<Thing_1>.doo(<Thi" + " -> " + completeCall("<Thing_1>.doo(<Thi"));
    System.out.println("Completing " + "<Thing_1>.did(12," + " -> " + completeCall("<Thing_1>.did(12,"));
    System.out.println("Completing " + "<Thing_1>.did(12" + " -> " + completeCall("<Thing_1>.did(12"));
    System.out.println("Completing " + "<Thing_1>.did(12)" + " -> " + completeCall("<Thing_1>.did(12)"));
    System.out.println("Completing " + "<Thing_1>.doo(<Thing_1>, 12" + " -> " + completeCall("<Thing_1>.doo(<Thing_1>, 12"));
    System.out.println("Completing " + "<Thing_1>.doo(<Thing_1>, 12)" + " -> " + completeCall("<Thing_1>.doo(<Thing_1>, 12)"));
    System.out.println("Completing " + "<Thing_1>.doo(<Thing_1>, <M" + " -> " + completeCall("<Thing_1>.doo(<Thing_1>, <M"));
    System.out.println("Completing " + "<Thing_1>.doo(<Thing_1>, <MyThing_12" + " -> " + completeCall("<Thing_1>.doo(<Thing_1>, <MyThing_12"));
    System.out.println("Completing " + "<Thing_1>.doo(<Thing_1>, <MyThing_12>)" + " -> " + completeCall("<Thing_1>.doo(<Thing_1>, <MyThing_12>)"));


    System.out.println("Completing " + "<Thing_1>.doo(<SubThing_4>)" + " -> " + completeCall("<Thing_1>.doo(<SubThing_4>)"));

    try {

      System.out.println("Parsing " + "<Thing_1>.doo(<SubThing_4>)" + " -> " + parse("<Thing_1>.doo(<SubThing_4>)"));

      System.out.println("Parsing " + "<Thing_1>.doo()" + " -> " + parse("<Thing_1>.doo()"));
      System.out.println("Parsing " + "<Thing_1>.doo(12)" + " -> " + parse("<Thing_1>.doo(12)"));
      System.out.println("Parsing " + "<Thing_1>.doo(<Thing_1>, 12)" + " -> " + parse("<Thing_1>.doo(<Thing_1>, 12)"));
      System.out.println("Parsing " + "<Thing_1>.doo(<Thing_1>)" + " -> " + parse("<Thing_1>.doo(<Thing_1>)"));
      System.out.println("Parsing " + "<Thing_1>.doo(12, <Thing_1>)" + " -> " + parse("<Thing_1>.doo(12, <Thing_1>)"));
      System.out.println("Parsing " + "<Thing_1>.doo(<Thing_1>, <MyThing_7>)" + " -> " + parse("<Thing_1>.doo(<Thing_1>, <MyThing_7>)"));

      //    System.out.println(methodTable);
    }
    catch (CompletionException ce) {System.out.println(ce);}
    

  }
}


class Thing {
  public static Object doo() {return "WORKED";}
  public static Object doo(Thing t) {return "WORKED";}
  public static Object doo(Thing t, int i) {return "WORKED";}
  public static Object doo(Thing t, MyThing mt) {return "WORKED";}
  public static Object doo(int i) {return "WORKED";}
  public static Object doo(Thing t, int i, boolean b) {return "WORKED";}
  public static Object doo(Thing t, int i, boolean b, int j) {return "WORKED";}
  public  Object did() {return "WORKED";}
  public  Object doone() {return "WORKED";}
}

class SubThing extends Thing {}

class MyThing {
}


class MethodCompletionPair {
  public String mutual;
  public Vector methods = new MyVector();

  public MethodCompletionPair(String s, Method m) {
    mutual=s;
    methods.add(m);
  }

  public Method firstMethod() {
    if (methods.size() == 0) return null;		// Trouble!
    return (Method)methods.elementAt(0);
  }

  public void addMethod(Method m) {
    methods.add(m);
  }
}



class ObjectCompletionPair {
  public String mutual;
  public Object obj;

  public ObjectCompletionPair(String s, Object o) {
    mutual=s;
    obj = o;
  }

}


