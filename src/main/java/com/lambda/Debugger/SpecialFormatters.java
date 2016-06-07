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
import java.util.*;


/**
  Special formatters are loaded from data in the .debuggerDefaults file.
**/

public class SpecialFormatters {

	private static List formatters = new ArrayList();

	public static String format(Object obj) {
		int len = formatters.size();
		for (int i = 0; i < len; i++) {
			SpecialFormatter formatter = (SpecialFormatter) formatters.get(i);
			String s = formatter.format(obj);
			if (s != null) return s;
		}
		return null;
	}

	public static void add(SpecialFormatter f) {formatters.add(f);}

	public static void add(String formatterName) {
		Debugger.println("Loading special formatter: " + formatterName);
		ClassLoader cl =  SpecialFormatters.class.getClassLoader();
		try {
			Class clazz=cl.loadClass(formatterName);
			SpecialFormatter sf = (SpecialFormatter) clazz.newInstance();
			add(sf);
		}
	    catch (ClassNotFoundException ex) {
			System.err.println("Class not found: " + formatterName);
			return;
	    }
	    catch (InstantiationException ex) {
			System.err.println("Could not instanciate: " + formatterName);
			return;
	    }
	    catch (IllegalAccessException ex) {
			System.err.println("Could not instanciate: " + formatterName);
			return;
	    }



	}
}

