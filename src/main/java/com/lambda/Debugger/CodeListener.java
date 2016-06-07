/*                        CodeListener.java

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


import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CodeListener implements ListSelectionListener {
    
  public void valueChanged(ListSelectionEvent event) {
    if (TimeStamp.empty()) {
      Debugger.message("No Time Stamps recorded. Is the target program debugified?", true);
      return;
    }

    if ( (event.getSource() == Debugger.codeJList) && (!event.getValueIsAdjusting()) &&
	 (Debugger.codeJList.getSelectedIndex() >= 0) && (!Debugger.reverting) ) {
      int line =  Debugger.codeJList.getSelectedIndex();
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "selectCodeLine", line);
      dc.execute();
      return;
    }
  }

  public static void selectCodeLine(int line) {
    TimeStamp ts;
    boolean dir = Debugger.codeDirection.getState();
    boolean out = Debugger.codeOutsideOK.getState();
    boolean thr = Debugger.codeThreadOK.getState();		// NOT IMPLEMENTED YET
    boolean any = Debugger.codeAnyDirection.getState();

    //D.println(Debugger.codeJList.getSelectedValue() + " "+ Debugger.codeJList.getSelectedIndex() );
    //FileLine fl = (FileLine)Debugger.codeJList.getSelectedValue();
    SourceLine sl = SourceLine.getSourceLineFileName(Debugger.codePanelCurrentFile + ":" + (line+1));
    //D.println("Looking for TS on "+sl);

    // Experiment with best choice
    if ((line < TimeStamp.currentTime().getSourceLine().line) && dir) {
      ts = TimeStamp.getPreviousStampOnLine(sl);// JList Line nubmers 1 more than ours 
      if (any && (ts == null)) ts = TimeStamp.getNextStampOnLine(sl);
      if (out && (ts == null)) ts = TimeStamp.getPreviousStampOnLineAnyMethod(sl);
      if (any && out && (ts == null)) ts = TimeStamp.getNextStampOnLineAnyMethod(sl);
      if (thr && (ts == null)) ts = TimeStamp.getAnyStampOnLineAnyThread(sl);
    }

    else {
      ts = TimeStamp.getNextStampOnLine(sl);
      if (any && (ts == null)) ts = TimeStamp.getNextStampOnLineAnyMethod(sl);
      if (out && (ts == null)) ts = TimeStamp.getPreviousStampOnLine(sl);
      if (any && out && (ts == null)) ts = TimeStamp.getPreviousStampOnLineAnyMethod(sl);
      if (thr && (ts == null)) ts = TimeStamp.getAnyStampOnLineAnyThread(sl);
    }
    if (ts == null) 
      Debugger.message("No time stamps on this line", true);
    else
      Debugger.revert(ts);
  }
}

