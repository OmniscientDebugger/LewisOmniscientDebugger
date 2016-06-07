/*                        MyObj.java

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

// USED in the unit tests DON'T DELETE

import java.io.*;
import java.util.*;


public class MyObj  {
    int aNumber=0;
    String s;
    HashMap v = new HashMap();

    public void frob(int nn, MyObj mo) {
        for (int i = 0; i<nn; i++) { 
            twiddle(mo);
        }
    }


    public void twiddle(MyObj o) {
	if (o == null) return;
	twiddle(null);
        MyObj m1 = new MyObj(10);        
        s = o.toString() + " " + this.toString();
	v.put(o, s); 
        //System.out.println(" Obj: " + s);
	return;
    }

    static int repeat = 1;

    public static void main(String[] args) {
	if (args.length > 0) repeat = Integer.parseInt(args[0]);
        MyObj m1 = new MyObj();        

        long start = new Date().getTime();                                        
        m1.frob(repeat, m1);
        long end = new Date().getTime();   
        System.out.println("Run time:" + (end-start) + "ms");
	Debugger.printStatistics();
    }


    static int count = 0;
    int cnt = 0;

    public MyObj() {
        cnt = count++;
    }
    public MyObj(int i) {
        cnt = count++;
	aNumber = i;
    }

    public String toString() {
        return("<MyObj_" + cnt + ">");
    }


}



