/*                        ThrowLine.java

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

//              ThrowLine.java

/*
 */


import java.io.*;
import java.util.*;


public class ThrowLine extends MethodLine {

    public Throwable exception;



    public ThrowLine(int time, Throwable ex, TraceLine tl) {
	this.exception = ex;
	this.traceLine = tl;
	this.time = time;
    }

    public String toString() {
	return spaces((traceLine.getDepth()*2)-2)+"Throw -> "+exception;
    }

    public String toString(int room) {
	if (room < 20)
	    return("<ThrowLine " + time+">");
	if (room < 50)
	return("<ThrowLine " + time + " " + getSourceLine() +" "+trimToLength(exception,30)+">");
	return("<ThrowLine " + time + " " + getSourceLine() +" "+getThread()+" "+trimToLength(exception,30)+" "+traceLine.toString(50)+">");
    }

    
    public static ThrowLine addThrowLine(int slIndex, Throwable ex, TraceLine tl) {
      int time = TimeStamp.addStamp(slIndex, TimeStamp.THROW, tl);
      ThrowLine rl = new ThrowLine(time, ex,  tl);
      TraceLine.addTrace(rl);
      return rl;
    }

    public void verify(int eot) {
	if ( (TimeStamp.getType(time) == TimeStamp.THROW)	     )
	    return;
	throw new DebuggerException("TL.verify() failed on "+this+TimeStamp.getTypeString(time));
    }

    
    /*
    public static void main(String[] args) {
	D.println("----------------------ThrowLine----------------------\n");
	TimeStamp a, b, c;
	String file = "/tmp/foo.java";
	Exception ex = new DebuggerException("My ex");

	a = addStamp(file, 1);
	b = addStamp("Fake.txt:1");
	c = addStamp(file, 3);

	MyObj obj = new MyObj();
	Object[] argsList = {"<Obj 1>", "<Obj 12>", "<Obj 13>"};
	
	TraceLine aa, ab, ba, bb, bc, r1, r2, r3, r4, r5;
	ThrowLine th;
	TraceLine.addTrace("Fake.txt:1", obj , "init", argsList);
	aa = TraceLine.addTrace("Fake.txt:1", obj , "frob", argsList);
	ab = TraceLine.addTrace("Fake.txt:1", obj , "frob", argsList);
	//	ab.addLocal("i");
	//	ab.addLocal("j");
	//	ab.addLocal("k");
	r1 = ReturnLine.addReturnLine("Fake.txt:1", 0, 6, "frob", "<Obj RETURN0>", ab);
	ba = TraceLine.addTrace("Fake.txt:1", obj , "frob1", argsList);
	bb = TraceLine.addTrace("Fake.txt:1", obj , "frob1", argsList);
	bc = TraceLine.addTrace("Fake.txt:1", obj , "frob1", argsList);
	th = ThrowLine.addThrowLine("Fake.txt:1", ex, bb);
	r2 = ReturnLine.addReturnLine("Fake.txt:1", 0, 10, "frob1", "<Obj RETURN1>", bc);  // Line numbers must be exact!
	r3 = ReturnLine.addReturnLine("Fake.txt:1", 0, 11, "frob1", "<Obj RETURN2>", bb);
	r4 = ReturnLine.addReturnLine("Fake.txt:1", 0, 12, "frob1", "<Obj RETURN3>", ba);
	r5 = ReturnLine.addReturnLine("Fake.txt:1", 0, 13, "frob", "<Obj RETURN4>", aa);

	TimeStamp.printAll();

    }
*/

}
