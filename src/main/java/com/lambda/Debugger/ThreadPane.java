/*                        ThreadPane.java

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

//              ThreadPane/ThreadPane.java

/*

Using TP for container AND contained. FIX
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;


public class ThreadPane  extends AbstractListModel {

    public static HashMap 		toStrings = new HashMap();	// Thread, "<Thread-2>"
    public static HashMap 		deathTime = new HashMap();	// Thread, TS
    public static HashMap 		birthTime = new HashMap();	// Thread, TS
    public static VectorD 		displayList = new VectorD();	// ThreadPane

    public static HashMap 		deathTimeAlternate = new HashMap();	// Thread, TS
    public static HashMap 		birthTimeAlternate = new HashMap();	// Thread, TS
    public static VectorD 		displayListAlternate = new VectorD();	// ThreadPane

    private static ThreadPane 		SINGLETON = new ThreadPane();	// Never gets changed.

    public Thread			tid=null;
    public int				index=0;



    public static ThreadPane singleton() {
	return SINGLETON;
    }

    public static void clear() {
	//displayList.removeAllElements();
	deathTime.clear();
	birthTime.clear();
    }

  public static void switchTimeLines(boolean clear) {

    VectorD a;
    HashMap b, c;


    a = displayListAlternate;
    b = deathTimeAlternate;
    c = birthTimeAlternate;

    displayListAlternate = displayList;
    deathTimeAlternate = deathTime;
    birthTimeAlternate = birthTime;

    displayList = a;
    deathTime = b;
    birthTime = c;

    if (clear) {
      clear();
      initialize();
    }
  }



    public ThreadPane() {}

    public ThreadPane(Thread t, int i) {
	index=i;
	tid = t;
    }

    public int getSize() {
	return displayList.size();
    }

    public Object getElementAt(int i) {
      if (displayList.size() <= i) return null;
	return displayList.elementAt(i);
    }

  public static String getName(Thread thread) {
      String s = (String) toStrings.get(thread);
		return s;
	}


  public String toString() {
      String s = (String) toStrings.get(tid);
      if (s == null) {s = TimeStamp.trimToLength(tid, 15); toStrings.put(tid, s);}
    TimeStamp now = TimeStamp.currentTime();

    TimeStamp bt = (TimeStamp)birthTime.get(tid);
    if (bt == null) {
      bt = TimeStamp.getFirstThread(tid);
      if (bt == null) 
	  birthTime.put(tid, bt=TimeStamp.bot());
      else
	  birthTime.put(tid, bt);
    }
    if (bt.laterThan(now)) {
      s = "-- " + s + " -- ";
      return(s);
    }

    int l = 18-s.length();
     s = s + MethodLine.spaces(l);
    

    if (!tid.isAlive()) {
      TimeStamp dt = (TimeStamp)deathTime.get(tid);
      if (dt == null) {
	dt = TimeStamp.getLastThread(tid);
	if (dt == null) return s + "Long Dead??";		// No long has ANY time stamps.
	deathTime.put(tid, dt);
      }
      if (dt.earlierThan(now)) {
	s = s + "Dead";
	return(s);
      }
      else
	s = s; // + "    dying";
    }

    HistoryList hl = Shadow.getBlockedHL(tid);
    if (hl != null) {
	Object o = hl.valueOn(now, false);
	if ( (o != null) && (o != Dashes.DASHES) ) s = s  + TimeStamp.trimToLength(o, 30); // + "   blocked on "
    }
    
    return(s);
  }



  public Object getSelectedObject(int x, FontMetrics fm) {
    String str=TimeStamp.trimToLength(tid, 15);
    if (x < fm.stringWidth(str)) return(tid);

    int l = 18-str.length();
    str = str + MethodLine.spaces(l);
    if (x < fm.stringWidth(str)) return(null);

    TimeStamp now =  TimeStamp.currentTime();

    TimeStamp dt = (TimeStamp)deathTime.get(tid);
    if (dt != null) {
      if (dt.earlierThan(now)) return(null);
    }

    HistoryList hl = Shadow.getBlockedHL(tid);
    if (hl != null) {
	Object o = hl.valueOn(now, false);
	if ( (o != null) && (o != Dashes.DASHES) ) str += TimeStamp.trimToLength(o, 30); //
	if (x < fm.stringWidth(str)) return(o);
    }

    return(null);
  }


    public String toString(int room) {
	return("<ThreadPane "+index+" "+tid+">");
    }

    public static void printAll() {
	System.out.println("=====ThreadPane=====");
	int len =  displayList.size();
	for (int i = 0 ; i < len; i++) {
	    System.out.println(displayList.elementAt(i));
	}
	System.out.println("=====ThreadPane=====");
    }

  static void initialize() {
    clear();
    for (int i = 0; i < TimeStamp.MAX_THREADS; i++) {
      Thread t = TimeStamp.getThreadFromArray(i);
      if (t == null) break;
      ThreadPane tp = new ThreadPane(t, i);
      displayList.add(tp);
    }
  }

    public static int find(Thread t) {
      int len =  displayList.size();
	for (int i=0; i < len; i++) {
	    if (((ThreadPane)displayList.elementAt(i)).tid == t)
		return i;
	}

	// Oh My! A new thread! Now where did he come from? Might as well make him comfortable
	displayList.add(new ThreadPane(t, displayList.size()));
	Debugger.ThreadPList.updateUI();
	return (displayList.size() - 1);
    }

	
    public static void main(String[] args) {
	System.out.println("----------------------ThreadPane----------------------\n");

	ThreadPane l = new ThreadPane();
	l.printAll();
	System.out.println("----------------------ThreadPane----------------------\n");
		     
    }

}

