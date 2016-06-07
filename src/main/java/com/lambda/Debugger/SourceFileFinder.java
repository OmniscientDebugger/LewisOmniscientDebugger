/*                        SourceFileFinder.java

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

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

public class SourceFileFinder extends JFrame {
    private static String 		fileName = ".debuggerSourceDirectories";
    private static String 		className;
    private static SourceFileFinder 	loadFileWindow;
    JFileChooser 			fileChooser;
    protected static VectorD 		sourceDirectories=new VectorD();	// {"/User/bil/dir/", "/tmp/dir/",..}
    protected static boolean		dontAsk = false;



    public SourceFileFinder(String sourceFileName, String className) {
	SourceFileFinder.className = className;
	final String sourceFileName1 = sourceFileName;
	fileChooser = new JFileChooser("~/");		// OS X recognizes this as "." ??! and "." as "/" ??
	final String sfn =  sourceFileName;
	fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
		public boolean accept(File f) {
		    return f.isDirectory() || f.getName().equals(sfn);
		}

		public String getDescription() {
		    return sourceFileName1;
		}
	    });
	fileChooser.setApproveButtonText("SourceFileFinder");

	JButton dontAskButton = new JButton("No Source Available");
	dontAskButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    dontAsk = true;
		    setVisible(false);
		    loadFileWindow.dispose();
		}});
	fileChooser.add(dontAskButton, BorderLayout.EAST);

    }


    public File loadFile(File file) throws Exception {
	String fileName = file.getName();
	String directory;

	if (fileName.endsWith(".java")) {
	    directory = file.getParentFile().getAbsolutePath().replace('\\', '/')+"/";
		String packageName = "";
		String tmp = className.replace('.', '/');			// foo.bar.baz.Bar
	    int end = tmp.lastIndexOf("/");
	    if (end > 0) packageName = tmp.substring(0, end+1);	// foo/bar/baz
		int end2 = directory.length() - packageName.length();
		if (directory.endsWith(packageName)) directory = directory.substring(0, end2);

	    sourceDirectories.add(directory);
	    Defaults.writeDefaults();
	}
	else {
	    System.err.println("Not a java file: " + file);
	    return null;
	}
	setVisible(false);
	loadFileWindow.dispose();
	return file;
    }


    public static File create(String sourceFileName, String className) {
	if (dontAsk) return null;

	loadFileWindow = new SourceFileFinder(sourceFileName, className); 

	if (loadFileWindow.fileChooser.showOpenDialog(loadFileWindow) == JFileChooser.APPROVE_OPTION) {
	    try { return loadFileWindow.loadFile(loadFileWindow.fileChooser.getSelectedFile()); }
	    catch (Exception ex) {
		ex.printStackTrace();
		return null;
	    }
	}
	return null;
    }

    public static void main(String[] args) {
	create("Micro1.java", "foo.Micro1");
    }
}

