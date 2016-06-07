/*                        Operator.java

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

public class Operator {

    static Operator		UNIFY = new Operator("UNIFY");
    static Operator		NEQ = new Operator("NEQ");
    static Operator		GT = new Operator("GT");
    static Operator		LT = new Operator("LT");
    static Operator		GTEQ = new Operator("GTEQ");
    static Operator		LTEQ = new Operator("LTEQ");
    static Operator		IN = new Operator("IN");
    static Operator		SUBSTRING = new Operator("SUBSTRING");


    public final String		name;

    // Constructors

    Operator(String s) {name=s;}


    public static Operator find(String s) {		// Used only by parser
	if (s.equals("=")) return UNIFY;
	if (s.equals("<>")) return NEQ;
	if (s.equals("!=")) return NEQ;
	if (s.equals(">")) return GT;
	if (s.equals("<")) return LT;
	if (s.equals(">=")) return GTEQ;
	if (s.equals("<=")) return LTEQ;
	if (s.equals("in")) return IN;
	if (s.equals("substring")) return SUBSTRING;
	if (s.equals("ss")) return SUBSTRING;
;
	throw new LSDException("Not a legal string: "+s);
    }


    // The matching methods

    public boolean match(Object attrValue, Value value) {
	Object av = attrValue;
	Object v = value.getValue();
	if ((value instanceof VariableValue) && ((VariableValue)value).notSet()) v = value;
	//	if (value instanceof ConstantValue) v = value;
	if (value instanceof Tuple) v = value;

	if (this == IN) {
	    if (!(value instanceof Tuple)) return false;
	    Tuple t = (Tuple) value;
	    if (matchListIn(av, t)) return true;
	    if (t.rest() != null) {
		Value vr = t.rest();
		if (vr instanceof VariableValue) {
		    VariableValue vv = (VariableValue) vr;
		    if (vv.notSet()) return false;			// IN does not UNIFY
		    Object rv = vv.getValue();
		    if (!(rv instanceof Tuple)) throw new LSDException("Not a tuple "+rv);
		    return matchListIn(av, (Tuple)rv);
		}
	    }
	    return false;
	}

	if (this == SUBSTRING) {
	    if (!(v instanceof String)) return false;
	    if (!(av instanceof String)) return false;
	    int i = ((String)av).indexOf((String)v);
	    if (i == -1) return false;
	    return true;
	}

	if (this == GT) {
	    if ((av instanceof ShadowInt) && (v instanceof ShadowInt)) {
		ShadowInt avi = (ShadowInt) av;
		ShadowInt vi = (ShadowInt) v;
		return(avi.intValue() > vi.intValue());
	    }
	    return false;
	}
	if (this == LT) {
	    if ((av instanceof ShadowInt) && (v instanceof ShadowInt)) {
		ShadowInt avi = (ShadowInt) av;
		ShadowInt vi = (ShadowInt) v;
		return(avi.intValue() < vi.intValue());
	    }
	    return false;
	}
	if (this == GTEQ) {
	    if ((av instanceof ShadowInt) && (v instanceof ShadowInt)) {
		ShadowInt avi = (ShadowInt) av;
		ShadowInt vi = (ShadowInt) v;
		return(avi.intValue() >= vi.intValue());
	    }
	    return false;
	}
	if (this == LTEQ) {
	    if ((av instanceof ShadowInt) && (v instanceof ShadowInt)) {
		ShadowInt avi = (ShadowInt) av;
		ShadowInt vi = (ShadowInt) v;
		return(avi.intValue() <= vi.intValue());
	    }
	    return false;
	}

	if (this == NEQ) return(attrValue != value.getValue());

	if (this == UNIFY) return(unify(attrValue, v));

	throw new LSDException("No such operator " + this);
    }


    private boolean unifyTuple(Tuple t0, Tuple t1) {
	int s0 = t0.size();
	int s1 = t1.size();
	Value rest = t1.rest();

	if (s0 < s1) return false;

	if ((s0 > s1) && (rest == null)) return false;

	for (int i = 0; i < t1.size(); i++) {
	    if (!unify(t0.get(i), t1.get(i))) return false;
	}

	if (rest == null) return true;

	if (rest instanceof VariableValue) {
	    VariableValue vv = (VariableValue) rest;
	    if (vv.notSet()) {
		vv.setValue(t0.tail(s0-s1));
		return true;
	    }
	    Object o = rest.getValue();
	    if (!(o instanceof Tuple)) return false;		// not impossible, just stupid
	    return unifyTuple(t0.tail(s0-s1), (Tuple)o);
	}
	else {
	    throw new LSDException("Tuple.rest must be a VV "+t0+" "+t1);
	}	    
    }

    private boolean unify(Object v0, Object v1) {
	if (v1 instanceof VariableValue) {
	    VariableValue vv = (VariableValue) v1;
	    if (vv.notSet() && v0 != Value.INVALID) {
		vv.setValue(v0);
		return true;
	    }
	    Object o1 = vv.getValue();
	    return(unify(v0, o1));
	}

	if (v1 instanceof Tuple) {
	    if (!(v0 instanceof Tuple)) return false;
	    return unifyTuple((Tuple)v0, (Tuple)v1);
	}

	return(v0 == v1);
    }

    private boolean matchList(Object o, Object v) {
	if (o == v) return true;
	return false;
    }

    private boolean matchListIn(Object o, Tuple t) {
	for (int i = 0; i < t.size(); i++) {
	    if (matchList(o, t.get(i))) return true;
	}
	return false;
    }




    public String toString() {
	return name;
    }


}
