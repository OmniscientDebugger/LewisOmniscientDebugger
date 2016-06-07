/*                        ChangeInstanceVariableEvent.java

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

public class ChangeInstanceVariableEvent extends ChangeEvent {

    // This is the SLOW interface. (one method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.NAME) return varName();
	if (a == Attribute.TYPE) return varType();
	if (a == Attribute.OLD_VALUE) return oldValue();
	if (a == Attribute.NEW_VALUE) return newValue();
	if (a == Attribute.OBJECT) return object();
	if (a == Attribute.OBJECT_CLASS) return objectClass();
	if (a == Attribute.IS_IVAR_STATIC) return isIVarStatic();

	return(super.getAttrValue(a));
    }



    // These are the FAST public methods this class provides.

    public Value getPort() {return ConstantValue.CHGINSTANCEVAR;}
    // These are the FAST public methods this class provides. (All others return INVALID)
    public String getVarName() {return varName;}
    public Object getVarType() {
	ClassInformation ci = ClassInformation.get(object);
	int varIndex = ci.getVarIndex(varName);
	if (varIndex == -1) return Value.INVALID;
	Class type = ci.getVarClass(varIndex);
	return type;
    }
    public Object getObject() {return ((object instanceof Class) ? Value.INVALID : object);}
    public Class getObjectClass() {return((object instanceof Class) ? (Class)object : object.getClass());}
    public boolean getIsIVarStatic() {return(object instanceof Class);}



    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************



    private static ChangeInstanceVariableEvent	SINGLETON = new ChangeInstanceVariableEvent();


    private ChangeInstanceVariableEvent() {}		// Only one instance!



    // **************** These are all from EnterEvent ****************

    String				varName;
    Object				object;


    // **************** These are all specific for ChangeInstanceVariableEvent ****************



    // These are the SLOW public methods the parent class provides. (All others return INVALID)
    public Object varName() {return getVarName();}
    public Object varType() {return getVarType();}
    public Object newValue() {return getNewValue();}
    public Object isIVarStatic() {return ShadowBoolean.createShadowBoolean(getIsIVarStatic());}
    public Object object() {return getObject();}
    public Object objectClass() {return getObjectClass();}


    public static ChangeInstanceVariableEvent setA(int slIndex, Object object, String varName, Object newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = REFERENCE;	
	SINGLETON.newValueA = newValue;
	return SINGLETON;
    }
    public static ChangeInstanceVariableEvent setZ(int slIndex, Object object, String varName, boolean newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = BOOLEAN;	
	SINGLETON.newValueZ = newValue;
	return SINGLETON;
    }
    public static ChangeInstanceVariableEvent setB(int slIndex, Object object, String varName, byte newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = BYTE;	
	SINGLETON.newValueB = newValue;
	return SINGLETON;
    }
    public static ChangeInstanceVariableEvent setS(int slIndex, Object object, String varName, short newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = SHORT;	
	SINGLETON.newValueS = newValue;
	return SINGLETON;
    }
    public static ChangeInstanceVariableEvent setC(int slIndex, Object object, String varName, char newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = CHAR;	
	SINGLETON.newValueC = newValue;
	return SINGLETON;
    }
    public static ChangeInstanceVariableEvent setI(int slIndex, Object object, String varName, int newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = INT;	
	SINGLETON.newValueI = newValue;
	return SINGLETON;
    }
    public static ChangeInstanceVariableEvent setL(int slIndex, Object object, String varName, long newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = LONG;	
	SINGLETON.newValueL = newValue;
	return SINGLETON;
    }
    public static ChangeInstanceVariableEvent setF(int slIndex, Object object, String varName, float newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = FLOAT;	
	SINGLETON.newValueF = newValue;
	return SINGLETON;
    }
    public static ChangeInstanceVariableEvent setD(int slIndex, Object object, String varName, double newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, object, varName, tl);
	SINGLETON.newValueType = DOUBLE;	
	SINGLETON.newValueD = newValue;
	return SINGLETON;
    }




    public void set_(int slIndex, Object object, String varName, TraceLine tl) {
	super.set_(slIndex, tl);
	this.object = object;
	this.varName = varName;
    }

    public static ChangeInstanceVariableEvent set(int time) {
	SINGLETON.set_(time);
	return SINGLETON;
    }
    protected void set_(int time) {
	super.set_(time);
	varName = EventInterface.getVarName(time);
	object = EventInterface.getObject(time);
	newValueA = EventInterface.getValue(time);
	newValueType = REFERENCE;
    }




    public String newValueString() {
	return printString(newValue());
    }

    public String toString() {
	return "<CIV "+  printString() + " "+printString(object)+"."+varName()+"="+newValueString()+">";	
    }
}


