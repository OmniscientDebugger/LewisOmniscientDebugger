/*                        EventPattern.java

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

import java.util.*;
import com.lambda.Debugger.*;

public class EventPattern {

    public static boolean	DEBUG = false;
    public final Condition[]	conditions;

    // Optimization stuff
    public Condition[]		optimizedConditions;
    private boolean 		checkPort=false, checkNewValueA=false, checkNewValueI=false, checkNewValueB=false;
    private ConstantValue	port;
    private Object 		newValueA;
    private int 		newValueI;
    private byte 		newValueB;
    // ... etc.


    // These are required for EventInterface to figure out optimizations
    public ConstantValue getPort() {return port;}
    public boolean getCheckNewValueI() {return checkNewValueI;}
    public int getNewValueI() {return newValueI;}



    // Constructors

    EventPattern(ArrayList c) {
	optimizedConditions = conditions = new Condition[c.size()];
	c.toArray(conditions);
    }

    EventPattern(Condition[] c) {
	optimizedConditions = conditions = c;
    }



    // This is called by Debugger to find out when to start/stop recording or pause & return to LSD
    // This is where the all the optimizations go
    public boolean match(Event e) {
	boolean v = match1(e);
       	if (DEBUG) {
	    System.out.println("Testing: "+e +"\t -> "+ v);
	    VariableValue.printVars();
	}
	return v;
    }
	
    public boolean match1(Event e) {
	if (checkPort) {if (e.getPort() != port) return false;}
	if (checkNewValueI) {if ((e.getNewValueType() != Event.INT)  || (e.getNewValueI() != newValueI)) return false;}
	if (checkNewValueB) {if ((e.getNewValueType() != Event.BYTE) || (e.getNewValueB() != newValueB)) return false;}
	if (optimizedConditions.length > 0) return matchConditions(optimizedConditions, e);
	return true;
    }


    private boolean matchConditions(Condition[] c, Event e) {
	int len = c.length;
	resetVariables();
	for (int i = 0; i < len; i++) {
	    if (!c[i].match(e)) return false;
	}
	return true;
    }



    public void resetVariables() {
	for (int i = 0; i < conditions.length; i++) conditions[i].resetVariable();
    }





    // A rather sad little optimizer who needs help...  (I think LSD does all this for you?)

    public boolean optimize() {			// -> Did this optimize anything?
	optimizedConditions = conditions;
	Condition c0;

	// (port = enter)  => checkPort
	if (optimizedConditions.length == 0) return(optimizedConditions.length < conditions.length);
	c0 = optimizedConditions[0];
	if ( (c0.attribute == Attribute.PORT) && (c0.operator == Operator.UNIFY) && (c0.value instanceof ConstantValue) ) {
	    checkPort = true;
	    port = (ConstantValue) c0.value;
	    Condition[] cs = new Condition[optimizedConditions.length-1];
	    System.arraycopy(optimizedConditions, 1, cs, 0, optimizedConditions.length-1);
	    optimizedConditions = cs;
	}

	// (newValue = 42)  =>  checkNewValueI
	if (optimizedConditions.length == 0) return(optimizedConditions.length < conditions.length);
	c0 = optimizedConditions[0];
	if ( (c0.attribute == Attribute.NEW_VALUE) && (c0.operator == Operator.UNIFY) && (c0.value instanceof Value) ) {
	    Object value = c0.value.value;
	    if (value instanceof ShadowInt) {
		checkNewValueI = true;
		newValueI = ((ShadowInt)value).intValue();
	    }
	    else if (value instanceof ShadowByte) {
		checkNewValueB = true;
		newValueB = ((ShadowByte)value).byteValue();
	    }
	    // ...
	    else {
		checkNewValueA = true;
		newValueA = value;
	    }

	    Condition[] cs = new Condition[optimizedConditions.length-1];
	    System.arraycopy(optimizedConditions, 1, cs, 0, optimizedConditions.length-1);
	    //	    optimizedConditions = cs;		DON'T REMOVE THIS UNTIL ALL TYPES ARE HANDLED ABOVE.
	}


	return(optimizedConditions.length < conditions.length);
    }

    

    public void print() {
	Condition[] c = conditions;
	if (optimizedConditions != null) c = optimizedConditions;

	System.out.println("<EventPattern:");
	for (int i = 0; i < c.length; i++) System.out.println("\t"+c[i]);
	if (checkPort) System.out.println("port == "+port);
	if (checkNewValueI) System.out.println("var="+newValueI);
	System.out.println(">");
    }

    public String toString() {
	String s = "<EventPattern:";
	for (int i = 0; i < conditions.length; i++) s+=" "+conditions[i];
	s+=">";
	return s;
    }
}
