/*                        ObjectActionListener.java

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


public class ObjectActionListener implements ActionListener {

  JButton b0, b1, b2, b3, b4, b5, b6;

  public ObjectActionListener(){}

  public ObjectActionListener(JButton b0, JButton b1, JButton b2, JButton b3) {
    this.b0=b0;
    this.b1=b1;
    this.b2=b2;
    this.b3=b3;
  }



  public void actionPerformed(ActionEvent event) {
    int index = Debugger.ObjectsPList.getSelectedIndex();
    /*
      Shadow sh = (Shadow) ObjectPane.getShadowAt(index);
      if (sh instanceof ShadowInstanceVariable) {
	  ShadowInstanceVariable siv = (ShadowInstanceVariable) sh;
	  if (siv.level == 2) {
	      Debugger.message("Cannot navigate on second level IVs. Add to Object Pane first.", true);
	      return;
	  }
      }
    */
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
    TimeStamp ts = ObjectPane.getFirst(index);
    if ((ts != null) && (ts.earlierThan(TimeStamp.currentTime())))
      Debugger.revert(ts);
    else
      Debugger.message("First value", true);
  }

  public static void previous(int index) {
    TimeStamp ts = ObjectPane.getPrevious(index);
    if (ts != null)
      Debugger.revert(ts);
    else
      Debugger.message("No previous values", true);
  }

  public static void next(int index) {
    TimeStamp ts = ObjectPane.getNext(index);
    if (ts != null)
      Debugger.revert(ts);
    else
      Debugger.message("No more values", true);
  }

  public static void last(int index) {
    TimeStamp ts = ObjectPane.getLast(index);
    if ((ts != null) && (ts.laterThan(TimeStamp.currentTime())))
      Debugger.revert(ts);
    else
      Debugger.message("Last value", true);
  }

}
	    
