/*                        TraceMenuActionListener.java

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
import javax.swing.text.*;
import javax.swing.event.*;


class TraceMenuActionListener implements ActionListener {

  JMenuItem b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18;

  public void addButtons(JMenuItem b1, JMenuItem b2, JMenuItem b3,
			 JMenuItem b4, JMenuItem b5, JMenuItem b6, JMenuItem b7, JMenuItem b8
			 , JMenuItem b9, JMenuItem b10, JMenuItem b11, JMenuItem b12, JMenuItem b13,
			 JMenuItem b14, JMenuItem b15, JMenuItem b16, JMenuItem b17, JMenuItem b18) {
    this.b1=b1;
    this.b2=b2;
    this.b3=b3;
    this.b4=b4;
    this.b5=b5;
    this.b6=b6;
    this.b7=b7;
    this.b8=b8;
    this.b9=b9;
    this.b10=b10;
    this.b11=b11;
    this.b12=b12;
    this.b13=b13;
    this.b14=b14;
    this.b15=b15;
    this.b16=b16;
    this.b17=b17;
    this.b18=b18;
  }

  public void actionPerformed(ActionEvent event) {
    TraceLine tl = null;

    Object eventSource = event.getSource();


    if (eventSource == b8) {		//"search"
      MiniBuffer.search();
      return;
    }
    if (eventSource == b9) {		//"reverse-search"
      MiniBuffer.rsearch();
      return;
    }
    if (eventSource == b10) {		//"end search"
      MiniBuffer.endSearch();
      return;
    }
    if (eventSource == b11) {		//"fget"
      MiniBuffer.beginCdata();
      return;
    }
    if (eventSource == b12) {		//"fget"
      MiniBuffer.beginFGet();
      return;
    }
    if (eventSource == b13) {		//"save fget as start"
	String s = MiniBuffer.getText();
	if (!s.startsWith("fget: ")) {Debugger.message("Not showing an fget", true); return;}
	s = s.substring(6, s.length());
	EventInterface.setStartPatternString(s);
	Debugger.message("Start pattern saved to .debuggerDefaults", false);
	Defaults.writeDefaults();
      return;
    }
    if (eventSource == b14) {		//"save fget as stop"
	String s = MiniBuffer.getText();
	if (!s.startsWith("fget: ")) {Debugger.message("Not showing an fget", true); return;}
	s = s.substring(6, s.length());
	EventInterface.setStopPatternString(s);
	Debugger.message("Stop pattern saved to .debuggerDefaults", false);
	Defaults.writeDefaults();
	return;
    }
    if (eventSource == b15) {		// count
	String s = MiniBuffer.getText();
	if (!s.startsWith("fget: ") && !s.startsWith("cdata: ")) {Debugger.message("Not showing a search", true); return;}
	MiniBuffer.countMatches();
	return;
    }
    if (eventSource == b16) {		//"save fget as stop"
	SourceLine sl = TimeStamp.getSourceLine(TimeStamp.ct());
	String s = "sf = \""+ sl.getFile() + "\" & sl = " + sl.getLine();
	EventInterface.setStartPatternString(s);
	Debugger.message("Start pattern saved to .debuggerDefaults", false);
	Defaults.writeDefaults();
	return;
    }

    MethodLine ml = (MethodLine) Debugger.TracePList.getSelectedValue();
    if (ml instanceof TraceLine) tl = (TraceLine) ml;
    if (tl == null) {Debugger.message("No Traceline selected.", true); return;}
    Object thisObj = tl.thisObj;
    //		D.println("TraceMenuActionListener event: "+event+" "+ Debugger.TracePList.getSelectedValue());


    if (eventSource == b1) {		//"Copy this"
      ObjectPane.add(tl.thisObj);
    }

    else if (eventSource == b2) {		//"Copy argument 1"
      if (tl.getArgCount() > 0) 
	ObjectPane.add(tl.getArg(0));
      else
	Debugger.message("There are not enough arguments to this method.", false);
    }

    else if (eventSource == b3) {		//"Copy argument 2"
      if (tl.getArgCount() > 1) 
	ObjectPane.add(tl.getArg(1));
      else
	Debugger.message("There are not enough arguments to this method.", true);
    }

    else if (eventSource == b4) {		//"Copy argument 3"
      if (tl.getArgCount() > 2) 
	ObjectPane.add(tl.getArg(2));
      else
	Debugger.message("There are not enough arguments to this method.", true);
    }

    else if (eventSource == b5) {		//"Copy argument 4"
      if (tl.getArgCount() > 3) 
	ObjectPane.add(tl.getArg(3));
      else
	Debugger.message("There are not enough arguments to this method.", true);
    }

    else if (eventSource == b6) {		//"Copy argument 5"
      if (tl.getArgCount() > 4) 
	ObjectPane.add(tl.getArg(4));
      else
	Debugger.message("There are not enough arguments to this method.", true);
    }
    else if (eventSource == b7) {		//"Copy return value"
      ObjectPane.add(tl.returnValue);
    }

    else if (eventSource == b17) {
      createFGETQuery();
    }
    else if (eventSource == b18) {
      createFGETSLQuery();
    }
  }

    public static void createFGETQuery() {
	 MethodLine ml = (MethodLine) Debugger.TracePList.getSelectedValue();
	 if (ml instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml;
		String m = tl.method;
		Class c = (tl.thisObj instanceof Class) ? (Class) tl.thisObj : tl.thisObj.getClass();
		String cs = "";
		if (c != null) cs = c.toString();
		cs = cs.substring(6, cs.length());
		String thread = ThreadPane.getName(tl.getThread());
		String query = "port = call & callObjectClass = #" + cs + " & callMethodName = \"" +
			m + "\"" + " & thread = " + thread;
		EventInterface.setPattern(query);
		MiniBuffer.beginFGet();
		MiniBuffer.beginFGet();
		
		}
	}

    public static void createFGETSLQuery() {
	int t = TimeStamp.ct();


		String thread = ThreadPane.getName(TimeStamp.getThread(t));
		SourceLine sl = TimeStamp.getSourceLine(t);
		String sf = sl.getFile();
		int l = sl.getLine();
		String query = "sf = \"" + sf + "\" & sl = " + l +  " & thread = " + thread;
		EventInterface.setPattern(query);
		MiniBuffer.beginFGet();
		MiniBuffer.beginFGet();
	}

}
