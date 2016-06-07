/*                        ShadowFloat.java

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

public class ShadowFloat implements ShadowPrimitive  {
    private float value;
    private String printString;

    ShadowFloat(float i) {
	value = i;
    }

  public static ShadowFloat createShadowFloat(float i) {
      return new ShadowFloat(i);
  }

    public String toString() {
	//return "<Sf "+ value +">";
	if (printString == null) printString = Float.toString(value);
	return printString;
    }
  public float floatValue() {
    return value;
  }

    public Class getType() {return float.class;}


}
