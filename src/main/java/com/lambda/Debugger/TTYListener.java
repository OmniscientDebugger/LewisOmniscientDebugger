/*                        TTYListener.java

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

public class TTYListener implements ListSelectionListener {
    
  public void valueChanged(ListSelectionEvent event) {
    if (TimeStamp.empty()) {
      Debugger.message("No Time Stamps recorded. Is the target program debugified?", true);
      return;
    }

    int index = Debugger.TTYPList.getSelectedIndex();
    if ((event.getSource() == Debugger.TTYPList) && (!event.getValueIsAdjusting()) &&
	(index >= 0) && (!Debugger.reverting)) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "select", index);
      dc.execute();
      return;
    }
  }


  public static void select(int index) {	  
    ShadowPrintStream sps = (ShadowPrintStream)TTYPane.singleton().getElementAt(index);
    TimeStamp ts = sps.timeStamp();
    Debugger.revert(ts);			// Returnlines should show the call to return
  }
}
