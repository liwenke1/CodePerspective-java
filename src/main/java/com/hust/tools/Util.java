package com.hust.tools;

import java.util.List;

public class Util {
    public static double mean(List<Integer> list) {
        double average = 0;
        for (int number : list) {
            average += number;
        }
        return average / list.size();
    }

    public static double mean(int[] list) {
        double average = 0;
        for (int number : list) {
            average += number;
        }
        return average / list.length;
    }

    public static double variance(List<Integer> list) {
        int sum1 = 0;
        int sum2 = 0;
        for (int number : list) {
            sum1 += number * number;
            sum2 += number;
        }
        return sum1 / list.size() - (sum2 / list.size()) * (sum2 / list.size());
    }

    public static double variance(int[] list) {
        int sum1 = 0;
        int sum2 = 0;
        for (int number : list) {
            sum1 += number * number;
            sum2 += number;
        }
        return sum1 / list.length - (sum2 / list.length) * (sum2 / list.length);
    }

    public static double standardDeviation(List<Integer> list) {
        return Math.sqrt(variance(list));
    }

    public static double standardDeviation(int[] list) {
        return Math.sqrt(variance(list));
    }
}
