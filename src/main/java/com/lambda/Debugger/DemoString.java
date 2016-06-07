/*                        DemoString.java

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



// This is a really doppy way of doing this! (BUT IT WORKS)

public class DemoString {
  static String programString = 
"/* \n" +
"  This is a cute little program which shows off the different things that the \n" +
"  ODB can do. It throws and catches a couple of exceptions, run a quicksort \n" +
"  program (buggy!), and starts up a couple of threads which promptly \n" +
"  (or not-so-promptly) deadlock. Finally, it throws an exception which is \n" +
"  only caught by the ODB itself. \n" +
"  */ \n" +
" \n" +
" \n" +
"package com.lambda.Debugger; \n" +
" \n" +
"import java.util.Hashtable; \n" +
"import java.util.Random; \n" +
"import java.util.Vector;\n" +
" \n" +
"public class Demo { \n" +
" \n" +
"    public static Demo		demo; \n" +
"    public static int		MAX = 20; \n" +
"    public static Vector 	v \n" +
" \n" +
"    public int[]		array; \n" +
" \n" +
"    public Demo			quick; \n" +
"    public char			c = 'X'; \n" +
"    public byte			b = 61; \n" +
" \n" +
"     \n" +
"    public static void badMethod() { \n" +
"	worseMethod(); \n" +
"    } \n" +
"    public static void worseMethod() { \n" +
"	worstMethod(); \n" +
"    } \n" +
"    public static void worstMethod() { \n" +
"	throw new NullPointerException(\"Bad method! Bad, bad, bad!\"); \n" +
"    } \n" +
" \n" +
" \n" +
"    public static void main(String[] argv) { \n" +
" \n" +
"	if (argv.length > 0) MAX = Integer.parseInt(argv[0]); \n" +
" \n" +
"	System.out.println(\"----------------------ODB Demo Program----------------------\"); \n" +
"	v = new Vector(); \n" +
" \n" +
"	try { badMethod(); } \n" +
"	catch (NullPointerException e) { \n" +
"	    System.out.println(\"A badMethod threw: \"+e); \n" +
"	} \n" +
" \n" +
"	System.out.println(\"Starting QuickSort: \" + MAX); \n" +
" \n" +
"	final Demo q = new Demo(); \n" +
"	demo = q; \n" +
"	q.quick = new Demo(); \n" +
"	q.array = new int[MAX]; \n" +
"	q.array[0] = 1; \n" +
"	for (int i=1; i < MAX; i++) q.array[i] = ((i-1)*1233)%1974; \n" +
" \n" +
"	Thread t = new Thread(new DemoRunnable(q, 0, MAX-1), \"Sorter\"); \n" +
"	t.start(); \n" +
"	try {t.join();} catch (InterruptedException ie) {}	// Impossible \n" +
" \n" +
"	System.out.println(\"Done sorting\"); \n" +
"	q.printAll(q); \n" +
" \n" +
"	Hashtable h = new Hashtable(); \n" +
" \n" +
"	for (int i = 0; i < 5; i++) { \n" +
"	    v.add(i+ \" bottles of beer on the wall\"); \n" +
"	    h.put(i+\"th\", i+\" beers\"); \n" +
"	    System.out.println(\"at \" + i + \" -> \" + v.elementAt(i)); \n" +
"	    System.out.println(\"at \" + i + \" -> \" + h.get(i+\"th\")); \n" +
"	} \n" +
" \n" +
"	for (int i = 4; i > 1; i--) { \n" +
"	    v.removeElementAt(i); \n" +
"	    h.put(i+\"th\", \"NO MORE BEER!\"); \n" +
"	} \n" +
" \n" +
" \n" +
" \n" +
"	for (int i = 0; i < 4; i++) { \n" +
"	    v.add(new DemoThing(i)); \n" +
"	}     \n" +
" \n" +
" \n" +
"	DemoWait.doWait(); \n" +
"	DemoDeadLock.deadHead(); \n" +
" \n" +
" \n" +
"	Vector v2 = manipulate(v); \n" +
"	print(v2); \n" +
" \n" +
"	// Demonstrate how exceptions are caught OUTSIDE of main() \n" +
"	throw new NullPointerException(\"Random exception for Debugger.runTarget to catch.\"); \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public static Vector manipulate(Vector v) { \n" +
"	Vector v2 = new Vector(); \n" +
"	for (int i = 0; i < v.size(); i++) { \n" +
"	    Object thing = v.elementAt(i); \n" +
"	    if (thing instanceof DemoThing) { \n" +
"		DemoThing dt = (DemoThing) thing; \n" +
"		String s = dt.name; \n" +
"		v2.add(s); \n" +
"	    } \n" +
"	} \n" +
"	return v2; \n" +
"    } \n" +
" \n" +
" \n" +
"    public static void print(Vector v) { \n" +
"	try { \n" +
"	    for (int i = 0; i < v.size(); i++) { \n" +
"		String s = (String) v.elementAt(i); \n" +
"		if (s.equals(\"Albert\")) continue;		// Don't show Albert! \n" +
"		System.out.println(s); \n" +
"	    } \n" +
"	} \n" +
"	catch (NullPointerException e) { \n" +
"	    System.out.println(\"Demo.print() caught \"+ e); \n" +
"	} \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public void printAll(Demo q) { \n" +
"	int top = MAX; \n" +
"	if (MAX > 100) top=100; \n" +
"	for (int i=0; i < top; i++) { \n" +
"	    System.out.println(i + \"\t \"+ array[i]); \n" +
"	} \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public void sort(int start, int end) { \n" +
"	int i, j, tmp, middle, newEnd; \n" +
" \n" +
" \n" +
"	if ((end - start) < 1) return; \n" +
" \n" +
"	if ((end - start) == 1) { \n" +
"	    if (array[end] < array[start]) { \n" +
"		tmp = array[start]; \n" +
"		array[start] = array[end]; \n" +
"		array[end] = tmp; \n" +
"		return; \n" +
"	    } \n" +
"	    return; \n" +
"	} \n" +
" \n" +
"	middle = average(start, end); \n" +
"	newEnd	 = end; \n" +
" \n" +
"	L: for (i = start; i < newEnd; i++) { \n" +
"	    if (array[i] > middle) { \n" +
"		for (j = newEnd; j > i; j--) { \n" +
"		    if (array[j] < middle) { \n" +
"			tmp = array[i]; \n" +
"			array[i] = array[j]; \n" +
"			array[j] = tmp; \n" +
"			newEnd = j; \n" +
"			continue L; \n" +
"		    } \n" +
"		} \n" +
"		newEnd = j; \n" +
"	    } \n" +
"	} \n" +
" \n" +
"	if (start == newEnd) return; \n" +
" \n" +
"	Thread t = null; \n" +
" \n" +
"	if (((newEnd - start) > 2) || ((end - newEnd) > 2)) { \n" +
"	    t = new Thread(new DemoRunnable(this, start, newEnd-1), \"Sorter\"); \n" +
"	    t.start(); \n" +
"	} \n" +
"	else \n" +
"	    sort(start, newEnd-1); \n" +
" \n" +
"	sort(newEnd, end); \n" +
" \n" +
" \n" +
"	if (t != null) { \n" +
"	    try {t.join();} catch (InterruptedException ie) {}	// Impossible \n" +
"	} \n" +
" \n" +
"	return; \n" +
"    } \n" +
" \n" +
"    public int average(int start, int end) { \n" +
"	int sum = 0; \n" +
"	for (int i = start; i < end; i++) { \n" +
"	    sum += array[i]; \n" +
"	} \n" +
"	return (sum/(end-start)); \n" +
"    } \n" +
" \n" +
"} \n" +
" \n" +
" \n" +
"class DemoWait  implements Runnable { \n" +
" \n" +
"  static void doWait() { \n" +
"    new Thread(new DemoWait(), \"Waiter\").start(); \n" +
"    new Thread(new DemoWait(), \"Waiter\").start(); \n" +
"    try {Thread.sleep(1000);} \n" +
"    catch (InterruptedException ie) {System.out.println(\"InterruptedException thrown?!\"); System.exit(0);} \n" +
"    synchronized (DemoWait.class) {DemoWait.class.notifyAll();} \n" +
"  } \n" +
" \n" +
"  public void run() { \n" +
"    System.out.println(\"Getting lock for DemoWait \" + Thread.currentThread().getName()); \n" +
"    synchronized (DemoWait.class) { \n" +
"      System.out.println(\"Got lock for DemoWait \" + Thread.currentThread().getName()); \n" +
"      try { \n" +
"	System.out.println(\"Waiting in DemoWait \" + Thread.currentThread().getName()); \n" +
"	DemoWait.class.wait(); \n" +
"	System.out.println(\"Waited in DemoWait \" + Thread.currentThread().getName()); \n" +
"      } \n" +
"      catch (InterruptedException ie) {System.out.println(\"InterruptedException thrown?!\"); System.exit(0);} \n" +
"    } \n" +
"    System.out.println(\"Released lock for DemoWait \" + Thread.currentThread().getName()); \n" +
"  } \n" +
" \n" +
"} \n" +
" \n" +
"	 \n" +
"			 \n" +
"class DemoRunnable implements Runnable { \n" +
"    int start, end; \n" +
"    Demo q; \n" +
"     \n" +
" \n" +
"    public DemoRunnable(Demo q, int start, int end) { \n" +
"	this.start = start; \n" +
"	this.end = end; \n" +
"	this.q = q; \n" +
"    } \n" +
" \n" +
"    public void run() { \n" +
"	q.sort(start, end); \n" +
"    } \n" +
"} \n" +
" \n" +
" \n" +
" \n" +
"class DemoDeadLock implements Runnable { \n" +
"  protected static boolean		DEBUG = true; \n" +
"  private static int			MAX = 2; \n" +
"  private static int			nDemoThings = MAX, nCompleted = 0, nThreads = MAX, nSwaps = 50; \n" +
"  private static DemoThing[]		things; \n" +
"  private static Thread[]		threads; \n" +
" \n" +
"  public int				count; \n" +
" \n" +
"  public DemoDeadLock(int i) { \n" +
"    count = i; \n" +
"  } \n" +
" \n" +
"     \n" +
"  public static void deadHead() { \n" +
"    things = new DemoThing[MAX]; \n" +
"    threads = new Thread[MAX]; \n" +
"    for (int i = 0; i<nDemoThings; i++) {things[i] = new DemoThing(i);} \n" +
"    DemoThing dt = (DemoThing) Demo.v.elementAt(4); \n" +
"    dt.name = null; \n" +
"    for (int i = 0; i<nThreads; i++) { \n" +
"      DemoDeadLock dead = new DemoDeadLock(i); \n" +
"      threads[i] = new Thread(dead, \"Hanger\"); \n" +
"      threads[i].start(); \n" +
"    } \n" +
"     \n" +
"    try {Thread.sleep(2000);} \n" +
"    catch (InterruptedException ie) {System.out.println(\"InterruptedException thrown?!\"); System.exit(0);} \n" +
"  } \n" +
" \n" +
" \n" +
"  public void run() { \n" +
"    Random ran = new Random(); \n" +
"    int r1, r2; \n" +
"    try { \n" +
"      for (int i = 0; i<nSwaps; i++) { \n" +
"	r1 = Math.abs(ran.nextInt() % nDemoThings); \n" +
"	r2 = Math.abs(ran.nextInt() % nDemoThings); \n" +
"	DemoThing thing1 = things[count]; \n" +
"	DemoThing thing2 = things[(count+1)%MAX];  \n" +
"	thing1.swap(thing2); \n" +
"      } \n" +
"    } \n" +
"    catch (InterruptedException ie) {System.out.println(\"InterruptedException thrown?!\"); System.exit(0);} \n" +
"  } \n" +
"} \n" +
" \n" +
" \n" +
" \n" +
"class DemoThing { \n" +
"  static String[]	names = {\"Albert\", \"Joshua\", \"Vladimir\", \"Ivan\"}; \n" +
" \n" +
"  int			value; \n" +
"  Random		ran; \n" +
"  String		name; \n" +
" \n" +
" \n" +
"  public DemoThing(int i) { \n" +
"    value = i; \n" +
"    ran = new Random(i); \n" +
"    if (i < names.length) name = names[i]; \n" +
"  } \n" +
" \n" +
"  public String toString() { \n" +
"    return(\"<DemoThing \" +name +\">\"); \n" +
"  } \n" +
" \n" +
" \n" +
"  public  void swap(DemoThing t) throws InterruptedException { \n" +
"    String name = Thread.currentThread().getName(); \n" +
" \n" +
"    if (DemoDeadLock.DEBUG) {System.out.println(name +\"\t \" + this + \".swap(\" + t + \")\");} \n" +
"    synchronized (this) { \n" +
"      Thread.sleep(Math.abs(ran.nextInt() % 200)); \n" +
"      synchronized (t) { \n" +
"	int tmp1 = t.value(); \n" +
"	t.setValue(value); \n" +
"	value = tmp1; \n" +
"      } \n" +
"    } \n" +
"    if (DemoDeadLock.DEBUG) {System.out.println(name + \"\t \" + this + \".swapped(\" + t + \")\");} \n" +
"  } \n" +
" \n" +
" \n" +
"  public  synchronized int value() throws InterruptedException { \n" +
"    return value; \n" +
"  } \n" +
" \n" +
"   \n" +
"  public   synchronized void setValue(int v) throws InterruptedException { \n" +
"    value = v; \n" +
"  } \n" +
" \n" +
"} \n" +
"\n";
}
