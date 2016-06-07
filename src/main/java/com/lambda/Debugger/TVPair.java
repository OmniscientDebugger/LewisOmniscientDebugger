/*                        TVPair.java

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

//              HistoryList/HistoryList.java

/*
 */


import java.io.*;
import java.util.*;


public class TVPair {
  public int time;
  public Object value;

  public String toString() {
    return TimeStamp.trimToLength(value, 40);
  }

  public TVPair(int time, Object v) {
    this.time = time;
    value = v;
  }
}
