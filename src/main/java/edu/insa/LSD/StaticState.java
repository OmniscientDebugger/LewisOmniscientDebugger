/*                        StaticState.java

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
import java.lang.reflect.*;



public class StaticState extends StateImpl {
    private Event			event;


    public StaticState(TraceLine tl, Event e) {super(tl); event = e; STATE = this;}
    private StaticState() {}


    // These are the FAST public methods this class provides. (All others return INVALID)
    public Object[] getAllObjects() {
	return null;
    }



    public Object getInstanceVarValue(Object o, String varName) {
	int varIndex = getInstanceVarIndex(o, varName);
	if (varIndex < 0) return Value.INVALID;
	return getInstanceVarValue(o, varIndex);
    }


    public int getInstanceVarIndex(Object o, String varName) {
	ClassInformation ci = Penumbra.getClassInfo(o);
	int nVars = ci.getnInstanceVars();
	for (int i = 0; i < nVars; i++) if (varName == ci.getVarName(i)) return i;
	return -1;
    }


	


    public Object getInstanceVarValue(Object o, int varIndex) {
	if (varIndex < 0) return Value.INVALID;
	ClassInformation ci = Penumbra.getClassInfo(o);
	int nVars = ci.getnInstanceVars();
	if (varIndex >= nVars) return Value.INVALID;
	Field varField = ci.getVarField(varIndex);
	Object value;
	try {value = varField.get(o);}						// Returns new Integer() &c.
	catch (IllegalAccessException e) {
	    D.println("createShadow cannot access field in " + o + " "+e);
	    value = "ODB Error in createShadow";
	}
	value = Shadow.convert(value);
	return value;
    }
	
    public Object getInstanceVarName(Object o, int varIndex) {
	if (varIndex < 0) return Value.INVALID;
	ClassInformation ci = Penumbra.getClassInfo(o);
	int nVars = ci.getnInstanceVars();
	if (varIndex >= nVars) return Value.INVALID;
	String varName = ci.getVarName(varIndex);
	return varName;
    }
	
    public Object getInstanceVarType(Object o, int varIndex) {
	if (varIndex < 0) return Value.INVALID;
	ClassInformation ci = Penumbra.getClassInfo(o);
	int nVars = ci.getnInstanceVars();
	if (varIndex >= nVars) return Value.INVALID;
	Class varClass = ci.getVarClass(varIndex);
	return varClass;
    }

    public Object getInstanceVars(Object o) {
	ClassInformation ci = Penumbra.getClassInfo(o);
	return ci.getVarNames();
    }


    public StackFrame[] getAllStackFrames() {
	ArrayList al = new ArrayList();

	TraceLine tl0 = tl;
	if (event.getPort() == ConstantValue.CALL) tl0 = tl.traceLine;
	for (TraceLine tl1 = tl0; tl1 != TraceLine.TOP_TRACELINE; tl1 = tl1.traceLine) al.add(new StackFrameImpl(tl1));
	StackFrame[] a = new StackFrame[al.size()];
	al.toArray(a);
	return a;
    }


    public int getnInstanceVars(Object o) {
	ClassInformation ci = Penumbra.getClassInfo(o);
	int nVars = ci.getnInstanceVars();
	return nVars;
}

    public Iterator getObjectsIterator() {
	Iterator iter = new StaticObjectIterator(this);	
	return iter;
    }
    public Iterator getObjectsIterator(Class c) {return null;}

    public void printAll() {
	String name;
	StackFrame[] sf = getAllStackFrames();
	int len = sf.length;
	System.out.println(" --- StackFrame ---");
	for (int i = 0; i < len; i++) {
	    String mn = sf[i].getMethodName();
	    Object o = sf[i].getThisObject();
	    if (sf[i].getIsMethodStatic()) o = sf[i].getThisObjectClass();
	    Tuple t = sf[i].getAllVariables();
	    System.out.println("\t"+getPrintName(o) +"."+mn+"(): " + t.toStringStatic(this));
	}

	Iterator iter = getObjectsIterator();
	System.out.println(" --- Objects ---");
	while(iter.hasNext()) {
	    Object o = iter.next();
	    name = getPrintName(o);
	    System.out.println("\t"+name);
	    int len2 = getnInstanceVars(o);
	    for (int j = 0; j < len2; j++) {
		Object varName = getInstanceVarName(o, j);
		if (varName == Value.INVALID) continue;
		String vn = (String) varName;
		Object value = getInstanceVarValue(o, j);
		Object type = getInstanceVarType(o, j);
		System.out.println("\t\t"+varName+"\t\t"+getPrintName(value)+"\t\t"+getPrintName(type));
	    }
	}
	System.out.println("");
    }


    public String getPrintName(Object o) {
	if (o == null) return "null";
	if (o instanceof Tuple) return ((Tuple)o).toStringStatic(this);
	
	if (o instanceof String) {
	    return("\""+o+"\"");
	}

	if (o == Dashes.DASHES) return "--";
	if (o instanceof ShadowPrimitive) return o.toString();

	return Penumbra.getPrintName(o); 
    }



}
