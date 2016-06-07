/*                        HistoryList.java

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



public abstract class HistoryList {

    public static int		nEntries=0;

    public abstract TVPair[] getValues();
    public static void printStatistics() {}
    public abstract int size();
    public abstract TimeStamp getTS(int i);
    public abstract int getTime(int i);
    public abstract Object getValue(int i);
    public abstract void setValue(Object obj, String VarName, Object value) throws NoSuchFieldException, SecurityException, CompletionException ;
    public abstract HistoryList add(int time, Object o);
    public abstract HistoryList add(int time, int i);
    public abstract void print();
    public abstract String toString(int i);
    public abstract TimeStamp getFirst();
    public abstract TimeStamp getLast();
    public abstract Object getLastValue();
    public abstract TimeStamp getPrevious();
    public abstract TimeStamp getNext();
    public abstract Object valueOn(TimeStamp ts, boolean foreign);
    public abstract Object valueOn(int time, boolean foreign);
    public abstract HistoryList dup(int time);
    public abstract void reset(int time);
    public abstract void removeLast();
    public abstract boolean compact(int eot);
    public abstract void verify(int eot);

    public void initializeEvents(String varName) {
	int nEntries = size();
	for (int i = 0; i < nEntries; i++) {
	    Object value = getValue(i);
	    int time = getTime(i);
	    EventInterface.record(time, varName, value);
	}
    }

    public void initializeEvents(Object object, String varName) {
	int nEntries = size();
	for (int i = 0; i < nEntries; i++) {
	    Object value = getValue(i);
	    int time = getTime(i);
	    EventInterface.record(time, object, varName, value);
	}
    }


}
