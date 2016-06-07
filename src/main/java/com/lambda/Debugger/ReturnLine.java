/*                        ReturnLine.java

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


import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public final class ReturnLine extends MethodLine {

  public TraceLine 		caller;
  public Object 		returnValue;
  public String	 		printString;



    public void verify(int eot) {
	if (
	    //	    (TimeStamp.getType(time) == TimeStamp.CALL) ||
	     	     (TimeStamp.getType(time) == TimeStamp.RETURN)
	     //	     (TimeStamp.getType(time) == TimeStamp.ABSENT) ||
	     //	     (TimeStamp.getType(time) == TimeStamp.CATCH)
	     )
	    return;
	throw new DebuggerException("RL.verify() failed on "+this+TimeStamp.getTypeString(time));
    }


  //    constructor
  public ReturnLine(int time, String meth, Object rv, TraceLine caller) {
    this.time = time;
    returnValue=rv;
    this.caller = caller;
    traceLine = caller.traceLine;
  }
  public ReturnLine() {}

  public Object getSelectedObject(int x, FontMetrics fm) {
    int l = caller.getDepth();
    String str=spaces((2*l)-2)+caller.method+" -> ";
    if (x < fm.stringWidth(str)) return(null);
    str +=  trimToLength(returnValue, 50);
    if (x < fm.stringWidth(str)) return(returnValue);
    return(null);
  }


  public String toString() {
    if (printString != null) return(printString);
    String s = trimToLength(caller.returnValue, 50);
    if (caller.getDepth() < 2)				// Should never be 0. It comes from a TraceLine!
      printString = caller.method+" -> "+Misc.replace(s, "\n", "\\n");
    else
      printString = spaces((caller.getDepth()*2)-2)+caller.method+" -> "+Misc.replace(s,"\n", "\\n");;
    return printString;
  }


  public String toString(int room) {
    if (room < 20)
      return ("<ReturnLine " + time +">");
    if (room < 50)
      return("<ReturnLine " + time + " " + this+">");
    return("<ReturnLine " + time + " " + getSourceLine() +" "+ getThread()+" "+this+" "+caller.toString(20)+">");
  }

  public static ReturnLine addReturnLine(Object rv, TraceLine caller) {
    if (rv == null) rv = ShadowNull.NULL;
    int slIndex = TimeStamp.getSourceIndex(caller.time);
    String meth = caller.method;
    int time = TimeStamp.addStamp(slIndex, TimeStamp.RETURN, caller);
    ReturnLine rl = new ReturnLine(time, meth,  rv,  caller);
    TraceLine.addTrace(rl);
    caller.addReturnValue(rv, rl);
    //    SideEffects.check(caller.thisObj, meth, caller);
    return rl;
  }



  public TraceLine getFirstCall() {
    return(caller.getFirstCall());
  }

  public TraceLine getNextCall() {
    return(caller.getNextCall(this));
  }

  public TraceLine getPreviousCall() {
    return(caller.getPreviousCall(this));
  }

  public TraceLine getLastCall() {
    return(caller.getLastCall());
  }




}
