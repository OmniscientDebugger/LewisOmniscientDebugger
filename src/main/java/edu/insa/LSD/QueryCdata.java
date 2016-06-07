/*                        QueryCdata.java

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
import com.lambda.Debugger.*;
import java.util.*;

public class QueryCdata {
    private static String		previousString="";
    private static int			index;
    private static int			time=-1;
    private static Object[]		objects;
    private static State		state;


    public static void cdata(String s, State state, boolean forward, boolean countp) {
	if (time != TimeStamp.ct()) {
	    time =  TimeStamp.ct();
	    index = 0;
	    QueryCdata.state = state;
	    objects = state.getAllObjects();
	    previousString = null;
	}

	if (forward)
	    if (index < objects.length) index++;
	else
	    if (index > 0) index--;
	int cmt = s.indexOf('%');
	if (cmt > 0) s = s.substring(0, cmt-10);		// REQUIRED 10 spaces before %
	if (!s.equals(previousString)) {
	    index = 0;
	    VariableValue.clear();
	    previousString=s;
	}
	if (countp) index = 0;
	DataPattern dp = DataParser.parse(s, countp);

	String vars;
	if (forward) {
	    while (moreObjects()) {
		Object o = nextMatch(dp);
		if (o == null) break;			// Never happen?
		vars = VariableValue.printString();
		//System.out.println("matched: "+e);
		//VariableValue.printVars();
		Debugger.message("cdata: "+s+vars, false);
		return;
	    }
	    vars = VariableValue.printString();
	    if (countp)
		Debugger.message("cdata: "+s+vars, false);
	    else
		Debugger.message("No more matches.", true);
	    return;
	}
	if (!forward) {
	    while (lessObjects()) {
		Object o = previousMatch(dp);
		if (o == null) break;			// Never happen?
		//System.out.println("matched: "+e);
		//VariableValue.printVars();
		vars = VariableValue.printString();
		Debugger.message("cdata: "+s+vars, false);
		return;
	    }
	    Debugger.message("No more matches.", true);
	    return;
	}
    }
	

    public static String previousPattern() {return previousString;}

    public static boolean moreObjects() {return(index < objects.length);}
    public static boolean lessObjects() {return(index > -1);}
    public static Object nextObject() {
	if (index == -1) index = 0;
	index++;
	return(objects[index-1]);
    }
    public static Object previousObject() {
	if (index == objects.length) index--;
	index--;
	return(objects[index+1]);
    }

    
    public static Object nextMatch(DataPattern dp) {
	while (moreObjects()) {
	    Object o = nextObject();
	    if (o == null) return null;
	    dp.resetVariables();
	    if (dp.match(o, state)) return o;
	}
	return null;
    }

    public static Object previousMatch(DataPattern dp) {
	while (lessObjects()) {
	    Object o = previousObject();
	    if (o == null) return null;
	    dp.resetVariables();
	    if (dp.match(o, state)) return o;
	}
	return null;
    }

    public static void dump() {
	index = 0;
	while (moreObjects()) {
	    Object o = nextObject();
	    System.out.println(RecordedState.STATE.getPrintName(o));
	}
    }



    
    public static void main(String[] args) {
	//	cdata("port = enter", true, false);
    }
}
