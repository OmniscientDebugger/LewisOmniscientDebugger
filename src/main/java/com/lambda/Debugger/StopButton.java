/*                        StopButton.java

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

public class StopButton extends JFrame {

    static JPanel topPanel;
    static JButton stopButton;
    static StopButton mainFrame;
    static JCheckBox debuggerCB, instrumentCB, recordCB, pauseProgamCB; // toStringCB
                                                                        // ;
    static String stopText;
    static String START = "Start Recording", STOP = "Stop Recording",
            START_TARGET = "Start Program";

    public StopButton(boolean startTarget, boolean paused, boolean show,
            boolean instrument) {
        if (paused) {
            stopText = START;
            D.DISABLE = true;
        } else {
            stopText = STOP;
            D.DISABLE = false;
        }
        if (!startTarget)
            stopText = START_TARGET;
        Debugger.INSTRUMENT = instrument;
        Debugger.PAUSED = paused;
        Debugger.SHOW = show;

        setTitle("Debugger Controller - " + Debugger.programName);

        topPanel = new JPanel();
        // topPanel.setLayout( new FlowLayout() );
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        getContentPane().add(topPanel);

        topPanel.add(stopButton = new JButton(stopText));
        stopButton.setToolTipText("Start/Stop Recording");
        ActionListener listener = new StopButtonActionListener(stopButton);
        stopButton.addActionListener(listener);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(debuggerCB = new JCheckBox("Bring up Debugger on Stop",
                Debugger.SHOW));
        p.add(instrumentCB = new JCheckBox("Instrument Classes",
                Debugger.INSTRUMENT));
        p.add(recordCB = new JCheckBox("Start Recording Immediately",
                !Debugger.PAUSED));
        p.add(pauseProgamCB = new JCheckBox("Pause Program on Stop",
                Debugger.PAUSE_ON_STOP));
        topPanel.add(p);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void create(boolean startTarget, boolean paused,
            boolean show, boolean instrument) {
        final boolean startTarget2 = startTarget;
        final boolean paused2 = paused;
        final boolean show2 = show, instrument2 = instrument;
        Runnable r = new Runnable() {
            public void run() {
                runButton(startTarget2, paused2, show2, instrument2);
            }
        };
        if (Thread.currentThread().getName().startsWith("AWT")) {
            r.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(r);
        } catch (InterruptedException ie) {
        }// impossible
        catch (java.lang.reflect.InvocationTargetException ie) {
        }// impossible
    }

    public static void runButton(boolean startTarget, boolean paused,
            boolean show, boolean instrument) {
        mainFrame = new StopButton(startTarget, paused, show, instrument);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void main(String args[]) {
        create(false, (System.getProperty("PAUSED") != null), (System
                .getProperty("DONT_SHOW") != null), false);
    }
}
