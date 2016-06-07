package com.lambda.tests;

import java.util.Vector;

public class TestMyVector {

    public static void main(String[] args) {
        int i = 1;
        Vector v = new Vector();
        v.setSize(0);
        v.add("Thing_0");
        v.add("Thing_" + (i++));
        v.add("Thing_" + (i++));
        v.add("Thing_" + (i++));
        v.add("Thing_" + (i++));
        v.setSize(5);
        v.setSize(2);
        v.clear();
        v.setSize(3);
        v.add("Thing_" + (i++));
        System.out.println("Done");
    }
}
