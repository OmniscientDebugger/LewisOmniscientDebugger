/*                        ShadowShort.java

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

public class ShadowShort implements ShadowPrimitive  {
    private static int			MAX = 65536;
    private static ShadowShort[] 		canonical = new ShadowShort[MAX];
    //    static {for (int i=-(MAX/2); i < MAX/2; i++) canonical[i+MAX/2] = new ShadowShort((short)i);}

    private short value;
    private String printString;

    ShadowShort(short i) {
	value = i;
    }


    public static ShadowShort createShadowShort(short b) {
	ShadowShort ss = canonical[b+MAX/2];
	if (ss == null) {
	    ss=new ShadowShort((short)(b));
	    canonical[b+MAX/2]=ss;
	}
	return ss;
    }

    public String toString() {
	//return "<Ss "+ value +">";
	if (printString == null) printString = Short.toString(value);
	return printString;
    }
  public short shortValue() {
    return value;
  }

    public Class getType() {return short.class;}


}
