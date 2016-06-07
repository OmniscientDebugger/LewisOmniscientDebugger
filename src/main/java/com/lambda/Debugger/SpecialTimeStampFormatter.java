/*                        ObjectsMenuActionListener.java

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

// This is installed at start-up by only when the DebuggerDebugger is in use.
// Note that primitives are all encapuslated as ShadowInt etc.

class SpecialTimeStampFormatter implements SpecialFormatter {

	public String format(Object obj) {
		if (obj instanceof ShadowInt) {
		int intValue = ((ShadowInt) obj).intValue();
		if (intValue == 0) return "0";
		return format(intValue, TimeStamp.TYPE_MASK)+"|" + format(intValue, TimeStamp.THREAD_MASK)
		    +"|" + format(intValue, TimeStamp.SOURCE_MASK);
	    }
		return null;
	}

    private static String format(int value, int mask) {
	if (TimeStamp.TYPE_MASK == mask) {
	    return(TimeStamp.getTypeStringFrom(value));
	}
	if (TimeStamp.THREAD_MASK == mask) {
	    value = value & mask;
	    value = value >> 20;
	    return "t@"+value;
	}
	if (TimeStamp.SOURCE_MASK == mask) {
	    value = value & mask;
	    return "SL:" + value;
	}
	return ""+value;
    }


}
