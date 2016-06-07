/*                        StackListener.java

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



class StackListener implements ActionListener, ListSelectionListener {


    public void valueChanged(ListSelectionEvent event) {
	int index = Debugger.StackPList.getSelectedIndex();
	if (! ((event.getSource() == Debugger.StackPList) && (!event.getValueIsAdjusting()) && (index >= 0)) ) return;

	//StackListElement sle = (StackListElement) Debugger.StackPList.getSelectedValue();
	DebuggerCommand dc = new DebuggerCommand(this.getClass(), "select", index);
	dc.execute();
	return;
    }


    public static void gotoStack() {
	int index = Debugger.StackPList.getSelectedIndex();
	if (index < 0) return;
	update(index, true);
    }

    public static void select(int index) {
	Debugger.reverting = true;
	update(index, false);
	Debugger.reverting = false;
    }


    public static void update(int index, boolean revertp) {
	StackList current = StackList.getCurrentStackList();
	StackListElement sle2;
	StackListElement sle = (StackListElement) current.getElementAt(index);
	TraceLine tl = sle.trace;
	
	if (index == current.getSize()-1) {
	    Debugger.updateCodePanel(TimeStamp.currentTime());
	    Debugger.updateLocalsPanel(tl);
	    return;
	}
	
	sle2 = (StackListElement) current.getElementAt(index+1);
	TraceLine tl2 = sle2.trace;
	TimeStamp ts = tl2.lookupTS();

	if (revertp) {
	    Debugger.revert(ts);
	}
	else {
	    Debugger.updateCodePanel(ts);
	    Debugger.updateLocalsPanel(tl);
	}
    }

    public void actionPerformed(ActionEvent event) {
    }
}


