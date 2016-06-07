/*                        DebugMenuActionListener.java

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

import javax.swing.JMenuItem;


class DebugMenuActionListener implements ActionListener {

    JMenuItem b0, b1, b2, b3, b4, b5, b6, b7, b8, b9;

    public void addButtons(JMenuItem b0, JMenuItem b1, JMenuItem b2, JMenuItem b3, JMenuItem b4, JMenuItem b5, JMenuItem b6, JMenuItem b7, JMenuItem b8) {
	this.b0=b0;
	this.b1=b1;
	this.b2=b2;
	this.b3=b3;
	this.b4=b4;
	this.b5=b5;
	this.b6=b6;
	this.b7=b7;
	this.b8=b8;
    }

    public void actionPerformed(ActionEvent event) {
	if (event.getSource() == b0) {			// Record
	  DebuggerCommand.startRecording();
	  return;
	}
	if (event.getSource() == b1) {			// Record
	  DebuggerCommand.stopRecording();
	  return;
	}
	if (event.getSource() == b2) {			// Record
	  DebuggerCommand.replayRecording();
	  return;
	}
	if (event.getSource() == b3) {			// "Dump"
	    	    Debugger.dump();
	    //	    Shadow.printAll();
	    Clock.dump();
	    return;
	}
	if (event.getSource() == b4) {			// "Statistics"
	    Debugger.printAllStatistics();
	    //EventInterface.dumpState();
	    return;
	}
	if (event.getSource() == b5) {			// "replay"
	    Thread t = new Thread(new Runnable() {public void run() {DebugMenuActionListener.test();}});
	    t.start();
	    Debugger.message("Reverting to all TS...",false);
	    return;
	}	  
	if (event.getSource() == b6) {			// "test"
	    EventInterface.test();
	    BackTrace.test();
	    SourceLine.dump();
	}
	if (event.getSource() == b7) {			// "wide strings"
	    
	    Debugger.message("NON_FUNCTIONAL: Displaying strings in wide/narrow format", true);
	}
	if (event.getSource() == b8) {			//
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "repeat");
	    dc.execute();
	    return;
	}
    }

    static void test() {
	TimeStamp eot = TimeStamp.eot();
	//	DebuggerActionListener.first();
	
	while (TimeStamp.currentTime() != eot) {
	    DebuggerActionListener.next();
	    try{Thread.sleep(100);} catch (InterruptedException e) {}
	}
    }
}
