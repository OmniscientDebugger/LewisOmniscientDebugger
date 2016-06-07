/*                        EventImpl.java

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

public abstract class EventImpl extends Event {


    // This is the SLOW interface. (one method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.PORT) return port();
	if (a == Attribute.SOURCE_LINE) return sourceLine();
	if (a == Attribute.SOURCE_FILE) return sourceFile();
	if (a == Attribute.THREAD) return thread();
	if (a == Attribute.THREAD_CLASS) return threadClass();
	if (a == Attribute.METHOD_NAME) return methodName();
	if (a == Attribute.IS_METHOD_STATIC) return isMethodStatic();
	if (a == Attribute.THIS_OBJECT) return thisObject();
	if (a == Attribute.THIS_OBJECT_CLASS) return thisObjectClass();
	if (a == Attribute.RETURN_TYPE) return returnType();
	if (a == Attribute.PARAMETERS) return parameters();
	if (a == Attribute.PARAMETER_VALUE0) return parameterValue(0);
	if (a == Attribute.PARAMETER_VALUE1) return parameterValue(1);
	if (a == Attribute.PARAMETER_VALUE2) return parameterValue(2);
	if (a == Attribute.PARAMETER_VALUE3) return parameterValue(3);
	if (a == Attribute.PARAMETER_VALUE4) return parameterValue(4);
	if (a == Attribute.PARAMETER_VALUE5) return parameterValue(5);
	if (a == Attribute.PARAMETER_VALUE6) return parameterValue(6);
	if (a == Attribute.PARAMETER_VALUE7) return parameterValue(7);
	if (a == Attribute.PARAMETER_VALUE8) return parameterValue(8);
	if (a == Attribute.PARAMETER_VALUE9) return parameterValue(9);
	if (a == Attribute.PRINT_STRING) return this.toString();
	if (a instanceof AttributeVariable) return variableValue((AttributeVariable)a);
	return Value.INVALID;
    }

    // These are the FAST public methods this class provides.

    public abstract Value getPort();
    public int getSourceLine() {return SourceLine.getSourceLine(slIndex).getLine();}			// SLOW: 23
    public String getSourceFile() {return SourceLine.getSourceLine(slIndex).getFile();}			// SLOW: "My.java"
    public String getSourceFileLine() {return SourceLine.getSourceLine(slIndex).getFileLine();}		// FAST: "My.java:23"
    public Object getThread() {return tl.getThread();}
    public Object getThreadClass() {return getThread().getClass();}
    public String getMethodName() {return tl.getMethod();}
    public boolean getIsMethodStatic() {return(tl.thisObj instanceof Class);}
    public Object getThisObject() {return((tl.thisObj instanceof Class) ? Value.INVALID : tl.thisObj);}
    public Object getThisObjectClass() {return((tl.thisObj instanceof Class) ? tl.thisObj : tl.thisObj.getClass());}
    public int getnParameters() {return tl.getArgCount();}
    public Object getReturnType() {
	Locals l = tl.locals;
	if (l == null) return Value.INVALID;
	return l.getReturnType();
    }



    
    // Constructors

    EventImpl() {}

    protected void set_(int time) {
	TraceLine tl = TimeStamp.getPreviousBalancedTrace(time);
	if ((tl == null) || (tl == TraceLine.TOP_TRACELINE)) throw new LSDException("No TraceLine for time " +time +" found.");// NOT A BUG
	this.tl = tl;
	this.slIndex = TimeStamp.getSourceIndex(time);
	this.time = time;
	parameters = null;
    }

    protected void set_(int slIndex, TraceLine tl) {
	this.tl = tl;
	this.slIndex = slIndex;
	parameters = null;
    }



    // **************** AVs only! Don't confuse w/parameters

    public Object variableValue(AttributeVariable a) {
	Attribute attr = a.attr;
	Object o = getAttrValue(attr);
	return getInstanceVarValue(o, a.varName);
    }

    public Object getInstanceVarValue(Object o, String varName) {
	Shadow sh = Shadow.getNoCreation(o);
	if (sh == null)  return Value.INVALID;
	int varIndex = sh.classInfo.getVarIndex(varName);
	if (varIndex == -1) return Value.INVALID;
	return getInstanceVarValue(o, varIndex);
    }

    public Object getInstanceVarValue(Object o, int varIndex) {
	if (varIndex == -1) return Value.INVALID;
	Shadow sh = Shadow.getNoCreation(o);
	if (sh == null)  return Value.INVALID;
	int nVars = sh.size();
	if (varIndex >= nVars) return Value.INVALID;
	HistoryList hl = sh.getShadowVar(varIndex);
	if (hl == null) return Value.INVALID;
	return hl.valueOn(time, false);
    }
	
	

    // **************** AVs only! Don't confuse w/parameters




    // ******* Pameters below ********
    public int getParameterIndex(String varName) {
	if (tl == null) return -1;
	Locals l = tl.locals;
	if (l == null) return -1;
	return l.getVarIndex(varName);
    }

    public Object getParameterValue(int argN) {
	if (tl == null) return Value.INVALID;
	Locals l = tl.locals;
	if (l == null) return Value.INVALID;
	int nArgs = tl.getArgCount();
	if (nArgs <= argN) return Value.INVALID;
	Object value = tl.getArg(argN);
	return value;
    }

    public Object getParameterName(int argN) {
	if (tl == null) return Value.INVALID;
	Locals l = tl.locals;
	if (l == null) return Value.INVALID;
	int nArgs = tl.getArgCount();
	if (nArgs <= argN) return Value.INVALID;
	Object value = tl.getVarType(argN);
	return value;
    }

    public Object getParameterType(int argN) {
	if (tl == null) return Value.INVALID;
	Locals l = tl.locals;
	if (l == null) return Value.INVALID;
	int nArgs = tl.getArgCount();
	if (nArgs <= argN) return Value.INVALID;
	Object value = l.getVarName(argN);
	return value;
    }




    // These are the SLOW  methods the parent class provides. (All others return INVALID)

    public int time() {return time;}
    public Object port() {return getPort().getValue();}		// Must use "ENTER"
    public Object sourceLine() {return ShadowInt.createShadowInt(getSourceLine());}
    public Object sourceFile() {return getSourceFile();}
    public Object thread() {return getThread();}
    public Object threadClass() {return getThreadClass();}
    public Object methodName() {return getMethodName();}
    public Object isMethodStatic() {return ShadowBoolean.createShadowBoolean(getIsMethodStatic());}
    public Object thisObject() {return getThisObject();}
    public Object thisObjectClass()  {return getThisObjectClass();}
    public Object returnType()  {return getReturnType();}

    public Object parameterValue(int i) {return getParameterValue(i);}

    //->    <Tuple[  <Tuple[<Object "i">, <Object INT>, <Object 42>]>,   <Tuple[<Object "s">, <Object String>, <Object "Hi">]>, ...]>
    public Object parameters() {
	if (parameters != null) return parameters;

	Locals l = tl.locals;
	if (l == null) return new Tuple(new Object[0]);		// TOP_TRACELINE: throw new LSDException("Can't find locals "+ tl); 
	int nArgs = tl.getArgCount();
	Tuple[] tuples = new Tuple[nArgs];

	for (int i = 0; i < nArgs; i++) {
	    Object value = tl.getArg(i);
	    String name = l.getVarName(i);
	    Class type = l.getVarType(i);
	    Object[] v = {(name), (type), (value)};
	    Tuple t = new Tuple(v);
	    tuples[i] = t;
	}
	parameters = new Tuple(tuples);
	return parameters;
    }


    // Print stuff



    public Object thisObjectString() {
	return printString(thisObject());
    }
    public Object thisObjectClassString() {
	return printString(thisObjectClass());
    }






    public static void main(String[] args) {
	String[] args1 = {"com.lambda.Debugger.Demo"};
	Debugger.main(args1);
	resetIndex();

	while (moreEvents()) {
	    Event e = nextEvent();
	    //if (e == null) continue;
	    System.out.println((Class)int.class);
	}

	//LSD.dump();

    }



    // **************** The new *faster* interfaces. ****************
    public int getNewValueType() {return -1;}
    public boolean getNewValueZ() {return false;}
    public byte getNewValueB() {return (byte)-1;}
    public char getNewValueC() {return ' ';}
    public short getNewValueS() {return (short)-1;}
    public int getNewValueI() {return -1;}
    public long getNewValueL() {return (long)-1;}
    public float getNewValueF() {return (float)-1.0;}
    public double getNewValueD() {return -1.0;}
    public Object getNewValueA() {return Value.INVALID;}

    public int getReturnValueType() {return -1;}
    public boolean getReturnValueZ() {return false;}
    public byte getReturnValueB() {return -1;}
    public char getReturnValueC() {return ' ';}
    public short getReturnValueS() {return -1;}
    public int getReturnValueI() {return -1;}
    public long getReturnValueL() {return (long)-1;}
    public float getReturnValueF() {return (float)-1.0;}
    public double getReturnValueD() {return -1.0;}
    public Object getReturnValueA() {return Value.INVALID;}

    public String getCallMethodName() {return "NoCallMethodName";}
    public int getnArguments() {return 0;}
    public String getVarName() {return "NoVarName";}
    public Object getVarType() {return Value.INVALID;}
    public boolean getIsIVarStatic() {return false;}
    public Object getIVObject() {return Value.INVALID;}
    public Object getIVObjectClass() {return Value.INVALID;}
    public Object getOldValue() {return Value.INVALID;}
    public boolean getIsCallMethodStatic() {return false;}
    public Object getCallObject() {return Value.INVALID;}
    public Object getCallObjectClass() {return Value.INVALID;}
    public Object getReturnValue() {return Value.INVALID;}
    public Object getNewValue() {return Value.INVALID;}
	
    public Object getArgumentValue(int i) {return Value.INVALID;}





    public String toString() {
	return "<Event "+ printString() + ">";
    }


    public String printString() {
	String s = getSourceFileLine()+" "+thread()+" "+thisObjectClassString()+" "+thisObjectString()+"."+methodName()+"(";
	int len = getnParameters();
	for (int i = 0; i < len; i++) {
	    s += printString(getParameterValue(i));
	    if (i < len-1) s+= ", ";
	}
	s += ")";
	return s;
    }

}
