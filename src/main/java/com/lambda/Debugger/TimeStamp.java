/*                        TimeStamp.java

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

//              TimeStamp/TimeStamp.java

/*
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;


public final class TimeStamp {
    public static final int		MAX_THREADS = 256;
    public static int			MAX_TIMESTAMPS = 400000;		// good for 80MB (set in Debugger.clinit)
    private static int[]		istamps = new int[1];			// Should never be used (?) see initialize()
    private static int[]		istampsAlternate = new int[1];
    private static Thread[]		threads = new Thread[MAX_THREADS];
    private static Thread[]		threadsAlternate = new Thread[MAX_THREADS];
    private static HashMap		lookupTable = new HashMap();	// {{time <TS >} ...}
    private static HashMap		lookupTableAlternate = new HashMap();	// {{time <TS >} ...}
    public static int			index = 0;
    public static int			nTSCreated = 0;	// Total ignoring GC
    public static int			indexAlternate = 0;

    private static boolean		initialized = false;
    private static TimeStamp		CURRENT_TIME = null, PREVIOUS_TIME=null;
    private static TimeStamp		CURRENT_TIME_ALTERNATE = null, PREVIOUS_TIME_ALTERNATE=null;
    private static boolean		NATIVE_TOSTRING = false;
    static				{ if (System.getProperty("NATIVE_TOSTRING") != null) NATIVE_TOSTRING = true;}
    private static String		types[] = {"local = value",     "throw: ",     "object.variable = value",    "Catch: ",
						   "array[..] = value",      "return: ",      "Other",      "Unparented call: ",
						   "call: ",     "locking",     "new array[..]",     "unlocking",
						   "First Line in: ",      "waiting",      "Last Line in: ",      "waited"};
    private static String		typesShort[] = {"l = v",     "Throw",     "o.v=v",    "Catch",
							"a[.]=",     "ret()",     "Other",    "**m()",
							"met()",     "lockg",     "n a[]",    "unlck",
							"First",     "waitg",     "Last ",    "waitd"};
    public static int 			LOCAL = 0x00000000, THROW = 0x10000000, OBJECT_IV = 0x20000000, CATCH = 0x30000000;
    public static int 			ONE_D_ARRAY = 0x40000000, RETURN = 0x50000000, OTHER = 0x60000000, ABSENT = 0x70000000;
    public static int 			CALL = 0x80000000, LOCKING = 0x90000000, MULTI_D_ARRAY = 0xA0000000, UNLOCKING = 0xB0000000;
    public static int 			FIRST = 0xC0000000, WAITING = 0xD0000000, LAST = 0xE0000000, WAITED = 0xF0000000;

    private static TimeStamp			DEFAULT;
    public final static int			THREAD_MASK =		0x0FF00000;
    public final static int			THREAD_MASK_SHIFTED =	0x000000FF;
    public final static int			TYPE_MASK =		0xF0000000;
    public final static int			TYPE_MASK_SHIFTED =	0x0000000F;
    public final static int			SOURCE_MASK =		0x000FFFFF;
    public final static int			TYPE_SHIFT_BITS =	28;
    public final static int			TYPE_SHIFT_THREADS =	20;
    private static int				EOT;
    

    protected int				time;
    private int				data;			// bit vector: 0-3:TYPES(8), 4-11:TID(512), 12-31:SOURCE_LINE(1m)


    public static void setMax(int max) {
	MAX_TIMESTAMPS = max;
    }

    public static void initialize() {
	index = 0;
	final Object obj = new Object();
	istampsAlternate = new int[100];		// Let Debugger.main set MAX first
	istamps = new int[MAX_TIMESTAMPS];
	DEFAULT = new TimeStamp(Thread.currentThread(), -1, SourceLine.getSourceLine("Obj:UnknownFile.java:1"), OTHER);

	new Thread( new Runnable() {
		public void run() {
		    addStamp("Obj:UnknownFile.java:1");		// There must always be at least 1 TS.
		    synchronized(obj) {				// Uh... a little too "cute"...
			initialized = true;
			obj.notify();
			while (true) {try {obj.wait();} catch (InterruptedException ie) {}}
		    }
		}
	    },
		    "Primordial").start();				// This will always be thread #1

	synchronized(obj) {
	    while (!initialized) {
		try {obj.wait();}
		catch (InterruptedException ie) {}
	    }
	}

	threadsAlternate[0] = threads[0];
	istampsAlternate[0] = istamps[0];
	indexAlternate = 1;
    }

    public static int lookupSize() {					//ONLY USED BY DEBUGGER for stats
	return(lookupTable.size());
    }

    public static int stampsSize() {					//ONLY USED BY DEBUGGER for stats
	return(istamps.length);
    }

    public static int nContextSwitches() {					//ONLY USED BY DEBUGGER for stats
	int nCS = 0, threadIndex = 0, oldIndex =0;
	for (int i = 0; i < index; i++) {
	    threadIndex = istamps[i] & THREAD_MASK;
	    if (threadIndex == oldIndex) continue;
	    oldIndex = threadIndex;
	    nCS++;
	}
	return(nCS);
    }


    public int getTime() {
	return time;
    }

    public static Thread getThread(int time) {
	//if (time == -1) {Debugger.println("TS out of range " + time + " >= "+index); return(threads[0]);}
	if ((time < 0) || (time > index)) throw new NullPointerException("TS out of range "+time + " >= "+index); //Debugger.println("TS out of range " + time + " ");
	int tid = (istamps[time] >> TYPE_SHIFT_THREADS) & THREAD_MASK_SHIFTED;
	if ((tid < 0) || (tid >= MAX_THREADS)) Debugger.println("TID out of range " + tid + " " + time);
	Thread thread = threads[tid];
	return thread;
    }

    public static Thread getThreadFromIndex(int threadIndex) {
	Thread thread = threads[threadIndex];
	return thread;
    }

    public Thread getThread() {
	return(getThread(time));
    }

    public static String printString(int time) {
	String ts = getTypeString(time);
	Thread t = getThread(time);
	SourceLine sl = getSourceLine(time);
	return "ts:"+time+"["+ts+", "+t+", "+sl+"]";
    }

    public static Thread getThreadFromArray(int index) {
	return(threads[index]);
    }

    public static SourceLine getSourceLine(int time) {
	int sIndex = istamps[time] & SOURCE_MASK;
	SourceLine sl = SourceLine.getSourceLine(sIndex);
	return sl;
    }

    public static int getSourceIndex(int time) {
	int sIndex = istamps[time] & SOURCE_MASK;
	return sIndex;
    }

    public int getSourceIndex() {
	int sIndex = istamps[time] & SOURCE_MASK;
	return sIndex;
    }

    public SourceLine getSourceLine() {
	return(getSourceLine(time));
    }

    public static String getTypeString(int time) {
	int typeIndex = (istamps[time] >> TYPE_SHIFT_BITS) &  TYPE_MASK_SHIFTED;
	String s = types[typeIndex];
	return s;
    }

    public static String getSourceLineFrom(int ts) {
	int sIndex = ts & SOURCE_MASK;
	SourceLine sl = SourceLine.getSourceLine(sIndex);
	return(sl.fileName + ":" + sl.line);
    }


    public static String getTypeStringFrom(int ts) {
	int typeIndex = (ts >> TYPE_SHIFT_BITS) &  TYPE_MASK_SHIFTED;
	String s = typesShort[typeIndex];
	return s;
    }

    public static Thread getThreadFrom(int ts) {
	int tid = (ts >> TYPE_SHIFT_THREADS) & THREAD_MASK_SHIFTED;
	if ((tid < 0) || (tid >= MAX_THREADS)) Debugger.println("TID out of range " + tid + " " + ts);
	Thread thread = threads[tid];
	return thread;
    }

    public static int getType(int time) {
	int typeIndex = (istamps[time] & TYPE_MASK);		// UNSHIFTED!
	return typeIndex;
    }

    public static void clear() {
	for (int i = 1; i < index; i++) istamps[i] = 0;		// So you don't see garbage while debugging.
	index = 1;
	for (int i = 1; i < MAX_THREADS; i++) { threads[i]=null; }
    
	CURRENT_TIME = eot();
	PREVIOUS_TIME=CURRENT_TIME;
    }


    public static int previousTSGettingLock(Thread tid) {		// Used by Locks.gotLock() ONLY to elide blocking TS
	if (getThread(index-1) != tid) return(-1);
	if (getType(index-1) != LOCKING) return(-1);
	return(index-1);						// Do elide, use this time instead of creating a new one
    }


    public static void switchTimeLines(boolean clear) {
	int[] a;
	int i;

	a = istamps;
	istamps = istampsAlternate;
	istampsAlternate = a;

	i = indexAlternate;
	indexAlternate = index;
	index = i; 

	Thread[]    t = threadsAlternate;
	threadsAlternate = threads;
	threads = t;

    
	HashMap    lt = lookupTableAlternate;
	lookupTableAlternate = lookupTable;
	lookupTable=lt;

	TimeStamp ca = CURRENT_TIME_ALTERNATE, pa = PREVIOUS_TIME_ALTERNATE;
	CURRENT_TIME_ALTERNATE = CURRENT_TIME;
	PREVIOUS_TIME_ALTERNATE = PREVIOUS_TIME;
	PREVIOUS_TIME = pa;
	CURRENT_TIME = ca;

	if (clear || CURRENT_TIME == null)      clear();
    }


    public String messageString() {
	int type = getType(time);
	String s = getTypeString(time);
	String mlString;
	MethodLine ml = getNearestTraceThisThread();
	if (ml == null)
	    mlString = "??";
	else
	    mlString = ml.toString().trim();
	if ((type == CALL) || (type == ABSENT) || (type == RETURN)) return(s+mlString);
	if ((type == FIRST) || (type == CATCH) || (type == THROW)) return(s+mlString);
	if ((type == LAST)) {
	    TimeStamp ts = getNextThisThread();
	    if (ts == null) return s;
	    ml = ts.getNearestTraceThisThread();
	    if (ml == null)
		mlString = "???";
	    else
		mlString = ml.toString().trim();	    
	    return(s + mlString);
	}
	return(s);
    }


    public static String trim(Object o) {
	return trim(o, 100);
    }

    public static String trim(Object o, int len) {
	String s;
	if (NATIVE_TOSTRING)
	    return trimNativeToString(o);
	else {
	    try {return trimDebuggerToString(o, len);}
	    catch (NoClassDefFoundError e) {return "NoClassDefFoundError object.toString()";}
	}
    }


    public static String trimNativeToString(Object o) {		// Use their toString() method
	String s;
	boolean bug = false;

	if (o == null) return "*NULL*";
	try {s = o.toString();if (s == null) s = "toString() -> null  ??!    **********************";}
	catch (Exception e) {
	    bug = true;
	    s = "<"+ o.getClass() +" BUG IN toString() >";
	}
	int i = s.indexOf('\n');
	if (i == -1) return s;
	return s.substring(0, i);
    }


    public static String trimDebuggerToString(Object o, int len) {	// Use our default 'toString' -> <ClassName_123>
	if (o == null) return "null";
	Shadow sh = Shadow.getCreateNoDash(o);
	String s = sh.printString(len);
	return s;
    }

    /*
    public static void removeStampsForThread(int time) {
	if (time < 0) return;
	int tIndex = getThreadIndex(time);
	for (int i = index-1; i >= time; i--) {
	    if (getThreadIndex(i) == tIndex) {
		int low, high = i;
		for (low = high-1; low >= time; low--) if (getThreadIndex(low) != tIndex) break;
		if (high+1 == index)
		    index = low+1;
		else {
		    updateTLs(low+1, high-low);
		    System.arraycopy(istamps, high+1, istamps, low+1, index-high-1);	// high -> t1, low+1 -> t1
		    index -= (high-low);
		}
		i -= (high-low);
		try {verifyCollection(index-1, "NOSTATUS");}
		catch (Exception e) {
		    System.out.println("BROKEN!");
		    D.CUTOFF_DEPTH=0;
		D.DISABLE=true;
		D.clearStartStop();
		D.PAUSE_PROGRAM=true;
		e.printStackTrace();
		}

	    }
	}
	
    }


    private static void updateTLs(int low, int diff) {
	for (int i = low; i < index; i++) {
	    if (getType(i) == CALL || getType(i) == ABSENT) {
		TraceLine tl = (TraceLine) TraceLine.getMethodLine(i);
		tl.time = i-diff;
	    }
	}
    }
    */


    public static String trimToLength(Object o, int max) {// DIVIDE into trimToLengthObject Double trimToLengthArglist ?
	if (max < 5) max = 5;
	if (o == null) return "null";// SHOULDNT EVER HAPPEN?? OR IS THIS OK IF VALUE IS NULL...?
	String s;
	

	if (o instanceof Thread) {
	    Shadow sh = Shadow.get(o);
	    return sh.printString();
	}

	if (o instanceof LocksList) {
	    return(o.toString());
	}

	if (o instanceof LockerPair) {
	    return(((LockerPair)o).toString(max));
	}


	if (o instanceof String) {
	    s = (String) o;
	    if (s.length() > max-4) 
		s = "\""+s.substring(0, max-4)+"..\"";
	    else
		s = "\""+s+"\"";
	    return s;
	}

	if (o == Dashes.DASHES) return "--";
	if (o instanceof ShadowPrimitive) return o.toString();

	s= trim(o, max);
	return s;
	/*
	  Class c = o.getClass();
	  if (c.isArray()) return s;
	
	  if (o instanceof Class) return trimPackageName(s);
	  if (s.length() == 0) return ""; //Let a empty list show nothing  (for fn calls:  frob() etc.)

	  if (s.startsWith("<")) {
	  if (s.length() > max) 
	  s = s.substring(0, max-3)+"...>"; // Never happen because shadow.tostringShort replaces it.(?)
	  }
	  else {// Add <> to string when using native toString
	  if (s.length() > max-2) 
	  s = "<"+s.substring(0, max-4)+"..>";
	  else
	  s = "<"+s+">";
	  }
	  return s;
	*/
    }


    static public String trimPackageName(String s) {
	if (s.startsWith("class ")) s = s.substring(6,  s.length());
	if (s.startsWith("java.") || s.startsWith("javax.")) {
	    int nextDot = s.indexOf('.', 7);
	    if (nextDot == -1) 
		s = s.substring(s.indexOf('.')+1, s.length());		// java.Foo[124nasdf] -> Foo[124nasdf]
	    else
		s = s.substring(nextDot+1, s.length());			// java.util.Foo[124nasdf] -> Foo[124nasdf]
	}									// java.util.aux.Foo[124nasdf] -> aux.Foo[124nasdf]
	return s;
    }


    public static boolean empty() {
	return(index == 0);
    }

    public static TimeStamp bot() {
	return(lookup(0));
    }

    public static TimeStamp bot1() {		// Hack to make the invalid Traceline @ index=0 less apparent.
	if (index < 2)
	    return(lookup(0));
	else
	    return(lookup(2));
    }

    public static int bott() {
	return(0);
    }

    public static TimeStamp eot() {
	if (index == 0) return DEFAULT;  // ERROR, useful for unit testing.
	return(lookup(index-1));
    }

    public static int eott() {
	if (index == 0) return 0;		// IS THIS A REAL PROBLEM? SHOWS UP IN Shadow.dup()
	return(index-1);
    }

    public boolean eotp() {
	return(time == index-1);
    }
    public boolean botp() {
	return(time == 0);
    }
  

    public static void setCurrentTime(TimeStamp ts) {
	PREVIOUS_TIME=CURRENT_TIME;
	CURRENT_TIME = ts;
	Debugger.setCurrentThread(getThread(ts.time));
    }

    public static int ct() {return CURRENT_TIME.time;}

    public static TimeStamp currentTime() {
	if (CURRENT_TIME == null) 		// WHAT TO DO IF ZERO STAMPS?
	    if (index == 0)
		return(new TimeStamp(Thread.currentThread(), -1, SourceLine.getSourceLine("Obj:UnknownFile.java:1"), OTHER));
	    else
		return(bot());
	return(CURRENT_TIME);
    }


    public static void printAll(int start, int end) {
	for (int i = start ; i < end; i++) {
	    TimeStamp ts = lookup(i);
	    if (ts == null)
		Debugger.println(i + "\t NULL?!");
	    else
		Debugger.println(i + "\t"+ts.toString(150));
	}
    }

    public static void printAll() {
	Debugger.println("=====Time Stamps=====");
	printAll(0, index);
    }



    public String toString() {
	return ("<TimeStamp " + time + " " + getSourceLine(time) + " "+getThread(time)+" "+getTypeString(time)+">");
    }


    public String toString(int room) {
	if (room < 20)
	    return ("<TimeStamp " + time + ">");
	if (room < 50)
	    return ("<TimeStamp " + time +" "+getTypeString(time)+">");
	return ("<TimeStamp " + time + " " + getSourceLine(time) + " "+getThread(time)+" "+getTypeString(time)+">");
    }




    public static int addStamp(String s) {	// ONly used by testing funs?
	return addStamp(SourceLine.getSourceLine(s), OTHER);
    }

    public static int addStamp(SourceLine sl) {	// ONly used by testing funs?
	return(addStamp(sl, OTHER));
    }


    public final static int addStamp(SourceLine sl, int type) {
	return addStamp(sl, type, Thread.currentThread());
    }

    public final static int addStamp(int slIndex, int type) {
	return addStamp(slIndex, type, Thread.currentThread());
    }

    public final static int addStamp(SourceLine sl, int type, TraceLine tl) {
	return addStamp(sl.getIndex(), type, tl);
    }

    public final static int addStamp(int slIndex, int type, TraceLine tl) {
	if (tl == null) return(addStamp(slIndex, type));
	if (tl.time < 0) return(addStamp(slIndex, type));
	int threadIndexUnshifted = istamps[tl.time] & THREAD_MASK;
	if (index == istamps.length) return(addStamp(slIndex, type, threadIndexUnshifted));

	istamps[index] = type | threadIndexUnshifted | slIndex;
	index++;
	nTSCreated++;
	return index-1;
    
	//    return(addStamp(sl, type, threadIndexUnshifted));
    }

    public final static int addStamp(SourceLine sl, int type, int threadIndexUnshifted) {
	return addStamp(sl.getIndex(), type, threadIndexUnshifted);
    }

    public final static int addStampTI(int slIndex, int type, int threadIndex) {
	return  addStamp(slIndex, type, threadIndex << TYPE_SHIFT_THREADS);
    }

    public final static int addStamp(int slIndex, int type, int threadIndexUnshifted) {
	if (index == istamps.length) {
	    if (index >= MAX_TIMESTAMPS) {
		if (Debugger.GC_OFF) {
		    if (!D.DISABLE) Debugger.println("GC off, collection halted.");
		    D.DISABLE=true;
		    return index-1;
		}
		int n = collect(50, true);
		if (n < MAX_TIMESTAMPS/4) n=collect(75, false);
		if (n < MAX_TIMESTAMPS/10) {
		    D.DISABLE=true;
		    Debugger.println("GC could not collect enough to continue. Recording turned off.");
		    if (index >= istamps.length) return index;
		}
	    }
	    else {// Never used?
		int newSize = (istamps.length * 2);
		if (newSize > MAX_TIMESTAMPS) newSize = MAX_TIMESTAMPS;
		int[] istamps2 = new int[newSize];
		System.arraycopy(istamps, 0, istamps2, 0, index);
		istamps = istamps2;
	    }
	}

	istamps[index] = type | threadIndexUnshifted | slIndex;
	index++;
	nTSCreated++;
	return index-1;
    }

    public final static int addStamp(SourceLine sl, int type, Thread thread) {
	int threadIndexUnshifted = (getThreadIndex(thread) << TYPE_SHIFT_THREADS);
	return(addStamp(sl, type, threadIndexUnshifted));
    }

    public final static int addStamp(int slIndex, int type, Thread thread) {
	int threadIndexUnshifted = (getThreadIndex(thread) << TYPE_SHIFT_THREADS);
	return(addStamp(slIndex, type, threadIndexUnshifted));
    }

    /*
      public final static int addStamp(SourceLine sl, int type, Thread thread) {
      if (index == istamps.length) {
      int[] istamps2 = new int[(istamps.length * 2)];
      System.arraycopy(istamps, 0, istamps2, 0, index);
      istamps = istamps2;
      }

      istamps[index] = type | (getThreadIndex(thread) << TYPE_SHIFT_THREADS) | sl.getIndex();
      index++;
      return index-1;
      }
    */


    // Constructor
    private TimeStamp(Thread tid, int time, SourceLine sl, int type) {
	this.time = time;
	data = type | (getThreadIndex(tid) << TYPE_SHIFT_THREADS) | sl.getIndex();
    }


    public static int getThreadIndex(Thread tid) {
	for (int i = 0; i < MAX_THREADS; i++) {
	    Thread t = threads[i];
	    if (t == tid) return(i);
	    if (t == null) {
		threads[i]=tid;
		return(i);
	    }
	}
	System.err.println("Too many threads. "+tid+" The debugger can only handle " + MAX_THREADS);
	System.exit(1);
	return(-1);
    }

    public static int getThreadIndex(int time) {
	int tid = (istamps[time] >> TYPE_SHIFT_THREADS) & THREAD_MASK_SHIFTED;
	return(tid);
    }

    public int getThreadIndex() {
	int tid = (data >> TYPE_SHIFT_THREADS) & THREAD_MASK_SHIFTED;
	return(tid);
    }

    public boolean earlierThan(TimeStamp t) {
	return (t.time > time);
    }
    public boolean laterThan(TimeStamp t) {
	return (time > t.time);
    }

    public boolean equal(int t) {
	return (t == time);
    }

    public boolean earlierThan(int t) {
	return (t > time);
    }
    public boolean laterThan(int t) {
	return (time > t);
    }
    public static boolean laterThan(int time1, int time2) {
	return (time1 > time2);
    }

    public static boolean laterThanNow(int time) {
	return (time > currentTime().time);
    }

    public boolean notEarlierThan(TimeStamp t) {
	return (!(t.time > time));
    }
    public boolean notLaterThan(TimeStamp t) {
	return (!(time > t.time));
    }
    public boolean notLaterThan(int time2) {
	return (!(time > time2));
    }

    public boolean earlierThanThisThread(TimeStamp t) {
	//	if (getThread(t.time) != getThread(this.time)) return(false);
	if ((t.data & THREAD_MASK) != (this.data & THREAD_MASK)) return(false);
	return (t.time > time);
    }
    public boolean laterThanThisThread(TimeStamp t) {
	//	if (t.tid != tid) return(false);
	//	if (getThread(t.time) != getThread(this.time)) return(false);
	if ((t.data & THREAD_MASK) != (this.data & THREAD_MASK)) return(false);
	return (time > t.time);
    }

    public boolean notEarlierThanThisThread(TimeStamp t) {
	//	if (t.tid != tid) return(false);
	//	if (getThread(t.time) != getThread(this.time)) return(false);
	if ((t.data & THREAD_MASK) != (this.data & THREAD_MASK)) return(false);
	return (!(t.time > time));
    }
    public boolean notLaterThanThisThread(TimeStamp t) {
	//	if (t.tid != tid) return(false);
	//	if (getThread(t.time) != getThread(this.time)) return(false);
	if ((t.data & THREAD_MASK) != (this.data & THREAD_MASK)) return(false);
	return (!(time > t.time));
    }





    // Simple Navigation methods


    public static TimeStamp previous() {
	return(lookup(index-1));
    }

    public static TimeStamp previous(int i) {
	return(lookup(index-i));
    }


    public TimeStamp getPrevious() {
	if (botp()) return(this);
	return(lookup(time-1));
    }

    public TimeStamp getNext() {
	if (this.eotp()) return(this);
	return(lookup(time+1));
    }

    public TimeStamp getLastThisThread() {
	int threadIndex = getThreadIndex();

	for (int i = eott(); i >= 0; i--) {
	    if (getThreadIndex(i) == threadIndex) return(lookup(i));
	}
	D.println("ERROR:getLastThisThread"+this);
	return(null);		// this is first in this thread
    }


    public static TimeStamp getLastThread(Thread tid) {
	for (int i = eott(); i >= 0; i--) {
	    if (getThread(i) == tid)
		return(lookup(i));
	}
	//    D.println("ERROR:getLastThread: "+tid);
	return(null);		// CAN be null. (eg, after a GC, and something else (what?))
    }

    public static TimeStamp getFirstThread(Thread tid) {
	int len = eott();
	for (int i = 0; i < len; i++) {
	    if (getThread(i) == tid)	return(lookup(i));
	}
	//    D.println("ERROR:getLastThread"+tid);
	return(null);		// CANNOT BE null. (Must only be called when dead)
    }


    public TimeStamp getFirstThisThread() {
	for (int i = 0; i < index; i++) {
	    if (getThread(i) == getThread(this.time))
		return(lookup(i));
	}
	D.println("ERROR:getFirstThisThread"+this);
	return(null);		// this is first in this thread
    }




    public TimeStamp getPreviousThisThreadOrAny() {		// Used only by  new Shadow(...) to set TS for --
	for (int i = this.time-1; i >= 0; i--) {
	    if ((getThread(i) == getThread(this.time)) && (getSourceLine(i) != getSourceLine(time)))		// Has to move to a different line
		return(lookup(i));
	}
	D.println("ERROR:getPreviousThisThreadOrAny"+this);
	if (botp()) return(this);
	return(lookup(time-1));		// Shouldn't be possible
    }


    public TimeStamp getPreviousSwitchThisThread() {
	//	if (botp) return(null);
	for (int i = this.time-1; i >= 0; i--) {
	    if (getThread(i) == getThread(this.time)) continue;
	    return(lookup(i+1));			// first TS in this thread after Context Switch
	}
	return(null);		// this is first in this thread
    }


    public TimeStamp getPreviousThisThread() {
	for (int i = this.time-1; i >= 0; i--) {
	    if (getThread(i) == getThread(this.time))// Has to move to a different line
		return(lookup(i));
	}
	return(null);		// this is first in this thread
    }

    public TimeStamp getPreviousLineThisThread() {	// Called by CodeActionListener to go to next line
	int sourceIndex = getSourceIndex();
	int threadIndex = getThreadIndex();

	for (int i = this.time-1; i >= 0; i--) {
	    if ((getThreadIndex(i) == threadIndex) && (getSourceIndex(i) != sourceIndex) && (getSourceIndex(i) != 0))	{	// Has to move to a different line
		return(lookup(i));
	    }
	}
	return(null);		// this is first in this thread
    }






    // Used by D.previousTrace()
    public static int getPreviousThisThreadforD() {
	Thread tid = Thread.currentThread();
	for (int i = index-1; i >= 0; i--) {
	    if (getThread(i) != tid) continue;
	    return(i);
	}
	return(-1);		// this is first in this thread
    }

    public TimeStamp getNextThisThread() {
	int threadIndex = getThreadIndex();
	int end = eott();
	if (eotp()) return(null);

	for (int i = this.time+1; i <= end; i++) {
	    if (getThreadIndex(i) == threadIndex)		// Has to move to a different line
		return(lookup(i));
	}
	return(null);		// this is first in this thread
    }

    public TimeStamp getNextSwitchThisThread() {
	if (eotp()) return(null);
	int end = eott();
	int threadIndex = getThreadIndex();

	for (int i = this.time+1; i < end; i++) {
	    if (getThreadIndex(i) == threadIndex) continue;
	    return(lookup(i-1));
	}
	return(null);		// this is first in this thread
    }

    public TimeStamp getNextLineThisThread() {
	if (eotp()) return(null);
	int end = eott();
	int threadIndex = getThreadIndex();
	int sourceIndex = getSourceIndex(time);

	for (int i = this.time+1; i <= end; i++) {
	    if ( (getThreadIndex(i) == threadIndex) && (getSourceIndex(i) != sourceIndex) && (getSourceIndex(i) != 0))	{ // Last test redundant?
		return(lookup(i));	// Has to move to a different line
	    }
	}
	return(null);		// this is first in this thread
    }

    
    public TimeStamp getLastOnLine() {
	if (eotp()) return(this);
	int sourceIndex = getSourceIndex(time);
	int end = eott();
	int i = this.time+1;
	SourceLine sl = getSourceLine(time);
	while ((i < end) && (getSourceIndex(i) == sourceIndex)) i++;		// Move the LAST TS on this source line.
	return(lookup(i-1));
    }







    // Complex Navigation methods




    //Find the TraceLine which established context for this TS. 
    public TraceLine getPreviousBalancedTrace() {
	return getPreviousBalancedTrace(time);
    }
    
    public static TraceLine getPreviousBalancedTrace(int time) {
	VectorD traces = TraceLine.unfilteredTraceSets[getThreadIndex(time)];
	int i;
	MethodLine ml=null;

	if (time <= 0) {return(null);}		// Hit TOP TRACELINE?
	if (empty()) return(null);
	if (traces == null) return(null);		// This is possible when <TimeStamp 1 UnknownFile.java:1 Thread[main,5,main] Other>
	if (traces.size() == 0) return(null);		// Never happen? (GC bugs cause this)
	int lower = 0, higher = traces.size()-1;
	MethodLine mlLower = (MethodLine)traces.elementAt(lower);
	MethodLine mlHigher = (MethodLine)traces.elementAt(higher);
	if (mlHigher.time == time) {lower=higher; mlLower=mlHigher;}
	if (mlLower.time == time) higher=lower;
	if (mlLower.time > time) return(null);
	
	while ((higher-lower) > 1) {
	    int middle = (higher-lower)/2 + lower;
	    MethodLine mlMiddle = (MethodLine)traces.elementAt(middle);
	    if (mlMiddle.time > time) 
		{higher = middle; mlHigher = mlMiddle;}
	    else
		{lower = middle; mlLower = mlMiddle;}
	}
	ml = mlLower;
	i = lower;


	if (i == -1) return(null);

	if (ml instanceof TraceLine) {
	    if (ml.time == time) return(ml.traceLine);
	    return((TraceLine)ml);
	}

	while (true) {
	    if (i == -1) return(null);
	    ml = (MethodLine)traces.elementAt(i);

	    if ( (ml instanceof CatchLine) || (ml instanceof ReturnLine) ) return(ml.traceLine);

	    if (ml instanceof ThrowLine) {
		i--;
		continue;
	    }

	    return((TraceLine) ml);
	}
    }
	
    

    // Used by TracePane to position JList. and TraceLine.filter()
    public MethodLine getNearestTraceThisThread() {	// returns nearest ML
	VectorD traces = TraceLine.filteredTraceSets[getThreadIndex()];
	int i;
	MethodLine ml;

	if (traces == null) return null;
	if (traces.size() == 0) return null;

	int lower = 0, higher = traces.size()-1;
	MethodLine mlLower = (MethodLine)traces.elementAt(lower);
	MethodLine mlHigher = (MethodLine)traces.elementAt(higher);
	if (mlLower.time > time) return(mlLower);
	
	while ((higher-lower) > 1) {
	    int middle = (higher-lower)/2 + lower;
	    MethodLine mlMiddle = (MethodLine)traces.elementAt(middle);
	    if (mlMiddle.time > time) 
		{higher = middle; mlHigher = mlMiddle;}
	    else
		{lower = middle; mlLower = mlMiddle;}
	}

	for (i = higher; i > -1; i--) {		// an ml MUST be found! 
	    ml = (MethodLine)traces.elementAt(i);
	    if ( (ml.time <= time) && (ml instanceof TraceLine) ) return(ml);
	    if ( (ml.time <= time) && (ml instanceof ReturnLine) ) return(ml);
	    if ( (ml.time <= time) && (ml instanceof CatchLine) ) return(ml);
	}




	return(null);
    }



    // Used by TracePane to position JList. and TraceLine.filter()
    public MethodLine getPreviousMethodThisThread() {	// returns nearest ML
	VectorD traces = TraceLine.unfilteredTraceSets[getThreadIndex()];
	int i;
	MethodLine ml;

	for (i = traces.size()-1; i > -1; i--) {		// an ml MUST be found! 
	    ml = (MethodLine)traces.elementAt(i);
	    if (ml.time <= time) return(ml);
	}
	return(null);
    }



    // Used by TracePane to position JList. and TraceLine.filter()
    public MethodLine getNextMethodThisThread() {	// returns nearest ML
	VectorD traces = TraceLine.unfilteredTraceSets[getThreadIndex()];
	int i, size = traces.size()-1;
	MethodLine ml;

	for (i = 0; i < size; i++) {		// an ml MUST be found! 
	    ml = (MethodLine)traces.elementAt(i);
	    if (ml.time >= time) return(ml);
	}
	return(null);
    }



    //ONLY used by CodeListener to navigate on a selection

    public static TimeStamp getPreviousStampOnLine(SourceLine sl) {
	TimeStamp now = currentTime();
	int nowTime = now.time;
	int sourceIndex = sl.getIndex();
	int threadIndex = now.getThreadIndex();

	if (now.botp()) return(null);

	MethodLine ml = now.getPreviousMethodThisThread();
	if (ml == null) return(null);		// Corrupt trace, too hard to mess with now.
	    
	for (int i = nowTime ; i>=0; i--) {
	    if (ml == null) return(null);
	    if (getThreadIndex(i) != threadIndex) continue;
	    if (getSourceIndex(i) == sourceIndex) {
		if (i == nowTime) continue;		// If clicking on the current line.
		return(lookup(i));
	    }
	    if (i > ml.time) continue;

	    if (ml instanceof ReturnLine) {
		ml = ((ReturnLine)ml).caller;
		if (ml == null) return(null);
		i = ml.time;			// This will be decremented & thus skip over this TL
		ml = ml.getPreviousMethodLine();
		continue;
	    }
	    if (ml instanceof CatchLine) {
		ml = ((CatchLine)ml).caller;
		if (ml == null) return(null);
		i = ml.time;			// This will be decremented & thus skip over this TL
		ml = ml.getPreviousMethodLine();
		continue;
	    }
	    if ( (ml instanceof ThrowLine) || (ml instanceof TraceLine) ) return(null);
	}
	return(null); // No previous stamp on this line
    }



    //used ONLY by CodeListener & iteration to navigate on a selection

    public static TimeStamp getNextStampOnLine(SourceLine sl) {	// This is the lineNumber in source code file.
	TimeStamp now = currentTime();
	int end = eott(), nowTime = now.time;
	int sourceIndex = sl.getIndex();
	int threadIndex = now.getThreadIndex();

	if (now.eotp()) return(null);

	MethodLine ml = now.getNextMethodThisThread();
	if (ml == null) return null;
	for (int i = nowTime; i<=end; i++) {//Debugger.println("getNextStampOnLine "+i+ml);
	    if (getThreadIndex(i) != threadIndex) continue;
	    if (getSourceIndex(i) == sourceIndex) {
		if (i == nowTime) continue;		// If clicking on the current line.
		return(lookup(i));
	    }
	    if (i < ml.time) continue;
	    if (ml instanceof  CatchLine) return(null);	// No more stamps on line.
	    if (ml instanceof  ThrowLine) return(null);	// No more stamps on line.
	    if (ml instanceof ReturnLine) continue;
	    if (ml instanceof TraceLine) {	// !=time (don't return the line we're on)
		TraceLine tl = (TraceLine) ml;
		ml = (MethodLine)tl.returnLine;
		if (ml == null) break;		// This takes care of unmatched TLs, eg TL 0. CHANGE.
		i = ml.time;				// TL & RL are on the same source line.
		continue;
	    }
	}
	//	D.println("ILLEGAL:getNextStampOnLine "+sl);
	return(null); // Illegal TimeStamp set: too few ReturnLines	NO LOTS OF WAYS TO GET THIS W/GC?
    }



    // Find the return from this frame. ONLY USED BY CodeActionListener
    public TimeStamp getLastThisFunction() {
	TraceLine tl = getPreviousBalancedTrace();
	if (tl == null) return(null);
	MethodLine ml = tl.returnLine;
	if (ml == null) return(null);
	TimeStamp ts1 = lookup(ml.time);
	TimeStamp ts = ts1.getPreviousThisThread();
	return ts;
    } 

    //Find TraceLine for the current fun
    public TraceLine getFirstThisFunction() {
	TraceLine tl = getPreviousBalancedTrace();
	return tl;
    }

    public TimeStamp getFirstTSThisFunction() {
	TraceLine tl = getPreviousBalancedTrace();
	if ((tl == null) || (tl.time == -1)) return(null);
	TimeStamp ts = lookup(tl.time);
	TimeStamp ts1 = ts.getNextThisThread();
	if (ts1.time == this.time) return(null);		//MUST move to a previous line. FOR BUGGY
	return ts1;
    }

	
    public static TimeStamp getAnyStampOnLineAnyThread(SourceLine sl) {	
	int end = eott();
	int sourceIndex = sl.getIndex();

	for (int i = 0; i<=end; i++) {
	    if (getSourceIndex(i) == sourceIndex) return(lookup(i));
	}
	//	D.println("No stamps on line:getAnyStampOnLineAnyThread "+sl);
	return(null);
    }

    public static TimeStamp getNextStampOnLineAnyMethod(SourceLine sl) {
	TimeStamp now = currentTime();
	int end = eott();
	int sourceIndex = sl.getIndex();
	int threadIndex = now.getThreadIndex();

	for (int i = now.time; i<=end; i++) {//Debugger.println("getNextStampOnLineAnyMethod " + i);
	    if (threadIndex != getThreadIndex(i)) continue;
	    if (getSourceIndex(i) == sourceIndex) {
		return(lookup(i));
	    }
	}
	//	D.println("No stamps on line:getNextStampOnLineAnyMethod "+sl);
	return(null); // Illegal TimeStamp set: too few ReturnLines	
    }
	

    //ONLY used by CodeListener to navigate on a selection
    public static TimeStamp getPreviousStampOnLineAnyMethod(SourceLine sl) {	// This is the lineNumber in source code file.
	TimeStamp now = currentTime();
	int sourceIndex = sl.getIndex();
	int threadIndex = now.getThreadIndex();

	for (int i = now.time-1; i >= 0; i--) {
	    if (threadIndex != getThreadIndex(i)) continue;
	    if (getSourceIndex(i) == sourceIndex) {
		return(lookup(i));
	    }
	}
	//	D.println("No stamps on line:getPreviousStampOnLineAnyMethod "+sl);
	return(null); // Illegal TimeStamp set: too few ReturnLines	
    }
	

    public TimeStamp getPreviousIteration() {		// Only used by CodeActionListener
	int i;
	TimeStamp now = currentTime();
	int threadIndex = now.getThreadIndex();
	SourceLine sl = now.getSourceLine();
	int sourceIndex = sl.getIndex();
	boolean wentOffLine = false;

	if (now.botp()) return(null);

	MethodLine ml = now.getPreviousMethodThisThread();
	if (ml == null) return(null);		// Corrupt trace, too hard to mess with now.
	    
	for ( i = now.time; i>=0; i--) {
	    if (getThreadIndex(i) != threadIndex) continue;
	    if ((getSourceIndex(i) == sourceIndex) && wentOffLine) return(lookup(i));
	    if (getSourceIndex(i) != sourceIndex) wentOffLine = true;
	    if (i > ml.time) continue;

	    if (ml instanceof ReturnLine) {
		ml = ((ReturnLine)ml).caller;
		if (ml == null) return null;
		i = ml.time;			// This will be decremented & thus skip over this TL
		ml = ml.getPreviousMethodLine();
		continue;
	    }
	    if (ml instanceof CatchLine) {
		ml = ((CatchLine)ml).caller;
		if (ml == null) return null;
		i = ml.time;			// This will be decremented & thus skip over this TL
		ml = ml.getPreviousMethodLine();
		continue;
	    }
	    if (i == now.time) {
		ml = ml.getPreviousMethodLine();
		continue;		// Starting out on a RL is OK.
	    }
	    if ( (ml instanceof ThrowLine) || (ml instanceof TraceLine) ) return(null);
	}
	return(null); // No previous stamp on this line
    }



    public TimeStamp getNextIteration() {		// Only used by CodeActionListener
	int i, end = eott();
	TimeStamp now = currentTime();
	int threadIndex = now.getThreadIndex();
	SourceLine sl = now.getSourceLine();
	int sourceIndex = sl.getIndex();
	boolean wentOffLine = false;

	MethodLine ml = now.getNextMethodThisThread();
	if (ml == null) return(null);		// Corrupt trace, too hard to mess with now.
	    
	for ( i = now.time; i < end; i++) {
	    if (getThreadIndex(i) != threadIndex) continue;
	    if ((getSourceIndex(i) == sourceIndex) && wentOffLine) return(lookup(i));
	    if (getSourceIndex(i) != sourceIndex) wentOffLine = true;
	    if (i < ml.time) continue;

	    if (ml instanceof TraceLine) {
		ml = ((TraceLine)ml).returnLine;
		i = ml.time;			// This will be decremented & thus skip over this TL
		ml = ml.getNextMethodLine();
		continue;
	    }
	    if (i == now.time) {
		ml = ml.getNextMethodLine();
		continue;		// Starting out on a RL is OK.
	    }
	    return(null);			// All other MLs are failures
	}
	return(null); // No next stamp on this line
    }
	


    // Next TS in this stack frame (if any).  Only used in CodeActionListener
    public TimeStamp getNextLineThisFunction() {
	TraceLine parent = getPreviousBalancedTrace();
	MethodLine ml = getNextMethodThisThread();
	int threadIndex = getThreadIndex();
	SourceLine sl = getSourceLine();
	int sourceIndex = sl.getIndex();

	if ((ml == null) || (parent == null)) return null;

	int len = eott();
	for (int i = time; i<=len; i++) {
	    if (i > ml.time) ml = lookup(i).getPreviousMethodThisThread();// NEXT??? IS THIS EVER CALLED?
	    if (threadIndex != getThreadIndex(i)) continue;
	    if ((sourceIndex != getSourceIndex(i)) && (getSourceIndex(i) != 0)) return(lookup(i));
	    if (getType(i) == LAST) return(null);
	    if (i == ml.time) {
		if (ml instanceof  ReturnLine) {
		    if (i == time) continue;
		    return(null);
		}
		if (ml instanceof TraceLine) {
		    TraceLine tl = (TraceLine) ml;
		    MethodLine ml2 = tl.returnLine;
		    if (ml2 == null) break;
		    if (ml2 instanceof CatchLine) {
			TraceLine catchParent = ((CatchLine)ml2).traceLine;
			if (catchParent != parent) return null;
		    }
		    i = ml2.time;
		}
		if (ml instanceof ThrowLine) {
		    return(null);			// Not quite right. If catch is in this method.
		}
	    }
	}
	return(null); 				// Illegal TimeStamp set: too few ReturnLines	
    }


    // Previous line in this stack frame (if any)  only used in CodeActionListener
    public TimeStamp getPreviousLineThisFunction() {
	MethodLine ml = getPreviousMethodThisThread();
	int threadIndex = getThreadIndex();
	int sourceIndex = getSourceIndex();

	if (ml == null) return null;

	for (int i = time; i>=0; i--) {
	    if (i < ml.time) ml = lookup(i).getPreviousMethodThisThread();
	    if (threadIndex != getThreadIndex(i)) continue;
	    if ((getSourceIndex(i) != sourceIndex) && (getSourceIndex(i) != 0)) return(lookup(i));
	    if (getType(i) == FIRST) return(null);
	    if (i == ml.time) {
		if (ml instanceof TraceLine) {
		    if (i == time) continue;
		    return(null);					// Never get here?
		}
		if (ml instanceof ReturnLine) {
		    ReturnLine rl = (ReturnLine) ml;
		    TraceLine tl = rl.caller;
		    if (tl == null) break;
		    i = tl.time;
		    continue;
		}
		if (ml instanceof CatchLine) {
		    ml = ((CatchLine)ml).caller;
		    i = ml.time;
		    return(lookup(i));
		}
		return(null);					//Throw & Catch 
	    }
	}
	return(null); 					// Illegal TimeStamp set: too many ReturnLines
    }



    public TimeStamp findNearest(Thread thread) {
	int len = eott();
	if (time == -1) return null;

	for (int i = time; i>=0; i--) {				// Past is better
	    if (getThread(i) == thread)
		return(lookup(i));
	}

	for (int i = time; i<=len; i++) {	// Future is best?
	    if (getThread(i) == thread)
		return(lookup(i));
	}
	return(this); // No entry for this thread. GC'd? Never recorded one?
    }
	







    private static void doDataTest() {
	SourceLine sl = SourceLine.getSourceLine("Obj:file.java:0");
	int time = addStamp(sl, OTHER);
	TimeStamp ts = lookup(time);
	Debugger.println(ts.getThread() + " == " + getThread(ts.time));
	Debugger.println(ts.getSourceLine() + " == " + getSourceLine(ts.time));
	Debugger.println(getTypeString(ts.time));
	Debugger.println("0x"+Integer.toHexString(ts.data));
    }

    public static void main(String[] args) {
	int repeat = 100000, ix=0;
	//Thing[] a = new Thing[10000000];
	Thread t = Thread.currentThread();

	Debugger.println("----------------------TimeStamp----------------------\n");

	SourceLine sl = SourceLine.getSourceLine("Obj:file.java:0");
	//doDataTest();
	//Thread t = new Thread() {public void run() {doDataTest();} };
	//t.start();




	Debugger.println("----------------------TimeStamp----------------------\n");	     
    }

    public static TimeStamp lookup(int time) {
	if (time >= index) throw new DebuggerException("time >= eot() " + time +" >= "+index);
	if (time < 0) throw new DebuggerException("time < 0 " + time);
	Helper.singleton.time = time;
	TimeStamp ts = (TimeStamp)lookupTable.get(Helper.singleton);
	if (ts == null) {
	    ts = new TimeStamp(getThread(time), time, getSourceLine(time), getType(time));
	    Helper h = new Helper(time);
	    lookupTable.put(h, ts);
	}
	return ts;
    }


    public static void verifyCollection(int eot, String status) {
	//	if (true) return;
	if (!Debugger.DEBUG_DEBUGGER) return;
	Debugger.println("Verifying collection to "+index);

	for (int i = index; i < istamps.length; i++) {istamps[i]=-1;}

	for (int i = 0; i < index; i++) {
	    int type = TYPE_MASK & istamps[i];
	    if ((type == CALL)||(type == ABSENT)||(type == CATCH)||(type == RETURN)) {
		MethodLine ml = TraceLine.getMethodLine(i);
		if (ml instanceof TraceLine) {
		    TraceLine tl = (TraceLine) ml;
		    MethodLine rl = tl.returnLine;
		    if (rl != null)
			{MethodLine ml1 = TraceLine.getMethodLine(rl.time);}
		}
	    }
	}
	Shadow.verifyCollection(eot, status);
	for (int i = 0; i < MAX_THREADS; i++) {
	    Thread t = threads[i];
	    if (t == null) break;
	    TraceLine.verify(t, eot);
	}
	Debugger.println("Verified collection to "+index);
	Runtime run = Runtime.getRuntime();
	Debugger.println(" Memory: " + run.freeMemory()/1000000 + "MB free / " + run.totalMemory()/1000000 + "MB max");
    }

	    

    public static int collect(int percentage, boolean retainIV) {
	//	    Shadow.printAll();
	try {return collect0(percentage, retainIV);}
	catch (Exception e) {
	    Debugger.println("collect() failed");
	    e.printStackTrace();
	    //	    Debugger.dump();
	    Debugger.println("\n\n\n\n");
	    //	    Shadow.printAll();
	    //	    throw (DebuggerException)e;
	}
	return 0;
    }
    public static int collect0(int percentage, boolean retainIV) {
	int divider = (index * percentage) / 100;
	int newIndex = 0, nCollected=0;
	int[] newTS = new int[MAX_TIMESTAMPS];
	int nTLCollected = TraceLine.nCollected;

	//Debugger.println("Starting collection...");
	//	(new DebuggerException("Just print a trace")).printStackTrace();
	EOT = index;
	if (Debugger.DEBUG_DEBUGGER) Shadow.clearStatus();
	if (Debugger.DEBUG_DEBUGGER) verifyCollection(EOT, "cleared");
	if (Debugger.DEBUG_DEBUGGER) Debugger.println("Collecting(retaining IVs: "+retainIV+")...");
	//	Debugger.dump();

	for (int i = 0; i < index; i++) {
	    if (disposable(i, divider, retainIV)) {
		istamps[i] = -1;			// Somebody uses this?
		nCollected++;
	    }
	    else {
		newTS[newIndex] = istamps[i];
		istamps[i] = newIndex;		// forwarding address
		newIndex++;
	    }
	}

	// Compact

	EOT = newIndex;
	CURRENT_TIME = forward(TimeStamp.currentTime());
	PREVIOUS_TIME=CURRENT_TIME;
	Shadow.compactAll(EOT);
	Clock.compactAll();
	//if (Debugger.DEBUG_DEBUGGER) Shadow.verifyCollection(EOT, "compacted");
	ShadowPrintStream.compactAll(EOT);
	for (int i = 0; i < MAX_THREADS; i++) {
	    Thread t = threads[i];
	    if (t == null) break;
	    TraceLine.compact(t, EOT);
	}
	Set set =  lookupTable.keySet();
	Set setCopy = new HashSet(set);
	Iterator e = setCopy.iterator();
	while (e.hasNext()) {
	    Object key = e.next();
	    TimeStamp ts = (TimeStamp) lookupTable.get(key);
	    ts.forward();
	}

	nTLCollected = TraceLine.nCollected-nTLCollected;
	Debugger.println(" Collected "+nCollected+" out of "+index+" stamps and " +
			   nTLCollected + " TraceLines " + (retainIV ? "" : " including IVs"));

	//	Debugger.dump();
	index = newIndex;
	istamps=newTS;
	TraceLine.unfilter();
	//verifyCollection(EOT, "verified");
	Shadow.removeDead();
	return nCollected;
    }

    public void forward() {
	time = forward(time);
    }

    public static int forward(int time) { // rturns -1 if no forwarding time
	//	Debugger.println("forward " + time +" -> "+istamps[time]);
	if (time < 0) return -1;
	int f =istamps[time];
	if (f > EOT) throw new DebuggerException("TS.forwrd() failed on " +" ["+f+">"+EOT+"]");
	return(f);
    }

    public static int forwardNext(int time) { 		// Never? rturns -1
	if (time < 0) return -1;			// Never happen?
	for (int i = time; i < index; i++) {
	    int f = istamps[i];
	    if (f != -1) return f;
	}
	return istamps[EOT-1];
    }

    public static TimeStamp forward(TimeStamp ts) {
	if (ts == null) return null;
	return lookup(forward(ts.time));
    }

    private static boolean disposable(int i, int divider, boolean retainIV) {
	if (i == 0) return false;		// Never collect Primordial thread
	if (i > divider) return false;
	int type = TYPE_MASK & istamps[i];

	// MyHashTable MyVector use OTHER, also missing TSs 
	if (retainIV && ( (type == OBJECT_IV) || (type == ONE_D_ARRAY) || (type == OTHER)) ) return false;
	try {
	    if ((type == CALL)||(type == ABSENT)) {
		MethodLine ml = TraceLine.getMethodLine(i);
		//		if (ml instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml;
		MethodLine rl = tl.returnLine;
		if (rl == null) return false;
		if (rl.time > divider) return false;
		return true;
		//		}
		//		return true;
	    }
	}
	catch (DebuggerException e) {e.printStackTrace();}
	return true;
    }

}



class Helper {
  static Helper singleton = new Helper(0);
  public int time;

  public Helper(int i) {
    time = i;
  }

  public boolean equals(Object o) {
    if (o instanceof Helper) {
      if (((Helper)o).time == time) return true;
    }
    return false;
  }

  public int hashCode() {
    return time;
  }
}
