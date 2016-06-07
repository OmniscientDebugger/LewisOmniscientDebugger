/*                        MethodLine.java

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

//              Method.java

/*
 */


import java.awt.FontMetrics;


public class MethodLine {

  static String[]		spacesCache = {"", " ", "  ", "   ", "    ", "     ", "      ", "       ",
					       "        ", "         ", "          ",
					       "           ",		// 10
					       "            ", "             ", "              ", "               ",
					       "                ", "                 ", "                  ",
					       "                   ","                    ",		// 20
					       "                     ",
					       "                      ","                       ","                        ",
					       "                         ","                          ","                           ",
					       "                            ","                             ",
					       "                              " }; // 0 - 30 spaces

  static int			nEntries=0;

  public int			time;
  public int			filteredIndex=-1;			//  -1  when not displayed
  public int			unfilteredIndex = -1;	// never -1
  public TraceLine		traceLine=null;


    public int getThreadIndex() { return TimeStamp.getThreadIndex(time); }

    public Object getSelectedObject(int x, FontMetrics fm) {return null;}


    public void compact(int eot) {
	time = TimeStamp.forward(time);
    }

    public void verify(int eot) {
	throw new DebuggerException("ML.verify() failed on "+this);
    }

  // Constructor

  public static String spaces(int i) {
    if (i<1) return " ";
    return spaces0(i);
  }

  public static String spaces0(int i) {
    if (i<31) return(spacesCache[i]);
    StringBuffer s = new StringBuffer(spacesCache[30]);
    for (int j=30; j<i; j++) s.append(" ");
    return(new String(s));
  }

  public String toString(int i) {
    return "<MethodLine NEVER USED>";
  }

  public int getDepth() {
    if (traceLine == null) return 0;
    return(traceLine.getDepth()+1);
  }


  public MethodLine getPreviousMethodLine() {
    VectorD traces = TraceLine.unfilteredTraceSets[TimeStamp.getThreadIndex(getThread())];
    if (unfilteredIndex < 1) return(null);
    return ((MethodLine) traces.elementAt(unfilteredIndex-1));
  }
  public MethodLine getNextMethodLine() {
    VectorD traces = TraceLine.unfilteredTraceSets[TimeStamp.getThreadIndex(getThread())];
    if ((unfilteredIndex < 1) || (unfilteredIndex == traces.size()-1)) return(null);
    return ((MethodLine) traces.elementAt(unfilteredIndex+1));
  }

  public TraceLine getFirstCall() {
    throw new DebuggerException("You must call this only on TraceLines and ReturnLines");
  }
  public TraceLine getLastCall() {
    throw new DebuggerException("You must call this only on TraceLines and ReturnLines");

  }
  public TraceLine getPreviousCall() {
    throw new DebuggerException("You must call this only on TraceLines and ReturnLines");

  }
  public TraceLine getNextCall() {
    throw new DebuggerException("You must call this only on TraceLines and ReturnLines");

  }





  public final static Thread getThread(int time) {
      if (time == -1) return Thread.currentThread();
      return TimeStamp.getThread(time);
  }
  public Thread getThread() {
    return(getThread(time));
  }

  public final static SourceLine getSourceLine(int time) {
    return TimeStamp.getSourceLine(time);
    
  }
  public final SourceLine getSourceLine() {
    return(getSourceLine(time));
  }
  public final int getSourceLineIndex() {
    return(TimeStamp.getSourceIndex(time));
  }

  public final static String getTypeString(int time) {
    return TimeStamp.getTypeString(time);
  }

  public final static int getType(int time) {
    return TimeStamp.getType(time);
  }

  public final static String trim(Object o) {
    return TimeStamp.trim(o);
  }



  public static String trimToLength(Object o, int max) {
    return TimeStamp.trimToLength(o, max);
  }

  public boolean earlierThan(MethodLine ml) {
    return(time < ml.time);				// Should this go down to TS?
  }


  public TimeStamp lookupTS() {
    return(TimeStamp.lookup(time));
  }
}
