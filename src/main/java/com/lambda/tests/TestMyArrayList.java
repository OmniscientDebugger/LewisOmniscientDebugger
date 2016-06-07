package com.lambda.tests;

import java.util.ArrayList;

public class TestMyArrayList {

    public static void main(String[] args) {
        int i = 1;
        ArrayList v = new ArrayList();
         
        v.add("Thing_0");
        v.add("Thing_" + (i++));
        v.add(0,"Thing_" + (i++));
        v.add("Thing_" + (i++));
        v.add("Thing_" + (i++));
        
        v.clear(); 
        v.add("Thing_" + (i++));
        System.out.println("Done");
    }
}
