/*                        RecordedState.java

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

public class RecordedState extends StateImpl {
    private int			time = -1;




    private RecordedState() {}
    private RecordedState(TraceLine tl) {super(tl); time = TimeStamp.ct(); STATE = this;}
    private RecordedState(int time) {
	super(time);
	this.time = time;
    }


    public static State createRecordedState(int time) {
	STATE = new RecordedState(time);
	return STATE;
    }


    public static State createRecordedState(TraceLine tl) {
	STATE = new RecordedState(tl);
	return STATE;
    }


    // These are the FAST public methods this class provides. (All others return INVALID)
    public Object[]  getAllObjects() {
	ArrayList al = new ArrayList();
	Iterator iter = getObjectsIterator();
	while(iter.hasNext()) al.add(iter.next());
	return al.toArray(new Object[al.size()]);
    }

    public Iterator getObjectsIterator() {return new ShadowObjectIterator(this);}
    public Iterator getObjectsIterator(Class c) {return new ShadowObjectIterator(this, c);}


    public int getnInstanceVars(Object o) {
	Shadow sh = Shadow.getNoCreation(o);
	int nVars = sh.classInfo.getnInstanceVars();
	return nVars;
    }

    public Object getInstanceVarValue(Object o, String varName) {
	Shadow sh = Shadow.getNoCreation(o);
	int varIndex = sh.classInfo.getVarIndex(varName);
	if (varIndex == -1) return Value.INVALID;
	return getInstanceVarValue(o, varIndex);
    }

    public Object getInstanceVarValue(Object o, int varIndex) {
	if (varIndex == -1) return Value.INVALID;
	Shadow sh = Shadow.getNoCreation(o);
	if (sh == null)  return Value.INVALID;
	int nVars = sh.size();
	if (varIndex >= nVars) return Value.INVALID;
	HistoryList hl = sh.getShadowVar(varIndex);
	if (hl == null) return Value.INVALID;
	return hl.valueOn(time, false);
    }
	

    public StackFrame[] getAllStackFrames() {
	ArrayList al = new ArrayList();

	for (TraceLine tl1 = tl; tl1 != TraceLine.TOP_TRACELINE; tl1 = tl1.traceLine) al.add(new StackFrameImpl(tl1));
	StackFrame[] a = new StackFrame[al.size()];
	al.toArray(a);
	return a;
    }

    int getTime() {return time;}

    public Object getInstanceVarName(Object o, int i) {
	Shadow sh = Shadow.getNoCreation(o);
	if (sh == null) return Value.INVALID;
	int nVars = sh.size();
	if (i >= nVars) return Value.INVALID;
	return sh.getVarName(i);
    }

    public Object getInstanceVarType(Object o, int i) {
	Shadow sh = Shadow.getNoCreation(o);
	if (sh == null) return Value.INVALID;
	int nVars = sh.size();
	if (i >= nVars) return Value.INVALID;
	return sh.getVarType(i);
    }




    public void printAll() {
	StackFrame[] sf = getAllStackFrames();
	int len = sf.length;
	System.out.println(" --- StackFrame ---");
	for (int i = 0; i < len; i++) {
	    String mn = sf[i].getMethodName();
	    Object o = sf[i].getThisObject();
	    if (sf[i].getIsMethodStatic()) o = sf[i].getThisObjectClass();
	    Tuple t = sf[i].getAllVariables();
	    System.out.println("\t"+Event.printString(o) +"."+mn+"(): " + t);
	}


	Iterator iter = getObjectsIterator();
	System.out.println(" --- Objects ---");
	while(iter.hasNext()) {
	    Object o = iter.next();
	    Shadow sh = Shadow.getNoCreation(o);
	    String name = sh.printString();
	    System.out.println("\t"+name);
	    int len2 = 5;
	    for (int j = 0; j < len2; j++) {
		Object varName = getInstanceVarName(o, j);
		if (varName == Value.INVALID) continue;
		String vn = (String) varName;
		Object value = getInstanceVarValue(o, j);
		//Object value1 = getInstanceVarValue(o, vn);
		//System.out.println("\t\t"+varName+"\t\t"+getPrintName(value)+"\t\t"+getPrintName(value1));
		System.out.println("\t\t"+varName+"\t\t"+getPrintName(value));
	    }
	}
	System.out.println("");
    }
}
