/*                        MiniBuffer.java

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


public class Misc {


    // How can there be no string replace???
    public static String replace(String source, String pattern, String replacement) {
	int index = source.indexOf(pattern);
	if (index > -1) {
	    String sub = source.substring(0, index);
	    int end = index+pattern.length();
	    String rest = source.substring(end, source.length());
	    return sub + replacement + replace(rest, pattern, replacement);
	}
	return source;
    }

}
