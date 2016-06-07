/*                        SideEffects.java

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


public class SideEffects {
  private static HashMapEq		methods = new HashMapEq(7);		// {{"frob" <VectorD MyObj YourObj>} ... }
  static {
      VectorD v = new VectorD(1); v.add(Arrays.class); methods.put("sort", v);
      VectorD vv = new VectorD(); vv.add(System.class); methods.put("arraycopy", vv);
  }

  public static void check(Object obj, String method, TraceLine tl) {
      if ((method != "sort") || (obj != Arrays.class))
	  if ((method != "arraycopy") || (obj != System.class)) return;// quick hack 

    VectorD v = (VectorD)methods.get(method);
    if (v == null) return;

    if (obj instanceof Class) {
      for (int i = v.size()-1; i > -1; i--) {
	if (obj == v.elementAt(i)) {	
	  recordChanges(obj, tl);
	  return;
	}
      }
    }
    else {
      for (int i = v.size()-1; i > -1; i--) {
	if (obj.getClass() == (Class)v.elementAt(i)) {		// NEED SUBTYPES TOO
	  recordChanges(obj, tl);
	  return;
	}
      }
    }
  }

  private static void recordChanges(Object obj, TraceLine tl) {
    int len = tl.getArgCount();
    Shadow.record(obj);
    for (int i = 0; i < len; i++) Shadow.record(tl.getArg(i));
  }

  
}
