/*                        LockEvent.java

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

public class LockEvent extends EventImpl {

    // This is the SLOW interface. (one inherited method!)
    public Object getAttrValue(Attribute a) {
	if (a == Attribute.LOCK_TYPE) return getLockType();
	if (a == Attribute.BLOCKED_ON_OBJECT) return getObject();
	if (a == Attribute.BLOCKED_ON_OBJECT_CLASS) return getObjectClass();
	if (a == Attribute.OBJECT) return getObject();
	if (a == Attribute.OBJECT_CLASS) return getObjectClass();
	return super.getAttrValue(a);
    }


    // These are the FAST public methods this class provides. (All others return INVALID)
    public Value getPort() {return ConstantValue.LOCK;}
    public Object getLockType() {return lockType;}
    public Object getObject() {return ((object instanceof Class) ? Value.INVALID : object);}
    public Class getObjectClass() {return((object instanceof Class) ? (Class)object : object.getClass());}







    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    private static LockEvent		SINGLETON = new LockEvent();	


    private LockEvent() {}


    // **************** These are all specific for LockEvent ****************


    private Object			object;
    private Value			lockType;



    public static LockEvent set(int slIndex, Object object, Value lockType, TraceLine tl) {
	SINGLETON.set_(slIndex, tl);
	SINGLETON.lockType = lockType;
	SINGLETON.object = object;
	return SINGLETON;
    }
    
    public static LockEvent set(int time, ConstantValue lockType) {
	SINGLETON.set_(time);
	SINGLETON.lockType = lockType;
	SINGLETON.object = EventInterface.getValue(time);
	if (SINGLETON.object == null) SINGLETON.object = EventInterface.getPreviousValue(time, TimeStamp.getThread(time));
	return SINGLETON;
    }
    
    protected void set_(int slIndex, TraceLine tl) {
	super.set_(slIndex, tl);

    }

    protected void set_(int time) {
	super.set_(time);
    }







    public String toString() {
	return "<Lock "+ printString(lockType) + " **** " + printString(object) +">";
    }


}
