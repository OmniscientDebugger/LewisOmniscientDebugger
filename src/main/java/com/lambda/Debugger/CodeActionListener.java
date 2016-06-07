/*                        CodeActionListener.java

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

import javax.swing.JButton;


class CodeActionListener implements ActionListener {

  JButton b0, b1, b2, b3, b4, b5, b6, b7, b8, b9;

  public CodeActionListener(){}

  public CodeActionListener(JButton b0,JButton b1,JButton b2,JButton b3,JButton b4,JButton b5,
			    JButton b6,JButton b7, JButton b8,JButton b9) {
    this.b0=b0;		// First line in this fn
    this.b1=b1;		// previous line in this fn
    this.b2=b2;		// previous TS any fn
    this.b3=b3;		// next TS any fn
    this.b4=b4;		// next line in this fn
    this.b5=b5;		// Last line in this fn
    this.b6=b6;		// Loop forward (next TS on this line, this stack frame only)
    this.b7=b7;		// Loop backward (prev TS on this line, this stack frame only)
    this.b8=b8;		// Up to calling function (b6 -1)
    this.b9=b9;		// Return to calling function  (b7 +1)
  }



  public void actionPerformed(ActionEvent event) {
    try {
      if (TimeStamp.empty()) {
	Debugger.message("No Time Stamps recorded. Is the target program debugified?", true);
	return;
      }


      if (!Debugger.BUG && event.getSource() == b0) {
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "firstLine");
	dc.execute();
	return;
      }
	
      if (event.getSource() == b1) {
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "previousLineThisFunction");
	dc.execute();
	return;
      }

      if (event.getSource() == b2){
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "previousLineAnyFunction");
	dc.execute();
	return;
      }

      if (event.getSource() == b3){
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "nextLineAnyFunction");
	dc.execute();
	return;
      }

      if (event.getSource() == b4){
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "nextLineThisFunction");
	dc.execute();
	return;
      }

      if (event.getSource() == b5) {
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "lastLine");
	dc.execute();
	return;
      }

      if (event.getSource() == b6){
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "nextIteration");
	dc.execute();
	return;
      }

      if (event.getSource() == b7){
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "previousIteration");
	dc.execute();
	return;
      }

      if (event.getSource() == b8) {
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "upToCaller");
	dc.execute();
	return;
      }

      if (event.getSource() == b9) {
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "returnToCaller");
	dc.execute();
	return;
      }

      if (event.getSource() == b0)  {
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "firstLineB");
	dc.execute();
	return;
      }
    }
    catch (NullPointerException e) {throw e;}
  }




  // 				THE COMMANDS

  public static void firstLine() {
    TimeStamp now = TimeStamp.currentTime();
    TimeStamp ts = now.getFirstTSThisFunction();
    if (ts == null) {Debugger.message("Already at first line", true); return;}
    Debugger.revert(ts);
    return;
  }

  public static void previousLineThisFunction()  {
    TimeStamp now = TimeStamp.currentTime();

    TimeStamp ts =  now.getPreviousLineThisFunction();
    if ( (ts == null)  || (ts == now) )
      Debugger.message("No previous line in this method.", true);
    else
      Debugger.revert(ts);
  }


  public static void previousLineAnyFunction()  {
    TimeStamp now = TimeStamp.currentTime();

    TimeStamp ts =  now.getPreviousLineThisThread();
    if (ts == null)
      Debugger.message("Fell off front of thread?", true);
    else
      Debugger.revert(ts);
  }


  public static void nextLineAnyFunction()    {
    TimeStamp now = TimeStamp.currentTime();

    TimeStamp ts =  now.getNextLineThisThread();
    if (ts == null)
      Debugger.message("Fell off end of thread?", true);
    else
      Debugger.revert(ts);
  }


  public static void nextLineThisFunction()  {
    TimeStamp now = TimeStamp.currentTime();

    TimeStamp ts =  now.getNextLineThisFunction();
    if ( (ts == null)  || (ts == now) )
      Debugger.message("No next line in this method.", true);
    else
      Debugger.revert(ts);
  }


  public static void lastLine() {
    TimeStamp now = TimeStamp.currentTime();
    TimeStamp ts =  now.getLastThisFunction();

    if  (ts == null) {
	ts = now.getLastThisThread();
	Debugger.revert(ts);
	Debugger.message("Fell off end of thread?", true);
    }
    else if (ts == now)
	Debugger.message("Already on last line.", true);
    else
	Debugger.revert(ts);
  }


  public static void nextIteration()  {
    TimeStamp now = TimeStamp.currentTime();
    TimeStamp ts =  now.getNextIteration();
    if (ts == null) 
      Debugger.message("No more iterations in this method.", true);
    else
      Debugger.revert(ts);
  }


  public static void previousIteration()  {
    TimeStamp now = TimeStamp.currentTime();
    TimeStamp ts =  now.getPreviousIteration();
    if (ts == null) 
      Debugger.message("No more iterations in this method.", true);
    else
      Debugger.revert(ts);
  }


  public static void upToCaller() {
    TimeStamp now = TimeStamp.currentTime();
    TraceLine tl  =  now.getFirstThisFunction();
    if ((tl == null) || (tl.time == -1)) 
      Debugger.message("Fell off front of thread?", true);
    else {
      TimeStamp ts = TimeStamp.lookup(tl.time);
      Debugger.revert(ts);		    
    }
  }


  public static void returnToCaller() {
    TimeStamp now = TimeStamp.currentTime();
    TimeStamp ts1, ts =  now.getLastThisFunction();

    if (ts == null) {
      ts = now.getLastThisThread();
      Debugger.revert(ts);	
      Debugger.message("Fell off end of thread?", true);
      return;
    }
    ts1 = ts.getNextThisThread();
    if (ts1 == null) {
      Debugger.revert(ts);
      Debugger.message("Fell off end of thread?", true);
      return;
    }
    Debugger.revert(ts1);
  }


  public static void firstLineB() {
    TimeStamp now = TimeStamp.currentTime();
    TimeStamp ts =  now.getFirstTSThisFunction();
    Debugger.revert(ts);
  }
}
	    

