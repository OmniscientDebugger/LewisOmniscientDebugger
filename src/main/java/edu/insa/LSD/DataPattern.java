/*                        DataPattern.java

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

import java.util.*;
import com.lambda.Debugger.*;

public class DataPattern {

    public final Condition[]	conditions;


    // Constructors

    DataPattern(ArrayList c) {
	conditions = new Condition[c.size()];
	c.toArray(conditions);
    }

    DataPattern(Condition[] c) {
	conditions = c;
    }



    // This is called by the debugger for interactive queries
    public boolean match(Object o, State state) {
	return matchConditions(conditions, o, state);
    }


    private boolean matchConditions(Condition[] c, Object o, State state) {
	int len = c.length;
	for (int i = 0; i < len; i++) {
	    if (!c[i].match(o, state)) return false;
	}
	return true;
    }



    public void resetVariables() {
	for (int i = 0; i < conditions.length; i++) conditions[i].resetVariable();
    }


    

    public void print() {
	Condition[] c = conditions;

	System.out.println("<DataPattern:");
	for (int i = 0; i < c.length; i++) System.out.println("\t"+c[i]);
	System.out.println(">");
    }

    public String toString() {
	String s = "<DataPattern:";
	for (int i = 0; i < conditions.length; i++) s+=" "+conditions[i];
	s+=">";
	return s;
    }
}
