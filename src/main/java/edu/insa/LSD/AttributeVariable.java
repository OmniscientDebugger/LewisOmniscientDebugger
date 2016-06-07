/*                        AttributeVariable.java

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

public class AttributeVariable extends Attribute {
    String varName;
    Attribute attr;

    public static AttributeVariable create(String s, Attribute a) {
	return new AttributeVariable(s, a);
    }
    private AttributeVariable(String s, Attribute a) {
	super("AttributeVariable");
	varName = s;
	attr = a;
    }
    public static AttributeVariable create(String s) {
	return new AttributeVariable(s);
    }
    private AttributeVariable(String s) {
	super("AttributeVariable");
	varName = s;
    }

}
    
