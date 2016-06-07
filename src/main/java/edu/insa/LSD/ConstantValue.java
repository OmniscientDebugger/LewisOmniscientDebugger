/*                        ConstantValue.java

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

public class ConstantValue extends Value {

    // These strings must be identical to the same strings in D.java
    public static final ConstantValue		CATCH = new ConstantValue("CATCH");
    public static final ConstantValue		RETURN = new ConstantValue("RETURN");
    public static final ConstantValue		ENTER = new ConstantValue("ENTER");
    public static final ConstantValue		CALL = new ConstantValue("CALL");
    public static final ConstantValue		EXIT = new ConstantValue("EXIT");
    public static final ConstantValue		CHGLOCALVAR = new ConstantValue("CHGLOCALVAR");
    public static final ConstantValue		CHGINSTANCEVAR  = new ConstantValue("CHGINSTANCEVAR");
    public static final ConstantValue		CHGARRAY = new ConstantValue("CHGARRAY");
    public static final ConstantValue		CHGTHREADSTATE = new ConstantValue("CHGTHREADSTATE");
    public static final ConstantValue		LOCK = new ConstantValue("LOCK");
    public static final ConstantValue		NEW = new ConstantValue("NEW");
    public static final ConstantValue		RETURN_NEW = new ConstantValue("RETURN_NEW");
    public static final ConstantValue		NOTDEFINED = new ConstantValue("NOTDEFINED");

    public static final ConstantValue		LOCKING = new ConstantValue("LOCKING");
    public static final ConstantValue		UNLOCKING = new ConstantValue("UNLOCKING");
    public static final ConstantValue		WAITING = new ConstantValue("WAITING");
    public static final ConstantValue		WAITED = new ConstantValue("WAITED");


    public static final ConstantValue		GETTING_LOCK = new ConstantValue("GETTING_LOCK");
    public static final ConstantValue		GOT_LOCK = new ConstantValue("GOT_LOCK");
    public static final ConstantValue		RELEASING_LOCK = new ConstantValue("RELEASING_LOCK");
    public static final ConstantValue		STARTING_WAIT = new ConstantValue("STARTING_WAIT");
    public static final ConstantValue		ENDING_WAIT = new ConstantValue("ENDING_WAIT");
    public static final ConstantValue		STARTING_JOIN = new ConstantValue("STARTING_JOIN");
    public static final ConstantValue		ENDING_JOIN = new ConstantValue("ENDING_JOIN");

    public static final ConstantValue		NULL = new ConstantValue(null);
    public static final ConstantValue		FALSE = new ConstantValue(ShadowBoolean.FALSE);
    public static final ConstantValue		TRUE = new ConstantValue(ShadowBoolean.TRUE);

    public static final ConstantValue		BOOLEAN = new ConstantValue(ShadowBoolean.class);
    public static final ConstantValue		BYTE = new ConstantValue(ShadowByte.class);
    public static final ConstantValue		CHAR = new ConstantValue(ShadowChar.class);
    public static final ConstantValue		SHORT = new ConstantValue(ShadowShort.class);
    public static final ConstantValue		INT = new ConstantValue(ShadowInt.class);
    public static final ConstantValue		LONG = new ConstantValue(ShadowLong.class);
    public static final ConstantValue		FLOAT = new ConstantValue(ShadowFloat.class);
    public static final ConstantValue		DOUBLE = new ConstantValue(ShadowDouble.class);
    public static final ConstantValue		STRING = new ConstantValue(String.class);

    public static final ConstantValue[]		portValues = {CHGLOCALVAR, NOTDEFINED, CHGINSTANCEVAR, CATCH,
							      CHGARRAY, RETURN, NOTDEFINED, CALL,
							      CALL, LOCKING, NOTDEFINED, UNLOCKING,
							      ENTER, WAITING, EXIT, WAITED};
    
    /*					These are the internal Debugger constants that coorespond to LSD's "port"
    public static int 			LOCAL = 0x00000000, THROW = 0x10000000, OBJECT_IV = 0x20000000, CATCH = 0x30000000;
    public static int 			ONE_D_ARRAY = 0x40000000, RETURN = 0x50000000, OTHER = 0x60000000, ABSENT = 0x70000000;
    public static int 			CALL = 0x80000000, LOCKING = 0x90000000, MULTI_D_ARRAY = 0xA0000000, UNLOCKING = 0xB0000000;
    public static int 			FIRST = 0xC0000000, WAITING = 0xD0000000, LAST = 0xE0000000, WAITED = 0xF0000000;
    */



    // Constructors

    ConstantValue(Object s) {super(s);}
    ConstantValue(int i) {super(i);}


    public static ConstantValue getPort(int type) {
	return portValues[type];
    }

    public static ConstantValue getBoolean(boolean z) {
	return(z ? TRUE : FALSE);
    }


    public static ConstantValue find(String s) {			// Used only by parser
	if (s.equals("catch")) return CATCH;
	if (s.equals("return") || s.equals("r")) return RETURN;
	if (s.equals("enter") || s.equals("e")) return ENTER;
	if (s.equals("call") || s.equals("c")) return CALL;
	if (s.equals("exit") || s.equals("x")) return EXIT;
	if (s.equals("lock")) return LOCK;
	if (s.equals("chgLocalVar") || s.equals("clv")) return CHGLOCALVAR;
	if (s.equals("chgInstanceVar") || s.equals("civ")) return CHGINSTANCEVAR;
	if (s.equals("chgArray")) return CHGARRAY;
	if (s.equals("chgThreadState") || s.equals("cts")) return CHGTHREADSTATE;
	if (s.equals("notdefined")) return NOTDEFINED;
	if (s.equals("null")) return NULL;
	if (s.equals("false")) return FALSE;
	if (s.equals("true")) return TRUE;
	if (s.equals("boolean")) return BOOLEAN;
	if (s.equals("byte")) return BYTE;
	if (s.equals("char")) return CHAR;
	if (s.equals("short")) return SHORT;
	if (s.equals("int")) return INT;
	if (s.equals("long")) return LONG;
	if (s.equals("float")) return FLOAT;
	if (s.equals("double")) return DOUBLE;
	if (s.equals("String")) return STRING;
	if (s.equals("gettingLock")) return GETTING_LOCK;
	if (s.equals("gotLock")) return GOT_LOCK;
	if (s.equals("releasingLock")) return RELEASING_LOCK;
	if (s.equals("startingWait")) return STARTING_WAIT;
	if (s.equals("endingWait")) return ENDING_WAIT;
	if (s.equals("startingJoin")) return STARTING_JOIN;
	if (s.equals("endingJoin")) return ENDING_JOIN;


	throw new LSDException("Not a legal constant: "+s);
    }



}
