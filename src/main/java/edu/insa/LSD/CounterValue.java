/*                        CounterValue.java

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

public class CounterValue extends VariableValue {
    private int			count = 0;

    // Constructors

    CounterValue(String s) {super(s);}


    public static VariableValue create(String s) {
	s = s.intern();
	VariableValue vv = (VariableValue)table.get(s);
	if (vv != null) return vv;
	vv = new CounterValue(s);
	table.put(s, vv);
	return vv;
    }


    public void setValue(Object v) {throw new LSDException("Cannot setValue() on "+this);}
    public void reset() {}
    public boolean notSet() {count++; return false;}
    public int getCount() {return count;}

    public String toString() {
	return name + ":" + count;
    }

}
