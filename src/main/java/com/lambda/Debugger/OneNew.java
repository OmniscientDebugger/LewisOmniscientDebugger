/*                        OneNew.java

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



public class OneNew  {
    static int count = 0;
    int cnt;


    public static void main(String[] args) {
      //OneNew lot= new OneNew(2);
      //lot.cnt++;
      //lot.frob(1);
	System.out.println("OneNew successfully completed!");
	System.exit(1);
    }

    public String toString() {
	return "<Micro1>";
    }

  public int frob(int i) {return 1;}

    public OneNew() {
        cnt = count++;
    }
    public OneNew(int i) {
        cnt = count++;
    }
}


