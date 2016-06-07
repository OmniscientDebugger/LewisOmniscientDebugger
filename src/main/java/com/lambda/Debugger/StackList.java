/*                        StackList.java

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

//              StackList/StackList.java

/*
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;


public class StackList extends AbstractListModel {
    public static StackList currentStackList = null;
    public VectorD displayList = new VectorD();
    public static StackList currentStackListAlternate = null;
  // public VectorD displayListAlternate = new VectorD();


    public static StackList getCurrentStackList() {
	return currentStackList;
    }
    public static void setCurrentStackList(StackList sl) {
	currentStackList = sl;
    }
    
    public static void clear() {
      if (currentStackList != null) currentStackList.displayList.removeAllElements();
    }

  public static void switchTimeLines(boolean clear) {
    StackList a;
    VectorD b;

    a = currentStackListAlternate;
    //b = displayListAlternate;

    currentStackListAlternate = currentStackList;
    //displayListAlternate = displayList;

    currentStackList = a;
    //displayList = b;
  }


  /*
    public Object clone() {
	StackList sl = new StackList();
	sl.displayList = (VectorD) displayList.clone();
	return sl;
    }
    */

    public int getSize() {
	return displayList.size();
    }

    public Object getElementAt(int i) {
	return displayList.elementAt(i);
    }

    public String toString() {
	return("<StackList "+ getSize() + ">");
    }

    public String toString(int room) {
	return("<StackList "+ getSize() + ">");
    }

    public void printAll() {
	D.println("=====StackList=====\n"+this);
	int len = getSize();
	for (int i = 0; i < len; i++) {
	    D.println(displayList.elementAt(i).toString());
	}
    }

    public boolean add(Object o) {	// Object because leftover from when it extended VectorD?
	TraceLine tl = (TraceLine)o;
	return(displayList.add(new StackListElement(tl)));
    }

    public void addLast(Object o) {	// Object because leftover from when it extended VectorD?
	TraceLine tl = (TraceLine)o;
	displayList.insertElementAt(new StackListElement(tl), 0);
    }
	

    public void remove() {
	if (getSize() == 0) {
	    D.println("StackList.remove()   VECTOR EMPTY BUG: code insertion error?" + this);
	    return; // Probably a code insertion bug
	}
	displayList.remove(displayList.size()-1);
    }


    public static void main(String[] args) {
	D.println("----------------------StackList----------------------\n");

    }
}

