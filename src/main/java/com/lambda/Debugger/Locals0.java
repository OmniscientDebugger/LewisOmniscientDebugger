/*                        Locals0.java

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



public class Locals0 extends  Locals  {

    public int getNLocals() {
	return tl.getArgCount();
    }

    public void setValue(int varIndex, int time, Object value) {
	switch (varIndex) {
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    void putHL(int i, HistoryList hl) {
	switch (i) {
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    public Object getObject(int i) {
	switch (i) {
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    public int getTime(int i) {
	switch (i) {
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }

    public void setTime(int i, int time) {
	switch (i) {
	default:	throw new DebuggerException("getHL IMPOSSIBLE");
	}
    }



    // Constructors

    protected Locals0(int time, TraceLine tl, String methodID) {
	super(time, tl, methodID);
    }


}
