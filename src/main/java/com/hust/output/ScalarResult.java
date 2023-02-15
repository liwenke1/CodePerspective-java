package com.hust.output;

public class ScalarResult implements Result {
    public double value = 0;

    public ScalarResult(double value) {
        this.value = value;
    }

    public double getResult() {
        return value;
    }
}