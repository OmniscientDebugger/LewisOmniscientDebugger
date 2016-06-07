/*                        Shadow.java

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

//              Shadow.java

/*
 */


import java.awt.FontMetrics;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class Shadow {

    public static final String BLOCKEDON = "_blockedOn";
    public static final String SLEEPERS = "_sleepers";
    public static final String OWNER = "_owner";
    public static final String WAITERS = "_waiters";
    public static final String LOCKSOWNED = "_locksOwned";
    
    private static final 	Comparator comp = new Comparator() {public int compare(Object o1, Object o2) {
				Field f1 = (Field) o1, f2 = (Field) o2;
				String s1 = f1.getName(), s2 = f2.getName();
				return s1.compareTo(s2);
				}
			};
		

    static private int			id_counter=0;
    static private int			stringLengths=0;
    static private int			nRemoved=0;
    static private int			nEntries=0;
    static int				wastedTripletons = 0, wastedMultitons = 0;

    static private HashMapEq		table = new HashMapEq(100);	// 10k
    static private HashMapEq		tableAlternate = new HashMapEq(100);	// 10k
    static private HashMapEq		blockedTable = new HashMapEq(100);
    static private HashMapEq		blockedTableAlternate = new HashMapEq(100);
    static private HashMapEq		sleeperTable = new HashMapEq(100);
    static private HashMapEq		sleeperTableAlternate = new HashMapEq(100);
    static private VectorD		toRemove= new VectorD();

    private HistoryList[] 		shadowVars;
    public Object 			obj;
    private int				id;
    public String 			tostring, tostringShort;
    public boolean			foreign = false;
    public ClassInformation		classInfo;
    public int				creationTime;
    public String 			gcStatus;

    public void initializeEvents() {
	int len = shadowVars.length;
	for (int i = 0; i < len; i++) {
	    String varName = classInfo.getVarName(i);
	    HistoryList hl = shadowVars[i];
	    if (hl == null) continue;
	    hl.initializeEvents(obj, varName);
	}
	HistoryList hl = getBlockedHL();
	if (hl != null) hl.initializeEvents(obj, "Lock");
	
    }

    public static void initializeAllEvents() {
	Iterator iter =  table.values().iterator();
	while (iter.hasNext()) {
	    Shadow sh = (Shadow) iter.next();	
	    sh.initializeEvents();
	}
    }

    public static ArrayList getAllObjects() {
	ArrayList al = new ArrayList();
	Iterator iter =  table.values().iterator();
	while (iter.hasNext()) {
	    Shadow sh = (Shadow) iter.next();
	    if (sh.creationTime > TimeStamp.ct()) continue;
	    al.add(sh.obj);
	}
	return al;
    }

    public static Iterator getIterator() {
	Iterator iter =  table.values().iterator();
	return iter;
    }




    
    static public void printStatistics() {
	Iterator iter =  table.values().iterator();
	int nSingletons = 0, nMultitons = 0, nTripletons=0;
	int[] sizes = new int[30];
	int nHLists = 0;
	int nHLEntries = 0;
	int emptyHLs=0;
	stringLengths=0;
	wastedTripletons = 0;
	wastedMultitons = 0;

	while (iter.hasNext()) {
	    Shadow sh = (Shadow) iter.next();
	    if (sh.tostring != null) stringLengths += sh.tostring.length();
	    //	    if (sh.tostringShort != null) stringLengths += sh.tostringShort.length();
	    HistoryList hls[] = sh.shadowVars;
	    nHLists += hls.length;
	    for (int i = 0; i < hls.length; i++) {
		HistoryList hl = hls[i];
		if (hl == null) {emptyHLs++; continue;}
		int size = hl.size();
		nHLEntries += size;
		if (hl instanceof HistoryListSingleton) nSingletons++;
		if (hl instanceof HistoryListTripleton) {nTripletons++; wastedTripletons += (3-hl.size());}
		if (hl instanceof HistoryListMultiple) {
		    nMultitons++;
		    HistoryListMultiple hlm = (HistoryListMultiple) hl;
		    wastedMultitons+=hlm.wasted();
		    if (size < 10)
			sizes[size]++;
		    else
			if (size < 20) sizes[10]++;
			else
			    if (size < 30) sizes[11]++;
			    else
				if (size < 40) sizes[12]++;
				else
				    if (size < 50) sizes[13]++;
				    else
					if (size < 60) sizes[14]++;
					else
					    if (size < 100) sizes[15]++;
					    else
						if (size < 200) sizes[16]++;
						else
						    if (size < 500) sizes[17]++;
						    else
							if (size < 800) sizes[18]++;
							else
							    if (size < 1200) sizes[19]++;
							    else
								if (size < 1600) sizes[20]++;
								else
								    if (size < 2000) sizes[21]++;
								    else
									if (size < 4000) sizes[22]++;
									else
									    if (size < 8000) sizes[23]++;
									    else
										if (size < 16000) sizes[24]++;
										else
										    sizes[25]++;


		}
	    }
	}

	System.out.println("\n -- Shadow Statistics -- ");
	System.out.println("Out of " + table.size() + " Shadows with " + nHLists + " HistoryLists and " + nHLEntries +" entries...");
	System.out.println(" Shadows removed: "+nRemoved );
	System.out.println(" Shadow string lengths: " + stringLengths);
	System.out.println(" "+emptyHLs + " empty HLs");
	System.out.println(" "+nSingletons + " nSingletons");
	System.out.println(" "+nTripletons + " Tripletons of which wasted: "+wastedTripletons);
	System.out.println(" "+nMultitons + " nMultitons, of which wasted: "+wastedMultitons);
	System.out.println("  size\tnumber");
	for (int i = 0; i < 10; i++) {if (sizes[i] > 0) System.out.println("  "+ i + "\t"+sizes[i]);}
	if (sizes[10] > 0) System.out.println("  <20\t"+sizes[10]);
	if (sizes[11] > 0) System.out.println("  <30\t"+sizes[11]);
	if (sizes[12] > 0) System.out.println("  <40\t"+sizes[12]);
	if (sizes[13] > 0) System.out.println("  <50\t"+sizes[13]);
	if (sizes[14] > 0) System.out.println("  <60\t"+sizes[14]);
	if (sizes[15] > 0) System.out.println("  <100\t"+sizes[15]);
	if (sizes[16] > 0) System.out.println("  <200\t"+sizes[16]);
	if (sizes[17] > 0) System.out.println("  <400\t"+sizes[17]);
	if (sizes[18] > 0) System.out.println("  <800\t"+sizes[18]);
	if (sizes[19] > 0) System.out.println("  <1200\t"+sizes[19]);
	if (sizes[20] > 0) System.out.println("  <1600\t"+sizes[20]);
	if (sizes[21] > 0) System.out.println("  <2k\t"+sizes[21]);
	if (sizes[22] > 0) System.out.println("  <4k\t"+sizes[22]);
	if (sizes[23] > 0) System.out.println("  <8k\t"+sizes[23]);
	if (sizes[24] > 0) System.out.println("  <16k\t"+sizes[24]);
    }

  
    static public void reset() {		// Clear out all values except for the current time value. Set that to time 0
	Iterator iter =  table.values().iterator();
	int time = TimeStamp.currentTime().time;

	while (iter.hasNext()) {
	    Shadow sh = (Shadow) iter.next();	
	    sh.reset(time);
	}
    }

    public void reset(int time) {
	for (int i = 0; i < size(); i++) {
	    HistoryList hl = shadowVars[i];
	    if (hl == null) continue;
	    hl.reset(time);
	}
    }
    


    static public int nEntries() {
	return nEntries;
    }

    static public void initialize() {				// These are to allow prgmr to type in values for EvaluateExpression etc. 
	Shadow sh;
	Object o;

	sh = new Shadow(0, o=new Boolean(true), new String[0], new Object[0], "true");
	table.put(o, sh);
	sh = new Shadow(0, o=new Boolean(false), new String[0], new Object[0], "false");
	table.put(o, sh);
	sh = new Shadow(0, o=ShadowNull.NULL, new String[0], new Object[0], "null");
	table.put(o, sh);
    }
  

    static public HashMapEq getTable() {		// ONLY CALLED BY "CALL INTERACTIVELY" COMPLETION CODE.
	return table;
    }


    // DUPLICATE the tables for the alternate time line, but with only 1 HL entry.
    private Shadow dup(int time, Object o) {
	Shadow s = new Shadow();
	s.classInfo = classInfo;
	s.shadowVars = dupSV(time, shadowVars);
	dupSleepers(time);
	dupBlocked(time);
	s.obj = obj;
	s.id = id;
	s.tostring =s.tostringShort = tostring;// + "_Alt";
	return s;
    }


    private void dupSleepers(int now) {
	SleeperSet ss = getSleeperSet();
	if (ss == null) return;
	ss = ss.dup(now);				// set to empty!
	sleeperTableAlternate.put(obj, ss);
    }


    private void dupBlocked(int now) {
	HistoryList hl = getBlockedHL();
	if (hl == null) return;
	//	hl = hl.dup(now);
	hl = new HistoryListSingleton();
	blockedTableAlternate.put(obj, hl);
    }


    private HistoryList[] dupSV(int time, HistoryList[] hls) {
	if (hls == null) return null;
	HistoryList[] hls1 = new HistoryList[hls.length];
	for (int i = 0; i < hls.length; i++) {
	    HistoryList hl = hls[i];
	    if (hl == null) continue;
	    hls1[i] = hl.dup(time);
	}
	return hls1;
    }

    public static Shadow getAlternate(Object o) {
	Shadow s = (Shadow) table.get(o);			// Shadow.switch has already been called.
	return s;
    }


    public static void updateAll() {
	Iterator iter =  table.values().iterator();

	while (iter.hasNext()) {
	    Shadow sh = (Shadow) iter.next();	
	    update(sh.obj);
	}
    }

    // Update the actual object to reflect the currently revert'd time.  
    public static void update(Object o) {
	if (o instanceof MyVector) return;
        if (o instanceof MyHashtable) return;
        if (o instanceof MyHashMap) return;
	if (o instanceof MyArrayList) return;
	try {update1(o);}
	catch (Exception e) {
	    System.out.println("While updating: " + o + " threw " + e);
	    Debugger.message("While updating: " + o + " threw " + e, true);
	}
    }

    public static void update1(Object o) {
	Shadow s = (Shadow) table.get(o);			// Shadow.switch has already been called.
	int len = s.size();
	Field f;
	TimeStamp ts = TimeStamp.currentTime();

	//    //    System.out.println("Updating: " + ((s.tostring != null) ? s.tostring : o));

	if (o instanceof int[]) {
	    int[] array = (int[]) o;
	    for (int i = 0; i < len; i++) {
		HistoryList hl = s.shadowVars[i];
		if (hl == null) continue;
		Object historyValue =  hl.valueOn(ts, false);
		if (historyValue instanceof ShadowInt) {
		    ShadowInt value = (ShadowInt) historyValue;
		    if (array[i] == value.intValue()) continue;
		    array[i] = value.intValue();
		    //	  System.out.println("  Updated " + i + " to " +  value.intValue());
		}
		else
		    if (historyValue instanceof Dashes) {
			//	    System.out.println("  Object not defined yet");
			return;
		    }
	    }
	    return;
	}

	if (o instanceof Object[]) {
	    Object[] array = (Object[]) o;
	    for (int i = 0; i < s.size(); i++) {
		HistoryList hl = s.shadowVars[i];
		if (hl == null) continue;
		Object historyValue =  hl.valueOn(ts, false);
		if (historyValue instanceof Dashes) {
		    //	    System.out.println("  Object not defined yet");
		    return;
		}
		if (array[i] == historyValue) continue;
		array[i] = historyValue;
		//	  System.out.println("  Updated " + i + " to " +  value.intValue());
	    }
	    return;
	}


	Class c;
	if (o instanceof Class)
	    c = (Class) o;
	else
	    c = o.getClass();

	for (int i = 0; i < len; i++) {
	    HistoryList hl = s.shadowVars[i];
	    if (hl == null) continue;
	    Object historyValue =  hl.valueOn(ts, false);
	    if (historyValue instanceof ShadowNull) historyValue = null;
	    String fieldName = s.classInfo.getVarName(i);

	    //            System.out.println("  Updating " + fieldName + " to " + historyValue);
      
	    if (historyValue instanceof Dashes) {
		//System.out.println("  Object not defined yet");
		return;
	    }

	    try { f = c.getField(fieldName); }
	    catch (Exception e) {D.println(""+e); continue;}		// Shouldn't ever happen


	    if (historyValue instanceof ShadowPrimitive) {
		if (historyValue instanceof ShadowInt) {
		    ShadowInt si = (ShadowInt) historyValue;
		    int historyIntValue = si.intValue();
		    try {
			int fieldValue = f.getInt(o);
			if (fieldValue == historyIntValue) continue;
			f.setInt(o, historyIntValue);
			//	    System.out.println("  Updated " + fieldName + " to " + historyIntValue);
		    }
		    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		}
		else if (historyValue instanceof ShadowBoolean) {
		    ShadowBoolean si = (ShadowBoolean) historyValue;
		    boolean historyBooleanValue = si.booleanValue();
		    try {
			boolean fieldValue = f.getBoolean(o);
			if (fieldValue == historyBooleanValue) continue;
			f.setBoolean(o, historyBooleanValue);
			//	    System.out.println("  Updated " + fieldName + " to " + historyBooleanValue);
		    }
		    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		}
		else if (historyValue instanceof ShadowLong) {
		    ShadowLong si = (ShadowLong) historyValue;
		    long historyLongValue = si.longValue();
		    try {
			long fieldValue = f.getLong(o);
			if (fieldValue == historyLongValue) continue;
			f.setLong(o, historyLongValue);
			//	    System.out.println("  Updated " + fieldName + " to " + historyLongValue);
		    }
		    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		}
		else if (historyValue instanceof ShadowShort) {
		    ShadowShort si = (ShadowShort) historyValue;
		    short historyShortValue = si.shortValue();
		    try {
			long fieldValue = f.getShort(o);
			if (fieldValue == historyShortValue) continue;
			f.setShort(o, historyShortValue);
			//	    System.out.println("  Updated " + fieldName + " to " + historyShortValue);
		    }
		    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		}
		else if (historyValue instanceof ShadowByte) {
		    ShadowByte si = (ShadowByte) historyValue;
		    byte historyByteValue = si.byteValue();
		    try {
			byte fieldValue = f.getByte(o);
			if (fieldValue == historyByteValue) continue;
			f.setByte(o, historyByteValue);
			//	    System.out.println("  Updated " + fieldName + " to " + historyByteValue);
		    }
		    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		}
		else if (historyValue instanceof ShadowChar) {
		    ShadowChar si = (ShadowChar) historyValue;
		    char historyCharValue = si.charValue();
		    try {
			char fieldValue = f.getChar(o);
			if (fieldValue == historyCharValue) continue;
			f.setChar(o, historyCharValue);
			//	    System.out.println("  Updated " + fieldName + " to " + historyCharValue);
		    }
		    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		}
		else if (historyValue instanceof ShadowFloat) {
		    ShadowFloat si = (ShadowFloat) historyValue;
		    float historyFloatValue = si.floatValue();
		    try {
			float fieldValue = f.getFloat(o);
			if (fieldValue == historyFloatValue) continue;
			f.setFloat(o, historyFloatValue);
			//	    System.out.println("  Updated " + fieldName + " to " + historyFloatValue);
		    }
		    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		}
		else if (historyValue instanceof ShadowDouble) {
		    ShadowDouble si = (ShadowDouble) historyValue;
		    double historyDoubleValue = si.doubleValue();
		    try {
			double fieldValue = f.getDouble(o);
			if (fieldValue == historyDoubleValue) continue;
			f.setDouble(o, historyDoubleValue);
			//	    System.out.println("  Updated " + fieldName + " to " + historyDoubleValue);
		    }
		    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
		}
		continue;
	    }

	    // if value is Object

	    try {
		Object fieldValue = f.get(o);
		if (fieldValue == historyValue) continue;
		f.set(o, historyValue);
		//	System.out.println("  Updated " + fieldName + " to " + historyValue);
	    }
	    catch (IllegalAccessException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }
	    catch (IllegalArgumentException e) { D.println("  Update cannot set field " + f + " in " + s.tostring + " "+e); }

	    continue;
	}
    }

    public static void clear() {
	table.clear();
    }

    private void compact(int eot) {
	//	System.out.println("Compacting "+this);
	if (Debugger.DEBUG_DEBUGGER) if (gcStatus != "verified") throw new DebuggerException(this + " not verified " + " but " + gcStatus);
	boolean dead = true, deadOne = true;
	for (int i = 0; i < size(); i++) {
	    HistoryList hl = shadowVars[i];
	    if (hl != null) {
		deadOne = hl.compact(eot);
		if (!deadOne) dead = false;		// Only dead if all are dead
	    }
	}
	if (dead) {
	    toRemove.add(this);			// Nothing interesting left in HLs
	    nRemoved++;
	}
	if (Debugger.DEBUG_DEBUGGER) 	gcStatus = "compacted";
    }
    /*
    public static void compactAll(int eot) {
	toRemove = new VectorD();
	Set set =  table.keySet();
	Set setCopy = new HashSet(set);
	Iterator e = setCopy.iterator();
	//      System.out.println("Shadows compacting to " + table.size() + " " + set.size());

	while (e.hasNext()) {
	    Object key = e.next();
	    Shadow s = (Shadow) table.get(key);
	    s.compact(eot);
	}

	if (Debugger.DEBUG_DEBUGGER) System.out.println("Shadows compacted to " + table.size());

	}
    */

    public static void compactAll(int eot) {
	toRemove = new VectorD();
	Iterator i =  table.values().iterator();
	while (i.hasNext()) {
	    Shadow s = (Shadow) i.next();
	    s.compact(eot);
	}

	if (Debugger.DEBUG_DEBUGGER) System.out.println("Shadows compacted to " + table.size());

	}

    public static void removeDead() {
	for (int i = toRemove.size()-1; i > -1; i--) {
	    Shadow s = (Shadow) toRemove.elementAt(i);
	    //System.out.println("removing "+ s.obj);
	    table.remove(s.obj);
	}
    }

    public static void clearStatus() {
	Iterator iter =  table.values().iterator();
	while (iter.hasNext()) {
	    Shadow sh = (Shadow) iter.next();	
	    if (Debugger.DEBUG_DEBUGGER) 	    sh.gcStatus = "cleared";
	}
	//      System.out.println("Shadows cleared to " + table.size() + " " + set.size());
    }


    private void verify(int eot) {
	//	System.out.println("Verifying "+this);
	for (int i = 0; i < size(); i++) {
	    HistoryList hl = shadowVars[i];
	    if (hl != null) hl.verify(eot);
	}
    }


    public static void verifyCollection(int eot, String status) {
	Iterator iter =  table.values().iterator();
	System.out.println("Shadows verifing to " +eot+ " table size: "+ table.size());
	while (iter.hasNext()) {
	    Shadow sh = (Shadow) iter.next();	
	    if (toRemove.contains(sh.obj)) throw new DebuggerException(sh + " impossobie");
	if (Debugger.DEBUG_DEBUGGER) 	    if (sh.gcStatus != status) throw new DebuggerException(sh + " not "+status+ " but "+sh.gcStatus);
	    sh.verify(eot);
	if (Debugger.DEBUG_DEBUGGER) 	    sh.gcStatus = "verified";
	}
	System.out.println("Shadows verified to " + table.size());
    }
	

    public static void switchTimeLines(boolean clear) {
	if (clear) tableAlternate.clear();
	if (tableAlternate.size() == 0) {
	    int time = TimeStamp.currentTime().time;
	    Iterator iter =  table.values().iterator();
	    while (iter.hasNext()) {
		Shadow sh = (Shadow) iter.next();	
		tableAlternate.put(sh.obj, sh.dup(time, sh.obj));
	    }
	}
	else {
	    int time = TimeStamp.currentTime().time;
	    Iterator iter =  table.values().iterator();
	    while (iter.hasNext()) {
		Shadow sh = (Shadow) iter.next();	
		sh.reset(time);
	    }
	}
      
    
	HashMapEq a = table;
	table = tableAlternate;
	tableAlternate = a;
	a = sleeperTable;
	sleeperTable = sleeperTableAlternate;
	sleeperTableAlternate = a;
	a = blockedTable;
	blockedTable = blockedTableAlternate;
	blockedTableAlternate = a;
    }

    public static int tableSize() {
	return(table.size());
    }


    public void dontShow(String name) {
    }

    public String getVarName(int i) {return classInfo.getVarName(i);}
    public Class getVarType(int i) {return classInfo.getVarClass(i);}

    public HistoryList getBlockedHL() {
	return (HistoryList) blockedTable.get(obj);
    }
    static public HistoryList getBlockedHL(Thread t) {
	return (HistoryList) blockedTable.get(t);
    }
    public SleeperSet getSleeperSet() {
	return (SleeperSet) sleeperTable.get(obj);
    }

    static private HistoryList addBlocked(Thread tid) {
	HistoryList hl = (HistoryList) blockedTable.get(tid);
	if (hl == null) {
	    hl = new HistoryListMultiple(BLOCKEDON);
	    blockedTable.put(tid, hl);
	}
	return hl;
    }

    static private SleeperSet addSleeperSet(Object o, int time) {
	SleeperSet ss = (SleeperSet)sleeperTable.get(o);
	if (ss == null) {
	    ss = new SleeperSet(time);
	    sleeperTable.put(o, ss);
	}
	return ss;
    }

    public void threadGetting(int time, Object o, TraceLine tl) {
	Thread tid = (Thread) obj;
	HistoryList hl = addBlocked(tid);
	hl.add(time, o);
    }
  
    public void threadGot(int time, Object o, TraceLine tl, boolean elide) {
	Thread tid = (Thread) obj;
	HistoryList hl = addBlocked(tid);
	if (elide)
	    hl.removeLast();
	else
	    hl.add(time, null);
    }


    public void threadReleasing(int time, Object o, TraceLine tl) {			// Currently does nothing
	if (!(obj instanceof Thread)) throw new DebuggerException("Shadow.threadWaitingFor() expecting a thread: " + obj);
    
    }



    public void addSleeper(int time, Thread tid, TraceLine tl) {
	SleeperSet ss = addSleeperSet(obj, time);

	HistoryList hl = ss.sleepers;
	LocksList ll = (LocksList) hl.getLastValue();
	if (ll == null) return;				// Shouldn't be possible
	LocksList llc = (LocksList)ll.clone();
	llc.add(new LockerPair(null, tid));
	hl.add(time, llc);
    }



    public void removeSleeperAddOwner(int time, Object o, TraceLine tl, boolean elide) {
	Thread tid = tl.getThread();
	SleeperSet ss = addSleeperSet(obj, time);

	HistoryList shl = ss.sleepers;
	HistoryList ohl = ss.owner;

	// Remove sleeper
	if (elide)
	    shl.removeLast();
	else {
	    LocksList ll = (LocksList) shl.getLastValue();
	    if (ll == null) return;				// Shouldn't be possible
	    LocksList llc = (LocksList)ll.clone();
	    LockerPair removed = llc.removeLP(tid);
	    if (removed != null) shl.add(time, llc);
	}
	// Add owner at depth
	LockerPair locker = (LockerPair) ohl.getLastValue();
	LockerPair lp = new LockerPair(locker, tid);
	ohl.add(time, lp);
    }





    public boolean removeOwner(int time, Object o, TraceLine tl) {
	Thread tid = tl.getThread();
	SleeperSet ss = addSleeperSet(obj, time);

	HistoryList shl = ss.sleepers;
	HistoryList ohl = ss.owner;

	LockerPair lp = (LockerPair) ohl.getLastValue();		// There must be an owner to get here.
	if (lp == null) return true;				// Only possible when unbalanced ?
	LockerPair prev = lp.previous();

	if (lp.getCount() == 1) {
	    ohl.add(time, null);
	    return true;
	}
    
	ohl.add(time, prev);
	return false;
    }




    public void addWaiterRemoveOwner(int time, Thread tid, TraceLine tl) {
	SleeperSet ss = addSleeperSet(obj, time);
	HistoryList whl = ss.waiters;
	HistoryList ohl = ss.owner;

	LocksList wl = (LocksList) whl.getLastValue();		// Get the WAITERS
	if (wl == null) return;				// Shouldn't be possible
	LocksList wlc = (LocksList)wl.clone();
	LockerPair owner = (LockerPair) ohl.getLastValue();		// There must be an owner to get here.
	wlc.add(owner);

	whl.add(time, wlc);
	ohl.add(time, null);
    }

    public void removeWaiterAddOwner(int time, Thread tid, TraceLine tl) {
	SleeperSet ss = addSleeperSet(obj, time);
	HistoryList whl = ss.waiters;
	HistoryList ohl = ss.owner;

	LocksList wl = (LocksList) whl.getLastValue();		// Get the WAITERS
	if (wl == null) return;				// Shouldn't be possible
	LocksList wlc = (LocksList)wl.clone();
	LockerPair lp = wlc.removeLP(tid);
	whl.add(time, wlc);
	ohl.add(time, lp);
    }


    public void addJoiner(int time, Thread tid, TraceLine tl) {
	SleeperSet ss = addSleeperSet(obj, time);
	HistoryList whl = ss.waiters;

	LocksList wl = (LocksList) whl.getLastValue();		// Get the WAITERS
	if (wl == null) return;				// Shouldn't be possible
	LocksList wlc = (LocksList)wl.clone();
	wlc.add(new LockerPair(null, tid));
	whl.add(time, wlc);
    }


    public void removeJoiner(int time, Thread tid, TraceLine tl) {
	SleeperSet ss = addSleeperSet(obj, time);
	HistoryList whl = ss.waiters;

	LocksList wl = (LocksList) whl.getLastValue();		// Get the WAITERS
	if (wl == null) return;				// Shouldn't be possible
	LocksList wlc = (LocksList)wl.clone();
	LockerPair lp = wlc.removeLP(tid);
	whl.add(time, wlc);
    }



    public void showAll() {
    }


    public void removeLastShadowVarValue(int i) {
	HistoryList hl = shadowVars[i];
	hl.removeLast();
    }


    public void addToShadowVar(int i, int time, Object value) {
	nEntries++;
	HistoryList hl = getShadowVar(i);	
	HistoryList hlNew = hl.add(time, value);
	if (hlNew != null) shadowVars[i] = hlNew;
    }

    public void addToShadowVarExtend(int i, int time, Object value) {
	nEntries++;
	int len = shadowVars.length;
	if (i >= len) {
	    HistoryList[] newVars = new  HistoryList[len*2];
	    System.arraycopy(shadowVars, 0, newVars, 0, len);
	    shadowVars = newVars;
	}
	HistoryList hl = shadowVars[i];
	if (hl == null) {
	    shadowVars[i] = new HistoryListSingleton(time, value);
	    return;
	}
	hl.add(time, value);
	HistoryList hlNew = hl.add(time, value);
	if (hlNew != null) shadowVars[i] = hlNew;
    }

    public HistoryList getShadowVar(int i) {
	if (shadowVars == null) return null;
	HistoryList hl = shadowVars[i];
	if (hl == null) {

	    if (isVector()) {		
		MyVector array = (MyVector) obj;
		int l = array.size();
		hl = new HistoryListSingleton(creationTime, array.elementAt(i));
		shadowVars[i] = hl;
		return hl;
	    }
	    if (isArrayList()) {		
		MyArrayList array = (MyArrayList) obj;
		int l = array.size();
		hl = new HistoryListSingleton(creationTime, array.get(i));
		shadowVars[i] = hl;
		return hl;
	    }
	    if (isArray()) {
		Class clazz = obj.getClass();
		if (clazz == int[].class) {
		    int[] array = (int[]) obj;
		    hl = new HistoryListTripleton(creationTime, ShadowInt.createShadowInt(array[i]));
		    shadowVars[i] = hl;
		    return hl;
		}
		if (clazz == long[].class) {
		    long[] array = (long[]) obj;
		    hl = new HistoryListTripleton(creationTime, ShadowLong.createShadowLong(array[i]));
		    shadowVars[i] = hl;
		    return hl;
		}
		if (clazz == byte[].class) {
		    byte[] array = (byte[]) obj;
		    hl = new HistoryListTripleton(creationTime, ShadowByte.createShadowByte(array[i]));
		    shadowVars[i] = hl;
		    return hl;
		}
		if (clazz == boolean[].class) {
		    boolean[] array = (boolean[]) obj;
		    hl = new HistoryListTripleton(creationTime, ShadowBoolean.createShadowBoolean(array[i]));
		    shadowVars[i] = hl;
		    return hl;
		}
		if (clazz == char[].class) {
		    char[] array = (char[]) obj;
		    hl = new HistoryListTripleton(creationTime, ShadowChar.createShadowChar(array[i]));
		    shadowVars[i] = hl;
		    return hl;
		}
		if (clazz == short[].class) {
		    short[] array = (short[]) obj;
		    hl = new HistoryListTripleton(creationTime, ShadowShort.createShadowShort(array[i]));
		    shadowVars[i] = hl;
		    return hl;
		}
		if (clazz == float[].class) {
		    float[] array = (float[]) obj;
		    hl = new HistoryListTripleton(creationTime, ShadowFloat.createShadowFloat(array[i]));
		    shadowVars[i] = hl;
		    return hl;
		}
		if (clazz == double[].class) {
		    double[] array = (double[]) obj;
		    hl = new HistoryListTripleton(creationTime, ShadowDouble.createShadowDouble(array[i]));
		    shadowVars[i] = hl;
		    return hl;
		}
		{// Object[]
		    Object[] array = (Object[]) obj;
		    hl = new HistoryListTripleton(creationTime, array[i]);
		    shadowVars[i] = hl;
		    return hl;
		}
	    }
		
	    Field f;
	    String fieldName = classInfo.getVarName(i);
	    try { f = classInfo.clazz.getField(fieldName); }
	    catch (Exception e) {D.println(""+e); f=null;}		// Shouldn't ever happen
	    Object oldValue;
	    try {oldValue = f.get(obj);}	// Returns new Integer() &c. FIX SOMEDAY	}
	    catch (IllegalArgumentException e) { // NARROW!!
		D.println("createShadow cannot access field in " + obj + " "+e); oldValue = "ODB Error in createShadow";
	    }
	    catch (IllegalAccessException e) { // NARROW!!
		D.println("createShadow cannot access field in " + obj + " "+e); oldValue = "ODB Error in createShadow";
	    }
	    oldValue = convert(oldValue);
	    hl = new HistoryListTripleton(creationTime, oldValue);
	    shadowVars[i] = hl;
	}
      
	return(hl);
    }

    

    public void setShadowVar(int i, HistoryList hl) {
	shadowVars[i]=hl;
    }

    public Object value() {
	return(obj);
    }
    


    public TimeStamp getFirstAllVars() {
	TimeStamp best = TimeStamp.eot();
	int len = size();
	for (int i=0; i<len; i++) {
	    TimeStamp ts = getShadowVar(i).getFirst();	// cannot be null
	    if (best.laterThan(ts))
		best=ts;
	}
	return(best);// if no IVs, then return eot?
    }

    public TimeStamp getLastAllVars() {
	TimeStamp best = TimeStamp.bot();
	int len = size();
	for (int i=0; i<len; i++) {
	    TimeStamp ts = getShadowVar(i).getLast();	// cannot be null
	    if (ts.laterThan(best))
		best=ts;
	}
	return(best);// if no IVs, then return bot?
    }

    public TimeStamp getPreviousAllVars() {
	TimeStamp best = TimeStamp.bot();
	int len = size();
	for (int i=0; i<len; i++) {
	    TimeStamp ts = getShadowVar(i).getPrevious();
	    if (ts == null) continue;
	    if (!best.laterThan(ts))
		best=ts;
	}
	if (best.botp())
	    return(null);
	else
	    return(best);
    }


    public TimeStamp getNextAllVars() {
	TimeStamp best = TimeStamp.eot();
	int len = size();
	for (int i=0; i<len; i++) {
	    TimeStamp ts = getShadowVar(i).getNext();
	    if (ts == null) continue;
	    if (!ts.laterThan(best)){
		best=ts;
	    }
	}
	if (best.eotp())
	    return(null);
	else
	    return(best);
    }

    public void setForeign(boolean f) {
	foreign = f;
    }


    public static Shadow createShadow(Object o, boolean notForeign) {
	Shadow sh =(Shadow) table.get(o);
	if (sh == null) {
	    sh = createShadowInternal(o, notForeign);  // Cannot be null
	    table.put(o, sh);
	}
	else
	    sh.setForeign(false);// Why not notForeign??
	return sh;
    }
    
    private static Shadow createShadowInternal(Object o, boolean notForeign) {
	Shadow s;
	int time = TimeStamp.eott();
	if (!notForeign) time = 0;
	Class c;
	c = o.getClass();
	//String className = c.getName();
	s = (Shadow) table.get(o);


	if (s != null) {
	    if (notForeign) s.setForeign(false);
	    //D.println("Trying to call invokespecial on an existing object: createShadow("+TimeStamp.trimToLength(o, 50)+")");
	    return s;		// How can there already be a shadow?? THIS MAY GET CALLED INAPPROPRIATELY
	}



	if ((o instanceof Object[])) {
	    Object[] array = (Object[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof int[])) {
	    int[] array = (int[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof int[][])) {
	    int[][] array = (int[][]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof short[])) {
	    short[] array = (short[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof byte[])) {
	    byte[] array = (byte[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof char[])) {
	    char[] array = (char[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof boolean[])) {
	    boolean[] array = (boolean[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof long[])) {
	    long[] array = (long[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof double[])) {
	    double[] array = (double[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof float[])) {
	    float[] array = (float[]) o;		
	    s = new Shadow(TimeStamp.eott(), array, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof MyVector)) {
	    MyVector v = (MyVector) o;
	    s = new Shadow(TimeStamp.eott(), v, notForeign);
	    table.put(o, s);
	    return s;
	} else 	    if ((o instanceof MyArrayList)) {
	    MyArrayList v = (MyArrayList) o;
	    s = new Shadow(TimeStamp.eott(), v, notForeign);
	    table.put(o, s);
	    return s;
	}  else            if ((o instanceof MyHashtable)) {
            MyHashtable h = (MyHashtable) o;            
            s = new Shadow(TimeStamp.eott(), h, notForeign);
            table.put(o, s);
            return s;
        }  else             if ((o instanceof MyHashMap)) {
            MyHashMap h = (MyHashMap) o;            
            s = new Shadow(TimeStamp.eott(), h, notForeign);
            table.put(o, s);
            return s;
        } 

	Field[] f;
	int nFields = 0, index = 0;
	String[] varNames;
	Object[] initValues;
	//D.println("Obj: " + o + " in " + c + " " + f);
	if ((o instanceof Class)) {	// Try to make EVERYTHING a ShadowClass?
	    index = 0;
	    try{f = ((Class)o).getFields();}
	    catch (NoClassDefFoundError e) {Debugger.println("Impossible: NCD "+o); return createShadow(Shadow.class, true);}
		Arrays.sort(f, comp);
	    int len = f.length;
	    for (int i = 0; i< len; i++) {if (!Modifier.isStatic(f[i].getModifiers())) nFields++;}

	    varNames = new String[f.length-nFields];
	    initValues = new Object[f.length-nFields];
	    for (int i = 0; i< len; i++) {
		if (! Modifier.isStatic(f[i].getModifiers())) continue;
		//D.println("Class: " + o + " " + f[i]);
		String n = f[i].getName().intern();
		Class fc = f[i].getType();
		Object value;
		try {value = f[i].get(o);}	// Returns new Integer() &c. FIX SOMEDAY	}
		catch (IllegalAccessException e) { // NARROW!!
		    D.println("createShadow cannot access field in " + o + " "+e); value = "ODB Error in createShadow";
		}
		value = convert(value);
		varNames[index] = n;
		initValues[index] = value;
		index++;
		//D.println("createShadow(STATIC): " + o + "." + n + " = (" + fc + ") " + value);
	    }
	    s = new Shadow(0, o, varNames, initValues, false); // All Classes (ie statics) are foreign  STATICS ARE CONSIDERED TO ALWAYS EXIST
	    table.put(o, s);
	    return s;
	}
	else {
	    index = 0;
	    f = c.getFields();
		Arrays.sort(f, comp);
	    int len = f.length;
	    for (int i = 0; i< len; i++) {if (!Modifier.isStatic(f[i].getModifiers())) nFields++;}
	    varNames = new String[nFields];
	    initValues = new Object[nFields];

	    for (int i = 0; i< len; i++) {
		if (Modifier.isStatic(f[i].getModifiers())) continue;
		//D.println("Obj: " + o + " in " + c + " " + f[i]);
		String n = f[i].getName().intern();
		Class fc = f[i].getType();
		Object value;
		try {value = f[i].get(o);}	// Returns new Integer() &c. FIX SOMEDAY	}
		catch (IllegalAccessException e) { // NARROW!!
		    D.println("createShadow cannot access field: "+ f[i] +" in: " + o + " "+e); value = "ODB Error in createShadow";
		}
		value = convert(value);
		varNames[index] = n;
		initValues[index] = value;
		//D.println("createShadow(DYNAMIC): " +index + " "+ c + "."+ o + "." + n + " = (" + fc + ") " + value);
		index++;
	    }
	    s = new Shadow(time, o, varNames, initValues, notForeign);
	    table.put(o, s);
	    return s;
	}
	/*
	// Now check the class is in the table
	s = (Shadow) table.get(c);
	if (s == null) {
	index = 0;
	f = c.getFields();
	int len = f.length;
	for (int i = 0; i< len; i++) {if (!Modifier.isStatic(f[i].getModifiers())) nFields++;}
	varNames = new String[f.length-nFields];
	initValues = new Object[f.length-nFields];

	for (int i = 0; i< len; i++) {
	//D.println("Class: " + c +" " + f[i]);
	if (! Modifier.isStatic(f[i].getModifiers())) continue;
	String n = f[i].getName().intern();
	Class fc = f[i].getType();
	Object value;
	try {value = f[i].get(o);}	// Returns new Integer() &c. FIX SOMEDAY	}
	catch (IllegalAccessException e) { // NARROW!!
	D.println("createShadow cannot access field in " + o + " "+e); value = "ODB Error in createShadow";
	}
	value = convert(value);
	varNames[index] = n;
	initValues[index] = value;
	index++;
	//D.println("createShadow(STATIC 2): " + c + "."+ o + "." + n + " = (" + fc + ") " + value);
	}
	s = new Shadow(time, c, varNames, initValues, notForeign);
	table.put(c, s);
	return(s);
	}
	}
	return(new Shadow(time, o, new String[0], new String[0], true));
	*/
    }

    public static Object convert(Object value) {
	if (value instanceof Integer) return(ShadowInt.createShadowInt(((Integer)value).intValue()));
	if (value instanceof Boolean) return(ShadowBoolean.findShadowBoolean(((Boolean)value).booleanValue()));
	if (value instanceof Byte) return(ShadowByte.createShadowByte(((Byte)value).byteValue()));
	if (value instanceof Character) return(ShadowChar.createShadowChar(((Character)value).charValue()));
	if (value instanceof Short) return(ShadowShort.createShadowShort(((Short)value).shortValue()));
	if (value instanceof Long) return(ShadowLong.createShadowLong(((Long)value).longValue()));
	if (value instanceof Float) return(new ShadowFloat(((Float)value).floatValue()));
	if (value instanceof Double) return(new ShadowDouble(((Double)value).doubleValue()));
	if (value == null) return ShadowNull.NULL;
	return value;
    }

    private String userSelectedField(Object o) {
	if (classInfo == null) return null;
	String userSelectedField = classInfo.userSelectedField;
	if (userSelectedField == null) return null;

	Field[] f;
	Object value="IMPOSSIBLE userSelectedField";

	f = ((Class)this.obj).getFields();
	Arrays.sort(f, comp);
	int len = f.length;
	for (int i = 0; i< len; i++) {
	    String n = f[i].getName().intern();
	    if (n.equals(userSelectedField)) {
		try {value = f[i].get(o);}	// Returns new Integer() &c. FIX SOMEDAY	}
		catch (IllegalAccessException e) { // NARROW!!
		    D.println("userSelectedField cannot access field in " + o + " "+e);
		}
		if (value == null) return "null";
		return value.toString();
	    }
	}
	D.println("userSelectedField cannot find field "+userSelectedField+" in " + o);
	return "IMPOSSIBLE userSelectedField";
    }


    // **************************************** Constructors: new Shadow(...) ****************************************
    public Shadow() {}

    private Shadow(int time, Object o, String[] varNames, Object[] initValues) {
	this(time, o, varNames, initValues, false);
    }

    private Shadow(int time, Object o, String[] varNames, Object[] initValues, String tostring) {
	this(time, o, varNames, initValues, false);
	this.tostring = this.tostringShort = tostring;
    }


    private Shadow(int time, Object o, String[] varNames, Object[] initValues, boolean notForeign) {
	classInfo = ClassInformation.get(o);
	creationTime = time;
	shadowVars = new HistoryList[varNames.length];
	obj = o;
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, Object[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, String[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, int[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	creationTime = time;
	obj = array;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, int[][] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, short[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, byte[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, char[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	creationTime = time;
	obj = array;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, boolean[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, long[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, double[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }

    private Shadow(int time, float[] array, boolean notForeign) {
	classInfo = ClassInformation.get(array);
	obj = array;
	creationTime = time;
	shadowVars = new HistoryList[array.length];
	foreign = !notForeign;
	id = classInfo.nextID();
    }


    private Shadow(int time, MyVector v, boolean notForeign) {
	classInfo = ClassInformation.get(v);
	obj = v;
	creationTime = time;
	shadowVars = new HistoryList[v.size()+10];
	foreign = !notForeign;
	id = classInfo.nextID();
	tostring = tostringShort = "<MyVector_"+id+">";     // WHY do I need this?
    }

    private Shadow(int time, MyArrayList v, boolean notForeign) {
	classInfo = ClassInformation.get(v);
	obj = v;
	creationTime = time;
	shadowVars = new HistoryList[v.size()+10];
	foreign = !notForeign;
	id = classInfo.nextID();
	tostring = tostringShort = "<MyArrayList_"+id+">";     // WHY do I need this?
    }

    public Shadow(int time, MyHashtable v, boolean notForeign) {
        classInfo = ClassInformation.get(v);
        obj = v;
        int i = 0;
        creationTime = time;
        shadowVars = new HistoryList[v.size()+10];
        foreign = !notForeign;
        id = classInfo.nextID();
        tostring = tostringShort = "<MyHashtable_"+id+">";     // WHY do I need this?
    }


    public Shadow(int time, MyHashMap v, boolean notForeign) {
        classInfo = ClassInformation.get(v);
        obj = v;
        int i = 0;
        creationTime = time;
        shadowVars = new HistoryList[v.size()+10];
        foreign = !notForeign;
        id = classInfo.nextID();
        tostring = tostringShort = "<MyHashMap"+id+">";     // WHY do I need this?
    }




    protected boolean isArray() {
	return classInfo.isArray();
    }
    protected boolean isVector() {
	return classInfo.isVector();
    }
    protected boolean isArrayList() {
	return classInfo.isArrayList();
    }
    protected boolean isHashtable() {
        return classInfo.isHashtable();
    }
    protected boolean isHashMap() {
        return classInfo.isHashMap();
    }






    //			Used by SideEffects only

    public static void record(Object o, boolean newTS) {
	if (newTS) TimeStamp.addStamp(0, TimeStamp.OBJECT_IV);// This really wants to be attached to the RL (yet to be recorded!).
	record(o);
    }


    public static void record(Object o) {
	Shadow s = get(o, true);
	HistoryList[] 	shadowVars = s.shadowVars;
	int now = TimeStamp.eott();

	if (o instanceof Object[]) {
	    Object[] array = (Object[]) o;
	    int l = array.length;
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if (hl.getLastValue() != array[i]) s.addToShadowVar(i, now, array[i]);
	    }
	    return;
	}
	if (o instanceof int[]) {
	    int[] array = (int[]) o;
	    int l = array.length;
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if (((ShadowInt)hl.getLastValue()).intValue() != array[i]) s.addToShadowVar(i, now, ShadowInt.createShadowInt(array[i]));
	    }
	    return;
	}
	if (o instanceof long[]) {
	    long[] array = (long[]) o;
	    int l = array.length;
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if (((ShadowLong)hl.getLastValue()).longValue() != array[i]) s.addToShadowVar(i, now, new ShadowLong(array[i]));
	    }
	    return;
	}
	if (o instanceof float[]) {
	    float[] array = (float[]) o;
	    int l = array.length;
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if (((ShadowFloat)hl.getLastValue()).floatValue() != array[i]) s.addToShadowVar(i, now, new ShadowFloat(array[i]));
	    }
	    return;
	}
	if (o instanceof double[]) {
	    double[] array = (double[]) o;
	    int l = array.length;
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if (((ShadowDouble)hl.getLastValue()).doubleValue() != array[i]) s.addToShadowVar(i, now, new ShadowDouble(array[i]));
	    }
	    return;
	}
	if (o instanceof byte[]) {
	    byte[] array = (byte[]) o;
	    int l = array.length;
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if (((ShadowByte)hl.getLastValue()).byteValue() != array[i]) s.addToShadowVar(i, now, ShadowByte.createShadowByte(array[i]));
	    }
	    return;
	}
	if (o instanceof char[]) {
	    char[] array = (char[]) o;
	    int l = array.length;
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if (((ShadowChar)hl.getLastValue()).charValue() != array[i]) s.addToShadowVar(i, now, ShadowChar.createShadowChar(array[i]));
	    }
	    return;
	}
	if (o instanceof short[]) {
	    short[] array = (short[]) o;
	    int l = array.length;
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if (((ShadowShort)hl.getLastValue()).shortValue() != array[i]) s.addToShadowVar(i, now, ShadowShort.createShadowShort(array[i]));
	    }
	    return;
	}
	if (o instanceof MyVector) {
	    MyVector array = (MyVector) o;
	    int l = array.size();
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if ((hl.getLastValue()) != array.elementAt(i)) s.addToShadowVar(i, now, array.elementAt(i));
	    }
	    return;
	}
	if (o instanceof MyArrayList) {
	    MyArrayList array = (MyArrayList) o;
	    int l = array.size();
	    for (int i = 0; i < l; i++) {
		HistoryList hl = s.getShadowVar(i);
		if ((hl.getLastValue()) != array.get(i)) s.addToShadowVar(i, now, array.get(i));
	    }
	    return;
	}

        if (o instanceof MyHashtable) {
            Debugger.println("Not implmented MyHashtable");
            return;
        }

        if (o instanceof MyHashMap) {
            Debugger.println("Not implmented MyHashMap");
            return;
        }



		  
	Field[] f;
	int nFields = 0, index = 0;
	String[] varNames;
	Object[] initValues;
	index = 0;
	Class c = o.getClass();      
	f = c.getFields();

	Arrays.sort(f, comp);
	int len = f.length;
	for (int i = 0; i< len; i++) {
	    if (Modifier.isStatic(f[i].getModifiers())) continue;
	    Object value;
	    try {value = f[i].get(o);}				// Returns new Integer() &c. FIX SOMEDAY	
	    catch (IllegalAccessException e) {			// NARROW!!
		D.println("Shadow.record cannot access field in " + o + " "+e); value = "ODB Error in Shadow.record";
	    }
	    value = convert(value);
	    HistoryList hl = shadowVars[index];
	    if (!value.equals(hl.getLastValue())) hl.add(now, value);		// Use equals() because convert() will call new ShadowInt, etc.
	    index++;
	}
    }






    public String printString() {
	return(printString(100));
    }



    public String printString(int len) {
	if (tostring != null)
	    if (len < 21)
		return tostringShort;
	    else
		return tostring;
	return createPrintString(len);
    }

    public String createPrintString(int len) {
    if (obj instanceof Class) {
	tostring = tostringShort = createClassPrintString();			// -> "int" "String" "int[][]"
	return tostring;
    }

    Class c = obj.getClass();
    String cs = createClassTypePrintString();					// -> "int" "String" "int"

    if (c.isArray()) {
	tostring = tostringShort =  createArrayPrintString(cs);				//  -> "int[3]" "String[2][5][6]"
	return tostring;
    }

    if (obj instanceof Thread) {
	tostring = tostringShort = createThreadPrintString();			// -> "<Thread-4>"	
	return tostring;
    }


    String userSelectedField = classInfo.userSelectedField;
	Object usfValue = null;
    	if ((userSelectedField != null) || (obj instanceof Number) || (obj instanceof Boolean) || (obj instanceof Character)) {
    		if (obj instanceof Number)  usfValue = obj.toString();
    		else if (obj instanceof Character) usfValue = obj.toString() + " " + (int) ((Character)obj).charValue();
		 else if (obj instanceof Boolean) usfValue = obj.toString();
			
		if (userSelectedField != null) 	usfValue = getLastValue(userSelectedField);
    	
	if (usfValue == null) usfValue = "null";
	String usfString;
	if (usfValue instanceof String) 
	    usfString = (String) usfValue;
	else {							
	    // If not String, do something SIMPLE & reasonable. (Someday change to do recursive shadow printString())
	    // Today: <MyObj_23 <MyObj...>>   Someday: <MyObj_23 <MyObj_44>>
	    if (!(usfValue instanceof Class)) {
		usfValue = usfValue.getClass();
		usfString = "<"+usfValue.toString()+"...>";
	    }
	    else
		usfString = usfValue.toString();
	}
	tostring = "<" + cs + "_" + id + " " + usfString + ">";			// <MyObject_123 Jimmy>
	if (tostring.length() < 21)
	    tostringShort = tostring;
	else {
	    if (cs.length() > 7) cs = cs.substring(0, 6);
	    if (usfString.length() > 8) usfString = usfString.substring(0, 8);
	    tostringShort = "<" + cs + ".." + id + " " + usfString + "..>";		// <MyObje..123 Jimm..>
	}
    }
    else {
	tostring = "<" + cs + "_" + id + ">";				// <MyObject_123>
	if (tostring.length() < 21)
	    tostringShort = tostring;
	else {
	    if (cs.length() > 12) cs = cs.substring(0, 12);
	    tostringShort = "<" + cs + ".." + id + ">";	// <MyObje..123>
	}
    }    
    if (len < 21)
	return tostringShort;
    else
	return tostring;
}

    public String createClassPrintString() {					// -> "int" "String" "int[][]"
	String s = ((Class)obj).getName();						// "I" "[I" "[[Ljava.lang.String;"
	String brackets = "";
	if (s.endsWith(";")) s = s.substring(0, s.length()-1);			// "I" "[I" "[[Ljava.lang.String"

	while(s.startsWith("[")) {s = s.substring(1, s.length()); brackets += "[]";}		// "I" "I[]" "Ljava.lang.String[][]"
	if (brackets != "") {
	
	if (s.equals("Z")) return "boolean" + brackets;	// "boolean" "boolean[]"
	if (s.equals("B")) return "byte" + brackets;		// "byte" "byte[]"
	if (s.equals("C")) return "char" + brackets;		// "char" "char[]"
	if (s.equals("S")) return "short" + brackets;		// "short" "short[]"
	if (s.equals("I")) return "int" + brackets;		// "int" "int[]"
	if (s.equals("J")) return "long" + brackets;		// "long" "long[]"
	if (s.equals("F")) return "float" + brackets;		// "float" "float[]"
	if (s.equals("D")) return "double" + brackets;	// "double" "double[]"   
	}
	int i = s.lastIndexOf('.');
	if (i != -1) return s.substring(i+1, s.length())+brackets;		// Trim off packages "Llambda.Debugger.Debugger[]" -> "Debugger[]"
	if (brackets != "") return s.substring(1, s.length())+brackets;		// Trim off "L"  "Lcom.foo.Debugger" -> "Debugger"
	return s;								// Trim off "L"  "LDebugger" -> "Debugger"
    }
	
private String createClassTypePrintString() {					// -> "int" "String" "int"
    String s = obj.getClass().getName();						// "I" "[I" "[[Ljava.lang.String;"
    String brackets = "";
    if (s.endsWith(";")) s = s.substring(0, s.length()-1);			// "I" "[I" "[[Ljava.lang.String"

    while(s.startsWith("[")) {s = s.substring(1, s.length()); brackets += "[]";}		// "I" "I[]" "Ljava.lang.String[][]"
	if (brackets != "") {
    if (s.equals("Z")) return "boolean";
    if (s.equals("B")) return "byte";
    if (s.equals("C")) return "char";
    if (s.equals("S")) return "short";
    if (s.equals("I")) return "int";
    if (s.equals("J")) return "long";
    if (s.equals("F")) return "float";
    if (s.equals("D")) return "double";
	}
    int i = s.lastIndexOf('.');
    if (i != -1) return s.substring(i+1, s.length());				// Trim off packages "Llambda.Debugger.Debugger" -> "Debugger"
    if (brackets != "") return s.substring(1, s.length());		// Trim off "L"  "Lcom.foo.Debugger" -> "Debugger"
    return s;									// Trim off "L"  "LDebugger" -> "Debugger"
}
	
    private String createArrayPrintString(String cs) {				//  -> "int[3]" "String[2][5][6]"
	if (obj instanceof int[][]) {
	    int[][] array = (int[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		int[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = "int[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof int[]) {
	    int[] array = (int[])obj;	// Try out this style.
	    tostring =tostringShort = "int[" + array.length + "]_"+id;
	    return tostring;
	}
	if (obj instanceof short[][]) {
	    short[][] array = (short[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		short[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = "short[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof short[]) {
	    short[] array = (short[])obj;	// Try out this style.
	    tostring =tostringShort = "short[" + array.length + "]_"+id;
	    return tostring;
	}
	if (obj instanceof byte[][]) {
	    byte[][] array = (byte[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		byte[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = "byte[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof byte[]) {
	    byte[] array = (byte[])obj;	// Try out this style.
	    tostring =tostringShort = "byte[" + array.length + "]_"+id;
	    return tostring;
	}
	if (obj instanceof char[][]) {
	    char[][] array = (char[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		char[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = "char[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof char[]) {
	    char[] array = (char[])obj;	// Try out this style.
	    tostring =tostringShort = "char[" + array.length + "]_"+id;
	    return tostring;
	}
	if (obj instanceof boolean[][]) {
	    boolean[][] array = (boolean[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		boolean[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = "boolean[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof boolean[]) {
	    boolean[] array = (boolean[])obj;	// Try out this style.
	    tostring =tostringShort = "boolean[" + array.length + "]_"+id;
	    return tostring;
	}
	if (obj instanceof long[][]) {
	    long[][] array = (long[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		long[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = "long[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof long[]) {
	    long[] array = (long[])obj;	// Try out this style.
	    tostring =tostringShort = "long[" + array.length + "]_"+id;
	    return tostring;
	}
	if (obj instanceof double[][]) {
	    double[][] array = (double[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		double[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = "double[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof double[]) {
	    double[] array = (double[])obj;	// Try out this style.
	    tostring =tostringShort = "double[" + array.length + "]_"+id;
	    return tostring;
	}
	if (obj instanceof float[][]) {
	    float[][] array = (float[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		float[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = "float[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof float[]) {
	    float[] array = (float[])obj;	// Try out this style.
	    tostring =tostringShort = "float[" + array.length + "]_"+id;
	    return tostring;
	}
	if (obj instanceof Object[][]) {
	    Object[][] array = (Object[][])obj;	// Try out this style.
	    int len2=0;
	    if (array.length > 0) {
		Object[] a1 = array[0];
		len2 = (a1==null) ? 0 : a1.length;
	    }
	    tostring =tostringShort = cs+"[" + array.length + "]["+len2+"]_"+id;
	    return tostring;
	}
	if (obj instanceof Object[]) {					// class [Ljava.lang.Object;@49cf9f
	    Object[] array = (Object[])obj;	// Try out this style.
	    tostring =tostringShort = cs+"[" + array.length + "]_"+id;
	    return tostring;
	}
	return cs+"[n][n]?";
    }

private String createThreadPrintString() {
    String s = ((Thread)obj).getName();
    if ((s == null) || (s.length() == 0)) {s = "Unnamed Thread";}
    //if (Character.isDigit(s.charAt(s.length()-1))) 				// Allow <Thread-2> etc.
	//tostring = tostringShort = "<"+s+">";				
    //else
	tostring = tostringShort = "<"+s+"_"+id+">";
    return tostring;
}
    public String toString() {		// This only gets called from JList in ObjectPane & ThisPane
	/*	if (Debugger.NATIVE_TOSTRING)
	    return(TimeStamp.trimNativeToString(obj));
	else
	*/
	if (foreign)
	    return(TimeStamp.trimToLength(obj, 500) + " @");
	else
	    return(TimeStamp.trimToLength(obj, 500));

    }


    public Object getSelectedObject(int x, FontMetrics fm) {
	/*
	  String str =  TimeStamp.trimToLength(obj, MAX_LENGTH_DISPLAY);
	  if (x < fm.stringWidth(str)) return(obj);
	  return(null);
	*/
	return(obj);	// Let 'em click anywhere on the line
    }

    public String toString(int room) {
	if (obj instanceof Shadow) return "<Shadow RECURSIVE TOSTRING(n) CALL "+id+" ??!>";
	return("<Shadow " + TimeStamp.trimToLength(obj, room) + " " + classInfo + " " + ">");
    }


    public static Shadow get(Object o) {return get(o, false);}
	
    public static Shadow get(Object o, boolean discoveryAtZero) {
	if (o == null) {o = "IMPOSSIBLE:NULL OBJECT FOR SHADOW"; Debugger.println("Shadow.get " + o);}
	Shadow s = (Shadow) table.get(o);
	if (s == null) {
	    s = createShadow(o, true);		// Hack for dynamic creation
	}
	return s;			// null is NOT legal
    }

    public static Shadow getNoCreation(Object o) {
	if (o == null) {o = "IMPOSSIBLE:NULL OBJECT FOR SHADOW2 "; Debugger.println("Shadow.get " + o);}
	Shadow s = (Shadow) table.get(o);
	return s;			// null is legal
    }


    public static Shadow getCreateNoDash(Object o) {
	if (o == null) {o = "IMPOSSIBLE:NULL OBJECT FOR SHADOW22 "; Debugger.println("Shadow.get " + o);}
	Shadow s = (Shadow) table.get(o);
	if (s == null) {
	    s = createShadow(o, false);
	}
	return s;			// null is legal
    }

    
    // ADD value TO THE HISTORYLIST OF the array[ index ]  AT file:line
    public int add(int slIndex, int index, Object value, TraceLine tl) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.ONE_D_ARRAY, tl);
	if (!legalSize(index)) {
	    //D.println("Attempting to save an element at an index that wasn't seen in reflection " +index +" "+this);
	    return time;
	}
	addToShadowVar(index, time, value);
	return time;
    }

    // shadow.hashtablePut(sl, key, value);  
    public int hashtableRemove(int slIndex, Object key) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	int index;

	for (index = 0; index < size(); index++) {
	    HistoryListHashtable hlh = (HistoryListHashtable)shadowVars[index]; // Cannot be null
	    Object k = hlh.key;
	    if (k == null) {
		throw new DebuggerException("htput bug " + this);
	    }
	    if (key.equals(k)) {
		addToShadowVar(index, time, Dashes.DASHES);
		return time;
	    }
	}
	return time; // Stupid, but possible
    }

    public void hashtableClear(int slIndex) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	int len = size();

	for (int index = 0; index < len; index++) {
	    HistoryListHashtable hlh = (HistoryListHashtable)shadowVars[index]; // Cannot be null
	    addToShadowVar(index, time, Dashes.DASHES);
	}
    }

    public int hashtablePut(int slIndex, Object key, Object value) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	int index;
	for (index = 0; index < size(); index++) {
	    HistoryListHashtable hlh = (HistoryListHashtable)shadowVars[index]; // Cannot be null
	    Object k = hlh.key;
	    if (k == null) {
		throw new DebuggerException("htput bug " + this);
	    }
	    if (key.equals(k)) {
		addToShadowVar(index, time, value);
		return time;
	    }
	}

	HistoryList[] newHL = new HistoryList[index+10];
	for (int i = 0; i < index; i++) newHL[i] = shadowVars[i];
	shadowVars = newHL;
	HistoryList hl = new HistoryListHashtable(time, value, key);
	setShadowVar(index, hl);
	return time;
    }
  
    // ******************************** VECTORS ********************************

    // ADD value TO THE HISTORYLIST OF the vector[ index ]  AT file:line
    public int vectorChange(int slIndex, int index, Object value) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	if (index >= size()) record(obj);
	addToShadowVar(index, time, value);
	return time;
    }


    // ADD value TO THE HISTORYLIST OF the vector[ index ]  AT file:line
    public int vectorRemove(int slIndex, int index, int range) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	int elementCount = size();
	if (index >= elementCount) {
	    record(obj);
	    //Debugger.println("Impossible vectorRemove "+index); 
	    return time;
	}
	for (int i = index; i < elementCount; i++) {
	    Object value = Dashes.DASHES;
	    if (i+range<elementCount) value = getShadowVar(i+range).getLastValue();
	    if ((value == Dashes.DASHES) && (getShadowVar(i).getLastValue() == Dashes.DASHES)) return time;
	    addToShadowVar(i, time, value);
	}
	return time;
    }


    //	shadow.vectorInsert(sl, index, elementCount, value);
    public int vectorInsert(int slIndex, int index, Object value) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	int elementCount = size();
	if (index > elementCount) {
	    record(obj);
	    //	    Debugger.println("Impossible vectorInsert: "+index+">"+elementCount+" "+this);
	    return time;
	}
	for (int i = elementCount; i > index; i--) {
	    Object v = getShadowVar(i-1).getLastValue();
	    if (v != Dashes.DASHES) addToShadowVarExtend(i, time, v);
	}
	addToShadowVarExtend(index, time, value);
	return time;
    }

    // ******************************** ARRAYLISTS ********************************
    /*
    // ADD value TO THE HISTORYLIST OF the vector[ index ]  AT file:line
    public int arraylistChange(int slIndex, int index, Object value) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	if (index >= size()) record(obj);
	addToShadowVar(index, time, value);
	return time;
    }


    // ADD value TO THE HISTORYLIST OF the arraylist[ index ]  AT file:line
    public int arraylistRemove(int slIndex, int index, int range) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	int elementCount = ((ArrayList)obj).size()+range;
	if (index >= elementCount) {Debugger.println("Impossible arraylistRemove "+index); return time;}
	if (index < 0) {Debugger.println("Impossible arraylistRemove "+index); return time;}
	for (int i = index; i < elementCount; i++) {
	    Object value = Dashes.DASHES;
	    if (i+range<elementCount) value = getShadowVar(i+range).getLastValue();
	    addToShadowVar(i, time, value);
	}
	return time;
    }


    //	shadow.arraylistInsert(sl, index, elementCount, value);
    public int arraylistInsert(int slIndex, int index, Object value) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV);
	if (index == -1) index = this.size();// Append to AL
	int elementCount = ((ArrayList)obj).size()-1;
	if (index > elementCount) {D.println("Impossible arraylistInsert"); return time;}//  Arraylist.insert() will throw 2nd.
	for (int i = elementCount; i > index; i--) {
	    Object v = getShadowVar(i-1).getLastValue();
	    addToShadowVarExtend(i, time, v);
	}
	addToShadowVarExtend(index, time, value);
	return time;
    }

    */


    // Add value to the historylist of varName in 'this' at file:line
    public int add(int slIndex, String varName, Object value, TraceLine tl) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.OBJECT_IV, tl);
	int len = size();
	int i = classInfo.getVarIndex(varName);
	if (i == -1) return time;
	addToShadowVar(i, time, value);
	return time;
    }


    public Object getLastValue(String varName) {
	int time = TimeStamp.eott();
	int len = size();
	int i = classInfo.getVarIndex(varName);
	if (i == -1) return null;
	return(getShadowVar(i).getLastValue());
    }


    public Object getValue(String varName, TimeStamp ts) {
	int len = size();
	int i = classInfo.getVarIndex(varName);
	if (i == -1) return null;
	return(getShadowVar(i).valueOn(ts, foreign));
    }

    public boolean legalSize(int index) {
	return (index < shadowVars.length);
    }

    public int size() {
	if (isHashtable() || isHashMap() || isVector() || isArrayList()) {
	    for (int i = 0; i < shadowVars.length; i++) {
		if (shadowVars[i] == null) return i;
	    }
	}
	return shadowVars.length;
    }

    public void print() {
	System.out.println(this.toString(40));
	/*    int len = size();
	      for (int i=0; i<len; i++) {
	      System.out.println(getVarName(i) + "  \t");
	      getShadowVar(i).print();
	      }
	*/
    }
	


    public static void printAll() {
	System.out.println("\n=====================Shadow Objects=====================");
	Set set = table.entrySet();
	Set setCopy = new HashSet(set);
	Iterator i = setCopy.iterator();		// This will NOT cause ConcurrentModificationException
	while (i.hasNext()) {
	    Object o =  i.next();
	    Shadow s = (Shadow) ((Map.Entry)o).getValue();
	    s.print();
	}
	System.out.println("=====================Shadow Objects=====================\n");
    }
    /*
      public static void main(String[] args) {



      try {
      Object o = new MyObj();

      Class c = o.getClass();
      Field f[] = c.getFields();
      D.println("Obj: " + o + " in " + c + " " + f);
      for (int i = 0; i< f.length; i++) {
      String n = f[i].getName().intern();
      Class fc = f[i].getType();
      Object value = f[i].get(o);
      D.println("Obj: " + o + " in " + c + " " + n + " " + fc + " " + value);
      createShadow(o, true);


      }
      }
      catch (Exception e) { D.println(e); }





      String[] 		vars = {"m", "n", "a"};
      MyObj mo1=new MyObj(), mo2=new MyObj();
      D.println("----------------------Shadow----------------------\n");
      int time1 = TimeStamp.addStamp("foo.x:1");
      int time2 = TimeStamp.addStamp("foo.x:2");
      //Shadow.shadow(ts1, mo1, vars);
      //Shadow.shadow(ts2, mo2, vars);
      printAll();

      TimeStamp a, b, c;
      String file ="/tmp/foo.java";

      Shadow s1 = get(mo1);
      Shadow s2 = get(mo2);
      a=s1.add(file, 1, "m", "<Int 77>");
      s1.add(file, 11, "m", "<Int 775>");
      s1.add(file, 11, "m", "<Int 777>");
      s1.add(file, 1111, "m", "<Int 778>");
      s1.add(file, 12, "n", "<Int 771>");
      b=s1.add(file, 122, "n", "<Int 772>");
      s1.add(file, 1222, "a", "<Int 773>");

      s2.add(file, 1111, "m", "<Int 778>");
      s2.add(file, 12, "n", "<Int 771>");
      s2.add(file, 122, "n", "<Int 772>");
      c=s2.add(file, 1222, "a", "<Int 773>");

      TimeStamp.setCurrentTime(a);
      s1.print();
      s2.print();

      printAll();

      TimeStamp.printAll();

	
      D.println("First: "+s1.getFirstAllVars().toString(0));
      D.println("First: "+s2.getFirstAllVars().toString(0));
      D.println("Last: "+s1.getLastAllVars().toString(0));
      D.println("Last: "+s2.getLastAllVars().toString(0));

      TimeStamp n;
      TimeStamp.setCurrentTime(a);
      n = s1.getNextAllVars();
      if (n == null)
      D.println("Next from "+a+" --");
      else
      D.println("Next from "+a+" "+n.toString(0));
      n = s1.getPreviousAllVars();
      if (n == null)
      D.println("Prev from "+a+" --");
      else
      D.println("Prev from "+a+" "+n.toString(0));

      TimeStamp.setCurrentTime(b);
      n = s1.getNextAllVars();
      if (n == null)
      D.println("Next from "+b+" --");
      else
      D.println("Next from "+b+" "+n.toString(0));
      n = s1.getPreviousAllVars();
      if (n == null)
      D.println("Prev from "+b+" --");
      else
      D.println("Prev from "+b+" "+n.toString(0));

      TimeStamp.setCurrentTime(c);
      n = s1.getNextAllVars();
      if (n == null)
      D.println("Next from "+c+" --");
      else
      D.println("Next from "+c+" "+n.toString(0));
      n = s1.getPreviousAllVars();
      if (n == null)
      D.println("Prev from "+c+" --");
      else
      D.println("Prev from "+c+" "+n.toString(0));

      TimeStamp.setCurrentTime(a);
      D.println("Object toString at: "+a+" " + s1 + " "+ s2);
      TimeStamp.setCurrentTime(b);
      D.println("Object toString at: "+b+" " + s1 + " "+ s2);
      TimeStamp.setCurrentTime(c);
      D.println("Object toString at: "+c+" " + s1 + " "+ s2);
      D.println("----------------------Shadow----------------------\n");		     
      }

    */
}
