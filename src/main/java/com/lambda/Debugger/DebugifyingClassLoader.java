/*                        DebugifyingClassLoader.java

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

/*
 * DebugifyingClassLoader.java
 *
 */

package com.lambda.Debugger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class DebugifyingClassLoader extends java.lang.ClassLoader {
    static private PrintStream out = System.out;
    static private VectorD dontInstrument = new VectorD();
    static {
        dontInstrument.add("java.");
        dontInstrument.add("sun.");
        dontInstrument.add("apple.");
        dontInstrument.add("javax.");
        dontInstrument.add("JAVAX.");
        dontInstrument.add("org.apache.bcel");
        dontInstrument.add("com.lambda.Debugger");
        dontInstrument.add("edu.insa.LSD");
        dontInstrument.add("lambda.Debugger");
        dontInstrument.add("insa.LSD");
    }
    static private int depth = 0;
    static private String[] spaces = { "", " ", "   ", "    ", "     ",
            "      ", "       ", "        ", "         ", "          ",
            "           " };

    private static void println(String s) {
        System.out.println(s);
    }

    private static String spaces(int i) {
        if (i < 0)
            return spaces[0];
        if (i < 10)
            return spaces[i];
        return spaces[10];
    }

    private static String spacesPlus() {
        depth++;
        return spaces(depth - 1);
    }

    private static String spacesMinus() {
        depth--;
        return spaces(depth);
    }

    private static String spacesExact() {
        return spaces(depth - 1);
    }

    private boolean dontInstrument(String className) {
        for (int i = 0; i < dontInstrument.size(); i++) {
            if (className.startsWith((String) dontInstrument.elementAt(i)))
                return true;
        }
        return false;
    }

    protected Class loadClass(String className, boolean resolve)
            throws java.lang.ClassNotFoundException {
        Class clazz;
        boolean instrument = true;

        clazz = findLoadedClass(className);
        if (clazz != null)
            return clazz;

        if ((!className.startsWith("javax.xml."))
                && (dontInstrument(className) || (!Debugger.INSTRUMENT))) {
            clazz = getParent().loadClass(className);
            if (Debugger.TRACE_LOADER)
                println("loaded via parent: " + getParent() + " " + className);
            return clazz;
        }

        if (Debugger.TRACE_LOADER)
            println(spacesPlus() + "DebugifyingClassLoader loading:     "
                    + className);
        try {
            clazz = findClass(className, instrument);
        } catch (VerifyError ve) {
            println(spacesMinus() + "The ODB cannot instrument: " + className
                    + ". Please report bug.\n" + ve);
            if (Debugger.TRACE_LOADER)
                ve.printStackTrace();
            clazz = getParent().loadClass(className);
        }

        if (clazz == null)
            throw new java.lang.ClassNotFoundException(className); // return
        // null;
        if (resolve)
            resolveClass(clazz);
        return clazz;
    }

    public static byte[] debugify(String className, byte[] bytes) {
        if (!Debugger.INSTRUMENT)
            return bytes;
        if (Debugger.TRACE_LOADER)
            println("debugifying " + className);

        ClassParser parser = new ClassParser(new ByteArrayInputStream(bytes),
                "<generated>");
        JavaClass javaClass;
        try {
            javaClass = parser.parse();
        } catch (IOException e) {
            System.out.println("IMPOSSIBLE");
            e.printStackTrace();
            return bytes;
        }

        Class clazz;
        if (javaClass == null)
            return null;

        if (Debugger.TRACE_LOADER)
            println(spacesExact() + "DebugifyingClassLoader debugifying: "
                    + className);
        if (Debugger.TRACE_LOADER_STACK)
            (new Exception("Just used to get stack trace")).printStackTrace();
        long start = System.currentTimeMillis();
        javaClass = Debugify.debugifyClass(javaClass, className);
        long end = System.currentTimeMillis();
        Debugger.timeDebugifying += (end - start);
        bytes = javaClass.getBytes();

        return bytes;
    }

    protected Class findClass(String className, boolean instrument) throws ClassNotFoundException {
        JavaClass javaClass = null;
        try {
            javaClass = Repository.lookupClass(className);
        } catch (ClassNotFoundException e) {
            if (Debugger.TRACE_LOADER) e.printStackTrace();
            throw e;//new RuntimeException(e);
        }
        Class clazz;
        if (javaClass == null)
            return null;
        if (!Debugger.USE_BOOTCLASSLOADER) {
            if (Debugger.TRACE_LOADER)
                println(spacesExact() + "DebugifyingClassLoader debugifying: "
                        + className);
            if (Debugger.TRACE_LOADER_STACK)
                (new Exception("Just used to get stack trace"))
                        .printStackTrace();
            long start = System.currentTimeMillis();
            javaClass = Debugify.debugifyClass(javaClass, className);
            long end = System.currentTimeMillis();
            Debugger.timeDebugifying += (end - start);
        }
        byte[] b = javaClass.getBytes();
        clazz = defineClass(className, b, 0, b.length);
        if (Debugger.TRACE_LOADER)
            println(spacesMinus() + "DebugifyingClassLoader loaded:      "
                    + className);
        return clazz;
    }

}
