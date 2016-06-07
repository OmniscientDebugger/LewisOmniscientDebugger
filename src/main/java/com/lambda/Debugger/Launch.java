/*                        Launch.java

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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Launch extends JFrame {
    private static Launch launchWindow;
    JFileChooser fileChooser;

    public Launch() {
        File f = new File("");
        fileChooser = new JFileChooser(f.getAbsolutePath());
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                // Potentially, we could accept only class
                // files which have a main() method but I
                // suspect it would be a bit too heavy.

                return f.isDirectory() || f.getName().endsWith(".class");
                // || f.getName().endsWith(".jar")
            }

            public String getDescription() {
                return "*.class";
            }
        });
        fileChooser.setApproveButtonText("Launch");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        fileChooser.add(panel, BorderLayout.SOUTH);

        JButton openButton1 = new JButton("Demo");
        openButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Debugger.INSTRUMENT = false;
                Debugger.DEMO = true;
                Debugger.clazz = com.lambda.Debugger.Demo.class;
                Debugger.programName = "com.lambda.Debugger.Demo";
                ClassInformation ci = ClassInformation.get(DemoThing.class);
                ci.setUserSelectedField("name");
                StopButton.create(true, false, true, false);
                setVisible(false);
                launchWindow.dispose();
                Debugger.startTarget(Debugger.clazz); // Not called from cmd
                // line. Cannot have
                // args.
            }
        });
        panel.add(openButton1);
        JButton openButton2 = new JButton("QuickSort Bug");
        openButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Debugger.INSTRUMENT = false;
                Debugger.DEMO = true;
                Debugger.clazz = com.lambda.Debugger.QuickSortNonThreaded.class;
                Debugger.programName = "com.lambda.Debugger.QuickSortNonThreaded";
                StopButton.create(true, false, true, false);
                setVisible(false);
                launchWindow.dispose();
                Debugger.startTarget(Debugger.clazz); // Not called from cmd
                // line. Cannot have
                // args.
            }
        });
        panel.add(openButton2);
        JButton openButton3 = new JButton("Rewrite Bug");
        openButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Debugger.INSTRUMENT = false;
                Debugger.DEMO = true;
                Debugger.clazz = com.lambda.Debugger.Rewrite.class;
                Debugger.programName = "com.lambda.Debugger.Rewrite";
                StopButton.create(true, false, true, false);
                setVisible(false);
                launchWindow.dispose();
                Debugger.startTarget(Debugger.clazz); // Not called from cmd
                // line. Cannot have
                // args.
            }
        });
        panel.add(openButton3);
        /*
         * CANNOT GET JAVAX.swing SOURCE JButton openButton4 = new
         * JButton("Test"); openButton4.addActionListener(new ActionListener() {
         * public void actionPerformed(ActionEvent evt) { Debugger.INSTRUMENT =
         * false; Debugger.DEMO = true; Debugger.clazz =
         * com.lambda.Debugger.Test.class; Debugger.programName =
         * "com.lambda.Debugger.Test"; StopButton.create(true, false, true,
         * false); setVisible(false); launchWindow.dispose();
         * Debugger.startTarget(Debugger.clazz); // Not called from cmd line.
         * Cannot have args. }}); panel.add(openButton4);
         */
        // setBounds(200, 100, 200, 100);
    }

    public static void launch(File file) throws Exception {
        String fileName = file.getName();
        URL url = null;
        String className = null;

        if (fileName.endsWith(".class")) {
            Debugger.DIRECTORY = file.getParentFile().getAbsolutePath() + "/";
            url = new URL("file:" + file.getParentFile().getAbsolutePath()
                    + "/");
            className = fileName.substring(0, fileName.length() - 6);
        }

        else {
            System.err.println("Not a class file: " + file);
            return;
        }
        launchWindow.setVisible(false);
        launchWindow.dispose();
        launch(new URL[] { url }, className);
    }

    public static void launch(URL[] classPath, String className)
            throws Exception {
        Debugger.classLoader = new URLClassLoader(classPath);
        Debugger.programName = className;
        StopButton.create(false, false, true, true);
    }

    public static void create() {
        launchWindow = new Launch();
        launchWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int showOpenDialog = launchWindow.fileChooser.showOpenDialog(launchWindow);
        if (showOpenDialog == JFileChooser.CANCEL_OPTION) {
            System.exit(0);
        }
        if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
            try {
                launch(launchWindow.fileChooser.getSelectedFile());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        create();
    }
}
