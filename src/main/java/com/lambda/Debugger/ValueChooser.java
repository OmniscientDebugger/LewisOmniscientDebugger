/*                        ValueChooser.java

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
import java.lang.reflect.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.net.*;
import java.util.jar.*;


public class ValueChooser extends JDialog {
  private JList list;
  private static ValueChooser dialog;
  private static String value ="";

  public static void initialize(Component c, TVPair[] values, String title) {
    Frame f = JOptionPane.getFrameForComponent(c);
    int now = TimeStamp.currentTime().time;
    int selectedIndex = values.length-1;
    for (int i = 1; i < values.length; i++) {
	TVPair tp = values[i];
	if (tp.time > now) {
	    selectedIndex = i-1;
	    break;
	}
    }
    dialog = new ValueChooser(f, values, title, selectedIndex);
  }


  private ValueChooser(Frame frame, TVPair[] data, String title, int si) {
    super(frame, title, true);

    list = new JList(data);
    list.setSelectedIndex(si);
    list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if  ( (!e.getValueIsAdjusting()) && (list.getSelectedIndex() >= 0) && (!Debugger.reverting) ) {
	  //System.out.println("Selected " + list.getSelectedValue());
	  TimeStamp ts = TimeStamp.lookup(((TVPair)list.getSelectedValue()).time);
	  Debugger.revert(ts);
	  ValueChooser.dialog.setVisible(false);
	}
      } } );
      JScrollPane jsp = new JScrollPane(list);
      if (Debugger.SCREEN_SHOT || Debugger.VGA) {
	  jsp.setPreferredSize(new Dimension(180, 300));
	  jsp.setMaximumSize(new Dimension(250, 500));
      }
      else {
	  jsp.setPreferredSize(new Dimension(350, 600));
	  jsp.setMaximumSize(new Dimension(450, 850));
      }
      JButton cancel = new JButton("Cancel");
      cancel.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  ValueChooser.dialog.setVisible(false);
	}
      } );

      JPanel jp = new JPanel();
      if (Debugger.SCREEN_SHOT || Debugger.VGA) {
	  jp.setPreferredSize(new Dimension(220, 350));
	  jp.setMaximumSize(new Dimension(280, 550));
      }
      else {
	  jp.setPreferredSize(new Dimension(380, 650));
	  jp.setMaximumSize(new Dimension(480, 850));
      }
      jp.add(jsp, BorderLayout.NORTH);
      jp.add(cancel, BorderLayout.SOUTH);

      Container c = getContentPane();
      c.add(jp, BorderLayout.CENTER);
      pack();
      }

  public static String showDialog(Component comp, String init) {
    if (dialog != null) {
      dialog.value = init;
      dialog.setLocationRelativeTo(comp);
      dialog.setVisible(true);
    }
    return value;
  }
      


  public static void main(String[] args) {
    String[] names = {"Me", "You", "Me", "You", "Me", "You", "Me", "You", "Me", "You", "Me", "You", "Me", "You", "Me", "You", "Me", "You", "Me", "You"};
    JFrame f = new JFrame("Select a Value");
    TVPair[] tp = new TVPair[names.length];

    for (int i = 0; i < names.length; i++) tp[i] = new TVPair(0, names[i]);

    JButton b = new JButton("Select a vlaue");
    ValueChooser.initialize(f, tp, "VC");
    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String n = ValueChooser.showDialog(null, "You");
	System.out.println("Selected " + n);
      }
    } );

    JPanel cp = new JPanel();
    f.setContentPane(cp);
    cp.add(b);
    f.pack();
    f.setVisible(true);
  }

}
