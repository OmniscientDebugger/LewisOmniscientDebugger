/*                        TLStack.java

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


public final class TLStack  {
    private TraceLineReusable[] 	tls = new TraceLineReusable[40];
    private int			index = 0;				// 0 == empty
    private int			threadIndex;

    public TLStack() {
	TraceLine previousTL = TraceLine.TOP_TRACELINE;
	threadIndex = TimeStamp.getThreadIndex(Thread.currentThread());
	for (int i = 0; i < tls.length; i++) {
	    TraceLineReusable tlm = new TraceLineReusable(i, "NOMETHOD", null, threadIndex, previousTL,
						  null, null, null, null, null, null, null, null, null, null);
		tls[i] = tlm;
		previousTL = tlm;
	    }
    }

    public void assignTimeStamps() {
	for (int i = 0; i < index; i++) {
	    TraceLineReusable tlm = tls[i];
	    int time = TimeStamp.addStampTI(tlm.slIndex, TimeStamp.ABSENT, tlm.getThreadIndex());
	    tlm.time = time;
	    TraceLine.addTrace(tlm);
	    //System.out.println("assigning for "+tlm);
	}
    }

    public TraceLineReusable getNext() {
	if (tls.length == index) {
	    TraceLineReusable previousTL = tls[index-1];
	    TraceLineReusable[] tls2 = new TraceLineReusable[tls.length*2];
	    System.arraycopy(tls, 0, tls2, 0, tls.length);
	    for (int i = tls.length; i < tls2.length; i++) {
		TraceLineReusable tlm =new TraceLineReusable(index-1, "NOMETHOD", null, threadIndex, previousTL,
						     null, null, null, null, null, null, null, null, null, null);
		tls2[i] = tlm;
		previousTL = tlm;
	    }
	    tls = tls2;
	}
	index++;
	return tls[index-1];
    }

    public TraceLineReusable getCurrent() {
	if (index == 0) return null;			// TLS is empty. Should never be called.
	return tls[index-1];
    }


    public void pop() {
	index--;
    }

    public void popExclusive(TraceLineReusable tlm) {		// Toss tlm+1
	index=tlm.tlIndex+1;
    }
    public void popInclusive(TraceLineReusable tlm) {		// Toss tlm
	index=tlm.tlIndex;
    }

    public static void main(String[] args) {
	int repeat = 1000;
	if (args.length > 0) repeat = Integer.parseInt(args[0]);


	TraceLineReusable tlm = TraceLineReusable.getNextTL(null);

	long start = System.currentTimeMillis();

	for (int i = 0; i < repeat; i++) {			// 230ns
	    for (int j = 0; j < 1000; j++) {
		TraceLineReusable tlm2 = tlm.getNextTL();
		tlm2.popInclusive();
	    }
	}

	long end = System.currentTimeMillis();
	long total = (end-start);
	long avePerCall =  (total*1000000)/(repeat*1000);	// ns/loop

	System.out.println("Total: " + total + " Average: "+avePerCall +"ns");
    }

}
