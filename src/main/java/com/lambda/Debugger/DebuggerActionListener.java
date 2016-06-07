/*                        DebuggerActionListener.java

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


class DebuggerActionListener implements ActionListener {

  JButton b0, b1, b2, b3, b4, b5, b6, b7;

  public DebuggerActionListener(){}

  public DebuggerActionListener(JButton b3,JButton b4,JButton b5, JButton b6,JButton b7) {
    this.b3=b3;
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
    //    (new NullPointerException("TEST")).printStackTrace();

    if (event.getSource() == b3) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "previousRevert");
      dc.execute();
      return;
    }

    else if (event.getSource() == b4) {	  
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "first");
      dc.execute();
      return;
    }

    else if (event.getSource() == b5) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "previous");
      dc.execute();
      return;
    }

    else if (event.getSource() == b6) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "next");
      dc.execute();
      return;
    }

    else if (event.getSource() == b7) {
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "last");
      dc.execute();
      return;
    }
  }



  public static void last() {
    Debugger.revert(TimeStamp.eot());
  }

  public static void previousRevert() {
    Debugger.revertPrevious();
  }

  public static void first() {
    Debugger.revert(TimeStamp.bot1());		// index==0 is the Invalid Traceline.
  }

  public static void previous() {
    TimeStamp ts =  TimeStamp.currentTime().getPrevious();		// never returns null. ?!
    if (ts == null) 
      Debugger.message("IMPOSSIBLE?! getPrevious DebuggerActionListener.java", true);
    else
      Debugger.revert(ts);
  }

  public static void next() {
    TimeStamp ts =  TimeStamp.currentTime().getNext();	// never returns null. ?!
    if (ts == null) 
      Debugger.message("IMPOSSIBLE?! getNext DebuggerActionListener.java", true);
    else
      Debugger.revert(ts);
  }

}
