/*                        TimeSliderListener.java

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



class TimeSliderListener implements ChangeListener {

  public void stateChanged(ChangeEvent e) {
    if (Debugger.reverting) return;
    JSlider js = (JSlider)e.getSource();
    if (!js.getValueIsAdjusting()) {
      int time = (int) Debugger.timeSlider.getValue();
      if (time < 0) return;
      DebuggerCommand dc = new DebuggerCommand(this.getClass(), "slider", time);
      dc.execute();
      //slider(time);
      return;
    }
  }


  public static void slider(int time) {
    TimeStamp ts = TimeStamp.lookup(time);
    Debugger.revert(ts);
  }
}
