package com.lambda.Debugger;

import java.io.*;
import java.util.*;

/*
  Demo quick sort program with a bug.
  QuickSortNonThreaded 10               works correctly
  QuickSortNonThreaded 11               doesn't!
*/

public class QuickSortNonThreaded {
    static int          MAX;
    int[]               array;

    

    public static void main(String[] argv) {
        int n = 11;
        if (argv.length > 0) n = Integer.parseInt(argv[0]);

        long start = System.currentTimeMillis();
        
        sortNElements(n);

        long end = System.currentTimeMillis();
        long total = end-start;
        System.out.println(""+ total + "ms");

    }



    public static void sortNElements(int nElements) {
        MAX = nElements;
        System.out.println("-------------- QuickSortNonThreaded Program ----------------");

        QuickSortNonThreaded q = new QuickSortNonThreaded();
        q.array = new int[MAX];
        q.array[0] = 1;
        for (int i=1; i < MAX; i++) q.array[i] = ((i-1)*1233)%1974;// More-or-less random

        q.sortAll();
        q.checkOrder();
        q.printAll();
    }



    public void sortAll() {
        sort(0, MAX-1);
    }



    public void checkOrder() {
        for (int i=1; i < MAX; i++) {
            if (array[i-1] > array[i])
                System.out.println("Out of order: array[" + (i-1) + "]="+array[i-1]
                                   +" > array["+i+"]="+array[i]);
        }
    }



    public void printAll() {
        int top = MAX;
        if (MAX > 100) top=100;
        for (int i=0; i < top; i++) {
            System.out.println(i + "\t "+ array[i]);
        }
    }



    // **** This will be called both recursively and from different threads. ****

    public void sort(int start, int end) {
        int i, j, tmp, average, middle;

        if ((end - start) < 1) return;                  // One element, done!

        if ((end - start) == 1) {                       // Two elements, sort directly
            if (array[end] > array[start]) return;
            tmp = array[start];
            array[start] = array[end];
            array[end] = tmp;
            return;
        }

        average = average(start, end);
        middle = end;                                   // This will become the pivot point

        L: for (i = start; i < middle; i++) {           // Start the pivot: 
            if (array[i] > average) {                   // Move all values > average up, 
                for (j = middle; j > i; j--) {
                    if (array[j] <= average) {          // all values <= average down.
                        tmp = array[i];
                        array[i] = array[j];
                        array[j] = tmp;
                        middle = j;             // The pivot point remains in the middle
                        continue L;
                    }
                }

            }
        }



        sort(start, middle-1);                          // Do the bottom half here.
        sort(middle, end);                              // Do the top half here.

        return;
    }



    public int average(int start, int end) {
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        return (sum/(end-start));
    }
}


