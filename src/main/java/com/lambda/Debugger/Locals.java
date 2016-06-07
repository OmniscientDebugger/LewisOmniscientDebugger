/*                        Locals.java

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

// Locals/Locals.java

/*
 */

import java.util.HashMap;

public abstract class Locals {
    static int MAX_INTERNAL = 10;
    static HashMap varNameTable = new HashMap(); // "com.lambda.Debugger.Debugger.main:15"
    // -> {"i", "j"...}
    static HashMap varTypeTable = new HashMap(); // "com.lambda.Debugger.Debugger.main:15"
    // -> {Demo, ShadowInt, ...} or the string name!
    static HashMap retTypeTable = new HashMap(); // "com.lambda.Debugger.Debugger.main:15"
    // -> Demo or the string name!
    static int nTripletons = 0, nSingletons = 0, nMultitons = 0, nHLists = 0,
            nHLEntries = 0, emptyHLs = 0;
    static int wastedTripletons = 0, wastedMultitons = 0, nLObjects = 0;
    static int nBindings = 0;
    static String[] defaultNames = { "var0", "var1", "var2", "var3", "var4",
            "var5", "var6", "var7",// Only for MyVector?
            "var8", "var9", "vara", "varb", "varc", "vard", "vare", "varf",
            "varg", "varh", "vari", "varj", "vark", "varl", "varm", "varn",
            "varn", "varo", "varp", "varq", "varr", "vars", "vart", "varu" };
    static int[] sizes = new int[30];
    public static Locals DEFAULT = createLocals(0,
            TraceLine.defaultTraceLine(), "unknown", 0);

    String methodID; // "foo.MyObj:frob:3"
    TraceLine tl;
    int creationTime;

    public final void setMethodID(String mid) {
        methodID = mid;
    }

    abstract public int getNLocals();

    public void setNLocals(int nLocals) {
        throw new DebuggerException("Cannot change nLocals if not LocalsMore");
    }

    public VectorD createShadowLocals() {
        VectorD v = new VectorD();
        int nLocals = getNLocals();

        for (int i = 0; i < nLocals; i++) {
            HistoryList hl = getHLCreate(i);
            if (hl == null)
                continue;
            String varName = getVarName(i);
            v.add(new ShadowLocal(varName, hl, i, this));
        }
        return v;
    }

    static void appendVarNames(String methodID, String[] sa) {
        varNameTable.put(methodID, sa);
    }

    // 6/Mar/07 loading the type early causes out-of-order load problems.
    static void appendVarTypes(String methodID, String[] sa,
            String returnTypeString, ClassLoader cl) {
        Object[] varTypes = new Object[sa.length];
        for (int i = 0; i < sa.length; i++) {
            String s = sa[i];
            varTypes[i] = new Object[] { s, cl };// parseType(s, cl);
        }
        varTypeTable.put(methodID, varTypes);
        retTypeTable.put(methodID, new Object[] { returnTypeString, cl });// parseType(returnTypeString,
        // cl));
    }

    public static Class parseType(String s0, ClassLoader cl) {
        Class type = null;
        boolean isArray = false, isArray2 = false;

        if (s0 == null)
            return null;
        String s = s0.replace('/', '.');
        if (s.startsWith("L") && s.endsWith(";")) {
            s = s.substring(1, s.length() - 1);
        } // "Lcom.lambda.Foo;" -> "com.lambda.Foo"

        if (s == "I") {
            type = (isArray2 ? int[][].class : (isArray ? int[].class
                    : int.class));
        } else if (s == "B") {
            type = (isArray2 ? byte[][].class : (isArray ? byte[].class
                    : byte.class));
        } else if (s == "Z") {
            type = (isArray2 ? boolean[][].class : (isArray ? boolean[].class
                    : boolean.class));
        } else if (s == "C") {
            type = (isArray2 ? char[][].class : (isArray ? char[].class
                    : char.class));
        } else if (s == "S") {
            type = (isArray2 ? short[][].class : (isArray ? short[].class
                    : short.class));
        } else if (s == "J") {
            type = (isArray2 ? long[][].class : (isArray ? long[].class
                    : long.class));
        } else if (s == "F") {
            type = (isArray2 ? float[][].class : (isArray ? float[].class
                    : float.class));
        } else if (s == "D") {
            type = (isArray2 ? double[][].class : (isArray ? double[].class
                    : double.class));
        } else if (s == "V") {
            type = ShadowVoid.class;
        } else {
            if (isArray2)
                s = "[[L" + s + ";";
            if (isArray)
                s = "[L" + s + ";"; // System.out.println("CL "+
            // Debugger.classLoader);
            try {
                type = Class.forName(s, true, cl);
            } catch (Exception e) {
                Debugger.println("Locals.parseType() could not find " + s);
            }
        }
        // System.out.println(s0+" "+ s + ":\t\t "+type);
        return type;
    }

    public void compact(int eot) {
        int t = TimeStamp.forward(creationTime);
        if (t == -1)
            creationTime = 0;
        else
            creationTime = t;
        int len = getNLocals();
        for (int i = 0; i < len; i++) {
            HistoryList hl = getHL(i);
            if (hl == null) {
                int varIndex = i - tl.getArgCount();
                if (varIndex < 0)
                    continue; // args are bound w/o a time
                int time0 = getTime(varIndex);
                if (time0 == -1)
                    continue;
                time0 = TimeStamp.forward(time0);
                if (time0 == -1)
                    time0 = 0;
                setTime(varIndex, time0);
            } else
                hl.compact(eot);
        }
    }

    public void verify(int eot) {
        int len = getNLocals();
        for (int i = 0; i < len; i++) {
            HistoryList hl = getHL(i);
            if (hl == null)
                continue;
            hl.verify(eot);
        }
    }

    // constructors

    static public Locals createLocals(int time, TraceLine tl, String methodID,
            int nLocals) {

        int nLocals2 = nLocals - tl.getArgCount();
        if (nLocals2 >= 0)
            // throw new DebuggerException("IMPOSSIBLE: nLocals: " + nLocals
            // + " nArgs: " + tl.getArgCount() + " for: "+ methodID +" on:"+tl);
            nLocals = nLocals2;
        // The correct fix is to repair the BCEL code to handle the 1.5 compiler
        // dumb stuff corrrectly. (The compiler generates entries for the LVT
        // only for the programmer's variables, not "hidden" ones. The 1.4
        // compiler
        // did both and I wrote for that.)
        // This hack just allocates extra space in that case.
        // Are there other forms of this where nLocals2 will be > 0, but still
        // too small?
        switch (nLocals) {
        case 0:
            return new Locals0(time, tl, methodID);
        case 1:
            return new Locals1(time, tl, methodID);
        case 2:
            return new Locals2(time, tl, methodID);
        case 3:
            return new Locals3(time, tl, methodID);
        case 4:
            return new Locals4(time, tl, methodID);
        case 5:
            return new Locals5(time, tl, methodID);
        case 6:
            return new Locals6(time, tl, methodID);
        case 7:
            return new Locals7(time, tl, methodID);
        case 8:
            return new Locals8(time, tl, methodID);
        case 9:
            return new Locals9(time, tl, methodID);
        case 10:
            return new Locals10(time, tl, methodID);
        default:
            return new LocalsMore(time, tl, methodID, nLocals);
        }
    }

    protected Locals(int time, TraceLine tl, String methodID) {
        this.creationTime = time;
        this.tl = tl;
        this.methodID = methodID;
    }

    public String getVarName(int i) {
        String[] varNames = (String[]) varNameTable.get(methodID);
        if (varNames == null)
            varNames = defaultNames;
        if (varNames.length <= i)
            return "MISSING_VAR_NAME";
        return varNames[i];
    }

    public int getVarIndex(String varName) {
        String[] varNames = (String[]) varNameTable.get(methodID);
        if (varNames == null)
            return -1;
        for (int i = 0; i < varNames.length; i++)
            if (varNames[i] == varName)
                return i;
        return -1;
    }

    public String[] getVarNames() {
        String[] varNames = (String[]) varNameTable.get(methodID);
        if (varNames == null)
            varNames = defaultNames;
        return varNames;
    }

    public Class getReturnType() {
        Object type = (Object) retTypeTable.get(methodID);
        if (type instanceof Object[]) {
            Object[] oa = (Object[]) type;
            type = parseType((String) oa[0], (ClassLoader) oa[1]);
            retTypeTable.put(methodID, type);
        }
        return (Class) type;
    }

    public Class getVarType(int i) {
        Object[] varTypes = (Object[]) varTypeTable.get(methodID);
        if (varTypes == null)
            return null;
        if (varTypes.length <= i)
            return null;
        Object o = varTypes[i];
        if (o instanceof Object[]) {
            Object[] oa = (Object[]) o;
            o = parseType((String) oa[0], (ClassLoader) oa[1]);
            varTypes[i] = o;
        }
        return (Class) o;
    }

    // public Class[] getVarTypes() {
    // Class[] varTypes = (Class[]) varTypeTable.get(methodID);
    // if (varTypes == null)
    // return null;
    // return varTypes;
    // }

    public String toString() {
        String s = "[";
        for (int i = 0; i < getNLocals(); i++) {
            s += getVarName(i) + ", ";
        }
        return ("<Locals " + methodID + " " + s + "]>"); // +"
        // "+trace.toString(10)+">");
    }

    public void printAll() {
        for (int i = 0; i < getNLocals(); i++) {
            HistoryList hl = getHL(i);
            System.out.println(hl);
        }
    }

    public void add(int slIndex, int varIndex, Object value, TraceLine tl) {
        int time = TimeStamp.addStamp(slIndex, TimeStamp.LOCAL, tl);

        // LSD.record(varIndex, value, this, time);

        int nArgsTL = tl.getArgCount();
        HistoryList hl;

        if (varIndex < nArgsTL) {
            Object o = tl.getArgActual(varIndex);
            if (o instanceof HistoryList)
                hl = (HistoryList) o;
            else {
                hl = new HistoryListTripleton(creationTime, o); // Assume we're
                // doing an
                // add().
                hl.add(time, value);
                tl.putArg(varIndex, hl);
                return;
            }
            HistoryList hlNew = hl.add(time, value);
            if (hlNew != null)
                tl.putArg(varIndex, hlNew);
            return;
        }

        varIndex -= nArgsTL;
        Object v = getObject(varIndex);
        if (v instanceof HistoryList) {
            hl = (HistoryList) v;
            HistoryList hlNew = hl.add(time, value);
            if (hlNew != null)
                putHL(varIndex, hlNew);
            return;
        }

        int t = getTime(varIndex);
        if (t > -1) {
            hl = new HistoryListTripleton(t, v); // Assume we're doing an
            // add().
            hl.add(time, value);
            putHL(varIndex, hl);
        } else {
            setValue(varIndex, time, value);
        }
        return;
    }

    private HistoryList getHL(int i) { // Only used for GC
        int nArgsTL = tl.getArgCount();
        Object o;

        if (i < nArgsTL)
            o = tl.getArgActual(i);
        else
            o = getObject(i - nArgsTL);

        if (o instanceof HistoryList)
            return (HistoryList) o;
        return null;
    }

    public HistoryList getHLCreate(int varIndex) {
        int nArgsTL = tl.getArgCount();
        if (varIndex < nArgsTL) {
            Object o = tl.getArgActual(varIndex);
            if (o instanceof HistoryList)
                return (HistoryList) o;
            return new HistoryListSingleton(creationTime, o); // Assume we're
            // doing an
            // ShadowLocals
        }

        varIndex = varIndex - nArgsTL;
        Object o = getObject(varIndex);

        if (o instanceof HistoryList)
            return (HistoryList) o;
        int t = getTime(varIndex);
        if (t > -1)
            return new HistoryListSingleton(t, o);
        return null;
    }

    public void bind(int varIndex, Object value, TraceLine tl) {
        int nArgsTL = tl.getArgCount();
        if (varIndex < nArgsTL)
            throw new DebuggerException("Local var unbound IMPOSSIBLE "
                    + varIndex + " " + value);
        varIndex -= nArgsTL;
        setValue(varIndex, creationTime, value);
    }

    abstract void setValue(int varIndex, int time, Object value);

    abstract void putHL(int i, HistoryList hl);

    abstract Object getObject(int i);

    abstract int getTime(int i);

    abstract void setTime(int varIndex, int time);

    public static void main(String[] args) {
        System.out
                .println("----------------------Locals----------------------\n");

        System.out
                .println("----------------------Locals----------------------\n");

    }

    static public void printStatistics() {
        nSingletons = 0;
        nTripletons = 0;
        wastedTripletons = 0;
        wastedMultitons = 0;
        nMultitons = 0;
        sizes = new int[30];
        nHLists = 0;
        nHLEntries = 0;
        nBindings = 0;
        emptyHLs = 0;
        nLObjects = 0;

        TraceLine.countInstrumentedMethods();

        System.out.println("\n -- Locals Statistics -- ");
        System.out.println("Out of " + nLObjects + " Locals with " + nHLists
                + " HistoryLists and " + nHLEntries + " entries...");
        System.out.println(" " + emptyHLs + " empty HLs");
        System.out.println(" " + nBindings + " simple bindings");
        System.out.println(" " + nSingletons + " nSingletons");
        System.out.println(" " + nTripletons + " Tripletons of which wasted: "
                + wastedTripletons);
        System.out.println(" " + nMultitons + " nMultitons, of which wasted: "
                + wastedMultitons);
        System.out.println("  size\tnumber");
        for (int i = 0; i < 10; i++) {
            if (sizes[i] > 0)
                System.out.println("  " + i + "\t" + sizes[i]);
        }
        if (sizes[10] > 0)
            System.out.println("  <20\t" + sizes[10]);
        if (sizes[11] > 0)
            System.out.println("  <30\t" + sizes[11]);
        if (sizes[12] > 0)
            System.out.println("  <40\t" + sizes[12]);
        if (sizes[13] > 0)
            System.out.println("  <50\t" + sizes[13]);
        if (sizes[14] > 0)
            System.out.println("  <60\t" + sizes[14]);
        if (sizes[15] > 0)
            System.out.println("  <100\t" + sizes[15]);
        if (sizes[16] > 0)
            System.out.println("  <200\t" + sizes[16]);
        if (sizes[17] > 0)
            System.out.println("  <400\t" + sizes[17]);
        if (sizes[18] > 0)
            System.out.println("  <800\t" + sizes[18]);
        if (sizes[19] > 0)
            System.out.println("  <1200\t" + sizes[19]);
        if (sizes[20] > 0)
            System.out.println("  <1600\t" + sizes[20]);
        if (sizes[21] > 0)
            System.out.println("  <2k\t" + sizes[21]);
        if (sizes[22] > 0)
            System.out.println("  <4k\t" + sizes[22]);
        if (sizes[23] > 0)
            System.out.println("  <8k\t" + sizes[23]);
        if (sizes[24] > 0)
            System.out.println("  <16k\t" + sizes[24]);
    }

    public void countSizes() {
        nLObjects++;
        for (int i = 0; i < getNLocals(); i++) {
            HistoryList hl = getHL(i);
            nHLists++;
            if (hl == null) {
                emptyHLs++;
                continue;
            }
            int size = hl.size();
            nHLEntries += size;
            nBindings += tl.getArgCount();
            if (hl instanceof HistoryListSingleton)
                nSingletons++;
            if (hl instanceof HistoryListTripleton) {
                nTripletons++;
                wastedTripletons += (3 - hl.size());
            }
            if (hl instanceof HistoryListMultiple) {
                nMultitons++;
                HistoryListMultiple hlm = (HistoryListMultiple) hl;
                wastedMultitons += hlm.wasted();
                if (size < 10)
                    sizes[size]++;
                else if (size < 20)
                    sizes[10]++;
                else if (size < 30)
                    sizes[11]++;
                else if (size < 40)
                    sizes[12]++;
                else if (size < 50)
                    sizes[13]++;
                else if (size < 60)
                    sizes[14]++;
                else if (size < 100)
                    sizes[15]++;
                else if (size < 200)
                    sizes[16]++;
                else if (size < 400)
                    sizes[17]++;
                else if (size < 800)
                    sizes[18]++;
                else if (size < 1200)
                    sizes[19]++;
                else if (size < 1600)
                    sizes[20]++;
                else if (size < 2000)
                    sizes[21]++;
                else if (size < 4000)
                    sizes[22]++;
                else if (size < 8000)
                    sizes[23]++;
                else if (size < 16000)
                    sizes[24]++;
                else
                    sizes[25]++;
            }
        }
    }

}
