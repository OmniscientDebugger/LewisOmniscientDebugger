/*                        LocalsPane.java

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

//              LocalsPane/LocalsPane.java

/*
 */


import javax.swing.AbstractListModel;


public class LocalsPane extends AbstractListModel {

    public static VectorD 		displayList = new VectorD(20); // ShadowLocal only GETS REPLACED A LOT?
    public static VectorD 		displayListAlternate = new VectorD(20); // ShadowLocal only GETS REPLACED A LOT?
    public static LocalsPane		SINGLETON;


  public static void selectFrom(int i) {
	if ((i<0)||(i>=displayList.size())) {
	  Debugger.message("No local variable selected.", true);
	  return;
	}
	ShadowLocal sl = (ShadowLocal) displayList.elementAt(i);
	if (sl == null) {D.println("selectFrom bug. NULL SHADOE "+i); return;}
	HistoryList hl = sl.history;
	TVPair[] values = hl.getValues();
	ValueChooser.initialize(Debugger.mainFrame, values, "Select a Value");
	ValueChooser.showDialog(null, "");
	return;
  }
    



    public static void clear() {
	displayList.removeAllElements();
	SINGLETON=new LocalsPane();
    }


    public static void switchTimeLines() {
      VectorD a = displayList;
      displayList = displayListAlternate;
      displayListAlternate = a;
    }

    public static LocalsPane singleton() {
	if (SINGLETON == null) {
	    SINGLETON=new LocalsPane();
	}
	return SINGLETON;
    }

    public int getSize() {
	return displayList.size();
    }

    public Object getElementAt(int i) {
	return (ShadowLocal)displayList.elementAt(i);		// ShadowLocal only
    }

    public String toString() {
	return("<LocalsPane "+getSize()+">");
    }

    public static void printAll() {
	D.println("=====LocalsPane=====");
	int len = displayList.size();
	for (int i = 0 ; i < len; i++) {
	    ((ShadowLocal)displayList.elementAt(i)).print();
	}
    }

    public static void add(ShadowLocal s) {
	displayList.add(0, s);
    }


    public static void remove(int i) {
    }

    public static TimeStamp getFirst(int i) {
	if ((i<0)||(i>=displayList.size())) return(null);
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowLocal) {
	    ShadowLocal iv = (ShadowLocal) o;
	    //D.println(iv + " getFirst "+ iv.getFirst());
	    return(iv.getFirst());
	}
	D.println("getPrevious failed 1");		 // Shouldn't happen?
	return(null);						// Never happen
    }

    public static TimeStamp getLast(int i) {
	if ((i<0)||(i>=displayList.size())) return(null);
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowLocal) {
	    ShadowLocal iv = (ShadowLocal) o;
	    //D.println(iv + " getLast "+ iv.getLast());
	    return(iv.getLast());
	}
	D.println("getPrevious failed2");		 // Shouldn't happen?
	return(null);						// Never happen
    }

    public static TimeStamp getPrevious(int i) {
	if ((i<0)||(i>=displayList.size())) {D.println("getPrevious1 IS THIS EVER USED?");return null;}
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowLocal) {
	    ShadowLocal iv = (ShadowLocal) o;
	    //D.println(iv + " getPrevious "+ iv.getPrevious());
	    TimeStamp ts, prev;
	    ts = TimeStamp.currentTime();
	    TraceLine tl = ts.getPreviousBalancedTrace();
	    prev = iv.getPrevious();
	    if (prev == null || tl == null) return(null);
	    if (prev.notLaterThan(tl.time)) return(null);
	    return(prev);
	}
	D.println("getPrevious failed 3");		 // Shouldn't happen?
	return(null);						// Never happen
    }

    public static TimeStamp getNext(int i) {
	if ((i<0)||(i>=displayList.size())) return null;
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowLocal) {
	    ShadowLocal iv = (ShadowLocal) o;
	    //D.println(iv + " getNext "+ iv.getNext());
	    return(iv.getNext());
	}
	D.println("getPrevious failed 4");		 // Shouldn't happen?
	return(null);						// Never happen
    }
	

    public static void expand(int i) {
    }



    public static void copy(int i) {
	if ((i<0)||(i>=displayList.size())) return;
	Object o = displayList.elementAt(i);
	ObjectPane.add(((ShadowLocal) o).value());
	return;
    }
	    
	
    public static void main(String[] args) {
	D.println("----------------------LocalsPane----------------------\n");

	LocalsPane l = new LocalsPane();
	l.printAll();
	D.println("----------------------LocalsPane----------------------\n");
		     
    }

}
