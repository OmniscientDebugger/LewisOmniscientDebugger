/*                        DoubleClickJList.java

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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class DoubleClickJList extends JList {

    public DoubleClickJList(AbstractListModel data) {
	super(data);
	init();
    }

    public DoubleClickJList(Vector data) {
	super(data);
	init();
    }

    private void init() {
	addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		Object o = getSelectedValue();
		if ((e.getClickCount() == 2)) {
		    if (o instanceof MethodLine)
			select((MethodLine)o, e.getX());
		    else if (o instanceof Shadow)
			select((Shadow)o, e.getX());
		    else if (o instanceof ShadowLocal)
			select((ShadowLocal)o, e.getX());
		    else if (o instanceof StackListElement)
			select((StackListElement)o, e.getX());
		    else if (o instanceof ThreadPane)
			select((ThreadPane)o, e.getX());
		    else if (o instanceof FileLine)
			select((FileLine)o, e.getX());
		} 
	    }
	});
    }

    private void execute(Object selected) {
	if (selected == null) {
	    Debugger.message("No object under mouse", true);
	    return;
	}
	if (MiniBuffer.wantsInput())
	    MiniBuffer.add(selected);
	else {
	    if (this != Debugger.codeJList) {
		if (this == Debugger.ObjectsPList) 
		    ObjectPane.expand(this.getSelectedIndex());
		else
		    ObjectPane.add(selected);
	    }
	}
	repaint();
    }

    public void select(MethodLine t, int x) {
	Object selected = t.getSelectedObject(x, getFontMetrics(getFont()));
	execute(selected);
    }
    public void select(FileLine t, int x) {
	Object selected = t.getSelectedObject(x, getFontMetrics(getFont()));
	execute(selected);
    }
    public void select(Shadow t, int x) {
	Object selected = t.getSelectedObject(x, getFontMetrics(getFont()));
	execute(selected);	    
    }
    public void select(ShadowLocal t, int x) {
	Object selected = t.getSelectedObject(x, getFontMetrics(getFont()));
	execute(selected);
    }
    public void select(StackListElement sl, int x) {
	Object selected = sl.getSelectedObject(x, getFontMetrics(getFont()));
	execute(selected);
    }
    public void select(ThreadPane sl, int x) {
	Object selected = sl.getSelectedObject(x, getFontMetrics(getFont()));
	execute(selected);
    }


    static int MAX = 5;
    public static void main(String[] args) {
      /*
	TraceLine traceLine=null;
	if (args.length > 0) MAX = Integer.parseInt(args[0]);
	JFrame frame = new JFrame("Test Bil's JList");
	//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	SourceLine sl = SourceLine.getSourceLine("Obj:File.java:23");
	Object t = new Object();
	String meth = "frob";
	Object[] a = new Object[] {"arg1", "arg2", "arg3"};
	for (int i=0; i < MAX; i++) traceLine = TraceLine.addTrace(sl, t,  meth+i, a);
	JList list = new DoubleClickJList(traceLine);
	frame.getContentPane().add(list);
	frame.setBounds(200, 200, 400, 300);
	frame.setVisible(true);
	*/
    }

}


class TraceItem {
    String o, m, arg1, arg2;
    static int count=0;
    int cnt;
    Object[] objects;

    public TraceItem(String o, String meth,String arg1,String arg2) {
	this.o = o;
	this.m = meth;
	this.arg1 = arg1;
	this.arg2 = arg2;
	cnt = count++;
	objects = new Object[4];
	objects[0] = o;
	objects[1] = m;
	objects[2] = arg1;
	objects[3] = arg2;
    }

    public String toString() {
	String s = "";
	if (cnt > 1) s = "  ";
	s = s + o+"."+m+"("+arg1+", "+arg2+")";
	return s;
    }

    public Object getSelectedObject(int x, FontMetrics fm) {
	Object[] call = objects;
	String str="";
	if (cnt > 1) str = "  ";
	if (x < fm.stringWidth(str)) return(null);
	str += call[0];
	if (x < fm.stringWidth(str)) return(call[0]);
	str += "."+ call[1]+"(";
	if (x < fm.stringWidth(str)) return(null);

	for (int i = 2; i < call.length; i++) {
	    str += call[i];
	    if (i < call.length - 1) str += ", ";
	    if (x < fm.stringWidth(str)) return(call[i]);
	}
	str += ")";
	return(null);
    }

}	    
