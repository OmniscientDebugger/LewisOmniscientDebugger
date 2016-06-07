/*                        FileLine.java

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

import java.awt.FontMetrics;

public class FileLine {

    public SourceLine source;
    public String lineText;
    private static String[] 		spaces = {"", " ", "  ", "   ", "    ", "     ", "      ",
					  "       ", "        "};

    public FileLine(String fileName, int line, String inputLine) {
	source = SourceLine.getSourceLineFileName(fileName+":"+ line);
	lineText = replaceTabs(inputLine);
    }


    public String replaceTabs(String s) {
      if (s.length() == 0) return " ";
	if (s.indexOf('\t') == -1) return s;
	    
	StringBuffer b = new StringBuffer();
	int len = s.length();
	for (int i = 0; i < len; i++) {
	    if (s.charAt(i) == '\t') {
		int tabSpaces = 8 - (i % 8);
		b.append(spaces[tabSpaces]);
	    }
	    else
		b.append(s.charAt(i));
	}
	return b.toString();
    }

    public Object getSelectedObject(int x, FontMetrics fm) {
	return(source.getFileLine());
    }

    public String toString() {
	return lineText;
    }

}
