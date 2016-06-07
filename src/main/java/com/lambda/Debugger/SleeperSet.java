/*                        SleeperSet.java

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

public class SleeperSet {

    protected HistoryList sleepers = new HistoryListMultiple(Shadow.SLEEPERS);
    protected HistoryList owner = new HistoryListMultiple(Shadow.OWNER);
    protected HistoryList waiters = new HistoryListMultiple(Shadow.WAITERS);

    private SleeperSet() {}

    SleeperSet(int time) {
	LocksList sl = new LocksList();
	LocksList wl = new LocksList();
	sleepers.add(time, sl);
	waiters.add(time, wl);
    }


    SleeperSet dup(int now) {
	SleeperSet ss = new SleeperSet(0);
	//	ss.sleepers = sleepers.dup(now);
	//	ss.waiters = waiters.dup(now);
	//	ss.owner = owner.dup(now);
	return ss;
    }

}
