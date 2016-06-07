/*                        Tuple.java

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

/*
  [1, 2, 3]						<Tuple [1 2 3]>
  ["Foo", "Bar", THIRD]					<Tuple ["Foo" "Bar" <VV THIRD>]>
  [<Demo_1> | REST]     				<Tuple [<Demo_1>], <VV REST>>
  [[<Demo_1> | REST1], [_ | REST2] | REST3]		<Tuple [<Tuple [<Demo_1>], REST1>, <Tuple [<VV ANY>], <VV REST2>>], <VV REST3>>
*/


public class Tuple extends Value {

    private Object[] 		list;
    private VariableValue	rest;
    public static Tuple		EMPTY = new Tuple(new Object[0]);
    
    // Constructors

    Tuple(ArrayList al) {list = al.toArray();}
    Tuple(Object[] o) {list=o;}
    Tuple(Object[] o, VariableValue v) {
	//	if (!(v instanceof VariableValue) && !(v instanceof Tuple)) throw new LSDException("Rest must be a Tuple: " + v);
	list=o;
	rest = v;
    }



    public void printVars() {
	int len = list.length;
	for (int i = 0; i < len; i++) {
	    Object v = get(i);
	    if (v instanceof VariableValue) System.out.println("\t"+v);
	    if (v instanceof Tuple) {
		Tuple t = (Tuple) v;
		t.printVars();
	    }

	}
	if (rest  instanceof VariableValue) System.out.println("\t"+rest);
    }


    // [a, b, c, d].tail(1) -> [d]
    public Tuple tail(int i) {
	if (i > list.length) throw new LSDException("IMPOSSIBLE "+this+" "+i);
	Object[] newv = new Object[i];
	System.arraycopy(list, list.length-i, newv, 0, i);
	return new Tuple(newv);
    }

    public void resetVariables() {
	for (int i = 0; i < list.length; i++) {
	    Object value = get(i);
	    if (value instanceof VariableValue) {
		VariableValue vv = (VariableValue) value;
		vv.reset();
	    }
	    if (value instanceof Tuple) {
		Tuple t = (Tuple) value;
		t.resetVariables();
	    }
	}
	if (rest instanceof VariableValue) {
	    VariableValue vv = (VariableValue) rest;
	    vv.reset();
	}
    }



    public Object get(int i) {return list[i];}
    public int size() {return list.length;}
    public Value rest() {return rest;}


    public String toStringStatic(StaticState ss) {			// When called from StaticState use Penumbra
	String plist = "[";
	if (list.length == 0) 
	    plist = "[]";
	else {
	    for (int i = 0; i < list.length-1; i++) plist+=ss.getPrintName(list[i])+", ";
	    plist+=ss.getPrintName(list[list.length-1])+"]";
	}
	if (rest == null) return plist;
	//	return "<Tuple " + plist +", "+ rest +">";
	return plist +" | "+ rest;
    }

    public String toString() {
	String plist = "[";
	if (list.length == 0) 
	    plist = "[]";
	else {
	    for (int i = 0; i < list.length-1; i++) plist+=toString(list[i])+", ";
	    plist+=toString(list[list.length-1])+"]";
	}
	if (rest == null) return plist;
	//	return "<Tuple " + plist +", "+ rest +">";
	return plist +" | "+ rest;
    }

    private String toString(Object value) {
	if (value == null)  return "null";
	if (value instanceof String) return "\""+value+"\"";
	if (value instanceof ShadowPrimitive) return value.toString();
	if (value instanceof Value) return value.toString();
	String ps = Value.getPrintName(value);
	return ps;
    }

    public static void main(String[] args) {
	Object[] o1 = {("Foo"), ("Bar"), VariableValue.create("THIRD")};
	Tuple t1 = new Tuple(o1);
	System.out.println(t1);

	Object[] o2 = {("<Demo_1>")};
	Tuple t2 = new Tuple(o2, VariableValue.create("REST"));
	System.out.println(t2);

	Object[] o3 = {(t1), (t2)};
	Tuple t3 = new Tuple(o3, VariableValue.create("REST3"));
	System.out.println(t3);

	Object[] o5 = {t1, t2, t3};
	Tuple t5 = new Tuple(o5, VariableValue.create("REST4"));
	System.out.println(t5);

    }
	

}
