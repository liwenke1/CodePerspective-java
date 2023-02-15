package com.hust.output;

import java.util.Map;

public class DictResult implements Result {
    public Map<String, Double> value;

    public DictResult(Map<String, Double> value) {
        this.value = value;
    }

    public double getResult() {
        return 0;
    }
}
