/*                        StackFrameImpl.java

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

package edu.insa.LSD;
import com.lambda.Debugger.*;
import java.util.*;

public class StackFrameImpl implements StackFrame  {

    TraceLine 		tl;
    VectorD		shadowLocals;
    

    StackFrameImpl(TraceLine tl) {this.tl = tl;}

    public Tuple getAllVariables() {
	ArrayList al = new ArrayList();
	Locals l = tl.locals;
	if (l == null) return Tuple.EMPTY;
	VectorD v = shadowLocals;
	if (v == null) v = l.createShadowLocals();
	//	int nLocals = l.getNLocals();
	int nLocals = v.size();
	for (int i = 0; i < nLocals; i++) {
	    ShadowLocal sl = (ShadowLocal)v.elementAt(i);
	    String varName = sl.varName();
	    Class varType = sl.getVarType();
	    Object varValue = sl.value();
	    Object[] os = {varName, varType, varValue};
	    al.add(new Tuple(os));
	}
	return new Tuple(al);
    }

    public String getMethodName() {return tl.getMethod();}
    public Object getThisObject() {return((tl.thisObj instanceof Class) ? Value.INVALID : tl.thisObj);}
    public Object getThisObjectClass() {return((tl.thisObj instanceof Class) ? tl.thisObj : tl.thisObj.getClass());}
    public boolean getIsMethodStatic() {return(tl.thisObj instanceof Class);}
    public Object getVarName(int i) {return getVar(i).varName();}
    //public Object getVarType(int i) {return getVar(i).getVarType();}
    public Object getVarValue(int i) {return getVar(i).value();}
    public int    getnVars() {VectorD v = initVars(); return v.size();}
    public int    getnArgs() {return tl.getArgCount();}


    private VectorD initVars() {
	VectorD v = shadowLocals;
	if (v == null) {
	    Locals l = tl.locals;
	    if (l == null)
		shadowLocals = new VectorD();
	    else
		shadowLocals = l.createShadowLocals();
	}
	return shadowLocals;
    }

    private ShadowLocal getVar(int i) {
	VectorD v = initVars();
	if (i >= v.size()) return null;
	ShadowLocal sl = (ShadowLocal)v.elementAt(i);
	return sl;
    }

    public Tuple getAllArguments() {
	return null;
    }

    public String toString() {
	return "<StackFrameImpl >";
    }


}
