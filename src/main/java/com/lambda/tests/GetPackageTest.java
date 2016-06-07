package com.lambda.tests;

import java.security.ProtectionDomain;

public class GetPackageTest {
    public void test1() {
        Class c = this.getClass();
        Package p = c.getPackage();
        //ProtectionDomain d = c.getProtectionDomain();
        ClassLoader cl = c.getClassLoader();
        System.out.println(cl);
        System.out.println(c);
        System.out.println(p);
        //System.out.println(d);
    }

    public static void main(String[] args) {
        System.out.println("==========GetPackageTest==========");
        GetPackageTest test = new GetPackageTest();
        test.test1();
    }
}
