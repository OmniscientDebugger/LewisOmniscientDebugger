/*                        TraceLine.java

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

//              TraceLine/TraceLine.java

/*
 */


import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.reflect.*;


public class TraceLine extends MethodLine {

    static int 					nCollected = 0, nTraceLines, nInstrumented;
    final static boolean			DEBUG = true;
    public static TraceLineAbstractListModel	SINGLETON = new TraceLineAbstractListModel();

    public static VectorD[]			filteredTraceSets = new VectorD[TimeStamp.MAX_THREADS];	// {{thread, traceSet}, ..}
    public static VectorD[]			unfilteredTraceSets = new VectorD[TimeStamp.MAX_THREADS];	  // {{thread, traceSet}, ..}
    public static VectorD[]			filteredTraceSetsAlternate = new VectorD[TimeStamp.MAX_THREADS];  // {{thread, traceSet}, ..}
    public static VectorD[]			unfilteredTraceSetsAlternate = new VectorD[TimeStamp.MAX_THREADS];// {{thread, traceSet}, ..}

    static TraceLine				DEFAULT_TRACELINE = null;
    //static TraceLine				INITIAL_TRACELINE = new TraceLine("Initial.main()");
    public static TraceLine			TOP_TRACELINE = new TraceLine("TOP_TRACELINE");//Never visible
    static int					MAX_ARGS_DISPLAYED = 3;
    static int					OBJECT_PRINT_WIDTH = 20;

    String					method="NO METHOR ERROR";
    public Object				thisObj = "DEFAULT TRACELINE";
    public Locals				locals = null;
    public Object				returnValue = null;
    public MethodLine				returnLine=null;	// A CatchLine can be put here. REFACTOR!
    protected String				printString;

    public boolean isUnparented() {
	if (time < 0) return true;
	return (TimeStamp.getType(time) == TimeStamp.ABSENT);
    }


    public Object getArgActual(int i) {
	throw new DebuggerException("getArg(i>MAX) " + i);
    }

    public Object getArg(int i) {
	Object o = getArgActual(i);
	if (o instanceof HistoryList) {
	    HistoryList hl = (HistoryList) o;
	    return hl.getValue(0);
	}
	return o;
    }


    public String[] getVarNames() {
	if (locals==null) return new String[0];
	return locals.getVarNames();
    }
    public String getVarName(int i) {
	if (locals==null) return "NONAME";
	return locals.getVarName(i);
    }
//    public Class[] getVarTypes() {
//	if (locals==null) return null;
//	return locals.getVarTypes();
//    }
    public Class getVarType(int i) {
	if (locals==null) return null;
	return locals.getVarType(i);
    }
    public Class getReturnType() {
	if (locals==null) return null;
	return locals.getReturnType();
    }

    public void putArg(int i, Object value) {
	throw new DebuggerException("putArg(i>MAX) " + i);
    }

    public void putArg(Object value) {
	String nv = "No_Value";
	int len = getArgCount();
	for (int i = 0; i < len; i++) {
	    Object v = getArg(i);
	    if (v == nv) {
		putArg(i, value);
		return;
	    }
	}
	//      throw new DebuggerException("putArg(i>MAX) ");  NOT A BUG
    }

    public int getArgCount() {
	return(0);
    }


    public static TraceLine getFirstTraceline() {
	TraceLine best = null;
	for (int i = 0; i < TimeStamp.MAX_THREADS; i++) {
	    VectorD v0 = filteredTraceSets[i];		// Why filtered?
	    if ( (v0 == null) || (v0.size() == 0) ) continue;
	    TraceLine tl = (TraceLine)v0.elementAt(0);
	    if (best == null)
		best = tl;
	    else
		if (tl.earlierThan(best)) best = tl;
	}
	return(best);
    }

    public void initializeEvents() {
	int nArgs = getArgCount();
	if (locals == null) return;
	for (int i = 0; i < nArgs; i++) {
	    String varName = locals.getVarName(i);
	    Object o = getArgActual(i);
	    if (!(o instanceof HistoryList)) continue;		// Singletons are just binding -- don't record
	    HistoryList hl = (HistoryList) o;
	    hl.initializeEvents(varName);
	}
	int nVars = locals.getNLocals()-nArgs;
	for (int i = 0; i < nVars; i++) {
	    String varName = locals.getVarName(i+nArgs);
	    Object o = locals.getObject(i);
	    if (!(o instanceof HistoryList)) {		// Singletons are assignments -- do record
		int time = locals.getTime(i);
		if (time == -1) continue;			// Never set
		EventInterface.record(time, null, varName, o);
		continue;
	    }
	    HistoryList hl = (HistoryList) o;
	    hl.initializeEvents(varName);
	}
    }

    public static void initializeAllEvents() {
	for (int i = 0; i < TimeStamp.MAX_THREADS; i++) {
	    VectorD v = unfilteredTraceSets[i];
	    if (v == null) continue;
	    for (int j = 0; j < v.size(); j++) {
		MethodLine ml = (MethodLine)v.elementAt(j);
		if (!(ml instanceof TraceLine)) continue;
		TraceLine tl = (TraceLine) ml;
		tl.initializeEvents();
	    }
	}
    }

    public void addLocals(int slIndex, String methodID, int nLocals) {
	int time = TimeStamp.addStamp(slIndex, TimeStamp.FIRST, this);
	if (locals != null) throw new DebuggerException("addLocals");
	locals = Locals.createLocals(time, this, methodID, nLocals);
    }

    public static void clear() {
	ClassObjectFilter.clear();
	for (int i = 0; i < TimeStamp.MAX_THREADS; i++) {
	    filteredTraceSets[i]= null;	
	    unfilteredTraceSets[i]= null;	
	}
    }

    public static void switchTimeLines(boolean clear) {
	VectorD[] a, b;

	a = filteredTraceSetsAlternate;
	b = unfilteredTraceSetsAlternate;
	filteredTraceSetsAlternate = filteredTraceSets;
	unfilteredTraceSetsAlternate = unfilteredTraceSets;
	filteredTraceSets = a;
	unfilteredTraceSets = b;
	if (clear) {clear();}
    }


    public void compact(int eot) {
	if (locals != null) locals.compact(eot);
	int t = TimeStamp.forward(time);
	if (t == -1)
	    time = 0;
	else
	    time = t;
    }

    public static void compact(Thread t, int eot) {
	int threadIndex = TimeStamp.getThreadIndex(t);
	VectorD traceSet= unfilteredTraceSets[threadIndex];
	if (traceSet == null) return;
	int len = traceSet.size();
	VectorD newTS = new VectorD(len);
	int j = 0;

	for (int i = 0; i < len; i++) {
	    MethodLine ml = (MethodLine)traceSet.elementAt(i);
	    int f = TimeStamp.forward(ml.time);
	    if (f >= 0) {
		ml.unfilteredIndex =newTS.size();
		newTS.add(ml);
		ml.compact(eot);
		continue;
	    }
	    if (ml instanceof TraceLine) {		// This is really dependent on TS.disposable()
		TraceLine tl = (TraceLine) ml;
		MethodLine rl = tl.returnLine;
		if (rl == null) {
		    ml.unfilteredIndex =newTS.size();
		    newTS.add(ml);
		    tl.compact(eot);
		    continue;
		}
		else {
		    int rf = TimeStamp.forward(rl.time);
		    if (rf >= 0) {
			ml.unfilteredIndex =newTS.size();
			newTS.add(ml);
			tl.compact(eot);
			continue;
		    }
		}
	    }
	    /*
	      if (ml instanceof CatchLine) {
	      CatchLine cl = (CatchLine)ml;
	      ml.unfilteredIndex =newTS.size();
	      newTS.add(ml);
	      cl.compact(divider);
	      continue;
	      }
	    */
	    ml.time = -2;
	    nCollected++;
	}
	unfilteredTraceSets[threadIndex] = newTS;
	
    }


    public void verify(int eot) {
	if (locals != null) locals.verify(eot);
	if ( (TimeStamp.getType(time) == TimeStamp.CALL) ||
	     //	     (TimeStamp.getType(time) == TimeStamp.RETURN) ||
	     (TimeStamp.getType(time) == TimeStamp.ABSENT)
	     //	     (TimeStamp.getType(time) == TimeStamp.CATCH)
	     )
	    return;
	throw new DebuggerException("TL.verify() failed on: "+this+ " time: " + time + " found: " + TimeStamp.getTypeString(time));
    }

    public static void verify(Thread t, int eot) {
	int threadIndex = TimeStamp.getThreadIndex(t);
	VectorD traceSet= unfilteredTraceSets[threadIndex];
	if (traceSet == null) return;
	int len = traceSet.size();

	for (int i = 0; i < len; i++) {
	    MethodLine ml = (MethodLine)traceSet.elementAt(i);
	    ml.verify(eot);
	}
    }


    static boolean search(TraceLine tl, String pattern) {
	if (match(tl.printString, pattern)) return true;		// match TL printString: "<LongFooName..1>.get(23,<X_1>) -> "Long.."
	if (match(tl.thisObj, pattern)) return true;			// match "<LongFooNamespace_1>"
	if (match(tl.returnValue, pattern)) return true;		// match "Long windy answer"
	for (int i = 0; i < tl.getArgCount(); i++)
	    if (match(tl.getArg(i), pattern)) return true;			// match "<X_1>" "23"
	return false;
    }

    static boolean search(ReturnLine rl, String pattern) {
	if (match(rl.printString, pattern)) return true;		// match TL printString: "<LongFooName..1>.get(23,<X_1>) -> "Long.."
	if (match(rl.returnValue, pattern)) return true;		// match "Long windy answer"
	return false;
    }

    static boolean search(CatchLine cl, String pattern) {
	if (match(cl.printString, pattern)) return true;		// match TL printString: "Catch -> <NullPointerException_2>"
	return false;
    }

    static boolean match(Object obj, String pattern) {
	String s="";
	if (obj instanceof String)
	    s = (String) obj;
	else {
	    if (obj != null) {
		s = Shadow.get(obj).printString(100);
	    }
	}

	s = Misc.replace(s, "\n", "\\n").toUpperCase();
	if (s.indexOf(pattern) > -1) return true;
	return false;
    }

    public static MethodLine search(int selectedLine, String pattern, boolean forward) {
	int threadIndex = TimeStamp.getThreadIndex(TimeStamp.currentTime().time);
	VectorD traceSet= filteredTraceSets[threadIndex];
	int len = traceSet.size();

	pattern = pattern.toUpperCase();				// no case-sensitive matching

	if (forward) {
	    for (int i = selectedLine; i < len; i++) {
		MethodLine ml = (MethodLine)traceSet.elementAt(i);
		if (ml instanceof TraceLine) {
		    if (search((TraceLine) ml, pattern)) return  ml;
		}
		if (ml instanceof ReturnLine) {
		    if (search((ReturnLine) ml, pattern)) return ml;
		}
		if (ml instanceof CatchLine) {
		    if (search((CatchLine) ml, pattern)) return ml;
		}
	    }
	    return null;
	}
	else {
	    for (int i = selectedLine; i >= 0; i--) {
		MethodLine ml = (MethodLine)traceSet.elementAt(i);
		if (ml instanceof TraceLine) {
		    if (search((TraceLine) ml, pattern)) return ml;
		}
		if (ml instanceof ReturnLine) {
		    if (search((ReturnLine) ml, pattern)) return ml;
		}
		if (ml instanceof CatchLine) {
		    if (search((CatchLine) ml, pattern)) return ml;
		}
	    }
	    return null;
	}
    }

    public String printStringNoSpaces() {
	StringBuffer sb = new StringBuffer();
	return printStringNoSpaces(sb).toString();
    }


    public StringBuffer printStringNoSpaces(StringBuffer sb) {
	sb.append(trimToLength(thisObj, OBJECT_PRINT_WIDTH));
	sb.append(".");
	sb.append(method);
	sb.append("(");
	printArgs(sb);
	sb.append(")");
	return sb;
    }


    // 	****************			new TraceLine(sl, tsIndex, meth, t, a);

    // Constructors

    public TraceLine() {
	this("");	//Invalid TraceLine
    }

    public TraceLine(String s) {
	this.time = -1;
	thisObj = "";	// NOTHING
	method = s;
	traceLine = TOP_TRACELINE;
    }

    public TraceLine(int time, String meth, Object t, int threadIndex, TraceLine tl) {
	this.time = time;
	thisObj = t;
	method = meth;
	if  (tl == null)
	    traceLine = previousTraceLine(threadIndex);
	else
	    traceLine = tl;
    }



    public final int getDepth() {
	int depth = 0;
	TraceLine tl = this;
	while (tl != TOP_TRACELINE) {
	    if (tl == null) break; //{D.println("Impossible getDepth"+this); break;}
	    depth++;
	    tl = tl.traceLine;
	}
	return depth;
    }

    public static ThrowLine getPreviousThrowThisThread() {
	VectorD traceSet = unfilteredTraceSets[TimeStamp.getThreadIndex(Thread.currentThread())];
	if (traceSet == null) return null;
	for (int i = traceSet.size()-1; i > -1; i--) {
	    MethodLine ml = (MethodLine)traceSet.elementAt(i);
	    if (ml instanceof ThrowLine) return((ThrowLine)ml);
	}
	return(null);
    }
	
	
	
    public final static TraceLine previousTraceLine() {		// Never returns null
	return(previousTraceLine(TimeStamp.getThreadIndex(Thread.currentThread())));
    }

    private final static TraceLine previousTraceLine(int threadIndex) {
	VectorD traceSet = unfilteredTraceSets[threadIndex];
	if ( (traceSet == null) || (traceSet.size() == 0) ) return TOP_TRACELINE;
	MethodLine ml = (MethodLine)traceSet.lastElement();
	if (ml instanceof TraceLine) return (TraceLine) ml;
	return ml.traceLine;
    }

    public StackList generateStackList() {
	StackList sl = new StackList();
	TraceLine tl = this;
	while (tl != TOP_TRACELINE) {
	    sl.addLast(tl);
	    tl = tl.traceLine;
	    if (tl == null) {D.println("IMPOSSIBLE generateStackList"+this); return sl;}
	}
	return sl;
    }


    public Object getSelectedObject(int x, FontMetrics fm) {
	int l = getDepth();
	String str=spaces((2*l)-2);		// This seems to work. Why -2?
	if (x < fm.stringWidth(str)) return(null);
	if (isUnparented()) str+="**";
	if (x < fm.stringWidth(str)) return(null);
	str += trimToLength(thisObj, OBJECT_PRINT_WIDTH);
	if (x < fm.stringWidth(str)) return(thisObj);
	str += "."+ method+"(";
	if (x < fm.stringWidth(str)) return(null);

	int max = Math.min(MAX_ARGS_DISPLAYED, getArgCount());
	for (int i = 0; i < max; i++) {
	    str += trimToLength(getArg(i), OBJECT_PRINT_WIDTH);
	    if (i < max - 1) str += ", ";
	    if (x < fm.stringWidth(str)) return(getArg(i));
	}

	if (max < getArgCount())
	    str += ", ...)";
	else
	    str += ")";

	if (returnValue instanceof ShadowException)
	    str += " **** ";
	else
	    str += " -> ";

	

	if (x < fm.stringWidth(str)) return(null);
	str +=  trimToLength(returnValue, OBJECT_PRINT_WIDTH);
	if (x < fm.stringWidth(str)) return(returnValue);
	return(null);
    }


    public static void unfilter() {
	ClassObjectFilter.clear();

	for (int i = 0; i < TimeStamp.MAX_THREADS; i++) {
	    VectorD v0 = unfilteredTraceSets[i];
	    Thread thread = TimeStamp.getThreadFromArray(i);
	    if (v0 == null) continue;
	    VectorD v1 = filter(thread, v0, 1000, false);
	    filteredTraceSets[i]= v1;
	}
    }
	

    public static void refilter() {		// Remove elements from the display.
	refilter(1000);
    }

    public static void refilter(int depthLimit) {		// Remove elements from the display.
	int i = TimeStamp.getThreadIndex(TimeStamp.currentTime().time);
	VectorD v0 = filteredTraceSets[i];
	Thread thread = TimeStamp.getThreadFromArray(i);
	if (v0 == null) return;
	VectorD v1 = filter(thread, v0, depthLimit, true);
	filteredTraceSets[i]= v1;
    }

    public static void filterToDepth(int depthLimit) {
	ClassObjectFilter.clear();

	int i = TimeStamp.getThreadIndex(TimeStamp.currentTime().time);
	VectorD v0 = unfilteredTraceSets[i];
	Thread thread = TimeStamp.getThreadFromArray(i);
	if (v0 == null) return;
	VectorD v1 = filter(thread, v0, depthLimit, false);
	filteredTraceSets[i]= v1;
    }


    public static VectorD filter(Thread thread, VectorD traceSet, int depthLimit, boolean filtered) {
	VectorD displayVectorD = new VectorD();
	int size = traceSet.size();
	int index = 0;
	int firstLineIx = 0, lastLineIx = size-1;

	if (size == 0) return displayVectorD;

	MethodLine firstLine = ClassObjectFilter.getFirst(), lastLine = ClassObjectFilter.getLast();
	if (filtered) {
	    if ((firstLine!=null) && (TimeStamp.getThread(firstLine.time) == thread)) firstLineIx = firstLine.filteredIndex;
	    if ((lastLine!=null)  && (TimeStamp.getThread(lastLine.time) == thread) ) lastLineIx =  lastLine.filteredIndex;
	}    else {
	    if ((firstLine!=null) && (TimeStamp.getThread(firstLine.time) == thread)) firstLineIx = firstLine.unfilteredIndex;
	    if ((lastLine!=null)  && (TimeStamp.getThread(lastLine.time) == thread))  lastLineIx =  lastLine.unfilteredIndex;
	}
      

	for (int i = firstLineIx; i <= lastLineIx; i++) {
	    MethodLine ml = (MethodLine)traceSet.elementAt(i);

	    if (ml.getDepth() > depthLimit) {
		ml.filteredIndex = -1;
		continue;
	    }

	    if (ml instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml;
		Class clazz;
		if (tl.thisObj instanceof Class)
		    clazz = (Class) tl.thisObj;
		else {
		    if (tl.thisObj == null)
			clazz = null;
		    else
			clazz = tl.thisObj.getClass();
		}
		if (ClassObjectFilter.contains(clazz, tl.method)) {
		    boolean filterInternals = ClassObjectFilter.internals(tl.method);		// Just for "Internals"
		    TraceLine tl1;
		    MethodLine ml1, ml2, ml3 = tl.returnLine;
		    if (ml3 == null) {D.println("Badly formed trace"); ml3 = tl.lastMethodLine();}	// Badly formed trace. Filter to eot.
		    if (ml3 instanceof CatchLine) {
			TimeStamp ts = ml3.lookupTS();
			ml2 = ts.getNearestTraceThisThread();
		    }
		    else
			ml2 = ml3;
		    if (ml2.filteredIndex == -1) {			// For RLs which are not in displayList
			tl.filteredIndex = -1;
			continue;
		    }
		    //if (ml2 == null) ml2 = tl.lastMethodLine();	// Badly formed trace. Filter to eot. IMPOSSIBLE (SEE CODE)

		    for (ml1 = tl; ((ml1 != ml2) && (i < size-1)); i++, ml1 =  (MethodLine)traceSet.elementAt(i)) {
			if (filterInternals && ml1 == tl) {tl.filteredIndex = index; displayVectorD.add(ml); index++; continue;}
			ml1.filteredIndex = -1;
		    }		
		    ml2.filteredIndex = -1;
		    continue;
		}
		ml.filteredIndex = index;
		displayVectorD.add(ml);
		index++;
		continue;
	    }

	    if (ml instanceof ReturnLine) {	// DON't display return line right after its TL
		ReturnLine rl = (ReturnLine)ml;
		if (index == 0) continue;	// Don't let an RL be the first
		if (rl.caller == displayVectorD.lastElement()) continue;
		ml.filteredIndex  = index;
		index++;
		displayVectorD.add(ml);
		continue;
	    }

	    if (ml instanceof ThrowLine) {			// Never show throwlines (why?)
		//	ml.filteredIndex = -1;
		ml.filteredIndex = index;
		displayVectorD.add(ml);
		index++;
		continue;
	    }

	    if (ml instanceof CatchLine) {
		ml.filteredIndex = index;
		displayVectorD.add(ml);
		index++;
		continue;
	    }

	    throw new NullPointerException("IMPOSSIBLE: filter() " + ml); 
	    /*
	      ml.filteredIndex = index;		// THIS IS UGLY. FROM METHODLINE & USED BY JLIST
	      displayVectorD.add(ml);
	      index++;
	    */
	}
	return displayVectorD;
    }


    public final MethodLine lastMethodLine() {
	VectorD traceSet = unfilteredTraceSets[TimeStamp.getThreadIndex(time)];
	MethodLine ml = (MethodLine)traceSet.lastElement();
	return ml;
    }


    public final static MethodLine getTrace(int i, Thread thread) {
	VectorD traces = filteredTraceSets[TimeStamp.getThreadIndex(thread)];
	if (i >= traces.size()) return null;
	return((MethodLine)traces.elementAt(i));
    }

    public static MethodLine getTrace(int i) {			// internal only
	return(getTrace(i, Thread.currentThread()));
    }

    public final static MethodLine addTrace(ReturnLine rl) {
	int threadIndex = TimeStamp.getThreadIndex(rl.time);
	VectorD traces = unfilteredTraceSets[threadIndex];
	VectorD filteredTraces = filteredTraceSets[threadIndex];
	if (traces == null) {
	    traces = new VectorD(20);
	    filteredTraces = new VectorD(20);
	    unfilteredTraceSets[threadIndex]= traces;
	    filteredTraceSets[threadIndex]= filteredTraces;
	}
	rl.unfilteredIndex = traces.size();
	traces.add(rl);

	int size = filteredTraces.size();
	if (size == 0) return rl;
	if (rl.caller == filteredTraces.lastElement()) return rl;
	rl.filteredIndex = size;
	filteredTraces.add(rl);
	return rl;
    }


    public final static void removePreviousTLExclusive(TraceLine tl) {
	tl.popExclusive();
    }
    public void popExclusive() { return; }


    public final static void removePreviousTLInclusive(TraceLine tl) {
	tl.popInclusive();
    }
    public void popInclusive() { return; }


    private static boolean COLLECT_STATISTICS = false;
    private static HashMap countTable = new HashMap();
    public static void printCountStats() {
	Iterator iter =  countTable.values().iterator();
	while (iter.hasNext()) {
	    TLCounter tlc = (TLCounter) iter.next();
	    System.out.println(tlc.name +"\t "+ tlc.count);
	}
    }


    public final static void recordStats(TraceLine tl) {
	TLCounter tlc = (TLCounter)countTable.get(tl.method);
	if (tlc == null) {
	    tlc = new TLCounter(tl.method);
	    countTable.put(tl.method, tlc);
	}
	tlc.count++;
    }

    public final static MethodLine addTrace(TraceLine tl) {
	if (COLLECT_STATISTICS) recordStats(tl);
	int threadIndex = tl.getThreadIndex();
	return addTrace(tl, threadIndex);
    }

    public final static MethodLine addTrace(TraceLine tl, int threadIndex) {
	VectorD traces = unfilteredTraceSets[threadIndex];
	VectorD filteredTraces = filteredTraceSets[threadIndex];
	if (traces == null) {
	    traces = new VectorD(20);
	    filteredTraces = new VectorD(20);
	    unfilteredTraceSets[threadIndex]= traces;
	    filteredTraceSets[threadIndex]= filteredTraces;
	}
	tl.unfilteredIndex = traces.size();
	traces.add(tl);

	if ( tl.method == "println" && (tl.thisObj instanceof PrintStream) && (tl.getArgCount() == 1) ) { // "println" must be interned.
	    Object arg0 = tl.getArg(0);
	    String out = "null";
	    if (arg0 != null) out = arg0.toString();			// DANGEROUS!!!!!! calling toString() of what object??
	    ShadowPrintStream.add((PrintStream)tl.thisObj, out);
	}
	if ( (tl.thisObj instanceof Field) && (tl.getArgCount() == 2) ) addTraceField(tl);
	tl.filteredIndex = filteredTraces.size();
	filteredTraces.add(tl);
	return tl;
    }

    private static void addTraceField(TraceLine tl) {
	int slIndex = TimeStamp.getSourceIndex(tl.time);
	String IVName = ((Field)tl.thisObj).getName();
	if (tl.method == "setBoolean") {
	    boolean value = ((ShadowBoolean) tl.getArg(1)).booleanValue();
	    D.changeIVZ(tl.getArg(0), value, slIndex, IVName, tl);
	    return;
	}
	if (tl.method == "setByte") {
	    byte value = ((ShadowByte) tl.getArg(1)).byteValue();
	    D.changeIVB(tl.getArg(0), value, slIndex, IVName, tl);
	    return;
	}
	if (tl.method == "setChar") {
	    char value = ((ShadowChar) tl.getArg(1)).charValue();
	    D.changeIVC(tl.getArg(0), value, slIndex, IVName, tl);
	    return;
	}
	if (tl.method == "setInt") {
	    int value = ((ShadowInt) tl.getArg(1)).intValue();
	    D.changeIVI(tl.getArg(0), value, slIndex, IVName, tl);
	    return;
	}
	if (tl.method == "setLong") {
	    long value = ((ShadowLong) tl.getArg(1)).longValue();
	    D.changeIVL(tl.getArg(0), value, slIndex, IVName, tl);
	    return;
	}
	if (tl.method == "setFloat") {
	    float value = ((ShadowFloat) tl.getArg(1)).floatValue();
	    D.changeIVF(tl.getArg(0), value, slIndex, IVName, tl);
	    return;
	}
	if (tl.method == "setDouble") {
	    double value = ((ShadowDouble) tl.getArg(1)).doubleValue();
	    D.changeIVD(tl.getArg(0), value, slIndex, IVName, tl);
	    return;
	}
	if (tl.method == "set") {
	    Object value = tl.getArg(1);
	    D.changeIVA(tl.getArg(0), value, slIndex, IVName, tl);
	    return;
	}
    }



    public final static MethodLine addTrace(CatchLine cl) {
	int threadIndex = TimeStamp.getThreadIndex(cl.time);
	VectorD traces = unfilteredTraceSets[threadIndex];
	VectorD filteredTraces = filteredTraceSets[threadIndex];
	if (traces == null) {
	    traces = new VectorD(20);
	    filteredTraces = new VectorD(20);
	    unfilteredTraceSets[threadIndex]= traces;
	    filteredTraceSets[threadIndex]= filteredTraces;
	}
	cl.unfilteredIndex = traces.size();
	traces.add(cl);

	int size = filteredTraces.size();
	cl.filteredIndex = size;
	filteredTraces.add(cl);
	return cl;
    }



    public final static MethodLine addTrace(ThrowLine tl) {
	int threadIndex = tl.getThreadIndex();
	VectorD traces = unfilteredTraceSets[threadIndex];
	//VectorD filteredTraces = filteredTraceSets[threadIndex];
	if (traces == null) {
	    traces = new VectorD(20);
	    VectorD filteredTraces = new VectorD(20);
	    unfilteredTraceSets[threadIndex]= traces;
	    filteredTraceSets[threadIndex]= filteredTraces;
	}
	tl.unfilteredIndex = traces.size();
	traces.add(tl);

	//int size = filteredTraces.size();
	//tl.filteredIndex = size;
	//filteredTraces.add(tl);
	return tl;
    }

    public static TraceLine addUnparentedTrace0(int slIndex, Object t,  String meth, TraceLine tl,
					       int nLocals) {
	int threadIndex = TimeStamp.getThreadIndex(Thread.currentThread());
	//TraceLine tl2 = getTraceLineD2(methodID, meth, threadIndex);
	TraceLine tla=null;
	if (D.CHECKING_START) {
	    TraceLineReusable tl1 = TraceLineReusable.getNextTL(threadIndex);
	    tl1.set0(true, slIndex, meth, t, tl);
	    tla = tl1;
	    return tla;
	    //System.out.println("Using: "+tla);
	}
	else {
	    return addUnparentedTrace(slIndex, t, meth, null, nLocals, 0, null, null, null, null, null, null, null, null, null, null);
	}
    }

    public static TraceLine addUnparentedTrace4(int slIndex, Object t,  String meth, TraceLine tl,
					       int nLocals, int nArgs, Object arg0, Object arg1, Object arg2, Object arg3) {
	int threadIndex = TimeStamp.getThreadIndex(Thread.currentThread());
	//TraceLine tl2 = getTraceLineD2(methodID, meth, threadIndex);
	TraceLine tla=null;
	if (D.CHECKING_START) {
	    TraceLineReusable tl1 = TraceLineReusable.getNextTL(threadIndex);
	    tl1.set4(true, slIndex, meth, t, tl, nArgs, arg0, arg1, arg2, arg3);
	    tla = tl1;
	    return tla;
	    //System.out.println("Using: "+tla);
	}
	else {
	    return addUnparentedTrace(slIndex, t, meth, null, nLocals, nArgs, arg0, arg1, arg2, arg3, null, null, null, null, null, null);
	}
    }

    public static TraceLine addUnparentedTrace(int slIndex, Object t,  String meth, TraceLine tl, 
					       int nLocals, int nArgs,
					       Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
					       Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
	int threadIndex = TimeStamp.getThreadIndex(Thread.currentThread());
	TraceLine tla=null;
	if (D.CHECKING_START) {
	    TraceLineReusable tl1 = TraceLineReusable.getNextTL(threadIndex);
	    tl1.set(true, slIndex, meth, t, tl, nArgs, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	    tla = tl1;
	    return tla;
	    //System.out.println("Using: "+tla);
	}
	else {
	    int time;
	    if (tl == null) time = TimeStamp.addStamp(slIndex, TimeStamp.ABSENT, tl);
	    else
		time = TimeStamp.addStampTI(slIndex, TimeStamp.ABSENT, threadIndex);
	    String nv = "No_Value";
	    nArgs = Math.min(nArgs, Debugify.MAX_ARGS_RECORDED);

	    switch (nArgs) {
	    case 0:   tla = new TraceLine0(time, meth, t, threadIndex, tl);  break;
	    case 1:   tla = new TraceLine1(time, meth, t, threadIndex, tl, arg0);  break;
	    case 2:   tla = new TraceLine2(time, meth, t, threadIndex, tl, arg0, arg1);  break;
	    case 3:   tla = new TraceLine3(time, meth, t, threadIndex, tl, arg0, arg1, arg2);  break;
	    case 4:   tla = new TraceLine4(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3);  break;
	    case 5:   tla = new TraceLine5(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4);  break;
	    case 6:   tla = new TraceLine6(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5);  break;
	    case 7:   tla = new TraceLine7(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6);  break;
	    case 8:   tla = new TraceLine8(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);  break;
	    case 9:   tla = new TraceLine9(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);  break;
	    case 10:  tla = new TraceLine10(time,meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);  break;
	    default:  throw new DebuggerException("IMPOSSIBLE"+nArgs);
	    }
	}
	addTrace(tla);
	return tla;
    }

    //
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext0(0, meth, t, (TraceLineReusable) tl);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine0(time, meth, t, threadIndex, tl);
	}

	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext4(0, meth, t, (TraceLineReusable) tl,  1, arg0, null, null, null);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine1(time, meth, t, threadIndex, tl, arg0);
	}

	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext4(0, meth, t, (TraceLineReusable) tl,  2, arg0, arg1, null, null);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine2(time, meth, t, threadIndex, tl, arg0, arg1);
	}

	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1, Object arg2) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext4(0, meth, t, (TraceLineReusable) tl,  3, arg0, arg1, arg2, null);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine3(time, meth, t, threadIndex, tl, arg0, arg1, arg2);
	}
	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1, Object arg2, Object arg3) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext4(0, meth, t, (TraceLineReusable) tl,  4, arg0, arg1, arg2, arg3);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine4(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3);
	}
	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1, Object arg2, Object arg3, Object arg4) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext(0, meth, t, (TraceLineReusable) tl,  5, arg0, arg1, arg2, arg3, arg4, null, null, null, null, null);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine5(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4);
	}
	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext(0, meth, t, (TraceLineReusable) tl,  6, arg0, arg1, arg2, arg3, arg4, arg5, null, null, null, null);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine6(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5);
	}
	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1, Object arg2, Object arg3, Object arg4, Object arg5,
				     Object arg6) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext(0, meth, t, (TraceLineReusable) tl,  7, arg0, arg1, arg2, arg3, arg4, arg5, arg6, null, null, null);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine7(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}
	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1, Object arg2, Object arg3, Object arg4, Object arg5,
				     Object arg6, Object arg7) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext(0, meth, t, (TraceLineReusable) tl,  8, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, null, null);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	tl1 = new TraceLine8(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}
	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1, Object arg2, Object arg3, Object arg4, Object arg5,
				     Object arg6, Object arg7, Object arg8) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext(0, meth, t, (TraceLineReusable) tl,  9, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, null);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine9(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
	}
	addTrace(tl1);
	return tl1;
    }
    public static TraceLine addTrace(int slIndex, Object t,  String meth, TraceLine tl, Object arg0,
				     Object arg1, Object arg2, Object arg3, Object arg4, Object arg5,
				     Object arg6, Object arg7, Object arg8, Object arg9) {
	TraceLine tl1;
	if (D.CHECKING_START){
	    tl1 = TraceLineReusable.setNext(0, meth, t, (TraceLineReusable) tl,  10, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	    return tl1;
	    }
	else {
	    int time = TimeStamp.addStamp(slIndex, TimeStamp.CALL, tl);
	    int threadIndex = TimeStamp.getThreadIndex(time);
	    tl1 = new TraceLine10(time, meth, t, threadIndex, tl, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	}
	addTrace(tl1);
	return tl1;
    }

    //YYYY



    public static TraceLine defaultTraceLine() {
	if (DEFAULT_TRACELINE == null)
	    DEFAULT_TRACELINE = new TraceLine();
	return DEFAULT_TRACELINE;
    }





    public String getMethod() {return method;}

    public static MethodLine getMethodLine(int time) {		// MUST find the ML, otherwise Exception.
	VectorD v =  unfilteredTraceSets[TimeStamp.getThreadIndex(time)];
	MethodLine ml;
	if (v == null) {throw new DebuggerException("IMPOSSIBLE TraceLine.getMethodLine() v==null " + time);}
	int lo = 0, hi = v.size()-1, middle;
	if (hi == -1)  {throw new DebuggerException("EMPTY: TraceLine.getMethodLine() ");}

	while(true) {
	    if ((hi - lo) <= 1) {
		MethodLine mll =(MethodLine)v.elementAt(lo);
		if (mll.time == time) return(mll);
		MethodLine mlh =(MethodLine)v.elementAt(hi);
		if (mlh.time == time) return(mlh);
		throw new DebuggerException("IMPOSSIBLE TraceLine.getMethodLine() not found: " + time + " "+
					    mll+"["+mll.time+"] " +mlh+"["+mlh.time+"]");
	    }

	    middle = lo+((hi-lo)/2);
	    ml = (MethodLine)v.elementAt(middle);
	    if (ml.time == time) return(ml);
	    if (ml.time > time)
		hi = middle;
	    else
		lo = middle;
	}
    }

    /*
    public static TraceLine getTraceLineD2(String methodID, String meth, int threadIndex) {
	VectorD v =  unfilteredTraceSets[threadIndex];
	MethodLine ml;
	if ( (v == null) || (v.size() == 0) ) return null;
	ml =(MethodLine)v.lastElement();
	if (ml instanceof TraceLine) {
	    TraceLine tl = (TraceLine) ml;
	    if (tl.locals != null) return null;
	    if ( (tl.method != meth) && (! ((meth == "<init>") && (tl.method == "new"))) ) return null;
	    return tl;
	}
	return null;
    }
    */
    public static TraceLine getTraceLineD(String methodID, String methodName) {
	int threadIndex = TimeStamp.getThreadIndex(Thread.currentThread());
	if (D.CHECKING_START) return TraceLineReusable.getCurrentTL(threadIndex, methodName);
	VectorD v =  unfilteredTraceSets[threadIndex];
	MethodLine ml;
	if ( (v == null) || (v.size() == 0) ) return null;
	ml =(MethodLine)v.lastElement();
	if (ml instanceof TraceLineReusable) return (TraceLineReusable) ml;				// Assumed correct
	if (ml instanceof TraceLine) {
	    TraceLine tl = (TraceLine) ml;					// is this the RIGHT TL?
	    if (tl.locals != null) return null;					// if set, this can't be the right TL
	    if ( (tl.method != methodName) && (! ((methodName == "<init>") && (tl.method == "new"))) ) return null;
	    return tl;
	}
	return null;
    }

    /*
    public static TraceLine getTraceLineD1() {
	VectorD v =  unfilteredTraceSets[TimeStamp.getThreadIndex(Thread.currentThread())];
	MethodLine ml;
	if ( (v == null) || (v.size() == 0) ) return null;
	ml =(MethodLine)v.lastElement();
	if (ml instanceof TraceLine) {
	    TraceLine tl = (TraceLine) ml;
	    return tl;
	}
	if (ml instanceof ReturnLine) {
	    ReturnLine rl = (ReturnLine) ml;
	    TraceLine tl = rl.traceLine;
	    if (tl == null) System.out.println("D1 saw RL.null "+rl);
	    return tl;
	}
	if (ml instanceof CatchLine) {
	    CatchLine cl = (CatchLine) ml;
	    TraceLine tl = cl.traceLine;
	    if (tl == null) System.out.println("D1 saw CL.null "+cl);
	    return tl;
	}
    
	return null;
    }

    */
  

    // ********************************  NAVIGATION ********************************

    public TraceLine getFirstCall() {
	VectorD v0 = filteredTraceSets[TimeStamp.getThreadIndex(time)];
	int size = v0.size();
	for (int i = 0; i < size; i++) {
	    MethodLine ml = (MethodLine)v0.elementAt(i);
	    if (ml instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml;
		Class c0 = (thisObj instanceof Class) ? (Class) thisObj : thisObj.getClass();
		Class c1 = (tl.thisObj instanceof Class) ? (Class) tl.thisObj : tl.thisObj.getClass();
		if ((tl.method == method) && (c0 == c1)) return(tl);
	    }
	}
	D.println("IMPOSSIBLE TraceLine getFirstCall()" + this);
	return this;
    }
    public TraceLine getNextCall() {
	VectorD v0 = filteredTraceSets[TimeStamp.getThreadIndex(time)];
	int size = v0.size();
	for (int i = filteredIndex+1; i < size; i++) {
	    MethodLine ml = (MethodLine)v0.elementAt(i);
	    if (ml instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml;
		Class c0 = (thisObj instanceof Class) ? (Class) thisObj : thisObj.getClass();
		Class c1 = (tl.thisObj instanceof Class) ? (Class) tl.thisObj : tl.thisObj.getClass();
		if ((tl.method == method) && (c0 == c1)) return(tl);
	    }
	}
	//	D.println("IMPOSSIBLE TraceLine getFirstCall()" + this);  NO. THIS IS OK
	return this;
    }

    public TraceLine getPreviousCall() {
	VectorD v0 = filteredTraceSets[TimeStamp.getThreadIndex(time)];
	int size = v0.size();
	for (int i = filteredIndex-1; i >= 0; i--) {
	    MethodLine ml = (MethodLine)v0.elementAt(i);
	    if (ml instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml;
		Class c0 = (thisObj instanceof Class) ? (Class) thisObj : thisObj.getClass();
		Class c1 = (tl.thisObj instanceof Class) ? (Class) tl.thisObj : tl.thisObj.getClass();
		if ((tl.method == method) && (c0 == c1)) return(tl);
	    }
	}
	//	D.println("IMPOSSIBLE TraceLine getFirstCall()" + this);
	return this;
    }

    public TraceLine getLastCall() {
	VectorD v0 = filteredTraceSets[TimeStamp.getThreadIndex(time)];
	int size = v0.size();
	for (int i = size-1; i >= 0; i--) {
	    MethodLine ml = (MethodLine)v0.elementAt(i);
	    if (ml instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml;
		Class c0 = (thisObj instanceof Class) ? (Class) thisObj : thisObj.getClass();
		Class c1 = (tl.thisObj instanceof Class) ? (Class) tl.thisObj : tl.thisObj.getClass();
		if ((tl.method == method) && (c0 == c1)) return(tl);
	    }
	}
	D.println("IMPOSSIBLE TraceLine getLastCall()" + this);
	return this;
    }
    // Used by returnline for the same purpose

    public TraceLine getNextCall(MethodLine ml) {
	VectorD v0 = filteredTraceSets[TimeStamp.getThreadIndex(time)];
	int size = v0.size();
	for (int i = ml.filteredIndex+1; i < size; i++) {
	    MethodLine ml2 = (MethodLine)v0.elementAt(i);
	    if (ml2 instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml2;
		Class c0 = (thisObj instanceof Class) ? (Class) thisObj : thisObj.getClass();
		Class c1 = (tl.thisObj instanceof Class) ? (Class) tl.thisObj : tl.thisObj.getClass();
		if ((tl.method == method) && (c0 == c1)) return(tl);
	    }
	}
	D.println("IMPOSSIBLE TraceLine getFirstCall()" + this);
	return this;
    }

    public TraceLine getPreviousCall(MethodLine ml) {
	VectorD v0 = filteredTraceSets[TimeStamp.getThreadIndex(time)];
	int size = v0.size();
	for (int i = ml.filteredIndex-1; i >= 0; i--) {
	    MethodLine ml2 = (MethodLine)v0.elementAt(i);
	    if (ml2 instanceof TraceLine) {
		TraceLine tl = (TraceLine) ml2;
		Class c0 = (thisObj instanceof Class) ? (Class) thisObj : thisObj.getClass();
		Class c1 = (tl.thisObj instanceof Class) ? (Class) tl.thisObj : tl.thisObj.getClass();
		if ((tl.method == method) && (c0 == c1)) return(tl);
	    }
	}
	D.println("IMPOSSIBLE TraceLine getFirstCall()" + this);
	return this;
    }





    static public void printStatistics() {
	System.out.println("\n -- TraceLine Statistics -- ");
	System.out.println(" "+"String lengths (TLs+RLs): " + stringLengths);
	System.out.println(" "+"TraceLines collected: "+nCollected);	
	System.out.println("  nArgs\tnumber");
	for (int i = 0; i < sizes.length; i++) {if (sizes[i] > 0) System.out.println("  "+ i + "\t"+sizes[i]);}
	System.out.println("Method \t nCalls");
	printCountStats();
    }




    private void countSizes() {
	int s = getArgCount();
	sizes[s]++;
    }

    private static int[] sizes;
    private static int stringLengths = 0;

    public static void countInstrumentedMethods() {
	nInstrumented=0;		// methods that are instrumented
	sizes = new int[Debugify.MAX_ARGS_RECORDED+1];
	stringLengths = 0;
	nTraceLines = 0;

	for (int ii = 0; ii < TimeStamp.MAX_THREADS; ii++) {
	    VectorD v0 = unfilteredTraceSets[ii];
	    if (v0 == null) continue;
	    for (int i = v0.size()-1; i > -1; i--) {
		MethodLine ml = (MethodLine)v0.elementAt(i);
		if (ml instanceof TraceLine) {
		    nTraceLines++;
		    TraceLine tl = (TraceLine) ml;
		    if (tl.printString != null) stringLengths+=tl.printString.length();
		    tl.countSizes();
		    Locals l = tl.locals;
		    if (l == null) continue;
		    nInstrumented++;
		    l.countSizes();						// Only used by Locals.printStatistics()
		}
		if (ml instanceof ReturnLine) {
		    ReturnLine rl = (ReturnLine) ml;
		    if (rl.printString != null) stringLengths+=rl.printString.length();
		}
	    }
	}
    }

 
    public static void printAll() {
	for (int i = 0; i < TimeStamp.MAX_THREADS; i++) {
	    VectorD v0 = unfilteredTraceSets[i];
	    if (v0 == null) continue;
	    System.out.println("======"+TimeStamp.getThreadFromArray(i)+"======");
	    printAll(v0);
	}
	System.out.println("====== Methods called ======");
    }

    public static void printAll(VectorD methodLines) {
	int end = methodLines.size();
	for (int i = 0 ; i < end; i++) {
	    MethodLine ml = (MethodLine)methodLines.get(i);
	    System.out.println(ml.toString(100));
	    if (DEBUG) {
		//	System.out.println(getTrace(i).tid +" "+ getTrace(i).time +" "+
		//		   getTrace(i).source +" "+ getTrace(i).stack);
		System.out.println("\t\t");
		if (ml instanceof TraceLine) {
		    TraceLine tl = (TraceLine)ml;
		    if (tl.locals == null)
			System.out.println("locals == null");
		    else
			tl.locals.printAll();
		}
	    }
	}
    }


    public String toString() {
	if (printString != null) return(printString);
	try {
	    toString1();
	    return printString;
	}
	catch (Error e) {return "Bug in TL.toString()";}
    }
    
    public String toString1() {
	//      if (true) return "tl";
	String arrow;

	if (returnValue instanceof ShadowException)
	    arrow = " **** ";
	else
	    arrow = " -> ";

	int l = 0;
	if (traceLine != null) l= traceLine.getDepth();
	String rvString;
	if (returnValue == null)
	    {rvString = " ****"; arrow = "";}
	else
	    rvString = trimToLength(returnValue, OBJECT_PRINT_WIDTH);

	StringBuffer sb = new StringBuffer();
	if (l > 0)	sb.append(spaces(2*l));
	if (isUnparented()) sb.append("**");
	printStringNoSpaces(sb);
	sb.append(arrow);
	sb.append(rvString);
	printString = Misc.replace(sb.toString(), "\n", "\\n");
	return printString;
    }

    public String toString(int room) {
	if (room < 50)
	    return("<TraceLine " + time +">");
	if (room < 100)
	    return("<TraceLine " + time +" "+printStringNoSpaces()+">");
	return("<TraceLine " + time + " " + getSourceLine() + " "+getThread(time)+" "+printStringNoSpaces()
	       +" "+traceLine.toString(10)+">"); // +returnLine
    }



    public String printArgs(StringBuffer s) {
	int nArgss = MAX_ARGS_DISPLAYED;
	if (getArgCount() < nArgss) nArgss = getArgCount();

	if (nArgss == 0) return "";
	
	for (int i = 0; i < nArgss-1; i++) {
	    s.append(trimToLength(getArg(i), OBJECT_PRINT_WIDTH));
	    s.append( ", ");
	}
	s.append(trimToLength(getArg(nArgss-1), OBJECT_PRINT_WIDTH));
	if (nArgss < getArgCount()) s.append(", ...");
	return (new String(s));
    }

    public final void addReturnValue(Object rv, ReturnLine rl) {
	returnValue = rv;
	returnLine = rl;
    }


    public final void addReturnValue(Object rv, CatchLine rl) {
	returnValue = rv;
	returnLine = rl;
    }

    /*
      public final int localsAdd(int slIndex, int varIndex, Object value) {
      return locals.add(slIndex, varIndex, value, this);
      }
    */

    public final void localsBind(int varIndex, Object value) {
	locals.bind(varIndex, value, this);
    }
    /*
      public final void localsBind(int nArgs, Object a0, Object a1, Object a2, Object a3, Object a4, Object a5,
      Object a6, Object a7, Object a8, Object a9, Object a10) {
      int time = TimeStamp.addStamp(slIndex, TimeStamp.FIRST, tl);
      if (locals != null) {System.out.println("IMPOSSIBLE12"); return;}
      }
    */
    
    public static void main(String[] args) {
	int i=0, index=0;
	VectorD v1 = new VectorD(100000);
	VectorD v2 = new VectorD(100000);
	TraceLine[] array = new TraceLine[100000];

	System.out.println("----------------------TraceLine----------------------\n");       
	
	try {
	    long start = new Date().getTime();
	    for (i = 0; i < 1000; i++) {
		for (int i1 = 0; i1 < 100; i1++) {
		    TraceLine tl = new TraceLine();
		    //v1.add(tl);
		    //v2.add(tl);
		    array[index++] = tl;
		    if (index == 100) index = 0;
		    //addTrace(tl, 0);
		}
	    }
	    long end = new Date().getTime();   
	    System.out.println("Created 100,000 TLs in: "+ (end-start) + "ms");
	}
	catch (Exception e) {System.out.println(e);}
    
    }

}



class TraceLineAbstractListModel extends AbstractListModel {

    public int getSize() {
      int threadIndex = TimeStamp.getThreadIndex(Debugger.currentThread());
	VectorD traces = TraceLine.filteredTraceSets[threadIndex];
	if (traces == null) {
	    //	    D.println("TraceLine.getSize() needed to create new trace VectorD(). IMPOSSIBLE? "); POSSIBLE
	    TraceLine.unfilteredTraceSets[threadIndex]= traces = new VectorD(20);	// SHOULDN'T BE CALLED ?
	    TraceLine.filteredTraceSets[threadIndex]= new VectorD(20);
	}
	return traces.size();
    }

    public Object getElementAt(int i) {
	return TraceLine.getTrace(i, Debugger.currentThread());
    }
}




class TLCounter {
	int count = 0;
	String name;

	TLCounter(String name) {this.name = name;}
    }
