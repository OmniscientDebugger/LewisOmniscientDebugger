/*                        SourceLine.java

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

//              SourceLine/SourceLine.java

/*


SourceLine's have been hacked to produce class names instead! (See: Debugify.java).
References to "fileName" are really class names now. This is unstable & confusing,
tho minor. 

See hackSourceName()

Sept '02

 */

import java.io.*;
import java.util.*;

public final class SourceLine {

	private static HashMap table = new HashMap();
	// "com.foo.MyClass:MyCode.java:123" -> <SourceLine MyCode.java:123>
	private static VectorD sourceLines = new VectorD();
	// [i] == <SourceLine MyCode.java:123>
	public static SourceLine LINE_NUMBERS_NOT_RECORDED =
		getSourceLine("Obj:UnknownFile.java:2");

	public static SourceLine SPECIAL_HIDDEN_FILE_LINE =
		getSourceLine("Obj:UnknownFile.java:1");
	public static int nEntries = 0;
	static public String startLine, stopLine;

	final String fileName, className; // "Demo.java" "com.lambda.Debugger.Demo"
	final String fileLine;
	// "com.foo.MyClass:MyCode.java:123" (used by DCJL-codepane)
	final String sourceFileLine; // "MyCode.java:123"
	final int line; // 123
	private int index; // sourceLines[index] -> this

	public static void dump() {
		for (int i = 0; i < sourceLines.size(); i++) {
			System.out.println("" + i + "\t" + sourceLines.get(i));
		}
	}

	public final int getLine() {
		return (line);
	}
	public final String getFile() {
		return (fileName);
	}
	public final String getFileLine() {
		return (fileLine);
	}

	public final String getClassName() {
		return (className);
	}

	public int size() {
		return (table.size());
	}

	public static SourceLine getSourceLine(int i) {
		SourceLine sl = (SourceLine) sourceLines.elementAt(i);
		return sl;
	}

	final public int getIndex() {
		return index;
	}

	//    static SourceLine[] newSourceLineTable = new SourceLine[1000];
	//    static int newIndex = 0;
	static HashMap offsetTable = new HashMap(1000);

	public static int getOffset(String className) {
		Integer i = (Integer) offsetTable.get(className);
		return i.intValue();
	}

	public static int addSourceLines(String[] sourceLineArray) {
		int returnValue = sourceLines.size();
		int len = sourceLineArray.length;
		if (len == 0)
			return returnValue;

		for (int i = 0; i < len; i++) {
			SourceLine sl = getSourceLine(sourceLineArray[i]);
			if (startLine == sourceLineArray[i])
				setStartLine(sl);
			if (stopLine == sourceLineArray[i])
				setStopLine(sl);
		}
		return returnValue;
	}

	public static SourceLine getSourceLineFileName(String fileLine) { // Should be "File.java:23"
		SourceLine sl = (SourceLine) table.get(fileLine);
		if (sl == null) {
			nEntries++;
			int first = fileLine.indexOf(":");
			int last = fileLine.lastIndexOf(":");
			//      int semi = fileLine.indexOf(";");
			if ((first < 1) || (first != last)) {
				D.println("getSourceLine() " + fileLine);
				throw new DebuggerException(
					"getSourceLineFileName() " + fileLine);
			}
			//      if (semi > 0) last = semi;			// AspectJ does this: Point.java;introduction/CloneablePoint.java[1k]:25
			String fileName = fileLine.substring(0, last).intern();
			String lineS = fileLine.substring(last + 1, fileLine.length());
			int l = Integer.parseInt(lineS);
			sl = new SourceLine(null, fileName, l, fileLine, fileLine);
			// When making FileLine's which were not executed.
			table.put(fileLine, sl);
			//      System.out.println("getSourceLineFileName() error? "+fileLine);
			//      (new NullPointerException()).printStackTrace();
		}
		return (sl);
	}

	public static SourceLine getSourceLine(String fileLine) { // Should be "com.foo.MyObject:File.java:23"
		SourceLine sl = (SourceLine) table.get(fileLine);
		if (sl == null) {
			nEntries++;
			int first = fileLine.indexOf(":");
			int last = fileLine.lastIndexOf(":");
			if ((first < 1) || (first == last)) {
				D.println("getSourceLine() " + fileLine);
				throw new DebuggerException("getSourceLine() " + fileLine);
			}
			String className = fileLine.substring(0, first);
			String fileName = fileLine.substring(first + 1, last).intern();
			String lineS = fileLine.substring(last + 1, fileLine.length());
			int l = Integer.parseInt(lineS);
			String s = (fileName + ":" + lineS).intern();
			sl = (SourceLine) table.get(s);
			if (sl == null) {
				sl = new SourceLine(className, fileName, l, fileLine, s);
				table.put(s, sl); // put("file.java:12" SL)
			}
			table.put(fileLine, sl); // put("obj:file.java:12" SL)
		}
		return (sl);
	}

	SourceLine(String c, String f, int l, String fileLine, String sfl) {
		this.fileLine = fileLine;
		fileName = f.intern();
		className = c;
		line = l;
		sourceFileLine = sfl;
		index = sourceLines.size();
		sourceLines.add(this);
	}

	public String toString() {
		//	return (fileName + ":" + line);
		return "<SL:" + index + " " + fileLine + ">";
	}

	public String toString(int len) {
		if (fileName.length() < len - 4)
			return (fileName + ":" + line);
		String s = fileName.substring(0, len - 5);
		//      System.out.println(fileName + ":" + line);
		return (s + ":" + line);
	}

	public static void main(String[] args) {

		SourceLine sl =
			getSourceLine("com.lambda.Debugger.MyClass:myFile.java:175");
		System.out.println("SL: " + sl + " " + sl.getClassName());

		sl = getSourceLine("com.lambda.Debugger.MyClass:myFile.java:1");
		System.out.println("SL: " + sl + " " + sl.getClassName());

		try {
			sl = getSourceLine(":1");
			System.out.println("SL: " + sl + " " + sl.getClassName());
		} catch (DebuggerException de) {
			System.out.println(de);
		}

		try {
			sl = getSourceLine("");
			System.out.println("SL: " + sl + " " + sl.getClassName());
		} catch (DebuggerException de) {
			System.out.println("SL: " + sl + " " + sl.getClassName());
		}

		try {
			sl = getSourceLine("myFile.java:1");
			System.out.println(sl);
		} catch (DebuggerException de) {
			System.out.println(de);
		}
	}

	public static void setStopLine(String sl) {
		stopLine = sl.intern();
	}
	public static void setStartLine(String sl) {
		startLine = sl.intern();
		D.DISABLE = true;
	}
	public static void setStopLine(SourceLine sl) {
		String s = "sl = " + sl.line + " & sf = \"" + sl.fileName + "\"";
		EventInterface.setStopPatternString(s);
		Defaults.writeDefaults();
		Debugger.message("Start pattern saved to .debuggerDefaults", false);
	}
	public static void setStartLine(SourceLine sl) {
		String s = "sl = " + sl.line + " & sf = \"" + sl.fileName + "\"";
		EventInterface.setStartPatternString(s);
		Defaults.writeDefaults();
		Debugger.message("Start pattern saved to .debuggerDefaults", false);
	}
	public static void clearStartStop() {
	} //D.clearStartStop(); D.DISABLE=true; Defaults.writeDefaults();}

	public static String getString(TimeStamp ts) {
		int nChars = 26;
		SourceLine sl = ts.getSourceLine();
		String s = sl.fileName;
		if (s.length() > nChars - 6)
			s = s.substring(0, nChars - 5);
		s = (s + ":" + sl.line);
		//while (s.length() < nChars)			s = " " + s;
        s = "   " + s;
		return s;
	}

}
