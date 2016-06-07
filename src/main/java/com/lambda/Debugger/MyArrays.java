/*                        MyArrays.java

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

import java.util.*;

public class MyArrays {

    public static void sort(byte[] array) {
	Shadow.record(array);
	Arrays.sort(array);
	Shadow.record(array, true);
    }
    public static void sort(byte[] array, int start, int end) {
	Shadow.record(array);
	Arrays.sort(array, start, end);
	Shadow.record(array, true);
    }


    public static void sort(char[] array) {
	Shadow.record(array);
	Arrays.sort(array);
	Shadow.record(array, true);
    }
    public static void sort(char[] array, int start, int end) {
	Shadow.record(array);
	Arrays.sort(array, start, end);
	Shadow.record(array, true);
    }


    public static void sort(short[] array) {
	Shadow.record(array);
	Arrays.sort(array);
	Shadow.record(array, true);
    }
    public static void sort(short[] array, int start, int end) {
	Shadow.record(array);
	Arrays.sort(array, start, end);
	Shadow.record(array, true);
    }


    public static void sort(int[] array) {
	Shadow.record(array);
	Arrays.sort(array);
	Shadow.record(array, true);
    }
    public static void sort(int[] array, int start, int end) {
	Shadow.record(array);
	Arrays.sort(array, start, end);
	Shadow.record(array, true);
    }


    public static void sort(long[] array) {
	Shadow.record(array);
	Arrays.sort(array);
	Shadow.record(array, true);
    }
    public static void sort(long[] array, int start, int end) {
	Shadow.record(array);
	Arrays.sort(array, start, end);
	Shadow.record(array, true);
    }


    public static void sort(float[] array) {
	Shadow.record(array);
	Arrays.sort(array);
	Shadow.record(array, true);
    }
    public static void sort(float[] array, int start, int end) {
	Shadow.record(array);
	Arrays.sort(array, start, end);
	Shadow.record(array, true);
    }


    public static void sort(double[] array) {
	Shadow.record(array);
	Arrays.sort(array);
	Shadow.record(array, true);
    }
    public static void sort(double[] array, int start, int end) {
	Shadow.record(array);
	Arrays.sort(array, start, end);
	Shadow.record(array, true);
    }


    public static void sort(Object[] array) {
	Shadow.record(array);
	Arrays.sort(array);
	Shadow.record(array, true);
    }
    public static void sort(Object[] array, int start, int end) {
	Shadow.record(array);
	Arrays.sort(array, start, end);
	Shadow.record(array, true);
    }

    public static void sort(Object[] array, Comparator c) {
	Shadow.record(array);
	Arrays.sort(array, c);
	Shadow.record(array, true);
    }
    public static void sort(Object[] array, int start, int end, Comparator c) {
	Shadow.record(array);
	Arrays.sort(array, start, end, c);
	Shadow.record(array, true);
    }



    public static void fill(boolean[] array, boolean value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(boolean[] array, int start, int end, boolean value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }

    public static void fill(byte[] array, byte value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(byte[] array, int start, int end, byte value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }


    public static void fill(char[] array, char value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(char[] array, int start, int end, char value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }


    public static void fill(short[] array, short value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(short[] array, int start, int end, short value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }


    public static void fill(int[] array, int value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(int[] array, int start, int end, int value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }


    public static void fill(long[] array, long value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(long[] array, int start, int end, long value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }


    public static void fill(float[] array, float value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(float[] array, int start, int end, float value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }


    public static void fill(double[] array, double value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(double[] array, int start, int end, double value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }


    public static void fill(Object[] array, Object value) {
	Shadow.record(array);
	Arrays.fill(array, value);
	Shadow.record(array, true);
    }
    public static void fill(Object[] array, int start, int end, Object value) {
	Shadow.record(array);
	Arrays.fill(array, start, end, value);
	Shadow.record(array, true);
    }




}
