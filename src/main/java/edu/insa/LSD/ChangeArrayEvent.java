/*                        ChangeArrayEvent.java

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

public class ChangeArrayEvent extends ChangeEvent {

    // This is the SLOW interface. (one inherited method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.ARRAY) return array();
	if (a == Attribute.ARRAY_CLASS) return arrayClass();
	if (a == Attribute.NEW_VALUE) return newValue();
	if (a == Attribute.INDEX) return index();
	return super.getAttrValue(a);
    }


    // These are the FAST public methods this class provides. (All others return INVALID)
    public Value getPort() {return ConstantValue.CHGARRAY;}
    public Object getArrayClass() {return(array.getClass());}
    public Object getArray() {return array;}
    public int getIndex() {return index;}




    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    private static ChangeArrayEvent		SINGLETON = new ChangeArrayEvent();	


    private ChangeArrayEvent() {}

    // These are the SLOW methods the parent class provides for internal use only. (All others return INVALID)
    public Object array() {return getArray();}
    public Object arrayClass() {return getArrayClass();}
    public Object newValue() {return getNewValue();}
    public Object index() {return ShadowInt.createShadowInt(getIndex());}


    // **************** These are all specific for ChangeArrayEvent ****************

    private Object		array;
    private int			index;


    public static ChangeArrayEvent setA(int slIndex, Object array, int index, Object newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueA = newValue;
	SINGLETON.newValueType = REFERENCE;
	return SINGLETON;
    }
    public static ChangeArrayEvent setZ(int slIndex, Object array, int index, boolean newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueZ = newValue;
	SINGLETON.newValueType = BOOLEAN;
	return SINGLETON;
    }
    public static ChangeArrayEvent setB(int slIndex, Object array, int index, byte newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueB = newValue;
	SINGLETON.newValueType = BYTE;
	return SINGLETON;
    }
    public static ChangeArrayEvent setS(int slIndex, Object array, int index, short newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueS = newValue;
	SINGLETON.newValueType = SHORT;
	return SINGLETON;
    }
    public static ChangeArrayEvent setC(int slIndex, Object array, int index, char newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueC = newValue;
	SINGLETON.newValueType = CHAR;
	return SINGLETON;
    }
    public static ChangeArrayEvent setI(int slIndex, Object array, int index, int newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueI = newValue;
	SINGLETON.newValueType = INT;
	return SINGLETON;
    }
    public static ChangeArrayEvent setL(int slIndex, Object array, int index, long newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueL = newValue;
	SINGLETON.newValueType = LONG;
	return SINGLETON;
    }
    public static ChangeArrayEvent setF(int slIndex, Object array, int index, float newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueF = newValue;
	SINGLETON.newValueType = FLOAT;
	return SINGLETON;
    }
    public static ChangeArrayEvent setD(int slIndex, Object array, int index, double newValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl, array, index);
	SINGLETON.newValueD = newValue;
	SINGLETON.newValueType = DOUBLE;
	return SINGLETON;
    }
    
    public static ChangeArrayEvent set(int time) {
	SINGLETON.set_(time);
	return SINGLETON;
    }
    
    protected void set_(int slIndex, TraceLine tl, Object array, int index) {
	super.set_(slIndex, tl);
	SINGLETON.array = array;
	SINGLETON.index = index;
    }

    protected void set_(int time) {
	super.set_(time);
	array = EventInterface.getObject(time);
	newValueA = EventInterface.getValue(time);
	index = -1;
	/*
	  ShadowInt indexInt = (ShadowInt) EventInterface.getVarName();
	if (indexInt == null)
	    index = -1;
	else
	    index = indexInt.getIntValue();
	*/
	SINGLETON.newValueType = REFERENCE;
	index = -1;
    }





    public String toString() {
	return "<ChngA "+printString()+" "+printString(array()) + "[" + index() + "]="+printString(newValue())+">";
    }



}
