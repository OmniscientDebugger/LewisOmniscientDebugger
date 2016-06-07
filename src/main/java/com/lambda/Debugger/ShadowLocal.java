/*                        ShadowLocal.java

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

//              /ShadowLocal.java

/*
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;



public final class ShadowLocal {
    static protected int		NCreated = 0;
    protected HistoryList		history;
    private String			varName;
    private int				varIndex;
    private Locals			locals;
    public int 				SPACES = 10;		// Format for "  start    14"

    public Object getSelectedObject(int x, FontMetrics fm) {
	if (history == null) {D.println("IMPOSSIBLE5 SIV.toString "+this.toString() +" "); return "---";}
	Object value = history.valueOn(TimeStamp.currentTime(), false);
	return(value);		// Let 'em click anywhere on the line
    }

    public void compact(int eot) {
	history.compact(eot);
    }

    public void verify(int eot) {
	history.verify(eot);
    }

    public String varName() {
	return(varName);
    }

    public Class getVarType() {
	return locals.getVarType(varIndex);
    }

    public Object value() {
	Object o = history.valueOn(TimeStamp.currentTime(), false);
	return(o);
    }

    public ShadowLocal(String varName, HistoryList hl, int varIndex, Locals locals) {
	NCreated++;
	this.varName = varName;
	this.varIndex = varIndex;
	this.locals = locals;
	history= hl;
    }

    public void print() {
	System.out.println(this);
	history.print();
    }

    public String toString() {
      if ((history == null) || (varName == null) ) return("BAD HL: " + history);
    	Object value = history.valueOn(TimeStamp.currentTime(), false);
	Object oldValue = history.valueOn(Debugger.previousTime, false);

	if ( (value == oldValue) || ((value instanceof UnknownValue) && (oldValue instanceof UnknownValue)) )
	return("  "+ varName + MethodLine.spaces(SPACES-(varName.length())) +
	       TimeStamp.trimToLength(history.valueOn(TimeStamp.currentTime(), false), 20));
	else
	return("* "+ varName + MethodLine.spaces(SPACES-(varName.toString().length())) +
	       TimeStamp.trimToLength(history.valueOn(TimeStamp.currentTime(), false), 20));
    }

    public TimeStamp getFirst() {	
	return(history.getTS(0));
    }

    public TimeStamp getLast() {	
	return(history.getTS(history.size()-1));
    }

    public TimeStamp getPrevious() {	
	return(history.getPrevious());
    }

    public TimeStamp getNext() {	
	return(history.getNext());
    }

    public void add(int time, Object value) {
	if (value == null) value = ShadowNull.NULL;
	HistoryList hlNew = history.add(time, value);
	if (hlNew != null) history = hlNew;
    }

    public static void main(String[] args) {
	D.println("----------------------ShadowLocal----------------------\n");
    }
}
