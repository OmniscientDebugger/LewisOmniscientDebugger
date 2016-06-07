package com.lambda.tests;

public class SystemOutErrTest {
    

    public static void main(String[] args) {
        System.out.println("==========SystemOutErrTest==========");
        SystemOutErrTest test = new SystemOutErrTest();
        test.test1();
    }

    private void test1() {
        System.err.println("Yes!");
    }

}
