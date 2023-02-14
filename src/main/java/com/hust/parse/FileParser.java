package com.hust.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.hust.model.JavaExtract;

public class FileParser {
    JavaExtract listener;
    ParseTreeWalker walker;

    public FileParser() {
        listener = new JavaExtract();
        walker = new ParseTreeWalker();
    }

    private int countMatchNumber(String code, String rule) {
        Pattern pattern = Pattern.compile(rule);
        Matcher matcher = pattern.matcher(code);
        int count = 0;
        while (matcher.find()) {
            count += 1;
        }
        return count;
    }

    public Map<String, Integer> calculateUsage(String code) {
        String[] newRules = new String[] {
                "->", ".stream", "Instant.", "LocalDate.", "LocalTime.",
                "LocalDateTime.", "ZonedDateTime.", "Period.",
                "ZoneOffset.", "Clock.", "Optional.", "var", "copyOf(",
                "ByteArrayOutputStream(", ".transferTo", ".isBlank",
                ".strip", ".stripTrailing", ".stripLeading", ".repeat",
                "Pack200."
        };
        String[] oldRules = new String[] {
                "com.sun.awt.AWTUtilities", "sun.misc.Unsafe.defineClass",
                "Thread.destroy", "Thread.stop", "jdk.snmp"
        };
        String[] safetyRules = new String[] {
                "public final", "private final", "SecurityManager",
                "synchronized", "volatile", "ReentrantLock"
        };
        int newUsageNumber = 0, oldUsageNumber = 0, safetyUsageNumber = 0;
        for (String newRule : newRules) {
            newUsageNumber += this.countMatchNumber(code, newRule);
        }
        for (String oldRule : oldRules) {
            oldUsageNumber += this.countMatchNumber(code, oldRule);
        }
        for (String safetyRule : safetyRules) {
            safetyUsageNumber += this.countMatchNumber(code, safetyRule);
        }
        Map<String, Integer> usageMap = new HashMap<>();
        usageMap.put("NewUsageNumber", newUsageNumber);
        usageMap.put("OldUsageNumber", oldUsageNumber);
        usageMap.put("SafetyUsageNumber", safetyUsageNumber);
        return usageMap;
    }

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("abc");
        Matcher m = pattern.matcher("abcabcabc dd abc");
        int count = 0;

        while (m.find()) {
            count++;
            System.out.println("Match number " + count);
            System.out.println("start(): " + m.start());
            System.out.println("end(): " + m.end());
        }
    }
}
