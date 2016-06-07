/*                        CallEvent.java

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

public class CallEvent extends EventImpl {


    // This is the SLOW interface. (one inherited method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.CALL_METHOD_NAME) return callMethodName();
	if (a == Attribute.IS_CALL_METHOD_STATIC) return isCallMethodStatic();
	if (a == Attribute.CALL_OBJECT) return callObject();
	if (a == Attribute.CALL_OBJECT_CLASS) return callObjectClass();
	if (a == Attribute.CALL_ARGUMENTS) return callArguments();
	if (a == Attribute.CALL_ARGUMENT_VALUE0) return argumentValue(0);
	if (a == Attribute.CALL_ARGUMENT_VALUE1) return argumentValue(1);
	if (a == Attribute.CALL_ARGUMENT_VALUE2) return argumentValue(2);
	if (a == Attribute.CALL_ARGUMENT_VALUE3) return argumentValue(3);
	if (a == Attribute.CALL_ARGUMENT_VALUE4) return argumentValue(4);
	if (a == Attribute.CALL_ARGUMENT_VALUE5) return argumentValue(5);
	if (a == Attribute.CALL_ARGUMENT_VALUE6) return argumentValue(6);
	if (a == Attribute.CALL_ARGUMENT_VALUE7) return argumentValue(7);
	if (a == Attribute.CALL_ARGUMENT_VALUE8) return argumentValue(8);
	if (a == Attribute.CALL_ARGUMENT_VALUE9) return argumentValue(9);
	return super.getAttrValue(a);
    }


    // These are the FAST public methods this class provides. (All others return INVALID)
    public Value getPort() {return ConstantValue.CALL;}
    public String getCallMethodName() {return callMethodName;}
    public boolean getIsCallMethodStatic() {return(callObject instanceof Class);}
    public Object getCallObject() {return((callObject instanceof Class) ? Value.INVALID : callObject);}
    public Object getCallObjectClass() {return((callObject instanceof Class) ? callObject : callObject.getClass());}
    public int getnArgs() {return nArgs;}
    public Object getArgumentValue(int argN) {		
	if (nArgs <= argN) return Value.INVALID;
	Object value = args[argN];
	return value;
    }
						    




    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    private static CallEvent		SINGLETON = new CallEvent();	


    private CallEvent() {}

    // These are the SLOW methods the parent class provides for internal use only. (All others return INVALID)
    public Object callMethodName() {return getCallMethodName();}
    public Object isCallMethodStatic() {return(getIsCallMethodStatic() ? ShadowBoolean.TRUE : ShadowBoolean.FALSE);}
    public Object callObject() {return getCallObject();}
    public Object callObjectClass() {return getCallObjectClass();}
    public Object argumentValue(int i) {return getArgumentValue(i);}
    public Tuple callArguments() {
	if (callArguments != null) return callArguments;

	Locals l = callTL.locals;
	if (l == null) return new Tuple(new Tuple[0]);		// TOP_TRACELINE: throw new LSDException("Can't find locals "+ tl); 
	int nArgs = callTL.getArgCount();
	Tuple[] tuples = new Tuple[nArgs];

	for (int i = 0; i < nArgs; i++) {
	    Object value = callTL.getArg(i);
	    Class type = null;
	    if (value != null) type = value.getClass();
	    Object[] v = {(type), (value)};
	    Tuple t = new Tuple(v);
	    tuples[i] = t;
	}
	return new Tuple(tuples);
    }





    // **************** These are all specific for CallEvent ****************

    private Object		callObject;
    private Tuple		callArguments;	// <Tuple[ <Tuple[<Object INT>, <Object 76>]>,  <Tuple[<Object FLOAT>...]> ]>
    private Object[]		args = new Object[10];
    private String		callMethodName;
    private int			nArgs;
    private TraceLine 		callTL;

    public static CallEvent set(int slIndex, Object o, String meth, TraceLine callTL, int nArgs,
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
	TraceLine tl = null;
	if (callTL != null) tl = callTL.traceLine;
	SINGLETON.callTL = callTL;
	SINGLETON.set_(slIndex, tl);
	SINGLETON.callMethodName = meth;
	SINGLETON.callObject = o;
	SINGLETON.nArgs = nArgs;
	SINGLETON.args[0] = arg0;
	SINGLETON.args[1] = arg1;
	SINGLETON.args[2] = arg2;
	SINGLETON.args[3] = arg3;
	SINGLETON.args[4] = arg4;
	SINGLETON.args[5] = arg5;
	SINGLETON.args[6] = arg6;
	SINGLETON.args[7] = arg7;
	SINGLETON.args[8] = arg8;
	SINGLETON.args[9] = arg9;
	return SINGLETON;
    }
    
    public static CallEvent set(int time) {
	SINGLETON.set_(time);
	return SINGLETON;
    }
    
    protected void set_(int slIndex, TraceLine tl) {
	super.set_(slIndex, tl);
    }

    protected void set_(int time) {
	super.set_(time);
	callTL = (TraceLine) TraceLine.getMethodLine(time);
	this.nArgs = callTL.getArgCount();
	for (int i = 0; i < nArgs; i++)	this.args[i] = callTL.getArg(i);
	this.callObject = callTL.thisObj;
	this.callMethodName = callTL.getMethod();
    }


    public String printString() {
	String s = getSourceFileLine()+" "+getThread()+" "+printString(getCallObjectClass())+" "+printString(getCallObject())+"."+getCallMethodName()+"(";
	int len = getnArgs();
	for (int i = 0; i < len; i++) {
	    s += printString(getArgumentValue(i));
	    if (i < len-1) s+= ", ";
	}
	s += ")";
	return s;
    }


    public String toString() {
	return "<Call "+ printString() + ">";
    }


}
