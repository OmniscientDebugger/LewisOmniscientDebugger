/*                        Event.java

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

public abstract class Event {
    // The getXXXType() methods return these, where REFERENCE is any Object.      (Z==BOOLEAN, A==REFERENCE, I==INT...)
    public static final int	BOOLEAN=0, BYTE=1, CHAR=2, SHORT=3, INT=4, LONG=5, FLOAT=6, DOUBLE=7, REFERENCE=8;


    // This is the SLOW interface. (one method!)
    public abstract Object getAttrValue(Attribute a);



    // These are the FAST public methods this class provides.

    public abstract Value getPort();
    public abstract int getSourceLine();
    public abstract String getSourceFile();
    public abstract String getSourceFileLine();
    public abstract Object getThread();
    public abstract Object getThreadClass();
    public abstract String getMethodName();
    public abstract boolean getIsMethodStatic();
    public abstract Object getThisObject();
    public abstract Object getThisObjectClass();
    public abstract int getnParameters();

    // HIGHLY specific/fast interfaces
    public abstract int getNewValueType();		//NB: a constant from above, NOT a class!
    public abstract boolean getNewValueZ();
    public abstract byte getNewValueB();
    public abstract char getNewValueC();
    public abstract short getNewValueS();
    public abstract int getNewValueI();
    public abstract long getNewValueL();
    public abstract float getNewValueF();
    public abstract double getNewValueD();
    public abstract Object getNewValueA();

    /*
    public abstract int getReturnValueType();
    public abstract boolean getReturnValueZ();
    public abstract byte getReturnValueB();
    public abstract char getReturnValueC();
    public abstract short getReturnValueS();
    public abstract int getReturnValueI();
    public abstract long getReturnValueL();
    public abstract float getReturnValueF();
    public abstract double getReturnValueD();
    public abstract Object getReturnValueA();
    */

    public abstract String getCallMethodName();
    public abstract int getnArguments();
    public abstract String getVarName();
    public abstract Object getVarType();
    public abstract boolean getIsIVarStatic();
    public abstract Object getIVObject();
    public abstract Object getIVObjectClass();
    public abstract boolean getIsCallMethodStatic();
    public abstract Object getCallObject();
    public abstract Object getCallObjectClass();
    public abstract Object getReturnValue();
    public abstract Object getReturnType();
    public abstract Object getOldValue();			// not implemented
    public abstract Object getNewValue();
	
    public abstract Object getArgumentValue(int i);
    public abstract Object getParameterValue(int i);
    public abstract Object getParameterName(int i);
    public abstract Object getParameterType(int i);



    // Static methods only!

    public static Event createEvent(int time) {
	int type = (TimeStamp.getType(time) >> 28) & TimeStamp.TYPE_MASK_SHIFTED;
	ConstantValue port = ConstantValue.getPort(type);

	try {
	if (port == ConstantValue.LOCKING) return LockEvent.set(time, ConstantValue.LOCKING);
	if (port == ConstantValue.UNLOCKING) return LockEvent.set(time, ConstantValue.UNLOCKING);
	if (port == ConstantValue.WAITING) return LockEvent.set(time, ConstantValue.WAITING);
	if (port == ConstantValue.WAITED) return LockEvent.set(time, ConstantValue.WAITED);
	if (port == ConstantValue.ENTER) return EnterEvent.set(time);
	if (port == ConstantValue.EXIT) return ExitEvent.set(time);
	if (port == ConstantValue.CHGLOCALVAR) return ChangeLocalVariableEvent.set(time);
	if (port == ConstantValue.CHGINSTANCEVAR) return ChangeInstanceVariableEvent.set(time);
	if (port == ConstantValue.CHGARRAY) return ChangeArrayEvent.set(time);
	if (port == ConstantValue.CALL) return CallEvent.set(time);
	if (port == ConstantValue.RETURN) return ReturnEvent.set(time);
	if (port == ConstantValue.CATCH) return CatchEvent.set(time);
	if (port == ConstantValue.NOTDEFINED) return null;
	throw new LSDException("No such port value: " + port);
	}
	catch (LSDException e) {return null;}// This will be ignored in nextEvent()
    }


    private static Event nEvent, pEvent;
    public static Event nextEvent() {
	while (true) {
	    if (!moreEvents()) return null;
	    index++;
	    return nEvent;
	}
    }

    public static Event previousEvent() {
	while (true) {
	    if (!previousEvents()) return null;
	    index--;
	    return pEvent;
	}
    }

    public static boolean previousEvents() {
	while (index >= 0) {
	    if (pEvent != null && index == pEvent.time) return true;		// Don't regenerate 
	    pEvent = createEvent(index);
	    if (pEvent == null) {index--;continue;}
	    return true;
	}
	return false;
    }

    public static void resetIndex() {index = 0;}
    public static void setIndex(int i) {index = i;}

    public static boolean moreEvents() {
	while (index <= TimeStamp.eott()) {
	    if (nEvent != null && index == nEvent.time) return true;		// Don't regenerate 
	    nEvent = createEvent(index);
	    if (nEvent == null) {index++;continue;}
	    return true;
	}
	return false;
    }



    public static void dump() {
	resetIndex();
	while (moreEvents()) {
	    Event e = nextEvent();
	    System.out.println(e);
	}
    }


    public static String printString(Object value) {
	if (value == null)  return ""+value;
	if (value == Value.INVALID)  return "INVALID";
	if (value == Value.NOVALUE)  return "NOVALUE";
	if (value instanceof String) return "\""+value+"\"";
	if (value instanceof ShadowPrimitive) return ""+value;
	if (value instanceof Value) return "<V "+value+">";		// Neverhappen?
	Shadow sh = Shadow.get(value);
	String ps = sh.printString();
	return ps;
    }

    public static String printStringStatic(Object value) {
	if (value == null)  return ""+value;
	if (value == Value.INVALID)  return "INVALID";
	if (value == Value.NOVALUE)  return "NOVALUE";
	if (value instanceof String) return "\""+value+"\"";
	if (value instanceof ShadowPrimitive) return ""+value;
	if (value instanceof Value) return "<V "+value+">";		// Neverhappen?
	String ps = Penumbra.getPrintName(value);
	return ps;
    }


    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************

    protected static int	index = 0;
    protected int		time;			// Only defined for interactive fget() & used for Debugger.revert()
    protected TraceLine		tl;
    protected int		slIndex;
    protected Tuple		parameters;		// Keep if computed once. Expensive to recompute.

    public int time() {return time;}
}
