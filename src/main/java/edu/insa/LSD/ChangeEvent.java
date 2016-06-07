/*                        ChangeEvent.java

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

public abstract class ChangeEvent extends EventImpl {

    // These are the FAST public methods this class provides. (All others return INVALID)
    public int getNewValueType() {return newValueType;}		// enumeration: INT, BYTE...
    public boolean getNewValueZ() {return newValueZ;}
    public byte getNewValueB() {return newValueB;}
    public char getNewValueC() {return newValueC;}
    public short getNewValueS() {return newValueS;}
    public int getNewValueI() {return newValueI;}
    public long getNewValueL() {return newValueL;}
    public float getNewValueF() {return newValueF;}
    public double getNewValueD() {return newValueD;}
    public Object getNewValueA() {return newValueA;}
    public Object getOldValue() {return Value.INVALID;}
    public Object getNewValue() {		// SLOW interface. Avoid using
	switch(newValueType) {
	case BOOLEAN:		return ShadowBoolean.createShadowBoolean(newValueZ);
	case BYTE:		return ShadowByte.createShadowByte(newValueB);
	case CHAR:		return ShadowChar.createShadowChar(newValueC);
	case SHORT:		return ShadowShort.createShadowShort(newValueS);
	case INT:		return ShadowInt.createShadowInt(newValueI);
	case LONG:		return ShadowLong.createShadowLong(newValueL);
	case FLOAT:		return ShadowFloat.createShadowFloat(newValueF);
	case DOUBLE:		return ShadowDouble.createShadowDouble(newValueD);
	case REFERENCE:		return newValueA;
	default:		throw new LSDException("Impossible type "+newValueType);
	}
    }



    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************



    // **************** These are all specific for ChangeLocalVariableEvent ****************

    int			newValueType;		// enumeration: INT, BYTE...  NB: varType
    boolean		newValueZ;
    byte		newValueB;
    char		newValueC;
    short		newValueS;
    int 		newValueI;
    long		newValueL;
    float		newValueF;
    double		newValueD;
    Object 		newValueA;
    int			varIndex;




    // These are the SLOW public methods the parent class provides. (All others return INVALID)
    public Object varName() {return getVarName();}
    public Object varType() {return getVarType();}
    public Object newValue() {return getNewValue();}
    public Object oldValue() {return getOldValue();}

}
