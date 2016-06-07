/*                        TestLoadClass.java

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


import java.io.IOException;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class TestLoadClass {

    public static void main( String args[] ) {
	String cd = System.getProperty("INSTRUMENT");
	if (cd != null)
	    Debugger.INSTRUMENT = true;
	else
	    Debugger.INSTRUMENT = false;
	Debugger.classLoader = Thread.currentThread().getContextClassLoader();     //new DebugifyingClassLoader();
	String className;
	Class clazz0=null, clazz1=null;

	Debugger.TRACE_LOADER = true;
	System.out.println("Test-loading classes " + Debugger.version);

	for (int i = 0; i < args.length; i++) {
	    String classFile = args[i];
	    if (!classFile.endsWith(".class")) {
		System.out.println("Not a class file?! "+classFile);
		continue;
	    }

	    //	    System.out.println("Test-loading: " + classFile);

	    try {
		JavaClass javaClass = new ClassParser(classFile).parse();
		className = javaClass.getClassName();
	    }
	    catch(IOException e) {
		System.out.println("Can't parse class??? "+classFile);
		e.printStackTrace();
		continue;
	    }

	    //	    Debugger.INSTRUMENT = true;
      
	    try {
		clazz0 = Debugger.classLoader.loadClass(className);
		clazz0.getDeclaredMethod("mainx", new Class[] { });
	    }
	    catch (NoSuchMethodException e) {}
	    catch (java.lang.NoClassDefFoundError e) {}
	    catch (Exception e) {
		System.err.println("****************Problem with " + className +" "+e);
		continue;
	    }
	    catch (Error e) {
		System.err.println("****************Problem with " + className +" "+e);
		continue;
	    }
	    //	    System.out.println("Normal-loaded:  " + classFile);

	    //	    Debugger.INSTRUMENT = true;
	    try {
		clazz1 = Debugger.classLoader.loadClass(className);
		clazz1.getDeclaredMethod("mainx", new Class[] { });
	    }
	    catch (NoSuchMethodException e) {}
	    catch (java.lang.NoClassDefFoundError e) {}
	    catch (Exception e) {
		System.err.println("****************Problem with " + className +" "+e);
		continue;
	    }
	    catch (Error e) {
		System.err.println("****************Problem with " + className +" "+e);
		continue;
	    }

	    //	    System.out.println("Test-loaded:  " + classFile);
	    //	    System.out.println(clazz0 == clazz1);
	}
	System.out.println("Done.");    
    }
}
