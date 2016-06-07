/*                        ThisPane.java

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

//              ThisPane/ThisPane.java

/*
 */


//import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;


public class ThisPane  extends AbstractListModel {

    public static VectorD 		displayList = new VectorD(20); // Shadow & ShadowInstanceVariable only GETS CHANGED A LOT
    public static VectorD 		displayListAlternate = new VectorD(20); // Shadow & ShadowInstanceVariable only GETS CHANGED A LOT
    public static ThisPane		SINGLETON;


    public static void clear() {
	displayList.removeAllElements();
	SINGLETON=new ThisPane();
    }

    public static void switchTimeLines(boolean clear) {
      VectorD a;

      a = displayList;
      displayList = displayListAlternate;
      displayListAlternate = a;
    }



    public static ThisPane singleton() {
	if (SINGLETON == null) {
	    SINGLETON=new ThisPane();
	}
	return SINGLETON;
    }

    public int getSize() {
	return displayList.size();
    }

    public Object getElementAt(int i) {
	return (Shadow)displayList.elementAt(i);		// Shadow only
    }

    public String toString() {
	return("<ThisPane "+getSize()+">");
    }

    public static void printAll() {
	D.println("=====ThisPane=====");
	int len = displayList.size();
	for (int i = 0 ; i < len; i++) {
	    Shadow s = (Shadow)displayList.elementAt(i);
	    if (s instanceof ShadowInstanceVariable) continue;
	    s.print();
	}
    }

    public static void add(Object o) {
	Shadow s = Shadow.get(o);
	//D.println("OP add " +o+" "+s);
	if (s != null) {
	    displayList.add(0, s);
	    expand(0);
	}
    }


    public static void remove(int i) {
    }

    public static TimeStamp getFirst(int i) {
	if ((i<0)||(i>=displayList.size())) return(null);
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowInstanceVariable) {
	    ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
	    //D.println(iv + " getFirst "+ iv.getFirst());
	    return(iv.getFirst());
	}
	Shadow s = (Shadow) o;
	HistoryList hl = s.getShadowVar(0);
	TimeStamp ts = hl.getTS(0);
	return(ts);
    }

    public static TimeStamp getLast(int i) {
	if ((i<0)||(i>=displayList.size())) return(null);
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowInstanceVariable) {
	    ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
	    //D.println(iv + " getLast "+ iv.getLast());
	    return(iv.getLast());
	}
	Shadow s = (Shadow) o;
	TimeStamp ts = s.getLastAllVars();
	return(ts);
    }

    public static TimeStamp getPrevious(int i) {
	if ((i<0)||(i>=displayList.size())) return null;
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowInstanceVariable) {
	    ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
	    //D.println(iv + " getPrevious "+ iv.getPrevious());
	    return(iv.getPrevious());
	}
	Shadow s = (Shadow) o;
	if (s.size() > 0) {
	    TimeStamp ts = s.getPreviousAllVars();
	    return(ts);
	}
	return(TimeStamp.currentTime());
    }

    public static TimeStamp getNext(int i) {
	if ((i<0)||(i>=displayList.size())) return null;
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowInstanceVariable) {
	    ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
	    //D.println(iv + " getNext "+ iv.getNext());
	    return(iv.getNext());
	}
	Shadow s = (Shadow) o;
	if (s.size() > 0) {
	    TimeStamp ts = s.getNextAllVars();
	    return(ts);
	}
	return(TimeStamp.currentTime());
    }
	

    public static void expand(int i) {		// Dumb way to do this....
	if ((i<0)||(i>=displayList.size())) return;
	Shadow s = (Shadow) displayList.elementAt(i);

	int len = Math.min(s.size(), ObjectPane.MAX_VARS_DISPLAYED);
	List displayedVars = DisplayVariables.getDisplayedVariables(s);
	for (int j=0; j<len; j++) {
	    if ( (displayedVars != null) && (!displayedVars.contains(s.getVarName(j))) ) continue;
	    ShadowInstanceVariable siv2 = new ShadowInstanceVariable(s, j);
	    displayList.insertElementAt(siv2, i+1);
	}
    }


    public static void copy(int i) {
	if ((i<0)||(i>=displayList.size())) return;
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowInstanceVariable) {
	    Shadow s = ((ShadowInstanceVariable)o).getCurrentShadow();
	    if (s == null) return;
	    ObjectPane.add(s.value());
	}
	else
	    ObjectPane.add(((Shadow) o).value());	
	return;
    }
	    
	
    public static void main(String[] args) {
	D.println("----------------------ThisPane----------------------\n");

	ThisPane l = new ThisPane();
	l.printAll();
	D.println("----------------------ThisPane----------------------\n");
		     
    }

}
