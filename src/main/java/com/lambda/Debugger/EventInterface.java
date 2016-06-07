/*                        EventInterface.java

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

import java.util.*;
import edu.insa.LSD.*;



public class EventInterface  {

    // The interface for LSD is 2 methods.
    public static void setPauseCallback(PauseCallback tc) {pause = tc;}	
    public static void setPausePattern(EventPattern pattern) {
	pausePattern = pattern;
	setCheckPause(true, pattern);
    }



    // Additional methods for starting/stopping/testing
    public static void setStartPattern(EventPattern pattern) {
	startPattern = pattern;
	setCheckStart(true, pattern);
	if (Debugger.PAUSED) Debugger.println("Recording is currently off. You must turn it on before the pattern can be matched.");
	Debugger.println("Recording will start when this pattern is matched:\n"+startPattern);
	}

    public static void setStopPattern(EventPattern pattern) {
	stopPattern = pattern;
	setCheckStop(true, pattern);
    }

    public static void setTestCallback(PauseCallback tc) {test = tc;}	

    

    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************





    public static void setCheckStart(boolean startp, EventPattern ep) {
	String port = null, methodName = null;
	CHECK_START = startp;
	boolean checkNewValueI = false;
	int newValueI=-1;
	if (ep != null) {
	    ConstantValue cv = ep.getPort();
	    if (cv == null)
		port = null;
	    else 
		port = (String) cv.getValue();			// Must be a port constant!
	    checkNewValueI = ep.getCheckNewValueI();
	    newValueI =  ep.getNewValueI();
	}
	D.setCheckPattern(startp, startp, port, methodName, checkNewValueI, newValueI);
    }
    public static void setCheckPause(boolean z, EventPattern ep) {
	String port = null, methodName = null;
	CHECK_PAUSE = z;
	if (ep != null) {
	    ConstantValue cv = ep.getPort();
	    if (cv == null)
		port = null;
	    else
		port = (String) cv.getValue();			// Must be a port constant!
	}
	if (z) {D.setCheckPattern(z, z, port, methodName); }
    }
    public static void setCheckStop(boolean z, EventPattern ep) {
	String port = null, methodName = null;
	CHECK_START = z;
	if (ep != null) {
	    ConstantValue cv = ep.getPort();
	    if (cv == null)
		port = null;
	    else
		port = (String) cv.getValue();			// Must be a port constant!
	}
	if (z) 	D.setCheckPattern(z, !z, port, methodName);

    }




    private static Object[]		objects;
    private static Object[]		values;
    private static String[]		varNames;


    private static PauseCallback		test;
    private static PauseCallback		pause;

    public static void test() {
	initialize();
	if (test == null) {
	    test = PauseCallback.DEFAULT;
	}
	Event e = Event.createEvent(TimeStamp.ct());
	State state = RecordedState.createRecordedState(TimeStamp.ct());
	String s = test.run(pausePattern, e, state);
	Debugger.message(s, false); 
    }

    public static void record(int time, String varName, Object value) {
	objects[time] = null;
	values[time] = value;
	varNames[time] = varName;
    }

    public static void record(int time, Object object, String varName, Object value) {
	objects[time] = object;
	values[time] = value;
	varNames[time] = varName;
    }

    public static void dump() {
	for (int i = 0; i < TimeStamp.eott(); i++) {
	    if (varNames[i] == null) continue;
	    String o = TimeStamp.trim(objects[i]);
	    String v = TimeStamp.trim(values[i]);
	    if (objects[i] == null)
		System.out.println(""+i+": " + varNames[i] + " = " + v);
	    else
		System.out.println(""+i+": " +o+"." + varNames[i] + " = " + v);
	}
    }

    public static String  getVarName(int time) {return varNames[time];}
    public static Object  getObject(int time) {return objects[time];}
    public static Object  getValue(int time) {return values[time];}
    public static Object  getPreviousValue(int time, Object o) {		// For unlocking to find the lost object
	for (int i = time; i > -1; i--) if (o == objects[i]) return values[i];
	return null;
    }

    public static void fget(String s, boolean forward, boolean countp) {
	initialize();
	try {QueryFGet.fget(s, forward, countp);}
	catch (LSDException e) {throw new DebuggerException(e.getMessage());}
    }

    public static void cdata(String s, boolean forward, boolean countp) {
	initialize();
	State state = RecordedState.createRecordedState(TimeStamp.ct());
	try {QueryCdata.cdata(s, state, forward, countp);}
	catch (Exception e) {throw new DebuggerException(e.getMessage());}
    }

    public static void initialize() {
	synchronized(D.class) {
	    if (objects != null) return;
	    objects = new Object[TimeStamp.MAX_TIMESTAMPS];
	    values = new Object[TimeStamp.MAX_TIMESTAMPS];
	    varNames = new String[TimeStamp.MAX_TIMESTAMPS];

	    Shadow.initializeAllEvents();
	    TraceLine.initializeAllEvents();
	}
    }

    public static String previousPattern() {return QueryFGet.previousPattern();}
    public static void setPattern(String s) {QueryFGet.setPattern(s);}
    public static String previousCdataPattern() {return QueryCdata.previousPattern();}


    // **************** BELOW IS FOR DYNAMIC DECISIONS ON TURNING ON/OFF RECORDING ****************

    private static boolean		CHECK_START=false, CHECK_STOP=false, CHECK_PAUSE=false;

    private static EventPattern stopPattern = null;
    private static EventPattern startPattern = null;
    private static EventPattern pausePattern = null;





    public static boolean skip(Event event) {	// -> true   ==  don't record
	/*  Not implemented yet
	if (!D.DISABLE && CHECK_STOP)
	    if (matchStopRecording(event)) {
		synchronized(D.class) {
		    D.disable();
		    return true;
		}
	    }
	    else
		return false;
	*/
	if (D.CHECKING_START && CHECK_START) {
	    if (matchStartRecording(event)) {
		synchronized(D.class) {			// If checking is moved outside of synch(D) in D.java
		    System.out.println("Starting recording: " + event);
		    setCheckStart(false, null);
		    TraceLineReusable.assignTimeStamps();
		    return false;
		}
	    }
	    else
		return true;
	}
	if (CHECK_PAUSE && matchPauseRecording(event)) {
	    	if (pause == null) return false;
		initialize();
		State state = new StaticState(TraceLineReusable.getCurrentTL(), event);
		pause.run(pausePattern, event, state);
		return true;
	}
	return true;
    }




    // **************** GOBS OF INDIVIDUAL TESTS ****************


    // **************** Change Local ****************
    public static boolean skipLocalVarA(int slIndex, int varIndex, Object newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setA(slIndex, varIndex, newValue, tl);
	return skip(event);
    }
    public static boolean skipLocalVarZ(int slIndex, int varIndex, boolean newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setZ(slIndex, varIndex, newValue, tl);
	return skip(event);
    }
    public static boolean skipLocalVarB(int slIndex, int varIndex, byte newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setB(slIndex, varIndex, newValue, tl);
	return skip(event);
    }
    public static boolean skipLocalVarS(int slIndex, int varIndex, short newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setS(slIndex, varIndex, newValue, tl);
	return skip(event);
    }
    public static boolean skipLocalVarC(int slIndex, int varIndex, char newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setC(slIndex, varIndex, newValue, tl);
	return skip(event);
    }
    public static boolean skipLocalVarI(int slIndex, int varIndex, int newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setI(slIndex, varIndex, newValue, tl);
	return skip(event);
    }
    public static boolean skipLocalVarL(int slIndex, int varIndex, long newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setL(slIndex, varIndex, newValue, tl);
	return skip(event);
    }
    public static boolean skipLocalVarF(int slIndex, int varIndex, float newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setF(slIndex, varIndex, newValue, tl);
	return skip(event);
    }
    public static boolean skipLocalVarD(int slIndex, int varIndex, double newValue, TraceLine tl) {
	Event event = ChangeLocalVariableEvent.setD(slIndex, varIndex, newValue, tl);
	return skip(event);
    }


    // **************** Change IV ****************
    public static boolean skipIVarA(int slIndex, Object object, String varName, Object newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setA(slIndex, object, varName, newValue, tl);
	return skip(event);
    }
    public static boolean skipIVarZ(int slIndex, Object object, String varName, boolean newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setZ(slIndex, object, varName, newValue, tl);
	return skip(event);
    }
    public static boolean skipIVarB(int slIndex, Object object, String varName, byte newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setB(slIndex, object, varName, newValue, tl);
	return skip(event);
    }
    public static boolean skipIVarS(int slIndex, Object object, String varName, short newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setS(slIndex, object, varName, newValue, tl);
	return skip(event);
    }
    public static boolean skipIVarC(int slIndex, Object object, String varName, char newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setC(slIndex, object, varName, newValue, tl);
	return skip(event);
    }
    public static boolean skipIVarI(int slIndex, Object object, String varName, int newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setI(slIndex, object, varName, newValue, tl);
	return skip(event);
    }
    public static boolean skipIVarL(int slIndex, Object object, String varName, long newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setL(slIndex, object, varName, newValue, tl);
	return skip(event);
    }
    public static boolean skipIVarF(int slIndex, Object object, String varName, float newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setF(slIndex, object, varName, newValue, tl);
	return skip(event);
    }
    public static boolean skipIVarD(int slIndex, Object object, String varName, double newValue, TraceLine tl) {
	Event event = ChangeInstanceVariableEvent.setD(slIndex, object, varName, newValue, tl);
	return skip(event);
    }


    // **************** ARRAY ****************
    public static boolean skipArrayA(int slIndex, Object array, int index, Object newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setA(slIndex, array, index, newValue, tl);
	return skip(event);
    }
    public static boolean skipArrayZ(int slIndex, Object array, int index, boolean newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setZ(slIndex, array, index, newValue, tl);
	return skip(event);
    }
    public static boolean skipArrayB(int slIndex, Object array, int index, byte newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setB(slIndex, array, index, newValue, tl);
	return skip(event);
    }
    public static boolean skipArrayS(int slIndex, Object array, int index, short newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setS(slIndex, array, index, newValue, tl);
	return skip(event);
    }
    public static boolean skipArrayC(int slIndex, Object array, int index, char newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setC(slIndex, array, index, newValue, tl);
	return skip(event);
    }
    public static boolean skipArrayI(int slIndex, Object array, int index, int newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setI(slIndex, array, index, newValue, tl);
	return skip(event);
    }
    public static boolean skipArrayL(int slIndex, Object array, int index, long newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setL(slIndex, array, index, newValue, tl);
	return skip(event);
    }
    public static boolean skipArrayF(int slIndex, Object array, int index, float newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setF(slIndex, array, index, newValue, tl);
	return skip(event);
    }
    public static boolean skipArrayD(int slIndex, Object array, int index, double newValue, TraceLine tl) {
	Event event = ChangeArrayEvent.setD(slIndex, array, index, newValue, tl);
	return skip(event);
    }

    // **************** Methods ****************
    public static boolean skipEnter(int slIndex, TraceLine tl) {
	Event event = EnterEvent.set(slIndex, tl);
	return skip(event);
    }
    public static boolean skipExitA(int slIndex, Object returnValue, TraceLine tl) {
	Event event = ExitEvent.set(slIndex, returnValue, tl);
	return skip(event);
    }
    public static boolean skipReturn(Object returnValue, TraceLine tl) {
	if (tl == null) tl = TraceLine.previousTraceLine();		// yuck! Fix this. (ReturnEvents get tl==null)
	Event event = ReturnEvent.set(returnValue, tl);
	return skip(event);
    }

    public static boolean skipReturnNew(Object returnValue, TraceLine tl) {
	if (tl == null) tl = TraceLine.previousTraceLine();		// yuck! Fix this. (ReturnEvents get tl==null)
	Event event = ReturnEvent.set(returnValue, tl);
	Penumbra.record(returnValue);
	return skip(event);
    }

    public static boolean skipCall(int slIndex, Object o, String meth, TraceLine callTL, int nArgs,
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
	Event event = CallEvent.set(slIndex, o, meth, callTL, nArgs, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	return skip(event);
    }

    public static boolean skipGettingLock(int slIndex, Object obj, TraceLine tl) {
	Event event = LockEvent.set(slIndex, obj, ConstantValue.GETTING_LOCK, tl);
	return skip(event);
    }
    public static boolean skipGotLock(int slIndex, Object obj, TraceLine tl) {
	Event event = LockEvent.set(slIndex, obj, ConstantValue.GOT_LOCK, tl);
	return skip(event);
    }
    public static boolean skipReleasingLock(int slIndex, Object obj, TraceLine tl) {
	Event event = LockEvent.set(slIndex, obj, ConstantValue.RELEASING_LOCK, tl);
	return skip(event);
    }
    public static boolean skipStartingWait(int slIndex, Object obj, TraceLine tl) {
	Event event = LockEvent.set(slIndex, obj, ConstantValue.STARTING_WAIT, tl);
	return skip(event);
    }
    public static boolean skipEndingWait(int slIndex, Object obj, TraceLine tl) {
	Event event = LockEvent.set(slIndex, obj, ConstantValue.ENDING_WAIT, tl);
	return skip(event);
    }
    public static boolean skipStartingJoin(int slIndex, Object obj, TraceLine tl) {
	Event event = LockEvent.set(slIndex, obj, ConstantValue.STARTING_JOIN, tl);
	return skip(event);
    }
    public static boolean skipEndingJoin(int slIndex, Object obj, TraceLine tl) {
	Event event = LockEvent.set(slIndex, obj, ConstantValue.ENDING_JOIN, tl);
	return skip(event);
    }

    public static boolean skipCatch(int slIndex, Throwable ex, TraceLine tl) {
	Event event = CatchEvent.set(slIndex, ex, tl);
	return skip(event);
    }

    public static boolean skipNew(int slIndex, Object o, String meth, TraceLine tl, int nArgs,
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
	//	Penumbra.record(o);
	Event event = CallEvent.set(slIndex, o, meth, tl, nArgs, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	return skip(event);
    }

    //		****************************************************************


    
    public static boolean matchStartRecording(Event event) {
	if (startPattern == null) return false;
	boolean start = startPattern.match(event);
	return start;
    }

    public static boolean matchStopRecording(Event event) {
	return false;
    }

    public static boolean matchPauseRecording(Event event) {
	if (pausePattern == null) return false;
	boolean pause = pausePattern.match(event);
	return pause;
    }



    public static void dumpState() {
	TraceLine tl = TimeStamp.getPreviousBalancedTrace(TimeStamp.currentTime().time);
	State s = RecordedState.createRecordedState(tl);
	StackFrame[] sf = s.getAllStackFrames();
	s.printAll();
	Object[] os = s.getAllObjects();
    }


    private static String pausePatternString = null, startPatternString = null, stopPatternString = null;

    public static void setStartPatternString(String pattern) {
	startPatternString = pattern;
	EventPattern p = FGetParser.parse(pattern, false);
	p.optimize();
	setStartPattern(p);
	//	Debugger.println("recording will start when this pattern is matched:\n"+pattern);
    }

    public static void setStopPatternString(String pattern) {
	stopPatternString = pattern;
    }
    public static String getStartPatternString() {return startPatternString;}
    public static String getStopPatternString() {return stopPatternString;}

    public static Iterator getIterator() {return Penumbra.getIterator();}
}
