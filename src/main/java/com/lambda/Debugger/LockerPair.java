/*                        LockerPair.java

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

//              Shadow.java

/*
 */



public class LockerPair {
  private int count = 1;
  private LockerPair previousLP;
  private Thread tid;

  public LockerPair(LockerPair lp, Thread t) {
    if (lp != null) count = lp.count+1;
    previousLP = lp;
    tid = t;
  }

  public LockerPair previous() {
    return(previousLP);
  }
  public Thread getThread() {
    return(tid);
  }
  public int getCount() {
    return(count);
  }

  private LockerPair() {}

  public String toString(int len) {
    if (count == 1)
      return(TimeStamp.trimToLength(tid, len));
    else
      return(TimeStamp.trimToLength(tid, len)+":"+count);
  }

}
