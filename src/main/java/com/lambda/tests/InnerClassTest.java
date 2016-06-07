package com.lambda.tests;

import java.util.ArrayList;
import java.util.List;

public class InnerClassTest {

    public static void main(String[] args) {
        startTarget(null);
    }

    static void startTarget(Thing thing) {
        final Thing thing0 = thing;

        Thing t = new Thing() {
            public void processThing(int i, int i0, int i1, int i2, int i3,
                    int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
                Thing t = thing0;
            }
        };
        t.processThing(-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

}

class Thing {
    public void processThing(int i, int i0, int i1, int i2, int i3, int i4,
            int i5, int i6, int i7, int i8, int i9, int i10) {

    }
}