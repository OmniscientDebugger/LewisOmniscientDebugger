/*                        ObjectsMenuActionListener.java

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


class ObjectsMenuActionListener implements ActionListener {

  JMenuItem b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15;

  public void addButtons(JMenuItem b0, JMenuItem b1, JMenuItem b2, JMenuItem b3, JMenuItem b4,
			 JMenuItem b5, JMenuItem b6, JMenuItem b7, JMenuItem b8, JMenuItem b9,
			 JMenuItem b10, JMenuItem b11, JMenuItem b12, JMenuItem b13, JMenuItem b14, JMenuItem b15) {
    this.b0=b0;
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
  }

  public void actionPerformed(ActionEvent event) {
    if (TimeStamp.empty()) {
      Debugger.message("No Time Stamps recorded. Is the target program debugified?", true);
      return;
    }


    if (event.getSource() == b0) {			// "Copy Class"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "copyClass");
      dc.execute();
      return;
    }
    else if (event.getSource() == b1) {		//"Remove"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "remove");
      dc.execute();
      return;
    }
    else if (event.getSource() == b2) {		//"Expand"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "expand");
      dc.execute();
      return;
    }
    else if (event.getSource() == b3) {		//"Select"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "select");
      dc.execute();
      return;
    }
    else if (event.getSource() == b4) {		//"Select Local"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "selectLocal");
      dc.execute();
      return;
    }
    else if (event.getSource() == b5) {		//"Retain"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "retain");
      dc.execute();
      return;
    }
    else if (event.getSource() == b6) {		//"Set"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "set");
      dc.execute();
      return;
    }
    else if (event.getSource() == b7) {		//"abort"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "abort");
      dc.execute();
      return;
    }
    else if (event.getSource() == b8) {		//"copy
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "copy");
      dc.execute();
      return;
    }
    else if (event.getSource() == b9) {		//"showAll"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "showAll");
      dc.execute();
      return;
    }
    else if (event.getSource() == b10) {		//"restore"
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "restore");
      dc.execute();
      return;
    }
    else if (event.getSource() == b11) {		//
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "add");
      dc.execute();
      return;
    }
    else if (event.getSource() == b12) {		//
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "hex");
      dc.execute();
      return;
    }
    else if (event.getSource() == b13) {		//
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "print");
      dc.execute();
      return;
    }
    else if (event.getSource() == b14) {		//
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "tostring");
      dc.execute();
      return;
    }
    else if (event.getSource() == b15) {		//
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "input");
      dc.execute();
      return;
    }
  }


  public static void restore() {			//
    Shadow sh =(Shadow) Debugger.ObjectsPList.getSelectedValue();
    if (sh instanceof ShadowInstanceVariable) return;
    int index = Debugger.ObjectsPList.getSelectedIndex();
    ObjectPane.close(index);
    DisplayVariables.restore(sh);
    ObjectPane.expand(index);
  }

  public static void showAll() {
    int index = Debugger.ObjectsPList.getSelectedIndex();
    ObjectPane.close(index);
    ObjectPane.expand(index, true);
  }

  public static void copyClass() {			// "Copy Class"
    int index = Debugger.ObjectsPList.getSelectedIndex();
    ObjectPane.expandClass(index);
    Debugger.ObjectsPList.updateUI();
  }

  public static void add() {			// "Copy Object
    int index = Debugger.ObjectsPList.getSelectedIndex();
    ObjectPane.add(index);
    Debugger.ObjectsPList.updateUI();
  }

  public static void remove() {		//"Remove"
    int index = Debugger.ObjectsPList.getSelectedIndex();
    ObjectPane.remove(index);
    Debugger.ObjectsPList.updateUI();
  }

  public static void expand() {		//"Expand"
    int index = Debugger.ObjectsPList.getSelectedIndex();
    ObjectPane.expand(index);
    Debugger.ObjectsPList.updateUI();
  }

  public static void select() {		//"Select"
    int index = Debugger.ObjectsPList.getSelectedIndex();
    ObjectPane.selectFrom(index);
    Debugger.ObjectsPList.updateUI();
  }

  public static void selectLocal() {		//"Select Local"
    int index = Debugger.LocalsPList.getSelectedIndex();
    LocalsPane.selectFrom(index);
    Debugger.ObjectsPList.updateUI();
  }

  public static void retain() {
    int index = Debugger.ObjectsPList.getSelectedIndex();
    ObjectPane.retain(index);
    Debugger.ObjectsPList.updateUI();
  }

  public static void set() {
    int index = Debugger.ObjectsPList.getSelectedIndex();
    //    System.out.println("Setting value " + index);

    if (Debugger.mainTimeLine)
      Debugger.message("Switch to secondary time line before altering variable values", true);
    else if (index == -1)
      Debugger.message("Select a variable to alter", true);
    else      
      MiniBuffer.inputValue();
  }

  public static void abort() {
    MiniBuffer.abort();
  }

  public static void copy() {
    int index = Debugger.ObjectsPList.getSelectedIndex();
    String s = ObjectPane.getValueString(index);
    MiniBuffer.copyValue(s);
  }

  public static void hex() {
      ObjectPane.hex();
  }

  public static void print() {
    int index = Debugger.ObjectsPList.getSelectedIndex();
    String s = ObjectPane.getValueString(index);
    System.out.println("\n\n\n");
    Shadow sh = ObjectPane.getShadowAt(index);
    if (sh.obj instanceof String) {
	Debugger.println("");
	System.out.println(s);
	MiniBuffer.messageLong(s, false);
	return;
    }
    Debugger.println(s);
    print(sh);
    MiniBuffer.messageLong(s, false);      
  }

  public static void tostring() {
    int index = Debugger.ObjectsPList.getSelectedIndex();
    String s;
    System.out.println("\n\n\n");
    Shadow sh = ObjectPane.getShadowAt(index);
	if (sh instanceof ShadowInstanceVariable) {
	    Object o =  ((ShadowInstanceVariable) sh).getCurrentValue();
	    sh = Shadow.get(o);
	}
	Object obj = sh.obj;
    if (obj == null)
		s = "null";
	else
		s = obj.toString();
    Debugger.println(s);
    MiniBuffer.messageLong(s, false);      
  }

  public static void input() {
      MiniBuffer.inputObjectAndDisplay();
  }

    private static final int MAX_VARS_DISPLAYED = 10000;

    public static void print(Shadow sh) {
	if (sh instanceof ShadowInstanceVariable) {
	    Object o =  ((ShadowInstanceVariable) sh).getCurrentValue();
	    sh = Shadow.get(o);
	}
	int len = Math.min(sh.size(), MAX_VARS_DISPLAYED);
	java.util.List displayedVars = DisplayVariables.getDisplayedVariables(sh);
	for (int j=0; j<len; j++) {	// How to deal with large arrays? CHOP AT MAX_VARS_DISPLAYED.
	    ShadowInstanceVariable siv2 = new ShadowInstanceVariable(sh, j);
	    Debugger.println(""+siv2);
	}

	HistoryList bhl = sh.getBlockedHL();
	if (bhl != null) Debugger.println(""+new ShadowInstanceVariable(sh, bhl, Shadow.BLOCKEDON));
	SleeperSet ss =  sh.getSleeperSet();
	if (ss != null) {
	    HistoryList ohl = ss.owner;
	    Debugger.println(""+new ShadowInstanceVariable(sh, ohl, Shadow.OWNER));
	    HistoryList shl = ss.sleepers;
	    Debugger.println(""+new ShadowInstanceVariable(sh, shl, Shadow.SLEEPERS));
	    HistoryList whl = ss.waiters;
	    Debugger.println(""+new ShadowInstanceVariable(sh, whl, Shadow.WAITERS));
	}
    }
}
