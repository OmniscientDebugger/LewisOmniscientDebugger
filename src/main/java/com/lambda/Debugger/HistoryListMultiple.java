/*                        HistoryListMultiple.java

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

//              HistoryListMultiple/HistoryListMultiple.java

/*
 */


import java.lang.reflect.Field;


public class HistoryListMultiple extends HistoryList {

  public static boolean		DEBUG = false;
  private int[]			times = new int[7];
  private Object[]		values = new Object[7];
  private int 			index = 0;



    public boolean compact(int eot) {		// returns "Can this be GC'd?"
	int j = 0;
	Object lastValue = null;
	if (index == 0) return true;
	lastValue = values[index-1];

	for (int i = 0; i < index; i++) {
	    int f = TimeStamp.forward(times[i]);
	    if (f >= 0) {
		if (f > eot)
		    throw new DebuggerException("HLM.compact() failed on " + this +" ["+f+">"+eot+"] = "+values[i]);
		times[j] = f;
		values[j] = values[i];
		j++;
	    }
	}
	for (int i = j; i < index; i++) values[i] = null;
	if (j == 0) {
	    index = 1;
	    times[0] = 0;
	    values[0] = lastValue;
	}
	else
	    index = j;
	verify(eot);
	return (index < 2);
    }


    public void verify(int eot) {
	
	for (int i = 0; i < index; i++) {
	    if (times[i] == 0) continue;
	    if (times[i] > eot) throw new DebuggerException("HLM.verify() failed on" + this +": ["+times[i]+">"+eot+"] "+values[i]);

	    /*
	    int type = TimeStamp.getType(times[i]);
	    if ((type == TimeStamp.LOCAL) || (type == TimeStamp.OBJECT_IV) || (type == TimeStamp.ONE_D_ARRAY)
		//		|| (type == TimeStamp.OTHER)
		|| (type == TimeStamp.FIRST)
		|| (type == TimeStamp.type == TimeStamp.TimeStamp.ABSENT)
		//|| (TimeStamp.MULTI_LAST)
		|| (type == RETURN)
		|| (type == D_ARRAY)
		|| (type == TimeStamp.CALL)
		|| (type == TimeStamp.LOCKING)
		|| (type == TimeStamp.UNLOCKING) || (type == TimeStamp.WAITING) || (type == TimeStamp.WAITED)) {
		continue;
	    }

	if (type == TimeStamp.CALL) System.out.println("Call: " + TraceLine.getMethodLine(times[i]));
	 DebuggerException ex= new DebuggerException("HLM.verify() wrong type on <HLM" + this + "> times[" + i + "]="
					+ times[i]+" "+TimeStamp.getTypeString(times[i]));
	    ex.printStackTrace();
	TimeStamp.printAll();
	    throw ex;
	    */
	}
    }


  public void reset(int time) {
    Object currentValue = valueOn(time, false);
    times[0] = 0;
    values[0] = currentValue;
    index = 1;
  }

    private int findIndexFor(TimeStamp ts) {
	int time0 = ts.time;
	for (int i = index-1; i > -1; i--) {
	    if (!(TimeStamp.laterThan(times[i], time0))) return i;
	}
	return 0;
    }


  public void setValue(Object obj, String varName, Object value) throws NoSuchFieldException, SecurityException, CompletionException {
      int ix = findIndexFor(TimeStamp.currentTime());
    if (obj instanceof int[]) {
      if (value instanceof Integer) {
	value = ShadowInt.createShadowInt(((Integer)value).intValue());
	values[ix] = value;								// CHANGE THIS CURRENT TIME
	return;
      }
      throw new CompletionException("Wrong type. " + value + " is not an int");
    }    

    if (obj instanceof Object[]) {							// Check exact type somehow
      /*
      Object[] array = (Object[]) obj;
      Object old = array[aIndex];
      try { array[aIndex] = value; }
      catch (Exception e) {
	System.out.println("Wrong type. " + value + " is not a " +obj.getClass());
	throw new CompletionException("Wrong type. " + value + " is not a " +obj.getClass());
      }
      array[aIndex] = old;
      */
      values[ix] = value;									// CHANGE THIS CURRENT TIME
      return;
    }


    Field f = obj.getClass().getField(varName);
    Class fieldClass = f.getType();
    
    if (value instanceof Integer) {
      if (fieldClass.equals(int.class)) {
	value = new ShadowInt(((Integer)value).intValue());
	values[ix] = value;								// CHANGE THIS CURRENT TIME
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
	values[ix] = value;								// CHANGE THIS CURRENT TIME
	return;
      }
      else
	throw new CompletionException("Wrong type. " + value + " is not a " + fieldClass);
    }

    if ( (value != null) && (!Subtype.subtype(value.getClass(), fieldClass)) )
      throw new CompletionException("Wrong type. " + value + " is not a " + fieldClass);
    values[ix] = value;								// CHANGE THIS CURRENT TIME
  }


  public HistoryList dup(int time) {
    HistoryListMultiple hls = new HistoryListMultiple();
    hls.times[0] = 0;
    hls.values[0] = valueOn(time, false);
    hls.index = 1;				// index is CURRENT value
    return hls;
  }

  public TVPair[] getValues() {
    TVPair[] tvPairs = new TVPair[index];
    for (int i = 0; i < index; i++) {
      tvPairs[i] = new TVPair(times[i], values[i]);
    }
    return(tvPairs);
  }


  public static void printStatistics() {
    int nExpanded=0, size = 0; //hLists.size();
  }
	
  public int size() {
    return index;
  }
  public int wasted() {
    return times.length-index;
  }

    public int getTime(int i) {
      if (size() == 0) return(0);
      return(times[i]);
  }
  public TimeStamp getTS(int i) {
      if (size() == 0) return(TimeStamp.lookup(0));
    return(TimeStamp.lookup(times[i]));
  }
  public Object getValue(int i) {
      if (size() == 0) return null;
    return(values[i]);
  }


  public HistoryListMultiple() {}

  public HistoryListMultiple(String n) {
    nEntries++;
  }

  public HistoryListMultiple(int time, Object initialValue) {
    nEntries++;
    values[0]= initialValue;
    times[0] = time;
    index++;
  }
  public HistoryListMultiple(int time, Object initialValue, int time1, Object value1, int time2,
			     Object value2, int time3, Object value3) {
    nEntries++;
    values[0]= initialValue;
    times[0] = time;
    values[1] = value1;
    times[1] = time1;
    values[2] = value2;
    times[2] = time2;
    values[3] = value3;
    times[3] = time3;
    index=4;
  }

  public HistoryList add(int time, Object o) {
    nEntries++;
    if (index == times.length) {
      int[] newTimes = new int[index*2];
      System.arraycopy(times, 0, newTimes, 0, index);
      times = newTimes;
      Object[] newValues = new Object[index*2];
      System.arraycopy(values, 0, newValues, 0, index);
      values = newValues;
    }
    times[index]=time;
    values[index] = o;	// null NOT an error. (?)
    index++;
    return null;
  }

  public HistoryList add(int time, int i) {
      return add(time, ShadowInt.createShadowInt(i));
  }

  public void print() {
    for (int i = 0; i<index; i++) {
      System.out.println("\t" + times[i] + " \t" + getValue(i));
    }
  }

  public String toString() {
    return("<HistoryListMultiple " +index  + ">");
  }
  public String toString(int i) {
    return("<HistoryListMultiple " +index  + ">");
  }

  public TimeStamp getFirst() {	
      if (size() == 0) return(null);
      return(getTS(0));
  }

  public TimeStamp getLast() {
      if (size() == 0) return(null);
      return(getTS(index-1));
  }

  public Object getLastValue() {
      if (size() == 0) return(null);
      return(getValue(index-1));
  }

    public TimeStamp getPrevious() {
	int time0 = TimeStamp.currentTime().time;
	if (time0 == TimeStamp.bott()) return(null);
	for (int i = index-1; i > -1; i--) {
	    if (!TimeStamp.laterThan(times[i], time0)) {
		TimeStamp ts2 = getTS(i);
		TimeStamp prev = ts2.getPreviousThisThread();
		if (prev == null) return(ts2);
		return(prev);
	    }
	}
	return(null);
    }

  public TimeStamp getNext() {
    int time0 = TimeStamp.currentTime().time;
    for (int i = 0; i < index; i++) {
      if (TimeStamp.laterThan(times[i], time0)) {
	return(getTS(i));
      }
    }
    return(null);
  }


    public Object valueOnBS(int time, boolean foreign) {			// Binary Search
	int lower = 0, higher = index-1;
	if (TimeStamp.laterThan(times[0], time)) {
	    if (foreign) return(getValue(0));	// We don't have valid entries for ts
	    return(Dashes.DASHES);
	}
	if (!TimeStamp.laterThan(times[higher], time)) return(getValue(higher));
	while ((higher-lower) > 1) {
	    int middle = (higher-lower)/2 + lower;
	    if (TimeStamp.laterThan(times[middle], time))
		higher = middle;
	    else
		lower = middle;
	}
	if (foreign) return(getValue(lower));	// We don't have valid entries for ts
	return(getValue(lower));
    }

  public Object valueOn(TimeStamp ts, boolean foreign) {
      return valueOn(ts.time, foreign);
  }

    public Object valueOn(int time, boolean foreign) {
    if (size() == 0) return(Dashes.DASHES);
    if (index > 20) return(valueOnBS(time, foreign));
    for (int i = 0; i<index; i++) {
      if (TimeStamp.laterThan(times[i], time)) {
	if (i == 0) {
	  if (foreign) return(getValue(0));	// Let caller know we don't have valid entries for ts
	  return(Dashes.DASHES);
	}
	if (foreign) return(getValue(i-1));	// Let caller know we don't have valid entries for ts
	return(getValue(i-1));
      }
    }
    if (foreign) return(getValue(index-1));	// Let caller know we don't have valid entries for ts
    return(getValue(index-1));
  }



  public void removeLast() {
      if (index > 0) index--;
  }






  public static void main(String[] args) {
    System.out.println("----------------------HistoryListMultiple----------------------\n");

    int a, b, x;
    int i=0;
    VectorD v = new VectorD(100);

    x = TimeStamp.addStamp("Fake.xtx:10");
    a = TimeStamp.addStamp("Fake.xtx:10");

    try {
      for (i = 0; i < 1000; i++) {
	for (int i1 = 0; i1 < 10000; i1++) {
	  HistoryListMultiple h = new HistoryListMultiple(x, "--");
	  v.add(h);
	}
	System.out.println("Created "+(i+1)+"0,000 HLs");
      }
    }
    catch (Exception e) {System.out.println(e);}
	  
    System.out.println("Created "+i+"HLs");

    //h.add(a, "<Obj 99>");

    System.out.println("----------------------HistoryListMultiple----------------------\n");
  }


}
