/*                        CatchEvent.java

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

public class CatchEvent extends EventImpl {

    // This is the SLOW interface. (one inherited method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.EXCEPTION) return exception();
	if (a == Attribute.EXCEPTION_CLASS) return exceptionClass();
	if (a == Attribute.THROWING_METHOD_NAME) return Value.INVALID;
	return super.getAttrValue(a);
    }


    // These are the FAST public methods this class provides. (All others return INVALID)
    public Value getPort() {return ConstantValue.CATCH;}
    public Object getException() {return exception;}
    public Class getExceptionClass() {return exception.getClass();}






    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    private static CatchEvent		SINGLETON = new CatchEvent();	


    private CatchEvent() {}

    // These are the SLOW methods the parent class provides for internal use only. (All others return INVALID)
    public Object exception() {return getException();}
    public Object exceptionClass() {return getExceptionClass();}

    // **************** These are all specific for CatchEvent ****************


    private Throwable			exception;		



    public static CatchEvent set(int slIndex, Throwable exception, TraceLine tl) {
	SINGLETON.set_(slIndex, tl);
	SINGLETON.exception = exception;
	return SINGLETON;
    }
    
    public static CatchEvent set(int time) {
	SINGLETON.set_(time);
	return SINGLETON;
    }
    
    protected void set_(int slIndex, TraceLine tl) {
	super.set_(slIndex, tl);

    }

    protected void set_(int time) {
	super.set_(time);
	CatchLine cl = (CatchLine) TraceLine.getMethodLine(time);
	exception = (cl.exception.value);			// ShadowException.exception
	//tl = cl.traceLine;
    }







    public String toString() {
	return "<Catch "+ printString() + " **** " + exception +">";
    }


}
