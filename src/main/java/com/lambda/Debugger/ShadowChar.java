/*                        ShadowChar.java

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

public class ShadowChar implements ShadowPrimitive {
    private static int			MAX = 65536;
    private static ShadowChar[] 		canonical = new ShadowChar[MAX];
    //    static {for (int i=0; i < MAX; i++) canonical[i] = new ShadowChar((char)i);}

    private char value;
    private String printString;

  ShadowChar(char i) {
    value = i;
  }

  public static ShadowChar createShadowChar(char b) {
      ShadowChar sc = canonical[b];
      if (sc == null) {
	  sc = new ShadowChar(b);
	  canonical[b]=sc;
      }
      return sc;
  }


  public String toString() {
    //	return "<Sc "+ value +">";
    if (printString == null) printString = "'" + value +"' (" + (int)value+")";	// + Integer.toHexString(0x0000FFFF & value).toUpperCase();
    return printString;
  }

  public char charValue() {
    return value;
  }

    public Class getType() {return char.class;}


}
