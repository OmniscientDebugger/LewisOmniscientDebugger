/*                        ReturnEvent.java

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

public class ReturnEvent extends EventImpl {


    // This is the SLOW interface. (one inherited method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.CALL_METHOD_NAME) return callMethodName();
	if (a == Attribute.IS_CALL_METHOD_STATIC) return isCallMethodStatic();
	if (a == Attribute.CALL_OBJECT) return callObject();
	if (a == Attribute.CALL_OBJECT_CLASS) return callObjectClass();
	if (a == Attribute.RETURN_TYPE) return returnType();
	if (a == Attribute.RETURN_VALUE) return returnValue();
	return super.getAttrValue(a);
    }

    // These are the FAST public methods this class provides. (All others return INVALID)
    public Value getPort() {return ConstantValue.RETURN;}
    public Object getReturnValue() {return returnValue;}
    public Object getReturnType() {return getType(returnValue);}
    public Object getCallObjectClass() {return (callTL.thisObj instanceof Class)? callTL.thisObj : callTL.thisObj.getClass();}
    public Object getCallObject() {return  (callTL.thisObj instanceof Class)? Value.INVALID : callTL.thisObj;}
    public boolean getIsCallMethodStatic() {return (callTL.thisObj instanceof Class);}
    public String getCallMethodName() {return callTL.getMethod();}


    public Class getType(Object o) {
	if (o == null) return null;
	if (o instanceof ShadowPrimitive) return ((ShadowPrimitive) o).getType();
	return o.getClass();
    }


    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    private static ReturnEvent		SINGLETON = new ReturnEvent();	


    private ReturnEvent() {}

    // These are the SLOW methods the parent class provides for internal use only. (All others return INVALID)
    public Object returnValue() {return getReturnValue();}
    public Object callObjectClass() {return getCallObjectClass();}
    public Object callObject() {return getCallObject();}
    public Object isCallMethodStatic() {return ShadowBoolean.createShadowBoolean(getIsCallMethodStatic());}
    public Object callMethodName() {return getCallMethodName();}

    // **************** These are all specific for ReturnEvent ****************

    private Object 		returnValue;
    private Object		returnType;
    // **************** These are all specific for CallEvent ****************

    private Tuple		callArguments;	// <Tuple[ <Tuple[<Object INT>, <Object 76>]>,  <Tuple[<Object FLOAT>...]> ]>
    private TraceLine 		callTL;



    public static ReturnEvent set(Object returnValue, TraceLine callTL) {
	int slIndex = callTL.getSourceLineIndex();
	SINGLETON.callTL = callTL;
	TraceLine tl = null;
	if (callTL != null) tl = callTL.traceLine;
	SINGLETON.set_(slIndex, tl);
	SINGLETON.returnValue = returnValue;
	return SINGLETON;
    }
    
    public static ReturnEvent set(int time) {
	SINGLETON.set_(time);
	return SINGLETON;
    }
    
    protected void set_(int slIndex, TraceLine tl) {
	super.set_(slIndex, tl);

    }

    protected void set_(int time) {
	super.set_(time);
	ReturnLine rl = (ReturnLine) TraceLine.getMethodLine(time);
	returnValue = rl.returnValue;
	callTL = rl.caller;
    }


    public String toString() {
	return "<Return "+ //printString() +
	    " -> "+ printString(returnValue()) + ">";
    }
}
