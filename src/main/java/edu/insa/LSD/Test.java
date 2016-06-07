/*                        Test.java

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

public class Test {


    public static void main(String[] args) {

	String[] dargs = {"com.lambda.Debugger.Demo"};
	if (args.length > 0) dargs = args;
	test1();
	Debugger.runLSD(dargs, true);
	//	runTests();
    }

    /*
fget(port = chgInstanceVar & varType = #int & varName = P)

Then we have :Initial array of conditions :
<PATTERN :
         CONDITION : PORT UNIFY(8) "CHGINSTANCEVAR"
         CONDITION : TYPE UNIFY(8) class com.lambda.Debugger.ShadowInt
         CONDITION : NAME UNIFY(8) P = <V NOVALUE>
*/


    public static void test1() {
	VariableValue.clear();
	//	String pattern = "sl = X";    //"port = chgInstanceVar & varType = #int & varName = P";
	//String pattern = "port = PORT & sourceLine = SOURCELINE & sourceFile = SOURCEFILE & thread = THREAD & threadClass = THREADCLASS & thisObject = THISOBJECT & thisObjectClass = THISOBJECTCLASS & methodName = METHODNAME & isMethodStatic = ISMETHODSTATIC & parameters = PARAMETERS & returnType = RETURNTYPE";

	//String pattern = "parameterValue0 = PARAMETERVALUE0 & parameterValue1 = PARAMETERVALUE1 & parameterValue2 = PARAMETERVALUE2 & parameterValue3 = PARAMETERVALUE3 & parameterValue4 = PARAMETERVALUE4 & parameterValue5 = PARAMETERVALUE5 & parameterValue6 = PARAMETERVALUE6 & parameterValue7 = PARAMETERVALUE7 & parameterValue8 = PARAMETERVALUE8 & parameterValue9 = PARAMETERVALUE9";

	//	String pattern = "callMethodName = CALLMETHODNAME & isCallMethodStatic = ISCALLMETHODSTATIC & callObject = CALLOBJECT & callObjectClass = CALLOBJECTCLASS & callArguments = CALLARGUMENTS & returnType = RETURNTYPE & callArgumentValue0 = CALLARGUMENTVALUE0 & callArgumentValue1 = CALLARGUMENTVALUE1 & callArgumentValue2 = CALLARGUMENTVALUE2 & callArgumentValue3 = CALLARGUMENTVALUE3 & callArgumentValue4 = CALLARGUMENTVALUE4 & callArgumentValue5 = CALLARGUMENTVALUE5 & callArgumentValue6 = CALLARGUMENTVALUE6 & callArgumentValue7 = CALLARGUMENTVALUE7 & callArgumentValue8 = CALLARGUMENTVALUE8 & callArgumentValue9 = CALLARGUMENTVALUE9";

	//String pattern = "name = NAME & type = TYPE & newValue = NEWVALUE &  object = OBJECT & objectClass = OBJECTCLASS & isIvarStatic = ISIVARSTATIC";

	//	String pattern = "callMethodName = CALLMETHODNAME & isCallMethodStatic = ISCALLMETHODSTATIC & returnValue = RETURNVALUE  & returnType = RETURNTYPE & callObjectClass = CALLOBJECTCLASS & callObject = CALLOBJECT";

	//	String pattern = "lockType = LOCKTYPE & blockedOnObject = BLOCKEDONOBJECT & blockedOnObjectClass = BLOCKEDONOBJECTCLASS";

	String pattern = "exception = EXCEPTION & exceptionClass = EXCEPTIONCLASS";// & throwingMethodName = THROWINGMETHODNAME";

	EventPattern ep = FGetParser.parse(pattern, false);

	ep.print();
	//	ep.optimize();
	//	ep.print();

	EventInterface.setPausePattern(ep);
	EventInterface.setPauseCallback(new TestCallback());
    }



    public static void test1a() {
	// fget(port = enter   and    methodName = "average")

	VariableValue.clear();
	//<EventPattern: <Condition SOURCE_LINE UNIFY 8> <Condition SOURCE_FILE UNIFY "Test2.java">>"

	Condition c1 = new Condition(Attribute.SOURCE_LINE,	Operator.UNIFY,		new Value(8));
	Condition c0 = new Condition(Attribute.SOURCE_FILE,	Operator.UNIFY,		new Value("Test2.java"));

	Condition[] cs = {c1, c0};
	EventPattern ep = new EventPattern(cs);

	ep.print();
	ep.optimize();
	ep.print();

	//	EventInterface.setPausePattern(ep);
	EventInterface.setPausePattern(ep);
	EventInterface.setPauseCallback(new TestCallback());
    }














    public static void runTests() {
	//	EventInterface.dump();
	//	Event.dump();
	runTest5();
    }

    public static void runTest1() {
	int nEvents = 1118;
	long start = System.currentTimeMillis();
	for (int i = 0; i < 1000; i++) test1();
	long end = System.currentTimeMillis();
	long ave =  (end-start);

	if (ave >= 10000000)
	    System.out.println("For " + nEvents + " events tested: " + ave/1000000 + "Ms");
	else
	    if (ave >= 10000)
		System.out.println("For " + nEvents + " events tested: " + ave/1000 + "s");
	    else
		System.out.println("For " + nEvents + " events tested: " + ave/1 + "ms");
    }


    public static void runTest3() {


	{
	    // fget(port = enter   and   thisObject = THIS    and    isMethodStatic <> true   and   methodName = NAME  and  sourceLine = SL)
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY, 	ConstantValue.ENTER);
	    Condition c2 = new Condition(Attribute.THIS_OBJECT, 	Operator.UNIFY, 	VariableValue.create("THIS"));
	    Condition c3 = new Condition(Attribute.IS_METHOD_STATIC,	Operator.NEQ, 		ConstantValue.TRUE);
	    Condition c4 = new Condition(Attribute.METHOD_NAME,     	Operator.UNIFY,		VariableValue.create("NAME"));
	    Condition c5 = new Condition(Attribute.SOURCE_LINE,     	Operator.UNIFY,		VariableValue.create("SL"));
	    Condition[] cs = {c1, c2, c3, c4, c5};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}


	{
	    // fget(port = enter   and   thisObject = THIS    and   thisObjectClass = THIS)
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY, 	ConstantValue.ENTER);
	    Value v = VariableValue.create("THIS");
	    Condition c2 = new Condition(Attribute.THIS_OBJECT, 	Operator.UNIFY, 	v);
	    Condition c3 = new Condition(Attribute.THIS_OBJECT_CLASS,	Operator.UNIFY,		v);
	    Condition[] cs = {c1, c2, c3};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}


	{
	    // fget(port = changeIV   and   object = OBJ    and   newValue = 1)
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY, 	ConstantValue.CHGINSTANCEVAR);
	    Condition c2 = new Condition(Attribute.OBJECT,	 	Operator.UNIFY, 	VariableValue.create("OBJ"));
	    Condition c3 = new Condition(Attribute.NEW_VALUE,		Operator.UNIFY,		new ConstantValue(1));
	    Condition[] cs = {c1, c2, c3};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}

	{
	    // fget(port = changeLocal   and   methodName = MN   and   newValue > 12)
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY, 	ConstantValue.CHGLOCALVAR);
	    Condition c2 = new Condition(Attribute.METHOD_NAME,	 	Operator.UNIFY, 	VariableValue.create("MN"));
	    Condition c3 = new Condition(Attribute.NEW_VALUE,		Operator.GT,		new ConstantValue(12));
	    Condition[] cs = {c1, c2, c3};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}

	{
	    // fget(port in [enter, exit]   methodName = MN   and   isMethodStatic = true)
	    Object[] o4 = {ConstantValue.ENTER, ConstantValue.EXIT};
	    Tuple t = new Tuple(o4);
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.IN, 		t);
	    Condition c2 = new Condition(Attribute.METHOD_NAME,	 	Operator.UNIFY, 	VariableValue.create("MN"));
	    Condition c3 = new Condition(Attribute.IS_METHOD_STATIC,	Operator.UNIFY,		ConstantValue.TRUE);
	    Condition[] cs = {c1, c2, c3};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}

	{
	    // fget(port = enter   and    methodName = MN   and   parameters = [[V1N, V1T, 0] | VR])
	    Object[] o4 = {VariableValue.create("V1N"), VariableValue.create("V1T"), new Value(0)};
	    //Object[] o4 = {VariableValue.create("V1N"), VariableValue.create("V1T"), VariableValue.create("V1V")};
	    Tuple t1 = new Tuple(o4);
	    Object[] v2 = {t1};
	    Tuple t2 = new Tuple(v2, VariableValue.create("VR"));
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY,		ConstantValue.ENTER);
	    Condition c2 = new Condition(Attribute.METHOD_NAME,	 	Operator.UNIFY, 	VariableValue.create("MN"));
	    Condition c3 = new Condition(Attribute.PARAMETERS,		Operator.UNIFY,		t2);
	    Condition[] cs = {c1, c2, c3};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}


	{
	    // fget(port = enter   and    methodName = MN   and   parameters = [["start", V1, V2 | VR] | OTHERS])
	    Object[] o1 = {new Value("start"), VariableValue.create("V1"), VariableValue.create("V2")};
	    Tuple t1 = new Tuple(o1, VariableValue.create("VR"));
	    Object[] o2 = {t1};
	    Tuple t2 = new Tuple(o2, VariableValue.create("OTHERS"));
	    
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY,		ConstantValue.ENTER);
	    Condition c2 = new Condition(Attribute.METHOD_NAME,	 	Operator.UNIFY, 	VariableValue.create("MN"));
	    Condition c3 = new Condition(Attribute.PARAMETERS,		Operator.UNIFY,		VariableValue.create("VN"));
	    Condition c4 = new Condition(Attribute.PARAMETERS,		Operator.UNIFY,		t2);
	    Condition[] cs = {c1, c2, c3, c4};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}
    }


    public static void runTest2() {
	{
	    // fget(port = enter   and    parameters = VARS   and   parameters = [[_, V1T, V1V], [_, V1T, V2V] | OTHERS])
	    VariableValue.clear();
	    VariableValue V1T = VariableValue.create("V1T");
	    Object[] o1 = {VariableValue.ANYVALUE, V1T, VariableValue.create("V1V")};
	    Tuple t1 = new Tuple(o1);
	    Object[] o2 = {VariableValue.ANYVALUE, V1T, VariableValue.create("V2V")};
	    Tuple t2 = new Tuple(o2);
	    Object[] o3 = {t1, t2};
	    Tuple t3 = new Tuple(o3, VariableValue.create("OTHERS"));
	    
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY,		ConstantValue.ENTER);
	    Condition c3 = new Condition(Attribute.PARAMETERS,		Operator.UNIFY,		VariableValue.create("VARS"));
	    Condition c4 = new Condition(Attribute.PARAMETERS,		Operator.UNIFY,		t3);
	    Condition[] cs = {c1, c3, c4};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}
	QueryFGet.dump();// bugs in the Debugger on this one!
	//	EventInterface.dump();

    }

    public static void runTest4() {
	{
	    // fget(port = enter   and    parameters = VARS   and   parameters = [[_, int, V1V], [_, V1T, V2V] | OTHERS])
	    VariableValue.clear();
	    VariableValue V1T = VariableValue.create("V1T");
	    Object[] o1 = {VariableValue.ANYVALUE, ShadowInt.class, VariableValue.create("V1V")};
	    Tuple t1 = new Tuple(o1);
	    Object[] o2 = {VariableValue.ANYVALUE, V1T, VariableValue.create("V2V")};
	    Tuple t2 = new Tuple(o2);
	    Object[] o3 = {t1, t2};
	    Tuple t3 = new Tuple(o3, VariableValue.create("OTHERS"));
	    
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY,		ConstantValue.ENTER);
	    Condition c3 = new Condition(Attribute.PARAMETERS,		Operator.UNIFY,		VariableValue.create("VARS"));
	    Condition c4 = new Condition(Attribute.PARAMETERS,		Operator.UNIFY,		t3);
	    Condition[] cs = {c1, c3, c4};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}
	QueryFGet.dump();// bugs in the Debugger on this one!
	//	EventInterface.dump();

    }


    public static void runTest5() {
	{
	    // fget(port = enter   and    parameters = VARS   and   objectClass = Demo)
	    VariableValue.clear();
	    
	    Condition c1 = new Condition(Attribute.PORT, 		Operator.UNIFY,		ConstantValue.ENTER);
	    Condition c3 = new Condition(Attribute.PARAMETERS,		Operator.UNIFY,		VariableValue.create("VARS"));
	    Condition c4 = new Condition(Attribute.THIS_OBJECT_CLASS,	Operator.UNIFY,		new Value(Demo.class));
	    Condition[] cs = {c1, c3, c4};
	    EventPattern ep = new EventPattern(cs);

	    ep.print();

	    Event.resetIndex();
	    while (Event.moreEvents()) {
		Event e = QueryFGet.nextMatch(ep);
		if (e == null) break;
		System.out.println("Matched: "+e);
		VariableValue.printVars();
	    }
	    System.out.println("No more events.");
	}
	//EventPattern.dump();// bugs in the Debugger on this one!
	//	EventInterface.dump();

    }

}
