/*                        ShadowPrintStream.java

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

//              TraceLine/TraceLine.java

/*
 */


import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class ShadowPrintStream {

  
  public static HashMap		streams = new HashMap();	// {{<PrintStream_123> <VectorD_34 (SPS1, SPS2...)>} ...}
  public static HashMap		streamsAlternate = new HashMap();	// {{<PrintStream_123> <VectorD_34 (SPS1, SPS2...)>} ...}
  private String		outputLine;
    private int 		time;


    public String getString() {return outputLine;}

    public int time() {
	return time;
    }


    public TimeStamp timeStamp() {
	return TimeStamp.lookup(time);
    }

    public static void compactAll(int eot) {
	/* For the day where more than 1 stream is recorded.

	  Enumeration e = streams.keys();
	  while (e.hasMoreElements()) {
	  Object key = e.nextElement();
	*/
	Object o = streams.get(System.out);
	VectorD v = (VectorD) o;
	if (v == null) return;
	for (int i = 0; i < v.size(); i++) {
	    ShadowPrintStream sps = (ShadowPrintStream)v.elementAt(i);
	    int f = TimeStamp.forward(sps.time);
	    if (f > 0)
		sps.time = f;
	    else
		sps.time = 0;
	}

    }

  public static void add(PrintStream ps, String s) {
      if (ps == System.err) ps= System.out;
      if (s.length() > 100) s = s.substring(0, 100);
    VectorD v = (VectorD)streams.get(ps);
    ShadowPrintStream sps = new ShadowPrintStream(s, TimeStamp.eott());

    if (v == null) {
      v = new VectorD(1000);
      streams.put(ps, v);
    }
    v.add(sps);
  }

  public static void clear() {
    streams = new HashMap();
  }

    public static void switchTimeLines() {
      HashMap a;

      a = streams;
      streams = streamsAlternate;
      streamsAlternate = a;
    }


  public ShadowPrintStream(String s, int time) {
    outputLine = s;
    this.time = time;
  }

  public static VectorD get(PrintStream ps) {
    VectorD v = (VectorD)streams.get(ps);

    if (v == null) {
      v = new VectorD(1000);
      streams.put(ps, v);
    }
    return v;
  }
    


  public String toString() {
      //    if (ts.laterThan(TimeStamp.currentTime()))
      if (TimeStamp.laterThanNow(time))
      return "-- "+outputLine+" --";
    else
      return "   "+outputLine;

  }

}
