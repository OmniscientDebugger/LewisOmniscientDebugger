/*                        EnterEvent.java

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

public class EnterEvent extends EventImpl {


    // This is the SLOW interface. (one inherited method!)
    // These are the FAST public methods this class provides. (All others return INVALID)
    public Value getPort() {return ConstantValue.ENTER;}





    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************
    // **************** Everything below is for internal use only ****************


    private static EnterEvent	SINGLETON = new EnterEvent();


    private EnterEvent() {}		// Only one instance!



    public static EnterEvent set(int slIndex, TraceLine tl) {
	SINGLETON.set_(slIndex, tl);
	return SINGLETON;
    }
    
    public static EnterEvent set(int time) {
	SINGLETON.set_(time);
	return SINGLETON;
    }
    
    protected void set_(int slIndex, TraceLine tl) {
	super.set_(slIndex, tl);
    }

    protected void set_(int time) {
	super.set_(time);
    }





    public String toString() {
	return "<Enter "+ printString() + ">";
    }
    
}
