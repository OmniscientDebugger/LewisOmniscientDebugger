/*                        Condition.java

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

public class Condition {

    public final Attribute	attribute;
    public final Operator	operator;
    public final Value		value;


    // Constructors

    Condition(Attribute a, Operator o, Value v) {
	attribute = a;
	operator = o;
	value = v;
    }


    public static Condition createCounter() {
	return new Condition(Attribute.OBJECT, Operator.UNIFY, CounterValue.create("COUNT"));
    }

    public boolean match(Event e) {
	Object attrValue = e.getAttrValue(attribute);
	return(operator.match(attrValue, value));
    }


    public boolean match(Object o, State state) {
	Object attrValue = state.getAttrValue(o, attribute);
	return(operator.match(attrValue, value));
    }

    public void resetVariable() {
	if (value instanceof VariableValue) {
	    VariableValue vv = (VariableValue) value;
	    vv.reset();
	}
	if (value instanceof Tuple) {
	    ((Tuple)value).resetVariables();
	}
    }



    public String toString() {
	return "<Condition "+attribute+" "+operator+" "+value+">";
    }
}
