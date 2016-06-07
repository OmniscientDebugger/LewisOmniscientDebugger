/*                        Attribute.java

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

public class Attribute {

    // For all Events
    static Attribute		PORT = new Attribute("PORT");
    static Attribute		SOURCE_LINE = new Attribute("SOURCE_LINE");
    static Attribute		SOURCE_FILE = new Attribute("SOURCE_FILE");
    static Attribute		THREAD = new Attribute("THREAD");
    static Attribute		THREAD_CLASS = new Attribute("THREAD_CLASS");
    static Attribute		THIS_OBJECT = new Attribute("THIS_OBJECT");
    static Attribute		THIS_OBJECT_CLASS = new Attribute("THIS_OBJECT_CLASS");
    static Attribute		METHOD_NAME = new Attribute("METHOD_NAME");
    static Attribute		IS_METHOD_STATIC = new Attribute("IS_METHOD_STATIC");
    static Attribute		PARAMETERS = new Attribute("PARAMETERS");
    static Attribute		PARAMETER_VALUE0 = new Attribute("PARAMETER_VALUE0");
    static Attribute		PARAMETER_VALUE1 = new Attribute("PARAMETER_VALUE1");
    static Attribute		PARAMETER_VALUE2 = new Attribute("PARAMETER_VALUE2");
    static Attribute		PARAMETER_VALUE3 = new Attribute("PARAMETER_VALUE3");
    static Attribute		PARAMETER_VALUE4 = new Attribute("PARAMETER_VALUE4");
    static Attribute		PARAMETER_VALUE5 = new Attribute("PARAMETER_VALUE5");
    static Attribute		PARAMETER_VALUE6 = new Attribute("PARAMETER_VALUE6");
    static Attribute		PARAMETER_VALUE7 = new Attribute("PARAMETER_VALUE7");
    static Attribute		PARAMETER_VALUE8 = new Attribute("PARAMETER_VALUE8");
    static Attribute		PARAMETER_VALUE9 = new Attribute("PARAMETER_VALUE9");
    static Attribute		PRINT_STRING = new Attribute("PRINT_STRING");


    // For CallEvents
    static Attribute		CALL_METHOD_NAME = new Attribute("CALL_METHOD_NAME");
    static Attribute		IS_CALL_METHOD_STATIC = new Attribute("IS_CALL_METHOD_STATIC");
    static Attribute		CALL_OBJECT = new Attribute("CALL_OBJECT");
    static Attribute		CALL_OBJECT_CLASS = new Attribute("CALL_OBJECT_CLASS");
    static Attribute		CALL_RETURN_TYPE = new Attribute("CALL_RETURN_TYPE");
    static Attribute		CALL_ARGUMENTS = new Attribute("CALL_ARGUMENTS");
    static Attribute		CALL_ARGUMENT_VALUE0 = new Attribute("CALL_ARGUMENT_VALUE0");
    static Attribute		CALL_ARGUMENT_VALUE1 = new Attribute("CALL_ARGUMENT_VALUE1");
    static Attribute		CALL_ARGUMENT_VALUE2 = new Attribute("CALL_ARGUMENT_VALUE2");
    static Attribute		CALL_ARGUMENT_VALUE3 = new Attribute("CALL_ARGUMENT_VALUE3");
    static Attribute		CALL_ARGUMENT_VALUE4 = new Attribute("CALL_ARGUMENT_VALUE4");
    static Attribute		CALL_ARGUMENT_VALUE5 = new Attribute("CALL_ARGUMENT_VALUE5");
    static Attribute		CALL_ARGUMENT_VALUE6 = new Attribute("CALL_ARGUMENT_VALUE6");
    static Attribute		CALL_ARGUMENT_VALUE7 = new Attribute("CALL_ARGUMENT_VALUE7");
    static Attribute		CALL_ARGUMENT_VALUE8 = new Attribute("CALL_ARGUMENT_VALUE8");
    static Attribute		CALL_ARGUMENT_VALUE9 = new Attribute("CALL_ARGUMENT_VALUE9");

    // For ReturnEvents
    static Attribute		RETURN_TYPE = new Attribute("RETURN_TYPE");
    static Attribute		RETURN_VALUE = new Attribute("RETURN_VALUE");

    // For ExitEvents (the two above)

    // For ChangeLocalEvents
    static Attribute		NAME = new Attribute("NAME");
    static Attribute		TYPE = new Attribute("TYPE");
    static Attribute		NEW_VALUE = new Attribute("NEW_VALUE");
    static Attribute		OLD_VALUE = new Attribute("OLD_VALUE");

    // For ChangeIVEvents (along with: NEW_VALUE, OLD_VALUE, NAME, TYPE)
    static Attribute		OBJECT = new Attribute("OBJECT");
    static Attribute		OBJECT_CLASS = new Attribute("OBJECT_CLASS");
    static Attribute		IS_IVAR_STATIC = new Attribute("IS_IVAR_STATIC");

    // For ChangeArrayEvents (along with: NEW_VALUE, OLD_VALUE, OBJECT, OBJECT_CLASS)
    static Attribute		INDEX = new Attribute("INDEX");
    static Attribute		ARRAY = new Attribute("ARRAY");
    static Attribute		ARRAY_CLASS = new Attribute("ARRAY_CLASS");


    // For LockEvents
    static Attribute		LOCK_TYPE = new Attribute("LOCK_TYPE");
    static Attribute		BLOCKED_ON_OBJECT = new Attribute("BLOCKED_ON_OBJECT");
    static Attribute		BLOCKED_ON_OBJECT_CLASS = new Attribute("BLOCKED_ON_OBJECT_CLASS");

    // For CatchEvents 
    static Attribute		EXCEPTION = new Attribute("EXCEPTION");
    static Attribute		EXCEPTION_CLASS = new Attribute("EXCEPTION_CLASS");
    static Attribute		THROWING_METHOD_NAME = new Attribute("THROWING_METHOD_NAME");


    // For State Queries
    static Attribute		STACK_FRAMES = new Attribute("STACK_FRAMES");
    static Attribute		OBJECTS = new Attribute("OBJECTS");
    static Attribute		VARIABLE0 = new Attribute("VARIABLE0");
    static Attribute		VARIABLE1 = new Attribute("VARIABLE1");
    static Attribute		VARIABLES = new Attribute("VARIABLES");

    public final String		name;

    // Constructors

    Attribute(String s) {name=s;}
    //    public static Attribute create(String s) {return new Attribute(s);}



    public static Attribute find(String s) {		// Used only by parser
	int dot = s.indexOf('.');
	if (dot != -1) {
	    String port = s.substring(0, dot);
	    String var = s.substring(dot+1, s.length());
	    var = var.intern();
	    Attribute a = find(port);
	    if (port == null) return null;
	    return AttributeVariable.create(var, a);
	}
	if (s.equals("port") || s.equals("p")) return PORT;
	if (s.equals("sourceLine") || s.equals("sl")) return SOURCE_LINE;
	if (s.equals("sourceFile") || s.equals("sf")) return SOURCE_FILE;
	if (s.equals("thread") || s.equals("thr")) return THREAD;
	if (s.equals("threadClass") || s.equals("thrc")) return THREAD_CLASS;
	if (s.equals("thisObject") || s.equals("to")) return THIS_OBJECT;
	if (s.equals("thisObjectClass") || s.equals("toc")) return THIS_OBJECT_CLASS;
	if (s.equals("methodName") || s.equals("mn")) return METHOD_NAME;
	if (s.equals("isMethodStatic") || s.equals("ims")) return IS_METHOD_STATIC;
	if (s.equals("parameters") || s.equals("params")) return PARAMETERS;
	if (s.equals("callMethodName") || s.equals("cmn")) return CALL_METHOD_NAME;
	if (s.equals("isCallMethodStatic") || s.equals("icms")) return IS_CALL_METHOD_STATIC;
	if (s.equals("callObject") || s.equals("co")) return CALL_OBJECT;
	if (s.equals("callObjectClass") || s.equals("coc")) return CALL_OBJECT_CLASS;
	if (s.equals("callArguments") || s.equals("args")) return CALL_ARGUMENTS;
	if (s.equals("returnType") || s.equals("rt")) return RETURN_TYPE;
	if (s.equals("returnValue") || s.equals("rv")) return RETURN_VALUE;
	if (s.equals("name") || s.equals("varName") || s.equals("vn")) return NAME;
	if (s.equals("type") || s.equals("varType") || s.equals("vt")) return TYPE;
	if (s.equals("newValue") || s.equals("nv")) return NEW_VALUE;
	if (s.equals("oldValue") || s.equals("ov")) return OLD_VALUE;
	if (s.equals("object") || s.equals("o")) return OBJECT;
	if (s.equals("lockType") || s.equals("lt")) return LOCK_TYPE;
	if (s.equals("objectClass") || s.equals("oc")) return OBJECT_CLASS;
	if (s.equals("isIvarStatic") || s.equals("iivs")) return IS_IVAR_STATIC;
	if (s.equals("blockedOnObject") || s.equals("boo")) return BLOCKED_ON_OBJECT;
	if (s.equals("blockedOnObjectClass") || s.equals("booc")) return BLOCKED_ON_OBJECT_CLASS;
	if (s.equals("exception") || s.equals("ex")) return EXCEPTION;
	if (s.equals("exceptionClass") || s.equals("exc")) return EXCEPTION_CLASS;
	if (s.equals("throwingMethodName") || s.equals("thn")) return THROWING_METHOD_NAME;
	if (s.equals("callArgumentValue0") || s.equals("a0") || s.equals("arg0")) return CALL_ARGUMENT_VALUE0;
	if (s.equals("callArgumentValue1") || s.equals("a1") || s.equals("arg1")) return CALL_ARGUMENT_VALUE1;
	if (s.equals("callArgumentValue2") || s.equals("a2") || s.equals("arg2")) return CALL_ARGUMENT_VALUE2;
	if (s.equals("callArgumentValue3") || s.equals("a3") || s.equals("arg3")) return CALL_ARGUMENT_VALUE3;
	if (s.equals("callArgumentValue4") || s.equals("a4") || s.equals("arg4")) return CALL_ARGUMENT_VALUE4;
	if (s.equals("callArgumentValue5") || s.equals("a5") || s.equals("arg5")) return CALL_ARGUMENT_VALUE5;
	if (s.equals("callArgumentValue6") || s.equals("a6") || s.equals("arg6")) return CALL_ARGUMENT_VALUE6;
	if (s.equals("callArgumentValue7") || s.equals("a7") || s.equals("arg7")) return CALL_ARGUMENT_VALUE7;
	if (s.equals("callArgumentValue8") || s.equals("a8") || s.equals("arg8")) return CALL_ARGUMENT_VALUE8;
	if (s.equals("callArgumentValue9") || s.equals("a9") || s.equals("arg9")) return CALL_ARGUMENT_VALUE9;
	if (s.equals("parameterValue0") || s.equals("p0")) return PARAMETER_VALUE0;
	if (s.equals("parameterValue1") || s.equals("p1")) return PARAMETER_VALUE1;
	if (s.equals("parameterValue2") || s.equals("p2")) return PARAMETER_VALUE2;
	if (s.equals("parameterValue3") || s.equals("p3")) return PARAMETER_VALUE3;
	if (s.equals("parameterValue4") || s.equals("p4")) return PARAMETER_VALUE4;
	if (s.equals("parameterValue5") || s.equals("p5")) return PARAMETER_VALUE5;
	if (s.equals("parameterValue6") || s.equals("p6")) return PARAMETER_VALUE6;
	if (s.equals("parameterValue7") || s.equals("p7")) return PARAMETER_VALUE7;
	if (s.equals("parameterValue8") || s.equals("p8")) return PARAMETER_VALUE8;
	if (s.equals("parameterValue9") || s.equals("p9")) return PARAMETER_VALUE9;
	if (s.equals("objects") || s.equals("os")) return OBJECTS;
	if (s.equals("var0") || s.equals("v0")) return VARIABLE0;
	if (s.equals("var1") || s.equals("v1")) return VARIABLE1;
	if (s.equals("vars") || s.equals("vs")) return VARIABLES;

	if (s.equals("index") || s.equals("i")) return INDEX;
	if (s.equals("array") || s.equals("a")) return ARRAY;
	if (s.equals("arrayClass") || s.equals("ac")) return ARRAY_CLASS;

	if (s.equals("stackFrames") || s.equals("sf")) return STACK_FRAMES;
	if (s.equals("printString") || s.equals("ps")) return PRINT_STRING;

	return null;
    }




    public String toString() {
	return name;
    }


}
