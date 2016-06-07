/*                        Clock.java

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

public class Clock implements Runnable {
    private static boolean started = false;

    private static long startTime, endTime;
    private static long[] clockTimes = new long[10000];
    private static int[] timeStamps = new int[10000];
    private static int index = 0;

    public static int getTS(int i) {
        return timeStamps[i];
    }

    public static int size() {
        return index;
    }

    public static int getCK(int i) {
        if (i == 0)
            return 0;
        return (int) (clockTimes[i] - clockTimes[i - 1]);
    }

    public static synchronized void start() {
        if (started)
            return;
        Thread t = new Thread(new Clock(), "Clock");
        t.start();
        started = true;
    }

    public void run() {
        startTime = System.currentTimeMillis();
        int now = TimeStamp.eott();
        clockTimes[index] = 0;
        timeStamps[index] = now;
        index++;

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
            } // Never happen
            if (index == clockTimes.length) {
                Debugger.println("Turning off clock");
                return;
            }
            if (D.PAUSE_PROGRAM)
                continue;
            now = TimeStamp.eott();
            if (timeStamps[index - 1] == now)
                continue;
            long clockTime = System.currentTimeMillis();
            if (clockTimes[index - 1] == clockTime)
                continue;
            clockTimes[index] = clockTime - startTime;
            timeStamps[index] = now;
            index++;
        }
    }

    public static String getString(int time) { // time stamp time!
        if (true)
            return "";
        if (index < 2)
            return "NoClock";
        endTime = clockTimes[index - 2];
        String s = " Clock[" + formatTime(endTime) + "] "
                + formatTime(findTime(time));
        return s;
    }

    public static String formatTime(long time) {
        String neg = "";
        if (time < 0) {
            neg = "-";
            time = -time;
        }
        int nChars = ("" + endTime).length() + 1;
        long seconds = time / 1000;
        long ms = time - (seconds * 1000) + 1000;
        String s = neg + seconds + "." + ("" + ms).substring(1, 4);
        while (s.length() < nChars)
            s = " " + s;
        return s;
    }

    public static long findTime(int time) {
        for (int i = index - 1; i > -1; i--) {
            if (timeStamps[i] < time)
                return clockTimes[i];
        }
        return 0;
    }

    public static void dump() {
        System.out
                .println("\n===================== Clock =====================");
        System.out.println("Index\tClock\tDelta\tStamp\tDelta\tS/10ms");
        System.out.println("" + 0 + "\t" + formatTime(clockTimes[0]) + "\t"
                + formatTime(0) + "\t" + timeStamps[0] + "\t" + 0 + "\t" + 0);

        for (int i = 1; i < index; i++) {
            long t = clockTimes[i] - clockTimes[i - 1];
            int s = timeStamps[i] - timeStamps[i - 1];
            int ave = 0;
            if (t / 10 != 0)
                ave = s / ((int) t / 10);
            System.out.println("" + i + "\t" + formatTime(clockTimes[i]) + "\t"
                    + formatTime(t) + "\t" + timeStamps[i] + "\t" + s + "\t"
                    + ave);
        }
        System.out.println("");
    }

    public static void main(String[] args) {
        Clock.start();
    }

    public static void compactAll() {
        int s = timeStamps[0];
        int f = TimeStamp.forwardNext(s);
        int previous = f;
        int j = 0;

        timeStamps[0] = f;

        for (int i = 1; i < index; i++) {
            s = timeStamps[i];
            f = TimeStamp.forwardNext(s);
            if (f == previous)
                continue;
            previous = f;
            timeStamps[j] = f;
            clockTimes[j] = clockTimes[i];
            j++;
        }
        index = j;
        if (index == 0) {
            int now = TimeStamp.eott();
            clockTimes[index] = 0;
            timeStamps[index] = now;
            index++;
        }
    }

}
