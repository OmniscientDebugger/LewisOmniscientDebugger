/*                        StateImpl.java

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


public abstract class StateImpl implements State {
    public static State	STATE;

    // This is the SLOW interface. (one inherited method!)
    public Object getAttrValue(Object o, Attribute a) {
	if (a == Attribute.OBJECT) return o;
	if (a == Attribute.OBJECT_CLASS) return o.getClass();
	if (a == Attribute.VARIABLES) return getInstanceVars(o);
	if (a == Attribute.OBJECTS) return getAllObjects();
	if (a == Attribute.STACK_FRAMES) return getAllStackFrames();
	if (a == Attribute.VARIABLE0) return getInstanceVarValue(o, 0);
	if (a == Attribute.VARIABLE1) return getInstanceVarValue(o, 1);
	if (a instanceof AttributeVariable) return variableValue(o, (AttributeVariable)a);
	return Value.INVALID;
    }


    // These are the FAST public methods this class provides. (All others return INVALID)
    public abstract Object[] getAllObjects();
    //    public abstract Iterator getObjectsIterator();
    public abstract StackFrame[] getAllStackFrames();

    public abstract Object getInstanceVarValue(Object o, String varName);
    public abstract Object getInstanceVarValue(Object o, int varIndex);
    public abstract Object getInstanceVarType(Object o, int i);

    public Object getInstanceVars(Object o) {
	ArrayList al = new ArrayList(5);
	int nVars = getnInstanceVars(o);
	for(int i=0; i<nVars; i++) {	// For each variable	
	    Object[] lo = {getInstanceVarName(o,i), getInstanceVarType(o,i), getInstanceVarValue(o,i), VariableValue.ANYVALUE, VariableValue.ANYVALUE};
	    Tuple tu = new Tuple(lo);
	    al.add(tu);
	}
	return new Tuple(al);
    }
			

    public Object variableValue(Object o, AttributeVariable a) {
	return getInstanceVarValue(o, a.varName);
    }
	



    public abstract void printAll();


    public StateImpl(TraceLine tl) {this.tl = ((tl == null) ? TraceLine.TOP_TRACELINE : tl);}
    public StateImpl(int time) {
	TraceLine tl = TimeStamp.getPreviousBalancedTrace(time);
	this.tl = ((tl == null) ? TraceLine.TOP_TRACELINE : tl);
    }
    public StateImpl() {}




    public String getPrintName(Object o) {
	if (o == null) return "null";
	
	if (o instanceof String) {
	    return("\""+o+"\"");
	}

	if (o == Dashes.DASHES) return "--";
	if (o instanceof ShadowPrimitive) return o.toString();

	Shadow sh = Shadow.get(o);
	if (sh == null) return o.toString();
	return sh.printString();
    }


    public static State getState() {return STATE;}
    public static void setState(State s) {STATE = s;}


    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    protected TraceLine 			tl;


}
