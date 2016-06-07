/*                        Locals2.java

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



public class Locals2 extends  Locals  {

    private int			time0=-1,time1=-1;
    private Object		value0,value1;

    public int getNLocals() {
	return tl.getArgCount()+2;
    }

    public void setValue(int varIndex, int time, Object value) {
	switch (varIndex) {
	case 0:		value0 = value; time0 = time; return;
	case 1:		value1 = value; time1 = time; return;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    void putHL(int i, HistoryList hl) {
	switch (i) {
	case 0:		value0 = hl; return;
	case 1:		value1 = hl; return;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    public Object getObject(int i) {
	switch (i) {
	case 0:		return value0;
	case 1:		return value1;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    public int getTime(int i) {
	switch (i) {
	case 0:		return time0;
	case 1:		return time1;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }


    public void setTime(int varIndex, int time) {
	switch (varIndex) {
	case 0:		time0 = time; return;
	case 1:	        time1 = time; return;
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    
    // Constructors 
    protected Locals2(int time, TraceLine tl, String methodID) {
	super(time, tl, methodID);
    }

}
