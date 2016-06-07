/*                        Value.java

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


public class Value {

    protected Object		value;
    public static Value 	INVALID = new Value("INVALID");		// MUST not match any value, esp. itself
    public static Value 	NOVALUE =  new Value("NOVALUE");

    // Constructors

    Value() {}						// Tuples don't set value.
    Value(Object s) {
	if (s instanceof String) s = ((String)s).intern();
	value=s;
    }
    Value(int i) {value=ShadowInt.createShadowInt(i);}


    public Object getValue() {return value;}
    

    public String toString() {
	if (value == null)  return ""+value;
	if (this == INVALID)  return "INVALID";
	if (this == NOVALUE)  return "NOVALUE";
	if (value instanceof String) return "\""+value+"\"";
	if (value instanceof ShadowPrimitive) return ""+value;
	if (value instanceof Value) return "<V "+value+">";		// Neverhappen?
	String ps = getPrintName(value);
	return ps;
    }
	
    public static String getPrintName(Object o) {
	if (o == null) return "null";
	
	if (o instanceof String) {
	    return("\""+o+"\"");
	}

	if (o == Dashes.DASHES) return "--";
	if (o instanceof ShadowPrimitive) return o.toString();

	State state = StateImpl.getState();
	if (state instanceof StaticState) return state.getPrintName(o);
	Shadow sh = Shadow.get(o);
	if (sh == null) return o.toString();
	return sh.printString();
    }


}
