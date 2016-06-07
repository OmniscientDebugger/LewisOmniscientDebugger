/*                        ExitEvent.java

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

public class ExitEvent extends EventImpl {


    // This is the SLOW interface. (one inherited method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.RETURN_TYPE) return returnType();
	if (a == Attribute.RETURN_VALUE) return returnValue();
	return super.getAttrValue(a);
    }

    // These are the FAST public methods this class provides. (All others return INVALID)
    public Value getPort() {return ConstantValue.EXIT;}
    public Object getReturnValue() {return returnValue;}
    public Object getReturnType() {return tl.getReturnType();}




    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    private static ExitEvent	SINGLETON = new ExitEvent();


    private ExitEvent() {}		// Only one instance!

    // These are the SLOW methods the parent class provides for internal use only. (All others return INVALID)
    public Object returnType() {return getReturnType();}
    public Object returnValue() {return getReturnValue();}

    // **************** These are all specific for ExitEvent ****************

    private int			returnValueType;		// enumeration: INT, BYTE...  NB: varType
    private boolean		returnValueZ;
    private byte		returnValueB;
    private char		returnValueC;
    private short		returnValueS;
    private int 		returnValueI;
    private long		returnValueL;
    private float		returnValueF;
    private double		returnValueD;
    private Object 		returnValueA;
    private Object 		returnValue;		// Used when primitives aren't available
    private Object		returnType;


    public static ExitEvent set(int slIndex, Object returnValue, TraceLine tl) {
	SINGLETON.set_(slIndex, tl);
	SINGLETON.returnValue = returnValue;
	return SINGLETON;
    }
    
    public static ExitEvent set(int time) {
	SINGLETON.set_(time);
	return SINGLETON;
    }
    
    protected void set_(int slIndex, TraceLine tl) {
	super.set_(slIndex, tl);
    }

    protected void set_(int time) {
	super.set_(time);
	MethodLine ml = tl.returnLine;
	if (ml == null) {returnValue = Value.INVALID; return;}
	ReturnLine rl = (ReturnLine) ml;
	returnValue = rl.returnValue;
    }





    public String toString() {
	return "<Exit "+ printString() + " -> "+ printString(returnValue) + ">";
    }

}
