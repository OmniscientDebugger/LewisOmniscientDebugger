/*                        HistoryListTripleton.java

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

//              HistoryList/HistoryList.java

/*
 */


import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class HistoryListTripleton extends HistoryList {
  private static int			nCreated = 0;
  private static int			nUpgraded = 0;

  private int				time0=-1, time1=-1, time2=-1;		// TimeStamp
  private Object			value0, value1, value2;			// Object



  public void reset(int time) {
    Object currentValue = valueOn(time, false);
    time0  = 0;
    value0 = currentValue;
    time1 = -1;
  }

    public boolean compact(int eot) {
	if (Debugger.BUG) return compactB(eot);
	int f;
	if (time2 > -1) {
	    f = TimeStamp.forward(time2);
	    if (f >= 0) {
		time2 = f;
		if (time2 > eot)
		    throw new DebuggerException("HLM.compact() failed on " + this +" ["+time2+">"+eot+"] = "+value2);
	    }
	    else {
		time0 = 0; 
		time1 = time2 = -1;
		value0 = value2;
		value1 = value2 = null;
		verify(eot);
		return true;
	    }
	}
	if (time1 > -1) {
	    f = TimeStamp.forward(time1);
	    if (f >= 0) {
		time1 = f;
		if (time1 > eot)
		    throw new DebuggerException("HLM.compact() failed on " + this +" ["+time1+">"+eot+"] = "+value1);
	    }
	    else {
		if (time2 > -1) { time0 = time2; value0 = value2; }
		else { time0 = 0; value0 = value1;}
		time1 = time2 = -1;
		value1 = value2 = null;
		verify(eot);
		return true;
	    }
	}
	if (time0 > -1) {
	    f = TimeStamp.forward(time0);
	    if (f >= 0) {
		time0 = f;
		if (time0 > eot)
		    throw new DebuggerException("HLM.compact() failed on " + this +" ["+time0+">"+eot+"] = "+value0);
	    }
	    else {
		if (time1 > -1) { time0 = time1; value0 = value1; time1 = time2; value1 = value2; }
		else { time0 = 0; }
		time2 = -1;
		value2 = null;
		verify(eot);
		return (time1 == -1);
	    }
	}
	verify(eot);
	return (time1 == -1); // Is there more than 1 valid value?
    }
	    
    public boolean compactB(int eot) {
	int f;
	    f = TimeStamp.forward(time2);
	    if (f >= 0) {
		time2 = f;
		if (time2 > eot)
		    throw new DebuggerException("HLM.compact() failed on " + this +" ["+time2+">"+eot+"] = "+value2);
	    }
	    else {
		time0 = 0; 
		time1 = time2 = -1;
		value0 = value2;
		value1 = value2 = null;
		verify(eot);
		return true;
	    }

	if (time1 > -1) {
	    f = TimeStamp.forward(time1);
	    if (f >= 0) {
		time1 = f;
		if (time1 > eot)
		    throw new DebuggerException("HLM.compact() failed on " + this +" ["+time1+">"+eot+"] = "+value1);
	    }
	    else {
		if (time2 > -1) { time0 = time2; value0 = value2; }
		else { time0 = 0; value0 = value1;}
		time1 = time2 = -1;
		value1 = value2 = null;
		verify(eot);
		return true;
	    }
	}
	if (time0 > -1) {
	    f = TimeStamp.forward(time0);
	    if (f >= 0) {
		time0 = f;
		if (time0 > eot)
		    throw new DebuggerException("HLM.compact() failed on " + this +" ["+time0+">"+eot+"] = "+value0);
	    }
	    else {
		if (time1 > -1) { time0 = time1; value0 = value1; time1 = time2; value1 = value2; }
		else { time0 = 0; }
		time2 = -1;
		value2 = null;
		verify(eot);
		return (time1 == -1);
	    }
	}
	verify(eot);
	return (time1 == -1); // Is there more than 1 valid value?
    }
	    

    public void verify(int eot) {
	int time=0;
	for (int i = 0; i < size(); i++) {
	    if (i == 0) time = time0;
	    if (i == 1) time = time1;
	    if (i == 2) time = time2;
	    if (time == 0) continue;
	    if (time > eot) 
		throw new DebuggerException("HLM.compact() failed on " + this +" ["+time+">"+eot+"] = ");
	}
    }



  public void setValue(Object obj, String varName, Object value) throws NoSuchFieldException, SecurityException, CompletionException {
    if (obj instanceof int[]) {
      if (value instanceof Integer) {
	value = ShadowInt.createShadowInt(((Integer)value).intValue());
	this.value0 = value;								// CHANGE THIS CURRENT TIME
	return;
      }
      throw new CompletionException("Wrong type. " + value + " is not an int");
    }    

    if (obj instanceof Object[]) {							// Check exact type somehow
      this.value0 = value;									// CHANGE THIS CURRENT TIME
      return;
    }


    Field f = obj.getClass().getField(varName);
    Class fieldClass = f.getType();
    
    if (value instanceof Integer) {
      if (fieldClass.equals(int.class)) {
	value = new ShadowInt(((Integer)value).intValue());
	this.value0 = value;								// CHANGE THIS CURRENT TIME
	return;
      }
      else
	throw new CompletionException("Wrong type. " + value + " is not a " + fieldClass);
    }

    if (value instanceof Boolean) {
      if (fieldClass.equals(boolean.class)) {
	if (((Boolean)value).booleanValue())
	  value = ShadowBoolean.TRUE;
	else
	  value = ShadowBoolean.FALSE;
	this.value0 = value;								// CHANGE THIS CURRENT TIME
	return;
      }
      else
	throw new CompletionException("Wrong type. " + value + " is not a " + fieldClass);
    }

    if ( (value != null) && (!Subtype.subtype(value.getClass(), fieldClass)) )
      throw new CompletionException("Wrong type. " + value + " is not a " + fieldClass);
    this.value0 = value;								// CHANGE THIS CURRENT TIME
  }

  /*
  public void setValue(Object o, Class c) throws NoSuchFieldException, SecurityException, CompletionException {
    if (Subtype.subtype(c, int[].class)) {
      if (o instanceof Integer) {
	o = ShadowInt.createShadowInt(((Integer)o).intValue());
	value = o;								// CHANGE THIS CURRENT TIME
	return;
      }
      throw new CompletionException("Wrong type. " + o + " is not an int");
    }    

    if (Subtype.subtype(c, Object[].class)) {							// Check exact type somehow
      value = o;									// CHANGE THIS CURRENT TIME
      return;
    }


    Field f = c.getField(varName);
    Class fieldClass = f.getType();
    
    if (o instanceof Integer) {
      if (fieldClass.equals(int.class)) {
	o = new ShadowInt(((Integer)o).intValue());
	value = o;								// CHANGE THIS CURRENT TIME
	return;
      }
      else
	throw new CompletionException("Wrong type. " + o + " is not a " + fieldClass);
    }

    if ( (o != null) && (!Subtype.subtype(o.getClass(), fieldClass)) )
      throw new CompletionException("Wrong type. " + o + " is not a " + fieldClass);
    value = o;								// CHANGE THIS CURRENT TIME
  }
  */

  public static int getNCreated() {
    return nCreated;
  }
  public static int getNUpgraded() {
    return nUpgraded;
  }



  public HistoryList dup(int time) {
      HistoryListTripleton hls = new HistoryListTripleton(0, valueOn(time, false));
    return hls;
  }


  public TVPair[] getValues() {
      int size = size();
    TVPair[] values = new TVPair[size];
    if (size > 0) values[0] = new TVPair(time0, value0);
    if (size > 1) values[1] = new TVPair(time1, value1);
    if (size > 2) values[2] = new TVPair(time2, value2);
    return(values);
  }

  public int size() {
      if (time0 == -1) return 0;
      if (time1 == -1) return 1;
      if (time2 == -1) return 2;
      return 3;
  }


  public int getTime(int i) {
      if (i == 0) return(time0);
      if (i == 1) return(time1);
      if (i == 2) return(time2);
      throw new DebuggerException("Cannot get(i>2) HistoryListTripleton " + this);
  }
  public TimeStamp getTS(int i) {
      if (i == 0) return(TimeStamp.lookup(time0));
      if (i == 1) return(TimeStamp.lookup(time1));
      if (i == 2) return(TimeStamp.lookup(time2));
      throw new DebuggerException("Cannot get(i>2) HistoryListTripleton " + this);
  }
  public Object getValue(int i) {
      if (i == 0) return((value0));
      if (i == 1) return((value1));
      if (i == 2) return((value2));
      throw new DebuggerException("Cannot get(i>2) HistoryListTripleton " + this);
  }

  public HistoryList upgrade(int time, Object value) {
    nUpgraded++;
    HistoryList hl = new HistoryListMultiple(time0, value0, time1, value1, time2, value2, time, value);
    return hl;
  }

  public static void printStatistics() {}


  // Constructors
  public HistoryListTripleton() {}			      

  public HistoryListTripleton(int time, Object initialValue) {
    nEntries++;
    nCreated++;
    this.time0 = time;
    value0 = initialValue;
  }

  public HistoryListTripleton(int time, Object initialValue, int time1, Object value1) {
    nEntries++;
    nCreated++;
    this.time0 = time;
    value0 = initialValue;
    this.time1 = time1;
    this.value1 = value1;
  }

  public HistoryList add(int time, Object o) {
      int size = size();
      if (size == 0) {time0=time; value0 = o; return null;}
      if (size == 1) {time1=time; value1 = o; return null;}
      if (size == 2) {time2=time; value2 = o; return null;}
      return upgrade(time, o);
  }
  public HistoryList add(int time, int i) {
      return add(time, ShadowInt.createShadowInt(i));
  }
  public void print() {
    for (int i = 0; i<3; i++) {
      System.out.println("\t" + getTime(i) + " \t" + getValue(i));
    }
  }

  public String toString(int i) {
    return("<HistoryListTripleton "  + " " + 1 + ">");
  }

  public String toString() {
    return( "XYY                " + valueOn(TimeStamp.currentTime(), false));// Don't i need spaces here?
  }

  public TimeStamp getFirst() {
      if (size() == 0) return null;
    TimeStamp ts = TimeStamp.lookup(time0);
    return(ts);
  }

  public TimeStamp getLast() {
      int size = size();
      if (size == 0) return null;
    TimeStamp ts;
    if (size == 1) return TimeStamp.lookup(time0);
    if (size == 2) return TimeStamp.lookup(time1);
    if (size == 3) return TimeStamp.lookup(time2);
    return(null);
  }
  public int getLastTime() {
      int size = size();
    if (size == 1) return time0;
    if (size == 2) return time1;
    if (size == 3) return time2;
    return 0;
  }

  public Object getLastValue() {
      int size = size();
      if (size == 0) return(null);
      if (size == 1) return(value0);
      if (size == 2) return(value1);
      if (size == 3) return(value2);
      throw new DebuggerException("Impossible1 HistoryListTripleton " + this);
  }

    public TimeStamp getPrevious() {
	int size = size();
	int time00 = TimeStamp.currentTime().time;
	if (time00 == TimeStamp.bott()) return(null);

	if (size > 2) {
	    if (!TimeStamp.laterThan(time2, time00)) {
		TimeStamp ts2 = getTS(2);
		TimeStamp prev = ts2.getPreviousThisThread();
		if (prev == null) return(ts2);
		return(prev);
	    }
	}
    
	if (size > 1) {
	    if (!TimeStamp.laterThan(time1, time00)) {
		TimeStamp ts2 = getTS(1);
		TimeStamp prev = ts2.getPreviousThisThread();
		if (prev == null) return(ts2);
		return(prev);
	    }
	}
	if (size > 0) {
	    if (!TimeStamp.laterThan(time0, time00)) {
		TimeStamp ts2 = getTS(0);
		TimeStamp prev = ts2.getPreviousThisThread();
		if (prev == null) return(ts2);
		return(prev);
	    }
	}
	return(null);
    }

    public TimeStamp getNext() {
	int size = size();
	int time00 = TimeStamp.currentTime().time;

	if (size > 0) {
	    if (TimeStamp.laterThan(time0, time00)) {
		return(getTS(0));
	    }
	}
	if (size > 1) {
	    if (TimeStamp.laterThan(time1, time00)) {
		return(getTS(1));
	    }
	}
	if (size > 2) {
	    if (TimeStamp.laterThan(time2, time00)) {
		return(getTS(2));
	    }
	}
	return(null);
    }


  public Object valueOn(TimeStamp ts, boolean foreign) {
      return valueOn(ts.time, foreign);
  }

    public Object valueOn(int time, boolean foreign) {
	int size = size();
      if (size == 0) return(null);
	if (TimeStamp.laterThan(time0, time)) {
	    if (foreign) return(getValue(0));	// Let caller know we don't have valid entries for ts
	    return(Dashes.DASHES);
	}

	if (size > 1) {
	    if (TimeStamp.laterThan(time1, time)) {
		if (foreign) return(getValue(0));	// Let caller know we don't have valid entries for ts
		return(getValue(0));
	    }
	}
	else {
	    if (foreign) return(getValue(0));	// Let caller know we don't have valid entries for ts
	    return(getValue(0));
	}

	if (size > 2) {
	    if (TimeStamp.laterThan(time2, time)) {
		if (foreign) return(getValue(1));	// Let caller know we don't have valid entries for ts
		return(getValue(1));
	    }
	}
	else {
	    if (foreign) return(getValue(1));	// Let caller know we don't have valid entries for ts
	    return(getValue(1));
	    }
	if (foreign) return(getValue(2));	// Let caller know we don't have valid entries for ts
	return(getValue(2));
    }
  
  public void removeLast() {
      int size = size();
      if (size == 1) {time0 = -1; return;}
      if (size == 2) {time1 = -1; return;}
      if (size == 3) {time2 = -1; return;}
  }
    



  public static void main(String[] args) {
    System.out.println("----------------------HistoryList----------------------\n");

    System.out.println("----------------------HistoryList----------------------\n");
  }


}
