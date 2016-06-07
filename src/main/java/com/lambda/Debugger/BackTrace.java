/*                        BackTrace.java

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

import java.util.*;

public class BackTrace {
    static HashMapEq	roots = new HashMapEq();

    int			timeInclusive, timeExclusive;
    HashMapEq		callees = new HashMapEq();
    Thread		thread;
    String		methodID;

    BackTrace() {}
    BackTrace(Thread t, String mid) {thread=t; methodID = mid;}


    

    static public void test() {
	addTraces();
	printAll();
    }

    static public void addTraces() {
	for (int i = 1; i < Clock.size(); i++) {
	    int delta = Clock.getCK(i);
	    int time = Clock.getTS(i);
	    addTrace(time, delta);
	}
    }

    static public void addTrace(int time, int delta) {
	TraceLine tl0 = TimeStamp.getPreviousBalancedTrace(time);
	List l = new LinkedList();

	for (TraceLine tl1 = tl0; tl1 != TraceLine.TOP_TRACELINE; tl1 = tl1.traceLine) l.add(0, tl1);

	Thread t = TimeStamp.getThread(time);
	HashMapEq hm = (HashMapEq) roots.get(t);
	if (hm == null) {
	    hm = new HashMapEq();
	    roots.put(t, hm);
	}
	    

	for (int i = 0; i < l.size(); i++) {
	    TraceLine tl = (TraceLine) l.get(i);
	    Locals lcl = tl.locals;
	    String methodID;
	    if (lcl == null) 				// Leaf node.
		methodID = tl.method;			// "frob"		INCONSISTANT.!@#$
	    else
		methodID = lcl.methodID;		// "foo.MyObj:frob:3"
	    BackTrace bt = (BackTrace) hm.get(methodID);
	    if (bt == null) {
		bt = new BackTrace(t, methodID);
		hm.put(methodID, bt);
	    }
	    bt.timeInclusive += delta;
	    if (i == l.size()-1) bt.timeExclusive += delta;
	    hm = bt.callees;
	}
    }

    static public void printAll() {
	Iterator iter =  roots.values().iterator();
	while (iter.hasNext()) {
	    HashMapEq hm = (HashMapEq) iter.next();
	    Iterator iter1 =  hm.values().iterator();
	    while (iter1.hasNext()) {
		BackTrace bt = (BackTrace) iter1.next();	
		Thread t = bt.thread;
		System.out.println(t);
		bt.print(1);
	    }
	}
    }

    private void print(int depth) {
	String s = "\t";
	for (int i = 1; i < depth; i++) s+="\t";
	System.out.println(s+this);

	Iterator iter =  callees.values().iterator();
	while (iter.hasNext()) {
	    BackTrace bt = (BackTrace) iter.next();	
	    bt.print(depth+1);
	}
    }

    public String toString() {
	return "<BT " + methodID + " ["+timeInclusive+" "+timeExclusive+"]>";
    }

}
