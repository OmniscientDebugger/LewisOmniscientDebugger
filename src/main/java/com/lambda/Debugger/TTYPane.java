/*                        TTYPane.java

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

//              TTYPane/TTYPane.java

/*

Using TP for container AND contained. FIX
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;


public class TTYPane  extends AbstractListModel {

  public static VectorD			displayList;			//ShadowPrintStream
  public static VectorD			displayListAlternate;			//ShadowPrintStream
  private static TTYPane 		SINGLETON = new TTYPane();	// Never gets changed.


    /*
    public static void compactAll(int divider) {
	System.out.println("TTY ");
	if (displayList == null) return;
	for (int i = 0; i < displayList.size(); i++) {
	    ShadowPrintStream sps = (ShadowPrintStream)displayList.elementAt(i);
	    TimeStamp ts = sps.time();
	    int time = TimeStamp.forward(ts.time);
	    System.out.println("TTY "+ts.time + " -> "+ time);
	    ts = TimeStamp.lookup(time);
	    sps.time() = ts;
	}
    }
    */
    public static TTYPane singleton() {
	return SINGLETON;
    }

    public static void clear() {
      ShadowPrintStream.clear();
      initialize();
    }

    public static void switchTimeLines(boolean clear) {
      VectorD a;

      a = displayList;
      displayList = displayListAlternate;
      displayListAlternate = a;
      
      ShadowPrintStream.switchTimeLines();
      if (clear || displayList == null) initialize();
    }

    public TTYPane() {}


    public int getSize() {
	return displayList.size();
    }

    public Object getElementAt(int i) {
	return displayList.elementAt(i);
    }

  public String toString() {
    return("<TTYPane>");
  }

    public static void printAll() {
	System.out.println("=====TTYPane=====");
	if (displayList == null) return;
	for (int i = 0 ; i < displayList.size(); i++) {
	    System.out.println(displayList.elementAt(i));
	}
    }

  static void initialize() {
    displayList = ShadowPrintStream.get(System.out);
  }


  public static String getCurrent() {
      TimeStamp ts = TimeStamp.currentTime();
      int size = displayList.size();
      if (size == 0) return "";
      ShadowPrintStream sps = null;
      for (int i = 0 ; i < size; i++) {
	  sps = (ShadowPrintStream)displayList.elementAt(i);
	  if (ts.laterThan(sps.time())) continue;
	  if (ts.equal(sps.time())) {return sps.getString();}
	  if (i > 0) {sps = (ShadowPrintStream)displayList.elementAt(i-1); return sps.getString();}
      }
      return sps.getString();
  }


  public static int getClosest(TimeStamp ts) {
    int size = displayList.size();
    for (int i = 0 ; i < size; i++) {
      ShadowPrintStream sps = (ShadowPrintStream)displayList.elementAt(i);
      if (ts.laterThan(sps.time())) continue;
      if (ts.equal(sps.time())) return i;
      if (i > 0) return i-1;
      return i;
    }
    return size-1;
  }    

  public static TimeStamp getFirst() {
    int size = displayList.size();
    if (size == 0) return(null);
    ShadowPrintStream sps = (ShadowPrintStream)displayList.elementAt(0);
    return(sps.timeStamp());
  }

  public static TimeStamp getLast() {
    int size = displayList.size();
    if (size == 0) return(null);
    ShadowPrintStream sps = (ShadowPrintStream)displayList.elementAt(size-1);
    return(sps.timeStamp());
  }

  public static TimeStamp getNext(TimeStamp ts) {
    int size = displayList.size();
    if (size == 0) return(null);
    for (int i = 0 ; i < size; i++) {
      ShadowPrintStream sps = (ShadowPrintStream)displayList.elementAt(i);
      if (ts.earlierThan(sps.time())) return(sps.timeStamp());
    }
    return(null);
  }

  public static TimeStamp getPrevious(TimeStamp ts) {
    int size = displayList.size();
    if (size == 0) return(null);
    for (int i = size-1; i > -1; i--) {
      ShadowPrintStream sps = (ShadowPrintStream)displayList.elementAt(i);
      if (ts.laterThan(sps.time())) return(sps.timeStamp());
  }
    return(null);
  }

	
    public static void main(String[] args) {
	System.out.println("----------------------TTYPane----------------------\n");

	TTYPane l = new TTYPane();
	l.printAll();
	System.out.println("----------------------TTYPane----------------------\n");
		     
    }

}

