/*                        LocalsActionListener.java

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


class LocalsActionListener implements ActionListener, ListSelectionListener {

  JButton b0, b1, b2, b3, b4;

  public LocalsActionListener(){}

  public LocalsActionListener(JButton b0,JButton b1,JButton b2,JButton b3,JButton b4) {
    this.b0=b0;
    this.b1=b1;
    this.b2=b2;
    this.b3=b3;
    this.b4=b4;
  }

  public void actionPerformed(ActionEvent event) {
    if (TimeStamp.empty()) {
      Debugger.message("No Time Stamps recorded. Is the target program debugified?", true);
      return;
    }
    int index = Debugger.LocalsPList.getSelectedIndex();


    if (event.getSource() == b0) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "first", index);
      dc.execute();
      return;
    }	    
    if (event.getSource() == b1) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "previous", index);
      dc.execute();
      return;
    }
    if (event.getSource() == b2) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "next", index);
      dc.execute();
      return;
    }
    if (event.getSource() == b3) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "last", index);
      dc.execute();
      return;
    }
  }

  public static void first(int index) {
    TimeStamp ts = LocalsPane.getFirst(index);
    if ((ts != null) && (ts.earlierThan(TimeStamp.currentTime())))
      Debugger.revert(ts);
    else
      Debugger.message("First value.", true);
  }	    

  public static void previous(int index) {
    TimeStamp ts = LocalsPane.getPrevious(index);
    if (ts != null)
      Debugger.revert(ts);
    else
      Debugger.message("No previous values.", true);
  }

  public static void next(int index) {
    TimeStamp ts = LocalsPane.getNext(index);
    if (ts != null)
      Debugger.revert(ts);
    else
      Debugger.message("No more values.", true);
  }

  public static void last(int index) {
    TimeStamp ts = LocalsPane.getLast(index);
    if ((ts != null) && (ts.laterThan(TimeStamp.currentTime())))
      Debugger.revert(ts);
    else
      Debugger.message("Last value.", true);
  }

  public void valueChanged(ListSelectionEvent event) {
    if ((event.getSource() == Debugger.LocalsPList) &&
	(!event.getValueIsAdjusting()) &&
	(Debugger.LocalsPList.getSelectedIndex() >= 0)) {
      //D.println("LocalsActionListener:  What is this? "+ event + "\n");  JUST IGNORE SELECTION HERE. IT'S LEGAL, BUT NOT INTERESTING
    }

  }
	    

}
