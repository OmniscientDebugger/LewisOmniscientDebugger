/*                        D.java

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

//              D/D.java

/*
 */

//import Debugger.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


public final class D {

    static HashMap 		classTable = new HashMap();		// "com.lambda.Debugger.Debugger" -> #class

    public static boolean	DEBUG_DEBUGGER=false;	// Set to false for users.
    public static boolean	recursiveEntry=false;

    public static boolean	DISABLE=true;
    public static boolean	KILL_TARGET=false;		// Kill all target threads (precidence)
    public static boolean	PAUSE_PROGRAM=false;		// Pause all target threads

    public static boolean	CHECK_PATTERN=false;			// For ANY pattern: START, STOP, PAUSE
    public static boolean	CHECKING_START = false;			// When checking but not collecting
    public static boolean	CHECK_NEW_VALUE_INT = false;
    public static boolean	CHECK_NEW_VALUE_OBJECT = false;
    public static int		newValueI;
    public static Object	newValueA;

    // These strings must be identical to the same strings in ConstantValue.java
    static final String		CATCH = "CATCH";
    static final String		RETURN = "RETURN";
    static final String		ENTER = "ENTER";
    static final String		CALL = "CALL";
    static final String		EXIT = "EXIT";
    static final String		CHGLOCALVAR = "CHGLOCALVAR";
    static final String		CHGINSTANCEVAR = "CHGINSTANCEVAR";
    static final String		CHGARRAY = "CHGARRAY";
    static final String		CHGTHREADSTATE = "CHGTHREADSTATE";
    static final String		LOCK = "LOCK";
    static final String		NEW = "NEW";
    static final String		RETURN_NEW = "RETURN_NEW";
    static final String		NOTDEFINED = "NOTDEFINED";

    static String		port, methodName;

    public static boolean skip(String port) {
	if (D.port == null) return false;
	return (port != D.port);
    }

    public static boolean skipChangeLocalVarI(int i, String methodName) {
	if (DISABLE) return true;				// No collection at all. skip
	if (port == null) return false;				// port not set. Look at everything
	if (port != CHGLOCALVAR) return true;			// wrong port. skip
	if (!CHECK_NEW_VALUE_INT) return false;			// not looking for int, Look at rest
	if (newValueI != i) return true;			// wrong value. skip
	if (D.methodName == null) return false;			// mn not set. Look at rest
	if (D.methodName != methodName) return true;		// wrong nm. skip
	return false;						// correct value. Look at rest
    }
    public static boolean skipChangeLocalVarA(Object value, String methodName) {
	if (DISABLE) return true;				// No collection at all. skip
	if (port == null) return false;				// port not set. Look at everything
	if (port != CHGLOCALVAR) return true;			// wrong port. skip
	if (!CHECK_NEW_VALUE_OBJECT) return false;		// not looking for object, Look at rest
	if (newValueA != value) return true;			// wrong value. skip
	if (D.methodName == null) return false;			// mn not set. Look at rest
	if (D.methodName != methodName) return true;		// wrong nm. skip
	return false;						// correct value. Look at rest
    }
    public static boolean skipChangeInstanceVarI(int i, String methodName) {
	if (DISABLE) return true;				// No collection at all. skip
	if (port == null) return false;				// port not set. Look at everything
	if (port != CHGINSTANCEVAR) return true;			// wrong port. skip
	if (!CHECK_NEW_VALUE_INT) return false;			// not looking for int, Look at rest
	if (newValueI != i) return true;			// wrong value. skip
	if (D.methodName == null) return false;			// mn not set. Look at rest
	if (D.methodName != methodName) return true;		// wrong nm. skip
	return false;						// correct value. Look at rest
    }
    public static boolean skipChangeInstanceVarA(Object value, String methodName) {
	if (DISABLE) return true;				// No collection at all. skip
	if (port == null) return false;				// port not set. Look at everything
	if (port != CHGINSTANCEVAR) return true;			// wrong port. skip
	if (!CHECK_NEW_VALUE_OBJECT) return false;		// not looking for object, Look at rest
	if (newValueA != value) return true;			// wrong value. skip
	if (D.methodName == null) return false;			// mn not set. Look at rest
	if (D.methodName != methodName) return true;		// wrong nm. skip
	return false;						// correct value. Look at rest
    }

    public static boolean skipChangeArrayI(int i, String methodName) {
	if (DISABLE) return true;				// No collection at all. skip
	if (port == null) return false;				// port not set. Look at everything
	if (port != CHGARRAY) return true;			// wrong port. skip
	if (!CHECK_NEW_VALUE_INT) return false;			// not looking for int, Look at rest
	if (newValueI != i) return true;			// wrong value. skip
	if (D.methodName == null) return false;			// mn not set. Look at rest
	if (D.methodName != methodName) return true;		// wrong nm. skip
	return false;						// correct value. Look at rest
    }
    public static boolean skipChangeArrayA(Object value, String methodName) {
	if (DISABLE) return true;				// No collection at all. skip
	if (port == null) return false;				// port not set. Look at everything
	if (port != CHGARRAY) return true;			// wrong port. skip
	if (!CHECK_NEW_VALUE_OBJECT) return false;		// not looking for object, Look at rest
	if (newValueA != value) return true;			// wrong value. skip
	if (D.methodName == null) return false;			// mn not set. Look at rest
	if (D.methodName != methodName) return true;		// wrong nm. skip
	return false;						// correct value. Look at rest
    }






    public static void enable() {DISABLE=false; CHECKING_START = false;}
    public static void disable() {DISABLE=true;Debugger.PAUSED=true;}
    public static void setCheckPattern(boolean checkPattern, boolean checkStart, String port, String methodName) {
	CHECK_PATTERN = checkPattern;
	CHECKING_START=checkStart;
	DISABLE = false;
	D.port = port;
	D.methodName = methodName;
    }
    public static void setCheckPattern(boolean checkPattern, boolean checkStart, String port, String methodName, boolean checkNVI, int nvi) {
	setCheckPattern(checkPattern, checkStart, port, methodName);
	CHECK_NEW_VALUE_INT = checkNVI;
	newValueI = nvi;
    }
    public static boolean getCheckPattern() {return CHECK_PATTERN;}
    public static boolean recursiveEntry() {return false;}
    /*
		if (recursiveEntry) {
	    Debugger.println("Recursive entry");
	    Exception e = new DebuggerException("Recursive entry");
	    e.printStackTrace();
	}
	return false;//recursiveEntry;
    }
    */

    static synchronized void  killTarget() {
	KILL_TARGET=true;
	PAUSE_PROGRAM=true;
	D.class.notifyAll();
	try {Thread.sleep(2000);}		// A pretty safe bet, tho not 100% accurate.
	catch (InterruptedException ie) {throw new DebuggerException("IMPOSSIBLE");}
	KILL_TARGET=false;
	PAUSE_PROGRAM=false;
    }

    public static synchronized void println(String s)  {
	if (!DEBUG_DEBUGGER) return;
	//	System.out.println("com.lambda.Debugger: " + s);
	new DebuggerException("com.lambda.Debugger: " + s).printStackTrace();
    }

    public static synchronized void resumeProgram() {
	PAUSE_PROGRAM=false;
	D.class.notifyAll();
    }


    public static void checkPAUSE() {
	if (!PAUSE_PROGRAM) return;
	Thread t = Thread.currentThread();
	if (t.getName().startsWith("AWT")) return;
	if (KILL_TARGET) throw new ThreadDeath();
	synchronized(D.class) {
	    try {while(PAUSE_PROGRAM && !KILL_TARGET) D.class.wait();}
	    catch (InterruptedException ie) {throw new DebuggerException("IMPOSSIBLE");}
	}
	if (KILL_TARGET) throw new ThreadDeath();
    }


    /*
      public static String trim(Object o) {
      String s;
      boolean bug = false;

      if (o == null) return "null";
      try {s = o.toString();}
      catch (Exception e) {
      bug = true;
      s = "<"+ o.getClass() +" BUG IN toString() >";
      }
      if (bug) D.println("d.trim(): Target program has toString() bug: " + s);
      return s;
      }
    */






    // ******************************** COLLECTION METHODS ********************************
    // ******************************** COLLECTION METHODS ********************************
    // ******************************** COLLECTION METHODS ********************************


    public static synchronized void stamp(int slIndex,  TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	recursiveEntry = true;
	TimeStamp.addStamp(slIndex, TimeStamp.LAST, tl);
	recursiveEntry = false;
    }



    // ******************************** CHANGE FUNCTIONS ********************************

    // Bind extra arguments (arg[n], n> 10) after getPrevious()
    public static synchronized void bind(int slIndex, int varIndex, Object value, TraceLine tl) {
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (PAUSE_PROGRAM) checkPAUSE();
	recursiveEntry=true;
	tl.localsBind(varIndex, value);
	recursiveEntry = false;	
    }
    
    
    public static synchronized void changeA(Object value, int slIndex, int varIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skipChangeLocalVarA(value, tl.getMethod()) || EventInterface.skipLocalVarA(slIndex, varIndex, value, tl))) return;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, value, tl);
	recursiveEntry = false;
	return;
    }


    /*
    public static synchronized int changeI(int value, int slIndex, int varIndex, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skipChangeLocalVarI(value, tl.getMethod()) || EventInterface.skipLocalVarI(slIndex, varIndex, value, tl))) return value;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowInt.createShadowInt(value), tl);
	recursiveEntry = false;	
	return value;
    }
    */


    // BUGGY !!
    public static int changeI(int value, int slIndex, int varIndex, TraceLine tl) {
	if (Debugger.BUG) {
	    if (PAUSE_PROGRAM) checkPAUSE();
	    if (DISABLE || (tl == null) || recursiveEntry()) return value;
	    if (CHECK_PATTERN && (skipChangeLocalVarI(value, tl.getMethod()) || EventInterface.skipLocalVarI(slIndex, varIndex, value, tl))) return value;
	    recursiveEntry=true;
	    Locals locals = tl.locals;
	    locals.add(slIndex, varIndex, ShadowInt.createShadowInt(value), tl);
	    recursiveEntry = false;	
	    return value;
	}
	else {
	    synchronized (D.class) {
		if (PAUSE_PROGRAM) checkPAUSE();
		if (DISABLE || (tl == null) || recursiveEntry()) return value;
		if (CHECK_PATTERN && (skipChangeLocalVarI(value, tl.getMethod()) || EventInterface.skipLocalVarI(slIndex, varIndex, value, tl))) return value;
		recursiveEntry=true;
		Locals locals = tl.locals;
		locals.add(slIndex, varIndex, ShadowInt.createShadowInt(value), tl);
		recursiveEntry = false;	
		return value;
	    }
	}
    }

    public static synchronized void changeIvoid(int value, int slIndex, int varIndex, TraceLine tl) {		//for IINC
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skipChangeLocalVarI(value, tl.getMethod()) || EventInterface.skipLocalVarI(slIndex, varIndex, value, tl))) return;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowInt.createShadowInt(value), tl);
	recursiveEntry = false;	
    }
    public static synchronized long changeL(long value, int slIndex, int varIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGLOCALVAR) || EventInterface.skipLocalVarL(slIndex, varIndex, value, tl))) return value;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowLong.createShadowLong(value), tl);
	recursiveEntry = false;	
	return value;
    }
    public static synchronized byte changeB(byte value, int slIndex, int varIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGLOCALVAR) || EventInterface.skipLocalVarB(slIndex, varIndex, value, tl))) return value;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowByte.createShadowByte(value), tl);
	recursiveEntry = false;	
	return value;
    }
    public static synchronized boolean changeZ(boolean value, int slIndex, int varIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGLOCALVAR) || EventInterface.skipLocalVarZ(slIndex, varIndex, value, tl))) return value;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowBoolean.createShadowBoolean(value), tl);
	recursiveEntry = false;	
	return value;
    }
    public static synchronized char changeC(char value, int slIndex, int varIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGLOCALVAR) || EventInterface.skipLocalVarC(slIndex, varIndex, value, tl))) return value;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowChar.createShadowChar(value), tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized short changeS(short value, int slIndex, int varIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGLOCALVAR) || EventInterface.skipLocalVarS(slIndex, varIndex, value, tl))) return value;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowShort.createShadowShort(value), tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized float changeF(float value, int slIndex, int varIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGLOCALVAR) || EventInterface.skipLocalVarF(slIndex, varIndex, value, tl))) return value;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowFloat.createShadowFloat(value), tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized double changeD(double value, int slIndex, int varIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGLOCALVAR) || EventInterface.skipLocalVarD(slIndex, varIndex, value, tl))) return value;
	recursiveEntry=true;
	Locals locals = tl.locals;
	locals.add(slIndex, varIndex, ShadowDouble.createShadowDouble(value), tl);
	recursiveEntry = false;	
	return value;
    }

    // ******************************** CHANGE IVARS ********************************

    // Change instance var, type Object
    public static synchronized void changeIVA(Object o, Object value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarA(slIndex, o, varName, value, tl))) return;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	shadow.add(slIndex, varName, value, tl);
	recursiveEntry = false;
    }
    public static synchronized boolean changeIVZ(Object o, boolean value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarZ(slIndex, o, varName, value, tl))) return value;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	Object oValue = ShadowBoolean.createShadowBoolean(value);
	shadow.add(slIndex, varName, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized byte changeIVB(Object o, byte value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarB(slIndex, o, varName, value, tl))) return value;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	Object oValue = ShadowByte.createShadowByte(value);
	shadow.add(slIndex, varName, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized char changeIVC(Object o, char value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarC(slIndex, o, varName, value, tl))) return value;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	Object oValue = ShadowChar.createShadowChar(value);
	shadow.add(slIndex, varName, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized short changeIVS(Object o, short value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarS(slIndex, o, varName, value, tl))) return value;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	Object oValue = ShadowShort.createShadowShort(value);
	shadow.add(slIndex, varName, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized int changeIVI(Object o, int value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarI(slIndex, o, varName, value, tl))) return value;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	Object oValue = ShadowInt.createShadowInt(value);
	shadow.add(slIndex, varName, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized long changeIVL(Object o, long value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarL(slIndex, o, varName, value, tl))) return value;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	Object oValue = ShadowLong.createShadowLong(value);
	shadow.add(slIndex, varName, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized float changeIVF(Object o, float value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarF(slIndex, o, varName, value, tl))) return value;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	Object oValue = ShadowFloat.createShadowFloat(value);
	shadow.add(slIndex, varName, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized double changeIVD(Object o, double value, int slIndex, String varName, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return value;	
	if (CHECK_PATTERN && (skip(CHGINSTANCEVAR) || EventInterface.skipIVarD(slIndex, o, varName, value, tl))) return value;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(o);
	Object oValue = ShadowDouble.createShadowDouble(value);
	shadow.add(slIndex, varName, oValue, tl);
	recursiveEntry = false;
	return value;
    }

    // ******************************** ARRAY ********************************
    
    // Change array element, type Object
    public static synchronized void changeArrayA(Object array, int index, Object value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skipChangeArrayA(value, tl.getMethod()) || EventInterface.skipArrayA(slIndex, array, index, value, tl))) return;	
	recursiveEntry=true;
	Shadow shadow = Shadow.get(array);
	shadow.add(slIndex, index, value, tl);
	recursiveEntry = false;
    }
    public static synchronized int changeArrayI(Object array, int index, int value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skipChangeArrayI(value, tl.getMethod()) || EventInterface.skipArrayI(slIndex, array, index, value, tl))) return value;
	recursiveEntry=true;
	Object oValue = ShadowInt.createShadowInt(value);
	Shadow shadow = Shadow.get(array);
	shadow.add(slIndex, index, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized byte changeArrayB(Object array, int index, byte value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skip(CHGARRAY) || EventInterface.skipArrayB(slIndex, array, index, value, tl))) return value;	
	recursiveEntry=true;
	Shadow shadow = Shadow.get(array);
	Object oValue = ShadowByte.createShadowByte(value);
	shadow.add(slIndex, index, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized boolean changeArrayZ(Object array, int index, boolean value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skip(CHGARRAY) || EventInterface.skipArrayZ(slIndex, array, index, value, tl))) return value;	
	recursiveEntry=true;
	Shadow shadow = Shadow.get(array);
	Object oValue = ShadowBoolean.createShadowBoolean(value);
	shadow.add(slIndex, index, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized char changeArrayC(Object array, int index, char value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skip(CHGARRAY) || EventInterface.skipArrayC(slIndex, array, index, value, tl))) return value;	
	recursiveEntry=true;
	Shadow shadow = Shadow.get(array);
	Object oValue = ShadowChar.createShadowChar(value);
	shadow.add(slIndex, index, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized short changeArrayS(Object array, int index, short value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skip(CHGARRAY) || EventInterface.skipArrayS(slIndex, array, index, value, tl))) return value;	
	recursiveEntry=true;
	Shadow shadow = Shadow.get(array);
	Object oValue = ShadowShort.createShadowShort(value);
	shadow.add(slIndex, index, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized long changeArrayL(Object array, int index, long value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skip(CHGARRAY) || EventInterface.skipArrayL(slIndex, array, index, value, tl))) return value;	
	recursiveEntry=true;
	Shadow shadow = Shadow.get(array);
	Object oValue = ShadowLong.createShadowLong(value);
	shadow.add(slIndex, index, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized float changeArrayF(Object array, int index, float value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skip(CHGARRAY) || EventInterface.skipArrayF(slIndex, array, index, value, tl))) return value;	
	recursiveEntry=true;
	Shadow shadow = Shadow.get(array);
	Object oValue = ShadowFloat.createShadowFloat(value);
	shadow.add(slIndex, index, oValue, tl);
	recursiveEntry = false;
	return value;
    }
    public static synchronized double changeArrayD(Object array, int index, double value, int slIndex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || (tl == null) || recursiveEntry()) return value;
	if (CHECK_PATTERN && (skip(CHGARRAY) || EventInterface.skipArrayD(slIndex, array, index, value, tl))) return value;	
	recursiveEntry=true;
	Shadow shadow = Shadow.get(array);
	Object oValue = ShadowDouble.createShadowDouble(value);
	shadow.add(slIndex, index, oValue, tl);
	recursiveEntry = false;
	return value;
    }

    // ******************************** VECTOR ********************************

    // Change vector element(s)
    public static synchronized void vectorChange(int slIndex, Vector vector, int index, Object value) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE  || recursiveEntry()) return;
	if (CHECK_PATTERN && CHECKING_START) return;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(vector, true);
	shadow.vectorChange(slIndex, index, value);
	recursiveEntry = false;
    }
    public static synchronized void vectorInsert(int slIndex, Vector vector, int index, Object value) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return;
	if (CHECK_PATTERN && CHECKING_START) return;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(vector, true);
	shadow.vectorInsert(slIndex, index, value);
	recursiveEntry = false;
    }
    public static synchronized void vectorRemove(int slIndex, Vector vector, int index, int range) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return;
	if (CHECK_PATTERN && CHECKING_START) return;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(vector, true);
	shadow.vectorRemove(slIndex, index, range);
	recursiveEntry = false;
    }

    // ******************************** ARRAYLIST ********************************

    // Change ArrayList element
    public static synchronized void arraylistChange(int slIndex, ArrayList arraylist, int index, Object value) {
        if (PAUSE_PROGRAM) checkPAUSE();//        checkPAUSE(fileLine);
	if (DISABLE || recursiveEntry()) return;
	if (CHECK_PATTERN && CHECKING_START) return;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(arraylist, true);
	shadow.vectorChange(slIndex, index, value);
	recursiveEntry = false;
    }
    public static synchronized void arraylistInsert(int slIndex, ArrayList arraylist, int index, Object value) {
        if (PAUSE_PROGRAM) checkPAUSE();//        checkPAUSE(fileLine);
	if (DISABLE || recursiveEntry()) return;
	if (CHECK_PATTERN && CHECKING_START) return;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(arraylist, true);
	shadow.vectorInsert(slIndex, index, value);
	recursiveEntry = false;
    }
    public static synchronized void arraylistRemove(int slIndex, ArrayList arraylist, int index, int range) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return;
	if (CHECK_PATTERN && CHECKING_START) return;
	recursiveEntry=true;
	Shadow shadow = Shadow.get(arraylist, true);
	shadow.vectorRemove(slIndex, index, range);
	recursiveEntry = false;
    }

    // ******************************** HASHTABLE ********************************

    public static synchronized void hashtablePut(int slIndex, MyHashtable table, Object key, Object value) {
        if (PAUSE_PROGRAM) checkPAUSE();
        if (DISABLE || recursiveEntry()) return;
        if (CHECK_PATTERN && CHECKING_START) return;
        recursiveEntry=true;
        Shadow shadow = Shadow.get(table);
        shadow.hashtablePut(slIndex, key, value);
        recursiveEntry = false;
    }
    public static synchronized void hashtableRemove(int slIndex, MyHashtable table, Object key) {
        if (PAUSE_PROGRAM) checkPAUSE();
        if (DISABLE || recursiveEntry()) return;
        if (CHECK_PATTERN && CHECKING_START) return;
        recursiveEntry=true;
        Shadow shadow = Shadow.get(table);
        shadow.hashtableRemove(slIndex, key);
        recursiveEntry = false;
    }
    public static synchronized void hashtableClear(int slIndex, MyHashtable table) {
        if (PAUSE_PROGRAM) checkPAUSE();
        if (DISABLE || recursiveEntry()) return;
        if (CHECK_PATTERN && CHECKING_START) return;
        recursiveEntry=true;
        Shadow shadow = Shadow.get(table);
        shadow.hashtableClear(slIndex);
        recursiveEntry = false;
    }

    // ******************************** HASHMAP ********************************

    public static synchronized void hashMapPut(int slIndex, MyHashMap table, Object key, Object value) {
        if (PAUSE_PROGRAM) checkPAUSE();
        if (DISABLE || recursiveEntry()) return;
        if (CHECK_PATTERN && CHECKING_START) return;
        recursiveEntry=true;
        Shadow shadow = Shadow.get(table);
        shadow.hashtablePut(slIndex, key, value);
        recursiveEntry = false;
    }
    public static synchronized void hashMapRemove(int slIndex, MyHashMap table, Object key) {
        if (PAUSE_PROGRAM) checkPAUSE();
        if (DISABLE || recursiveEntry()) return;
        if (CHECK_PATTERN && CHECKING_START) return;
        recursiveEntry=true;
        Shadow shadow = Shadow.get(table);
        shadow.hashtableRemove(slIndex, key);
        recursiveEntry = false;
    }
    public static synchronized void hashMapClear(int slIndex, MyHashMap table) {
        if (PAUSE_PROGRAM) checkPAUSE();
        if (DISABLE || recursiveEntry()) return;
        if (CHECK_PATTERN && CHECKING_START) return;
        recursiveEntry=true;
        Shadow shadow = Shadow.get(table);
        shadow.hashtableClear(slIndex);
        recursiveEntry = false;
    }



    // ******************************** EXIT ********************************

    public static TraceLine exit(int slIndex, Object o, String meth, TraceLine tl, Object arg0) {
	synchronized(D.class) {
	    KILL_TARGET = true;					// Always kill on exit()
	    if (PAUSE_PROGRAM) checkPAUSE();			// If already exited(?!)
	    DISABLE = true;					// Stop collecting
	    recursiveEntry=true;
	    if (o == null) {throw new DebuggerException("No THIS in D.exit "+meth);}//IMPOSSIBLE
	    TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0);
	    recursiveEntry = false;
	}
	try {Thread.sleep(2000);}		// A pretty safe bet, tho not 100% accurate.
	catch (InterruptedException ie) {throw new DebuggerException("IMPOSSIBLE");}
	Debugger.stopTarget();
	throw new DebuggerExit("Target program called System.exit()");
    }

    // ******************************** INVOKE FUNCTIONS 0 - 10 ARGUMENTS ********************************
    
    public static synchronized TraceLine invoke(int slIndex, Object o, String meth, TraceLine tl) {	
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 0, null, null, null, null, null, null, null, null, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }

    public static synchronized TraceLine invoke(int slIndex, Object o, String meth, TraceLine tl, Object arg0) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 1, arg0, null, null, null, null, null, null, null, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }

    public static synchronized TraceLine invoke(int slIndex, Object o,		
						String meth, TraceLine tl, Object arg0, Object arg1) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 2, arg0, arg1, null, null, null, null, null, null, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }

    public static synchronized TraceLine invoke(int slIndex, Object o,		
						String meth, TraceLine tl, Object arg0, Object arg1, Object arg2) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1, arg2);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 3, arg0, arg1, arg2, null, null, null, null, null, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine invoke(int slIndex, Object o,		
						String meth, TraceLine tl, Object arg0, Object arg1, Object arg2,
						Object arg3) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1, arg2, arg3);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 4, arg0, arg1, arg2, arg3, null, null, null, null, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }


    public static synchronized TraceLine invoke(int slIndex, Object o,		
						String meth, TraceLine tl, Object arg0, Object arg1, Object arg2, 
						Object arg3, Object arg4) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1, arg2, arg3, arg4);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 5, arg0, arg1, arg2, arg3, arg4, null, null, null, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }

    public static synchronized TraceLine invoke(int slIndex, Object o, String meth, TraceLine tl,
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1, arg2, arg3, arg4, arg5);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 6, arg0, arg1, arg2, arg3, arg4, arg5, null, null, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine invoke(int slIndex, Object o, String meth, TraceLine tl,
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 7, arg0, arg1, arg2, arg3, arg4, arg5, arg6, null, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine invoke(int slIndex, Object o, String meth, TraceLine tl,
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 8, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, null, null))) {}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine invoke(int slIndex, Object o, String meth, TraceLine tl,
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7, Object arg8) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 9, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, null))) {}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine invoke(int slIndex, Object o, String meth, TraceLine tl,
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	if (o == null) o = ShadowNull.NULL;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, meth, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	if (CHECK_PATTERN && (skip(CALL) || EventInterface.skipCall(slIndex, o, meth, tl1, 10, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9))) {}
	recursiveEntry = false;
	return tl1;
    }

    // ******************************** NEWOBJ FUNCTIONS 0 - 5 ARGUMENTS ********************************

    public static synchronized void newArray(Object o, int slIndex) {	//  NOTE REVERSED ORDER! (so I can use DUP in byte code)
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(NEW) || CHECKING_START)) return;
	recursiveEntry=true;
	TimeStamp.addStamp(slIndex, TimeStamp.MULTI_D_ARRAY);
	if (o == null)
	    throw new DebuggerException("No THIS in D.newArray()");
	else
	    Shadow.createShadow(o, true);		// Useless??
	recursiveEntry = false;
    }

    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl) {	
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 0, null, null, null, null, null, null, null, null, null, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }

    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex,o,"new",tl,2,arg0,null,null,null,null,null,null,null,null,null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }

    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 2, arg0, arg1, null, null, null, null, null, null, null, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }

    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1, Object arg2) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1, arg2);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 3, arg0, arg1, arg2, null, null, null, null, null, null, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1, Object arg2,
						Object arg3) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1, arg2, arg3);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 4, arg0, arg1, arg2, arg3, null, null, null, null, null, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }


    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1, Object arg2, 
						Object arg3, Object arg4) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1, arg2, arg3, arg4);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 5, arg0, arg1, arg2, arg3, arg4, null, null, null, null, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1, arg2, arg3, arg4, arg5);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 6, arg0, arg1, arg2, arg3, arg4, arg5, null, null, null, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 7, arg0, arg1, arg2, arg3, arg4, arg5, arg6, null, null, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 8, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, null, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7, Object arg8) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 9, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, null))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }
    public static synchronized TraceLine newObj(int slIndex, Object o, TraceLine tl,		
						Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
						Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry() || (tl == null)) return null;
	recursiveEntry=true;
	TraceLine tl1 = TraceLine.addTrace(slIndex, o, "new", tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	if (CHECK_PATTERN && (skip(NEW) || EventInterface.skipNew(slIndex, o, "new", tl, 10, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9))) {
	    recursiveEntry = false;
	    return tl1;
	}
	recursiveEntry = false;
	return tl1;
    }





    // ******************************** RETURN ********************************


    public static synchronized void returnValue(Object rv, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE ||  (tl == null) || recursiveEntry()) return;
	if (CHECKING_START) {tl.popInclusive();}
	if (CHECK_PATTERN && (skip(RETURN) || EventInterface.skipReturn(rv, tl))) return;
	recursiveEntry=true;
	ReturnLine.addReturnLine(rv, tl);
	recursiveEntry = false;
    }

    public static boolean returnValueZ(boolean rv, TraceLine tl) {
	Object rvo = ShadowBoolean.createShadowBoolean(rv);
	returnValue(rvo, tl);
	return rv;
    }
    public static byte returnValueB(byte rv, TraceLine tl) {
	Object rvo = ShadowByte.createShadowByte(rv);
	returnValue(rvo, tl);
	return rv;
    }
    public static char returnValueC(char rv, TraceLine tl) {
	Object rvo = ShadowChar.createShadowChar(rv);
	returnValue(rvo, tl);
	return rv;
    }
    public static short returnValueS(short rv, TraceLine tl) {
	Object rvo = ShadowShort.createShadowShort(rv);
	returnValue(rvo, tl);
	return rv;
    }
    public static synchronized int returnValueI(int rv, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE ||  (tl == null) || recursiveEntry()) return rv;
	if (CHECKING_START) {tl.popInclusive();}
	if (CHECK_PATTERN && (skip(RETURN))) return rv;
	recursiveEntry=true;
	Object rvo = ShadowInt.createShadowInt(rv);
	if (CHECK_PATTERN && EventInterface.skipReturn(rvo, tl)) return rv;
	ReturnLine.addReturnLine(rvo, tl);
	recursiveEntry = false;
	return rv;
    }
    public static long returnValueL(long rv, TraceLine tl) {
	Object rvo = ShadowLong.createShadowLong(rv);
	returnValue(rvo, tl);
	return rv;
    }
    public static float returnValueF(float rv, TraceLine tl) {
	Object rvo = ShadowFloat.createShadowFloat(rv);
	returnValue(rvo, tl);
	return rv;
    }
    public static double returnValueD(double rv, TraceLine tl) {
	Object rvo = ShadowDouble.createShadowDouble(rv);
	returnValue(rvo, tl);
	return rv;
    }
    public static void returnValue(TraceLine tl) {
	returnValue(ShadowVoid.VOID, tl);
    }

    // ******************************** RETURN MARKER ********************************



    public static synchronized void returnMarker(Object rv, int slIndex,  TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECKING_START && tl.isUnparented()) {tl.popInclusive();}
	if (CHECK_PATTERN && (skip(EXIT) || EventInterface.skipExitA(slIndex, rv, tl))) return;
	recursiveEntry=true;
	TimeStamp.addStamp(slIndex, TimeStamp.LAST, tl);
	recursiveEntry = false;
	if (tl.isUnparented() || (CHECKING_START)) returnValue(rv, tl);
    }

    public static void returnMarker(int slIndex,  TraceLine tl) {
	returnMarker(ShadowVoid.VOID, slIndex, tl);
    }


    // ******************************** RETURN FROM NEW ********************************

    public static synchronized void returnNew(Object rv, TraceLine tl) {
        if (PAUSE_PROGRAM)  checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECKING_START) {tl.popInclusive();}
	if (CHECK_PATTERN && (skip(RETURN_NEW) || EventInterface.skipReturnNew(rv, tl))) return;
	recursiveEntry=true;
	ReturnLine.addReturnLine(rv, tl);
	Shadow.createShadow(rv, true);
	recursiveEntry = false;
    }


    // ******************************** CREATE THE SHADOW OBJECTS FOR PRIMITIVES ********************************


    public static synchronized Object createShadowClass(Class clazz) {
	return clazz;
    }

    public static synchronized Object createShadowClass(String className) {
	Class clazz = (Class) classTable.get(className);
	if (clazz != null) return clazz;
	try {
	    if (Debugger.classLoader == null)
		clazz = Class.forName(className);
	    else
		clazz = Class.forName(className, true, Debugger.classLoader);// this can't work
	    classTable.put(className, clazz);
	    return clazz;
	}
	catch (ClassNotFoundException e) {
	    Debugger.println("createShadowClass can't find class. IMPOSSIBLE " + className);
	    System.exit(1);
	}
	return null;			// Never gets here
    }
    public static  ShadowInt createShadowInt(int i) {
      	return ShadowInt.createShadowInt(i);
    }
    public static  ShadowShort createShadowShort(short i) {
	return ShadowShort.createShadowShort(i);
    }
    public static  ShadowByte createShadowByte(byte i) {
	return ShadowByte.createShadowByte(i);
    }
    public static  ShadowChar createShadowChar(char i) {
	return ShadowChar.createShadowChar(i);
    }
    public static  ShadowBoolean createShadowBoolean(boolean i) {
	return ShadowBoolean.findShadowBoolean(i);
    }
    public static  ShadowFloat createShadowFloat(float i) {
	return new ShadowFloat(i);
    }
    public static  ShadowLong createShadowLong(long i) {
	return ShadowLong.createShadowLong(i);
    }
    public static  ShadowDouble createShadowDouble(double i) {
	return new ShadowDouble(i);
    }




    // ******************************** THROW CATCH ********************************

    

    public static synchronized void throwEx(int slIndex, Object ex, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && CHECKING_START) return;
	recursiveEntry=true;
	ThrowLine.addThrowLine(slIndex, (Throwable) ex, tl);
	recursiveEntry = false;
    }

    public static synchronized void catchEx(int slIndex, Object ex, TraceLine tl) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	recursiveEntry=true;
	if (CHECKING_START) {
	    tl.popExclusive();
	    if (skip(CATCH) || EventInterface.skipCatch(slIndex, (Throwable) ex, tl)) {}
	}
	else
	    CatchLine.addCatchLine(slIndex, (Throwable) ex, tl);
	recursiveEntry = false;
    }



    // ******************************** TRACK LOCKS ********************************


    public static synchronized void gettingLock(int slIndex, Object obj, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(LOCK) || EventInterface.skipGettingLock(slIndex, obj, tl))) return;
	recursiveEntry=true;
	Locks.gettingLock(slIndex, obj, tl);
	recursiveEntry = false;
    }



    public static synchronized void gotLock(int slIndex, Object obj, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(LOCK) || EventInterface.skipGotLock(slIndex, obj, tl))) return;
	recursiveEntry=true;
	Locks.gotLock(slIndex, obj, tl);
	recursiveEntry = false;
    }



    public static synchronized void releasingLock(int slIndex, Object obj, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(LOCK) || EventInterface.skipReleasingLock(slIndex, obj, tl))) return;
	recursiveEntry=true;
	Locks.releasingLock(slIndex, obj, tl);
	recursiveEntry = false;
    }



    public static synchronized void startingWait(int slIndex, Object obj, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(LOCK) || EventInterface.skipStartingWait(slIndex, obj, tl))) return;
	recursiveEntry=true;
	Locks.startingWait(slIndex, obj, tl);
	recursiveEntry = false;
    }



    public static synchronized void endingWait(int slIndex, Object obj, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(LOCK) || EventInterface.skipEndingWait(slIndex, obj, tl))) return;
	recursiveEntry=true;
	Locks.endingWait(slIndex, obj, tl);
	recursiveEntry = false;
    }



    public static synchronized void startingJoin(int slIndex, Object obj, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(LOCK) || EventInterface.skipStartingJoin(slIndex, obj, tl))) return;
	recursiveEntry=true;
	if (obj instanceof Thread) Locks.startingJoin(slIndex, (Thread)obj, tl);
	recursiveEntry = false;
    }



    public static synchronized void endingJoin(int slIndex, Object obj, TraceLine tl) {
	if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || (tl == null) || recursiveEntry()) return;
	if (CHECK_PATTERN && (skip(LOCK) || EventInterface.skipEndingJoin(slIndex, obj, tl))) return;
	recursiveEntry=true;
	if (obj instanceof Thread) Locks.endingJoin(slIndex, (Thread)obj, tl);
	recursiveEntry = false;
    }






    // ******************************** RETURN PREVOUS TL  ********************************


    /*
      public static synchronized TraceLine blankTrace() {
      if (DISABLE || recursiveEntry()) return(null);
      recursiveEntry=true;

      Thread thread = Thread.currentThread();
      TraceLine tl = (TraceLine)blankTraces.get(thread);
      if (tl == null) {
      tl = TraceLine.previousTraceLine();
      blankTraces.put(thread, tl);
      }
      recursiveEntry=false;
      return tl;
      }
    */

    public static synchronized TraceLine addUnparented10(int slIndex, Object o, String meth, String methodID, int nLocals,
							 Object arg0, Object arg1,Object arg2, Object arg3, Object arg4,
							 Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace(slIndex, o, meth, null, nLocals, 10,
							  arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented9(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0, Object arg1,Object arg2, Object arg3, Object arg4,
							Object arg5, Object arg6, Object arg7, Object arg8) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace(slIndex, o, meth, null, nLocals, 9,
							  arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, null);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented8(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0, Object arg1,Object arg2, Object arg3, Object arg4,
							Object arg5, Object arg6, Object arg7) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace(slIndex, o, meth, null, nLocals, 8,
							  arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, null, null);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented7(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
							Object arg5, Object arg6) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace(slIndex, o, meth, null, nLocals, 7,
							  arg0, arg1, arg2, arg3, arg4, arg5, arg6, null, null, null);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented6(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0, Object arg1,Object arg2, Object arg3, Object arg4,
							Object arg5) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace(slIndex, o, meth, null, nLocals, 6,
							  arg0, arg1, arg2, arg3, arg4, arg5, null, null, null, null);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented5(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0, Object arg1,Object arg2, Object arg3, Object arg4) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace(slIndex, o, meth, null, nLocals, 5,
							  arg0, arg1, arg2, arg3, arg4, null, null, null, null, null);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented4(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0, Object arg1,Object arg2, Object arg3) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace4(slIndex, o, meth, null, nLocals, 4, arg0, arg1, arg2, arg3);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }
    public static synchronized TraceLine addUnparented3(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0, Object arg1 ,Object arg2) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace4(slIndex, o, meth, null, nLocals, 3, arg0, arg1, arg2, null);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented2(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0, Object arg1) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);

	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace4(slIndex, o, meth, null, nLocals, 2, arg0, arg1, null, null);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented1(int slIndex, Object o, String meth, String methodID, int nLocals,
							Object arg0) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace4(slIndex, o, meth, null, nLocals, 1, arg0, null, null, null);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }

    public static synchronized TraceLine addUnparented0(int slIndex, Object o, String meth, String methodID, int nLocals) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.addUnparentedTrace0(slIndex, o, meth, null, nLocals);
	tl.addLocals(slIndex, methodID, nLocals);
	recursiveEntry = false;
	if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) return tl;
	return(tl);
    }


    public static synchronized TraceLine getPreviousTL(int slIndex, String meth, String methodID, int nLocals) {
        if (PAUSE_PROGRAM) checkPAUSE();
	if (DISABLE || recursiveEntry()) return(null);
	recursiveEntry=true;
	TraceLine tl = TraceLine.getTraceLineD(methodID, meth);
	if (tl != null) {
	    tl.addLocals(slIndex, methodID, nLocals);
	    if (CHECK_PATTERN && (skip(ENTER) || EventInterface.skipEnter(slIndex, tl))) {}
	}
	recursiveEntry = false;
	return(tl);
    }



    public static void codeInsertionError(String s, TimeStamp ts) {
	TimeStamp.printAll();
	throw new DebuggerException("codeInsertionError: " + s +" "+ts);
    }

    public static void appendVarNames(String methodID, String[] sa) {
	Locals.appendVarNames(methodID, sa);
    }

    public static void appendVarTypes(String methodID, String[] sa, String returnType) {
	Locals.appendVarTypes(methodID, sa, returnType, null);
    }

    public static void appendVarTypes(String methodID, String[] sa, String returnType, ClassLoader cl) {
	Locals.appendVarTypes(methodID, sa, returnType, cl);
    }

    public static synchronized int addSourceLines(String[] sourceLines) {
	int off =  SourceLine.addSourceLines(sourceLines);
	return off;
    }


    public static void verifyVersion(String version, String className) {
        if (! version.equals(Debugify.version))
            throw new DebuggerException("Debugification version mismatch in: " + className + ". Expected: " + Debugify.version +
                                        " got: "+version + "\nPlease re-instrument");
    }

    public static void verifyVersion(String version) {// GET RID OF THIS LATER.
        String className = "UNKNOWN--Older version of ODB";
        if (! version.equals(Debugify.version))
            throw new DebuggerException("Debugification version mismatch in: " + className + ". Expected: " + Debugify.version +
                                        " got: "+version + "\nPlease re-instrument");
    }

    public static String[] createStringArray(int size) {
	return new String[size];
    }


    public static void main(String[] args) {
	int repeat = 1000;
	if (args.length > 0) repeat = Integer.parseInt(args[0]);
	createShadowClass("java.util.ArrayList");		// 500ns
	createShadowInt(1);					//  90ns
	createShadowInt(10001);					// 250ns
	ShadowInt.createShadowInt(1);				//  60ns

	long start = System.currentTimeMillis();

	for (int i = 0; i < repeat; i++) {			// 
	    for (int j = 0; j < 1000; j++) {
		ShadowInt.createShadowInt(1);
	    }
	}

	long end = System.currentTimeMillis();
	long total = (end-start);
	long avePerCall =  (total*1000000)/(repeat*1000);	// ns/loop

	System.out.println("Total: " + total + "ms Average: "+avePerCall +"ns");
    }

}
