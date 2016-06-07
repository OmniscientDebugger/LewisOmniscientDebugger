/*                        Debugify.java

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
import java.io.*;
import org.apache.bcel.*;
import org.apache.bcel.verifier.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;

public final class Debugify implements Constants {
    static String version = Debugger.version;
    public static PrintStream outputStream = System.out;
    public static final int MAX_ARGS_RECORDED = 10;
    private static InstructionFactory factory;
    private static final int MAX_SOURCE_LINES = 8000;
    // Max that a single method can store
    private static boolean isSynchronized = false;
    private static boolean replacingVector;
    private static boolean calledFromDebugify = false;
    private static boolean processedCLINIT = false;
    private static ConstantPoolGen cpg;
    // private static int out; // reference to D
    // private static int printlnString; // reference to PrintStream.println
    private static int toString; // reference to PrintStream.println
    private static Type traceLine, typeClass, typeClassLoader, typeShadowInt,
            typeShadowShort, typeShadowByte;
    private static Type typeShadowChar, typeShadowBoolean, typeShadowFloat,
            typeShadowLong;
    private static Type typeShadowDouble, typeShadowClass, typeCollection;
    private static String sourceFileName;
    static boolean SILENT = true;
    private static boolean DEBUG = false;
    static Code code = null;
    static String name = null;
    static LineNumberTable lineNumberTable = null;
    static LocalVariable[] localVariables;
    static boolean OneObj = false;
    static Type type = null;
    static Type returnType = null;
    static MethodGen mg = null;
    static InstructionList il = null;
    static InstructionHandle[] ihs = null;
    static InstructionList patch = null;
    static LocalVariableGen lg = null;
    static LocalVariableGen lg2 = null;
    static LocalVariableGen[] lvgs;
    static int tl2 = 0;
    static int tl = 0;
    static int flags = 0;
    static int D_new_myvector = 0;
    static int D_invoke = 0;
    static int D_athrow = 0;
    static int D_catch = 0;
    static int D_getPreviousTL = 0;
    static int D_addUnparented0 = 0;
    static int D_addUnparented1 = 0;
    static int D_addUnparented2 = 0;
    static int D_addUnparented3 = 0;
    static int D_addUnparented4 = 0;
    static int D_addUnparented5 = 0;
    static int D_addUnparented6 = 0;
    static int D_addUnparented7 = 0;
    static int D_addUnparented8 = 0;
    static int D_addUnparented9 = 0;
    static int D_addUnparented10 = 0;
    // static int D_blankTrace = 0;
    // static int D_change = 0;
    static int D_changeA = 0, D_changeI = 0, D_changeIvoid = 0, D_changeL = 0,
            D_changeB = 0, D_changeC = 0;
    static int D_changeS = 0, D_changeF = 0, D_changeZ = 0, D_changeD = 0;
    static int D_bind = 0;
    static int D_newArray = 0;
    static int D_changeIV = 0;
    static int D_changeIVA = 0, D_changeIVB = 0, D_changeIVC = 0,
            D_changeIVS = 0;
    static int D_changeIVI = 0, D_changeIVL = 0, D_changeIVF = 0,
            D_changeIVD = 0, D_changeIVZ = 0;
    static int D_createShadowClass = 0;
    static int D_createShadowClass1 = 0;
    static int D_createShadowInt = 0;
    static int D_createShadowShort = 0;
    static int D_createShadowByte = 0;
    static int D_createShadowChar = 0;
    static int D_createShadowBoolean = 0;
    static int D_changeArrayA = 0, D_changeArrayZ = 0, D_changeArrayB = 0,
            D_changeArrayC = 0;
    static int D_changeArrayS = 0, D_changeArrayI = 0, D_changeArrayL = 0,
            D_changeArrayF = 0, D_changeArrayD = 0;
    static int D_createShadowLong = 0;
    static int D_createShadowFloat = 0;
    static int D_createShadowDouble = 0;
    static int D_returnValue_0 = 0;
    static int D_returnValueA = 0;
    static int D_returnValueB = 0, D_returnValueC = 0, D_returnValueS = 0,
            D_returnValueI = 0;
    static int D_returnValueL = 0, D_returnValueF = 0, D_returnValueD = 0,
            D_returnValueZ = 0;
    static int D_returnNew = 0;
    static int D_exit = 0;
    static int D_invoke_0 = 0;
    static int D_invoke_1 = 0;
    static int D_invoke_2 = 0;
    static int D_invoke_3 = 0;
    static int D_invoke_4 = 0;
    static int D_invoke_5 = 0;
    static int D_invoke_6 = 0;
    static int D_invoke_7 = 0;
    static int D_invoke_8 = 0;
    static int D_invoke_9 = 0;
    static int D_invoke_10 = 0;
    static int D_newObj_0 = 0;
    static int D_newObj_1 = 0;
    static int D_newObj_2 = 0;
    static int D_newObj_3 = 0;
    static int D_newObj_4 = 0;
    static int D_newObj_5 = 0;
    static int D_newObj_6 = 0;
    static int D_newObj_7 = 0;
    static int D_newObj_8 = 0;
    static int D_newObj_9 = 0;
    static int D_newObj_10 = 0;
    // static int D_init = 0;
    static int D_returnMarker_0 = 0;
    static int D_returnMarker_1 = 0;

    static int D_gettingLock = 0;
    static int D_gotLock = 0;
    static int D_releasingLock = 0;
    static int D_startingWait = 0;
    static int D_endingWait = 0;
    static int D_startingJoin = 0;
    static int D_endingJoin = 0;
    static ArrayType stringArray = new ArrayType(ObjectType.STRING, 1);
    static ObjectType methodType = new ObjectType(
            "de.fub.bytecode.classfile.Method");
    static ArrayType methodArray = new ArrayType(methodType, 1);

    static int nMethods = 0;
    static int line = 0;
    static Instruction ins = null;
    static boolean PUBLIC_ONLY = false;
    static boolean NO_PUTFIELD = false, NO_PUTSTATIC = false,
            NO_ISTORE = false, NO_IINC = false, NO_IASTORE = false,
            NO_RETURN = false, NO_RETURNVALUE = false,
            NO_INVOKEVIRTUAL = false, NO_ARGUMENTS = true, NO_ASTORE = false,
            NO_ATHROW = false, NO_CATCH = false, NO_INVOKESTATIC = false,
            NO_NEW = false, NO_AASTORE = false, NO_PREVIOUS = false,
            NO_LOCKS = false, NO_WAITS = false, DONT_REPLACE_VECTOR = false;
    static VectorD dontRecord, dontInstrument, instrumentOnlyPackages;
    static CodeExceptionGen[] ceg;
    static String classPackageName, className, classNoNumbers;

    // java -DPUTFIELD -DISTORE -DIINC -DIASTORE -DRETURN -DRETURNVALUE
    // -DINVOKEVIRTUAL -DASTORE -DATHROW -DCATCH -DARGUMENTS -DINVOKESTATIC
    // Debugify
    // -DATHROW -DCATCH -DASTORE -DAASTORE -DIASTORE -DRETURN -DRETURNVALUE
    // -DINVOKEVIRTUAL -DIINC -DISTORE -DPUTFIELD -DPUTSTATIC -DINVOKESTATIC
    // -DARGUMENTS -DNEW

    static boolean alreadyDebugified;

    public static void main1(String[] args) {
        int nFiles = 0;
        initialize();
        calledFromDebugify = true;

        try {
            processFiles: for (int i = 0; i < args.length; i++) {
                if (args[i].endsWith(".class")) {
                    JavaClass javaClass = new ClassParser(args[i]).parse();
                    JavaClass newJC;
                    String className = javaClass.getClassName();
                    String packageName = javaClass.getPackageName();

                    // if (dontProcessPackage(className)) {Debugger.print("-");
                    // continue;}
                    if (dontProcessMethod(dontInstrument, "*", className, true)) {
                        System.out.print("-");
                        continue;
                    }

                    String classFileName = args[i];
                    newJC = debugifyClass(javaClass, classFileName);
                    if (!alreadyDebugified) {
                        newJC.dump(classFileName);
                        System.out.print("+");
                        nFiles++;
                    } else
                        System.out.print("-");
                } else
                    Debugger.println(args[i] + " is not a .class file");
            }
        } catch (Exception e) {
            Debugger.println("Debugify exiting with exception: ");
            e.printStackTrace();
        }
        Debugger.println(version + " debugified " + nFiles + " files.");
    }

    public static void main(String[] args) {
        main1(args);
        System.exit(0);
    } // MAIN ENDS

    static private boolean initialized = false;

    public static void initialize() {
        if (initialized)
            return;
        initialized = true;
        if (System.getProperty("NOTHING") != null) {
            NO_PUTFIELD = NO_PUTSTATIC = NO_ISTORE = NO_IINC = NO_IASTORE = NO_RETURN = NO_RETURNVALUE = NO_INVOKEVIRTUAL = true;
            NO_ASTORE = NO_ATHROW = NO_CATCH = NO_INVOKESTATIC = NO_ARGUMENTS = NO_IASTORE = NO_NEW = NO_AASTORE = true;
        }

        if (System.getProperty("SILENT") != null)
            SILENT = true;
        if (System.getProperty("DEBUG_DEBUGIFY") != null)
            SILENT = false;
        if (System.getProperty("ATHROW") != null)
            NO_ATHROW = true;
        if (System.getProperty("CATCH") != null)
            NO_CATCH = true;
        if (System.getProperty("ASTORE") != null)
            NO_ASTORE = true;
        if (System.getProperty("AASTORE") != null)
            NO_AASTORE = true;
        if (System.getProperty("IASTORE") != null)
            NO_IASTORE = true;
        if (System.getProperty("RETURN") != null)
            NO_RETURN = true;
        if (System.getProperty("RETURNVALUE") != null)
            NO_RETURNVALUE = true;
        if (System.getProperty("INVOKEVIRTUAL") != null)
            NO_INVOKEVIRTUAL = true;
        if (System.getProperty("IINC") != null)
            NO_IINC = true;
        if (System.getProperty("ISTORE") != null)
            NO_ISTORE = true;
        if (System.getProperty("PUTFIELD") != null)
            NO_PUTFIELD = true;
        if (System.getProperty("PUTSTATIC") != null)
            NO_PUTSTATIC = true;
        if (System.getProperty("INVOKESTATIC") != null)
            NO_INVOKESTATIC = true;
        if (System.getProperty("ARGUMENTS") != null)
            NO_ARGUMENTS = true;
        if (System.getProperty("NEW") != null)
            NO_NEW = true;
        if (System.getProperty("NO_LOCKS") != null)
            NO_LOCKS = true;
        if (System.getProperty("PUBLIC_ONLY") != null) {
            PUBLIC_ONLY = true;
        }
        if (System.getProperty("DONT_REPLACE_VECTOR") != null) {
            DONT_REPLACE_VECTOR = true;
        }
        if (System.getProperty("PUTFIELD_ONLY") != null) {
            NO_ISTORE = NO_IINC = NO_IASTORE = NO_RETURN = NO_RETURNVALUE = NO_INVOKEVIRTUAL = true;
            NO_ASTORE = NO_ATHROW = NO_CATCH = NO_INVOKESTATIC = NO_ARGUMENTS = NO_PREVIOUS = true;
        }

        Defaults.readDefaults();
        dontRecord = Defaults.dontRecord;
        // Read the file for methods not to record
        dontInstrument = Defaults.dontInstrument;
        // Read the file for methods not to instrument
        instrumentOnlyPackages = Defaults.instrumentOnlyPackages;
    }

    public static JavaClass debugifyClass(JavaClass javaClass1,
            String classFileName) {
        synchronized (D.class) {
            if (PUBLIC_ONLY)
                return publicifyClass(javaClass1, classFileName);
            classTable = new HashMap();
            ConstantPool constants = javaClass1.getConstantPool();
            classPackageName = javaClass1.getPackageName();
            className = javaClass1.getClassName();
            Attribute[] attributes = javaClass1.getAttributes();

            initialize();
            if (!javaClass1.isClass())
                return javaClass1;
            // if (dontProcessPackage(className)) {return javaClass1;}
            if (dontProcessPackage(className)) {
                return publicifyClass(javaClass1, classFileName);
            }
            sourceFileName = javaClass1.getSourceFileName();

            for (int l = 0; l < attributes.length; l++) {
                if (!SILENT)
                    Debugger.println("Attributes " + l + " " + attributes[l]);
                String aName = constants.constantToString(attributes[l]
                        .getNameIndex(), Constants.CONSTANT_Utf8);
                if (!SILENT)
                    Debugger.println("Attribute = " + aName);
                if (aName == null)
                    continue;
                if (aName.equals("Debugified")) {
                    if (!SILENT)
                        Debugger.println("This file already Debugified.");
                    alreadyDebugified = true;
                    return javaClass1;
                }
            }

            if (!SILENT)
                Debugger.println("Debugifying: " + classFileName + " "
                        + Thread.currentThread());
            warningPrinted = false; // 1 flag allowed per file.

            cpg = new ConstantPoolGen(constants);
            int loc = cpg.addUtf8("Debugified");
            byte[] bytes = { 2 };
            // CHECK THIS BEFORE ACCEPTING FILE AS CORRECT. (SOMEDAY)
            Attribute att = new Unknown(loc, 1, bytes, cpg.getConstantPool());
            Attribute[] attributes1 = new Attribute[attributes.length + 1];
            for (int l = 0; l < attributes.length; l++) {
                attributes1[l] = attributes[l];
            }
            attributes1[attributes.length] = att;
            attributes = javaClass1.getAttributes();
            if (!SILENT)
                Debugger.println("Attributes of " + javaClass1);

            // out = cpg.addFieldref("java.lang.System", "out",
            // "Ljava/io/PrintStream;");
            // printlnString = cpg.addMethodref("java.io.PrintStream",
            // "println", "(Ljava/lang/String;)V");
            toString = cpg.addMethodref("java.lang.Object", "toString",
                    "()Ljava/lang/String;");
            typeClass = new ObjectType("java.lang.Class");
            typeClassLoader = new ObjectType("java.lang.ClassLoader");
            traceLine = new ObjectType("com.lambda.Debugger.TraceLine");
            typeShadowInt = new ObjectType("com.lambda.Debugger.ShadowInt");
            typeShadowShort = new ObjectType("com.lambda.Debugger.ShadowShort");
            typeShadowByte = new ObjectType("com.lambda.Debugger.ShadowByte");
            typeShadowChar = new ObjectType("com.lambda.Debugger.ShadowChar");
            typeShadowBoolean = new ObjectType(
                    "com.lambda.Debugger.ShadowBoolean");
            typeShadowFloat = new ObjectType("com.lambda.Debugger.ShadowFloat");
            typeShadowLong = new ObjectType("com.lambda.Debugger.ShadowLong");
            typeShadowDouble = new ObjectType(
                    "com.lambda.Debugger.ShadowDouble");
            typeShadowClass = new ObjectType("com.lambda.Debugger.ShadowClass");
            typeCollection = new ObjectType("java.util.Collection");

            try {
                if (cpg.getSize() > 10000)
                    throw new DebuggerException("Too many constants in class "
                            + className + " " + cpg.getSize() + " > 10,000");

                ClassGen classGen = new ClassGen(javaClass1);

                classGen.isPublic(true);
                if (!SILENT)
                    Debugger.println("Fields of ");
                Field[] fields = classGen.getFields();
                for (int j = 0; j < fields.length; j++) {
                    fields[j].isPublic(true);
                    fields[j].isPrivate(false);
                    fields[j].isProtected(false);
                    if (!SILENT)
                        Debugger.println(fields[j] + " Private: "
                                + fields[j].isPrivate());
                }
                // Debugger.println("Fields of " + classGen );

                reset();
                Field f = new FieldGen(Constants.ACC_STATIC
                        | Constants.ACC_FINAL | Constants.ACC_PRIVATE,
                        Type.INT, "ODB_offset", cpg).getField();

                classGen.addField(f);
                createVarMappingsStart(classGen);
                Method[] methods = classGen.getMethods();
                int clPosition = -1;
                for (int j = 0; j < methods.length; j++) {
                    methods[j].isPrivate(false);
                    methods[j].isProtected(false);
                    methods[j].isPublic(true);
                    String name = methods[j].getName();
                    if (name.equals("<clinit>")) {
                        clPosition = j;
                    } else {
                        classGen.replaceMethod(methods[j], debugifyMethod(
                                classGen, methods[j]));
                    }
                }
                if (clPosition > -1)
                    classGen.replaceMethod(methods[clPosition], debugifyCLinit(
                            classGen, methods[clPosition]));
                createVarMappingsEnd(classGen);
                createClassNameMethod(classGen);

                if (!processedCLINIT)
                    createCLinit(classGen);

                JavaClass javaClass2 = classGen.getJavaClass();

                javaClass2.setConstantPool(cpg.getFinalConstantPool());
                // javaClass.setConstantPool(cpg);
                alreadyDebugified = false;
                javaClass2.setAttributes(attributes1);

                // Debugger.println("JC " +javaClass1.toString());
                // Debugger.println("GEN " +javaClass2.toString());

                Repository.removeClass(javaClass1);
                Repository.addClass(javaClass2);
                String n = javaClass2.getClassName();
                // verify(n);
                if (Debugger.TRACE_LOADER)
                    System.out.print("+");
                if (!SILENT)
                    Debugger.println("Debugified:  " + classFileName);
                return (javaClass2);
            } catch (Exception e) {
                if (e instanceof DebuggerException)
                    outputStream.println(e);
                else {
                    if (Debugger.DEBUG_DEBUGGER)
                        e.printStackTrace();
                    else
                        outputStream.println(e);
                    // e.printStackTrace();// EXTREA FOR THE MOMENT
                }
                outputStream.println("Debugify " + version
                        + ": Unable to instrument: " + className);
                return (javaClass1);
            }
        }
    }

    public static JavaClass publicifyClass(JavaClass javaClass1,
            String classFileName) {
        synchronized (D.class) {
            ConstantPool constants = javaClass1.getConstantPool();
            classPackageName = javaClass1.getPackageName();
            className = javaClass1.getClassName();
            Attribute[] attributes = javaClass1.getAttributes();

            initialize();
            if (!javaClass1.isClass())
                return javaClass1;
            // if (dontProcessPackage(className)) {return javaClass1;}

            sourceFileName = javaClass1.getSourceFileName();

            for (int l = 0; l < attributes.length; l++) {
                if (!SILENT)
                    Debugger.println("Attributes " + l + " " + attributes[l]);
                String aName = constants.constantToString(attributes[l]
                        .getNameIndex(), Constants.CONSTANT_Utf8);
                if (!SILENT)
                    Debugger.println("Attribute = " + aName);
                if (aName == null)
                    continue;
                if (aName.equals("Debugified")) {
                    if (!SILENT)
                        Debugger.println("This file already Debugified.");
                    alreadyDebugified = true;
                    return javaClass1;
                }
            }

            if (!SILENT)
                Debugger.println("Debugifying: " + classFileName + " "
                        + Thread.currentThread());
            warningPrinted = false; // 1 flag allowed per file.

            cpg = new ConstantPoolGen(constants);
            int loc = cpg.addUtf8("Debugified");
            byte[] bytes = { 2 };
            // CHECK THIS BEFORE ACCEPTING FILE AS CORRECT. (SOMEDAY)
            Attribute att = new Unknown(loc, 1, bytes, cpg.getConstantPool());
            Attribute[] attributes1 = new Attribute[attributes.length + 1];
            for (int l = 0; l < attributes.length; l++) {
                attributes1[l] = attributes[l];
            }
            attributes1[attributes.length] = att;
            attributes = javaClass1.getAttributes();
            if (!SILENT)
                Debugger.println("Attributes of " + javaClass1);
            //
            try {
                ClassGen classGen = new ClassGen(javaClass1);

                classGen.isPublic(true);
                if (!SILENT)
                    Debugger.println("Fields of ");
                Field[] fields = classGen.getFields();
                for (int j = 0; j < fields.length; j++) {
                    fields[j].isPublic(true);
                    fields[j].isPrivate(false);
                    fields[j].isProtected(false);
                    if (!SILENT)
                        Debugger.println(fields[j] + " Private: "
                                + fields[j].isPrivate());
                }
                // Debugger.println("Fields of " + classGen );

                reset();

                JavaClass javaClass2 = classGen.getJavaClass();

                javaClass2.setConstantPool(cpg.getFinalConstantPool());
                // javaClass.setConstantPool(cpg);
                alreadyDebugified = false;
                javaClass2.setAttributes(attributes1);

                // Debugger.println("JC " +javaClass1.toString());
                // Debugger.println("GEN " +javaClass2.toString());

                Repository.removeClass(javaClass1);
                Repository.addClass(javaClass2);
                String n = javaClass2.getClassName();
                // verify(n);
                if (Debugger.TRACE_LOADER)
                    System.out.print("+");
                if (!SILENT)
                    Debugger.println("Debugified:  " + classFileName);
                return (javaClass2);
            } catch (Exception e) {
                Debugger.println("FAILED Debugified: " + classFileName + " "
                        + className);
                if (e instanceof DebuggerException)
                    outputStream.println(e);
                else {
                    if (Debugger.DEBUG_DEBUGGER)
                        e.printStackTrace();
                    else
                        outputStream.println(e);
                    e.printStackTrace(); // EXTREA FOR THE MOMENT
                }
                outputStream.println("Unable to instrument: " + className);
                return (javaClass1);
            }
        }
    }

    static boolean dontProcessPackage(String cName) {
        if (calledFromDebugify)
            return false; // If this is a direct request, do it!
        int len = instrumentOnlyPackages.size();
        if (len == 0)
            return false;

        for (int i = 0; i < len; i++) {
            String iOnly = (String) instrumentOnlyPackages.elementAt(i);
            // Debugger.println("only methods: " + cName + "."+ "? startswith "
            // + iOnly);
            if (iOnly.equals("")) {
                int dot = cName.indexOf(".");
                if (dot == -1)
                    return false;
            } else if (cName.startsWith(iOnly))
                return false;
        }
        if (!Defaults.didntInstrument.contains(cName))
            Defaults.didntInstrument.add(cName);
        return true;
    }

    static boolean dontProcessMethod(VectorD cmPairs, String mName,
            String cName, boolean exact) { // true -> dont instrument/record
        int len = cmPairs.size();
        for (int i = 0; i < len; i++) {
            String[] cmPair = (String[]) cmPairs.elementAt(i);
            if (exact) {
                if ((cName.equals(cmPair[0])) && mName.equals(cmPair[1]))
                    return true;
            } else if (mName.equals("*") || mName.equals(cmPair[1])
                    || cmPair[1].equals("*")
                    || (mName.equals("<init>") && cmPair[1].equals("new"))) { // if
                // mName
                // is a
                // candidate
                // Debugger.println("hidden methods: " + cName + "."+ mName +
                // "?= " + cmPair[0] + "." + cmPair[1]);
                if (cmPair[0].equals("*"))
                    return true;
                if (cName.equals("*"))
                    return true;
                if (cName.equals(cmPair[0]))
                    return true;
            }
        }
        return false;
    }

    static boolean dontProcessMethod(VectorD cmPairs, String mName, String cName) {
        return dontProcessMethod(cmPairs, mName, cName, false);
    }

    // ******************************************************** DEBUGIFY METHOD
    // ****************************************************************
    // ******************************************************** DEBUGIFY METHOD
    // ****************************************************************
    // ******************************************************** DEBUGIFY METHOD
    // ****************************************************************

    static void createCLinitPatch(ClassGen classGen, InstructionList patch,
            MethodGen clinit) {
        InstructionFactory factory = new InstructionFactory(cpg);

        patch.append(new PUSH(cpg, version));
        patch.append(new PUSH(cpg, className));
        patch.append(factory.createInvoke("com.lambda.Debugger.D",
                "verifyVersion", Type.VOID, new Type[] { Type.STRING,
                        Type.STRING }, Constants.INVOKESTATIC));

        patch.append(factory.createInvoke(className, "ODB_declareVarMappings",
                Type.VOID, new Type[] {}, Constants.INVOKESTATIC));
        patch.append(factory.createInvoke(className, "ODB_classNameMethod",
                Type.VOID, new Type[] {}, Constants.INVOKESTATIC));
        patch.append(new PUSH(cpg, slVector.size()));
        // patch.append(new ANEWARRAY(ObjectType.STRING)); ??
        patch.append(factory.createInvoke("com.lambda.Debugger.D",
                "createStringArray", stringArray, new Type[] { Type.INT },
                Constants.INVOKESTATIC));
        LocalVariableGen lvgArray = clinit.addLocalVariable("lvArray",
                stringArray, null, null);
        int lvArray = lvgArray.getIndex();
        lvgArray.setStart(patch.append(new ASTORE(lvArray)));

        for (int i = 0; i < slVector.size(); i += MAX_SOURCE_LINES) {
            String mName = createSLStore(classGen, i);
            patch.append(new ALOAD(lvArray));
            patch.append(factory.createInvoke(className, mName, Type.VOID,
                    new Type[] { stringArray }, Constants.INVOKESTATIC));
        }

        patch.append(new ALOAD(lvArray));
        patch.append(factory.createInvoke("com.lambda.Debugger.D",
                "addSourceLines", Type.INT, new Type[] { stringArray },
                Constants.INVOKESTATIC));
        patch
                .append(factory.createPutStatic(className, "ODB_offset",
                        Type.INT));
    }

    private static String createSLStore(ClassGen classGen, int start) {
        int end = slVector.size();
        if (end > 8000)
            throw new DebuggerException("Too many lines in class "
                    + classGen.getClassName() + " " + end + " > 8,000");

        int lvArray = 0; // For a static method, the first arg is here
        String mName = "ODB_slStoreMethod" + start;
        InstructionList patch = new MyInstructionList();
        MethodGen slStoreMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC, Type.VOID, new Type[] { stringArray },
                new String[] { "lvArray" }, mName, className, patch, cpg);

        for (int j = start; j < end; j++) {
            // Debugger.println("Adding source line: " + slVector.elementAt(j));
            patch.append(new ALOAD(lvArray));
            patch.append(new PUSH(cpg, j));
            patch.append(new PUSH(cpg, (String) slVector.elementAt(j)));
            patch.append(new AASTORE());
        }
        patch.append(InstructionConstants.RETURN);
        slStoreMethod.setMaxStack();
        Method m = slStoreMethod.getMethod();
        classGen.addMethod(m);
        patch.dispose();
        return mName;
    }

    static void createCLinit(ClassGen classGen) { // If there is no clint
        InstructionList patch = new MyInstructionList();
        MethodGen clinit = new MethodGen(Constants.ACC_STATIC, Type.VOID,
                new Type[] {}, new String[] {}, "<clinit>", className, patch,
                cpg);
        createCLinitPatch(classGen, patch, clinit);
        patch.append(InstructionConstants.RETURN);
        clinit.setMaxStack();
        classGen.addMethod(clinit.getMethod());
        patch.dispose();
    }

    private static Method debugifyCLinit(ClassGen classGen, Method m) {
        // If there IS a clinit
        InstructionList patch = new MyInstructionList();
        mg = new MethodGen(m, className, cpg);
        removeUnknownAttributes();
        il = mg.getInstructionList();
        ihs = il.getInstructionHandles();
        createCLinitPatch(classGen, patch, mg);
        il.insert(ihs[0], patch);
        patch.dispose();
        mg.setMaxStack();
        m = mg.getMethod();
        processedCLINIT = true;
        return m;
    }

    private static Method debugifyMethod(ClassGen classGen, Method m) {
        name = m.getName();
        factory = new InstructionFactory(cpg);
        // outputStream.println("Debugifying: " + className + "." + name);

        // if (name.equals("<clinit>")) return(debugifyCLinit(classGen, m));
        if (name.equals("<clinit>"))
            throw new DebuggerException("impossible");

        code = m.getCode();
        flags = m.getAccessFlags();
        lineNumberTable = m.getLineNumberTable();
        LocalVariableTable localVariableTable = m.getLocalVariableTable();

        isSynchronized = m.isSynchronized();
        String methodID = className + "." + name + ":" + (nMethods++);

        // Debugger.println("Original Code length: "+code.getLength());
        if (code == null)
            return m;
        if ((lineNumberTable == null) && (className != classNoNumbers)) {
            Debugger.println("No line numbers available for: " + className);
            classNoNumbers = className;
        }
        if (m.isNative() || m.isAbstract() || (code == null)
                || dontProcessMethod(dontInstrument, name, className)) {
            if (!SILENT)
                Debugger.println("Skipping " + className + " . " + name);
            return m;
        }

        mg = new MethodGen(m, className, cpg);
        removeUnknownAttributes();

        boolean isStatic = mg.isStatic();
        boolean isFinal = mg.isFinal();

        LocalVariableGen lvs[] = mg.getLocalVariables();
        mg.getLocalVariableTable(cpg);
        LocalVariableTable lvt = localVariableTable;
        lvgs = lvs;

        returnType = mg.getReturnType();
        il = mg.getInstructionList();
        ceg = mg.getExceptionHandlers();
        ihs = il.getInstructionHandles();
        patch = new MyInstructionList();
        Type[] argTypes = mg.getArgumentTypes();
        String[] argNames = mg.getArgumentNames();
        String[] exceptions = mg.getExceptions();
        if (!NO_LOCKS)
            mg.setAccessFlags(flags & ~Constants.ACC_SYNCHRONIZED);

        if (!SILENT)
            Debugger
                    .println("================Starting on "
                            + (isFinal ? "final " : "")
                            + (isStatic ? "static " : "") + returnType + " "
                            + name + "() No. Instructions:" + ihs.length + "\n"
                            + lineNumberTable + "\n===============");

        localVariables = createMissingVarTable(lvs);
        int nLocals = localVariables.length;

        // if (!mg.isStatic()) nLocals--; // Don't count "this"
        int nArgs = argNames.length;

        // checkForRET(); // XXXXXXXXXXXXXX
        bindMethodNames(patch, cpg);
        appendVarMapping(methodID, mg);

        lg = mg.addLocalVariable("tl", traceLine, null, null);
        tl = lg.getIndex();
        lg2 = mg.addLocalVariable("tl2", traceLine, null, null);
        tl2 = lg2.getIndex();

        if (!SILENT)
            Debugger.println("Original Byte Code for " + name + " \n" + il);

        // ************************* START PATCHING WITH D.addUnparented()
        // ****************************************

        doCATCH(code);

        line = getLineNumber(0);
        IFNONNULL branch;

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new PUSH(cpg, name));
        patch.append(new PUSH(cpg, methodID));
        patch.append(new PUSH(cpg, nLocals));
        patch.append(new INVOKESTATIC(D_getPreviousTL));
        patch.append(new DUP());
        patch.append(branch = new IFNONNULL(null));

        {
            patch.append(new POP());
            patch.append(factory.createGetStatic(className, "ODB_offset",
                    Type.INT));
            patch.append(new PUSH(cpg, buildFileLineN(className,
                    sourceFileName, line)));
            patch.append(new IADD());
            if (isStatic || name.equals("<init>")) {
                insertGetClass(className, patch);
            } else
                patch.append(new ALOAD(0));
            patch.append(new PUSH(cpg, name));
            patch.append(new PUSH(cpg, methodID));
            patch.append(new PUSH(cpg, nLocals));

            int nArgsRecorded = calculateNArguments(argTypes, lvs, isStatic);
            // int nArgsRecorded = Math.min(argTypes.length, MAX_ARGS_RECORDED);
            // i nArgsRecorded =Math.min(lvs.length-1, MAX_ARGS_RECORDED);

            for (int i = 0; i < nArgsRecorded; i++) {
                Type type = argTypes[i];
                int index;
                if ((!isStatic) && (lvs.length <= i + 1))
                    outputStream.println("IMPOSSIBLE2 " + lvs[0].getName());
                // if ((isStatic) && (lvs.length <= i))
                // outputStream.println("IMPOSSIBLE1");
                if (isStatic)
                    index = lvs[i].getIndex();
                else
                    index = lvs[i + 1].getIndex();

                if (type instanceof ReferenceType) {
                    patch.append(new ALOAD(index));
                } else if (type == Type.INT) {
                    patch.append(new ILOAD(index));
                    patch.append(new INVOKESTATIC(D_createShadowInt));
                } else if (type == Type.SHORT) {
                    patch.append(new ILOAD(index));
                    patch.append(new INVOKESTATIC(D_createShadowShort));
                } else if (type == Type.BYTE) {
                    patch.append(new ILOAD(index));
                    patch.append(new INVOKESTATIC(D_createShadowByte));
                } else if (type == Type.CHAR) {
                    patch.append(new ILOAD(index));
                    patch.append(new INVOKESTATIC(D_createShadowChar));
                } else if (type == Type.BOOLEAN) {
                    patch.append(new ILOAD(index));
                    patch.append(new INVOKESTATIC(D_createShadowBoolean));
                } else if (type == Type.FLOAT) {
                    patch.append(new FLOAD(index));
                    patch.append(new INVOKESTATIC(D_createShadowFloat));
                }
                if (type == Type.DOUBLE) {
                    patch.append(new DLOAD(index));
                    patch.append(new INVOKESTATIC(D_createShadowDouble));
                }
                if (type == Type.LONG) {
                    patch.append(new LLOAD(index));
                    patch.append(new INVOKESTATIC(D_createShadowLong));
                }
            }

            switch (nArgsRecorded) {
            case 0:
                patch.append(new INVOKESTATIC(D_addUnparented0));
                break;
            case 1:
                patch.append(new INVOKESTATIC(D_addUnparented1));
                break;
            case 2:
                patch.append(new INVOKESTATIC(D_addUnparented2));
                break;
            case 3:
                patch.append(new INVOKESTATIC(D_addUnparented3));
                break;
            case 4:
                patch.append(new INVOKESTATIC(D_addUnparented4));
                break;
            case 5:
                patch.append(new INVOKESTATIC(D_addUnparented5));
                break;
            case 6:
                patch.append(new INVOKESTATIC(D_addUnparented6));
                break;
            case 7:
                patch.append(new INVOKESTATIC(D_addUnparented7));
                break;
            case 8:
                patch.append(new INVOKESTATIC(D_addUnparented8));
                break;
            case 9:
                patch.append(new INVOKESTATIC(D_addUnparented9));
                break;
            case 10:
                patch.append(new INVOKESTATIC(D_addUnparented10));
                break;
            default:
                throw new DebuggerException("nArgs>MAX");
            }
            lg.setStart(patch.append(new ASTORE(tl))); // `tl' valid from here
            branch.setTarget(lg.getStart());

            if (!SILENT)
                Debugger.println("Inserting D.addUnparented():\n" + patch);

            // *************************** CHECK FOR SYNCHRONIZED METHOD
            // ********************************
            if (isSynchronized) {
                // Debugger.println("This method: " + m + " is synchronized.");
                doMethodLock(patch);
            }
            il.insert(ihs[0], patch);
            doARGUMENTS(nArgsRecorded);
        }

        // ******************************** THE MAIN LOOP
        // ********************************

        boolean initMethodBeforeSuperCall = name.equals("<init>");
        for (int j = 0; j < ihs.length; j++) {
            ins = ihs[j].getInstruction();
            line = getLineNumber(ihs[j].getPosition());
            if (!SILENT)
                Debugger.println("Working on " + j + " " + ins + "\t\t" + line);

            // This is a messy way of ensuring that references to THIS are not
            // touched
            // before the super constructor is called. An ugly compiler could
            // fool us
            // because we don't PROVE that the THIS ref is passed to super().
            if (initMethodBeforeSuperCall) {
                if (ins instanceof ALOAD) {
                    ALOAD al = (ALOAD) ins;
                    int index = al.getIndex();
                    if (index != 0)
                        continue;
                    while (!(ins instanceof INVOKESPECIAL)) {
                        j++;
                        ins = ihs[j].getInstruction();
                    }
                    if (ins instanceof INVOKESPECIAL) {
                        InvokeInstruction ii = (InvokeInstruction) ins;
                        String methodName = ii.getMethodName(cpg);
                        String methodClass = ii.getClassName(cpg);
                        if (true) { // (methodClass.equals("java.lang.Object"))
                            // {
                            // Debugger.println("Working on "+j+ " "+ins +
                            // "\t\t"+line);
                            initMethodBeforeSuperCall = false;
                            // 6 invokespecial #2 <Method java.lang.Object()>
                        }
                    }
                }
                continue; // No calls allowed before super()
            }

            if (ins instanceof ATHROW) {
                doATHROW(j);
                continue;
            }

            if ((ins instanceof AASTORE) || (ins instanceof IASTORE)
                    || (ins instanceof LASTORE) || (ins instanceof FASTORE)
                    || (ins instanceof DASTORE) || (ins instanceof SASTORE)
                    || (ins instanceof CASTORE) || (ins instanceof BASTORE)) {
                createArray1DPatch(j);
                continue;
            }

            if (ins instanceof StoreInstruction) {
                createStorePatch(j);
                continue;
            }

            if (ins instanceof PUTFIELD) {
                doPUTFIELD(j);
                continue;
            }
            if (ins instanceof PUTSTATIC) {
                doPUTSTATIC(j);
                continue;
            }

            if (ins instanceof IINC) {
                doIINC(j);
                continue;
            }

            if (ins instanceof ReturnInstruction) {
                doRETURN(j); // ReturnMarker! not ReturnLine
                continue;
            }

            if (ins instanceof INVOKEVIRTUAL) {
                doINVOKEVIRTUAL(j, false, false);
                continue;
            }
            if (ins instanceof INVOKEINTERFACE) {
                doINVOKEVIRTUAL(j, false, false);
                continue;
            }

            if (ins instanceof INVOKESTATIC) {
                doINVOKESTATIC(j);
                continue;
            }

            if (ins instanceof INVOKESPECIAL) {
                doINVOKESPECIAL(j);
                continue;
            }
            if (ins instanceof NEWARRAY) {
                doNEWARRAY(j);
                continue;
            }
            if (ins instanceof ANEWARRAY) {
                doNEWARRAY(j);
                continue;
            }
            if (ins instanceof MULTIANEWARRAY) {
                doNEWARRAY(j);
                continue;
            }
            if (ins instanceof MONITORENTER) {
                doMONITORENTER(j);
                continue;
            }
            if (ins instanceof MONITOREXIT) {
                doMONITOREXIT(j);
                continue;
            }

            if (ins instanceof NEW) {
                // THE REAL WORK COMES IN THE CONSTRUCTORS
                doNEW(j);
                continue;
            }

        } // ******************************** THE MAIN LOOP
        // ********************************

        if (isSynchronized) {
            doMethodLockExit();
        }

        if (patch.size() > 0)
            Debugger
                    .println("Oh Shit! ****************************************************************\n"
                            + patch);
        lg.setEnd(ihs[ihs.length - 1]);
        // We need a target for end of patch EXISTING?

        mg.setMaxStack();

        m = mg.getMethod();

        if (!SILENT)
            Debugger.println("New Byte Code for " + name + " \n" + il);
        if (!SILENT)
            Debugger.println("==============Done on " + name
                    + " No. Instructions:" + ihs.length
                    + "==============\n\n\n");

        il.dispose(); // Reuse instruction handles

        Code code1 = m.getCode();
        if (code1.getLength() > 60000)
            throw new DebuggerException(
                    "Method too long. Instrumented version of " + m + " "
                            + code1.getLength() + " > 60,000 bytes."
                            + " Original version " + code.getLength());

        /*
         * if ((code1.getLength() > 9000) || ( ((code1.getLength()*10) /
         * code.getLength()) > 50) ) { Debugger.println("Orig Code length:
         * "+code.getLength()); Debugger.println("Instrumented Code length:
         * "+code1.getLength()); Debugger.println("Ratio: "+
         * ((code1.getLength()*10) / code.getLength())); }
         */
        return m;
    }

    private static void removeUnknownAttributes() {
        Attribute[] attrs = mg.getCodeAttributes();
        for (Attribute a : attrs) {
            // System.out.println("attr: " + a);
            // System.out.println();
            // int ix = a.getNameIndex();
            // ConstantPool cp = a.getConstantPool();
            byte tag = a.getTag();
            // if (tag==-1) tag= Constants.CONSTANT_Utf8;
            // Constant c = cp.getConstant(ix);
            // String s = c.toString();
            // if (s.equals("LocalVariableTypeTable")) {
            if (tag == -1) {
                // System.out.println("removing: " + a);
                mg.removeCodeAttribute(a);
            }
        }
    }

    private static int calculateNArguments(Type[] argTypes,
            LocalVariableGen[] lvs, boolean isStatic) {
        int nArgsRecorded = Math.min(argTypes.length, MAX_ARGS_RECORDED);

        for (int i = 0; i < nArgsRecorded; i++) {
            LocalVariableGen lvg;

            if (isStatic) {
                if (i >= lvs.length)
                    return i;
                lvg = lvs[i];
            } else {
                if (i + 1 >= lvs.length)
                    return i;
                lvg = lvs[i + 1];
            }

            int index = lvg.getIndex();
            if (index > 0)
                return i;
        }

        return nArgsRecorded;
    }

    /*
     * Order of instructions:
     * 
     * new astore -> value [dup] invokespecial push filename push line aload <-
     * value invokestatic newObj
     * 
     */

    private static InstructionHandle firstIns;

    static void doMethodLock(InstructionList patch) {
        if (NO_LOCKS)
            return;

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());

        if (mg.isStatic() || name.equals("<init>")) {
            // patch.append(new PUSH(cpg, className));
            // patch.append(new INVOKESTATIC(D_createShadowClass));
            // patch.append(factory.createInvoke("java.lang.Class", "forName",
            // typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
            // patch.append(new INVOKESTATIC(D_createShadowClass1));
            insertGetClass(className, patch);
        } else
            patch.append(new ALOAD(0));

        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_gettingLock));
        if (mg.isStatic() || name.equals("<init>")) {
            // patch.append(new PUSH(cpg, className));
            // patch.append(new INVOKESTATIC(D_createShadowClass));
            // patch.append(factory.createInvoke("java.lang.Class", "forName",
            // typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
            // patch.append(new INVOKESTATIC(D_createShadowClass1));
            insertGetClass(className, patch);

        } else
            patch.append(new ALOAD(0));
        patch.append(new MONITORENTER());

        firstIns = patch.append(new PUSH(cpg, buildFileLineN(className,
                sourceFileName, line)));
        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new IADD());
        if (mg.isStatic() || name.equals("<init>")) {
            // patch.append(new PUSH(cpg, className));
            // patch.append(new INVOKESTATIC(D_createShadowClass));
            // patch.append(factory.createInvoke("java.lang.Class", "forName",
            // typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
            // patch.append(new INVOKESTATIC(D_createShadowClass1));
            insertGetClass(className, patch);

        } else
            patch.append(new ALOAD(0));
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_gotLock));
    }

    static void doMONITORENTER(int j) {
        if (NO_LOCKS)
            return;

        LocalVariableGen lg1 = mg.addLocalVariable("obj", Type.OBJECT, null,
                null);
        int obj = lg1.getIndex();

        lg1.setStart(patch.append(new ASTORE(obj))); // 

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new ALOAD(obj));
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_gettingLock));
        lg1.setEnd(patch.append(new ALOAD(obj)));
        // if (!SILENT) Debugger.println("Inserting
        // throw:\n"+patch.toString(true));
        // il.insert(ihs[j], patch);
        insertPatch(ihs[j], "Inserting gettingLock:\n");

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new ALOAD(obj));
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_gotLock));
        appendPatch(ihs[j], "Appending gotLock:\n");
    }

    static void doMethodLockExit() { // When you see a synchronized method
        if (NO_LOCKS)
            return;

        // InstructionHandle firstIns = ihs[0];
        InstructionHandle lastIns = ihs[ihs.length - 1];
        InstructionHandle secondLastIns;
        if (ihs.length < 2)
            secondLastIns = lastIns;
        else
            secondLastIns = ihs[ihs.length - 2];

        InstructionHandle exceptionIns = patch.append(new PUSH(cpg,
                buildFileLineN(className, sourceFileName, line)));
        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new IADD());
        if (mg.isStatic() || name.equals("<init>")) {
            // patch.append(new PUSH(cpg, className));
            // patch.append(new INVOKESTATIC(D_createShadowClass));
            // patch.append(factory.createInvoke("java.lang.Class", "forName",
            // typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
            // patch.append(new INVOKESTATIC(D_createShadowClass1));
            insertGetClass(className, patch);

        } else
            patch.append(new ALOAD(0));
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_releasingLock));
        if (mg.isStatic() || name.equals("<init>")) {
            // patch.append(new PUSH(cpg, className));
            // patch.append(new INVOKESTATIC(D_createShadowClass));
            // patch.append(factory.createInvoke("java.lang.Class", "forName",
            // typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
            // patch.append(new INVOKESTATIC(D_createShadowClass1));
            insertGetClass(className, patch);

        } else
            patch.append(new ALOAD(0));
        patch.append(new MONITOREXIT());
        patch.append(new ATHROW());
        appendPatch(lastIns, "Inserting D_releasingLock:\n");

        // This was secondLastIns, Why???
        mg.addExceptionHandler(firstIns, lastIns, exceptionIns, new ObjectType(
                "java.lang.Exception"));
        // null == "all"
    }

    static void doLockReturn(int j) { // when you see a return() instruction
        // in a synch method
        if (NO_LOCKS)
            return;

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        if (mg.isStatic() || name.equals("<init>")) {
            // patch.append(new PUSH(cpg, className));
            // patch.append(new INVOKESTATIC(D_createShadowClass));
            // patch.append(factory.createInvoke("java.lang.Class", "forName",
            // typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
            // patch.append(new INVOKESTATIC(D_createShadowClass1));
            insertGetClass(className, patch);

        } else
            patch.append(new ALOAD(0));
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_releasingLock));
        if (mg.isStatic() || name.equals("<init>")) {
            // patch.append(new PUSH(cpg, className));
            // patch.append(new INVOKESTATIC(D_createShadowClass));
            // patch.append(factory.createInvoke("java.lang.Class", "forName",
            // typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
            // patch.append(new INVOKESTATIC(D_createShadowClass1));
            insertGetClass(className, patch);

        } else
            patch.append(new ALOAD(0));
        patch.append(new MONITOREXIT());
        insertPatch(ihs[j], "Inserting D_releasingLock:\n");
    }

    static void doMONITOREXIT(int j) {
        if (NO_LOCKS)
            return;

        LocalVariableGen lg1 = mg.addLocalVariable("obj", Type.OBJECT, null,
                null);
        int obj = lg1.getIndex();

        lg1.setStart(patch.append(new ASTORE(obj))); // 

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new ALOAD(obj));
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_releasingLock));
        lg1.setEnd(patch.append(new ALOAD(obj)));
        // if (!SILENT) Debugger.println("Inserting
        // throw:\n"+patch.toString(true));
        // il.insert(ihs[j], patch);
        insertPatch(ihs[j], "Inserting D_releasingLock:\n");
    }

    static void doNEW(int j) {
        NEW ii = (NEW) ins;
        String methodClass = ii.getType(cpg).toString();

        if (DONT_REPLACE_VECTOR)
            return;
        if (methodClass.equals("java.util.Vector"))
            patch.append(factory.createNew("com.lambda.Debugger.MyVector"));
        else if (methodClass.equals("java.util.Hashtable"))
            patch.append(factory.createNew("com.lambda.Debugger.MyHashtable"));
        else if (methodClass.equals("java.util.HashMap"))
            patch.append(factory.createNew("com.lambda.Debugger.MyHashMap"));
        else if (methodClass.equals("java.util.ArrayList"))
            patch.append(factory.createNew("com.lambda.Debugger.MyArrayList"));
        // else if (methodClass.equals("java.util.HashMapXXXXXXXX"))
        // patch.append(factory.createNew("com.lambda.Debugger.MyHashMap"));
        else
            return;
        replacePatch(ihs[j], "Replacing w/My Vector/Hashtable:\n");
        replacingVector = true;
    }

    static void doNEWARRAY(int j) {
        if (NO_NEW)
            return;
        if (!SILENT)
            Debugger.println("doNEWARRAY ");

        patch.append(new DUP());
        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new INVOKESTATIC(D_newArray));
        appendPatch(ihs[j], "Inserting  newArray");
    }

    static void doINVOKESPECIAL(int j) {
        if (NO_NEW)
            return;
        InvokeInstruction ii = (InvokeInstruction) ins;
        String methodClass = ii.getClassName(cpg);
        String methodName = ii.getMethodName(cpg);
        Type[] types = ii.getArgumentTypes(cpg);

        // if (dontProcessMethod(dontRecord, "<init>", methodClass)) return;
        // if (dontProcessMethod(dontRecord, "<cinit>", methodClass)) return;
        if (!SILENT)
            Debugger.println("doINVOKESPECIAL " + methodClass + "."
                    + methodName + "()");

        // doINVOKEVIRTUAL(j, false, (methodName == "<init>"));
        // doINVOKEVIRTUAL(j, false, true);

        doINVOKEVIRTUAL(j, false, true);
        if (!replacingVector)
            return; // If in <init> &1st instruction, it's a super call
        if (methodClass.equals("java.util.Vector"))
            patch.append(factory.createInvoke("com.lambda.Debugger.MyVector",
                    "<init>", Type.VOID, types, Constants.INVOKESPECIAL));
        else if (methodClass.equals("java.util.Hashtable"))
            patch.append(factory.createInvoke(
                    "com.lambda.Debugger.MyHashtable", "<init>", Type.VOID,
                    types, Constants.INVOKESPECIAL));
        else if (methodClass.equals("java.util.HashMap"))
            patch.append(factory.createInvoke("com.lambda.Debugger.MyHashMap",
                    "<init>", Type.VOID, types, Constants.INVOKESPECIAL));
        else if (methodClass.equals("java.util.ArrayList"))
            patch.append(factory.createInvoke(
                    "com.lambda.Debugger.MyArrayList", "<init>", Type.VOID,
                    types, Constants.INVOKESPECIAL));
        else
            return;
        replacePatch(ihs[j], "Replacing w/ My Vector/Hashtable<init>:\n");
        replacingVector = false;
    }

    static void doARGUMENTS(int start) { // Only called from debugifyMethod()
        if (NO_ARGUMENTS)
            return;
        String[] argNames = mg.getArgumentNames();
        Type[] argTypes = mg.getArgumentTypes();
        LocalVariableGen lvs[] = mg.getLocalVariables();
        // LineNumber[] table = lineNumberTable.getLineNumberTable();
        boolean isStatic = mg.isStatic();

        /*
         * if (table.length > 0) line = table[0].getLineNumber(); else line = 0;
         */

        line = getLineNumber(0);
        for (int i = start; i < argNames.length; i++) {
            Type type = argTypes[i];
            // LocalVariableGen lvg;
            int index;
            // if (isStatic) lvg = lvs[i]; else lvg = lvs[i+1];
            if (isStatic)
                index = lvs[i].getIndex();
            else
                index = lvs[i + 1].getIndex();
            // if (!SILENT) Debugger.println(mg.getName()+" " +argNames[i]+": "
            // +argTypes[i]+" "+lvg);

            patch.append(factory.createGetStatic(className, "ODB_offset",
                    Type.INT));
            patch.append(new PUSH(cpg, buildFileLineN(className,
                    sourceFileName, line)));
            patch.append(new IADD());
            patch.append(new PUSH(cpg, i));

            if (type instanceof ReferenceType) {
                patch.append(new ALOAD(index));
            } else if (type == Type.INT) {
                patch.append(new ILOAD(index));
                patch.append(new INVOKESTATIC(D_createShadowInt));
            } else if (type == Type.SHORT) {
                patch.append(new ILOAD(index));
                patch.append(new INVOKESTATIC(D_createShadowShort));
            } else if (type == Type.BYTE) {
                patch.append(new ILOAD(index));
                patch.append(new INVOKESTATIC(D_createShadowByte));
            } else if (type == Type.CHAR) {
                patch.append(new ILOAD(index));
                patch.append(new INVOKESTATIC(D_createShadowChar));
            } else if (type == Type.BOOLEAN) {
                patch.append(new ILOAD(index));
                patch.append(new INVOKESTATIC(D_createShadowBoolean));
            } else if (type == Type.FLOAT) {
                patch.append(new FLOAD(index));
                patch.append(new INVOKESTATIC(D_createShadowFloat));
            }
            if (type == Type.DOUBLE) {
                patch.append(new DLOAD(index));
                patch.append(new INVOKESTATIC(D_createShadowDouble));
            }
            if (type == Type.LONG) {
                patch.append(new LLOAD(index));
                patch.append(new INVOKESTATIC(D_createShadowLong));
            }

            patch.append(new ALOAD(tl));
            patch.append(new INVOKESTATIC(D_bind));
        }

        // if (!SILENT) Debugger.println("Inserting (Arguments:
        // \n"+patch.toString(true));
        // il.insert(ihs[0], patch);
        insertPatch(ihs[0], "Inserting Arguments: \n");
    }

    // catchEx(String sourceFileName, int line, Object ex, TraceLine tl)
    // {//Exception
    public static void doCATCH(Code code) {

        if (NO_CATCH)
            return;
        if (!SILENT)
            Debugger
                    .println("==========code.getExceptionHandlers() START-------------");
        for (int i = 0; i < ceg.length; i++) {
            if (!SILENT)
                Debugger.println("CodeExceptionGen: " + ceg[i]);
        }

        ArrayList handlers = new ArrayList();

        loop: for (int i = 0; i < ceg.length; i++) { // Don't repeat if
            // multiple exceptions
            // have same handler
            InstructionHandle handlerPC = ceg[i].getHandlerPC();
            for (int k = 0; k < i; k++) {
                InstructionHandle ih2 = ceg[k].getHandlerPC();
                if (ih2.getPosition() == handlerPC.getPosition()) {
                    // Debugger.println("Duplicate handler: " + ih2);
                    continue loop;
                }
            }
            handlers.add(ceg[i]);
        }

        for (int i = 0; i < handlers.size(); i++) {
            CodeExceptionGen eg = (CodeExceptionGen) handlers.get(i);
            InstructionHandle handlerPC = eg.getHandlerPC();

            line = getLineNumber(handlerPC.getPosition());
            // lineNumberTable.getSourceLine(handlerPC.getPosition());
            if (!SILENT)
                Debugger.println("ADDING catch code before " + handlerPC);

            LocalVariableGen lg1 = mg.addLocalVariable("exc", Type.OBJECT,
                    null, null);
            int exc = lg1.getIndex();

            lg1.setStart(patch.append(new ASTORE(exc))); // 
            patch.append(factory.createGetStatic(className, "ODB_offset",
                    Type.INT));
            patch.append(new PUSH(cpg, buildFileLineN(className,
                    sourceFileName, line)));
            patch.append(new IADD());
            patch.append(new ALOAD(exc));
            patch.append(new ALOAD(tl));
            patch.append(new INVOKESTATIC(D_catch));
            lg1.setEnd(patch.append(new ALOAD(exc)));

            // if (!SILENT) Debugger.println("Inserting
            // catch:\n"+patch.toString(true));

            for (int ii = 0; ii < ceg.length; ii++) { // Retarget all
                // exceptions using this
                // hander
                InstructionHandle handlerPC2 = ceg[ii].getHandlerPC();
                if (handlerPC2 == handlerPC)
                    ceg[ii].setHandlerPC(patch.getStart());
                // Why doesn't insertPatch() do this?
            }

            insertPatch(handlerPC, "Inserting Catch Code:\n");
        }

        if (!SILENT)
            Debugger
                    .println("==========code.getExceptionHandlers() END-------------");
        for (int i = 0; i < ceg.length; i++) {
            if (!SILENT)
                Debugger.println("CodeExceptionGen: " + ceg[i]);
        }
    }

    // public static synchronized void throwEx(String sourceFileName, int line,
    // Exception ex, TraceLine tl) {
    public static void doATHROW(int j) {
        if (NO_ATHROW)
            return;

        LocalVariableGen lg1 = mg.addLocalVariable("exc", Type.OBJECT, null,
                null);
        int exc = lg1.getIndex();

        lg1.setStart(patch.append(new ASTORE(exc))); // 

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new ALOAD(exc));
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_athrow));
        lg1.setEnd(patch.append(new ALOAD(exc)));
        // if (!SILENT) Debugger.println("Inserting
        // throw:\n"+patch.toString(true));
        // il.insert(ihs[j], patch);
        insertPatch(ihs[j], "Inserting throw:\n");
    }

    private static String dummyName(int st) {
        return "unnamedLocalVar" + st;
    }

    private static int lookupLocalVarIndex(int j, int storeTarget) {
        int pos = ihs[j].getPosition() + ins.getLength();
        int offSet = 0;

        if ((localVariables.length > 0)
                && (localVariables[0].getName().equals("this")))
            offSet = 1;

        for (int i = 0; i < localVariables.length; i++) {
            LocalVariable lv = localVariables[i];
            if ((lv.getIndex() == storeTarget) && (lv.getStartPC() <= pos)
                    && (lv.getStartPC() + lv.getLength() >= pos)) {
                return i - offSet;
            }
        }
        return -1;
    }

    public static void createStorePatch(int j) {
        if (NO_ASTORE)
            return;
        int storeTarget = ((LocalVariableInstruction) ins).getIndex();
        Type type = ((LocalVariableInstruction) ins).getType(cpg);
        int targetIndex = 0;

        targetIndex = lookupLocalVarIndex(j, storeTarget);
        if (targetIndex == -1)
            return; // Assume this is a compiler variable

        IFNE branch = null;
        if ((type == Type.INT) || (type == Type.OBJECT)) {
            patch.append(new DUP());
            patch.append(new PUSH(cpg, name));
            if (type == Type.INT) {
                patch.append(factory
                        .createInvoke("com.lambda.Debugger.D",
                                "skipChangeLocalVarI", Type.BOOLEAN,
                                new Type[] { Type.INT, Type.STRING },
                                Constants.INVOKESTATIC));
            }
            if (type == Type.OBJECT) {
                patch.append(factory.createInvoke("com.lambda.Debugger.D",
                        "skipChangeLocalVarA", Type.BOOLEAN, new Type[] {
                                Type.OBJECT, Type.STRING },
                        Constants.INVOKESTATIC));
            }
            patch.append(branch = new IFNE(null));
        }

        if (type == Type.OBJECT) {
            patch.append(new DUP());
        } // Need exact type here

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new PUSH(cpg, targetIndex));
        patch.append(new ALOAD(tl));

        if (type == Type.OBJECT)
            patch.append(new INVOKESTATIC(D_changeA)); // returns VOID!
        else if (type == Type.INT)
            patch.append(new INVOKESTATIC(D_changeI));
        else if (type == Type.SHORT)
            patch.append(new INVOKESTATIC(D_changeS));
        else if (type == Type.BYTE)
            patch.append(new INVOKESTATIC(D_changeB));
        else if (type == Type.CHAR)
            patch.append(new INVOKESTATIC(D_changeC));
        else if (type == Type.BOOLEAN)
            patch.append(new INVOKESTATIC(D_changeZ));
        else if (type == Type.LONG)
            patch.append(new INVOKESTATIC(D_changeL));
        else if (type == Type.FLOAT)
            patch.append(new INVOKESTATIC(D_changeF));
        else if (type == Type.DOUBLE)
            patch.append(new INVOKESTATIC(D_changeD));

        if ((type == Type.INT) || (type == Type.OBJECT)) {
            InstructionHandle ih = patch.append(new NOP());
            branch.setTarget(ih);
        }
        insertPatch(ihs[j], "Appending D_change:" + type + "\n");
    }

    public static void doIINC(int j) {
        if (NO_IINC)
            return;
        int storeTarget = ((LocalVariableInstruction) ins).getIndex();
        int targetIndex = 0;

        targetIndex = lookupLocalVarIndex(j, storeTarget);
        if (targetIndex == -1)
            return; // Assume this is a compiler variable
        IFNE branch;

        patch.append(new ILOAD(storeTarget));
        patch.append(new PUSH(cpg, name));
        patch.append(factory.createInvoke("com.lambda.Debugger.D",
                "skipChangeLocalVarI", Type.BOOLEAN, new Type[] { Type.INT,
                        Type.STRING }, Constants.INVOKESTATIC));
        patch.append(branch = new IFNE(null));
        patch.append(new ILOAD(storeTarget));
        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new PUSH(cpg, targetIndex));
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_changeIvoid));
        InstructionHandle ih = patch.append(new NOP());
        branch.setTarget(ih);

        appendPatch(ihs[j], "Appending D_createShadowInt IINC D_change:");
    }

    public static void createArray1DPatch(int j) {
        if (NO_AASTORE)
            return;

        Instruction storeIns, loadIns1, loadIns2 = null;
        InvokeInstruction changeIns;
        Type type = ((ArrayInstruction) ins).getType(cpg);

        if (!SILENT)
            Debugger.println("createArray1DPatch " + type + " " + ins);

        LocalVariableGen lg3 = mg.addLocalVariable("value", type, null, null);
        int value = lg3.getIndex();

        if ((type == Type.INT) || (type == Type.SHORT) || (type == Type.BYTE)
                || (type == Type.CHAR) || (type == Type.BOOLEAN)) {
            storeIns = new ISTORE(value);
            loadIns1 = new ILOAD(value);
            if (type == Type.INT)
                changeIns = new INVOKESTATIC(D_changeArrayI);
            else if (type == Type.SHORT)
                changeIns = new INVOKESTATIC(D_changeArrayS);
            else if (type == Type.BYTE)
                changeIns = new INVOKESTATIC(D_changeArrayB);
            else if (type == Type.CHAR)
                changeIns = new INVOKESTATIC(D_changeArrayC);
            else if (type == Type.BOOLEAN)
                changeIns = new INVOKESTATIC(D_changeArrayZ);
            else {
                Debugger.println("IMPOSSIBLE1 createArray1DPatch" + type);
                return;
            }
        } else if (type instanceof ReferenceType) {
            storeIns = new ASTORE(value);
            loadIns1 = new ALOAD(value);
            loadIns2 = new ALOAD(value);
            changeIns = new INVOKESTATIC(D_changeArrayA);
        } else if (type == Type.LONG) {
            storeIns = new LSTORE(value);
            loadIns1 = new LLOAD(value);
            changeIns = new INVOKESTATIC(D_changeArrayL);
        } else if (type == Type.DOUBLE) {
            storeIns = new DSTORE(value);
            loadIns1 = new DLOAD(value);
            changeIns = new INVOKESTATIC(D_changeArrayD);
        } else if (type == Type.FLOAT) {
            storeIns = new FSTORE(value);
            loadIns1 = new FLOAD(value);
            changeIns = new INVOKESTATIC(D_changeArrayF);
        } else {
            Debugger.println("IMPOSSIBLE2 createArray1DPatch" + type);
            return;
        }

        IFNE branch = null;
        if ((type == Type.INT) || (type == Type.OBJECT)) {
            patch.append(new DUP());
            patch.append(new PUSH(cpg, name));
            if (type == Type.INT) {
                patch.append(factory
                        .createInvoke("com.lambda.Debugger.D",
                                "skipChangeArrayI", Type.BOOLEAN, new Type[] {
                                        Type.INT, Type.STRING },
                                Constants.INVOKESTATIC));
            }
            if (type == Type.OBJECT) {
                patch.append(factory.createInvoke("com.lambda.Debugger.D",
                        "skipChangeArrayA", Type.BOOLEAN, new Type[] {
                                Type.OBJECT, Type.STRING },
                        Constants.INVOKESTATIC));
            }
            patch.append(branch = new IFNE(null));
        }

        lg3.setStart(patch.append(storeIns));
        // This is the value to be stored.

        patch.append(new DUP2()); // Load the index for him
        patch.append(loadIns1); // Load the value for him
        // Now the stack is restored.

        // Change instance var: D.changeArray1D("foo.java", 75, Int[23]{3, 4,
        // ...}, index, <Obj_33>, tl) SET INSTANCE VAR
        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new ALOAD(tl));
        InstructionHandle h1 = patch.append(changeIns);
        if (loadIns2 != null)
            h1 = patch.append(loadIns2); // Load the value for us
        lg3.setEnd(h1);

        if ((type == Type.INT) || (type == Type.OBJECT)) {
            InstructionHandle ih = patch.append(new NOP());
            branch.setTarget(ih);
        }

        // if (!SILENT) Debugger.println("Inserting
        // (D_changeArray1D:\n"+patch.toString(true));
        // il.insert(ihs[j], patch);
        insertPatch(ihs[j], "Inserting D_changeArray1D:\n");
    }

    public static void doPUTFIELD(int j) {
        if (NO_PUTFIELD)
            return;
        createChangeIVPatch(false);
        insertPatch(ihs[j], "Inserting D_changeIV:\n");
    }

    public static void doPUTSTATIC(int j) {
        if (NO_PUTSTATIC)
            return;
        createChangeIVPatch(true);
        insertPatch(ihs[j], "Inserting D_changeSTATIC:\n");
    }

    public static void createChangeIVPatch(boolean isStatic) {
        String fieldName = ((FieldInstruction) ins).getFieldName(cpg);
        Type fieldType = ((FieldInstruction) ins).getFieldType(cpg);
        Type classType = ((FieldInstruction) ins).getClassType(cpg);
        LocalVariableGen lg2 = mg.addLocalVariable("value", fieldType, null,
                null);
        int value = lg2.getIndex();
        Instruction storeIns, loadIns1, loadIns2 = null;
        InvokeInstruction changeIns;
        int obj = -1;
        LocalVariableGen lg1 = null;

        if ((fieldType == Type.INT) || (fieldType == Type.SHORT)
                || (fieldType == Type.BYTE) || (fieldType == Type.CHAR)
                || (fieldType == Type.BOOLEAN)) {
            storeIns = new ISTORE(value);
            loadIns1 = new ILOAD(value);
            if (fieldType == Type.INT)
                changeIns = new INVOKESTATIC(D_changeIVI);
            else if (fieldType == Type.SHORT)
                changeIns = new INVOKESTATIC(D_changeIVS);
            else if (fieldType == Type.BYTE)
                changeIns = new INVOKESTATIC(D_changeIVB);
            else if (fieldType == Type.CHAR)
                changeIns = new INVOKESTATIC(D_changeIVC);
            else if (fieldType == Type.BOOLEAN)
                changeIns = new INVOKESTATIC(D_changeIVZ);
            else
                return; // impossible
            if (!SILENT)
                Debugger.println("doPUTFIELD " + fieldName + " " + fieldType
                        + " " + changeIns.getMethodName(cpg));

        } else if (fieldType instanceof ReferenceType) {
            storeIns = new ASTORE(value);
            loadIns1 = new ALOAD(value);
            loadIns2 = new ALOAD(value);
            changeIns = new INVOKESTATIC(D_changeIVA);
        } else if (fieldType == Type.LONG) {
            storeIns = new LSTORE(value);
            loadIns1 = new LLOAD(value);
            changeIns = new INVOKESTATIC(D_changeIVL);
        } else if (fieldType == Type.DOUBLE) {
            storeIns = new DSTORE(value);
            loadIns1 = new DLOAD(value);
            changeIns = new INVOKESTATIC(D_changeIVD);
        } else if (fieldType == Type.FLOAT) {
            storeIns = new FSTORE(value);
            loadIns1 = new FLOAD(value);
            changeIns = new INVOKESTATIC(D_changeIVF);
        } else
            return;

        IFNE branch = null;
        if ((fieldType == Type.INT) || (fieldType == Type.OBJECT)) {
            patch.append(new DUP());
            patch.append(new PUSH(cpg, name));
            if (fieldType == Type.INT) {
                patch.append(factory
                        .createInvoke("com.lambda.Debugger.D",
                                "skipChangeInstanceVarI", Type.BOOLEAN,
                                new Type[] { Type.INT, Type.STRING },
                                Constants.INVOKESTATIC));
            }
            if (fieldType == Type.OBJECT) {
                patch.append(factory.createInvoke("com.lambda.Debugger.D",
                        "skipChangeInstanceVarA", Type.BOOLEAN, new Type[] {
                                Type.OBJECT, Type.STRING },
                        Constants.INVOKESTATIC));
            }
            patch.append(branch = new IFNE(null));
        }

        lg2.setStart(patch.append(storeIns));
        // This is the value to be stored.

        if (!isStatic) {
            patch.append(new DUP()); // 'this'
        } else {
            // patch.append(new PUSH(cpg, classType.toString()));
            // patch.append(new INVOKESTATIC(D_createShadowClass));
            // patch.append(factory.createInvoke("java.lang.Class", "forName",
            // typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
            // patch.append(new INVOKESTATIC(D_createShadowClass1));
            insertGetClass(classType.toString(), patch);

        }

        patch.append(loadIns1); // Load the value

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new PUSH(cpg, fieldName));
        patch.append(new ALOAD(tl));
        patch.append(changeIns);
        if (loadIns2 != null)
            patch.append(loadIns2); // Load the value

        if ((fieldType == Type.INT) || (fieldType == Type.OBJECT)) {
            InstructionHandle ih = patch.append(new NOP());
            branch.setTarget(ih);
        }

    }

    /*
     * public static void createChangeIVPatch(boolean isStatic) { String
     * fieldName = ((FieldInstruction)ins).getFieldName(cpg); Type fieldType =
     * ((FieldInstruction)ins).getFieldType(cpg); Type classType =
     * ((FieldInstruction)ins).getClassType(cpg); LocalVariableGen lg2 =
     * mg.addLocalVariable("value", fieldType, null, null); int value =
     * lg2.getIndex(); Instruction storeIns, loadIns1, loadIns2;
     * InvokeInstruction shadowIns; int obj= -1; LocalVariableGen lg1=null;
     * 
     * 
     * if ((fieldType == Type.INT) || (fieldType == Type.SHORT) || (fieldType ==
     * Type.BYTE) || (fieldType == Type.CHAR) || (fieldType == Type.BOOLEAN)) {
     * storeIns = new ISTORE(value); loadIns1 = new ILOAD(value); loadIns2 = new
     * ILOAD(value); if (fieldType == Type.INT) shadowIns = new
     * INVOKESTATIC(D_createShadowInt); else if (fieldType == Type.SHORT)
     * shadowIns = new INVOKESTATIC(D_createShadowShort); else if (fieldType ==
     * Type.BYTE) shadowIns = new INVOKESTATIC(D_createShadowByte); else if
     * (fieldType == Type.CHAR) shadowIns = new
     * INVOKESTATIC(D_createShadowChar); else if (fieldType == Type.BOOLEAN)
     * shadowIns = new INVOKESTATIC(D_createShadowBoolean); else return;
     * //impossible if (!SILENT) Debugger.println("doPUTFIELD " + fieldName + " " +
     * fieldType + " " + shadowIns.getMethodName(cpg)); } else if (fieldType
     * instanceof ReferenceType) { storeIns = new ASTORE(value); loadIns1 = new
     * ALOAD(value); loadIns2 = new ALOAD(value); shadowIns = null; } else if
     * (fieldType == Type.LONG) { storeIns = new LSTORE(value); loadIns1 = new
     * LLOAD(value); loadIns2 = new LLOAD(value); shadowIns = new
     * INVOKESTATIC(D_createShadowLong); } else if (fieldType == Type.DOUBLE) {
     * storeIns = new DSTORE(value); loadIns1 = new DLOAD(value); loadIns2 = new
     * DLOAD(value); shadowIns = new INVOKESTATIC(D_createShadowDouble); } else
     * if (fieldType == Type.FLOAT) { storeIns = new FSTORE(value); loadIns1 =
     * new FLOAD(value); loadIns2 = new FLOAD(value); shadowIns = new
     * INVOKESTATIC(D_createShadowFloat); } else return;
     * 
     * lg2.setStart(patch.append(storeIns)); // This is the value to be stored.
     * 
     * if (!isStatic) { lg1 = mg.addLocalVariable("obj", Type.OBJECT, null,
     * null); obj = lg1.getIndex(); lg1.setStart(patch.append(new ASTORE(obj))); //
     * This is the object for 'this' patch.append(new ALOAD(obj)); // Load the
     * 'this' for him } patch.append(loadIns1); // Load the value for him // Now
     * the stack is restored. // Change instance var: D.change("foo.java", 75,
     * <Obj_12>, "a", <Obj_33>, tl) SET INSTANCE VAR
     * patch.append(factory.createGetStatic(className, "ODB_offset", Type.INT));
     * patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
     * line))); patch.append(new IADD()); if (isStatic) { //patch.append(new
     * PUSH(cpg, classType.toString())); //patch.append(new
     * INVOKESTATIC(D_createShadowClass));
     * //patch.append(factory.createInvoke("java.lang.Class", "forName",
     * typeClass, new Type[] {Type.STRING}, Constants.INVOKESTATIC));
     * //patch.append(new INVOKESTATIC(D_createShadowClass1));
     * patch.append(getClassFromName(classType.toString())); } else
     * lg1.setEnd(patch.append(new ALOAD(obj))); // Load the 'this' for us
     * patch.append(new PUSH(cpg, fieldName));
     * lg2.setEnd(patch.append(loadIns2)); // Load the value for us if
     * (shadowIns != null) patch.append(shadowIns); patch.append(new ALOAD(tl));
     * patch.append(new INVOKESTATIC(D_changeIV)); }
     */
    static int[] retArray = new int[10000];
    // Unlikey there'll be 10k RETs in one method.
    static int retIndex = 0; // reset every method

    public static void checkForRET() { // List ret targets in array & DON'T
        // ASTORE them
        retIndex = 0;
        for (int j = 0; j < ihs.length; j++) {
            Instruction ins = ihs[j].getInstruction();
            if (!(ins instanceof RET))
                continue;
            // system.out.println("There's a
            // RET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");// XXXXXXXXXXXXXX
            RET retIns = (RET) ins;
            int storeTarget = retIns.getIndex();
            // Debugger.println("ret "+ins +" target" + storeTarget);
            retArray[retIndex] = storeTarget;
            retIndex++;
        }
    }

    public static boolean isRetTarget(int target) {
        for (int i = 0; i < retIndex; i++)
            if (retArray[i] == target)
                return true;
        return false;
    }

    public static void doRETURN(int j) { // Just a marker -- robust
        if (isSynchronized)
            doLockReturn(j);
        if (NO_RETURN)
            return;

        if (returnType == Type.VOID) {
            patch.append(factory.createGetStatic(className, "ODB_offset",
                    Type.INT));
            patch.append(new PUSH(cpg, buildFileLineN(className,
                    sourceFileName, line)));
            patch.append(new IADD());
            patch.append(new ALOAD(tl));
            patch.append(new INVOKESTATIC(D_returnMarker_0));
            insertPatch(ihs[j], "Inserting doRETURN(void) patch");
            return;
        }

        if (returnType == Type.LONG) {
            patch.append(new DUP2());
            patch.append(new INVOKESTATIC(D_createShadowLong));
        } else if (returnType == Type.DOUBLE) {
            patch.append(new DUP2());
            patch.append(new INVOKESTATIC(D_createShadowDouble));
        } else {
            patch.append(new DUP());
            if (returnType == Type.INT)
                patch.append(new INVOKESTATIC(D_createShadowInt));
            else if (returnType == Type.SHORT)
                patch.append(new INVOKESTATIC(D_createShadowShort));
            else if (returnType == Type.BYTE)
                patch.append(new INVOKESTATIC(D_createShadowByte));
            else if (returnType == Type.CHAR)
                patch.append(new INVOKESTATIC(D_createShadowChar));
            else if (returnType == Type.BOOLEAN)
                patch.append(new INVOKESTATIC(D_createShadowBoolean));
            else if (returnType == Type.FLOAT)
                patch.append(new INVOKESTATIC(D_createShadowFloat));
            else if (returnType instanceof ReferenceType)
                ;
            else {
                Debugger.println("IMPOSSIBLE: Appending returnValue "
                        + returnType);
                return;
            }
        }
        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        patch.append(new ALOAD(tl));
        patch.append(new INVOKESTATIC(D_returnMarker_1));
        insertPatch(ihs[j], "Inserting doRETURN(value) patch");
    }

    public static void doINVOKESTATIC(int j) {
        if (NO_INVOKESTATIC)
            return;
        doINVOKEVIRTUAL(j, true, false);
    }

    // Handles ALL combinations. (collects max 10 args)
    public static void doINVOKEVIRTUAL(int j, boolean isStatic,
            boolean isSpecial) {
        doINVOKEVIRTUAL1(j, isStatic, isSpecial);
        InvokeInstruction ii = (InvokeInstruction) ins;
        String methodName = ii.getMethodName(cpg);
        String methodClass = ii.getClassName(cpg);
        Type[] types = ii.getArgumentTypes(cpg);

        if ((methodName.equals("arraycopy"))
                && (methodClass.equals("java.lang.System")))
            patch.append(factory.createInvoke("com.lambda.Debugger.MySystem",
                    "arraycopy", Type.VOID, types, Constants.INVOKESTATIC));
        else if ((methodName.equals("sort"))
                && (methodClass.equals("java.util.Arrays")))
            patch.append(factory.createInvoke("com.lambda.Debugger.MyArrays",
                    "sort", Type.VOID, types, Constants.INVOKESTATIC));
        else if ((methodName.equals("fill"))
                && (methodClass.equals("java.util.Arrays")))
            patch.append(factory.createInvoke("com.lambda.Debugger.MyArrays",
                    "fill", Type.VOID, types, Constants.INVOKESTATIC));
        else
            return;
        replacePatch(ihs[j], "Replacing w/ MySystem:\n");
    }

    public static void doINVOKEVIRTUAL1(int j, boolean isStatic,
            boolean isSpecial) {
        if (NO_INVOKEVIRTUAL)
            return;

        InvokeInstruction invokeIns;
        InvokeInstruction ii = (InvokeInstruction) ins;
        Type returnType = ii.getReturnType(cpg);
        String methodName = ii.getMethodName(cpg);
        String methodClass = ii.getClassName(cpg);
        Type[] argumentTypes = ii.getArgumentTypes(cpg);
        LocalVariableGen lvg[] = new LocalVariableGen[argumentTypes.length];
        int nArgsRecorded;
        boolean isNew = (methodName.equals("<init>"));

        if (!SILENT)
            Debugger.println("doINVOKEVIRTUAL(" + j + " " + isStatic + " "
                    + isSpecial + " " + ii + " " + methodName + " "
                    + methodClass);
        OneObj = (!dontProcessMethod(dontRecord, methodName, methodClass));
        // OneObj = (!dontRecord.contains(methodName)); // &&
        // (argumentTypes.length <= MAX_ARGS_RECORDED));
        if (!OneObj)
            return;

        if (methodName.equals("specialMethodThatBreaksDebugger"))
            patch.append(new INVOKESTATIC(D_startingWait));
        // This will produce bad byte code

        for (int i = argumentTypes.length - 1; i >= 0; i--) { // Store the
            // args for us
            type = argumentTypes[i];

            if (!SILENT)
                Debugger.println("doINVOKEVIRTUAL " + type);

            lvg[i] = mg.addLocalVariable("arg_" + i, type, null, null);
            if (!SILENT)
                Debugger.println("arg type " + type);

            if (type instanceof ReferenceType) {
                lvg[i].setStart(patch.append(new ASTORE(lvg[i].getIndex())));
            } else if ((type == Type.INT) || (type == Type.BOOLEAN)
                    || (type == Type.CHAR) || (type == Type.SHORT)
                    || (type == Type.BYTE)) {
                lvg[i].setStart(patch.append(new ISTORE(lvg[i].getIndex())));
            } else if (type == Type.LONG) {
                lvg[i].setStart(patch.append(new LSTORE(lvg[i].getIndex())));
            } else if (type == Type.FLOAT) {
                lvg[i].setStart(patch.append(new FSTORE(lvg[i].getIndex())));
            } else if (type == Type.DOUBLE) {
                lvg[i].setStart(patch.append(new DSTORE(lvg[i].getIndex())));
            }
        }

        LocalVariableGen lg1 = mg.addLocalVariable("obj", Type.OBJECT, null,
                null);
        int obj = lg1.getIndex();
        if (!isStatic) {
            lg1.setStart(patch.append(new ASTORE(obj)));
            // This is the object for 'this'
            patch.append(new ALOAD(obj)); // Load the 'this' for him
        }

        for (int i = 0; i < argumentTypes.length; i++) { // Load the args for
            // him
            type = argumentTypes[i];
            if (type instanceof ReferenceType) {
                patch.append(new ALOAD(lvg[i].getIndex()));
            } else if ((type == Type.INT) || (type == Type.BOOLEAN)
                    || (type == Type.CHAR) || (type == Type.SHORT)
                    || (type == Type.BYTE)) {
                lvg[i].setStart(patch.append(new ILOAD(lvg[i].getIndex())));
            } else if (type == Type.LONG) {
                if (!SILENT)
                    Debugger.println("LLOAD TYPE!!*************************");
                lvg[i].setStart(patch.append(new LLOAD(lvg[i].getIndex())));
            } else if (type == Type.FLOAT) {
                if (!SILENT)
                    Debugger.println("FLOAD TYPE!!*************************");
                lvg[i].setStart(patch.append(new FLOAD(lvg[i].getIndex())));
            } else if (type == Type.DOUBLE) {
                if (!SILENT)
                    Debugger.println("DLOAD TYPE!!*************************");
                lvg[i].setStart(patch.append(new DLOAD(lvg[i].getIndex())));
            }
        }
        // The stack is now back to where it was

        if (isSpecial) {
            // insertPatch(ihs[j], "Inserting INVOKESPECIAL patch");
        }

        // ************************NOW START LOADING THE ARGUMENTS FOR
        // INVOKE()*************************
        // We will only load a max of MAX_ARGS_RECORDED arguments.
        // THIS WILL BE A LITTLE TRICKY... LOAD THE FIRST MAX_ARGS_RECORDED
        // ARGUMENTS
        // public static synchronized TraceLine invoke(String sourceFileName,
        // int line, Object o, String meth)

        patch
                .append(factory.createGetStatic(className, "ODB_offset",
                        Type.INT));
        patch.append(new PUSH(cpg, buildFileLineN(className, sourceFileName,
                line)));
        patch.append(new IADD());
        if (isStatic || isNew) {
            // if (isSpecial && !isNew)
            // insertGetClass("com.lambda.Debugger.SUPER", patch); else
            insertGetClass(ii.getClassType(cpg).toString(), patch);

        } else
            lg1.setEnd(patch.append(new ALOAD(obj)));
        if (!isNew)
            patch.append(new PUSH(cpg, methodName));
        patch.append(new ALOAD(tl));

        if (argumentTypes.length > MAX_ARGS_RECORDED)
            nArgsRecorded = MAX_ARGS_RECORDED;
        else
            nArgsRecorded = argumentTypes.length;

        for (int i = 0; i < nArgsRecorded; i++) {
            type = argumentTypes[i];

            if (type instanceof ReferenceType) {
                lvg[i].setEnd(patch.append(new ALOAD(lvg[i].getIndex())));
            } else if (type == Type.INT) {
                lvg[i].setEnd(patch.append(new ILOAD(lvg[i].getIndex())));
                patch.append(new INVOKESTATIC(D_createShadowInt));
            } else if (type == Type.SHORT) {
                lvg[i].setEnd(patch.append(new ILOAD(lvg[i].getIndex())));
                patch.append(new INVOKESTATIC(D_createShadowShort));
            } else if (type == Type.BYTE) {
                lvg[i].setEnd(patch.append(new ILOAD(lvg[i].getIndex())));
                patch.append(new INVOKESTATIC(D_createShadowByte));
            } else if (type == Type.CHAR) {
                lvg[i].setEnd(patch.append(new ILOAD(lvg[i].getIndex())));
                patch.append(new INVOKESTATIC(D_createShadowChar));
            } else if (type == Type.BOOLEAN) {
                lvg[i].setEnd(patch.append(new ILOAD(lvg[i].getIndex())));
                patch.append(new INVOKESTATIC(D_createShadowBoolean));
            } else if (type == Type.LONG) {
                lvg[i].setStart(patch.append(new LLOAD(lvg[i].getIndex())));
                patch.append(new INVOKESTATIC(D_createShadowLong));
            } else if (type == Type.FLOAT) {
                lvg[i].setStart(patch.append(new FLOAD(lvg[i].getIndex())));
                patch.append(new INVOKESTATIC(D_createShadowFloat));
            } else if (type == Type.DOUBLE) {
                lvg[i].setStart(patch.append(new DLOAD(lvg[i].getIndex())));
                patch.append(new INVOKESTATIC(D_createShadowDouble));
            }
        }
        // The stack is now ready for us.

        if (isNew) {
            if (nArgsRecorded == 0)
                invokeIns = new INVOKESTATIC(D_newObj_0);
            else if (nArgsRecorded == 1)
                invokeIns = new INVOKESTATIC(D_newObj_1);
            else if (nArgsRecorded == 2)
                invokeIns = new INVOKESTATIC(D_newObj_2);
            else if (nArgsRecorded == 3)
                invokeIns = new INVOKESTATIC(D_newObj_3);
            else if (nArgsRecorded == 4)
                invokeIns = new INVOKESTATIC(D_newObj_4);
            else if (nArgsRecorded == 5)
                invokeIns = new INVOKESTATIC(D_newObj_5);
            else if (nArgsRecorded == 6)
                invokeIns = new INVOKESTATIC(D_newObj_6);
            else if (nArgsRecorded == 7)
                invokeIns = new INVOKESTATIC(D_newObj_7);
            else if (nArgsRecorded == 8)
                invokeIns = new INVOKESTATIC(D_newObj_8);
            else if (nArgsRecorded == 9)
                invokeIns = new INVOKESTATIC(D_newObj_9);
            else if (nArgsRecorded == 10)
                invokeIns = new INVOKESTATIC(D_newObj_10);
            else
                throw new DebuggerException("nArgs>MAX");

        } else {
            if (methodName.equals("exit")
                    && methodClass.equals("java.lang.System"))
                invokeIns = new INVOKESTATIC(D_exit);
            else if (nArgsRecorded == 0)
                invokeIns = new INVOKESTATIC(D_invoke_0);
            else if (nArgsRecorded == 1)
                invokeIns = new INVOKESTATIC(D_invoke_1);
            else if (nArgsRecorded == 2)
                invokeIns = new INVOKESTATIC(D_invoke_2);
            else if (nArgsRecorded == 3)
                invokeIns = new INVOKESTATIC(D_invoke_3);
            else if (nArgsRecorded == 4)
                invokeIns = new INVOKESTATIC(D_invoke_4);
            else if (nArgsRecorded == 5)
                invokeIns = new INVOKESTATIC(D_invoke_5);
            else if (nArgsRecorded == 6)
                invokeIns = new INVOKESTATIC(D_invoke_6);
            else if (nArgsRecorded == 7)
                invokeIns = new INVOKESTATIC(D_invoke_7);
            else if (nArgsRecorded == 8)
                invokeIns = new INVOKESTATIC(D_invoke_8);
            else if (nArgsRecorded == 9)
                invokeIns = new INVOKESTATIC(D_invoke_9);
            else if (nArgsRecorded == 10)
                invokeIns = new INVOKESTATIC(D_invoke_10);
            else
                throw new DebuggerException("nArgs>MAX");
        }

        patch.append(invokeIns);
        patch.append(new ASTORE(tl2)); // Save this TraceLine's return tl.

        if (methodName.equals("wait") && (argumentTypes.length == 0)) {
            // We really want Object.wait(), but we can't prove that. ??!
            patch.append(factory.createGetStatic(className, "ODB_offset",
                    Type.INT));
            patch.append(new PUSH(cpg, buildFileLineN(className,
                    sourceFileName, line)));
            patch.append(new IADD());
            patch.append(new ALOAD(obj));
            patch.append(new ALOAD(tl));
            patch.append(new INVOKESTATIC(D_startingWait));
        }
        // if (methodName.equals("join") &&
        // methodClass.equals("java.lang.Thread")) {
        if (methodName.equals("join") && (argumentTypes.length == 0)) {
            patch.append(factory.createGetStatic(className, "ODB_offset",
                    Type.INT));
            patch.append(new PUSH(cpg, buildFileLineN(className,
                    sourceFileName, line)));
            patch.append(new IADD());
            patch.append(new ALOAD(obj));
            patch.append(new ALOAD(tl));
            patch.append(new INVOKESTATIC(D_startingJoin));
        }
        insertPatch(ihs[j], "Inserting  invokevirtual/invokespecial: \n");

        // ******************************** Insert returnValue after it returns
        // ********************************

        if (NO_RETURNVALUE)
            return;

        // After the INVOKEVIRTUAL returns, we'll record the value and insert a
        // D.returnValue()
        // The stack is CLEAN when we start & finish this. Unrelated to the
        // above.

        if (methodName.equals("wait") && (argumentTypes.length == 0)) {
            patch.append(factory.createGetStatic(className, "ODB_offset",
                    Type.INT));
            patch.append(new PUSH(cpg, buildFileLineN(className,
                    sourceFileName, line)));
            patch.append(new IADD());
            patch.append(new ALOAD(obj));
            patch.append(new ALOAD(tl));
            patch.append(new INVOKESTATIC(D_endingWait));
        }

        // if (methodName.equals("join") &&
        // methodClass.equals("java.lang.Thread")) {
        if (methodName.equals("join") && (argumentTypes.length == 0)) {
            patch.append(factory.createGetStatic(className, "ODB_offset",
                    Type.INT));
            patch.append(new PUSH(cpg, buildFileLineN(className,
                    sourceFileName, line)));
            patch.append(new IADD());
            patch.append(new ALOAD(obj));
            patch.append(new ALOAD(tl));
            patch.append(new INVOKESTATIC(D_endingJoin));
        }

        if (isNew) {
            // patch.append(new ALOAD(0));
            lg1.setEnd(patch.append(new ALOAD(obj)));
            patch.append(new ALOAD(tl2));
            patch.append(new INVOKESTATIC(D_returnNew));
        } else if (returnType == Type.VOID) {
            patch.append(new ALOAD(tl2));
            patch.append(new INVOKESTATIC(D_returnValue_0));
        } else
            createReturnPatch(returnType);

        if (isNew)
            appendPatch(ihs[j], "Appending returnNew: ");
        else
            appendPatch(ihs[j], "Appending returnValue: " + returnType);

    } // doINVOKEVIRTUAL

    /*
     * public static void createReturnPatch (Type fieldType) { LocalVariableGen
     * lg1 = mg.addLocalVariable("value", fieldType, null, null); int value =
     * lg1.getIndex(); Instruction storeIns, loadIns1, loadIns2;
     * InvokeInstruction shadowIns;
     * 
     * if ((fieldType == Type.INT) || (fieldType == Type.SHORT) || (fieldType ==
     * Type.BYTE) || (fieldType == Type.CHAR) || (fieldType == Type.BOOLEAN)) {
     * storeIns = new ISTORE(value); loadIns1 = new ILOAD(value); loadIns2 = new
     * ILOAD(value); if (fieldType == Type.INT) shadowIns = new
     * INVOKESTATIC(D_createShadowInt); else if (fieldType == Type.SHORT)
     * shadowIns = new INVOKESTATIC(D_createShadowShort); else if (fieldType ==
     * Type.BYTE) shadowIns = new INVOKESTATIC(D_createShadowByte); else if
     * (fieldType == Type.CHAR) shadowIns = new
     * INVOKESTATIC(D_createShadowChar); else if (fieldType == Type.BOOLEAN)
     * shadowIns = new INVOKESTATIC(D_createShadowBoolean); else return;
     * //impossible } else if (fieldType instanceof ReferenceType) { storeIns =
     * new ASTORE(value); loadIns1 = new ALOAD(value); loadIns2 = new
     * ALOAD(value); shadowIns = null; } else if (fieldType == Type.LONG) {
     * storeIns = new LSTORE(value); loadIns1 = new LLOAD(value); loadIns2 = new
     * LLOAD(value); shadowIns = new INVOKESTATIC(D_createShadowLong); } else if
     * (fieldType == Type.DOUBLE) { storeIns = new DSTORE(value); loadIns1 = new
     * DLOAD(value); loadIns2 = new DLOAD(value); shadowIns = new
     * INVOKESTATIC(D_createShadowDouble); } else if (fieldType == Type.FLOAT) {
     * storeIns = new FSTORE(value); loadIns1 = new FLOAD(value); loadIns2 = new
     * FLOAD(value); shadowIns = new INVOKESTATIC(D_createShadowFloat); } else {
     * Debugger.println("IMPOSSIBLE: Appending returnValue " + fieldType);
     * return; }
     * 
     * lg1.setStart(patch.append(storeIns)); // `tl' valid from here
     * patch.append(loadIns1); if (shadowIns != null) patch.append(shadowIns);
     * patch.append(new ALOAD(tl2)); patch.append(new
     * INVOKESTATIC(D_returnValue_1)); lg1.setEnd(patch.append(loadIns2));
     * 
     * if (!SILENT) Debugger.println("Creating return patch " + fieldType); }
     */

    public static void createReturnPatch(Type fieldType) {
        if (fieldType instanceof ReferenceType)
            patch.append(new DUP());
        patch.append(new ALOAD(tl2));
        if (fieldType == Type.DOUBLE)
            patch.append(new INVOKESTATIC(D_returnValueD));
        if (fieldType == Type.LONG)
            patch.append(new INVOKESTATIC(D_returnValueL));
        if (fieldType == Type.INT)
            patch.append(new INVOKESTATIC(D_returnValueI));
        if (fieldType == Type.BOOLEAN)
            patch.append(new INVOKESTATIC(D_returnValueZ));
        if (fieldType == Type.FLOAT)
            patch.append(new INVOKESTATIC(D_returnValueF));
        if (fieldType == Type.SHORT)
            patch.append(new INVOKESTATIC(D_returnValueS));
        if (fieldType == Type.CHAR)
            patch.append(new INVOKESTATIC(D_returnValueC));
        if (fieldType == Type.BYTE)
            patch.append(new INVOKESTATIC(D_returnValueB));
        if (fieldType instanceof ReferenceType)
            patch.append(new INVOKESTATIC(D_returnValueA));
    }

    public static void appendPatch(InstructionHandle ih, String debug) {
        if (!SILENT)
            Debugger.println(debug + "\n" + patch);

        InstructionHandle lastIHInPatch = patch.getEnd();
        il.append(ih, patch);

        /*
         * NOT NECESSARY? BECAUSE WE PROMISE NEVER TO THROW FROM OUR CODE:
         * D_invoke() etc. for (int i = 0; i < ceg.length; i++) { // The end of
         * handlers for monitors must cover our code. InstructionHandle
         * handlerEndPC= ceg[i].getEndPC(); // The other handlers, doesn't
         * matter. if (ih == handlerEndPC) { if (!SILENT)
         * Debugger.println("appendPatch redirecting handler end from:" +ih + "
         * to: "+ lastIHInPatch); ceg[i].setEndPC(lastIHInPatch); return; } }
         */
        return;
    }

    public static void replacePatch(InstructionHandle ih, String debug) {
        if (!SILENT)
            Debugger.println(debug + "\n" + patch);

        InstructionHandle firstIHInPatch = patch.getStart();
        il.append(ih, patch);

        InstructionTargeter[] it = ih.getTargeters();
        if (it != null) {
            for (int i = 0; i < it.length; i++) {
                if (!SILENT)
                    Debugger.println("Retargeting: " + className + " " + it[i]
                            + " from " + ih + " to " + firstIHInPatch);
                if (it[i] instanceof CodeExceptionGen) {
                    it[i].updateTarget(ih, firstIHInPatch);
                } else {
                    it[i].updateTarget(ih, firstIHInPatch);
                }
            }
        }
        try {
            il.delete(ih);
        } catch (TargetLostException e) {
            Debugger.println("Retargeting failed: " + className + " from " + ih
                    + " to " + firstIHInPatch);
            System.exit(1);
        }
        return;
    }

    public static void insertPatch(InstructionHandle ih, String debug) {
        if (!SILENT)
            Debugger.println(debug + "\n" + patch);
        if (patch.size() == 0)
            return;
        InstructionHandle firstIHInPatch = patch.getStart();
        il.insert(ih, patch);

        InstructionTargeter[] it = ih.getTargeters();
        if (it != null) {
            for (int i = 0; i < it.length; i++) {
                if (it[i] instanceof CodeExceptionGen) {
                    // if (!SILENT) Debugger.println("NOT Retargeting: " + it[i]
                    // + " from " + ih + " to " + firstIHInPatch); WHY NOT?
                } else {
                    // if (!SILENT) Debugger.println("Retargeting: " + it[i] + "
                    // from " + ih + " to " + firstIHInPatch);
                    it[i].updateTarget(ih, firstIHInPatch);
                }
            }
        }
        return;
    }

    static LocalVariable[] createMissingVarTable(LocalVariableGen[] lvs) {
        InstructionHandle firstIH = ihs[0], lastIH = ihs[ihs.length - 1];
        Vector v = new Vector();
        for (int i = 0; i < lvs.length; i++) {
            LocalVariableGen lvg = lvs[i];
            // if (lvg.getName().equals("this")) continue; I HAVE TO LOOK LIKE
            // THEM!
            v.add(lvg);
        }

        checkForRET();

        for (int j = 0; j < ihs.length; j++) {
            ins = ihs[j].getInstruction();

            if ((ins instanceof IINC) || (ins instanceof StoreInstruction)) {
                int storeTarget = ((LocalVariableInstruction) ins).getIndex();
                Type type = ((LocalVariableInstruction) ins).getType(cpg);
                if (isRetTarget(storeTarget))
                    continue; // NOT an OBJECT! "astore_3; ret 3"
                if (member(storeTarget, v))
                    continue;
                String name = "var" + v.size();
                LocalVariableGen lvg = new LocalVariableGen(storeTarget, name,
                        type, firstIH, lastIH);
                v.add(lvg);
            }
        }
        LocalVariable[] newLV = new LocalVariable[v.size()];
        for (int i = 0; i < newLV.length; i++) {
            newLV[i] = ((LocalVariableGen) v.elementAt(i))
                    .getLocalVariable(cpg);
            // Debugger.println("New LV: " + newLV[i]);
        }
        return newLV;
    }

    static boolean member(int target, Vector v) {
        int len = v.size();
        for (int i = 0; i < len; i++) {
            LocalVariableGen lv = (LocalVariableGen) v.elementAt(i);
            if (lv.getIndex() == target)
                return true;
        }
        return false;
    }

    static void appendVarMapping(String methodID, MethodGen m) {
        int start = 0;
        if ((localVariables.length > 0)
                && (localVariables[0].getName().equals("this")))
            start = 1;

        patchVM.append(new PUSH(cpg, localVariables.length - start));
        patchVM.append((CPInstruction) factory.createNewArray(Type.STRING,
                (short) 1));
        LocalVariableGen lvgArray = D_ODB_declareVarMappingsMethod
                .addLocalVariable("lvArray", stringArray, null, null);
        int lvArray = lvgArray.getIndex();
        lvgArray.setStart(patchVM.append(new ASTORE(lvArray)));

        for (int j = start; j < localVariables.length; j++) {
            patchVM.append(new ALOAD(lvArray));
            patchVM.append(new PUSH(cpg, j - start));
            patchVM.append(new PUSH(cpg, localVariables[j].getName()));
            patchVM.append(new AASTORE());
        }

        patchVM.append(new PUSH(cpg, methodID));
        patchVM.append(new ALOAD(lvArray));
        patchVM.append(factory.createInvoke("com.lambda.Debugger.D",
                "appendVarNames", Type.VOID, new Type[] { Type.STRING,
                        stringArray }, Constants.INVOKESTATIC));

        LocalVariableGen[] lvgs = m.getLocalVariables();

        // Then varTypes
        patchVM.append(new PUSH(cpg, localVariables.length - start));
        patchVM.append((CPInstruction) factory.createNewArray(Type.STRING,
                (short) 1));
        patchVM.append(new ASTORE(lvArray));

        for (int j = start; j < localVariables.length; j++) {
            patchVM.append(new ALOAD(lvArray));
            patchVM.append(new PUSH(cpg, j - start));
            patchVM.append(new PUSH(cpg, localVariables[j].getSignature()));
            patchVM.append(new AASTORE());
        }

        patchVM.append(new PUSH(cpg, methodID));
        patchVM.append(new ALOAD(lvArray));
        patchVM.append(new PUSH(cpg, returnType.getSignature()));
        patchVM.append(new PUSH(cpg, className));
        patchVM.append(factory.createInvoke("java.lang.Class", "forName",
                typeClass, new Type[] { Type.STRING }, Constants.INVOKESTATIC));
        patchVM.append(factory.createInvoke("java.lang.Class",
                "getClassLoader", typeClassLoader, new Type[] {},
                Constants.INVOKEVIRTUAL));
        patchVM.append(factory.createInvoke("com.lambda.Debugger.D",
                "appendVarTypes", Type.VOID, new Type[] { Type.STRING,
                        stringArray, Type.STRING, typeClassLoader },
                Constants.INVOKESTATIC));
    }

    static InstructionList patchVM;
    static InstructionFactory factoryVM;

    static void createVarMappingsStart(ClassGen javaClass) {
        factoryVM = new InstructionFactory(cpg);
        patchVM = new MyInstructionList();
        D_ODB_declareVarMappingsMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC, Type.VOID, new Type[] {},
                new String[] {}, "ODB_declareVarMappings", className, patchVM,
                cpg);
    }

    static void createVarMappingsEnd(ClassGen javaClass) {
        if (patchVM.size() > 40000)
            throw new DebuggerException("Too many variables in class "
                    + javaClass.getClassName() + " " + patchVM.size() / 4
                    + " > 10,000");
        patchVM.append(InstructionConstants.RETURN);
        D_ODB_declareVarMappingsMethod.setMaxStack();
        javaClass.addMethod(D_ODB_declareVarMappingsMethod.getMethod());
        patchVM.dispose();
    }

    static void bindMethodNames(InstructionList patch, ConstantPoolGen cpg) {
        bindMethodNames1(patch, cpg);

        MethodGen D_getPreviousTLMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.STRING, Type.STRING, Type.INT },
                new String[] { "slIndex", "meth", "methodID", "nLocals" },
                "getPreviousTL", "com.lambda.Debugger.D", patch, cpg);
        D_getPreviousTL = cpg.addMethodref(D_getPreviousTLMethod);
        MethodGen D_addUnparentedMethod0 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT }, new String[] { "slIndex", "o", "meth",
                        "methodID", "nLocals" }, "addUnparented0",
                "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented0 = cpg.addMethodref(D_addUnparentedMethod0);
        MethodGen D_addUnparentedMethod8 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT }, new String[] { "slIndex", "o", "meth",
                        "methodID", "nLocals", "arg0", "arg1", "arg2", "arg3",
                        "arg4", "arg5", "arg6", "arg7" }, "addUnparented8",
                "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented8 = cpg.addMethodref(D_addUnparentedMethod8);
        MethodGen D_addUnparentedMethod7 = new MethodGen(
                Constants.ACC_STATIC | Constants.ACC_PUBLIC
                        | Constants.ACC_SYNCHRONIZED,
                traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT },
                new String[] { "slIndex", "o", "meth", "methodID", "nLocals",
                        "arg0", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6" },
                "addUnparented7", "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented7 = cpg.addMethodref(D_addUnparentedMethod7);
        MethodGen D_addUnparentedMethod6 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT }, new String[] {
                        "slIndex", "o", "meth", "methodID", "nLocals", "arg0",
                        "arg1", "arg2", "arg3", "arg4", "arg5" },
                "addUnparented6", "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented6 = cpg.addMethodref(D_addUnparentedMethod6);
        MethodGen D_addUnparentedMethod5 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT }, new String[] { "slIndex",
                        "o", "meth", "methodID", "nLocals", "arg0", "arg1",
                        "arg2", "arg3", "arg4" }, "addUnparented5",
                "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented5 = cpg.addMethodref(D_addUnparentedMethod5);
        MethodGen D_addUnparentedMethod4 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT },
                new String[] { "slIndex", "o", "meth", "methodID", "nLocals",
                        "arg0", "arg1", "arg2", "arg3" }, "addUnparented4",
                "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented4 = cpg.addMethodref(D_addUnparentedMethod4);
        MethodGen D_addUnparentedMethod3 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT, Type.OBJECT },
                new String[] { "slIndex", "o", "meth", "methodID", "nLocals",
                        "arg0", "arg1", "arg2" }, "addUnparented3",
                "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented3 = cpg.addMethodref(D_addUnparentedMethod3);
        MethodGen D_addUnparentedMethod2 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT }, new String[] {
                        "slIndex", "o", "meth", "methodID", "nLocals", "arg0",
                        "arg1" }, "addUnparented2", "com.lambda.Debugger.D",
                patch, cpg);
        D_addUnparented2 = cpg.addMethodref(D_addUnparentedMethod2);
        MethodGen D_addUnparentedMethod1 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT }, new String[] { "slIndex", "o",
                        "meth", "methodID", "nLocals", "arg0" },
                "addUnparented1", "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented1 = cpg.addMethodref(D_addUnparentedMethod1);
        MethodGen D_addUnparentedMethod9 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT },
                new String[] { "slIndex", "o", "meth", "methodID", "nLocals",
                        "arg0", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6",
                        "arg7", "arg8" }, "addUnparented9",
                "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented9 = cpg.addMethodref(D_addUnparentedMethod9);
        MethodGen D_addUnparentedMethod10 = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.STRING,
                        Type.INT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT }, new String[] {
                        "slIndex", "o", "meth", "methodID", "nLocals", "arg0",
                        "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7",
                        "arg8", "arg9" }, "addUnparented10",
                "com.lambda.Debugger.D", patch, cpg);
        D_addUnparented10 = cpg.addMethodref(D_addUnparentedMethod10);

        MethodGen D_changeAMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.OBJECT, Type.INT, Type.INT, traceLine },
                new String[] { "value", "slIndex", "varIndex", "tl" },
                "changeA", "com.lambda.Debugger.D", patch, cpg);

        D_changeA = cpg.addMethodref(D_changeAMethod);
        MethodGen D_changeIMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.INT,
                new Type[] { Type.INT, Type.INT, Type.INT, traceLine },
                new String[] { "value", "slIndex", "varIndex", "tl" },
                "changeI", "com.lambda.Debugger.D", patch, cpg);

        D_changeI = cpg.addMethodref(D_changeIMethod);
        MethodGen D_changeIvoidMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.INT, Type.INT, traceLine },
                new String[] { "value", "slIndex", "varIndex", "tl" },
                "changeIvoid", "com.lambda.Debugger.D", patch, cpg);

        D_changeIvoid = cpg.addMethodref(D_changeIvoidMethod);
        MethodGen D_changeLMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.LONG,
                new Type[] { Type.LONG, Type.INT, Type.INT, traceLine },
                new String[] { "value", "slIndex", "varIndex", "tl" },
                "changeL", "com.lambda.Debugger.D", patch, cpg);

        D_changeL = cpg.addMethodref(D_changeLMethod);
        MethodGen D_changeBMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.BYTE,
                new Type[] { Type.BYTE, Type.INT, Type.INT, traceLine },
                new String[] { "value", "slIndex", "varIndex", "tl" },
                "changeB", "com.lambda.Debugger.D", patch, cpg);

        D_changeB = cpg.addMethodref(D_changeBMethod);
        MethodGen D_changeZMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.BOOLEAN, new Type[] { Type.BOOLEAN, Type.INT, Type.INT,
                        traceLine }, new String[] { "value", "slIndex",
                        "varIndex", "tl" }, "changeZ", "com.lambda.Debugger.D",
                patch, cpg);

        D_changeZ = cpg.addMethodref(D_changeZMethod);
        MethodGen D_changeCMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.CHAR,
                new Type[] { Type.CHAR, Type.INT, Type.INT, traceLine },
                new String[] { "value", "slIndex", "varIndex", "tl" },
                "changeC", "com.lambda.Debugger.D", patch, cpg);

        D_changeC = cpg.addMethodref(D_changeCMethod);
        MethodGen D_changeSMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.SHORT, new Type[] { Type.SHORT, Type.INT, Type.INT,
                        traceLine }, new String[] { "value", "slIndex",
                        "varIndex", "tl" }, "changeS", "com.lambda.Debugger.D",
                patch, cpg);

        D_changeS = cpg.addMethodref(D_changeSMethod);
        MethodGen D_changeFMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.FLOAT, new Type[] { Type.FLOAT, Type.INT, Type.INT,
                        traceLine }, new String[] { "value", "slIndex",
                        "varIndex", "tl" }, "changeF", "com.lambda.Debugger.D",
                patch, cpg);

        D_changeF = cpg.addMethodref(D_changeFMethod);
        MethodGen D_changeDMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.DOUBLE, new Type[] { Type.DOUBLE, Type.INT, Type.INT,
                        traceLine }, new String[] { "value", "slIndex",
                        "varIndex", "tl" }, "changeD", "com.lambda.Debugger.D",
                patch, cpg);

        D_changeD = cpg.addMethodref(D_changeDMethod);

        D_bindMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.INT, Type.OBJECT, traceLine },
                new String[] { "slIndex", "varIndex", "value", "tl" }, "bind",
                "com.lambda.Debugger.D", patch, cpg);

        D_bind = cpg.addMethodref(D_bindMethod);

        D_newArrayMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.OBJECT, Type.INT },
                // NOTE REVERSED ORDER! (so I can use DUP)
                new String[] { "array", "slIndex" }, "newArray",
                "com.lambda.Debugger.D", patch, cpg);

        D_newArray = cpg.addMethodref(D_newArrayMethod);

        // ChangeD instance var: D.changeD("foo.java", 75, <Obj_12>, "a",
        // <Obj_33>, tl) SET INSTANCE VAR
        D_changeIVMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, Type.OBJECT,
                        traceLine }, new String[] { "slIndex", "o", "varName",
                        "value", "tl" }, "change", "com.lambda.Debugger.D",
                patch, cpg);
        D_changeIV = cpg.addMethodref(D_changeIVMethod);

        MethodGen D_changeIVAMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.OBJECT, Type.OBJECT, Type.INT, Type.STRING,
                        traceLine }, new String[] { "o", "value", "slIndex",
                        "varName", "tl" }, "changeIVA",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVA = cpg.addMethodref(D_changeIVAMethod);
        MethodGen D_changeIVBMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.BYTE,
                new Type[] { Type.OBJECT, Type.BYTE, Type.INT, Type.STRING,
                        traceLine }, new String[] { "o", "value", "slIndex",
                        "varName", "tl" }, "changeIVB",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVB = cpg.addMethodref(D_changeIVBMethod);
        MethodGen D_changeIVCMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.CHAR,
                new Type[] { Type.OBJECT, Type.CHAR, Type.INT, Type.STRING,
                        traceLine }, new String[] { "o", "value", "slIndex",
                        "varName", "tl" }, "changeIVC",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVC = cpg.addMethodref(D_changeIVCMethod);
        MethodGen D_changeIVSMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.SHORT, new Type[] { Type.OBJECT, Type.SHORT, Type.INT,
                        Type.STRING, traceLine }, new String[] { "o", "value",
                        "slIndex", "varName", "tl" }, "changeIVS",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVS = cpg.addMethodref(D_changeIVSMethod);
        MethodGen D_changeIVIMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.INT,
                new Type[] { Type.OBJECT, Type.INT, Type.INT, Type.STRING,
                        traceLine }, new String[] { "o", "value", "slIndex",
                        "varName", "tl" }, "changeIVI",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVI = cpg.addMethodref(D_changeIVIMethod);
        MethodGen D_changeIVLMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.LONG,
                new Type[] { Type.OBJECT, Type.LONG, Type.INT, Type.STRING,
                        traceLine }, new String[] { "o", "value", "slIndex",
                        "varName", "tl" }, "changeIVL",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVL = cpg.addMethodref(D_changeIVLMethod);
        MethodGen D_changeIVFMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.FLOAT, new Type[] { Type.OBJECT, Type.FLOAT, Type.INT,
                        Type.STRING, traceLine }, new String[] { "o", "value",
                        "slIndex", "varName", "tl" }, "changeIVF",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVF = cpg.addMethodref(D_changeIVFMethod);
        MethodGen D_changeIVDMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.DOUBLE, new Type[] { Type.OBJECT, Type.DOUBLE, Type.INT,
                        Type.STRING, traceLine }, new String[] { "o", "value",
                        "slIndex", "varName", "tl" }, "changeIVD",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVD = cpg.addMethodref(D_changeIVDMethod);
        MethodGen D_changeIVZMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.BOOLEAN, new Type[] { Type.OBJECT, Type.BOOLEAN, Type.INT,
                        Type.STRING, traceLine }, new String[] { "o", "value",
                        "slIndex", "varName", "tl" }, "changeIVZ",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeIVZ = cpg.addMethodref(D_changeIVZMethod);

        MethodGen D_changeArrayAMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.OBJECT, Type.INT, Type.OBJECT, Type.INT,
                        traceLine }, new String[] { "array", "index", "value",
                        "slIndex", "tl" }, "changeArrayA",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayA = cpg.addMethodref(D_changeArrayAMethod);
        MethodGen D_changeArrayZMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.BOOLEAN, new Type[] { Type.OBJECT, Type.INT, Type.BOOLEAN,
                        Type.INT, traceLine }, new String[] { "array", "index",
                        "value", "slIndex", "tl" }, "changeArrayZ",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayZ = cpg.addMethodref(D_changeArrayZMethod);
        MethodGen D_changeArrayBMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.BYTE,
                new Type[] { Type.OBJECT, Type.INT, Type.BYTE, Type.INT,
                        traceLine }, new String[] { "array", "index", "value",
                        "slIndex", "tl" }, "changeArrayB",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayB = cpg.addMethodref(D_changeArrayBMethod);
        MethodGen D_changeArrayCMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.CHAR,
                new Type[] { Type.OBJECT, Type.INT, Type.CHAR, Type.INT,
                        traceLine }, new String[] { "array", "index", "value",
                        "slIndex", "tl" }, "changeArrayC",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayC = cpg.addMethodref(D_changeArrayCMethod);
        MethodGen D_changeArraySMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.SHORT, new Type[] { Type.OBJECT, Type.INT, Type.SHORT,
                        Type.INT, traceLine }, new String[] { "array", "index",
                        "value", "slIndex", "tl" }, "changeArrayS",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayS = cpg.addMethodref(D_changeArraySMethod);
        MethodGen D_changeArrayIMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.INT,
                new Type[] { Type.OBJECT, Type.INT, Type.INT, Type.INT,
                        traceLine }, new String[] { "array", "index", "value",
                        "slIndex", "tl" }, "changeArrayI",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayI = cpg.addMethodref(D_changeArrayIMethod);
        MethodGen D_changeArrayLMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.LONG,
                new Type[] { Type.OBJECT, Type.INT, Type.LONG, Type.INT,
                        traceLine }, new String[] { "array", "index", "value",
                        "slIndex", "tl" }, "changeArrayL",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayL = cpg.addMethodref(D_changeArrayLMethod);
        MethodGen D_changeArrayFMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.FLOAT, new Type[] { Type.OBJECT, Type.INT, Type.FLOAT,
                        Type.INT, traceLine }, new String[] { "array", "index",
                        "value", "slIndex", "tl" }, "changeArrayF",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayF = cpg.addMethodref(D_changeArrayFMethod);
        MethodGen D_changeArrayDMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.DOUBLE, new Type[] { Type.OBJECT, Type.INT, Type.DOUBLE,
                        Type.INT, traceLine }, new String[] { "array", "index",
                        "value", "slIndex", "tl" }, "changeArrayD",
                "com.lambda.Debugger.D", patch, cpg);
        D_changeArrayD = cpg.addMethodref(D_changeArrayDMethod);
    }

    static void bindMethodNames1(InstructionList patch, ConstantPoolGen cpg) {

        // The int types
        D_createShadowClassMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.OBJECT, new Type[] { Type.STRING },
                new String[] { "className" }, "createShadowClass",
                "com.lambda.Debugger.D", patch, cpg);
        D_createShadowClass = cpg.addMethodref(D_createShadowClassMethod);
        D_createShadowClass1Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.OBJECT, new Type[] { typeClass },
                new String[] { "className" }, "createShadowClass",
                "com.lambda.Debugger.D", patch, cpg);
        D_createShadowClass1 = cpg.addMethodref(D_createShadowClass1Method);
        D_createShadowShortMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                typeShadowShort, new Type[] { Type.SHORT },
                new String[] { "i" }, "createShadowShort",
                "com.lambda.Debugger.D", patch, cpg);
        D_createShadowShort = cpg.addMethodref(D_createShadowShortMethod);
        D_createShadowByteMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                typeShadowByte, new Type[] { Type.BYTE }, new String[] { "i" },
                "createShadowByte", "com.lambda.Debugger.D", patch, cpg);
        D_createShadowByte = cpg.addMethodref(D_createShadowByteMethod);
        D_createShadowCharMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                typeShadowChar, new Type[] { Type.CHAR }, new String[] { "i" },
                "createShadowChar", "com.lambda.Debugger.D", patch, cpg);
        D_createShadowChar = cpg.addMethodref(D_createShadowCharMethod);
        D_createShadowBooleanMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                typeShadowBoolean, new Type[] { Type.BOOLEAN },
                new String[] { "i" }, "createShadowBoolean",
                "com.lambda.Debugger.D", patch, cpg);
        D_createShadowBoolean = cpg.addMethodref(D_createShadowBooleanMethod);
        D_createShadowIntMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                typeShadowInt, new Type[] { Type.INT }, new String[] { "i" },
                "createShadowInt", "com.lambda.Debugger.D", patch, cpg);
        D_createShadowInt = cpg.addMethodref(D_createShadowIntMethod);
        D_createShadowLongMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                typeShadowLong, new Type[] { Type.LONG }, new String[] { "i" },
                "createShadowLong", "com.lambda.Debugger.D", patch, cpg);
        D_createShadowLong = cpg.addMethodref(D_createShadowLongMethod);

        D_createShadowFloatMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                typeShadowFloat, new Type[] { Type.FLOAT },
                new String[] { "i" }, "createShadowFloat",
                "com.lambda.Debugger.D", patch, cpg);
        D_createShadowFloat = cpg.addMethodref(D_createShadowFloatMethod);

        D_createShadowDoubleMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                typeShadowDouble, new Type[] { Type.DOUBLE },
                new String[] { "i" }, "createShadowDouble",
                "com.lambda.Debugger.D", patch, cpg);
        D_createShadowDouble = cpg.addMethodref(D_createShadowDoubleMethod);

        D_returnValue_0Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { traceLine }, new String[] { "tl" }, "returnValue",
                "com.lambda.Debugger.D", patch, cpg);
        D_returnValue_0 = cpg.addMethodref(D_returnValue_0Method);

        MethodGen D_returnValueAMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.OBJECT, traceLine }, new String[] { "rv",
                        "tl" }, "returnValue", "com.lambda.Debugger.D", patch,
                cpg);
        D_returnValueA = cpg.addMethodref(D_returnValueAMethod);
        // XXX
        MethodGen D_returnValueBMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.BYTE,
                new Type[] { Type.BYTE, traceLine },
                new String[] { "rv", "tl" }, "returnValueB",
                "com.lambda.Debugger.D", patch, cpg);
        D_returnValueB = cpg.addMethodref(D_returnValueBMethod);
        MethodGen D_returnValueCMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.CHAR,
                new Type[] { Type.CHAR, traceLine },
                new String[] { "rv", "tl" }, "returnValueC",
                "com.lambda.Debugger.D", patch, cpg);
        D_returnValueC = cpg.addMethodref(D_returnValueCMethod);
        MethodGen D_returnValueSMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.SHORT, new Type[] { Type.SHORT, traceLine }, new String[] {
                        "rv", "tl" }, "returnValueS", "com.lambda.Debugger.D",
                patch, cpg);
        D_returnValueS = cpg.addMethodref(D_returnValueSMethod);
        MethodGen D_returnValueIMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.INT,
                new Type[] { Type.INT, traceLine },
                new String[] { "rv", "tl" }, "returnValueI",
                "com.lambda.Debugger.D", patch, cpg);
        D_returnValueI = cpg.addMethodref(D_returnValueIMethod);
        MethodGen D_returnValueZMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.BOOLEAN, new Type[] { Type.BOOLEAN, traceLine },
                new String[] { "rv", "tl" }, "returnValueZ",
                "com.lambda.Debugger.D", patch, cpg);
        D_returnValueZ = cpg.addMethodref(D_returnValueZMethod);
        MethodGen D_returnValueLMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.LONG,
                new Type[] { Type.LONG, traceLine },
                new String[] { "rv", "tl" }, "returnValueL",
                "com.lambda.Debugger.D", patch, cpg);
        D_returnValueL = cpg.addMethodref(D_returnValueLMethod);
        MethodGen D_returnValueFMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.FLOAT, new Type[] { Type.FLOAT, traceLine }, new String[] {
                        "rv", "tl" }, "returnValueF", "com.lambda.Debugger.D",
                patch, cpg);
        D_returnValueF = cpg.addMethodref(D_returnValueFMethod);
        MethodGen D_returnValueDMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED,
                Type.DOUBLE, new Type[] { Type.DOUBLE, traceLine },
                new String[] { "rv", "tl" }, "returnValueD",
                "com.lambda.Debugger.D", patch, cpg);
        D_returnValueD = cpg.addMethodref(D_returnValueDMethod);
        // YYY
        D_returnNewMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.OBJECT, traceLine },
                // SWITCHED ORDER!!!
                new String[] { "rv", "tl" }, "returnNew",
                "com.lambda.Debugger.D", patch, cpg);
        D_returnNew = cpg.addMethodref(D_returnNewMethod);

        // public static synchronized void throwEx(String slIndex, int line,
        // Object ex, TraceLine tl)
        D_athrowMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "ex", "tl" }, "throwEx",
                "com.lambda.Debugger.D", patch, cpg);
        D_athrow = cpg.addMethodref(D_athrowMethod);

        // public static synchronized void catchEx(String slIndex, int line,
        // Object ex, TraceLine tl)
        D_catchMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "ex", "tl" }, "catchEx",
                "com.lambda.Debugger.D", patch, cpg);
        D_catch = cpg.addMethodref(D_catchMethod);

        D_exitMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT }, new String[] { "slIndex", "o", "meth",
                        "tl", "arg1" }, "exit", "com.lambda.Debugger.D", patch,
                cpg);
        D_exit = cpg.addMethodref(D_exitMethod);

        D_invoke_0Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine },
                new String[] { "slIndex", "o", "meth", "tl" }, "invoke",
                "com.lambda.Debugger.D", patch, cpg);
        D_invoke_0 = cpg.addMethodref(D_invoke_0Method);

        D_invoke_1Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT }, new String[] { "slIndex", "o", "meth",
                        "tl", "arg1" }, "invoke", "com.lambda.Debugger.D",
                patch, cpg);
        D_invoke_1 = cpg.addMethodref(D_invoke_1Method);

        D_invoke_2Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT }, new String[] { "slIndex",
                        "o", "meth", "tl", "arg1", "arg2" }, "invoke",
                "com.lambda.Debugger.D", patch, cpg);
        D_invoke_2 = cpg.addMethodref(D_invoke_2Method);

        D_invoke_3Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT }, new String[] {
                        "slIndex", "o", "meth", "tl", "arg1", "arg2", "arg3" },
                "invoke", "com.lambda.Debugger.D", patch, cpg);
        D_invoke_3 = cpg.addMethodref(D_invoke_3Method);

        D_invoke_4Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT },
                new String[] { "slIndex", "o", "meth", "tl", "arg1", "arg2",
                        "arg3", "arg4" }, "invoke", "com.lambda.Debugger.D",
                patch, cpg);
        D_invoke_4 = cpg.addMethodref(D_invoke_4Method);

        D_invoke_5Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT }, new String[] { "slIndex", "o", "meth",
                        "tl", "arg1", "arg2", "arg3", "arg4", "arg5" },
                "invoke", "com.lambda.Debugger.D", patch, cpg);
        D_invoke_5 = cpg.addMethodref(D_invoke_5Method);
        MethodGen D_invoke_6Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT }, new String[] { "slIndex",
                        "o", "meth", "tl", "arg1", "arg2", "arg3", "arg4",
                        "arg5", "arg6" }, "invoke", "com.lambda.Debugger.D",
                patch, cpg);
        D_invoke_6 = cpg.addMethodref(D_invoke_6Method);
        MethodGen D_invoke_7Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT }, new String[] {
                        "slIndex", "o", "meth", "tl", "arg1", "arg2", "arg3",
                        "arg4", "arg5", "arg6", "arg7" }, "invoke",
                "com.lambda.Debugger.D", patch, cpg);
        D_invoke_7 = cpg.addMethodref(D_invoke_7Method);
        MethodGen D_invoke_8Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT },
                new String[] { "slIndex", "o", "meth", "tl", "arg1", "arg2",
                        "arg3", "arg4", "arg5", "arg6", "arg7", "arg8" },
                "invoke", "com.lambda.Debugger.D", patch, cpg);
        D_invoke_8 = cpg.addMethodref(D_invoke_8Method);
        MethodGen D_invoke_9Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT }, new String[] { "slIndex", "o", "meth",
                        "tl", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6",
                        "arg7", "arg8", "arg9" }, "invoke",
                "com.lambda.Debugger.D", patch, cpg);
        D_invoke_9 = cpg.addMethodref(D_invoke_9Method);
        MethodGen D_invoke_10Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, Type.STRING, traceLine,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT }, new String[] { "slIndex",
                        "o", "meth", "tl", "arg1", "arg2", "arg3", "arg4",
                        "arg5", "arg6", "arg7", "arg8", "arg9", "arg10" },
                "invoke", "com.lambda.Debugger.D", patch, cpg);
        D_invoke_10 = cpg.addMethodref(D_invoke_10Method);

        D_newObj_0Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "o", "tl" }, "newObj",
                "com.lambda.Debugger.D", patch, cpg);
        D_newObj_0 = cpg.addMethodref(D_newObj_0Method);

        D_newObj_1Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT },
                new String[] { "slIndex", "o", "tl", "arg1" }, "newObj",
                "com.lambda.Debugger.D", patch, cpg);
        D_newObj_1 = cpg.addMethodref(D_newObj_1Method);

        D_newObj_2Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT }, new String[] { "slIndex", "o", "tl",
                        "arg1", "arg2" }, "newObj", "com.lambda.Debugger.D",
                patch, cpg);
        D_newObj_2 = cpg.addMethodref(D_newObj_2Method);

        D_newObj_3Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT }, new String[] { "slIndex",
                        "o", "tl", "arg1", "arg2", "arg3" }, "newObj",
                "com.lambda.Debugger.D", patch, cpg);
        D_newObj_3 = cpg.addMethodref(D_newObj_3Method);

        D_newObj_4Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT }, new String[] {
                        "slIndex", "o", "tl", "arg1", "arg2", "arg3", "arg4" },
                "newObj", "com.lambda.Debugger.D", patch, cpg);
        D_newObj_4 = cpg.addMethodref(D_newObj_4Method);

        D_newObj_5Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT },
                new String[] { "slIndex", "o", "tl", "arg1", "arg2", "arg3",
                        "arg4", "arg5" }, "newObj", "com.lambda.Debugger.D",
                patch, cpg);
        D_newObj_5 = cpg.addMethodref(D_newObj_5Method);

        MethodGen D_newObj_6Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT }, new String[] { "slIndex", "o", "tl",
                        "arg1", "arg2", "arg3", "arg4", "arg5", "arg6" },
                "newObj", "com.lambda.Debugger.D", patch, cpg);
        D_newObj_6 = cpg.addMethodref(D_newObj_6Method);
        MethodGen D_newObj_7Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT }, new String[] { "slIndex",
                        "o", "tl", "arg1", "arg2", "arg3", "arg4", "arg5",
                        "arg6", "arg7" }, "newObj", "com.lambda.Debugger.D",
                patch, cpg);
        D_newObj_7 = cpg.addMethodref(D_newObj_7Method);
        MethodGen D_newObj_8Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT }, new String[] {
                        "slIndex", "o", "tl", "arg1", "arg2", "arg3", "arg4",
                        "arg5", "arg6", "arg7", "arg8" }, "newObj",
                "com.lambda.Debugger.D", patch, cpg);
        D_newObj_8 = cpg.addMethodref(D_newObj_8Method);
        MethodGen D_newObj_9Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT },
                new String[] { "slIndex", "o", "tl", "arg1", "arg2", "arg3",
                        "arg4", "arg5", "arg6", "arg7", "arg8", "arg9" },
                "newObj", "com.lambda.Debugger.D", patch, cpg);
        D_newObj_9 = cpg.addMethodref(D_newObj_9Method);
        MethodGen D_newObj_10Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, traceLine,
                new Type[] { Type.INT, Type.OBJECT, traceLine, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT,
                        Type.OBJECT }, new String[] { "slIndex", "o", "tl",
                        "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7",
                        "arg8", "arg9", "arg10" }, "newObj",
                "com.lambda.Debugger.D", patch, cpg);
        D_newObj_10 = cpg.addMethodref(D_newObj_10Method);

        D_returnMarkerMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, traceLine }, new String[] { "slIndex",
                        "tl" }, "returnMarker", "com.lambda.Debugger.D", patch,
                cpg);
        D_returnMarker_0 = cpg.addMethodref(D_returnMarkerMethod);

        D_returnMarker_1Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.OBJECT, Type.INT, traceLine }, new String[] {
                        "rv", "slIndex", "tl" },
                // INVERTED ARGS
                "returnMarker", "com.lambda.Debugger.D", patch, cpg);
        D_returnMarker_1 = cpg.addMethodref(D_returnMarker_1Method);

        // void newObj(String slIndex, int line, Object o)

        D_gettingLock_Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "o", "tl" }, "gettingLock",
                "com.lambda.Debugger.D", patch, cpg);
        D_gettingLock = cpg.addMethodref(D_gettingLock_Method);

        D_gotLock_Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "o", "tl" }, "gotLock",
                "com.lambda.Debugger.D", patch, cpg);
        D_gotLock = cpg.addMethodref(D_gotLock_Method);

        D_releasingLock_Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "o", "tl" }, "releasingLock",
                "com.lambda.Debugger.D", patch, cpg);
        D_releasingLock = cpg.addMethodref(D_releasingLock_Method);

        D_startingWait_Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "o", "tl" }, "startingWait",
                "com.lambda.Debugger.D", patch, cpg);
        D_startingWait = cpg.addMethodref(D_startingWait_Method);

        D_endingWait_Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "o", "tl" }, "endingWait",
                "com.lambda.Debugger.D", patch, cpg);
        D_endingWait = cpg.addMethodref(D_endingWait_Method);

        D_startingJoin_Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "o", "tl" }, "startingJoin",
                "com.lambda.Debugger.D", patch, cpg);
        D_startingJoin = cpg.addMethodref(D_startingJoin_Method);

        D_endingJoin_Method = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC | Constants.ACC_SYNCHRONIZED, Type.VOID,
                new Type[] { Type.INT, Type.OBJECT, traceLine }, new String[] {
                        "slIndex", "o", "tl" }, "endingJoin",
                "com.lambda.Debugger.D", patch, cpg);
        D_endingJoin = cpg.addMethodref(D_endingJoin_Method);
    }

    static MethodGen D_ODB_declareVarMappingsMethod;
    static int D_ODB_declareVarMappings;

    static MethodGen D_gettingLock_Method = null;
    static MethodGen D_gotLock_Method = null;
    static MethodGen D_releasingLock_Method = null;
    static MethodGen D_startingWait_Method = null;
    static MethodGen D_endingWait_Method = null;
    static MethodGen D_startingJoin_Method = null;
    static MethodGen D_endingJoin_Method = null;
    static MethodGen D_addUnparentedMethod = null;
    static MethodGen D_addUnparentedMethod_1 = null;
    static MethodGen D_invokeMethod = null;
    static MethodGen D_changeMethod = null;
    static MethodGen D_bindMethod = null;
    static MethodGen D_newArrayMethod = null;
    static MethodGen D_changeIVMethod = null;
    static MethodGen D_changeArray1DMethod = null;
    static MethodGen D_createShadowClassMethod = null;
    static MethodGen D_createShadowClass1Method = null;
    static MethodGen D_createShadowIntMethod = null;
    static MethodGen D_createShadowShortMethod = null;
    static MethodGen D_createShadowByteMethod = null;
    static MethodGen D_createShadowCharMethod = null;
    static MethodGen D_createShadowBooleanMethod = null;
    static MethodGen D_createShadowFloatMethod = null;
    static MethodGen D_createShadowLongMethod = null;
    static MethodGen D_createShadowDoubleMethod = null;
    static MethodGen D_returnValue_0Method = null;
    static MethodGen D_returnValue_1Method = null;
    static MethodGen D_returnNewMethod = null;
    static MethodGen D_exitMethod = null;
    static MethodGen D_invoke_0Method = null;
    static MethodGen D_invoke_1Method = null;
    static MethodGen D_invoke_2Method = null;
    static MethodGen D_invoke_3Method = null;
    static MethodGen D_invoke_4Method = null;
    static MethodGen D_invoke_5Method = null;
    static MethodGen D_newObj_0Method = null;
    static MethodGen D_newObj_1Method = null;
    static MethodGen D_newObj_2Method = null;
    static MethodGen D_newObj_3Method = null;
    static MethodGen D_newObj_4Method = null;
    static MethodGen D_newObj_5Method = null;
    static MethodGen D_athrowMethod = null;
    static MethodGen D_catchMethod = null;
    // static MethodGen D_stampMethod = null;
    static MethodGen D_returnMarkerMethod = null;
    static MethodGen D_returnMarker_1Method = null;
    // static MethodGen D_newObjMethod = null;

    static boolean warningPrinted = false;

    public static void printCompilerFlag() {
        if (warningPrinted)
            return;
        Debugger.println("\n********************  PLEASE COMPILE "
                + sourceFileName + " WITH -g FLAG!  ********************");
        warningPrinted = true;
    }

    // AspectJ:
    // "Display1.java;spacewar/Debug.java[1k];coordination/Coordinator.java[2k];spacewar/RegistrySynchronization.java[3k]"
    // Java: "Display.java"

    static Vector slVector;
    static int slIndex;
    static Hashtable slTable;

    static void reset() {
        nMethods = 0;
        processedCLINIT = false;
        slVector = new Vector();
        slIndex = 0;
        slTable = new Hashtable();
    }

    static int buildFileLineN(String className, String sourceFileName, int line) {
        if (line == -1)
            return -1; // No source lines
        String fl = buildFileLine(className, sourceFileName, line);
        // Debugger.println("throw at "+ sourceFileName + line);
        Integer i = (Integer) slTable.get(fl);
        return i.intValue();
    }

    static String buildFileLine(String className, String sourceFileName,
            int line) {
        String fl = buildFileLine0(className, sourceFileName, line);
        Integer i = (Integer) slTable.get(fl);
        if (i == null) {
            slTable.put(fl, new Integer(slIndex));
            slVector.add(fl);
            slIndex++;
        }
        return fl;
    }

    static String buildFileLine0(String className, String sourceFileName,
            int line) {
        int start0 = 0, start1 = 0;
        int semi = sourceFileName.indexOf(";");
        if (semi < 0)
            return (className + ":" + sourceFileName + ":" + line); // Java
        if (line < 0) {
            if (!SILENT)
                Debugger.println("Bad line number: " + sourceFileName + ":"
                        + line);
            return (className + ":UnknownFile.java:2"); // Java
        }

        Vector v = new Vector();
        {
            String fn0 = sourceFileName, fn;
            while (semi > 0) {
                semi = fn0.indexOf(";");
                if (semi > 0) {
                    fn = fn0.substring(0, semi);
                    // fn1 = "spacewar/Debug.java[1k]"
                    fn0 = fn0.substring(semi + 1, fn0.length());
                    // fn0 = "coordination/Coordinator.java[2k];spacewar/..."
                } else
                    fn = fn0;
                v.add(fn);
            }
        }

        String fn0, fn1 = null;
        int line0 = -1;
        for (int i = 0; i < v.size(); i++) {
            fn0 = (String) v.elementAt(i);
            if (fn0.endsWith("]")) {
                int open = fn0.indexOf("[");
                String startString = fn0.substring(open + 1, fn0.length() - 1);
                // startString = "2k"
                if (startString.endsWith("k"))
                    start0 = Integer.parseInt(startString.substring(0,
                            startString.length() - 1)) * 1000;
                // start = 2000
                else
                    // throw new DebuggerException("Unknown start line
                    // "+startString+" in " +sourceFileName);
                    throw new NullPointerException("Unknown start line "
                            + startString + " in " + sourceFileName);
                fn0 = fn0.substring(0, open);
                // fn = "coordination/Coordinator.java"
            } else
                start0 = 0;

            if (line >= start0) {
                start1 = start0;
                fn1 = fn0;
            } else {
                line0 = line - start1;
                int slash = fn1.lastIndexOf("/");
                if (slash > 0)
                    fn1 = fn1.substring(slash + 1, fn1.length());
                // fn1 = "Coordinator.java"
                // Debugger.println("AspectJ: " + sourceFileName + ":"+line+" ->
                // " +fn1+":"+line0);
                return (className + ":" + fn1 + ":" + line0);
            }
        }
        line0 = line - start1;
        int slash = fn1.lastIndexOf("/");
        if (slash > 0)
            fn1 = fn1.substring(slash + 1, fn1.length());
        // fn1 = "Coordinator.java"
        // Debugger.println("AspectJ: " + sourceFileName + ":"+line+" -> "
        // +fn1+":"+line0);
        return (className + ":" + fn1 + ":" + line0);
    }

    public static void verify(String className) {
        Verifier v = VerifierFactory.getVerifier(className);
        VerificationResult vr;

        vr = v.doPass1();
        if (vr != VerificationResult.VR_OK)
            throw new DebuggerException("Verifier Error in: " + className);

        vr = v.doPass2();
        if (vr != VerificationResult.VR_OK)
            throw new DebuggerException("Verifier Error in: " + className);

        JavaClass jc = null;
        try {
            jc = Repository.lookupClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        for (int i = 0; i < jc.getMethods().length; i++) {
            vr = v.doPass3a(i);
            if (vr != VerificationResult.VR_OK) {
                Debugger.println("" + vr);
                throw new DebuggerException("Verifier Error in: " + className);
            }
            vr = v.doPass3b(i);
            if (vr != VerificationResult.VR_OK) {
                Debugger.println("" + vr);
                throw new DebuggerException("Verifier Error in: " + className);
            }
        }

    }

    private static int getLineNumber(int pc) {
        int line;
        if (lineNumberTable == null)
            return -1;
        try {
            line = lineNumberTable.getSourceLine(pc);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Debugger.println("Bad pos in lineNumberTable for: "+pc+"
            // instruction: " + ihs[pc]); // This sucks! Fix BCEL
            line = -1;
        }
        return line;
    }

    private static HashMap classTable = new HashMap();

    private static void insertGetClass(String className1, InstructionList patch) {
        IFNONNULL branch2;

        String varName = getClassVar(className1);

        patch.append(factory.createGetStatic(className, varName, typeClass));
        patch.append(new DUP());
        patch.append(branch2 = new IFNONNULL(null));
        patch.append(new POP());
        patch.append(new PUSH(cpg, className1));
        patch.append(factory.createInvoke("java.lang.Class", "forName",
                typeClass, new Type[] { Type.STRING }, Constants.INVOKESTATIC));
        patch.append(new DUP());
        patch.append(factory.createPutStatic(className, varName, typeClass));
        branch2.setTarget(patch.append(new NOP()));

    }

    private static String getClassVar(String className1) {
        String varName = (String) classTable.get(className1);
        if (varName == null) {
            varName = "ODB_classVar_" + classTable.size();
            classTable.put(className1, varName);
        }
        return varName;
    }

    static void createClassNameMethod(ClassGen classGen) {
        InstructionFactory factory = new InstructionFactory(cpg);
        InstructionList patch = new MyInstructionList();
        MethodGen D_ODB_classNameMethod = new MethodGen(Constants.ACC_STATIC
                | Constants.ACC_PUBLIC, Type.VOID, new Type[] {},
                new String[] {}, "ODB_classNameMethod", className, patch, cpg);

        Iterator it = classTable.keySet().iterator();
        while (it.hasNext()) {
            String className1 = (String) it.next();
            String varName = (String) classTable.get(className1);
            Field f = new FieldGen(Constants.ACC_STATIC | Constants.ACC_FINAL
                    | Constants.ACC_PRIVATE, typeClass, varName, cpg)
                    .getField();
            classGen.addField(f);
            /*
             * patch.append(new PUSH(cpg, className1)); patch.append(
             * factory.createInvoke( "java.lang.Class", "forName", typeClass,
             * new Type[] { Type.STRING }, Constants.INVOKESTATIC));
             * patch.append( factory.createPutStatic(className, varName,
             * typeClass));
             */
        }
        patch.append(InstructionConstants.RETURN);
        D_ODB_classNameMethod.setMaxStack();
        classGen.addMethod(D_ODB_classNameMethod.getMethod());
        patch.dispose();
    }

}

class MyInstructionList extends InstructionList {

    public InstructionHandle append(Instruction arg0) {
        if (!Debugify.SILENT)
            Debugger.println("\t" + arg0);
        return super.append(arg0);
    }

    public BranchHandle append(BranchInstruction arg0) {
        if (!Debugify.SILENT)
            Debugger.println("\t" + arg0);
        return super.append(arg0);
    }

    public InstructionHandle append(CompoundInstruction arg0) {
        if (!Debugify.SILENT)
            Debugger.println("\t" + arg0);
        return super.append(arg0);
    }

}
