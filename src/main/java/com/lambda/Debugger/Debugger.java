/*                        Debugger.java

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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

public class Debugger extends JFrame {
    private static int FONT_SIZE = 10;
    private static String FONT = "Courier";
    static String version = "28.Mar.07";
    static boolean firstRun = false;
    static Debugger mainFrame;
    static long endTime, startTime, totalTime;
    private static boolean firstTimeTracePane = true;
    protected static long timeDebugifying = 0;
    // Used by statistics & class loader

    static boolean GC_OFF = false;
    static boolean SHOW = true; // Used by StopButton.java
    static boolean NO_WINDOWS = false; // Used by StopButton.java
    static boolean INSTRUMENT = true; // Used by StopButton.java
    static boolean BUG = false;
    static boolean VGA = false;
    static boolean SCREEN_SHOT = false;
    static boolean DEBUGIFY_ONLY = false;
    static boolean PAUSED = false;
    // static boolean OUTPUT_ONLY = false; // Used by StopButton.java
    static boolean START = true;
    static boolean NATIVE_TOSTRING = false;
    static boolean TRACE_LOADER = false;
    static boolean TRACE_LOADER_STACK = false;//***************************
    static boolean TEST = false;
    static boolean DEMO = false; // Set by Launcher
    static boolean CMD_LINE = true; // Started from the command line?
    static boolean PAUSE_ON_STOP = false;
    static boolean KILL_TARGET_ON_STOP = true;
    static boolean DEBUG = false;
    static boolean DEBUG_DEBUGGER = false; // Set to false for users.
    static boolean NO_DEFAULTS = false;
    static boolean USE_BOOTCLASSLOADER = (InstrumentorForCL.class
            .getClassLoader() == null);
    static long MAX_MEMORY = 80000000;
    static Object[] argList = { new String[0] };
    static Class clazz = null;
    static String programName = "";
    static String DIRECTORY = "./";
    public static ClassLoader classLoader;
    public static String ODBName = "ODDB";
    static {
        if ("com.lambda".startsWith("com"))
            ODBName = "ODB";
    }
    static PrintStream StdOut = System.out;

    static private void readCommandLineFlags() {

        // These are used regularly and defined in the defaults file
        // String cd = System.getProperty("CUTOFF_DEPTH"); if (cd != null)
        // D.CUTOFF_DEPTH = Integer.parseInt(cd);
        {
            String s = System.getProperty("MEMORY");
            if (s != null) {
                MAX_MEMORY = Long.parseLong(s);
                if (MAX_MEMORY > ((long)Integer.MAX_VALUE))
                    MAX_MEMORY = (long)Integer.MAX_VALUE;
                TimeStamp.setMax((int) (MAX_MEMORY / 200L));
            }
        }  {
            String s = System.getProperty("FONT");
            if (s != null) {
                FONT = s;
            }
        }  {
            String s = System.getProperty("FONT_SIZE");
            if (s != null) {
                FONT_SIZE = Integer.parseInt(s);
            }
        }
        if (System.getProperty("GC_OFF") != null)
            GC_OFF = true;
        if (System.getProperty("DONT_SHOW") != null)
            SHOW = false;
        if (System.getProperty("NO_WINDOWS") != null) {
            SHOW = false;
            NO_WINDOWS = true;
        }
        if (System.getProperty("DONT_INSTRUMENT") != null)
            INSTRUMENT = false;
        if (System.getProperty("PAUSED") != null) {
            D.DISABLE = true;
            PAUSED = true;
        }
        // if (System.getProperty("OUTPUT_ONLY") != null) {OUTPUT_ONLY = true;
        // PAUSED=true;}
        if (System.getProperty("DONT_START") != null)
            START = false;
        if (System.getProperty("DONT_KILL_TARGET") != null)
            KILL_TARGET_ON_STOP = false;

        // These are rarely used by regular users
        if (System.getProperty("NO_DEFAULTS") != null)
            NO_DEFAULTS = true;
        if (System.getProperty("NATIVE_TOSTRING") != null)
            NATIVE_TOSTRING = true;
        if (System.getProperty("DEBUGIFY_ONLY") != null)
            DEBUGIFY_ONLY = true;

        // These are never used by regular users
        if (System.getProperty("BUG") != null)
            BUG = true;
        if (System.getProperty("VGA") != null)
            VGA = true;
        if (System.getProperty("SCREEN_SHOT") != null)
            SCREEN_SHOT = true;
        if (System.getProperty("TRACE_LOADER") != null)
            TRACE_LOADER = true;
        if (System.getProperty("TRACE_LOADER_STACK") != null)
            TRACE_LOADER = TRACE_LOADER_STACK = true;
        if (System.getProperty("TEST") != null)
            TEST = true;
        if (System.getProperty("DEBUG_DEBUGGER") != null)
            DEBUG_DEBUGGER = D.DEBUG_DEBUGGER = true;
        if (System.getProperty("DONT_PAUSE_ON_STOP") != null)
            PAUSE_ON_STOP = false;

        // if (D.CUTOFF_DEPTH > 0 && D.CUTOFF_DEPTH != D.DONT_START)
        // Debugger.println("Only calls less than "+D.CUTOFF_DEPTH+" deep will
        // be recorded (and no assignments).");
        if (PAUSED)
            Debugger
                    .println("Recording will not start until 'Start Recording' is pushed");
        if (!INSTRUMENT)
            Debugger.println("Classes will not be instrumented");
        if (!SHOW & !PAUSED)
            Debugger.println("Recording will continue until 'Stop' is pushed");

    }

    public static void println(String s) {
        if (!NO_WINDOWS)
            StdOut.println(ODBName + ": " + s);
    }

    private void initialize() {
        ClassLoader cl = getClass().getClassLoader();
        upImage = new ImageIcon(cl.getResource("images/up16.gif"));
        downImage = new ImageIcon(cl.getResource("images/down16.gif"));
        firstImage = new ImageIcon(cl.getResource("images/first16.gif"));
        backImage = new ImageIcon(cl.getResource("images/back16.gif"));
        forwardImage = new ImageIcon(cl.getResource("images/forward16.gif"));
        lastImage = new ImageIcon(cl.getResource("images/last16.gif"));
        loopImage = new ImageIcon(cl.getResource("images/loop16.gif"));
        backLoopImage = new ImageIcon(cl.getResource("images/BackLoop16.gif"));
        prevLineImage = new ImageIcon(cl.getResource("images/PrevLine16.gif"));
        nextLineImage = new ImageIcon(cl.getResource("images/NextLine16.gif"));

        setTitle("Omniscient Debugger " + version + " - " + programName);

        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        // topPanel.setSize(500, 800);
        getContentPane().add(topPanel);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        createFileMenu(menuBar);
        createRunMenu(menuBar);
        if (!SCREEN_SHOT)
            createCodeMenu(menuBar);
        createTraceMenu(menuBar);
        createFilterMenu(menuBar);
        if (!SCREEN_SHOT)
            createObjectsMenu(menuBar);
        if (!SCREEN_SHOT)
            createDebugMenu(menuBar);
        if (!SCREEN_SHOT)
            createHelpMenu(menuBar);

        JButton b3, b4, b5, b6, b7;

        menuBar.add(b3 = new JButton("Previous"));
        b3.setToolTipText("Revert to the previously selected time");
        menuBar.add(b4 = new JButton(firstImage));
        b4.setToolTipText("First timestamp (any thread)");
        menuBar.add(b5 = new JButton(backImage));
        b5.setToolTipText("Previous timestamp (any thread)");
        menuBar.add(b6 = new JButton(forwardImage));
        b6.setToolTipText("Next timestamp (any thread)");
        menuBar.add(b7 = new JButton(lastImage));
        b7.setToolTipText("Last timestamp (any thread)");
        ActionListener listener = new DebuggerActionListener(b3, b4, b5, b6, b7);
        b3.addActionListener(listener);
        b4.addActionListener(listener);
        b5.addActionListener(listener);
        b6.addActionListener(listener);
        b7.addActionListener(listener);

        menuBar.add(TSLabel = new JLabel("Time Stamp: "));

        // Create spliter panes for rows
        JSplitPane westPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane westPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane westPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // JSplitPane eastPane1 = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        // JSplitPane eastPane2 = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        // JSplitPane eastPane3 = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        JSplitPane centerPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane centerPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        ;
        // eastPane1.setTopComponent(eastPane2);
        // eastPane1.setBottomComponent(eastPane3);
        centerPane1.setBottomComponent(centerPane2);

        // Create a splitter pane for the three columns
        JSplitPane splitPaneH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane splitPaneH1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topPanel.add(splitPaneH, BorderLayout.CENTER);

        // miniBuffer at bottom
        JScrollPane sp = createTA("");
        miniBuffer = (JTextArea) sp.getViewport().getComponent(0);
        MiniBuffer.initialize(miniBuffer);
        // sp.setBackground( Color.lightGray );
        topPanel.add(sp, BorderLayout.SOUTH);
        splitPaneH1.setLeftComponent(westPane1);
        splitPaneH1.setRightComponent(centerPane1);
        splitPaneH.setLeftComponent(splitPaneH1);
        // splitPaneH.setRightComponent(eastPane1 );

        // // centerPane1.setMinimumSize(new Dimension(500, 500));
        // // splitPaneH1.setMinimumSize(new Dimension(750, 700));

        // createTimeSlider();
        // topPanel.add( timeSlider, BorderLayout.NORTH );

        // Set up west (left) column
        JPanel threadsPanel = createThreadsPanel();
        JPanel stackPanel = createStackPanel();
        JPanel localsPanel = createLocalsPanel();
        JPanel thisPanel = createThisPanel();

        westPane1.setTopComponent(threadsPanel);
        westPane1.setBottomComponent(westPane2);
        westPane2.setTopComponent(stackPanel);
        westPane2.setBottomComponent(westPane3);
        westPane3.setTopComponent(localsPanel);
        westPane3.setBottomComponent(thisPanel);

        // Set up middle column
        JPanel codePanel = createCodePanel();
        centerPane2.setTopComponent(codePanel);
        JPanel tracePanel = createTracePanel();
        centerPane1.setTopComponent(tracePanel);
        JPanel TTYPanel = createTTYPanel();
        centerPane2.setBottomComponent(TTYPanel);

        // Set up east (right) column
        JPanel variablePanel = createObjectPanel();
        splitPaneH.setRightComponent(variablePanel);
        // eastPane1.setTopComponent( variablePanel );
        // JPanel locksPanel = createLocksPanel();
        // eastPane1.setBottomComponent( locksPanel );
        // JPanel excPanel = createExcPanel();
        // eastPane3.setTopComponent( excPanel);
        // JPanel IOPanel = createIOPanel();
        // eastPane3.setBottomComponent( IOPanel);
    }

    static TimeStamp previousTime = null;

    public static void revertPrevious() {
        if (previousTime == null)
            return;
        revert(previousTime);
    }

    public static void revert() {
        revert(TimeStamp.currentTime());
    }

    public static void revert(int time) {
        revert(TimeStamp.lookup(time));
    }

    public static void revert(TimeStamp ts) {
        revert(ts, true);
    }

    public static void revert(TimeStamp ts, boolean updateMessage) {
        if (TimeStamp.empty()) {
            message("No Time Stamps Collected?!", true);
            return;
        }
        if (ts.time < 0) {
            Debugger
                    .message(
                            "Inconsistant/Non-Existant TimeStamps. (An unusual Start/Stop?)",
                            true);
            return; // Blink/beep?
        }
        if (TimeStamp.empty())
            return;
        if (reverting)
            return; // A recursive call from an updateUI?
        reverting = true;
        SourceLine sl = ts.getSourceLine();
        if (sl == SourceLine.SPECIAL_HIDDEN_FILE_LINE) {
            TimeStamp ts1 = ts.getNextThisThread();
            if (ts1 != null)
                ts = ts1;
        }
        previousTime = TimeStamp.currentTime();
        TraceLine tl = ts.getPreviousBalancedTrace();
        if (tl == null)
            tl = TraceLine.defaultTraceLine();
        long clock = Clock.findTime(ts.time)
                - Clock.findTime(previousTime.time);
        if (updateMessage)
            Debugger.message("From last: " + (ts.time - previousTime.time)
                    + " stamps, " + Clock.formatTime(clock) + "secs      "
                    + ts.messageString(), false);
        TimeStamp.setCurrentTime(ts);
        updateTracePane(ts);
        updateStackPanel(tl);
        updateLocalsPanel(tl);
        updateObjectsPane();
        updateCodePanel();
        updateThreadPanel(ts);
        updateTTYPanel(ts);
        updateThisPanel(tl.thisObj);
        updateTSLabel(ts);
        // if (timeSlider.getValue() != ts.time) {timeSlider.setValue(ts.time);
        // timeSlider.updateUI();}
        reverting = false;
    }

    public static void updateTSLabel(TimeStamp ts) {
        TSLabel.setText(getString(ts.time) + Clock.getString(ts.time)
                + SourceLine.getString(ts));
    }

    public static String getString(int time) { // time stamp time!
        String s = " Event " + formatTime(time) + " [" + TimeStamp.eott() + "]";
        return s;
    }

    private static String formatTime(int time) {
        int nChars = ("" + TimeStamp.eott()).length();
        String s = "" + time;
        while (s.length() < nChars)
            s = " " + s;
        return s;
    }

    public static void updateObjectsPane() {
        ObjectPane.update();
    }

    public static void updateTTYPanel(TimeStamp ts) {
        if (TTYPane.singleton().getSize() == 0)
            return;
        TTYPList.updateUI();
        int index = TTYPane.getClosest(ts);
        TTYPList.setSelectedIndex(index);
        if (index < 2)
            TTYPList.ensureIndexIsVisible(0);
        else
            TTYPList.ensureIndexIsVisible(index - 2);
        if (index + 2 > TTYPane.singleton().getSize())
            TTYPList.ensureIndexIsVisible(TTYPane.singleton().getSize() - 1);
        else
            TTYPList.ensureIndexIsVisible(index + 2);
        TTYPList.ensureIndexIsVisible(index);
    }

    public static void updateThreadPanel(TimeStamp ts) {
        if (ThreadPane.singleton().getSize() == 0)
            return;

        ThreadPane tp = (ThreadPane) ThreadPList.getSelectedValue();

        if ((tp == null) || (tp.tid != ts.getThread())) { // Only null once?
            // Best default??
            int index = ThreadPane.find(ts.getThread());
            ThreadPList.setSelectedIndex(index);
            ThreadPList.ensureIndexIsVisible(index);
        }
        ThreadPList.updateUI();
    }

    public static void updateTracePane(TimeStamp ts) {
        MethodLine ml = ts.getNearestTraceThisThread();

        if (TimeStamp.getType(ts.time) == TimeStamp.LAST) {
            // RM should display the same as following RL.
            TraceLine tl = ts.getPreviousBalancedTrace();
            if ((tl != null) && (tl.returnLine != null)
                    && (tl.returnLine.filteredIndex != -1))
                ml = tl.returnLine;
        }

        // if ((ml instanceof ReturnLine) && (ml.filteredIndex == -1)) ml =
        // ((ReturnLine) ml).traceLine; NEVER HAPPEN
        if (ml == null) {
            ml = TraceLine.defaultTraceLine();
        } // Just don't crash.
        // D.println("TraceLineB: "+ml.toString(0));
        if (previousTime.getThread() != ts.getThread()) {
            // D.println("Changing from "+ TimeStamp.previousTime().getThread()
            // +" to "+ ts.getThread());
            TracePList.updateUI();
        }
        if (firstTimeTracePane) {
            firstTimeTracePane = false;
            TracePList.updateUI();
            // Where should I do this? When changing threads (above) and
            // initializing (somewhere)
        }

        if (TracePList.getSelectedIndex() != ml.filteredIndex) {
            TracePList.setSelectedIndex(ml.filteredIndex);
            TracePList.ensureIndexIsVisible(ml.filteredIndex - 2);
            TracePList.ensureIndexIsVisible(ml.filteredIndex + 2);
            TracePList.ensureIndexIsVisible(ml.filteredIndex);
        }
    }

    public JPanel createP(String s) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(s);
        JPanel panel1 = new JPanel();
        // panel.add(l);
        panel1.add(l);
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        panel.add(panel1);
        // panel.setBackground( Color.lightGray );
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        return panel;
    }

    public JPanel createP2(String s) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        JLabel l = new JLabel(s);
        panel1.add(l);
        panel1.add(panel2);
        panel.add(panel1);

        // panel.setBackground( Color.lightGray );
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        return panel;
    }

    public JPanel createP1(String s) {
        JLabel l = new JLabel(s);
        JPanel panel = new JPanel();
        panel.add(l);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // panel.setBackground( Color.lightGray );
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        return panel;
    }

    public JScrollPane createTA(String s) {
        JTextArea ta = new JTextArea(s);
        ta.setFont(new Font(FONT, Font.PLAIN, FONT_SIZE));
        ta.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane sp = new JScrollPane(ta);
        // sp.getViewport().add(ta);
        return sp;
    }

    // REMOVE THIS WHEN CODEPANE is ready
    public JScrollPane createJL(VectorD listData, ListSelectionListener sl) {
        return (createJL(listData, sl, false));
    }

    public JScrollPane createJL(VectorD listData, ListSelectionListener sl,
            boolean doubleClick) {
        JList list;
        if (doubleClick)
            list = new DoubleClickJList(listData);
        else
            list = new JList(listData);

        list.setFont(new Font(FONT, Font.PLAIN, FONT_SIZE));
        list.addListSelectionListener(sl);
        list.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane sp = new JScrollPane(list);
        // sp.getViewport().add(list);
        return sp;
    }

    public JScrollPane createJL(AbstractListModel listData,
            ListSelectionListener sl) {
        return (createJL(listData, sl, false));
    }

    public JScrollPane createJL(AbstractListModel listData,
            ListSelectionListener sl, boolean doubleClick) {
        JList list;
        if (doubleClick)
            list = new DoubleClickJList(listData);
        else
            list = new JList(listData);

        list.setFont(new Font(FONT, Font.PLAIN, FONT_SIZE));
        list.addListSelectionListener(sl);
        list.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane sp = new JScrollPane(list);
        // sp.getViewport().add(list);
        return sp;
    }

    // Create special panels

    public JPanel createThreadsPanel() {
        JButton b0, b1, b2, b3;
        JPanel panel = createP("Threads ");
        JPanel panel1 = (JPanel) panel.getComponent(0);

        JScrollPane sp = createJL(ThreadPane.singleton(), new ThreadListener(),
                true);
        ThreadPList = (JList) sp.getViewport().getComponent(0);

        // panel.add(new Label("Threads"));

        panel1.add(b0 = new JButton(firstImage));
        b0.setToolTipText("First timestamp this thread");
        panel1.add(b1 = new JButton(backImage));
        b1.setToolTipText("Previous context switch");
        panel1.add(b2 = new JButton(forwardImage));
        b2.setToolTipText("Next context switch");
        panel1.add(b3 = new JButton(lastImage));
        b3.setToolTipText("Last timestamp this thread");
        ActionListener listener = new ThreadActionListener(b0, b1, b2, b3);
        b0.addActionListener(listener);
        b1.addActionListener(listener);
        b2.addActionListener(listener);
        b3.addActionListener(listener);
        panel.add(sp);
        if (VGA) {
            panel.setMinimumSize(new Dimension(80, 0));
            // panel.setPreferredSize(new Dimension(150, 100));
            panel.setMaximumSize(new Dimension(200, 300));
        } else {
            panel.setMinimumSize(new Dimension(200, 150));
            // panel.setPreferredSize(new Dimension(250, 250));
            panel.setMaximumSize(new Dimension(400, 400));
        }

        // ThreadPane.initialize();
        return panel;
    }

    public JPanel createStackPanel() {
        JPanel panel = createP1("Stack");
        JScrollPane sp = createJL(new StackList(), new StackListener(), true);
        StackPList = (JList) sp.getViewport().getComponent(0);
        panel.add(sp);
        if (VGA) {
            panel.setMinimumSize(new Dimension(80, 0));
            // panel.setPreferredSize(new Dimension(150, 80));
            panel.setMaximumSize(new Dimension(400, 200));
        } else {
            panel.setMinimumSize(new Dimension(200, 100));
            // panel.setPreferredSize(new Dimension(250, 250));
            panel.setMaximumSize(new Dimension(400, 300));
        }
        return panel;
    }

    public JPanel createLocalsPanel() {
        JButton b0, b1, b2, b3, b4;
        JPanel panel = createP("Locals ");
        JPanel panel1 = (JPanel) panel.getComponent(0);
        panel1.add(b0 = new JButton(firstImage));
        b0.setToolTipText("First value this variable");
        panel1.add(b1 = new JButton(backImage));
        b1.setToolTipText("Previous value this variable");
        panel1.add(b2 = new JButton(forwardImage));
        b2.setToolTipText("Next value this variable");
        panel1.add(b3 = new JButton(lastImage));
        b3.setToolTipText("Last value this variable");
        b4 = new JButton("X"); // KILL THIS LATER

        ActionListener listener = new LocalsActionListener(b0, b1, b2, b3, b4);
        b0.addActionListener(listener);
        b1.addActionListener(listener);
        b2.addActionListener(listener);
        b3.addActionListener(listener);
        b4.addActionListener(listener);

        JScrollPane sp = createJL(LocalsPane.singleton(),
                new LocalsActionListener(), true);
        panel.add(sp);
        LocalsPList = (JList) sp.getViewport().getComponent(0);
        if (VGA) {
            panel.setMinimumSize(new Dimension(80, 0));
            // panel.setPreferredSize(new Dimension(150, 120));
            panel.setMaximumSize(new Dimension(400, 300));
        } else {
            panel.setMinimumSize(new Dimension(200, 100));
            // panel.setPreferredSize(new Dimension(250, 250));
            panel.setMaximumSize(new Dimension(400, 300));
        }
        return panel;
    }

    public JPanel createThisPanel() {

        JButton b0, b1, b2, b3, b4;
        JPanel panel = createP1("this");
        JScrollPane sp = createJL(ThisPane.singleton(),
                new ThisActionListener(), true);
        panel.add(sp);
        ThisPList = (JList) sp.getViewport().getComponent(0);
        if (VGA) {
            panel.setMinimumSize(new Dimension(80, 0));
            // panel.setPreferredSize(new Dimension(150, 80));
            panel.setMaximumSize(new Dimension(400, 200));
        } else {
            panel.setMinimumSize(new Dimension(200, 100));
            // panel.setPreferredSize(new Dimension(250, 250));
            panel.setMaximumSize(new Dimension(400, 200));
        }
        return panel;
    }

    public static void updateStackPanel(TraceLine tl) {
        StackList sl = tl.generateStackList();
        StackList.setCurrentStackList(sl);
        StackPList.setModel(sl);
        StackPList.updateUI();
    }

    public static void updateCodePanel() {
        updateCodePanel(TimeStamp.currentTime());
    }

    public static void updateCodePanel(TimeStamp ts) {
        SourceLine sl = ts.getSourceLine();
        if (sl == SourceLine.SPECIAL_HIDDEN_FILE_LINE) {
            // System.out.println("Hidden line at: "+ts);
            TimeStamp ts1 = ts.getNextThisThread();
            if (ts1 != null)
                sl = ts1.getSourceLine();
        }
        int line = sl.line;
        int end = 0, start = 0;
        String fileName = sl.getFile();
        VectorD codeList = CodePane.getDisplayList(sl);
        // if (codeList.size() == 0) return; // For files not found, don't move.
        if (codePanelCurrentFile != fileName) {
            codeJList.setListData(codeList);
            codePanelCurrentFile = fileName;
            codeJList.updateUI();
        }
        if (line < 1)
            return;
        codeJList.setSelectedIndex(line - 1);
        int min = Math.max(0, line - 6);
        int max = Math.min(codeList.size() - 1, line + 6);
        codeJList.ensureIndexIsVisible(max);
        codeJList.ensureIndexIsVisible(min);
        codeJList.ensureIndexIsVisible(line - 1);
    }

    private static Object previousThis;

    public static void updateTimeSlider() {
        if (TimeStamp.empty())
            timeSlider.setMaximum(0);
        else
            timeSlider.setMaximum(TimeStamp.eott());

        timeSlider.updateUI();
    }

    public static void updateThisPanel(Object o) {
        int selectedLine = -1;

        if (o == previousThis) {
            ThisPList.updateUI();
            return; // SOMETHING LIKE THIS. BUT MUST UPDATE THE VALUES OF IVs.?
        }

        previousThis = o;

        ThisPane.singleton().displayList = new VectorD(); // KINDA TACKY.
        if (o != null)
            ThisPane.add(o);
        ThisPList.setModel(ThisPane.singleton());
        ThisPList.updateUI();
        if (selectedLine > -1)
            ThisPList.setSelectedIndex(selectedLine);
    }

    static Locals previousLocals = null;

    public static void updateLocalsPanel(TraceLine tl) {
        int selectedLine = -1;

        Locals locals = tl.locals;
        if (locals == null) {
            locals = Locals.DEFAULT;
        } // null is fine. Easier to make empty Locals than patch in null.
        if (previousLocals == locals) {
            selectedLine = LocalsPList.getSelectedIndex();
        } else {
            /*
             * VectorD v = new VectorD(); LocalsPane.singleton().displayList =
             * v; // Change soon! int len = locals.locals.length; for (int i=0;
             * i < len; i++) v.add(locals.locals[i]);
             */
            LocalsPane.singleton().displayList = locals.createShadowLocals();
            previousLocals = locals;
            LocalsPList.setModel(LocalsPane.singleton());
        }
        LocalsPList.updateUI();
        if (selectedLine > -1)
            LocalsPList.setSelectedIndex(selectedLine);
        // LocalsPane.printAll();
    }

    public JPanel createObjectPanel() {
        JButton b0, b1, b2, b3, b4, b5, b6;
        JPanel panel = createP2("Objects ");
        JPanel panel2 = (JPanel) panel.getComponent(0);
        JPanel panel1 = (JPanel) panel2.getComponent(1);
        panel1.add(b0 = new JButton(firstImage));
        b0.setToolTipText("First value this variable");
        panel1.add(b1 = new JButton(backImage));
        b1.setToolTipText("Previous value this variable");
        panel1.add(b2 = new JButton(forwardImage));
        b2.setToolTipText("Next value this variable");
        panel1.add(b3 = new JButton(lastImage));
        b3.setToolTipText("Last value this variable");
        ActionListener listener = new ObjectActionListener(b0, b1, b2, b3);
        b0.addActionListener(listener);
        b1.addActionListener(listener);
        b2.addActionListener(listener);
        b3.addActionListener(listener);

        JScrollPane sp = createJL(new ObjectPane(), new ObjectListener(), true);
        panel.add(sp);
        ObjectsPList = (JList) sp.getViewport().getComponent(0);
        if (VGA) {
            panel.setMinimumSize(new Dimension(80, 150));
            // panel.setPreferredSize(new Dimension(160, 250));
        } else {
            panel.setMinimumSize(new Dimension(100, 250));
            // panel.setPreferredSize(new Dimension(250, 250));
        }
        return panel;
    }

    public JPanel createTracePanel() {
        JButton b3, b4, b5, b6, b7;
        JPanel panel = createP("Method Traces ");
        JPanel panel1 = (JPanel) panel.getComponent(0);

        panel1.add(b4 = new JButton(firstImage));
        b4.setToolTipText("First call of this method");
        panel1.add(b5 = new JButton(backImage));
        b5.setToolTipText("Previous call of this method");
        panel1.add(b6 = new JButton(forwardImage));
        b6.setToolTipText("Next call of this method");
        panel1.add(b7 = new JButton(lastImage));
        b7.setToolTipText("Last call of this method");
        ActionListener listener = new TraceActionListener(b4, b5, b6, b7);
        b4.addActionListener(listener);
        b5.addActionListener(listener);
        b6.addActionListener(listener);
        b7.addActionListener(listener);

        JScrollPane sp = createJL(TraceLine.SINGLETON, new TraceListener(),
                true);
        TracePList = (JList) sp.getViewport().getComponent(0);
        if (VGA) {
            panel.setMinimumSize(new Dimension(200, 100));
            panel.setPreferredSize(new Dimension(350, 300));
            panel.setMaximumSize(new Dimension(800, 600));
        } else {
            panel.setMinimumSize(new Dimension(450, 150));
            // panel.setPreferredSize(new Dimension(550, 400));
            panel.setMaximumSize(new Dimension(500, 600));
        }
        panel.add(sp);
        return panel;
    }

    public JPanel createCodePanel() {
        JButton b0, b1, b2, b3, b4, b5, b6, b7, b8, b9;
        JPanel panel = createP("Code ");
        JPanel panel1 = (JPanel) panel.getComponent(0);
        panel1.add(b0 = new JButton(firstImage));
        b0.setToolTipText("First timestamp this method");
        panel1.add(b1 = new JButton(prevLineImage));
        b1.setToolTipText("Previous line this method (step over)");
        panel1.add(b2 = new JButton(backImage));
        b2.setToolTipText("Previous line any method (step in/out)");
        panel1.add(b3 = new JButton(forwardImage));
        b3.setToolTipText("Next line any method (step in/out)");
        panel1.add(b4 = new JButton(nextLineImage));
        b4.setToolTipText("Next line this method (step over)");
        panel1.add(b5 = new JButton(lastImage));
        b5.setToolTipText("Last timestamp this method");
        panel1.add(b6 = new JButton(loopImage));
        b6.setToolTipText("Next timestamp on this line, in this method");
        panel1.add(b7 = new JButton(backLoopImage));
        b7.setToolTipText("Previous timestamp on this line, in this method");
        panel1.add(b8 = new JButton(upImage));
        b8.setToolTipText("Go back to caller of this method");
        panel1.add(b9 = new JButton(downImage));
        b9.setToolTipText("Return from this method");
        ActionListener listener = new CodeActionListener(b0, b1, b2, b3, b4,
                b5, b6, b7, b8, b9);
        b0.addActionListener(listener);
        b1.addActionListener(listener);
        b2.addActionListener(listener);
        b3.addActionListener(listener);
        b4.addActionListener(listener);
        b5.addActionListener(listener);
        b6.addActionListener(listener);
        b7.addActionListener(listener);
        b8.addActionListener(listener);
        b9.addActionListener(listener);

        JScrollPane sp = createJL(new VectorD(), new CodeListener(), true);
        codeJList = (JList) sp.getViewport().getComponent(0);
        if (VGA) {
            panel.setMinimumSize(new Dimension(200, 100));
            // panel.setPreferredSize(new Dimension(300, 200));
            panel.setMaximumSize(new Dimension(800, 600));
        } else {
            panel.setMinimumSize(new Dimension(400, 150));
            // panel.setPreferredSize(new Dimension(550, 400));
            panel.setMaximumSize(new Dimension(500, 600));
        }
        panel.add(sp);
        return panel;
    }

    public JPanel createTTYPanel() {
        JButton b0, b1, b2, b3;

        JPanel panel = createP("TTY Output ");

        JPanel panel1 = (JPanel) panel.getComponent(0);
        panel1.add(b0 = new JButton(firstImage));
        b0.setToolTipText("First timestamp this PrintStream");
        panel1.add(b1 = new JButton(backImage));
        b1.setToolTipText("Previous timestamp this PrintStream");
        panel1.add(b2 = new JButton(forwardImage));
        b2.setToolTipText("Next timestamp this PrintStream");
        panel1.add(b3 = new JButton(lastImage));
        b3.setToolTipText("Last timestamp this PrintStream");
        ActionListener listener = new TTYActionListener(b0, b1, b2, b3);
        b0.addActionListener(listener);
        b1.addActionListener(listener);
        b2.addActionListener(listener);
        b3.addActionListener(listener);

        JScrollPane sp = createJL(TTYPane.singleton(), new TTYListener());
        TTYPList = (JList) sp.getViewport().getComponent(0);
        panel.add(sp);
        if (VGA) {
            panel.setMinimumSize(new Dimension(200, 0));
            // panel.setPreferredSize(new Dimension(300, 100));
            panel.setMaximumSize(new Dimension(500, 400));
        } else {
            panel.setMinimumSize(new Dimension(400, 0));
            // panel.setPreferredSize(new Dimension(550, 200));
            panel.setMaximumSize(new Dimension(500, 400));
        }
        TTYPane.initialize();
        return panel;
    }

    public void makeTracePopup(JComponent panel) {
        JMenuItem copyThis = new JMenuItem("Copy 'this' to Object Pane");
        JMenuItem copyArg1 = new JMenuItem("Copy 'arg 1' to Object Pane");
        JMenuItem copyArg2 = new JMenuItem("Copy 'arg 2' to Object Pane");
        JMenuItem copyArg3 = new JMenuItem("Copy 'arg 3' to Object Pane");
        traceMenu = new JPopupMenu("Trace Menu");
        traceMenu.add(copyThis);
        traceMenu.add(copyArg1);
        traceMenu.add(copyArg2);
        traceMenu.add(copyArg3);
        panel.add(traceMenu);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        ActionListener listener = new TraceActionListener();
        copyThis.addActionListener(listener);
        copyArg1.addActionListener(listener);
        copyArg2.addActionListener(listener);
        copyArg3.addActionListener(listener);
    }

    // ****************************** Main program
    // ******************************
    public static void runLSD(String args[], boolean noWindows) {
        NO_WINDOWS = noWindows;
        SHOW = !noWindows;
        main(args);
    }

    public static void main(String args[]) {
        final String[] args2 = args;
        main2(args);
    }

    public static void main2(String args[]) {
        // Create an instance of the test application
        String[] args1 = new String[0];

        println(version);

        if (DEBUGIFY_ONLY) { // Must be cmd line: % debugify *class
            Debugify.main(args);
            System.exit(0);
        }
        CMD_LINE = (args.length > 0);
        if (START)
            START = CMD_LINE;
        readCommandLineFlags();
        firstRun = Defaults.readDefaults();
        readCommandLineFlags();
        TimeStamp.initialize();

        if (CMD_LINE) { // if on command line w/a class name to run.
            programName = args[0];
            args1 = new String[args.length - 1];
            int len = args.length - 1;
            for (int i = 0; i < len; i++)
                args1[i] = args[i + 1];
            if (INSTRUMENT && !USE_BOOTCLASSLOADER) {
                classLoader = new DebugifyingClassLoader();
                Thread.currentThread().setContextClassLoader(classLoader);
            } else
                classLoader = Thread.currentThread().getContextClassLoader();
            try {
                clazz = classLoader.loadClass(programName);
            } catch (ClassNotFoundException ex) {
                System.err.println("Class not found: " + programName);
                System.exit(1);
            }
            if (firstRun) {
                // Package p = clazz.getPackage(); MAY RETURN NULL?!
                String name = clazz.getName();
                int dot = name.lastIndexOf('.');
                String packageName = "";
                if (dot == -1) {
                } else {
                    packageName = name.substring(0, dot + 1);
                }
                // System.out.println(""+packageName);
                //Defaults.addIOP(packageName);
            }
            if (!NO_WINDOWS)
                StopButton.create(START, PAUSED, SHOW, INSTRUMENT);

            argList = new Object[1];
            argList[0] = args1;

            final Object[] argList2 = argList;
            final Class clazz2 = clazz;
            if (START) {
                // new Thread(new Runnable() {public void run() {runMain(clazz2,
                // argList2);}}, "Main").start();
                runMain(clazz2, argList2);
            }
        } else
            Launch.create();

    } // Otherwise only create it when the user pushes STOP (StopButton.java)

    private static void runMain(Class clazz, Object[] argList) {
        runTarget(clazz, argList);
        if (SHOW)
            stopTarget(); // create debugger when main thread returns
        if (TEST)
            SwingUtilities.invokeLater(new DebuggerCommand());
    }

    public static synchronized void createDebugger() {
        if (mainFrame != null)
            return;
        // Somebody already created it. (Early STOP button is one way)
        mainFrame = new Debugger();
        mainFrame.initialize();
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ThreadPane.initialize();
        previousTime = TimeStamp.bot1();
        TimeStamp.setCurrentTime(previousTime);
        revert(TimeStamp.bot1());
        updateUIs();
        if (firstRun)
            Defaults.writeDefaults();

    }

    static public Object runAlternate(Object obj, Method method, Object[] args) {
        Object returnValue = null;
        startTime = new Date().getTime();

        Shadow.updateAll();
        if (mainTimeLine)
            Debugger.switchTimeLines(true);
        else
            clearTimeLine();
        D.DISABLE = false; // Start collecting
        D.resumeProgram();

        Clock.start();
        try {
            returnValue = method.invoke(obj, args);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof DebuggerExit) {
                System.out.println("In target program: \n" + clazz + "."
                        + method.getName() + " System.exit() called\n");
                D.PAUSE_PROGRAM = true;
                SHOW = true;
                D.DISABLE = true;
                // The program should be dead, so always show.
            } else {
                System.out.println("In target program1: \n" + clazz + "."
                        + method.getName() + " threw " + t);
                TraceLine tl = TraceLine.getFirstTraceline();
                Throwable t1;
                if (t instanceof Exception)
                    t1 = t;
                else
                    t1 = new DebuggerException("" + t);
                if (tl != null)
                    D.catchEx(tl.getSourceLine().getIndex(), t1, tl);
                t.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("IMPOSSIBLE? In target program: \n" + clazz
                    + "." + method.getName() + " threw " + e);
            e.printStackTrace();
        }

        if (method.getReturnType() == null)
            returnValue = ShadowVoid.VOID;
        if (returnValue == null)
            returnValue = ShadowNull.NULL;

        if (TimeStamp.eott() < 2) {
            if (!PAUSED)
                Debugger
                        .println("collected no data on this run.  Is target debugified?");
            return returnValue;
        }

        endTime = new Date().getTime();
        totalTime = endTime - startTime;
        stopTarget(); // HACK! Should be like runTarget()
        return returnValue;
    }

    static void runTarget(Class c, Object[] a) {
        clazz = c;
        argList = a; // Globals. rename.
        startTime = new Date().getTime();
        Method method = null;

        // System.out.println("Starting " +clazz);
        try {
            method = clazz.getDeclaredMethod("main",
                    new Class[] { String[].class });
        } catch (Exception e) {
            System.out.println("There is no main(String[] argv) in " + clazz
                    + ".\n" + e);
            System.exit(1);
        }

        Clock.start();
        try {
            method.invoke(null, argList);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof DebuggerExit) {
                System.out.println("In target program: \n" + clazz + "."
                        + method.getName() + " System.exit() called\n");
                D.PAUSE_PROGRAM = true;
                SHOW = true;
                D.DISABLE = true;
                // The program should be dead, so always show.

            } else {
                System.out.println("In target program2: \n" + clazz + "."
                        + method.getName() + " threw " + t);
                TraceLine tl = TraceLine.getFirstTraceline();
                Throwable t1;
                if (t instanceof Exception)
                    t1 = t;
                else
                    t1 = new DebuggerException("" + t);
                if (tl != null)
                    D.catchEx(tl.getSourceLine().getIndex(), t1, tl);
                t.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            System.out.println("main() not static in target program?: \n"
                    + clazz + "." + method.getName() + " threw " + e);
        } catch (Exception e) {
            System.out.println("IMPOSSIBLE? In target program: \n" + clazz
                    + "." + method.getName() + " threw " + e);
            e.printStackTrace();
        }

        if (TimeStamp.eott() < 2) {
            println("Collected no data on this run. Is target debugified?");
        }

        endTime = new Date().getTime();
        totalTime = endTime - startTime;
        if (SHOW)
            printStatistics();
    }

    static void restartTarget() { // Only called from event thread.
        D.killTarget();
        if (D.getCheckPattern())
            D.DISABLE = true;
        else
            D.DISABLE = false;
        startTarget(clazz);
    }

    static void startTarget(Class clazz) { // Only called from event thread.
        final Class clazzz = clazz;
        final Object[] argListt = argList;

        (new Thread(new Runnable() {
            public void run() {
                runTarget(clazzz, argListt);
                stopTarget();
            }
        }, "main")).start();
    }

    static void stopTarget() {
        D.PAUSE_PROGRAM = PAUSE_ON_STOP;
        D.KILL_TARGET = KILL_TARGET_ON_STOP;
        D.DISABLE = true;
        if (NO_WINDOWS)
            return;
        Runnable r = new Runnable() {
            public void run() {
                createDebugger();
            }
        };
        SwingUtilities.invokeLater(r);
    }

    public static void printStatistics() {
        if (DEBUG_DEBUGGER)
            printAllStatistics();
        else
            printSomeStatistics();
    }

    public static void printSomeStatistics() {
        Runtime run = Runtime.getRuntime();
        println(version + " run complete.");
        System.gc();
        System.out.println(" Realtime: " + totalTime / 1000.0 + " secs "
                + " TimeStamps Created: " + TimeStamp.nTSCreated + " ("
                + (TimeStamp.nTSCreated - TimeStamp.index) + " GC'd, leaving "
                + TimeStamp.index + ") ");
        System.out.println(" Memory: " + run.freeMemory() / 1000000
                + "MB free / " + run.totalMemory() / 1000000 + "MB max");
    }

    public static void printAllStatistics() {
        Runtime run = Runtime.getRuntime();
        println(version + " run complete.");
        System.gc();
        System.out.println(" Realtime: " + totalTime / 1000.0 + " secs "
                + " TimeStamps Created: " + TimeStamp.nTSCreated + " ("
                + (TimeStamp.nTSCreated - TimeStamp.index) + " GC'd, leaving "
                + TimeStamp.index + ") ");
        System.out.println(" Memory: " + run.freeMemory() / 1000000
                + "MB free / " + run.totalMemory() / 1000000 + "MB max");
        TraceLine.countInstrumentedMethods();
        // ShadowLocals: "+ ShadowLocal.NCreated+"
        System.out.println(" HistoryList Entries: " + HistoryList.nEntries
                + "\n SourceLines: " + SourceLine.nEntries + " TraceLines: "
                + TraceLine.nTraceLines + " of which Instrumented: "
                + TraceLine.nInstrumented + "\n HistoryListSingleton created: "
                + HistoryListSingleton.getNCreated()
                + " HistoryListSingleton upgraded: "
                + HistoryListSingleton.getNUpgraded() + " Lookups: "
                + TimeStamp.lookupSize() + "\n Shadows: " + Shadow.tableSize()
                + " Shadow entries " + Shadow.nEntries()
                + "\n Context Switches: " + TimeStamp.nContextSwitches()
                + "\n Time Debugifying Classes: " + timeDebugifying / 1000.0
                + " secs");
        HistoryList.printStatistics();
        Locals.printStatistics();
        Shadow.printStatistics();
        TraceLine.printStatistics();
    }

    public static String getMessage() {
        return (miniBuffer.getText());
    }

    public static void message(String msg, boolean beep) {
        MiniBuffer.message(msg, beep);
    }

    static ImageIcon upImage;
    static ImageIcon downImage;
    static ImageIcon firstImage;
    static ImageIcon backImage;
    static ImageIcon forwardImage;
    static ImageIcon lastImage;
    static ImageIcon loopImage;
    static ImageIcon backLoopImage;
    static ImageIcon prevLineImage;
    static ImageIcon nextLineImage;

    public static JList StackPList, LocalsPList, TracePList, ObjectsPList,
            ThisPList, ThreadPList, codeJList, TTYPList;
    public static JLabel TSLabel = null;
    public static JPopupMenu traceMenu;
    public static JPanel topPanel;
    public static boolean reverting = false;
    public static JTextArea miniBuffer;
    public static String codePanelCurrentFile = null;
    public static Thread CURRENT_THREAD = null;
    public static JSlider timeSlider;

    public static void setCurrentThread(Thread t) {
        CURRENT_THREAD = t;
    }

    public static Thread currentThread() {
        if ((TimeStamp.empty()) || (CURRENT_THREAD == null))
            CURRENT_THREAD = Thread.currentThread();
        return (CURRENT_THREAD);
    }

    public static JCheckBoxMenuItem firstLine; // , threadNextCommand;

    private void createFilterMenu(JMenuBar panel) {
        JMenu menu = new JMenu("Filter");
        FilterMenuActionListener listener = new FilterMenuActionListener();
        JMenuItem filterMethodC = createJMenuItemAlt(menu,
                "Filter out method in class", KeyEvent.VK_F, null, listener);
        JMenuItem filterMethod = createJMenuItemAlt(menu, "Filter out method",
                KeyEvent.VK_M, null, listener);
        JMenuItem filterMethodI = createJMenuItemAlt(menu,
                "Filter out method internals", 0, null, listener);
        JMenuItem unfilter = createJMenuItemAlt(menu, "Unfilter",
                KeyEvent.VK_U, null, listener);
        JMenuItem filterClass = createJMenuItemAlt(menu,
                "Filter out all methods in class", 0, null, listener);
        JMenuItem save = createJMenuItemAlt(menu, "Save filters to files", 0,
                null, listener);
        JMenuItem filterIn = createJMenuItemAlt(menu, "Filter in method",
                KeyEvent.VK_I, null, listener);
        JMenuItem filter1 = createJMenuItemAlt(menu, "Filter out depth>1", 0,
                null, listener);
        JMenuItem filter2 = createJMenuItemAlt(menu, "Filter out depth>2", 0,
                null, listener);
        JMenuItem filter3 = createJMenuItemAlt(menu, "Filter out depth>3", 0,
                null, listener);
        JMenuItem filter4 = createJMenuItemAlt(menu, "Filter out depth>4", 0,
                null, listener);
        JMenuItem filter5 = createJMenuItemAlt(menu, "Filter out depth>5", 0,
                null, listener);
        JMenuItem filter6 = createJMenuItemAlt(menu, "Filter out depth>6", 0,
                null, listener);
        JMenuItem filter7 = createJMenuItemAlt(menu, "Filter out depth>7", 0,
                null, listener);
        JMenuItem filter8 = createJMenuItemAlt(menu, "Filter out depth>8", 0,
                null, listener);
        JMenuItem filter9 = createJMenuItemAlt(menu, "Filter out depth>9", 0,
                null, listener);
        listener.addButtons(filterMethodC, unfilter, filterClass, save,
                filterIn, filter1, filter2, filter3, filter4, filter5, filter6,
                filter7, filter8, filter9, filterMethod, filterMethodI);
        panel.add(menu);
    }

    private void createTraceMenu(JMenuBar panel) {
        JMenu menu = new JMenu("Trace");
        TraceMenuActionListener listener = new TraceMenuActionListener();
        JMenuItem copy0MenuItem = createJMenuItemAlt(menu, "Copy 'this'", 0,
                null, listener);
        JMenuItem copy1MenuItem = createJMenuItemAlt(menu, "Copy argument 1",
                KeyEvent.VK_1, null, listener);
        JMenuItem copy2MenuItem = createJMenuItemAlt(menu, "Copy argument 2",
                KeyEvent.VK_2, null, listener);
        JMenuItem copy3MenuItem = createJMenuItemAlt(menu, "Copy argument 3",
                KeyEvent.VK_3, null, listener);
        JMenuItem copy4MenuItem = createJMenuItemAlt(menu, "Copy argument 4",
                KeyEvent.VK_4, null, listener);
        JMenuItem copy5MenuItem = createJMenuItemAlt(menu, "Copy argument 5",
                KeyEvent.VK_5, null, listener);
        JMenuItem copyrMenuItem = createJMenuItemAlt(menu, "Copy return value",
                0, null, listener);
        JMenuItem searchMenuItem = createJMenuItemCTRL(menu, "Search",
                KeyEvent.VK_S, "Search Trace Pane for string", listener);
        JMenuItem rsearchMenuItem = createJMenuItemCTRL(menu, "Reverse Search",
                KeyEvent.VK_R, "Reverse search Trace Pane for string", listener);
        JMenuItem esearchMenuItem = createJMenuItem(menu, "End Search",
                KeyEvent.VK_ENTER, "End Search and revert", listener);
        JMenuItem fgetMenuItem = createJMenuItemCTRL(menu, "fget",
                KeyEvent.VK_F, "fget ", listener);
        JMenuItem cdataMenuItem = createJMenuItemCTRL(menu, "cdata",
                KeyEvent.VK_C, "cdata ", listener);
        JMenuItem fgetStartMenuItem = createJMenuItemCTRL(menu,
                "Save start pattern", 0, "Save the fget pattern for start",
                listener);
        JMenuItem fgetStopMenuItem = createJMenuItemCTRL(menu,
                "Save stop pattern", 0, "Save the fget pattern for stop",
                listener);
        JMenuItem countMenuItem = createJMenuItemCTRL(menu,
                "Total no. of matches", KeyEvent.VK_T,
                "Count matches for current fget/cdata pattern", listener);
        JMenuItem fgetSFLMenuItem = createJMenuItemCTRL(menu,
                "Save start on current line", 0,
                "Save the fget pattern to start on this line", listener);
        JMenuItem fgetQueryMenuItem = createJMenuItemCTRL(menu,
                "Create FGET query", 0,
                "Create a query which matches the current traceline", listener);
        JMenuItem fgetSLQueryMenuItem = createJMenuItemCTRL(menu,
                "Create FGET SourceLine query", 0,
                "Create a query which matches the current SourceLine", listener);
        firstLine = new JCheckBoxMenuItem("Go to first line in method");
        firstLine.setState(true);
        menu.add(firstLine);
        /*
         * threadNextCommand = new JCheckBoxMenuItem("Thread previous/next goes
         * to NEXT thread"); threadNextCommand.setState(true);
         * menu.add(threadNextCommand);
         */
        listener.addButtons(copy0MenuItem, copy1MenuItem, copy2MenuItem,
                copy3MenuItem, copy4MenuItem, copy5MenuItem, copyrMenuItem,
                searchMenuItem, rsearchMenuItem, esearchMenuItem,
                cdataMenuItem, fgetMenuItem, fgetStartMenuItem,
                fgetStopMenuItem, countMenuItem, fgetSFLMenuItem,
                fgetQueryMenuItem, fgetSLQueryMenuItem);
        panel.add(menu);
    }

    private void createObjectsMenu(JMenuBar panel) {
        JMenu menu = new JMenu("Objects");
        // menu.setMnemonic('O');
        ObjectsMenuActionListener listener = new ObjectsMenuActionListener();

        JMenuItem copyClassMenuItem = createJMenuItemAlt(menu, "Add Class",
                KeyEvent.VK_K, "Add the Class of the selected object", listener);
        JMenuItem removeMenuItem = createJMenuItemAlt(menu, "Remove",
                KeyEvent.VK_R, "Remove the selected object", listener);
        JMenuItem expandMenuItem = createJMenuItemAlt(menu, "Expand",
                KeyEvent.VK_E, "Expand/Close the selected object", listener);
        JMenuItem selectMenuItem = createJMenuItemAlt(menu, "Select IV Value",
                KeyEvent.VK_S,
                "Select new value from list of values (Objects Pane)", listener);
        JMenuItem localMenuItem = createJMenuItemAlt(menu,
                "Select Local Value", KeyEvent.VK_L,
                "Select new value from list of values (Locals Pane)", listener);
        JMenuItem retainMenuItem = createJMenuItemAlt(menu, "Retain Only",
                KeyEvent.VK_O, "Retain only the selected IV", listener);
        JMenuItem setMenuItem = createJMenuItemAlt(menu, "Assign",
                KeyEvent.VK_A, "Assign new value to IV", listener);
        JMenuItem copyMenuItem = createJMenuItemAlt(menu, "Copy",
                KeyEvent.VK_C, "Copy into minibuffer", listener);
        JMenuItem abortMenuItem = createJMenuItemCTRL(menu, "Abort",
                KeyEvent.VK_G, "Abort current minibuffer command", listener);
        JMenuItem showAllMenuItem = createJMenuItemAlt(menu, "ShowAll", 0,
                "Show all IVs", listener);
        JMenuItem restoreMenuItem = createJMenuItemAlt(menu, "Restore", 0,
                "Restore all IVs", listener);
        JMenuItem addMenuItem = createJMenuItemAlt(menu,
                "Add Instance Variable", KeyEvent.VK_V,
                "Add the selected instance varible value", listener);
        JMenuItem hexMenuItem = createJMenuItemAlt(menu, "Special Format",
                KeyEvent.VK_X, "Display selected objects in special format",
                listener);
        JMenuItem printMenuItem = createJMenuItemAlt(menu, "Print", 0,
                "Print the selected object on terminal", listener);
        JMenuItem toStringMenuItem = createJMenuItemAlt(menu, "toString", 0,
                "Display obj.toString()", listener);
        JMenuItem inputObjectMenuItem = createJMenuItemAlt(menu,
                "Input Object", 0, "Input object from mini-buffer", listener);
        listener.addButtons(copyClassMenuItem, removeMenuItem, expandMenuItem,
                selectMenuItem, localMenuItem, retainMenuItem, setMenuItem,
                abortMenuItem, copyMenuItem, showAllMenuItem, restoreMenuItem,
                addMenuItem, hexMenuItem, printMenuItem, toStringMenuItem,
                inputObjectMenuItem);
        panel.add(menu);
    }

    private void createHelpMenu(JMenuBar panel) {
        JMenu menu = new JMenu("Help");
        // menu.setMnemonic('H');
        HelpMenuActionListener listener = new HelpMenuActionListener();
        JMenuItem ghelpMenuItem = createJMenuItemAlt(menu, "General Help", 0,
                "Show help message in mini buffer (bottom of Debugger)",
                listener);
        JMenuItem dhelpMenuItem = createJMenuItemAlt(
                menu,
                "ENV var Help",
                0,
                "Show Environment variable help message in mini buffer (bottom of Debugger)",
                listener);
        JMenuItem fhelpMenuItem = createJMenuItemAlt(menu, "FGET Help", 0,
                "Show FGET help message in mini buffer (bottom of Debugger)",
                listener);
        listener.addButtons(ghelpMenuItem, fhelpMenuItem, dhelpMenuItem);
        panel.add(menu);
    }

    private void createDebugMenu(JMenuBar panel) {
        JMenu menu = new JMenu("Debug");
        DebugMenuActionListener listener = new DebugMenuActionListener();
        JMenuItem recordMenuItem = createJMenuItemAlt(menu, "Record", 0,
                "Record Commands", listener);
        JMenuItem saveMenuItem = createJMenuItemAlt(menu, "Save", 0,
                "Save Commands to .debuggerCommands", listener);
        JMenuItem replayMenuItem = createJMenuItemAlt(menu, "Replay", 0,
                "Replay Commands", listener);
        JMenuItem dumpMenuItem = createJMenuItemAlt(menu, "Dump", 0,
                "Dump All Data to TTY", listener);
        JMenuItem statsMenuItem = createJMenuItemAlt(menu, "Statistics", 0,
                "Print out statistics to TTY", listener);
        JMenuItem revertMenuItem = createJMenuItemAlt(menu, "Revert", 0,
                "Revert to every time stamp in program", listener);
        JMenuItem testMenuItem = createJMenuItemAlt(menu, "Test", 0,
                "Run a test function", listener);
        JMenuItem wideMenuItem = createJMenuItemAlt(menu, "Wide", 0,
                "Show wide strings", listener);
        JMenuItem repeatMenuItem = createJMenuItemAlt(menu, "Repeat",
                KeyEvent.VK_SPACE, "Repeat last button command", listener);
        listener.addButtons(recordMenuItem, saveMenuItem, replayMenuItem,
                dumpMenuItem, statsMenuItem, revertMenuItem, testMenuItem,
                wideMenuItem, repeatMenuItem);
        panel.add(menu);
    }

    private JMenuItem createJMenuItemAlt(JMenu menu, String text, int key,
            String tooltip, ActionListener listener) {
        JMenuItem jm = new JMenuItem(text);
        if (key > 0)
            jm
                    .setAccelerator(KeyStroke.getKeyStroke(key,
                            ActionEvent.ALT_MASK));
        if (tooltip != null)
            jm.setToolTipText(tooltip);
        menu.add(jm);
        jm.addActionListener(listener);
        return jm;
    }

    private JMenuItem createJMenuItemCTRL(JMenu menu, String text, int key,
            String tooltip, ActionListener listener) {
        JMenuItem jm = new JMenuItem(text);
        if (key > 0)
            jm.setAccelerator(KeyStroke
                    .getKeyStroke(key, ActionEvent.CTRL_MASK));
        if (tooltip != null)
            jm.setToolTipText(tooltip);
        menu.add(jm);
        jm.addActionListener(listener);
        return jm;
    }

    private JMenuItem createJMenuItem(JMenu menu, String text, int key,
            String tooltip, ActionListener listener) {
        JMenuItem jm = new JMenuItem(text);
        if (key > 0)
            jm.setAccelerator(KeyStroke.getKeyStroke(key, 0));
        if (tooltip != null)
            jm.setToolTipText(tooltip);
        menu.add(jm);
        jm.addActionListener(listener);
        return jm;
    }

    private JSlider createTimeSlider() {
        timeSlider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);
        // Set in updateSlider
        timeSlider.addChangeListener(new TimeSliderListener());
        // timeSlider.setMajorTickSpacing(TimeStamp.eott()/20);
        // timeSlider.setPaintTicks(true);
        // timeSlider.setPaintLabels(true);
        timeSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        return (timeSlider);
    }

    public static JCheckBoxMenuItem codeDirection, codeOutsideOK, codeThreadOK,
            codeAnyDirection;

    private void createCodeMenu(JMenuBar panel) {
        JMenu menu = new JMenu("Code");
        codeDirection = new JCheckBoxMenuItem("Normal Direction");
        codeDirection.setState(true);
        codeAnyDirection = new JCheckBoxMenuItem("Any Direction OK");
        codeAnyDirection.setState(true);
        codeOutsideOK = new JCheckBoxMenuItem("Outside Current Stack Frame OK");
        codeOutsideOK.setState(true);
        codeThreadOK = new JCheckBoxMenuItem("Any Thread OK");

        StackMenuActionListener listener = new StackMenuActionListener();
        JMenuItem gotoStackJMenuItem = createJMenuItemAlt(menu,
                "Goto Stack Frame", 0, "Goto selected stack frame", listener);
        listener.addButtons(gotoStackJMenuItem);

        menu.add(codeAnyDirection);
        menu.add(codeDirection);
        menu.add(codeOutsideOK);
        menu.add(codeThreadOK);
        menu.add(gotoStackJMenuItem);
        panel.add(menu);
    }

    private void createFileMenu(JMenuBar panel) {
        JMenu menu = new JMenu("File");
        FileMenuActionListener listener = new FileMenuActionListener();
        JMenuItem openJMenuItem = createJMenuItemAlt(menu, "Open", 0,
                "Open File", listener);
        JMenuItem exitJMenuItem = createJMenuItemAlt(menu, "Exit", 0,
                "Open File", listener);
        JMenuItem addJMenuItem = createJMenuItemCTRL(menu, "Add Mark",
                KeyEvent.VK_SPACE, "Add a mark to the ring", listener);
        JMenuItem previousJMenuItem = createJMenuItemCTRL(menu,
                "Previous Mark", KeyEvent.VK_X, "Cycle one mark in the ring",
                listener);
        JMenuItem clearJMenuItem = createJMenuItemCTRL(menu, "Clear Marks", 0,
                "Clear the mark ring", listener);
        listener.addButtons(openJMenuItem, exitJMenuItem, addJMenuItem,
                previousJMenuItem, clearJMenuItem);
        panel.add(menu);
    }

    private void createRunMenu(JMenuBar panel) {
        JMenu menu = new JMenu("Run");
        RunMenuActionListener listener = new RunMenuActionListener();
        JMenuItem restartJMenuItem = createJMenuItemAlt(menu, "Restart", 0,
                "Clear memory and restart", listener);
        JMenuItem clearJMenuItem = createJMenuItemAlt(menu, "Clear", 0,
                "Clear everything.", listener);
        JMenuItem startJMenuItem = createJMenuItemAlt(menu,
                "Start Recording on Output", 0,
                "Start Recording on Selected TTY Output", listener);
        JMenuItem stopJMenuItem = createJMenuItemAlt(menu,
                "Stop Recording on Output", 0,
                "Stop Recording on Selected TTY Output", listener);
        JMenuItem startLJMenuItem = createJMenuItemAlt(menu,
                "Start Recording on Line", 0,
                "Start Recording on Selected Line", listener);
        JMenuItem stopLJMenuItem = createJMenuItemAlt(menu,
                "Stop Recording on Line", 0, "Stop Recording on Selected Line",
                listener);
        JMenuItem callJMenuItem = createJMenuItemCTRL(menu,
                "Evaluate Expression", KeyEvent.VK_E,
                "Example: <MyObj_1>.copy(<MyObj_2>, 1)", listener);
        JMenuItem switchJMenuItem = createJMenuItemCTRL(menu,
                "Switch Timelines", KeyEvent.VK_O,
                "Switch to the other Timeline", listener);
        JMenuItem clearSSJMenuItem = createJMenuItemCTRL(menu,
                "Clear Start/Stop", 0, "Clear All Start/Stop Request", listener);
        listener.addButtons(restartJMenuItem, clearJMenuItem, startJMenuItem,
                stopJMenuItem, startLJMenuItem, stopLJMenuItem, callJMenuItem,
                switchJMenuItem, clearSSJMenuItem);
        panel.add(menu);
    }

    public static void clear() {
        TraceLine.clear();
        TimeStamp.clear();
        Shadow.clear();
        ObjectPane.clear();
        ThreadPane.clear();
        StackList.clear();
        LocalsPane.clear();
        ThisPane.clear();
        TTYPane.clear();
        updateUIs();
        revert(); // Defaults to "NO TIME"
    }

    public static void clearTimeLine() {
        Shadow.reset();
        TraceLine.clear();
        TimeStamp.clear();
        ThreadPane.clear();
        StackList.clear();
        LocalsPane.clear();
        ThisPane.clear();
        TTYPane.clear();
        updateUIs();
        revert(); // Defaults to "NO TIME"
    }

    public static boolean mainTimeLine = true;

    public static void switchTimeLines(boolean clear) {
        Shadow.switchTimeLines(clear);
        TraceLine.switchTimeLines(clear);
        TimeStamp.switchTimeLines(clear);
        ObjectPane.switchTimeLines(clear);
        ThreadPane.switchTimeLines(clear);
        ThreadPList.setSelectedIndex(0); // HACK
        StackList.switchTimeLines(clear);
        ThisPane.switchTimeLines(clear);
        TTYPane.switchTimeLines(clear);
        if (TimeStamp.empty()) {
            // TimeStamp.clear() does this too. So this is never run?
            D.DISABLE = false;
            D.stamp(0, null);
            // The redisplay for Shadows in ObjectPane expect a valid TS
            D.DISABLE = true;
        }
        revert();
        firstTimeTracePane = true;
        updateUIs();

        mainTimeLine = !mainTimeLine;
    }

    public static void updateUIs() {
        reverting = true;
        updateThisPanel(null);
        ThreadPList.updateUI();
        TracePList.updateUI();
        LocalsPList.updateUI();
        ThisPList.updateUI();
        StackPList.updateUI();
        ObjectsPList.updateUI();
        TTYPList.updateUI();
        // updateTimeSlider();
        reverting = false;
    }

    public static void dump() {
        Debugger.printStatistics();
        previousTime = TimeStamp.currentTime();
        TimeStamp.printAll();
        TraceLine.printAll();
        Shadow.printAll();
        TTYPane.printAll();
    }

}
