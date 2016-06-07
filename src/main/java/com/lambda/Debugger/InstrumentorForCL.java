package com.lambda.Debugger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;

/**
 */

public class InstrumentorForCL {
    private static Class debugClassLoader;
    private static java.lang.reflect.Method method;


    // Instrument ClassLoader.class to call InstrumentorForCL.debugify() before defineClass0(). Then write
    // both files out to ClassLoader.jar.
    public void instrument(String destJar) throws Exception {
        File dest = new File(destJar);
        if (dest.exists() && !dest.canWrite()) {
            throw new Exception(destJar + " exists and is not writable");
        }

        // patch the java.lang.ClassLoader
        InputStream is = ClassLoader.getSystemClassLoader().getParent().getResourceAsStream("java/lang/ClassLoader.class");
        byte[] bytes = inputStreamToByteArray(is);
        is.close();
        byte[] patched = patchCL(bytes);


	// Include this file in the ajr
        is = ClassLoader.getSystemClassLoader().getResourceAsStream("com/lambda/Debugger/InstrumentorForCL.class");
        bytes = inputStreamToByteArray(is);
        is.close();

        // pack the jar file
        Manifest mf = new Manifest();
        Attributes at = mf.getMainAttributes();
        at.putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
        at.putValue("Created-By", "ODB [java " + System.getProperty("java.version") + "]");
        CRC32 crc = new CRC32();
        JarOutputStream jar = new JarOutputStream(new FileOutputStream(dest), mf);

	{
        ZipEntry entry = new ZipEntry("java/lang/ClassLoader.class");
        entry.setSize(patched.length);
        entry.setCrc(crc.getValue());
        crc.update(patched);
        jar.putNextEntry(entry);
        jar.write(patched);
        jar.closeEntry();
	}
	{
        ZipEntry entry = new ZipEntry("com/lambda/Debugger/InstrumentorForCL.class");
        entry.setSize(bytes.length);
        entry.setCrc(crc.getValue());
        crc.update(bytes);
        jar.putNextEntry(entry);
        jar.write(bytes);
        jar.closeEntry();
	}

        jar.close();
    }

    public static byte[] inputStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (int b = is.read(); b != -1; b = is.read()) {
            os.write(b);
        }
        return os.toByteArray();
    }


    public byte[] patchCL(byte[] b) {
        try {

            final ClassGen cg = new ClassGen(new ClassParser(new ByteArrayInputStream(b), "<generated>").parse());
            final String className = cg.getClassName();
            final Method[] methods = cg.getMethods();
            final ConstantPoolGen cpg = cg.getConstantPool();
            final InstructionFactory factory = new InstructionFactory(cg);

            // for all methods, look for caller side "this.define0" calls
            for (int i = 0; i < methods.length; i++) {
                final MethodGen mg = new MethodGen(methods[i], className, cpg);
                final InstructionList il = mg.getInstructionList();
                if (il == null) continue;
                InstructionHandle ih = il.getStart();
		String methodName = methods[i].getName();

                while (ih != null) {
                    final Instruction ins = ih.getInstruction();
                    if (ins instanceof INVOKESPECIAL
                            || ins instanceof INVOKESTATIC
                            || ins instanceof INVOKEVIRTUAL) {
                        final InvokeInstruction invokeInst = (InvokeInstruction)ins;
                        final String callerSideMethodClassName = invokeInst.getClassName(cpg);
                        final String callerSideMethodName = invokeInst.getMethodName(cpg);

                        if ("java.lang.ClassLoader".equals(callerSideMethodClassName)
                                && "defineClass0".equals(callerSideMethodName)) {

                            //assert compliant JRE
                            Type args[] = invokeInst.getArgumentTypes(cpg);
                            assertSupported(args);

                            // store former method args in local vars
                            InstructionHandle ihc = null;
                            if (args.length > 5) {
                                // IBM like JRE with extra args
                                ihc = il.append(ih.getPrev(), factory.createStore(args[args.length - 1], 2100 + args.length - 1));
                                for (int index = args.length - 2; index >= 5; index--) {
                                    ihc = il.append(ihc, InstructionFactory.createStore(args[index], 2100 + index));
                                }
                                ihc = il.append(ihc, factory.createStore(Type.OBJECT, 2016));//protection domain
                            }
                            else {
                                // SUN regular JRE
                                ihc = il.append(ih.getPrev(), factory.createStore(Type.OBJECT, 2016));//protection domain
                            }

                            ihc = il.append(ihc, factory.createStore(Type.INT, 2015));//length
                            ihc = il.append(ihc, factory.createStore(Type.INT, 2014));//index
                            ihc = il.append(ihc, factory.createStore(Type.OBJECT, 2013));//bytes
                            ihc = il.append(ihc, factory.createStore(Type.OBJECT, 2012));//name

                            // prepare method call stack
                            ihc = il.append(ihc, factory.createLoad(Type.OBJECT, 2012));
                            ihc = il.append(ihc, factory.createLoad(Type.OBJECT, 2013));
                            ihc = il.append(ihc, factory.createInvoke(
                                    "com.lambda.Debugger.InstrumentorForCL",
                                    "debugify",
                                    new ArrayType(Type.BYTE, 1),
                                    new Type[]{
                                        Type.STRING,
                                        new ArrayType(Type.BYTE, 1),
				    },
                                    Constants.INVOKESTATIC));
                            ihc = il.append(ihc, factory.createStore(Type.OBJECT, 3018));//result bytes

                            // rebuild former method call stack
                            ihc = il.append(ihc, factory.createLoad(Type.OBJECT, 2012));//name
                            ihc = il.append(ihc, factory.createLoad(Type.OBJECT, 3018));//bytes
                            ihc = il.append(ihc, new PUSH(cpg, 0));
                            ihc = il.append(ihc, factory.createLoad(Type.OBJECT, 3018));//bytes
                            ihc = il.append(ihc, InstructionConstants.ARRAYLENGTH);//.length
                            ihc = il.append(ihc, factory.createLoad(Type.OBJECT, 2016));//protection domain

                            // extra args for IBM like JRE
                            if (args.length > 5) {
                                for (int index = 5; index < args.length; index++) {
                                    ihc = il.append(ihc, factory.createLoad(args[index], 2100 + index));
                                }
                            }
                        }
                    }
                    ih = ih.getNext();
                }
                mg.setInstructionList(il);
                mg.setMaxLocals();
                mg.setMaxStack();
                methods[i] = mg.getMethod();
            }
            cg.setMethods(methods);
            return cg.getJavaClass().getBytes();
        }
        catch (Exception e) {
            System.err.println("failed to patch ClassLoader:");
            e.printStackTrace();
            return b;
        }
    }



    /**
     * Check the signature of defineClass0
     * @param args
     */
    private static void assertSupported(Type[] args) {
        if (args.length >= 5 &&
                (
                args[0].getSignature().equals("Ljava/lang/String;")
                && args[1].getSignature().equals("[B")
                && args[2].getSignature().equals("I")
                && args[3].getSignature().equals("I")
                && args[4].getSignature().equals("Ljava/security/ProtectionDomain;")
                ))
            ;
        else {
            StringBuffer sign = new StringBuffer("(");
            for (int i = 0; i < args.length; i++) {
                sign.append(args[i].toString());
                if (i < args.length - 1)
                    sign.append(", ");
            }
            sign.append(")");
            throw new Error("non standard JDK, native call not supported " + sign.toString());
        }
    }



    // Use reflection to avoid putting the Debugger on bootclasspath. NO! ON PATH!
    public static synchronized byte[] debugify(String name, byte[] b) {
	if (name.startsWith("java")) return b;
	if (name.startsWith("JAVAX")) return b;
	if (name.startsWith("edu.insa.LSD")) return b;
	if (name.startsWith("lambda.Debugger")) return b;
	if (name.startsWith("com.lambda.Debugger")) return b;
	if (name.startsWith("org.apache.bcel")) return b;
	//	System.out.println("Defining: " + name);
	try {
	    if (debugClassLoader == null) {  // NB: Not using this as a classloader, just calling debugify().
		debugClassLoader = ClassLoader.getSystemClassLoader().loadClass("com.lambda.Debugger.DebugifyingClassLoader");
		method = debugClassLoader.getDeclaredMethod("debugify", new Class[] { String.class, byte[].class });
	    }

	    Object[] argList = new Object[] {name, b};
	    byte[] patched = (byte[])method.invoke(null, argList);	// DebugifyingClassLoader.debugify("com.l.MyClass", byte[..])
	    return patched;
	}	
	catch (Exception e) {
	    System.out.println("ODB: IMPOSSIBLE. Missing debugify");
	    e.printStackTrace();
	    System.exit(1);
	}
	return b;
    }



    public static void main(String args[]) throws Exception {
	String destination = "/Users/bil/Debugger/com";
	if (args.length > 0) destination = args[0];
	destination += "ClassLoader.jar";
	InstrumentorForCL ins = new InstrumentorForCL();
	ins.instrument(destination);
	System.out.println("Patched ClassLoader written to: " + destination);
    }


}
