/*                        ChangeLocalVariableEvent.java

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

public class ChangeLocalVariableEvent extends ChangeEvent {


    // This is the SLOW interface. (one method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.NAME) return varName();
	if (a == Attribute.TYPE) return varType();
	if (a == Attribute.OLD_VALUE) return oldValue();
	if (a == Attribute.NEW_VALUE) return newValue();
	return(super.getAttrValue(a));
    }


    // These are the FAST public methods this class provides. (All others return INVALID)
    public Value getPort() {return ConstantValue.CHGLOCALVAR;}
    public String getVarName() {return tl.getVarName(varIndex);}
    public Object getVarType() {return tl.getVarType(varIndex);}
    public String getSourceFileLine() {return SourceLine.getSourceLine(slIndex).getFileLine();}		// FAST: "My.java:23"


    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    private static ChangeLocalVariableEvent	SINGLETON = new ChangeLocalVariableEvent();


    // **************** These are all specific for ChangeLocalVariableEvent ****************

    String				varName;
    int					varIndex;


    private ChangeLocalVariableEvent() {}		// Only one instance!



    // These are the SLOW public methods the parent class provides. (All others return INVALID)
    public Object varName() {return getVarName();}
    public Object varType() {return getVarType();}
    public Object newValue() {return getNewValue();}
    public Object oldValue() {return getOldValue();}



    public static ChangeLocalVariableEvent setA(int slIndex, int varIndex, Object newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = REFERENCE;	
	SINGLETON.newValueA = newValue;
	return SINGLETON;
    }
    public static ChangeLocalVariableEvent setZ(int slIndex, int varIndex, boolean newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = BOOLEAN;	
	SINGLETON.newValueZ = newValue;
	return SINGLETON;
    }
    public static ChangeLocalVariableEvent setB(int slIndex, int varIndex, byte newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = BYTE;	
	SINGLETON.newValueB = newValue;
	return SINGLETON;
    }
    public static ChangeLocalVariableEvent setS(int slIndex, int varIndex, short newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = SHORT;	
	SINGLETON.newValueS = newValue;
	return SINGLETON;
    }
    public static ChangeLocalVariableEvent setC(int slIndex, int varIndex, char newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = CHAR;	
	SINGLETON.newValueC = newValue;
	return SINGLETON;
    }
    public static ChangeLocalVariableEvent setI(int slIndex, int varIndex, int newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = INT;	
	SINGLETON.newValueI = newValue;
	return SINGLETON;
    }
    public static ChangeLocalVariableEvent setL(int slIndex, int varIndex, long newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = LONG;	
	SINGLETON.newValueL = newValue;
	return SINGLETON;
    }
    public static ChangeLocalVariableEvent setF(int slIndex, int varIndex, float newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = FLOAT;	
	SINGLETON.newValueF = newValue;
	return SINGLETON;
    }
    public static ChangeLocalVariableEvent setD(int slIndex, int varIndex, double newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, varIndex, tl);
	SINGLETON.newValueType = DOUBLE;	
	SINGLETON.newValueD = newValue;
	return SINGLETON;
    }



    private void set_(int slIndex, int varIndex, TraceLine tl) {
	super.set_(slIndex, tl);
	this.varIndex = varIndex;
    }

    public static ChangeLocalVariableEvent set(int time) {
	SINGLETON.set_(time);
	return SINGLETON;
    }
    protected void set_(int time) {
	super.set_(time);
	varName = EventInterface.getVarName(time);
	newValueA = EventInterface.getValue(time);
	newValueType = REFERENCE;
    }


    public String newValueString() {
	return printString(newValue());
    }

    public String toString() {
	return "<CLV "+ printString() + " " + varName()+"="+newValueString()+">";
    }
}
