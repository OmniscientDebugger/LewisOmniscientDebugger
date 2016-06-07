/*                        TraceLine0.java

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


public class TraceLine0 extends TraceLine  {

  public Object getArgActual(int i) {
    throw new DebuggerException("getArgActual(i>MAX) " + i);
  }

  public void putArg(int i, Object value) {
      throw new DebuggerException("putArg(i>MAX) " + i);
  }

  public int getArgCount() {
    return(0);
  }

    public TraceLine0(int time, String meth, Object t, int threadIndex, TraceLine tl) {
	super(time, meth, t, threadIndex, tl);
    }
}
