/*
  This is a cute little program which shows off the different things that the
  ODB can do. It throws and catches a couple of exceptions, run a quicksort
  program (buggy!), and starts up a couple of threads which promptly
  (or not-so-promptly) deadlock. Finally, it throws an exception which is
  only caught by the ODB itself.
  */


package com.lambda.Debugger;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class Demo {

    public static Demo		demo;
    public static int		MAX = 20;
    public static Vector 	v;

    public int[]		array;

    public Demo			quick;
    public char			c = 'X';
    public byte			b = 61;

    
    public static void badMethod() {
	worseMethod();
    }
    public static void worseMethod() {
	worstMethod();
    }
    public static void worstMethod() {
	throw new NullPointerException("Bad method! Bad, bad, bad!");
    }


    public static void main(String[] argv) {

	if (argv.length > 0) MAX = Integer.parseInt(argv[0]);

	System.out.println("----------------------ODB Demo Program----------------------");
	v = new Vector();

	try { badMethod(); }
	catch (NullPointerException e) {
	    System.out.println("A badMethod threw: "+e);
	}

	System.out.println("Starting QuickSort: " + MAX);

	final Demo q = new Demo();
	demo = q;
	q.quick = new Demo();
	q.array = new int[MAX];
	q.array[0] = 1;
	for (int i=1; i < MAX; i++) q.array[i] = ((i-1)*1233)%1974;

	Thread t = new Thread(new DemoRunnable(q, 0, MAX-1), "Sorter");
	t.start();
	try {t.join();} catch (InterruptedException ie) {}	// Impossible

	System.out.println("Done sorting");
	q.printAll(q);

	Hashtable h = new Hashtable();

	for (int i = 0; i < 5; i++) {
	    v.add(i+ " bottles of beer on the wall");
	    h.put(i+"th", i+" beers");
	    System.out.println("at " + i + " -> " + v.elementAt(i));
	    System.out.println("at " + i + " -> " + h.get(i+"th"));
	}

	for (int i = 4; i > 1; i--) {
	    v.removeElementAt(i);
	    h.put(i+"th", "NO MORE BEER!");
	}



	for (int i = 0; i < 4; i++) {
	    v.add(new DemoThing(i));
	}    


	DemoWait.doWait();
	DemoDeadLock.deadHead();


	Vector v2 = manipulate(v);
	print(v2);

	// Demonstrate how exceptions are caught OUTSIDE of main()
	throw new NullPointerException("Random exception for Debugger.runTarget to catch.");
    }



    public static Vector manipulate(Vector v) {
	Vector v2 = new Vector();
	for (int i = 0; i < v.size(); i++) {
	    Object thing = v.elementAt(i);
	    if (thing instanceof DemoThing) {
		DemoThing dt = (DemoThing) thing;
		String s = dt.name;
		v2.add(s);
	    }
	}
	return v2;
    }


    public static void print(Vector v) {
	try {
	    for (int i = 0; i < v.size(); i++) {
		String s = (String) v.elementAt(i);
		if (s.equals("Albert")) continue;		// Don't show Albert!
		System.out.println(s);
	    }
	}
	catch (NullPointerException e) {
	    System.out.println("Demo.print() caught "+ e);
	}
    }



    public void printAll(Demo q) {
	int top = MAX;
	if (MAX > 100) top=100;
	for (int i=0; i < top; i++) {
	    System.out.println(i + "\t "+ array[i]);
	}
    }



    public void sort(int start, int end) {
	int i, j, tmp, middle, newEnd;


	if ((end - start) < 1) return;

	if ((end - start) == 1) {
	    if (array[end] < array[start]) {
		tmp = array[start];
		array[start] = array[end];
		array[end] = tmp;
		return;
	    }
	    return;
	}

	middle = average(start, end);
	newEnd	 = end;

	L: for (i = start; i < newEnd; i++) {
	    if (array[i] > middle) {
		for (j = newEnd; j > i; j--) {
		    if (array[j] < middle) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
			newEnd = j;
			continue L;
		    }
		}
		newEnd = j;
	    }
	}

	if (start == newEnd) return;

	Thread t = null;

	if (((newEnd - start) > 2) || ((end - newEnd) > 2)) {
	    t = new Thread(new DemoRunnable(this, start, newEnd-1), "Sorter");
	    t.start();
	}
	else
	    sort(start, newEnd-1);

	sort(newEnd, end);


	if (t != null) {
	    try {t.join();} catch (InterruptedException ie) {}	// Impossible
	}

	return;
    }

    public int average(int start, int end) {
	int sum = 0;
	for (int i = start; i < end; i++) {
	    sum += array[i];
	}
	return (sum/(end-start));
    }

}


class DemoWait  implements Runnable {

  static void doWait() {
    new Thread(new DemoWait(), "Waiter").start();
    new Thread(new DemoWait(), "Waiter").start();
    try {Thread.sleep(1000);}
    catch (InterruptedException ie) {System.out.println("InterruptedException thrown?!"); System.exit(0);}
    synchronized (DemoWait.class) {DemoWait.class.notifyAll();}
  }

  public void run() {
    System.out.println("Getting lock for DemoWait " + Thread.currentThread().getName());
    synchronized (DemoWait.class) {
      System.out.println("Got lock for DemoWait " + Thread.currentThread().getName());
      try {
	System.out.println("Waiting in DemoWait " + Thread.currentThread().getName());
	DemoWait.class.wait();
	System.out.println("Waited in DemoWait " + Thread.currentThread().getName());
      }
      catch (InterruptedException ie) {System.out.println("InterruptedException thrown?!"); System.exit(0);}
    }
    System.out.println("Released lock for DemoWait " + Thread.currentThread().getName());
  }

}

	
			
class DemoRunnable implements Runnable {
    int start, end;
    Demo q;
    

    public DemoRunnable(Demo q, int start, int end) {
	this.start = start;
	this.end = end;
	this.q = q;
    }

    public void run() {
	q.sort(start, end);
    }
}



class DemoDeadLock implements Runnable {
  protected static boolean		DEBUG = true;
  private static int			MAX = 2;
  private static int			nDemoThings = MAX, nCompleted = 0, nThreads = MAX, nSwaps = 50;
  private static DemoThing[]		things;
  private static Thread[]		threads;

  public int				count;

  public DemoDeadLock(int i) {
    count = i;
  }

    
  public static void deadHead() {
    things = new DemoThing[MAX];
    threads = new Thread[MAX];
    for (int i = 0; i<nDemoThings; i++) {things[i] = new DemoThing(i);}
    DemoThing dt = (DemoThing) Demo.v.elementAt(4);
    dt.name = null;
    for (int i = 0; i<nThreads; i++) {
      DemoDeadLock dead = new DemoDeadLock(i);
      threads[i] = new Thread(dead, "Hanger");
      threads[i].start();
    }
    
    try {Thread.sleep(2000);}
    catch (InterruptedException ie) {System.out.println("InterruptedException thrown?!"); System.exit(0);}
  }


  public void run() {
    Random ran = new Random();
    int r1, r2;
    try {
      for (int i = 0; i<nSwaps; i++) {
	r1 = Math.abs(ran.nextInt() % nDemoThings);
	r2 = Math.abs(ran.nextInt() % nDemoThings);
	DemoThing thing1 = things[count];
	DemoThing thing2 = things[(count+1)%MAX]; 
	thing1.swap(thing2);
      }
    }
    catch (InterruptedException ie) {System.out.println("InterruptedException thrown?!"); System.exit(0);}
  }
}



class DemoThing {
  static String[]	names = {"Albert", "Joshua", "Vladimir", "Ivan"};

  int			value;
  Random		ran;
  String		name;


  public DemoThing(int i) {
    value = i;
    ran = new Random(i);
    if (i < names.length) name = names[i];
  }

  public String toString() {
    return("<DemoThing " +name +">");
  }


  public  void swap(DemoThing t) throws InterruptedException {
    String name = Thread.currentThread().getName();

    if (DemoDeadLock.DEBUG) {System.out.println(name +"\t " + this + ".swap(" + t + ")");}
    synchronized (this) {
      Thread.sleep(Math.abs(ran.nextInt() % 200));
      synchronized (t) {
	int tmp1 = t.value();
	t.setValue(value);
	value = tmp1;
      }
    }
    if (DemoDeadLock.DEBUG) {System.out.println(name + "\t " + this + ".swapped(" + t + ")");}
  }


  public  synchronized int value() throws InterruptedException {
    return value;
  }

  
  public   synchronized void setValue(int v) throws InterruptedException {
    value = v;
  }

}
