/*                        CodePane.java

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.HashMap;

import org.apache.bcel.Repository;
import org.apache.bcel.util.ClassPath;
//import ClassPath.*;

public class CodePane {

  static HashMap table = new HashMap(); 			// [(String) fileName, (VectorD) {"line 1", "line 2"...}]


	private static VectorD getDemoList(String sourceFileName) {
		BufferedReader r;
	    if (Debugger.clazz == com.lambda.Debugger.QuickSortNonThreaded.class) {
		r = new BufferedReader(new InputStreamReader(new StringBufferInputStream(QuickSortNonThreadedString.programString)));
		return(buildFileLines(r, sourceFileName));
	    }
	    if (Debugger.clazz == com.lambda.Debugger.Rewrite.class) {
		r = new BufferedReader(new InputStreamReader(new StringBufferInputStream(RewriteString.programString)));
		return(buildFileLines(r, sourceFileName));
	    }
	    if (Debugger.clazz == com.lambda.Debugger.Demo.class) {
		r = new BufferedReader(new InputStreamReader(new StringBufferInputStream(DemoString.programString)));
		return(buildFileLines(r, sourceFileName));
	    }
	return null;
	}


    public static VectorD getDisplayList(SourceLine sl) {
	String sourceFileName = sl.getFile();
	String className = sl.getClassName();
	return getDisplayList(sourceFileName, className);
	}


	private static String getSourceFileName(ClassPath.ClassFile cf, String sourceFileName) {
	    String path = cf.getPath();
	    int dot = path.lastIndexOf("/");
	    if (dot < 0)
		return Debugger.DIRECTORY + sourceFileName;
	    else
		return path.substring(0, dot+1)+sourceFileName;	// /export/home/.../RegressionTests/Quick.java
	}

    public static String getSourceFileName(String className, String sourceFileName) {
	    String path = className.replace('.', '/');
	    int dot = path.lastIndexOf("/");
	    if (dot < 0)
		return Debugger.DIRECTORY + sourceFileName;
	    else
		return Debugger.DIRECTORY+path.substring(0, dot+1)+sourceFileName;
	}

    public static VectorD getDisplayList(String sourceFileName, String className) {
	VectorD displayList = (VectorD) table.get(sourceFileName);
	String sourceFilePath;

	if (displayList != null) return displayList;
	if (sourceFileName.equals("UnknownFile.java")) return new VectorD();

	BufferedReader r;
	  
	if (Debugger.DEMO) return getDemoList(sourceFileName);

	ClassPath.ClassFile cf = Repository.lookupClassFile(className);

	if (cf != null) sourceFilePath = getSourceFileName(cf, sourceFileName);
	else sourceFilePath = getSourceFileName(className, sourceFileName);

	r = getReader(sourceFilePath);
	if (r == null) r = getReaderFN(sourceFileName, className); 
	if (r == null) r = getReaderFN2(sourceFileName, className);
	return(buildFileLines(r, sourceFileName));
    }

    private static BufferedReader getReader(String sourceFilePath) {
	//	System.out.println("Reading in: " +sourceFilePath);
	try {
	    BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFilePath)));
	    return r;
	}
	catch (IOException e) {
	    return null;
	}
    }

	private static BufferedReader getReaderFN2(String sourceFileName, String className) {
	    File file = SourceFileFinder.create(sourceFileName, className);
	    if (file == null) return null;
	    String sourceFilePath = file.getPath();
	    return getReader(sourceFilePath);
	}

	private static BufferedReader getReaderFN(String sourceFileName, String className) {
		BufferedReader r = null;
		String packageName = "";
		String tmp = className.replace('.', '/');			// foo.bar.baz.Bar
	    int end = tmp.lastIndexOf("/");
	    if (end > 0) packageName = tmp.substring(0, end+1);	// foo/bar/baz/
		

	    int len = SourceFileFinder.sourceDirectories.size();
	    for (int i = 0; i < len; i++) {
		String dir = (String) SourceFileFinder.sourceDirectories.elementAt(i);
		String sourceFilePath = dir+packageName+sourceFileName;
		r = getReader(sourceFilePath);
		if (r != null) break;
	    }
	return r;
	}

  private static VectorD buildFileLines(BufferedReader r, String sourceFileName) {
  	if (r == null) return new VectorD(0);
    String inputLine;
    FileLine fileLine; 
    int line = 1;
    VectorD displayList = new VectorD(100);	// Good guess at # lines in a file?
    table.put(sourceFileName, displayList);
    try {
      while ((inputLine = r.readLine()) != null) {
	//D.println("Read line " + line + " "+inputLine);
	fileLine = new FileLine(sourceFileName, line, inputLine);
	displayList.add(fileLine);
	line++;
      }
      return displayList;
    }
    catch (IOException e) {
      return null;
    }
  }

  public static void open(String sourceFileName) {
    BufferedReader r;
    try {
      r = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFileName)));
    }
    catch (IOException e) {		// If Repository can't find it, look in current dir.
      //System.out.println("Reading in local file "+sourceFileName);
      return;
    }
    VectorD codeList = CodePane.buildFileLines(r, sourceFileName);
    if (Debugger.codePanelCurrentFile != sourceFileName) {
      Debugger.codeJList.setListData(codeList);
      Debugger.codePanelCurrentFile = sourceFileName;
      Debugger.codeJList.updateUI();
    }
  }

	public static void main(String[] args) {
		String sourceFileName = "Demo.java", className = "com.lambda.Debugger.Demo";
		 getDisplayList(sourceFileName, className);
		 getDisplayList("DebugTester.java", "foo.DebugTester");
		 getDisplayList("Thing.java", "foo.Thing");
		System.out.println("Done.");
	}
}

