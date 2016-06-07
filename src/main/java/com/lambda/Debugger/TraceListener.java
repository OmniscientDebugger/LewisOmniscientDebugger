/*                        TraceListener.java

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

public class TraceListener implements ActionListener, ListSelectionListener {
    
  public void valueChanged(ListSelectionEvent event) {
    if (TimeStamp.empty()) {
      Debugger.message("No Time Stamps recorded. Is the target program debugified?", true);
      return;
    }


    int index = Debugger.TracePList.getSelectedIndex();
    if ((event.getSource() == Debugger.TracePList) && (!event.getValueIsAdjusting()) &&
	(index >= 0) && (!Debugger.reverting)) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "select", index);
      dc.execute();
      return;
    }
  }


  

  public static void select(int index) {
    boolean firstLine = Debugger.firstLine.getState();			// JCheckBoxItem for reverting to: call point/first line
    TimeStamp ts1=null;
    MethodLine ml = (MethodLine)TraceLine.SINGLETON.getElementAt(index);
    TimeStamp ts = ml.lookupTS();

    if ( (ml instanceof TraceLine) && firstLine ) {
      ts1 = ts.getNextThisThread();
      if ( (ts1 != null) && (TimeStamp.getType(ts1.time) == TimeStamp.ABSENT) ) {
	Debugger.revert(ts);
	return;
      }
      if ( (ts1 != null) && (TimeStamp.getType(ts1.time) == TimeStamp.FIRST) ) {
	Debugger.revert(ts1);
	return;
      }
    }

    if ( (ml instanceof ReturnLine) && firstLine ) {
      ts1 = ts.getPreviousThisThread();
      if (ts1 != null) {
	  if (TimeStamp.getType(ts1.time) == TimeStamp.LAST)
	      Debugger.revert(ts1);
	  else
	      Debugger.revert(ts);
	  return;
      }
    }

    

    Debugger.revert(ts);			// if it has any traces inside.
  }
   

  public void actionPerformed(ActionEvent event) {
    //D.println("TraceListener event: "+event+" "+
    //	   Debugger.TracePList.getSelectedValue());
  }

}
