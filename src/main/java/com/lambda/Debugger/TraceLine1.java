/*                        TraceLine1.java

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


public class TraceLine1 extends TraceLine {
    private Object arg0;

    public Object getArgActual(int i) {
	if (i == 0) return arg0;
	return null;
    }

    public void putArg(int i, Object value) {
	if (i == 0) arg0=value;
    }

    public int getArgCount() {
	return(1);
    }

    public TraceLine1(int time, String meth, Object t, int threadIndex, TraceLine tl,
		      Object a0) {
	super(time, meth, t, threadIndex, tl);
	arg0 = a0;
    }

}

