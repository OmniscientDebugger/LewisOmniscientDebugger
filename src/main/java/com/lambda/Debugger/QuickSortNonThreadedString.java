/*                        QuickSortNonThreadedString.java

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

// This is a really doppy way of doing this! (BUT IT WORKS)

public class QuickSortNonThreadedString {
  static String programString = 
"package com.lambda.Debugger; \n" +
" \n" +
"import java.io.*; \n" +
"import java.util.*; \n" +
" \n" +
"/* \n" +
"  Demo quick sort program with a bug. \n" +
"  QuickSortNonThreaded 10		works correctly \n" +
"  QuickSortNonThreaded 11		doesn't! \n" +
"*/ \n" +
" \n" +
"public class QuickSortNonThreaded { \n" +
"    static int		MAX; \n" +
"    int[]		array; \n" +
" \n" +
"     \n" +
" \n" +
"    public static void main(String[] argv) { \n" +
"	int n = 11; \n" +
"	if (argv.length > 0) n = Integer.parseInt(argv[0]); \n" +
" \n" +
"	long start = System.currentTimeMillis(); \n" +
"	 \n" +
"	sortNElements(n); \n" +
" \n" +
"	long end = System.currentTimeMillis(); \n" +
"	long total = end-start; \n" +
"	System.out.println(\"\"+ total + \"ms\"); \n" +
" \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public static void sortNElements(int nElements) { \n" +
"	MAX = nElements; \n" +
"	System.out.println(\"-------------- QuickSortNonThreaded Program ----------------\"); \n" +
" \n" +
"	QuickSortNonThreaded q = new QuickSortNonThreaded(); \n" +
"	q.array = new int[MAX]; \n" +
"	q.array[0] = 1; \n" +
"	for (int i=1; i < MAX; i++) q.array[i] = ((i-1)*1233)%1974;// More-or-less random \n" +
" \n" +
"	q.sortAll(); \n" +
"	q.checkOrder(); \n" +
"	q.printAll(); \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public void sortAll() { \n" +
"	sort(0, MAX-1); \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public void checkOrder() { \n" +
"	for (int i=1; i < MAX; i++) { \n" +
"	    if (array[i-1] > array[i]) \n" +
"		System.out.println(\"Out of order: array[\" + (i-1) + \"]=\"+array[i-1] \n" +
"				   +\" > array[\"+i+\"]=\"+array[i]); \n" +
"	} \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public void printAll() { \n" +
"	int top = MAX; \n" +
"	if (MAX > 100) top=100; \n" +
"	for (int i=0; i < top; i++) { \n" +
"	    System.out.println(i + \"\t \"+ array[i]); \n" +
"	} \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    // **** This will be called both recursively and from different threads. **** \n" +
" \n" +
"    public void sort(int start, int end) { \n" +
"	int i, j, tmp, average, middle; \n" +
" \n" +
"	if ((end - start) < 1) return;			// One element, done! \n" +
" \n" +
"	if ((end - start) == 1) {			// Two elements, sort directly \n" +
"	    if (array[end] > array[start]) return; \n" +
"	    tmp = array[start]; \n" +
"	    array[start] = array[end]; \n" +
"	    array[end] = tmp; \n" +
"	    return; \n" +
"	} \n" +
" \n" +
"	average = average(start, end); \n" +
"	middle = end;					// This will become the pivot point \n" +
" \n" +
"	L: for (i = start; i < middle; i++) {		// Start the pivot:  \n" +
"	    if (array[i] > average) {			// Move all values > average up,  \n" +
"		for (j = middle; j > i; j--) { \n" +
"		    if (array[j] <= average) {		// all values <= average down. \n" +
"			tmp = array[i]; \n" +
"			array[i] = array[j]; \n" +
"			array[j] = tmp; \n" +
"			middle = j;		// The pivot point remains in the middle \n" +
"			continue L; \n" +
"		    } \n" +
"		} \n" +
" \n" +
"	    } \n" +
"	} \n" +
" \n" +
" \n" +
" \n" +
"	sort(start, middle-1);				// Do the bottom half here. \n" +
"	sort(middle, end);				// Do the top half here. \n" +
" \n" +
"	return; \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public int average(int start, int end) { \n" +
"	int sum = 0; \n" +
"	for (int i = start; i < end; i++) { \n" +
"	    sum += array[i]; \n" +
"	} \n" +
"	return (sum/(end-start)); \n" +
"    } \n" +
"} \n" +
"\n";
}
