/*                        FGetParser.java

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

public class FGetParser {
    private static String		inputString="";				// Set only in parse()
    private static int			len;					// Set only in parse()
    private static int			inputIndex;				// Changed everywhere
	

    public static EventPattern parse(String s, boolean countp) {
	inputString = s;
	inputIndex = 0;
	len = inputString.length();
	ArrayList al = new ArrayList();
	while (true) {
	    Condition c =  parseCondition();
	    al.add(c);
	    if (!moreConditions()) {
		if (countp) al.add(Condition.createCounter());
		return new EventPattern(al);
	    }
	    String remainingString = inputString.substring(inputIndex, len);
	    if (remainingString.startsWith("& "))
		inputIndex+=2;
	    else
		if (remainingString.startsWith("and "))
		    inputIndex+=4;
		else
		    throw new LSDException("Not a legal 'and': \""+remainingString+"\"");
	}
    }


    private static boolean moreConditions() {
	if (inputIndex >= len) return false;
	return true;
    }
				    
    private static Condition parseCondition() {
	try {
	    skipWhiteSpace();
	    Attribute attr = parseAttribute();
	    skipWhiteSpace();
	    Operator op = parseOperator();
	    skipWhiteSpace();
	    Object val = parseValue();
	    skipWhiteSpace();
	    if (!(val instanceof Value)) val = new Value(val);
	    Value v2 = (Value)val;

	    return new Condition(attr, op, v2);
	}
	catch (StringIndexOutOfBoundsException e) {throw new LSDException("Not a legal string: \""+inputString+"\"");}
    }

    private static Operator parseOperator() {			//pv:" = enter ..." -> Operator.UNIFY
	if (inputIndex == len) throw new LSDException("Not a legal operator: \""+inputString+"\"");
	char c = inputString.charAt(inputIndex);
	int spaceLoc = inputString.indexOf(' ', inputIndex);	// must be a following space
	if (spaceLoc == -1) throw new LSDException("Not a legal operator: \""+inputString+"\"");
	String s = inputString.substring(inputIndex, spaceLoc);
	inputIndex = spaceLoc+1;
	Operator op = Operator.find(s);
	if (op == null) throw new LSDException("Not a legal operator: \""+op+"\"");
	return op;
    }

    private static Object parseValue() {			//pv:"port = enter ..." -> Attribute.PORT
	try {return parseValue1();}
	catch (StringIndexOutOfBoundsException e) {throw new LSDException("Not a legal string: \""+inputString+"\"");}
    }

    private static Attribute parseAttribute() {			//pv:"port = enter ..." -> Attribute.PORT
	try {
	    Object o = parseValue1();
	    if (o instanceof Attribute) return((Attribute)o);
	    throw new LSDException("Not a legal attribute: \""+o+"\"");
	}
	catch (StringIndexOutOfBoundsException e) {throw new LSDException("Not a legal string: \""+inputString+"\"");}
    }
    
    private static Object parseValue1() {			//pv:"port = enter ..." -> Attribute.PORT
	if (inputIndex == len) return null;
	char c = inputString.charAt(inputIndex);
	int delimiterLoc = len;
	int spaceLoc = inputString.indexOf(' ', inputIndex);
	if (!(spaceLoc == -1) && (spaceLoc < delimiterLoc)) delimiterLoc = spaceLoc;
	int commaLoc = inputString.indexOf(',', inputIndex);
	if (!(commaLoc == -1) && (commaLoc < delimiterLoc)) delimiterLoc = commaLoc;
	int sCloseLoc = inputString.indexOf(']', inputIndex);
	if (!(sCloseLoc == -1) && (sCloseLoc < delimiterLoc)) delimiterLoc = sCloseLoc;

	if (c == '"') {					// everything between two '"'s is a string
	    int quoteLoc = inputString.indexOf('"', inputIndex+1);
	    if (quoteLoc == -1) throw new LSDException("Not a legal string: \""+inputString+"\"");
	    String s = inputString.substring(inputIndex+1, quoteLoc);
	    inputIndex = quoteLoc+1;
	    return s.intern();
	}

	if (c == '#') {					// parse class: "#Demo" -> class Demo
	    String cName = inputString.substring(inputIndex+1, delimiterLoc);
	    inputIndex = delimiterLoc;
	    //	    if (cName.equals("int")) return ShadowInt.class;
	    //	    if (cName.equals("boolean")) return ShadowBoolean.class;
	    if (cName.equals("int")) return int.class;
	    if (cName.equals("boolean")) return boolean.class;
	    if (cName.equals("String")) return String.class;
	    try {
		Class cl = Class.forName(cName, true, Debugger.classLoader);
		if (cl == null) throw new LSDException("Not a legal class name: \""+cName+"\"");
		return cl;
	    }
	    catch (Exception e) {throw new LSDException("Not a legal class name: \""+cName+"\"");}
	}

	if (c == '[') {					// parse tuple: [23, ["frob", #String], 99]
	    ArrayList al = new ArrayList();
	    inputIndex++;
	    c = inputString.charAt(inputIndex);
	    if (c == ']') {
		inputIndex++;
		return new Tuple(al);
	    }
	    while (true) {
		Object o = parseValue();
		al.add(o);
		c = inputString.charAt(inputIndex);
		if (c == ',') {
		    inputIndex+=2;     // skip space too
		    continue;
		}
		if (c == ']') {
		    inputIndex++;
		    return new Tuple(al);
		}
	    }
	}

	if (Character.isUpperCase(c)) {				// "VAL" -> <VariableValue VAL>
	    String name = inputString.substring(inputIndex, delimiterLoc);
	    inputIndex = delimiterLoc;
	    if (!allUppercase(name)) throw new LSDException("Not a legal variable name: \""+name+"\"");
	    VariableValue vv = VariableValue.create(name);
	    return vv;
	}

	if (c == '.') {					// parse class: ".count" -> <Attribute "count">
	    String cName = inputString.substring(inputIndex+1, delimiterLoc);
	    inputIndex = delimiterLoc;
	    return AttributeVariable.create(cName.intern());
	}

	if (Character.isLowerCase(c)) {				// "enter" -> ConstantValue.ENTER
	    String name = inputString.substring(inputIndex, delimiterLoc);
	    inputIndex = delimiterLoc;
	    Attribute a = Attribute.find(name);
	    if (a != null) return a;
	    ConstantValue cv = ConstantValue.find(name);
	    if (cv != null) return cv;
	    if (name.equals("null")) return null;
	    throw new LSDException("Not a legal attribute/value name: \""+name+"\"");
	}

	if (Character.isDigit(c)) {				// "234" -> <ShadowInt 234>
	    String name = inputString.substring(inputIndex, delimiterLoc);
	    inputIndex = delimiterLoc;
	    int i = Integer.parseInt(name);
	    return ShadowInt.createShadowInt(i);
	}

	if (c == '<') {						// "<Demo_22>" -> <Demo_22>
	    Completion.createCompletionTable();
	    String name = inputString.substring(inputIndex, delimiterLoc);
	    try {
		Object o = Completion.completedObject(name);
		inputIndex = delimiterLoc;
		return o;
	    }
	    catch(CompletionException e) {throw new LSDException("Not a legal object name: \""+name+"\"");}
	}

	if (c == '_') {						// "_" -> VariableValue.ANYVALUE
	    inputIndex = delimiterLoc;
	    return VariableValue.ANYVALUE;
	}

	throw new LSDException("Not a legal value: \""+inputString+"\"");
    }

    private static void skipWhiteSpace() {//skip spaces only
	while(true) {
	    if (len == inputIndex) return;
	    char c = inputString.charAt(inputIndex);
	    if (c != ' ') return;
	    inputIndex++;
	}
    }

    private static boolean allUppercase(String s) {return true;}// Assume it's true

    public static void main(String[] args) {
	String[] inputLines = {"port = enter & parameters = P",
			       "parameters = [[A, B, C],D]",
			       "thisObjectClass > #String",
			       "thisObjectClass <> #edu.insa.LSD.FGetParser",
			       "VAR < VAR",
			       "a0 < VAR",
			       ""};

	for (int i = 0; i < inputLines.length; i++) {
	    String in = inputLines[i];
	    try {
		EventPattern ep = parse(in, false);
		System.out.println("Input Line: \"" + in + "\" \t\t-> " + ep);
	    }
	    catch (LSDException e) {
		System.out.println("Input Line: \"" + in + "\" \t\t**** " + e);
	    }
	}
    }

}
