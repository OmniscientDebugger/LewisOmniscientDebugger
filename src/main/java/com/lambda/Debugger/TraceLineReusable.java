/*                        TraceLineReusable.java

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


public final class TraceLineReusable extends TraceLine  {
    private Object arg0;
    private Object arg1;
    private Object arg2;
    private Object arg3;
    private Object arg4;
    private Object arg5;
    private Object arg6;
    private Object arg7;
    private Object arg8;
    private Object arg9;
    private int nArgs;
    private int threadIndex;
    public int slIndex;
    public int tlIndex;
    private boolean isUnparented;

    private final static TLStack[]	stacks = new TLStack[TimeStamp.MAX_THREADS];		// <Thread-1> -> <TLStack TLM[100], index>


    public boolean isUnparented() {return isUnparented;}

    public static void assignTimeStamps() {
	for (int i = 0; i < TimeStamp.MAX_THREADS; i++) {
	    TLStack tls = stacks[i];
	    if (tls == null) continue;
	    tls.assignTimeStamps();
	}
    }


    public void addLocals(int slIndex, String methodID, int nLocals) {
	locals.setMethodID(methodID);
	locals.setNLocals(nLocals);
    } // Do nothing!

    public static TraceLineReusable getNextTL(TraceLineReusable tlm) {
	if (tlm != null) return tlm.getNextTL();
	int threadIndex = TimeStamp.getThreadIndex(Thread.currentThread());
	TLStack tls = stacks[threadIndex];
	if (tls == null) {
	    tls = new TLStack();
	    stacks[threadIndex] = tls;
	}
	return tls.getNext();
    }

    public static TraceLineReusable getNextTL(int threadIndex) {
	TLStack tls = stacks[threadIndex];
	if (tls == null) {
	    tls = new TLStack();
	    stacks[threadIndex] = tls;
	}
	return tls.getNext();
    }

    public TraceLineReusable getNextTL() {
	TLStack tls = stacks[threadIndex];
	TraceLineReusable tl = tls.getNext();
	return tl;
    }
    public static TraceLineReusable getCurrentTL() {
	int threadIndex = TimeStamp.getThreadIndex(Thread.currentThread());
	TLStack tls = stacks[threadIndex];
	if (tls == null) {
	    return null;
	}
	return tls.getCurrent();
    }
    public static TraceLineReusable getCurrentTL(int threadIndex) {
	TLStack tls = stacks[threadIndex];
	if (tls == null) {
	    return null;
	}
	return tls.getCurrent();
    }
    public static TraceLineReusable getCurrentTL(int threadIndex, String methodName) {
	TLStack tls = stacks[threadIndex];
	if (tls == null) {
	    return null;
	}
	TraceLineReusable tl = tls.getCurrent();
	if (tl == null) return null;
	if ( (tl.method != methodName) && (! ((methodName == "<init>") && (tl.method == "new"))) ) return null;
	return tl;
    }
    public void popExclusive() {
	TLStack tls = stacks[threadIndex];
	tls.popExclusive(this);
    }
    public void popInclusive() {
	TLStack tls = stacks[threadIndex];
	tls.popInclusive(this);
    }

  public Object getArgActual(int i) {
    if (i == 0) return arg0;
    if (i == 1) return arg1;
    if (i == 2) return arg2;
    if (i == 3) return arg3;
    if (i == 4) return arg4;
    if (i == 5) return arg5;
    if (i == 6) return arg6;
    if (i == 7) return arg7;
    if (i == 8) return arg8;
    if (i == 9) return arg9;
    throw new DebuggerException("getArg(i>MAX) " + i);
  }

  public void putArg(int i, Object value) {
      if (i == 0) {arg0=value; return;}
      if (i == 1) {arg1=value; return;}
      if (i == 2) {arg2=value; return;}
      if (i == 3) {arg3=value; return;}
      if (i == 4) {arg4=value; return;}
      if (i == 5) {arg5=value; return;}
      if (i == 6) {arg6=value; return;}
      if (i == 7) {arg7=value; return;}
      if (i == 8) {arg8=value; return;}
      if (i == 9) {arg9=value; return;}
      throw new DebuggerException("putArg(i>MAX) " + i);
  }


    // Constructors

    public TraceLineReusable(int tlIndex, String meth, Object t, int threadIndex, TraceLine tl,
		       Object a0, Object a1, Object a2, Object a3, Object a4, Object a5,
		       Object a6, Object a7, Object a8, Object a9) {
	super(0, meth, t, threadIndex, tl);
	this.threadIndex = threadIndex;
	this.tlIndex = tlIndex;
	locals = Locals.createLocals(0, this, "NOMETHODYET", 50);
	arg0 = a0;
	arg1 = a1;
	arg2 = a2;
	arg3 = a3;
	arg4 = a4;
	arg5 = a5;
	arg6 = a6;
	arg7 = a7;
	arg8 = a8;
	arg9 = a9;
    }

    // tl is always null.
    public void set0(boolean up, int slIndex, String meth, Object t, TraceLine tl) {
	this.slIndex = slIndex;
	this.method = meth;
	this.thisObj = t;
	this.nArgs = 0;
	this.isUnparented = up;
    }

    public void set4(boolean up, int slIndex, String meth, Object t, TraceLine tl, int nArgs, Object a0, Object a1, Object a2, Object a3) {
	this.slIndex = slIndex;
	this.method = meth;
	this.thisObj = t;
	this.nArgs = nArgs;
	this.isUnparented = up;

	arg0 = a0;
	arg1 = a1;
	arg2 = a2;
	arg3 = a3;
    }

    public void set(boolean up, int slIndex, String meth, Object t, TraceLine tl, int nArgs,
		       Object a0, Object a1, Object a2, Object a3, Object a4, Object a5,
		       Object a6, Object a7, Object a8, Object a9) {
	this.slIndex = slIndex;
	this.method = meth;
	this.thisObj = t;
	this.nArgs = nArgs;
	this.isUnparented = up;
	
	arg0 = a0;
	arg1 = a1;
	arg2 = a2;
	arg3 = a3;
	arg4 = a4;
	arg5 = a5;
	arg6 = a6;
	arg7 = a7;
	arg8 = a8;
	arg9 = a9;
    }


    public static TraceLineReusable setNext0(int slIndex, String meth, Object t, TraceLineReusable tlm0) {
	TraceLineReusable tlm1;
	if (tlm0 == null)
	    tlm1 = getNextTL(tlm0);
	else
	    tlm1 = tlm0.getNextTL();
	tlm1.set0(false, slIndex, meth,  t, tlm0);
	return tlm1;
    }

    public static TraceLineReusable setNext4(int slIndex, String meth, Object t, TraceLineReusable tlm0, int nArgs,
		       Object a0, Object a1, Object a2, Object a3) {
	TraceLineReusable tlm1;
	if (tlm0 == null)
	    tlm1 = getNextTL(tlm0);
	else
	    tlm1 = tlm0.getNextTL();
	tlm1.set4(false, slIndex, meth,  t, tlm0, nArgs, a0,  a1,  a2,  a3);
	return tlm1;
    }

    public static TraceLineReusable setNext(int slIndex, String meth, Object t, TraceLineReusable tlm0, int nArgs,
		       Object a0, Object a1, Object a2, Object a3, Object a4, Object a5,
		       Object a6, Object a7, Object a8, Object a9) {
	TraceLineReusable tlm1;
	if (tlm0 == null)
	    tlm1 = getNextTL(tlm0);
	else
	    tlm1 = tlm0.getNextTL();
	tlm1.set(false, slIndex, meth,  t, tlm0, nArgs, a0,  a1,  a2,  a3,  a4,  a5, a6,  a7,  a8, a9);
	return tlm1;
    }


    public int getThreadIndex() {return threadIndex;}
    public Thread getThread() {return TimeStamp.getThreadFromIndex(threadIndex);}


  public int getArgCount() {
    return(nArgs);
  }


    public String toString() {
	printString = null;
	return super.toString() + " @";
    }

}
