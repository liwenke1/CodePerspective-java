package com.hust.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.hust.antlr.JavaLexer;
import com.hust.antlr.JavaParser;
import com.hust.model.Function;
import com.hust.model.JavaExtract;
import com.hust.model.Variable;

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
            newUsageNumber += countMatchNumber(code, newRule);
        }
        for (String oldRule : oldRules) {
            oldUsageNumber += countMatchNumber(code, oldRule);
        }
        for (String safetyRule : safetyRules) {
            safetyUsageNumber += countMatchNumber(code, safetyRule);
        }
        Map<String, Integer> usageMap = new HashMap<>();
        usageMap.put("NewUsageNumber", newUsageNumber);
        usageMap.put("OldUsageNumber", oldUsageNumber);
        usageMap.put("SafetyUsageNumber", safetyUsageNumber);
        return usageMap;
    }

    public double calculateFunctionAverageLength() {
        if (this.listener.functionList.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (Function function : this.listener.functionList) {
            sum += function.endLine - function.startLine + 1;
        }
        return sum / this.listener.functionList.size();
    }

    // calculate variable 方差
    private double calculateVariable(List<Double> list) {
        Double average = Double.valueOf(0), variance = Double.valueOf(0);
        for (Double number : list) {
            average += number;
        }
        average = average / list.size();
        for (Double number : list) {
            variance += (Math.pow((number - average), 2));
        }
        variance = variance / list.size();
        return variance;
    }

    public double calculateVariableLocationVariance() {
        if (this.listener.functionList.size() == 0) {
            return 0;
        }
        List<Double> variableRelativeLocation = new ArrayList<>();
        for (Function function : this.listener.functionList) {
            int functionStartLine = function.startLine;
            int functionLength = function.endLine - function.startLine + 1;
            for (Variable variable : function.localVariables) {
                variableRelativeLocation.add(Double.valueOf((variable.line - functionStartLine + 1) / functionLength));
            }
        }
        return calculateVariable(variableRelativeLocation);
    }

    public static void main(String[] args) {
        // Pattern pattern = Pattern.compile("abc");
        // Matcher m = pattern.matcher("abcabcabc dd abc");
        // int count = 0;

        // while (m.find()) {
        // count++;
        // System.out.println("Match number " + count);
        // System.out.println("start(): " + m.start());
        // System.out.println("end(): " + m.end());
        // }
        try {
            CharStream charStream = CharStreams.fromFileName("src\\main\\java\\com\\hust\\antlr\\JavaLexer.java");
            JavaLexer lexer = new JavaLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JavaParser parser = new JavaParser(tokens);
            ParseTreeWalker walker = new ParseTreeWalker();
            JavaExtract listener = new JavaExtract();
            walker.walk(listener, parser.compilationUnit());
            System.out.println(listener.functionList.size());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
