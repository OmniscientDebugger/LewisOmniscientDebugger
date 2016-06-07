/*                        ShadowInt.java

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

package com.lambda.Debugger;

public final class ShadowInt implements ShadowPrimitive {
    private final static int		MAX = 10001;
    private final static ShadowInt[] 	canonical = new ShadowInt[MAX];
    private final static ShadowInt[] 	canonicalNeg = new ShadowInt[100];

    private   int value;
    private   String printString;


    ShadowInt(int i) {
	value = i;
    }

    public int intValue() {
	return value;
    }

    public static ShadowInt createShadowInt(int i) {
	if ((i < MAX) && (i >= 0)) {
	    ShadowInt si = canonical[i];
	    if (si != null) return si;
	    si = new ShadowInt(i);
	    canonical[i]=si;
	    return si;
	}
	if ((i < 0) && (i > -100)){
	    ShadowInt si = canonicalNeg[-i];
	    if (si != null) return si;
	    si = new ShadowInt(i);
	    canonicalNeg[-i]=si;
	    return si;
	}
	//D.println("SI "+i); throw new NullPointerException();
	return new ShadowInt(i);
    }


    public String toString() {
	//return "<Si "+ value +">";
	//	return ""+value;
	if (printString == null) printString = Integer.toString(value); // + "      " + " == 0x" + Integer.toHexString(value).toUpperCase();
	return printString;
    }

    public Class getType() {return int.class;}

}
