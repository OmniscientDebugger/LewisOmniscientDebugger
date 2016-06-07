/*                        StackFrame.java

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

public interface StackFrame  {
    public String getMethodName();
    public Object getThisObject();
    public Object getThisObjectClass();
    public boolean getIsMethodStatic();
    //public int getDepth();

    public Tuple  getAllVariables();
    public int    getnVars();
    public int    getnArgs();
    public Object getVarName(int i);
    //public Object getVarType(int i);
    //public boolean getIsParameter(int i);
}
