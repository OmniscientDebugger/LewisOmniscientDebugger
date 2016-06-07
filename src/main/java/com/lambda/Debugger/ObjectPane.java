/*                        ObjectPane.java

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

//              ObjectPane/ObjectPane.java

/*
 */

import javax.swing.*;
import java.io.*;
import java.util.*;

public class ObjectPane extends AbstractListModel {

	public static VectorD displayList = new VectorD(100);
	// Shadow & ShadowInstanceVariable
	public static final int MAX_VARS_DISPLAYED = 1000;

	public static void set(Object o) {
		int index = Debugger.ObjectsPList.getSelectedIndex();

		if (o == ShadowNull.NULL)
			o = null;
		//System.out.println("Setting " + index + " to " + o);

		if (index == -1) {
			Debugger.message(
				"You must select an instance variable to change",
				true);
			return;
		}

		Shadow s = (Shadow) displayList.elementAt(index);
		if (!(s instanceof ShadowInstanceVariable)) {
			Debugger.message(
				"You must select an instance variable to change",
				true);
			return;
		}
		ShadowInstanceVariable siv = (ShadowInstanceVariable) s;
		try {
			siv.setValue(o);
		} catch (Exception e) {
			String varName = siv.getVarName();
			//System.out.println("Cannot set " + varName + " to " + o);
			Debugger.message("Cannot set " + varName + " to " + o, true);
		}
		Debugger.ObjectsPList.updateUI();
	}

	public static void clear() {
		displayList.removeAllElements();
	}

	// THIS IS A REAL UGLY HACK
	public static void switchTimeLines(boolean clear) { // Must be called AFTER Shadow.switch
		int len = displayList.size();

		for (int i = 0; i < len; i++) {
			Shadow s = (Shadow) displayList.elementAt(i);
			if (s == null)
				continue;
			if (s instanceof ShadowInstanceVariable) {
				ShadowInstanceVariable siv = (ShadowInstanceVariable) s;
				s = siv.s;
				Shadow newS = Shadow.getAlternate(s.obj);
				if (newS != null)
					siv.s = newS;
				// If no such shadow in main timeline, do nothing
				HistoryList hl;
				if (siv.varIndex > -1)
					hl = newS.getShadowVar(siv.varIndex);
				else
					hl = siv.getSleeperHL();
				if (hl == null)
					siv.setHL(new HistoryListSingleton());
				else
					siv.setHL(hl);
			} else {
				Shadow newS = Shadow.getAlternate(s.obj);
				if (newS != null)
					displayList.setElementAt(newS, i);
				// If no such shadow in main timeline, do nothing
			}
		}
	}

	public static String getValueString(int i) {
		if (i < 0)
			return null;
		Shadow sh = (Shadow) displayList.elementAt(i);
		if (sh instanceof ShadowInstanceVariable) {
			ShadowInstanceVariable siv = (ShadowInstanceVariable) sh;
			HistoryList hl = siv.getHL();
			Object value = hl.valueOn(TimeStamp.currentTime(), sh.foreign);
			String pstring;
			if (value instanceof String)
				pstring = "\"" + value + "\"";
			else
				pstring = TimeStamp.trimToLength(value, 500);
			return pstring;
		}
		Object value = sh.obj;
		if (value instanceof String) {
			String pstring = "\"" + value + "\"";
			return pstring;
		}
		String s = sh.printString();
		return (s);
	}

	public int getSize() {
		return displayList.size();
	}

	public Object getElementAt(int i) {
		if (displayList.size() <= i)
			return null;
		return displayList.elementAt(i);
	}

	public String toString() {
		return ("<ObjectPane " + getSize() + ">");
	}

	public static void printAll() {
		D.println("=====ObjectPane=====");
		int len = displayList.size();
		for (int i = 0; i < len; i++) {
			((Shadow) displayList.elementAt(i)).print();
		}
	}

	public static void update() {
		for (int i = 0; i < displayList.size(); i++) {
			Shadow sh = (Shadow) displayList.elementAt(i);
			if (sh instanceof ShadowInstanceVariable) {
				ShadowInstanceVariable siv = (ShadowInstanceVariable) sh;
				if (siv.level == 1) {
					if (siv.expanded) {
						{ //if (siv.getPreviousValue() != siv.getCurrentValue()) {
							for (int j = i + 1; j < displayList.size();) {
								Shadow sh3 = (Shadow) displayList.elementAt(j);
								if (sh3 instanceof ShadowInstanceVariable) {
									if (((ShadowInstanceVariable) sh3).level
										== 1)
										break;
									displayList.removeElementAt(j);
								} else
									break;
							}
							siv.expanded = false; // addSIV will change this
							addSIV(siv, i);
							siv.expanded = true;
							// addSIV messes up on primitives
							continue;
						}
					}
				}
			}
		}
		Debugger.ObjectsPList.updateUI();
	}

	public static void add(int i) {
		Shadow sh = (Shadow) displayList.elementAt(i);
		if (sh instanceof ShadowInstanceVariable) {
			ShadowInstanceVariable siv = (ShadowInstanceVariable) sh;
			Object o = siv.getCurrentValue();
			add(o);
		}
	}

	public static void add(Object o) {
		if (o == null)
			return;
		if (o instanceof UnknownValue)
			o = ((UnknownValue) o).getValue();
		if (o instanceof LockerPair)
			o = ((LockerPair) o).getThread();
		Shadow s;
		if (o instanceof LocksList)
			s = (Shadow) o;
		else
			s = Shadow.getCreateNoDash(o); // GO AHEAD AND SHOW FOREIGN OBJECTS
		if (s == null) {
			D.println("IMPOSSIBLE: There is no Shadow recorded for " + o);
			// impossible
		} else {
			displayList.add(0, s);
			expand(0);
		}
		Object o1 = s.obj;
		String str = o1.toString();
		Debugger.message(str, false);
		Debugger.ObjectsPList.updateUI();
	}

	public static void addSIV(ShadowInstanceVariable siv, int i) {
		Object o = siv.getCurrentValue();
		if (o instanceof UnknownValue)
			o = ((UnknownValue) o).getValue();
		if ((o instanceof ShadowPrimitive) || (o == null))
			return;
		Shadow s;
		if (o instanceof LocksList)
			s = (Shadow) o;
		else
			s = Shadow.get(o);
		boolean showAll = false;

		if (siv.expanded) {
			siv.expanded = false;
			for (int j = i + 1; j < displayList.size();) {
				Shadow sh = (Shadow) displayList.elementAt(j);
				if (sh instanceof ShadowInstanceVariable) {
					ShadowInstanceVariable siv2 = (ShadowInstanceVariable) sh;
					if (siv2.level == 1)
						break;
					displayList.removeElementAt(j);
				} else
					break;
			}
		} else {
			int len = Math.min(s.size(), MAX_VARS_DISPLAYED);
			List displayedVars = DisplayVariables.getDisplayedVariables(s);
			for (int j = 0;
				j < len;
				j++) { // How to deal with large arrays? CHOP AT MAX_VARS_DISPLAYED.
				if (!showAll && (displayedVars != null)) {
					if (!displayedVars.contains(s.getVarName(j)))
						continue;
				}
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, j, 2),
					i + 1);
			}
			HistoryList bhl = s.getBlockedHL();
			if (bhl != null)
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, bhl, Shadow.BLOCKEDON, 2),
					i + 1);
			SleeperSet ss = s.getSleeperSet();
			if (ss != null) {
				HistoryList ohl = ss.owner;
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, ohl, Shadow.OWNER, 2),
					i + 1);
				HistoryList shl = ss.sleepers;
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, shl, Shadow.SLEEPERS, 2),
					i + 1);
				HistoryList whl = ss.waiters;
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, whl, Shadow.WAITERS, 2),
					i + 1);
			}

			siv.expanded = true;
		}
		Debugger.ObjectsPList.updateUI();
	}

	public static void remove(int i) {
		if ((i < 0) || (i >= displayList.size()))
			return;
		Object o = displayList.elementAt(i);
		if (o instanceof ShadowInstanceVariable) {
			DisplayVariables.removeVar((ShadowInstanceVariable) o);
			displayList.removeElementAt(i);
			Debugger.ObjectsPList.updateUI();
			return;
		}
		Shadow s = (Shadow) o;
		displayList.removeElementAt(i);
		while ((displayList.size() > i)
			&& (displayList.elementAt(i) instanceof ShadowInstanceVariable)) {
			displayList.removeElementAt(i);
		}
		Debugger.ObjectsPList.updateUI();
	}

	public static void retain(int i) {
		int j;
		int size = displayList.size();
		if ((i < 0) || (i >= size))
			return;
		Object o = displayList.elementAt(i);

		if (!(o instanceof ShadowInstanceVariable))
			return;

		for (j = i - 1; j >= 0; j--) { // Kinda weird way of doing this.
			Object o1 = displayList.elementAt(j);
			if (!(o1 instanceof ShadowInstanceVariable))
				break;
			DisplayVariables.removeVar((ShadowInstanceVariable) o1);
			displayList.removeElementAt(j);
		}
		for (j = j + 2;
			j < displayList.size();
			) { // NB: The displayList is getting smaller.
			Object o1 = displayList.elementAt(j);
			if (!(o1 instanceof ShadowInstanceVariable))
				break;
			DisplayVariables.removeVar((ShadowInstanceVariable) o1);
			displayList.removeElementAt(j);
		}
		Debugger.ObjectsPList.updateUI();
		return; // updateUI must be done by caller

	}

	public static void expand(int i) {
		expand(i, false);
	}

	public static void expand(int i, boolean showAll) {
		if ((i < 0) || (i >= displayList.size()))
			return;
		Shadow s = (Shadow) displayList.elementAt(i);
		if (s == null) {
			D.println("expand bug. NULL SHADOE " + i);
			return;
		}
		if (s instanceof ShadowInstanceVariable) {
			ShadowInstanceVariable siv = (ShadowInstanceVariable) s;
			if (siv.level == 1)
				addSIV(siv, i);
			else
				add(siv.getCurrentValue());
			return;
		}

		if ((displayList.size() > i + 1)
			&& (displayList.elementAt(i + 1) instanceof ShadowInstanceVariable)) {
			// already expanded
			while ((displayList.size() > i + 1)
				&& (displayList.elementAt(i + 1)
					instanceof ShadowInstanceVariable)) {
				displayList.removeElementAt(i + 1);
			}
		} else {
			Comparator sivSorter = new Comparator() {
				public int compare(Object o1, Object o2) {
					if ((o1 instanceof ShadowInstanceVariable) && (o2 instanceof ShadowInstanceVariable)) {
						ShadowInstanceVariable siv1 = (ShadowInstanceVariable) o1;
						ShadowInstanceVariable siv2 = (ShadowInstanceVariable) o2;
						return (siv1.getVarName().compareTo(siv2.getVarName()));
						}
						throw new DebuggerException("IMPOSSIBLE " + o1 +" " + o2);
				}
			};
			int len = Math.min(s.size(), MAX_VARS_DISPLAYED);
			List displayedVars = DisplayVariables.getDisplayedVariables(s);
			int unShown = (displayedVars == null) ? 0 : displayedVars.size();
			ShadowInstanceVariable[] sivs = new ShadowInstanceVariable[len-unShown];
			int index = 0;
			for (int j = 0; j < len; j++) {
				// How to deal with large arrays? CHOP AT MAX_VARS_DISPLAYED.
				if (!showAll && (displayedVars != null)) {
					if (!displayedVars.contains(s.getVarName(j)))
						continue;
				}
				ShadowInstanceVariable siv2 = new ShadowInstanceVariable(s, j);
				sivs[index] = siv2;
				index++;
			}
			if (index > 0) {
				char c = sivs[0].getVarName().charAt(0);
				if (!Character.isDigit(c)) Arrays.sort(sivs, sivSorter);	//Don't sort arrays!
			}
			for (int j = 0; j < index; j++) {
				displayList.insertElementAt(sivs[j], i + 1);
			}
			HistoryList bhl = s.getBlockedHL();
			if (bhl != null)
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, bhl, Shadow.BLOCKEDON),
					i + 1);
			SleeperSet ss = s.getSleeperSet();
			if (ss != null) {
				HistoryList ohl = ss.owner;
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, ohl, Shadow.OWNER),
					i + 1);
				HistoryList shl = ss.sleepers;
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, shl, Shadow.SLEEPERS),
					i + 1);
				HistoryList whl = ss.waiters;
				displayList.insertElementAt(
					new ShadowInstanceVariable(s, whl, Shadow.WAITERS),
					i + 1);
			}
		}
		Debugger.ObjectsPList.updateUI();
	}

	public static void close(int i) {
		if ((i < 0) || (i >= displayList.size()))
			return;
		Shadow s = (Shadow) displayList.elementAt(i);
		if (s == null) {
			D.println("expand bug. NULL SHADOE " + i);
			return;
		}
		if (s instanceof ShadowInstanceVariable) {
			return;
		}

		if ((displayList.size() > i + 1)
			&& (displayList.elementAt(i + 1) instanceof ShadowInstanceVariable)) {
			// already expanded
			while ((displayList.size() > i + 1)
				&& (displayList.elementAt(i + 1)
					instanceof ShadowInstanceVariable)) {
				displayList.removeElementAt(i + 1);
			}
		}
		Debugger.ObjectsPList.updateUI();
	}

	public static void expandClass(int i) {
		Object o;
		Class c;
		if ((i < 0) || (i >= displayList.size()))
			return;
		Shadow s = (Shadow) displayList.elementAt(i);

		if (s instanceof ShadowInstanceVariable) {
			o = ((ShadowInstanceVariable) s).getCurrentValue();
			if ((o == null) || (o instanceof ShadowPrimitive)) {
				Debugger.message("Cannot show class for primitives/null", true);
				return;
			}
			c = o.getClass();
		} else {
			c = s.obj.getClass();
		}
		Shadow sc = Shadow.get(c);
		if (sc == null) {
			D.println("expandClass null??" + c);
			return;
		}
		displayList.add(0, sc);
		expand(0);
		Debugger.ObjectsPList.updateUI();
		return;
	}

	public static TimeStamp getFirst(int i) {
		if ((i < 0) || (i >= displayList.size()))
			return (null);
		Object o = displayList.elementAt(i);
		if (o instanceof ShadowInstanceVariable) {
			ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
			//D.println(iv + " getFirst "+ iv.getFirst());
			return (iv.getFirst());
		}
		TimeStamp ts0 = TimeStamp.eot();
		for (int j = i + 1; j < displayList.size(); j++) {
			Object o1 = displayList.elementAt(j);
			if (o1 instanceof ShadowInstanceVariable) {
				ShadowInstanceVariable siv1 = (ShadowInstanceVariable) o1;
				TimeStamp ts1 = siv1.getFirst();
				if (ts1 == null)
					continue;
				if (ts1.earlierThan(ts0))
					ts0 = ts1;
				continue;
			}
			break;
		}
		if (ts0 == TimeStamp.eot())
			return null;
		return (ts0);
	}

	public static TimeStamp getPrevious(int i) {
		if ((i < 0) || (i >= displayList.size()))
			return null;
		Object o = displayList.elementAt(i);
		if (o instanceof ShadowInstanceVariable) {
			ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
			return (iv.getPrevious());
		}
		TimeStamp ts0 = TimeStamp.bot();
		for (int j = i + 1; j < displayList.size(); j++) {
			Object o1 = displayList.elementAt(j);
			if (o1 instanceof ShadowInstanceVariable) {
				ShadowInstanceVariable siv1 = (ShadowInstanceVariable) o1;
				TimeStamp ts1 = siv1.getPrevious();
				if (ts1 == null)
					continue;
				if (ts1.laterThan(ts0))
					ts0 = ts1;
				continue;
			}
			break;
		}
		if (ts0 == TimeStamp.bot())
			return null;
		return (ts0);
	}

	public static TimeStamp getLast(int i) {
		if ((i < 0) || (i >= displayList.size()))
			return (null);
		Object o = displayList.elementAt(i);
		if (o instanceof ShadowInstanceVariable) {
			ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
			return (iv.getLast());
		}
		TimeStamp ts0 = TimeStamp.bot();
		for (int j = i + 1; j < displayList.size(); j++) {
			Object o1 = displayList.elementAt(j);
			if (o1 instanceof ShadowInstanceVariable) {
				ShadowInstanceVariable siv1 = (ShadowInstanceVariable) o1;
				TimeStamp ts1 = siv1.getLast();
				if (ts1 == null)
					continue;
				if (ts1.laterThan(ts0))
					ts0 = ts1;
				continue;
			}
			break;
		}
		if (ts0 == TimeStamp.bot())
			return null;
		return (ts0);
	}

	/*
	public static TimeStamp getLast(int i) {
	if ((i<0)||(i>=displayList.size())) return(null);
	Object o = displayList.elementAt(i);
	if (o instanceof ShadowInstanceVariable) {
	    ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
	    //D.println(iv + " getLast "+ iv.getLast());
	    return(iv.getLast());
	}
	Shadow s = (Shadow) o;
	TimeStamp ts = s.getLastAllVars();
	return(ts);
	}
	*/

	public static TimeStamp getNext(int i) {
		if ((i < 0) || (i >= displayList.size()))
			return null;
		Object o = displayList.elementAt(i);
		if (o instanceof ShadowInstanceVariable) {
			ShadowInstanceVariable iv = (ShadowInstanceVariable) o;
			//D.println(iv + " getNext "+ iv.getNext());
			return (iv.getNext());
		}
		TimeStamp ts0 = TimeStamp.eot();
		for (int j = i + 1; j < displayList.size(); j++) {
			Object o1 = displayList.elementAt(j);
			if (o1 instanceof ShadowInstanceVariable) {
				ShadowInstanceVariable siv1 = (ShadowInstanceVariable) o1;
				TimeStamp ts1 = siv1.getNext();
				if (ts1 == null)
					continue;
				if (ts1.earlierThan(ts0))
					ts0 = ts1;
				continue;
			}
			break;
		}
		if (ts0 == TimeStamp.eot())
			return null;
		return (ts0);
	}

	public static Shadow getShadowAt(int i) {
		if ((i < 0) || (i >= displayList.size())) {
			Debugger.message("No instance variable selected.", true);
			return null;
		}
		Shadow s = (Shadow) displayList.elementAt(i);
		return s;
	}

	public static void selectFrom(int i) {
		if ((i < 0) || (i >= displayList.size())) {
			Debugger.message("No instance variable selected.", true);
			return;
		}
		Shadow s = (Shadow) displayList.elementAt(i);
		if (s == null) {
			D.println("selectFrom bug. NULL SHADOE " + i);
			return;
		}
		if (s instanceof ShadowInstanceVariable) {
			ShadowInstanceVariable siv = (ShadowInstanceVariable) s;
			HistoryList hl = siv.getHL();
			TVPair[] values = hl.getValues();
			ValueChooser.initialize(
				Debugger.mainFrame,
				values,
				"Select a Value");
			ValueChooser.showDialog(null, "");
			return;
		}
		Debugger.message("Please select an INSTANCE VARIABLE.", true);
	}

	public static void main(String[] args) {
		D.println("----------------------ObjectPane----------------------\n");

		ObjectPane l = new ObjectPane();
		l.printAll();
		D.println("----------------------ObjectPane----------------------\n");

	}

	public static void hex() {
		Object value;
		int level;
		int index = Debugger.ObjectsPList.getSelectedIndex();
		Shadow sh = (Shadow) Debugger.ObjectsPList.getSelectedValue();
		if (!(sh instanceof ShadowInstanceVariable)) {
			value = sh.obj;
			level = 1;
		} else {
			ShadowInstanceVariable siv = (ShadowInstanceVariable) sh;
			value = siv.getCurrentValue();
			siv.special = !siv.special;
			level = 2;
		}
		if (value instanceof UnknownValue)
			value = ((UnknownValue) value).getValue();
		if (value instanceof int[]) {
			for (int i = index + 1; i < displayList.size(); i++) {
				Shadow sh2 = (Shadow) displayList.elementAt(i);
				if (!(sh2 instanceof ShadowInstanceVariable))
					break;
				ShadowInstanceVariable siv2 = (ShadowInstanceVariable) sh2;
				if (siv2.level != level)
					break;
				siv2.special = !siv2.special;
			}
		}
		Debugger.ObjectsPList.updateUI();
	}

}
