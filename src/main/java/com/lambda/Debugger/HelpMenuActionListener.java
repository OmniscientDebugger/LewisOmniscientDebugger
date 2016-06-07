/*                        HelpMenuActionListener.java

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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;


class HelpMenuActionListener implements ActionListener {
    JMenuItem b0, b1, b2, b3, b4, b5, b6, b7, b8, b9;

    private static String ghelpMessage = ""+
	"If you are calling the ODB from the command line, there are three primary options\n"+
	"which are defined as aliases (UNIX) or .bat files (WINDOWS):\n"+
	"\n"+
	"debug  Program args     Run the program and bring up the debugger when main() exits.\n"+
	"debugp Program args     Bring up the command window and allow the programmer to select options.\n"+
	"debugn Program args     Run the program but don't bring up the debugger until STOP RECORDING is pressed.\n"+
	"                        (for multithreaded programs where main() exits early)\n"+
	"\n"+
	"The arrow buttons all the execute First/Previous/Next/Last on the selected element.\n"+
	"They all have tool-tip popups.\n"+
	"\n"+
	"o  Selecting a line in the Trace pane reverts to the first line in that method.\n"+
	"o  Selecting a line in the Threads pane reverts to the nearest previous event in that thread.\n"+
	"o  Selecting a line in the Code pane reverts to *AN* event on that line (if any). It attempts\n"+
	"   to revert the most 'reasonable' event, but this is not always clear.\n"+
	"\n"+
	"o  Double-clicking on an object anywhere will copy that object into the Objects pane and open it.\n"+
	"o  Double-clicking on an object in the Objects pane will show/not-show the instance variables it. \n"+
	"o  Double-clicking on an instance variable in the Objects pane will show its IVs recursively.\n"+
	"\n"+
	"The minibuffer at the bottom of the debugger window will display details about the most recent command.\n"+
	"This is also where extended commands such as Incremental String Search and Evaluate Expression are run.\n"+
	"For details, see the manual in jar file.\n"+
	"\n"+
	"-Bil Lewis  Bil.Lewis@LambdaCS.com\n"+
	"\n"+
	"                                      Type <Ctrl-G> to close this help message.\n"+
	"";

    private static String fhelpMessage = ""+
	"An fget query is prolog-style, where this:\n" +
	"\n" +
	"port = call & callObject = <Thing_3> & arg0 > 33 & callMethodName = CMN\n" +
	"\n" +
	"means 'find a call to a method, where the object is <Thing_3> and the first\n" +
	"argument is an integer greater than 33. Then put the method name into the\n" +
	"variable CMN and display it.' 	\n" +
	"\n" +
	"There are two options on the Trace menu which will create interactive queries.\n" +
	"One will create an FGET query which matches the currently selected trace line.\n" +
	"Selecting that menu item will store the query as the previous FGET query, such that \n" +
	"the next time you type ^F^F, it will be the query that appears.\n" +
	"\n" +
	"The other option will create a query which matches the current line of code. For example:\n" +
	"\n" +
	"\n" +
	"\n" +
	"Possible lhs attributes: (port p) (sourceLine sl) (sourceFile sf) (thread thr) (threadClass thrc) (thisObject\n" +
	"to) (thisObjectClass toc) (methodName mn) (isMethodStatic ims) (parameters params) (callMethodName cmn)\n" +
	"(isCallMethodStatic icms) (callObject co) (callObjectClass coc) (callArguments args) (returnType rt)\n" +
	"(returnValue rv) (name varName) (type varType) (newValue nv) (oldValue ov) (object o) (lockType lt)\n" +
	"(objectClass oc) (isIvarStatic iivs) (blockedOnObject boo) (blockedOnObjectClass booc) (exception ex)\n" +
	"(exceptionClass exc) (throwingMethodName thn) (callArgumentValue0 a0 arg0 - arg9) (parameterValue0 p0 - p9)\n" +
	"(objects os) (var0 v0) (var1 v1) (vars vs) (index i) (array a) (arrayClass ac) (stackFrames sf) (printString\n" +
	"ps)\n" +
	"\n" +
	"\n" +
	"Possible rhs constants: catch return enter (call c) (exit x) lock (chgLocalVar clv) (chgInstanceVar civ)\n" +
	"chgArray (chgThreadState cts) notdefined null false true boolean byte char short int long float double String\n" +
	"gettingLock gotLock releasingLock startingWait endingWait startingJoin endingJoin\n" +
	"\n"+
	"\n"+
	"-Bil Lewis  Bil.Lewis@LambdaCS.com\n"+
	"\n"+
	"                                      Type <Ctrl-G> to close this help message.\n"+
	"";

	private static String dhelpMessage = ""+
	"MEMORY			Used to determine number of Timestamps.\n" +
	"GC_OFF			If too many Timestamps, turn off recording.\n" +
	"DONT_SHOW		Don't stop recording when main() exits.\n" +
	"DONT_INSTRUMENT		Don't instrument class. (Assume files instrumented.)\n" +
	"PAUSED			Don't start recording.\n" +
	"DONT_START		Just bring up the controller.\n" +
	"DEBUGIFY_ONLY		Just debugify. (Not used normally.)\n" +
	"BUG			Use the buggy version of revert() for demos.\n" +
	"VGA			Format window for VGA screen (drops some menus).\n" +
	"SCREEN_SHOT		Format window for screen shots.\n" +
	"TRACE_LOADER		Trace all calls to the classloader.\n" +
	"TRACE_LOADER_STACK	Trace all calls to the classloader + print out stack.\n" +
	"TEST			Run recorded tests.\n" +
	"DEBUG_DEBUGGER		Print out data on GCs.\n" +
	"DONT_PAUSE_ON_STOP	Let the target program continue to run when recording stops.\n" +
	"\n" +
	"NOTHING			Debugifier: don't do any instrumentation.\n" +
	"DEBUG_DEBUGIFY		Debugifier: print out details during instrumentation.\n" +
	"ATHROW			Debugifier: don't instrument throws.\n" +
	"CATCH			Debugifier: don't instrument catch.\n" +
	"ASTORE			Debugifier: don't instrument astore.\n" +
	"AASTORE			Debugifier: don't instrument aastore.\n" +
	"IASTORE			Debugifier: don't instrument iastore.\n" +
	"RETURN			Debugifier: don't instrument return.\n" +
	"RETURNVALUE		Debugifier: don't instrument returnvalue.\n" +
	"INVOKEVIRTUAL		Debugifier: don't instrument invokevirtual.\n" +
	"IINC			Debugifier: don't instrument iinc.\n" +
	"ISTORE			Debugifier: don't instrument istore.\n" +
	"PUTFIELD		Debugifier: don't instrument putfield.\n" +
	"PUTSTATIC		Debugifier: don't instrument putstatic.\n" +
	"INVOKESTATIC		Debugifier: don't instrument invokestatic.\n" +
	"ARGUMENTS		Debugifier: don't instrument arguments.\n" +
	"NEW			Debugifier: don't instrument new.\n" +
	"NO_LOCKS		Debugifier: don't instrument locks.\n" +
	"PUBLIC_ONLY		Debugifier: only change all IVs to be public.\n" +
	"DONT_REPLACE_VECTOR	Debugifier: don't replace Vectors, etc.\n" +
	"PUTFIELD_ONLY		Debugifier: only instrument putfield.\n\n" +
	"                                      Type <Ctrl-G> to close this help message.\n"+
	"\n";

	
	
    public void addButtons(JMenuItem b0, JMenuItem b1, JMenuItem b2) {
	this.b0=b0;
	this.b1=b1;
	this.b2=b2;
    }

    public void actionPerformed(ActionEvent event) {
	if (event.getSource() == b0) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "ghelp");
	    dc.execute();
	    return;
	}
	if (event.getSource() == b1) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "fhelp");
	    dc.execute();
	    return;
	}
	if (event.getSource() == b2) {
		DebuggerCommand dc = new DebuggerCommand(this.getClass(), "dhelp");
		dc.execute();
		return;
	}
    }
    public static void fhelp() {
	MiniBuffer.messageLong(fhelpMessage, false);
	return;
    }
    public static void ghelp() {
	MiniBuffer.messageLong(ghelpMessage, false);
	return;
    }
	public static void dhelp() {
	MiniBuffer.messageLong(dhelpMessage, false);
	return;
	}
}
