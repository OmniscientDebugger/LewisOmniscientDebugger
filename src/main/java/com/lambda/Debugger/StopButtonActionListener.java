/*                        StopButtonActionListener.java

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


public class StopButtonActionListener implements ActionListener {

    JButton b0, b1;

    public StopButtonActionListener(){}

    public StopButtonActionListener(JButton b0) {
	this.b0=b0;	// startButton
    }

  public void actionPerformed(ActionEvent event) {
    Class clazz=null;
    Object e = event.getSource();
    synchronized(D.class) {
	if (e == b0) {
	    if (b0.getText() == StopButton.START_TARGET) {
		Debugger.PAUSED = !StopButton.recordCB.isSelected();
		if (Debugger.PAUSED) {
		    D.DISABLE = true;
		    b0.setText(StopButton.START);
		}
		else {
		    D.DISABLE = false;
		    b0.setText(StopButton.STOP);
		}
		Debugger.INSTRUMENT = StopButton.instrumentCB.isSelected();

		if (!Debugger.CMD_LINE) {
		    // Don't know how to tell BCEL to load  from different directory
		    //DebugifyingClassLoader dcl = new DebugifyingClassLoader(Debugger.DIRECTORY);
	    
		    //System.out.println("Debugifying files. (Cannot use class loader from file menu -- see docs.)");
		    //if (Debugger.INSTRUMENT) DebugifyFiles.debugify(Debugger.DIRECTORY);
	    
		    try {
			clazz = Debugger.classLoader.loadClass(Debugger.programName); 
			Debugger.clazz = clazz;
		    }
		    catch (Exception ex) { ex.printStackTrace(); }
		}

		Debugger.startTarget(Debugger.clazz);	// Not called from cmd line. Cannot have args.
		return;
	    }
		
	    if (b0.getText() == StopButton.STOP) {
		b0.setText(StopButton.START);
		D.DISABLE = true;		// Stop collecting
		Debugger.PAUSE_ON_STOP = StopButton.pauseProgamCB.isSelected();
		if (Debugger.PAUSE_ON_STOP) Debugger.KILL_TARGET_ON_STOP = false;
		if (!StopButton.debuggerCB.isSelected()) return;
		Debugger.stopTarget();
		//StopButton.delete();
	    }
	    else {
		b0.setText(StopButton.STOP);
		D.DISABLE = false;		// Start collecting
		D.resumeProgram();
	    }
	}
    }
  }
}
