package com.lambda.tests;

public class PrivateInnerClassTest {

    public static void main(String[] args) {
        Outer o = new Outer();
        o.doSomeCoolThing();
        System.out.println("Done");
    }


    public void someMethod() {
    }

}

class Outer extends PrivateInnerClassTest {
    public void someMethod() {
    }

    public void doSomeCoolThing() {
        Inner i = new Inner();
        i.doSomething();
    }

    class Inner {
        public void doSomething() {
            Outer.super.someMethod();
        }
    }
}
