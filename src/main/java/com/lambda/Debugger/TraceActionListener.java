/*                        TraceActionListener.java

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


class TraceActionListener implements ActionListener {

  JButton b0, b1, b2, b3, b4, b5, b6, b7;

  public TraceActionListener(){}

  public TraceActionListener(JButton b4, JButton b5, JButton b6, JButton b7) {
    this.b4=b4;
    this.b5=b5;
    this.b6=b6;
    this.b7=b7;
  }

  public void actionPerformed(ActionEvent event) {
    if (TimeStamp.empty()) {
      Debugger.message("No Time Stamps recorded. Is the target program debugified?", true);
      return;
    }




    if (event.getSource() == b4) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "first");
      dc.execute();
      return;
    }

    if (event.getSource() == b5) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "previous");
      dc.execute();
      return;
    }	

    if (event.getSource() == b6) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "next");
      dc.execute();
      return;
    }

    if (event.getSource() == b7) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "last");
      dc.execute();
      return;
    }
  }







  public static void next() {
    MethodLine ml = (MethodLine)Debugger.TracePList.getSelectedValue();
    if ((ml == null) || (ml instanceof CatchLine) || (ml instanceof ThrowLine)) {
      Debugger.message("Please select a method call line.", true);
      return;
    }
    MethodLine ml2 = ml.getNextCall();
    if (ml2 == ml) 
      Debugger.message("No more invocations of this method.", true);
    else
      Debugger.revert(ml2.lookupTS());
    return;
  }

  public static void first() {
    MethodLine ml = (MethodLine)Debugger.TracePList.getSelectedValue();
    if ((ml == null) || (ml instanceof CatchLine) || (ml instanceof ThrowLine)) {
      Debugger.message("Please select a method call line.", true);
      return;
    }
    TraceLine tl1 = ml.getFirstCall();
    if (tl1 == ml)
      Debugger.message("First invocation of this method.", true);
    else
      Debugger.revert(tl1.lookupTS());

  }

  public static void previous() {
    MethodLine ml = (MethodLine)Debugger.TracePList.getSelectedValue();
    if ((ml == null) || (ml instanceof CatchLine) || (ml instanceof ThrowLine)) {
      Debugger.message("Please select a method call line.", true);
      return;
    }
    TraceLine tl1  =  ml.getPreviousCall();
    if (tl1 == ml) 
      Debugger.message("No previous invocations of this method.", true);
    else
      Debugger.revert(tl1.lookupTS());
    return;
  }

  public static void last() {
    MethodLine ml = (MethodLine)Debugger.TracePList.getSelectedValue();
    if ((ml == null) || (ml instanceof CatchLine) || (ml instanceof ThrowLine)) {
      Debugger.message("Please select a method call line.", true);
      return;
    }
    TraceLine tl1 = ml.getLastCall();
    if (tl1 == ml)
      Debugger.message("Last invocation of this method.", true);
    else
      Debugger.revert(tl1.lookupTS());
    return;
  }
}
