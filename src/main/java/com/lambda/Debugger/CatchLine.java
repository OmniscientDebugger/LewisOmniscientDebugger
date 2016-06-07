/*                        CatchLine.java

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

import java.awt.FontMetrics;

public class CatchLine extends MethodLine {

    public TraceLine caller;
    public ShadowException exception;
    public ThrowLine throwLine; // Could be null if throw in foreign code
    public String printString = null;

    public CatchLine(int time, ShadowException ex, ThrowLine th, TraceLine tl) {
        this.exception = ex;
        this.traceLine = tl;
        this.throwLine = th;
        this.time = time;
    }

    public void compact(int divider, int eot) {
        if (throwLine != null)
            if (throwLine.time <= divider)
                throwLine = null;
        if (caller != null)
            if (caller.time <= divider)
                caller = null;
        time = TimeStamp.forward(time);
    }

    public void verify(int eot) {
        if (time == 0)
            return;
        if (
        // (TimeStamp.getType(time) == TimeStamp.CALL) ||
        // (TimeStamp.getType(time) == TimeStamp.RETURN)
        // (TimeStamp.getType(time) == TimeStamp.ABSENT) ||
        (TimeStamp.getType(time) == TimeStamp.CATCH))
            return;
        throw new DebuggerException("CL.verify() failed on time:" + time
                + " <CL " + this + "> " + TimeStamp.getTypeString(time));
    }

    public String toString() {
        if (printString == null)
            printString = spaces((traceLine.getDepth() * 2)) + "Catch -> "
                    + trimToLength(exception.value, 30);
        return printString;
    }

    public Object getSelectedObject(int x, FontMetrics fm) {
        int l = traceLine.getDepth();
        String str = spaces((2 * l)) + "Catch -> ";
        if (x < fm.stringWidth(str))
            return (null);
        str += trimToLength(exception.value, 30);
        if (x < fm.stringWidth(str))
            return (exception.value);
        return (null);
    }

    public String toString(int room) {
        if (room < 20)
            return ("<CatchLine " + time + ">");
        if (room < 50)
            return ("<CatchLine " + time + " "
                    + trimToLength(exception.value, 30) + ">");
        if (room < 100)
            return ("<CatchLine " + time + " " + getSourceLine() + " "
                    + getThread() + " " + " "
                    + trimToLength(exception.value, 30) + " "
                    + traceLine.toString(40) + ">");
        return ("<CatchLine " + time + " " + getSourceLine() + " "
                + getThread() + " " + " " + trimToLength(exception.value, 30)
                + " " + traceLine + " " + throwLine + ">");
    }

    public static CatchLine addCatchLine(int slIndex, Throwable ex, TraceLine tl) {
        ThrowLine th = TraceLine.getPreviousThrowThisThread();
        ShadowException se = new ShadowException(ex);
        int time = TimeStamp.addStamp(slIndex, TimeStamp.CATCH, tl);
        CatchLine cl = new CatchLine(time, se, th, tl);
        addCatchToTraces(se, cl);
        TraceLine.addTrace(cl);
        return cl;
    }

    public static void addCatchToTraces(ShadowException ex, CatchLine cl) {
        Thread thread = cl.getThread();
        TraceLine clt = cl.traceLine;
        VectorD traces = TraceLine.unfilteredTraceSets[TimeStamp
                .getThreadIndex(thread)];
        for (int i = traces.size() - 1; i > -1; i--) {
            MethodLine ml = (MethodLine) traces.elementAt(i);
            if (clt == ml)
                return;
            if (ml instanceof ReturnLine) {
                ReturnLine rl = (ReturnLine) ml;
                TraceLine tl2 = rl.traceLine;
                if (tl2 == null)
                    continue;
                int i2 = tl2.unfilteredIndex + 1;
                if (i2 > i) {
                    if (Debugger.DEBUG_DEBUGGER)
                        throw new DebuggerException("IMPOSSIBLE: " + cl + " "
                                + tl2);
                    return;
                }
                continue;
            }
            if (ml instanceof TraceLine) {
                TraceLine tl = (TraceLine) ml;
                if (tl.returnLine == null) {
                    tl.addReturnValue(ex, cl);
                    cl.caller = tl; // The last is the one in Catch's stack
                    // frame.
                }
                TraceLine tl2 = tl.traceLine;
                if (tl2 == null)
                    continue;
                int i2 = tl2.unfilteredIndex + 1;
                if (i2 > i) {
                    if (Debugger.DEBUG_DEBUGGER)
                        throw new DebuggerException("IMPOSSIBLE: " + cl + " "
                                + tl2);
                    return;
                }
                continue;
            }
        }
        return;
    }

    /*
     * public static void main(String[] args) {
     * D.println("----------------------CatchLine----------------------\n");
     * TimeStamp a, b, c; String file = "/tmp/foo.java"; Exception ex = new
     * DebuggerException("My ex");
     * 
     * a = addStamp("Fake:1"); b = addStamp("Fake:1"); c = addStamp("Fake:1");
     * 
     * MyObj obj = new MyObj(); Object[] argsList = {"<Obj 1>", "<Obj 12>", "<Obj
     * 13>"};
     * 
     * TraceLine aa, ab, ba, bb, bc, r1, r2, r3, r4, r5; CatchLine ca; ThrowLine
     * th; TraceLine.addTrace(file, 2, obj , "init", argsList); aa =
     * TraceLine.addTrace(file, 2, obj , "frob", argsList); ab =
     * TraceLine.addTrace(file, 2, obj , "frob", argsList); // ab.addLocal("i"); //
     * ab.addLocal("j"); // ab.addLocal("k"); r1 =
     * ReturnLine.addReturnLine("Falke.t:1", 0, 6, "frob", "<Obj RETURN0>",
     * ab); ba = TraceLine.addTrace("Falke.t:1", obj , "frob1", argsList); bb =
     * TraceLine.addTrace("Falke.t:1", obj , "frob1", argsList); bc =
     * TraceLine.addTrace("Falke.t:1", obj , "frob1", argsList); th =
     * ThrowLine.addThrowLine("Falke.t:1", ex, bc); ca =
     * CatchLine.addCatchLine("Falke.t:1", ex, aa); //r2 =
     * ReturnLine.addReturnLine("Falke.t:1", 0, 10, "frob1", "<Obj RETURN1>",
     * bc); // THROWN OVER //r3 = ReturnLine.addReturnLine("Falke.t:1", 0, 12,
     * "frob1", "<Obj RETURN2>", bb); //r4 =
     * ReturnLine.addReturnLine("Falke.t:1", 0, 13, "frob1", "<Obj RETURN3>",
     * ba); r5 = ReturnLine.addReturnLine("Falke.t:1", 0, 14, "frob", "<Obj
     * RETURN4>", aa); // HAND EDITED TIMES
     * 
     * TimeStamp.printAll(); }
     */
}
