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
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.hust.antlr.JavaLexer;
import com.hust.antlr.JavaParser;
import com.hust.model.Function;
import com.hust.model.JavaExtract;
import com.hust.model.Variable;
import com.hust.output.DictResult;
import com.hust.output.Result;
import com.hust.output.ScalarResult;
import com.hust.tools.Util;

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
        int[] functionParamNumber = new int[listener.functionList.size()];
        for (int i = 0; i < listener.functionList.size(); i++) {
            functionParamNumber[i] = listener.functionList.get(i).params.size();
        }
        fileFeatures.put("AverageOfFunctionParamNumber", new ScalarResult(Util.mean(functionParamNumber)));
        fileFeatures.put("VarianceOfFunctionParamNumber",
                new ScalarResult(Util.standardDeviation(functionParamNumber)));
    }

    public void calculateAverageAndVarianceOfLineLength(String[] fileAllLines) {
        if (fileAllLines.length == 0) {
            return;
        }
        int[] lineLength = new int[fileAllLines.length];
        for (int i = 0; i < fileAllLines.length; i++) {
            lineLength[i] = fileAllLines[i].length();
        }
        fileFeatures.put("AverageOfLineLength", new ScalarResult(Util.mean(lineLength)));
        fileFeatures.put("VarianceOfLineLength", new ScalarResult(Util.standardDeviation(lineLength)));
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

    public void calcluateWhiteSpaceChar(String fileData) {
        int whiteSpaceNumber = 0;
        int tabNumber = 0;
        int spaceNumber = 0;
        for (int i = 0; i < fileData.length(); i++) {
            if (fileData.charAt(i) == '\t') {
                tabNumber += 1;
                whiteSpaceNumber += 1;
            } else if (fileData.charAt(i) == ' ') {
                spaceNumber += 1;
                whiteSpaceNumber += 1;
            } else if (Character.toString(fileData.charAt(i)).matches("\\s")) {
                whiteSpaceNumber += 1;
            }
        }
        fileFeatures.put("TabNumber", new ScalarResult(tabNumber));
        fileFeatures.put("SpaceNumber", new ScalarResult(spaceNumber));
        fileFeatures.put("WhiteSpaceNumber", new ScalarResult(whiteSpaceNumber));
    }

    public void calcluateTabOrSpaceLeadLinesAndEmptyLineNumber(String[] fileAllLines) {
        int tabLeadNumber = 0;
        int spaceLeadNumber = 0;
        int emptyLineNumber = 0;
        for (String line : fileAllLines) {
            if (line.charAt(0) == '\t') {
                tabLeadNumber += 1;
            } else if (line.charAt(0) == ' ') {
                spaceLeadNumber += 1;
            }
            if (line.matches("\\s*")) {
                emptyLineNumber += 1;
            }
        }
        fileFeatures.put("TabLeadLines", new ScalarResult(tabLeadNumber > spaceLeadNumber ? 1 : 0));
        fileFeatures.put("EmptyLineNumber", new ScalarResult(emptyLineNumber));
    }

    public void calculateNewLineOrOnLineBeforeOpenBrance(CommonTokenStream tokens) {
        int newLineNumber = 0;
        int onLineNumber = 0;
        for (Token token : tokens.getTokens()) {
            if (token.getType() != JavaLexer.LBRACE) {
                continue;
            }
            boolean newLine = false;
            int tokenIndex = token.getTokenIndex();
            for (int i = tokenIndex - 1; i >= 0; i--) {
                int channel = tokens.get(i).getChannel();
                if (channel == JavaLexer.DEFAULT_TOKEN_CHANNEL) {
                    if (newLine) {
                        newLineNumber += 1;
                    } else {
                        onLineNumber += 1;
                    }
                } else if (channel == JavaLexer.SPACE) {
                    newLine = true;
                }
            }
        }
        fileFeatures.put("NewLineBeforeOpenBrance", new ScalarResult(newLineNumber > onLineNumber ? 1 : 0));
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
        fileFeatures.put("FileLineNumber", new ScalarResult(fileAllLines.length));
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
            CharStream charStream = CharStreams.fromFileName("src/main/java/com/hust/model/Function.java");
            JavaLexer lexer = new JavaLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JavaParser parser = new JavaParser(tokens);
            ParseTreeWalker walker = new ParseTreeWalker();
            JavaExtract listener = new JavaExtract();
            walker.walk(listener, parser.compilationUnit());
            System.out.println(tokens.getNumberOfOnChannelTokens());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
