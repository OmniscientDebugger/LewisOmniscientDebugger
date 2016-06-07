/*                        ShadowBoolean.java

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

public class ShadowBoolean implements ShadowPrimitive {
    public static final ShadowBoolean TRUE = new ShadowBoolean(true, 0);
    public static final ShadowBoolean FALSE = new ShadowBoolean(false, 0);
    private boolean value;
    private String printString;

  ShadowBoolean(boolean i, int ii) {
    value = i;
    printString = ""+value;
  }

  ShadowBoolean(boolean i) {
    value = i;
    printString = ""+value;
  }

  public boolean booleanValue() {
    return value;
  }

  public static ShadowBoolean createShadowBoolean(boolean i) {
    if (i)
      return TRUE;
    else
      return FALSE;
  }

  static ShadowBoolean findShadowBoolean(boolean i) {
    if (i)
      return TRUE;
    else
      return FALSE;
  }

  public String toString() {
    //return "<St "+ value +">";
    return printString;
  }
    public Class getType() {return boolean.class;}

}
