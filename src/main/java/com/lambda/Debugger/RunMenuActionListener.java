/*                        RunMenuActionListener.java

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


class RunMenuActionListener implements ActionListener {
  //  public static CallInteractiveDialog   callInteractiveDialog;

  JMenuItem b0, b1, b2, b3, b4, b5, b6, b7, b8, b9;

  public void addButtons(JMenuItem b0, JMenuItem b2, JMenuItem b3, JMenuItem b4,
			 JMenuItem b5, JMenuItem b6, JMenuItem b7, JMenuItem b8, JMenuItem b9) {
    this.b0=b0;
    this.b2=b2;
    this.b3=b3;
    this.b4=b4;
    this.b5=b5;
    this.b6=b6;
    this.b7=b7;
    this.b8=b8;
    this.b9=b9;
  }

  public void actionPerformed(ActionEvent event) {
      Object e = event.getSource();

    if (e == b0) {			// "Clear & restart"
	if (!Debugger.mainTimeLine) {
	    Debugger.message("Clearing alternate time line not allowed", true);
	    return;
	}
      Debugger.clear();
      Debugger.restartTarget();
      Debugger.message("Clearing memory and restarting.", true);
      return;
    }

    if (e == b2) {		// "Clear"
	if (!Debugger.mainTimeLine) {
	    Debugger.message("Clearing alternate time line not allowed", true);
	    return;
	}
	Debugger.clear();
      return;
    }



    if (e == b3) {		// "Start Recording"
	String s = TTYPane.getCurrent();
	//	D.setStartString(s);
	Debugger.message("NYI: Recording will Automatically start on line "+s, false);
	Defaults.writeDefaults();
	return;
      }	

      if (e == b4) {		// "Stop Recording"
	String s = TTYPane.getCurrent();
	//	D.setStopString(s);
	Debugger.message("NYI:Recording will Automatically stop on line "+s, false);
	Defaults.writeDefaults();
	return;
      }		


    if (e == b5) {		// "Start Recording"
      FileLine fl = (FileLine)Debugger.codeJList.getSelectedValue();
      if (fl == null) {Debugger.message("No selected line in Code Pane.", true); return;}
      SourceLine sl = fl.source;
      SourceLine.setStartLine(sl);
      Debugger.message("Recording will Automatically start on line "+sl, false);
      return;
      }	

      if (e == b6) {		// "Stop Recording"
      FileLine fl = (FileLine)Debugger.codeJList.getSelectedValue();
      if (fl == null) {Debugger.message("No selected line in Code Pane.", true); return;}
      SourceLine sl = fl.source;
      SourceLine.setStopLine(sl);
      Debugger.message("Recording will Automatically stop on line "+sl, false);
      return;
      }		

    if (e == b7) {		// "Call Interactively"
      //callInteractiveDialog = new CallInteractiveDialog( Debugger.mainFrame );
      //Completion.createCompletionTable(Shadow.getTable());
      //callInteractiveDialog.show();
      MiniBuffer.evaluateExpressionInitialize();
      return;
    }

    if (e == b8) {		// "Switch"
      Debugger.switchTimeLines(false);
      if (Debugger.mainTimeLine)
	Debugger.message("Switched to Main Time Line.", false);
      else
	Debugger.message("Switched to Secondary Time Line.", false);
      return;
      }	

    if (e == b9) {		// "clear start/stop
	SourceLine.clearStartStop();
	//	D.setStartString(null);	
	//	D.setStopString(null);	
  }

  }

}
