/*                        LocksList.java

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

//              Shadow.java

/*
 */


import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// TimeStamp.trimToLength() recognizes this to display a LL as "{Thread-1, Thread-2, Thread-3}"

public class LocksList extends Shadow implements Cloneable {
    private Vector v  = new Vector();

    public LocksList() {
	super();
	classInfo = ClassInformation.get(LocksList.class);
    }

    public void add(Object o) {v.add(o);}

    public int size() {return v.size();}

    public String toString() {
	StringBuffer sb = new StringBuffer();

	if (v.size() == 0) return "{}";
	sb.append(TimeStamp.trimToLength(v.elementAt(0), 20));

	for (int i = 1; i < v.size(); i++) {
	    Object o = v.elementAt(i);
	    String s = TimeStamp.trimToLength(o, 20);
	    sb.append(", ");
	    sb.append(s);
	}
	return("{"+sb+"}");
    }

    public LockerPair removeLP(Thread tid) {
	int len = v.size();

	for (int i = 0; i < len; i++) {
	    LockerPair lp = (LockerPair)v.elementAt(i);

	    if (lp == null) return null;
	    if (lp.getThread() == tid) {
		v.remove(i);
		return lp;
	    }
	}
	return null;
    }

    public HistoryList getShadowVar(int IVIndex) {
	HistoryList hl = new HistoryListSingleton(0, v.elementAt(IVIndex));
	return hl;
    }
    
    public String getVarName(int IVIndex) {return "";}

    public synchronized Object clone() {
	try { 
	    LocksList ll = (LocksList)super.clone();
	    v = (Vector) v.clone();
	    return ll;
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }


}
