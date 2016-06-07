/*
 Demo quick sort program with a bug.
 QuickSort 10       works correctly
 QuickSort 11       doesn't!
 */

package com.lambda.Experiment;


public class QuickSort {
    static int MAX;
    int[] array;

    public static void main(String[] argv) {
        int n = 10;
        if (argv.length > 0)
            n = Integer.parseInt(argv[0]);
        sortNElements(n);
    }

    public static void sortNElements(int nElements) {
        MAX = nElements;
        System.out
                .println("----------------------QuickSort Program----------------------");

        QuickSort q = new QuickSort();
        q.array = new int[MAX];
        q.array[0] = 1;
        for (int i = 1; i < MAX; i++)
            q.array[i] = ((i - 1) * 1233) % 1974; // More-or-less random

        q.sortAll();
        q.checkOrder();
        q.printAll();
    }

    public void sortAll() {
        Thread t = new Thread(new QuickSortRunnable(this, 0, MAX - 1));
        t.start();
        try {
            t.join();
        } catch (InterruptedException ie) {
            System.out.println("Impossible");
        }
    }

    public void checkOrder() {
        for (int i = 1; i < MAX; i++) {
            if (array[i - 1] > array[i])
                System.out.println("Out of order: array[" + (i - 1) + "]="
                        + array[i - 1] + " > array[" + i + "]=" + array[i]);
        }
    }

    public void printAll() {
        int top = MAX;
        if (MAX > 100)
            top = 100;
        for (int i = 0; i < top; i++) {
            System.out.println(i + "\t " + array[i]);
        }
    }

    // **** This will be called recursively and from different threads. ****

    public void sort(int start, int end) {
        int i, j, tmp, average, middle;

        if ((end - start) < 1)
            return; // One element, done!

        if ((end - start) == 1) { // Two elements, sort directly
            if (array[end] > array[start])
                return;
            tmp = array[start];
            array[start] = array[end];
            array[end] = tmp;
            return;
        }

        average = average(start, end);
        middle = end; // This will become the pivot point

        L: for (i = start; i < middle; i++) { // Start the pivot:
            if (array[i] > average) { // Move all values > average up,
                for (j = middle; j > i; j--) {
                    if (array[j] <= average) { // all values <= average down.
                        tmp = array[i];
                        array[i] = array[j];
                        array[j] = tmp;
                        middle = j; // The pivot point remains in the middle
                        continue L;
                    }
                }

            }
        }

        Thread t = null; // Make a new thread to do bottom half of sort
        t = new Thread(new QuickSortRunnable(this, start, middle - 1));
        t.start();

        sort(middle, end); // Do the top half here.

        try {
            t.join();
        } // Wait for the bottom half to finish
        catch (InterruptedException ie) {
            System.out.println("Impossible");
        }

        return;
    }

    public int average(int start, int end) {
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        return (sum / (end - start));
    }
}

// **************** Just a wrapper for threads ****************

class QuickSortRunnable implements Runnable {
    private int start, end;
    private QuickSort sorter;

    public QuickSortRunnable(QuickSort qs, int start, int end) {
        this.start = start;
        this.end = end;
        this.sorter = qs;
    }

    public void run() {
        sorter.sort(start, end);
    }

}
