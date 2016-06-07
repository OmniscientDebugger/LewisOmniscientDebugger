/*                        HistoryListSingleton.java

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


import java.lang.reflect.Field;

public class HistoryListSingleton extends HistoryList {
  private static int			nCreated = 0;
  private static int			nUpgraded = 0;

  private int				time0;			// TimeStamp
  private Object			value0;			// Object

  public void reset(int time) {
    Object currentValue = valueOn(time, false);
    time0  = 0;
    value0 = currentValue;
  }

    public boolean compact(int eot) {
	time0 = TimeStamp.forward(time0);
	if (time0 >= 0) {
	    if (time0 > eot)
		throw new DebuggerException("HLM.compact() failed on " + this +" ["+time0+">"+eot+"] = "+value0);
	}
	else
	    time0 = 0;
	verify(eot);
	return true;
    }

    public void verify(int eot) {
	if (time0 == 0) return;
	if (time0 > eot) throw new DebuggerException("HLS.verify() failed on <HLS" + this + " >" + time0);
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

  public static int getNCreated() {
    return nCreated;
  }
  public static int getNUpgraded() {
    return nUpgraded;
  }



  public HistoryList dup(int time) {
    HistoryListSingleton hls = new HistoryListSingleton(0, valueOn(time, false));
    return hls;
  }


  public TVPair[] getValues() {
    TVPair[] values = new TVPair[1];
    values[0] = new TVPair(time0, value0);
    return(values);
  }

  public int size() {
      if (time0 >= 0)
	  return 1;
      else
	  return 0;
  }


  public TimeStamp getTS(int i) {
    return(TimeStamp.lookup(time0));
  }
  public int getTime(int i) {
    return(time0);
  }
  public Object getValue(int i) {
    return(value0);
  }

  public HistoryList upgrade(int time, Object value) {
    nUpgraded++;
    HistoryList hl = new HistoryListTripleton(this.time0, this.value0, time, value);
    return hl;
  }

  public static void printStatistics() {}


  // Constructors
  public HistoryListSingleton() {time0 = -1;}


  public HistoryListSingleton(int time, Object initialValue) {
    nEntries++;
    nCreated++;
    this.time0 = time;
    value0 = initialValue;
  }

  public HistoryList add(int time, Object o) {
      if (size() == 0) {
	  this.time0 = time;
	  this.value0 = o;
	  return null;
      }
      return upgrade(time, o);
  }
  public HistoryList add(int time, int i) {
      return add(time, ShadowInt.createShadowInt(i));
  }

  public void print() {
      System.out.println("\t" + time0 + " \t" + value0);
  }

  public String toString(int i) {
    return("<HistoryListSingleton " +  " " + 1 + ">");
  }

  public String toString() {
    return( "XXY                " + valueOn(TimeStamp.currentTime(), false));// Don't i need spaces here?
  }

  public TimeStamp getFirst() {
      if (size() == 0) return null;
    TimeStamp ts = TimeStamp.lookup(time0);
    return(ts);
  }

  public TimeStamp getLast() {
      if (size() == 0) return null;
    TimeStamp ts = TimeStamp.lookup(time0);
    return(ts);
  }

  public Object getLastValue() {
      if (size() == 0) return(null);			// Never happen?
      return(value0);
  }

  public TimeStamp getPrevious() {
      if (size() == 0) return null;
    TimeStamp ts1 = TimeStamp.currentTime();
    if (ts1 == TimeStamp.bot()) return(null);
    TimeStamp timeStamp = TimeStamp.lookup(time0);
    if (!timeStamp.laterThan(ts1)) {
      TimeStamp prev = timeStamp.getPreviousThisThread();
      if (prev == null) return(timeStamp);
      return(prev);
    }
    return(null);
  }

  public TimeStamp getNext() {
      if (size() == 0) return null;
    TimeStamp timeStamp = TimeStamp.lookup(time0);
    if (timeStamp.laterThan(TimeStamp.currentTime())) {
      return(timeStamp);
    }
    return(null);
  }


  public Object valueOn(TimeStamp ts, boolean foreign) {
      return valueOn(ts.time, foreign);
  }

    public Object valueOn(int tim, boolean foreign) {
      if (size() == 0) return(null);
      if (foreign) return(value0);	// Let caller know we don't have valid entries for ts
      if (TimeStamp.laterThan(time0, tim)) {
      return(Dashes.DASHES);
    }
    return(value0);
  }
  
  public void removeLast() {
      time0 = -1;
  }
    



  public static void main(String[] args) {
    System.out.println("----------------------HistoryList----------------------\n");

    System.out.println("----------------------HistoryList----------------------\n");
  }


}
