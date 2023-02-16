package com.hust.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.hust.output.DictResult;
import com.hust.output.Result;
import com.hust.output.ScalarResult;

public class FileParser {
    JavaExtract listener;
    ParseTreeWalker walker;
    Map<String, Result> fileFeatures;

    public FileParser() {
        listener = new JavaExtract();
        walker = new ParseTreeWalker();
        fileFeatures = new HashMap<String, Result>();
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

    public void calculateUsage(String code) {
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
        fileFeatures.put("NewUsageNumber", new ScalarResult(newUsageNumber));
        fileFeatures.put("OldUsageNumber", new ScalarResult(oldUsageNumber));
        fileFeatures.put("SafetyUsageNumber", new ScalarResult(safetyUsageNumber));
    }

    public double calculateFunctionAverageLength() {
        if (listener.functionList.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (Function function : listener.functionList) {
            sum += function.endLine - function.startLine + 1;
        }
        return sum / listener.functionList.size();
    }

    public void calculateWordUnigramFrequency(String fileData) {
        String[] wordUnigrams = fileData.split("\\s+");
        Map<String, Double> wordUnigramTF = new HashMap<>();
        for (String word : wordUnigrams) {
            if (wordUnigramTF.containsKey(word)) {
                wordUnigramTF.put(word, wordUnigramTF.get(word) + 1);
            } else {
                wordUnigramTF.put(word, Double.valueOf(1));
            }
        }
        fileFeatures.put("WordUnigramFrequency", new DictResult(wordUnigramTF));
    }

    public void calculateControlKeywordNumber() {
        fileFeatures.put("ControlKeywordNumber", new ScalarResult(listener.controlStructureNumber));
    }

    public void calculateTernaryNumber() {
        fileFeatures.put("TernaryNumber", new ScalarResult(listener.ternaryOperatorNumber));
    }

    public void calculateTokenNumber(String fileData) {
        fileFeatures.put("TokenNumber",
                new ScalarResult(fileData.split("[*;\\{\\}\\[\\]()+=\\-&/|%!?:,<>~`\\s\"]").length));
    }

    public void calculateCommentNumber(CommonTokenStream tokens) {
        Set<Integer> commentType = new HashSet<Integer>();
        commentType.add(JavaLexer.LINE_COMMENT);
        commentType.add(JavaLexer.COMMENT);
        fileFeatures.put("CommentNumber", new ScalarResult(tokens.getTokens(0, tokens.size() - 1, commentType).size()));
    }

    public void calculateLiteralNumber() {
        fileFeatures.put("LiteralNumber", new ScalarResult(listener.literalNumber));
    }

    public void calculateKeywordNumber(CommonTokenStream tokens) {
        Set<Integer> keywordType = new HashSet<>();
        // keyword type index is from 1 to 66
        for (int i = 0; i <= 66; i++) {
            keywordType.add(i);
        }
        fileFeatures.put("KeywordNumber", new ScalarResult(tokens.getTokens(0, tokens.size() - 1, keywordType).size()));
    }

    public void calculateFunctionNumber() {
        fileFeatures.put("FunctionNumber", new ScalarResult(listener.functionList.size()));
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
        if (listener.functionList.size() == 0) {
            return 0;
        }
        List<Double> variableRelativeLocation = new ArrayList<>();
        for (Function function : listener.functionList) {
            int functionStartLine = function.startLine;
            int functionLength = function.endLine - function.startLine + 1;
            for (Variable variable : function.localVariables) {
                variableRelativeLocation.add(Double.valueOf((variable.line - functionStartLine + 1) / functionLength));
            }
        }
        return calculateVariable(variableRelativeLocation);
    }

    public void calculateAverageAndVarianceOfFunctionParamNumber() {
        if (listener.functionList.size() == 0) {
            return;
        }
        int paramNumber = 0;
        for (Function function : listener.functionList) {
            paramNumber += function.params.size();
        }
        double averageParamNumber = paramNumber / listener.functionList.size(), variance = 0;
        for (Function function : listener.functionList) {
            variance += (Math.pow((function.params.size() - averageParamNumber), 2));
        }
        fileFeatures.put("AverageOfFunctionParamNumber", new ScalarResult(averageParamNumber));
        fileFeatures.put("VarianceOfFunctionParamNumber", new ScalarResult(variance));
    }

    public void calculateAverageAndVarianceOfLineLength(String[] fileAllLines) {
        int lineTotalLength = 0;
        for (String line : fileAllLines) {
            lineTotalLength += line.length();
        }
        double averageLineLength = lineTotalLength / fileAllLines.length, variance = 0;
        for (String line : fileAllLines) {
            variance += (Math.pow((line.length() - averageLineLength), 2));
        }
        fileFeatures.put("AverageOfLineLength", new ScalarResult(averageLineLength));
        fileFeatures.put("VarianceOfLineLength", new ScalarResult(variance));
    }

    public void calculateNestingDepth() {
        fileFeatures.put("NestingDepth", new ScalarResult(listener.nestingDepth));
    }

    public void calculateBranchingFactor() {
        double branchingFactor = 0;
        for (int branchingNumber : listener.branchingNumberList) {
            branchingFactor += branchingNumber;
        }
        branchingFactor /= listener.branchingNumberList.size();
        fileFeatures.put("BranchingFactor", new ScalarResult(branchingFactor));
    }

    private String[] readFileAllLines(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray(new String[lines.size()]);
        } finally {
            br.close();
        }
    }

    public void parseFile(String fileName) {
        String[] fileAllLines = new String[] {};
        try {
            fileAllLines = readFileAllLines(fileName);
        } catch (IOException e) {
            System.out.println(e);
        }
        String fileData = String.join("\n", fileAllLines);
        fileFeatures.put("FileLength", new ScalarResult(fileData.length()));
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
            CharStream charStream = CharStreams.fromFileName("target\\test.java");
            JavaLexer lexer = new JavaLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JavaParser parser = new JavaParser(tokens);
            ParseTreeWalker walker = new ParseTreeWalker();
            JavaExtract listener = new JavaExtract();
            walker.walk(listener, parser.compilationUnit());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
