package com.lambda.tests;

public class LoadOrderTest {
    void doNothing(LoadOrderTestSub aParam) {
    }

    public static void main(String argv[]) {
        System.out.println("yes");
        LoadOrderTest t = new LoadOrderTest();
        t.doNothing(null);
        System.out.println("yes");
        // new LoadOrderTestSub();
    }

}

class LoadOrderTestSub {
    static {
        System.out.println("Oh no no no");
        if (true)
            throw new Error("bu");
    }
}
