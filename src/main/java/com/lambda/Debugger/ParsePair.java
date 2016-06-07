/*                        ParsePair.java

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

//              Completion.java

/*
 */


import java.lang.reflect.Method;


class ParsePair {
  
  public String inputString;
  public Object obj;
  public MethodCompletionPair cPair;
  public Method method;
  public Object[] objects;

  public ParsePair(String s, Object o, MethodCompletionPair cp, Object[] os) {
    inputString=s;
    obj = o;
    cPair = cp;
    objects = os;
  }

  public ParsePair(String s, Object o, MethodCompletionPair cp) {
    inputString=s;
    obj = o;
    cPair = cp;
    objects = new Object[0];
    method = cp.firstMethod();
  }


  public ParsePair(String s, Object o, MethodCompletionPair cp, Object arg1) {
    inputString=s;
    obj = o;
    cPair = cp;
    objects = new Object[1];
    objects[0] = arg1;
    method = cp.firstMethod();
  }



  public ParsePair(String s, Object o, MethodCompletionPair cp, Object arg1, Object arg2) {
    inputString=s;
    obj = o;
    cPair = cp;
    objects = new Object[2];
    objects[0] = arg1;
    objects[1] = arg2;
    method = cp.firstMethod();
  }


  public ParsePair(String s, Object o, MethodCompletionPair cp, Object arg1, Object arg2, Object arg3) {
    inputString=s;
    obj = o;
    cPair = cp;
    objects = new Object[3];
    objects[0] = arg1;
    objects[1] = arg2;
    objects[2] = arg3;
    method = cp.firstMethod();
  }


  public ParsePair(String s, Object o, MethodCompletionPair cp, Object arg1, Object arg2, Object arg3, Object arg4) {
    inputString=s;
    obj = o;
    cPair = cp;
    objects = new Object[4];
    objects[0] = arg1;
    objects[1] = arg2;
    objects[2] = arg3;
    objects[3] = arg4;
    method = cp.firstMethod();
  }

  public String toString() {
    if (objects.length == 0) return("PP("+inputString+"\n"+obj+" . "+method+"( )");
    if (objects.length == 1) return("PP("+inputString+"\n"+obj+" . "+method+"( "+objects[0]+" )");
    if (objects.length == 2) return("PP("+inputString+"\n"+obj+" . "+method+"( "+objects[0]+", "+objects[1]+" )");
    return "HUH?";
  }

}
