/*                        Locals9.java

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

//              Locals/Locals.java

import java.util.*;


public class Locals9 extends  Locals  {

    private int			time0=-1;
    private Object		value0;
    private int			time1=-1;
    private Object		value1;
    private int			time2=-1;
    private Object		value2;
    private int			time3=-1;
    private Object		value3;
    private int			time4=-1;
    private Object		value4;
    private int			time5=-1;
    private Object		value5;
    private int			time6=-1;
    private Object		value6;
    private int			time7=-1;
    private Object		value7;
    private int			time8=-1;
    private Object		value8;


    public int getNLocals() {
	return tl.getArgCount()+9;
    }

    public void setValue(int varIndex, int time, Object value) {
	switch (varIndex) {
	case 0:		value0 = value; time0 = time; return;
	case 1:		value1 = value; time1 = time; return;
	case 2:		value2 = value; time2 = time; return;
	case 3:		value3 = value; time3 = time; return;
	case 4:		value4 = value; time4 = time; return;
	case 5:		value5 = value; time5 = time; return;
	case 6:		value6 = value; time6 = time; return;
	case 7:		value7 = value; time7 = time; return;
	case 8:		value8 = value; time8 = time; return;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    void putHL(int i, HistoryList hl) {
	switch (i) {
	case 0:		value0 = hl; return;
	case 1:		value1 = hl; return;
	case 2:		value2 = hl; return;
	case 3:		value3 = hl; return;
	case 4:		value4 = hl; return;
	case 5:		value5 = hl; return;
	case 6:		value6 = hl; return;
	case 7:		value7 = hl; return;
	case 8:		value8 = hl; return;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    public Object getObject(int i) {
	switch (i) {
	case 0:		return value0;
	case 1:		return value1;
	case 2:		return value2;
	case 3:		return value3;
	case 4:		return value4;
	case 5:		return value5;
	case 6:		return value6;
	case 7:		return value7;
	case 8:		return value8;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    public int getTime(int i) {
	switch (i) {
	case 0:		return time0;
	case 1:		return time1;
	case 2:		return time2;
	case 3:		return time3;
	case 4:		return time4;
	case 5:		return time5;
	case 6:		return time6;
	case 7:		return time7;
	case 8:		return time8;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    public void setTime(int varIndex, int time) {
	switch (varIndex) {
	case 0:		time0 = time; return;
	case 1:		time1 = time; return;
	case 2:		time2 = time; return;
	case 3:		time3 = time; return;
	case 4:		time4 = time; return;
	case 5:		time5 = time; return;
	case 6:		time6 = time; return;
	case 7:		time7 = time; return;
	case 8:		time8 = time; return;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }


    // Constructors 
    protected Locals9(int time, TraceLine tl, String methodID) {
	super(time, tl, methodID);
    }

}
