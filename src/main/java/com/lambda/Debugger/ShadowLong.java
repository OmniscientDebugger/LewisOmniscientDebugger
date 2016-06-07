/*                        ShadowLong.java

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

public class ShadowLong implements ShadowPrimitive  {
    private long value;
    private String printString;

    ShadowLong(long i) {
	value = i;
    }

  public static ShadowLong createShadowLong(long l) {
      return new ShadowLong(l);
  }

    public String toString() {
	//return "<Sl "+ value +">";
	if (printString == null) printString = Long.toString(value);
	return printString;
    }

  public long longValue() {
    return value;
  }

    public Class getType() {return long.class;}


}
