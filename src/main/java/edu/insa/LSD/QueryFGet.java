/*                        QueryFGet.java

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

public class QueryFGet {
    private static String		previousString="";
    private static int			inputIndex, len;


    public static void fget(String s, boolean forward, boolean countp) {
	int now = TimeStamp.currentTime().getTime();
	if (forward)
	    Event.setIndex(now+1);
	else
	    Event.setIndex(now-1);
	int cmt = s.indexOf('%');
	if (cmt > 0) s = s.substring(0, cmt-10);		// REQUIRED 10 spaces before %
	if (countp || !s.equals(previousString)) {
	    VariableValue.clear();
	    previousString=s;
	}
	//if (countp) index = 0;
	EventPattern ep = FGetParser.parse(s, countp);
	String vars;
	if (forward) {
	    while (Event.moreEvents()) {
		Event e = nextMatch(ep);
		if (e == null) break;			// Never happen?
		vars = VariableValue.printString();
		//System.out.println("matched: "+e);
		//VariableValue.printVars();
		Debugger.revert(e.time());
		Debugger.message("fget: "+s+vars, false);
		Debugger.println("fget: "+s+vars);
		return;
	    }
	}
	else {
	    while (Event.previousEvents()) {
		Event e = previousMatch(ep);
		if (e == null) break;			// Never happen?
		//System.out.println("matched: "+e);
		//VariableValue.printVars();
		vars = VariableValue.printString();
		Debugger.revert(e.time());
		Debugger.message("fget: "+s+vars, false);
		Debugger.println("fget: "+s+vars);
		return;
	    }
	}
	vars = VariableValue.printString();
	if (countp) {
	    Debugger.message("fget: "+s+vars, false);
		Debugger.println("fget: "+s+vars);
	}
	else
	    Debugger.message("No more matches.", true);
    }
	

    public static String previousPattern() {return previousString;}
    public static void setPattern(String s) {previousString = s;}



    
    public static Event nextMatch(EventPattern ep) {
	while (Event.moreEvents()) {
	    Event e = Event.nextEvent();
	    if (e == null) return null;
	    ep.resetVariables();
	    if (ep.match(e)) return e;
	}
	return null;
    }

    public static Event previousMatch(EventPattern ep) {
	while (Event.previousEvents()) {
	    Event e = Event.previousEvent();
	    if (e == null) return null;
	    ep.resetVariables();
	    if (ep.match(e)) return e;
	}
	return null;
    }

    public static void dump() {
	Event.resetIndex();
	while (Event.moreEvents()) {
	    Event e = Event.nextEvent();
	    System.out.println(e);
	}
    }



    
    public static void main(String[] args) {
	fget("port = enter", true, false);
    }
}
