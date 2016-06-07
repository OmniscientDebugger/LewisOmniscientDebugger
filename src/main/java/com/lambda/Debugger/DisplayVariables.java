/*                        DisplayVariables.java

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


/*
 */


import java.util.*;


public class DisplayVariables {
    private static HashMap 		varsDisplayed = new HashMap();

    public static List getDisplayedVariables(Shadow sh) {
	Class c = sh.classInfo.clazz;
	List l = (List) varsDisplayed.get(c);
	return(l);		// null indicates no entry 
    }

    public static void restore(Shadow sh) {
	Class c = sh.obj.getClass();
	varsDisplayed.put(c, null);
    }

    public static void removeVar(ShadowInstanceVariable siv) {
	HistoryList hl = siv.getHL();
	String varName = siv.getVarName();
	Object obj = siv.s.obj;
	Class c = obj.getClass();
	List l = (List) varsDisplayed.get(c);
	if (l == null) {
	    l = new LinkedList();
	    ClassInformation ci = siv.s.classInfo;
	    for (int i = 0; i < ci.size(); i++) l.add(ci.getVarName(i));
	    varsDisplayed.put(c, l);
	}
	l.remove(varName);
    }

    public static void main(String[] args) {
	DisplayVariables dv = new DisplayVariables();
	dv.test();
    }

    private void test() {
	DVThing target = new DVThing();
	Shadow sh = Shadow.get(target);
	ShadowInstanceVariable siv = new ShadowInstanceVariable(sh, 2);
	removeVar(siv);
	List l = getDisplayedVariables(sh);
	System.out.println("Vars left "+l);
    }


private class DVThing {
    public int i,j,k,l,m,n,o,p;
}


}
