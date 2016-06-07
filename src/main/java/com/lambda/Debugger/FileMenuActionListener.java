/*                        FileMenuActionListener.java

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
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;


class FileMenuActionListener extends JFrame implements ActionListener {
  private JFileChooser fileChooser;
  private static FileMenuActionListener fileWindow;
  private JMenuItem b0, b1, b2, b3, b4, b5, b6, b7;
    private static Ring markRing = new Ring();

    public void addButtons(JMenuItem b0, JMenuItem b1, JMenuItem b2, JMenuItem b3, JMenuItem b4) {
	this.b0=b0;
	this.b1=b1;
	this.b1=b1;
	this.b2=b2;
	this.b3=b3;
	this.b4=b4;
    }


    public FileMenuActionListener() {}

    public void actionPerformed(ActionEvent event) {
	if (TimeStamp.empty()) {
	    Debugger.message("No Time Stamps recorded. Is the target program debugified?", true);
	    return;
	}

	if (event.getSource() == b0) {
	  //Debugger.message("Not implemented yet.", true);
	  DebuggerCommand dc = new DebuggerCommand(this.getClass(), "choose");
	  dc.execute();
	  return;
	}

	if (event.getSource() == b1) {
	    System.exit(0);
	}

	if (event.getSource() == b2) { 		//add mark
	    markRing.add(TimeStamp.currentTime());
	    Debugger.message("Added to mark ring.", false);
	}

	if (event.getSource() == b3) { 		//cycle ring
	    if (markRing.size() == 0) {
		Debugger.message("Empty ring! No marks!", true);
		return;
	    }
	    TimeStamp ts = (TimeStamp) markRing.cycle();
	    Debugger.revert(ts);
	}
	
	if (event.getSource() == b4) { 		//clear marks
	    markRing.clear();
	    Debugger.message("Cleared mark ring.", false);
	}
    }

  public static void choose() {
    create();
  }
	

    public FileMenuActionListener(boolean ignore) {		// Needs to be 2 classes.
	fileChooser = new JFileChooser(".");
	fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
		public boolean accept(File f) {			    
		    return ( f.isDirectory() || f.getName().endsWith(".java") );
		}
			
		public String getDescription() {
		    return "*.java";
		}
	    });
	fileChooser.setApproveButtonText("Open");
    }




    public static void create() {
      fileWindow = new FileMenuActionListener(true);
      if (fileWindow.fileChooser.showOpenDialog(fileWindow) == JFileChooser.APPROVE_OPTION) {
	fileWindow.dispose();
	CodePane.open(fileWindow.fileChooser.getSelectedFile().getName());
      }
      fileWindow.setVisible(true);
    }

    public static void main(String[] args) {
	create();
    }
}

