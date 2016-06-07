/*                        ShadowInstanceVariable.java

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

//             ShadowInstanceVariable .java

/*
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;



public class ShadowInstanceVariable extends Shadow { // extends Shadow and contains a Shadow ??
    static int			NCreated = 0;
    public Shadow		s;
    public int			level = 1;
    public boolean 		expanded = false, special = false;
    static int			spaceToValue = 15, charsInValue = 100;
    static 			{if (Debugger.VGA) spaceToValue = 8;}
    private HistoryList 	hl;
    private String		varName;
    public int			varIndex=-1;

    public void setHL(HistoryList hl) {this.hl = hl;}
    public HistoryList getHL() {return hl;}
    public String getVarName() {return varName;}

    public void setValue(Object value)  throws NoSuchFieldException, SecurityException, CompletionException  {
	hl.setValue(s.obj, varName, value);
    }

    public Object getPreviousValue() {
	Object oldValue = hl.valueOn(Debugger.previousTime, s.foreign);
	return(oldValue);
    }

    public Object getCurrentValue() {
	Object value = hl.valueOn(TimeStamp.currentTime(), s.foreign);
	return(value);
    }

    public String toString() {  // Used by JList for display. NOT GOOD. CHANGE
	String spaces = "  ", spacesStar = "* ";
	if (level == 2) {spaces = "    ";  spacesStar = "  * ";}
	Object value = hl.valueOn(TimeStamp.currentTime(), s.foreign);
	Object oldValue = hl.valueOn(Debugger.previousTime, s.foreign);
	boolean ukn = false;

	if (value instanceof UnknownValue) {ukn = true; value = ((UnknownValue)value).getValue();}
	if (oldValue instanceof UnknownValue) oldValue =  ((UnknownValue)oldValue).getValue();

	String pstring = null;
	if (value instanceof String)
	    pstring = "\""+value+"\"";
	else {
	    if (special) pstring = SpecialFormatters.format(value);
		if (pstring == null) pstring = TimeStamp.trimToLength(value,charsInValue);
	}

	//	if (ukn) pstring = "-- " + pstring + " --";
	if (value == oldValue)
	    return(spaces+varName+ MethodLine.spaces(spaceToValue - varName.length())+ pstring);
	return(spacesStar+varName+ MethodLine.spaces(spaceToValue - varName.length())+ pstring);
    }

    public Object getSelectedObject(int x, FontMetrics fm) {
	Object value = hl.valueOn(TimeStamp.currentTime(), s.foreign);
	return(value);		// Let 'em click anywhere on the line
    }


    public String toString(int room) {
	return("<ShadowIV  "+varName+">");  // TOO CLOSELY CONNECTED -- FIX!
    }

    public void print() {
	D.println(this.toString(0));
	int len = s.size();
	for (int i=0; i<len; i++) {
	    s.getShadowVar(i).print();
	}
    }
	

    public Shadow getCurrentShadow() {
	Object o = hl.valueOn(TimeStamp.currentTime(), s.foreign);
	return(Shadow.get(o));			// may be null
    }

    HistoryList getSleeperHL() {
	if (varName == Shadow.BLOCKEDON) return s.getBlockedHL();

	SleeperSet ss = s.getSleeperSet();
	if (ss == null) return null;		// Shouldn't happen?
	if (varName == Shadow.SLEEPERS) return ss.sleepers;
	if (varName == Shadow.OWNER) return ss.owner;
	if (varName == Shadow.WAITERS) return ss.waiters;
	throw new DebuggerException("Invalid name: " + varName);
    }

    //  public ShadowInstanceVariable() {}

    public ShadowInstanceVariable(Shadow s, HistoryList hl, String varName) {// Only used by SLEEPER etc./
	NCreated++;
	this.s = s;
	this.hl = hl;
	this.varName = varName;
    }

    public ShadowInstanceVariable(Shadow s, HistoryList hl, String varName, int l) {
	NCreated++;
	this.s = s;
	this.hl = hl;
	this.varName = varName;
	level = l;
    }

    public ShadowInstanceVariable(Shadow s,int IVIndex ) {
	this(s, IVIndex, 1);
    }

    public ShadowInstanceVariable(Shadow s,int IVIndex, int l) {
	NCreated++;
	this.s = s;
	varIndex = IVIndex;
	hl = s.getShadowVar(IVIndex);
	if (hl instanceof HistoryListHashtable) {
	    HistoryListHashtable hlh = (HistoryListHashtable) hl;
	    this.varName = hlh.getVarName();
	}
	else
	    this.varName = s.getVarName(IVIndex);
	level = l;
    }

    public TimeStamp getFirst() {	
	return(hl.getFirst());
    }

    public TimeStamp getLast() {	
	return(hl.getLast());
    }

    public TimeStamp getPrevious() {	
	return(hl.getPrevious());
    }

    public TimeStamp getNext() {	
	return(hl.getNext());
    }



}
