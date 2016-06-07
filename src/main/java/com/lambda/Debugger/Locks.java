/*                        Locks.java

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

import java.io.*;
import java.util.*;


class Locks {
  Vector		sleepersV = new Vector();
  String		lockOwner;
  String		sleepers = "";



  public static void gettingLock(int slIndex, Object obj, TraceLine tl) {
    Thread tid = tl.getThread();
    //    System.out.println("gettingLock( " + obj + " ) " + tid);
    int time = TimeStamp.addStamp(slIndex, TimeStamp.LOCKING, tl);

    Shadow sh = Shadow.get(tid);
    sh.threadGetting(time, obj, tl);

    Shadow objSh = Shadow.get(obj);
    objSh.addSleeper(time, tid, tl);
  }

  public static void gotLock(int slIndex, Object obj, TraceLine tl) {
    Thread tid = tl.getThread();
    //    System.out.println("gotLock( " + obj + " ) " + tid);
    int time;
    time = TimeStamp.previousTSGettingLock(tid);				// Do elide, use this time instead of creating a new one
    boolean elide = (time != -1);
    if (!elide) time = TimeStamp.addStamp(slIndex, TimeStamp.LOCKING, tl);
    
    Shadow sh = Shadow.get(tid);
    sh.threadGot(time, obj, tl, elide);

    Shadow objSh = Shadow.get(obj);
    objSh.removeSleeperAddOwner(time, null, tl, elide);

  }

  public static void releasingLock(int slIndex, Object obj, TraceLine tl) {
    Thread tid = tl.getThread();
    //    System.out.println("releasingLock( " + obj + " ) " + tid);
    int time = TimeStamp.addStamp(slIndex, TimeStamp.UNLOCKING, tl);

    Shadow objSh = Shadow.get(obj);
    boolean freed = objSh.removeOwner(time, obj, tl);
    
    if (freed) {
      Shadow sh = Shadow.get(tid);
      sh.threadReleasing(time, obj, tl);
    }
  }

  public static void startingWait(int slIndex, Object obj, TraceLine tl) {
    Thread tid = tl.getThread();
    //    System.out.println("startingWait( " + obj + " ) " + tid);
    int time = TimeStamp.addStamp(slIndex, TimeStamp.WAITING, tl);

    Shadow sh = Shadow.get(tid);
    sh.threadGetting(time, obj, tl);

    Shadow objSh = Shadow.get(obj);
    //    objSh.removeOwner(time, obj, tl);
    objSh.addWaiterRemoveOwner(time, tid, tl);
  }

  public static void endingWait(int slIndex, Object obj, TraceLine tl) {
    Thread tid = tl.getThread();
    //    System.out.println("endingWait( " + obj + " ) " + tid);
    int time = TimeStamp.addStamp(slIndex, TimeStamp.WAITED, tl);

    Shadow sh = Shadow.get(tid);
    sh.threadGot(time, obj, tl, false);

    Shadow objSh = Shadow.get(obj);
    objSh.removeWaiterAddOwner(time, tid, tl);
  }

  public static void startingJoin(int slIndex, Thread targetTid, TraceLine tl) {
    Thread tid = tl.getThread();
    //    System.out.println("startingJoin( " + targetTid + " ) " + tid);
    int time = TimeStamp.addStamp(slIndex, TimeStamp.WAITING, tl);

    Shadow sh = Shadow.get(tid);
    sh.threadGetting(time, targetTid, tl);

    Shadow targetTidSh = Shadow.get(targetTid);
    //    targetTidSh.removeOwner(time, targetTid, tl);
    targetTidSh.addJoiner(time, tid, tl);
  }

  public static void endingJoin(int slIndex, Thread targetTid, TraceLine tl) {
    Thread tid = tl.getThread();
    //    System.out.println("endingJoin( " + targetTid + " ) " + tid);
    int time = TimeStamp.addStamp(slIndex, TimeStamp.WAITED, tl);

    Shadow sh = Shadow.get(tid);
    sh.threadGot(time, targetTid, tl, false);

    Shadow targetTidSh = Shadow.get(targetTid);
    targetTidSh.removeJoiner(time, tid, tl);
  }


}
