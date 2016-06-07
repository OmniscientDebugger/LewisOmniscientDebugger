/*                        VariableValue.java

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

import java.util.*;

public class VariableValue extends Value {
    static HashMap		table = new HashMap();
    public static final VariableValue	ANYVALUE = new VariableValue("ANYVALUE");		// Not in table.

    String				name;


    // Constructors

    VariableValue(String s) {super(NOVALUE);name=s;}

    public static VariableValue create(String s) {
	s = s.intern();
	VariableValue vv = (VariableValue)table.get(s);
	if (vv != null) return vv;
	vv = new VariableValue(s);
	table.put(s, vv);
	return vv;
    }



    public void setValue(Object v) {
	if (this == ANYVALUE) return;
	value = v;
    }
    public void reset() {value = NOVALUE;}
    public boolean notSet() {return((value==NOVALUE) || (this==ANYVALUE));}
    public static void clear() {table.clear();}



    public static void printVars() {
	Iterator iter =  table.values().iterator();
	while (iter.hasNext()) {
	    VariableValue vv = (VariableValue) iter.next();
	    System.out.println(vv);
	}
    }

    public static String printString() {
	String s = "          % ";
	Iterator iter =  table.values().iterator();
	while (iter.hasNext()) {
	    VariableValue vv = (VariableValue) iter.next();
	    s += vv+"   ";
	}

	return s;
    }

    public String toString() {
	if (value instanceof Tuple) return name + ":" + value;
	return name + ":" + super.toString();
    }

}
