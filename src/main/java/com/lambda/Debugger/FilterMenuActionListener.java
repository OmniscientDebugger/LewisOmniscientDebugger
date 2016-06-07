/*                        FilterMenuActionListener.java

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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;


class FilterMenuActionListener implements ActionListener {

    JMenuItem b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15;

    public void addButtons(JMenuItem b0, JMenuItem b1, JMenuItem b2, JMenuItem b3, JMenuItem b4, JMenuItem b5, JMenuItem b6, JMenuItem b7,
			   JMenuItem b8, JMenuItem b9, JMenuItem b10, JMenuItem b11, JMenuItem b12, JMenuItem b13, JMenuItem b14, JMenuItem b15) {
	this.b0=b0;		// "Filter out method in class"
	this.b1=b1;		// "Unfilter"
	this.b2=b2;		// "Filter out all methods in class"
	this.b3=b3;		// "Save filters to files"
	this.b4=b4;		// "Filter in method"
	this.b5=b5;		// "Filter out depth>1"
	this.b6=b6;		// "Filter out depth>2"
	this.b7=b7;		// "Filter out depth>3"
	this.b8=b8;		// "Filter out depth>4"
	this.b9=b9;		// "Filter out depth>5"
	this.b10=b10;	// "Filter out depth>6"
	this.b11=b11;	// "Filter out depth>7"
	this.b12=b12;	// "Filter out depth>8"
	this.b13=b13;	// "Filter out depth>9"
	this.b14=b14;	// "Filter out method any class"
	this.b15=b15;	// "Filter out method internals"
    }


    public void actionPerformed(ActionEvent event) {
	Object b = event.getSource();

	if (b == b0)  {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filterOutMethodClass");
	    dc.execute();
	    return;
	}
	if (b == b1) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "unfilter");
	    dc.execute();
	    return;
	}
	if (b == b2) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filterOutClass");
	    dc.execute();
	    return;
	}
	if (b == b3)  {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "saveFilters");
	    dc.execute();
	    return;
	}
	if (b == b4) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filterIn");
	    dc.execute();
	    return;
	}
	if (b == b5) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter1");
	    dc.execute();
	    return;
	}
	if (b == b6) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter2");
	    dc.execute();
	    return;
	}
	if (b == b7) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter3");
	    dc.execute();
	    return;
	}
	if (b == b8) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter4");
	    dc.execute();
	    return;
	}
	if (b == b9) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter5");
	    dc.execute();
	    return;
	}
	if (b == b10) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter6");
	    dc.execute();
	    return;
	}
	if (b == b11) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter7");
	    dc.execute();
	    return;
	}
	if (b == b12) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter8");
	    dc.execute();
	    return;
	}
	if (b == b13) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filter9");
	    dc.execute();
	    return;
	}
	if (b == b14) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filterOutMethod");
	    dc.execute();
	    return;
	}
	if (b == b15) {
	    DebuggerCommand dc = new DebuggerCommand(this.getClass(), "filterOutMethodInternals");
	    dc.execute();
	    return;
	}

    }



    public static void filter1() {
	TraceLine.filterToDepth(1);
	Debugger.TracePList.updateUI();
    }
    public static void filter2() {
	TraceLine.filterToDepth(2);
	Debugger.TracePList.updateUI();
    }
    public static void filter3() {
	TraceLine.filterToDepth(3);
	Debugger.TracePList.updateUI();
    }
    public static void filter4() {
	TraceLine.filterToDepth(4);
	Debugger.TracePList.updateUI();
    }
    public static void filter5() {
	TraceLine.filterToDepth(5);
	Debugger.TracePList.updateUI();
    }
    public static void filter6() {
	TraceLine.filterToDepth(6);
	Debugger.TracePList.updateUI();
    }
    public static void filter7() {
	TraceLine.filterToDepth(7);
	Debugger.TracePList.updateUI();
    }
    public static void filter8() {
	TraceLine.filterToDepth(8);
	Debugger.TracePList.updateUI();
    }
    public static void filter9() {
	TraceLine.filterToDepth(9);
	Debugger.TracePList.updateUI();
    }
    public static void filterOutMethod(){			// "Filter out method any Class"
	TraceLine tl;
	MethodLine ml = (MethodLine) Debugger.TracePList.getSelectedValue();
	if (ml == null) {Debugger.message("No Traceline selected.", true); return;}
	if (ml instanceof TraceLine)
	    tl = (TraceLine)ml;
	else
	    if (ml instanceof ReturnLine)
		tl = ((ReturnLine)ml).caller;
	    else {
		Debugger.message("Cannot filter out Throw/Catch.", true);
		return;
	    }

	ClassObjectFilter.put(tl.method);		// Implicit "*" -- All Classes
	TraceLine.refilter();
	Debugger.TracePList.updateUI();
	Debugger.revert();
	Debugger.message("Filtering out: "+tl.method, false);
    }


    public static void filterOutMethodInternals(){
	TraceLine tl;
	MethodLine ml = (MethodLine) Debugger.TracePList.getSelectedValue();
	if (ml == null) {Debugger.message("No Traceline selected.", true); return;}
	if (ml instanceof TraceLine)
	    tl = (TraceLine)ml;
	else
	    if (ml instanceof ReturnLine)
		tl = ((ReturnLine)ml).caller;
	    else {
		Debugger.message("Cannot filter out Throw/Catch.", true);
		return;
	    }

	ClassObjectFilter.put(tl.method, "Internals");
	TraceLine.refilter();
	Debugger.TracePList.updateUI();
	Debugger.revert();
	Debugger.message("Filtering out: "+tl.method, false);
    }

    public static void filterOutMethodClass(){			// "Filter out method in Class"
	TraceLine tl;
	MethodLine ml = (MethodLine) Debugger.TracePList.getSelectedValue();
	if (ml == null) {Debugger.message("No Traceline selected.", true); return;}
	if (ml instanceof TraceLine)
	    tl = (TraceLine)ml;
	else
	    if (ml instanceof ReturnLine)
		tl = ((ReturnLine)ml).caller;
	    else {
		Debugger.message("Cannot filter out Throw/Catch.", true);
		return;
	    }

	Object thisObj = tl.thisObj;    
	Class clazz = thisObj.getClass();
	if (thisObj instanceof Class) clazz = (Class)thisObj;
	ClassObjectFilter.put(clazz, tl.method);
	TraceLine.refilter();
	Debugger.TracePList.updateUI();
	Debugger.revert();
	Debugger.message("Filtering out: "+tl.method, false);
    }
    public static void filterOutClass() {			// "Filter out class"
	TraceLine tl;
	MethodLine ml = (MethodLine) Debugger.TracePList.getSelectedValue();
	if (ml == null) {Debugger.message("No Traceline selected.", true); return;}
	if (ml instanceof TraceLine)
	    tl = (TraceLine)ml;
	else
	    if (ml instanceof ReturnLine)
		tl = ((ReturnLine)ml).caller;
	    else {
		Debugger.message("Cannot filter out Throw/Catch.", true);
		return;
	    }

	Object thisObj = tl.thisObj;    
	Class clazz = thisObj.getClass();
	if (thisObj instanceof Class) clazz = (Class)thisObj;
	ClassObjectFilter.put(clazz, "*");
	TraceLine.refilter();
	Debugger.TracePList.updateUI();
	Debugger.revert();
	Debugger.message("Filtering out: "+ clazz, false);
    }

    public static void filterIn()  {			// "Filter In Method"
	TraceLine tl;
	MethodLine ml = (MethodLine) Debugger.TracePList.getSelectedValue();
	if (ml == null) {Debugger.message("No Traceline selected.", true); return;}
	if (ml instanceof TraceLine)
	    tl = (TraceLine)ml;
	else
	    if (ml instanceof ReturnLine) {
		tl = ((ReturnLine)ml).caller;
		if (tl == null) {Debugger.message("Unmatched ReturnLine"+ml, true); return;}
	    }
	    else {
		Debugger.message("Cannot filter out Throw/Catch.", true);
		return;
	    }

	ClassObjectFilter.put("First", tl);
	if ( (tl.returnLine != null) && (tl.returnLine.unfilteredIndex > -1) )
	    ClassObjectFilter.put("Last", tl.returnLine); // If returnLine not shown, no "Last" for filter
	else
	    ClassObjectFilter.clear("Last"); // If returnLine not shown, no "Last" for filter
	TraceLine.refilter();
	Debugger.TracePList.updateUI();
	Debugger.revert();
	Debugger.message("Filtering in: "+ tl + " -> " + tl.returnLine, false);
    }


    public static void unfilter() {	
	TraceLine.unfilter();	// Restore all methods
	Debugger.message("Unfiltering all", false);
	Debugger.TracePList.updateUI();
	Debugger.revert();
    }
}
