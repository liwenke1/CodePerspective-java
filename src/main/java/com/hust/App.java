package com.hust;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.hust.output.Result;
import com.hust.parse.FileParser;
import com.hust.tools.Util;

public class App {

    public static List<Map<String, Result>> parseFilePathList(List<File> filePathList) {
        List<Map<String, Result>> parseResultList = new ArrayList<>();
        FileParser fileParser = new FileParser();
        for (int i = 0; i < filePathList.size(); i++) {
            System.out.println("-------- parse file: " + filePathList.get(i).getPath() + " --------\n");
            parseResultList.add(fileParser.parseFile(filePathList.get(i).getAbsolutePath()));
        }
        return parseResultList;
    }

    public static String[] extractWordUnigramList(List<Map<String, Result>> parseResultList) {
        HashSet<String> wordUnigramSet = new HashSet<>();
        for (Map<String, Result> fileFeature : parseResultList) {
            wordUnigramSet.addAll(fileFeature.get("WordUnigramTF").getDictResult().keySet());
        }
        return wordUnigramSet.toArray(new String[0]);
    }

    public static String[] extractKeywordList(List<Map<String, Result>> parseResultList) {
        HashSet<String> keywordSet = new HashSet<>();
        for (Map<String, Result> fileFeature : parseResultList) {
            keywordSet.addAll(fileFeature.get("KeywordTF").getDictResult().keySet());
        }
        return keywordSet.toArray(new String[0]);
    }

    public static String[] extractTypeNodeList(List<Map<String, Result>> parseResultList) {
        HashSet<String> typeNodeSet = new HashSet<>();
        for (Map<String, Result> fileFeature : parseResultList) {
            typeNodeSet.addAll(fileFeature.get("TypeNodeFrequency").getDictResult().keySet());
        }
        return typeNodeSet.toArray(new String[0]);
    }

    public static String[] extractLeafNodeList(List<Map<String, Result>> parseResultList) {
        HashSet<String> leafNodeSet = new HashSet<>();
        for (Map<String, Result> fileFeature : parseResultList) {
            leafNodeSet.addAll(fileFeature.get("LeafNodeFrequency").getDictResult().keySet());
        }
        return leafNodeSet.toArray(new String[0]);
    }

    public static float[] calculateTermFrequency(Map<String, Double> frequency, String[] allKeyList) {
        int keyLength = allKeyList.length;
        float[] termFrequency = new float[keyLength];
        float sum = 0;
        for (Double value : frequency.values()) {
            sum += value;
        }
        for (int i = 0; i < keyLength; i++) {
            allKeyList[i] = allKeyList[i].replace("'", "apostrophesymbol");
            if (frequency.containsKey(allKeyList[i])) {
                termFrequency[i] = (float) (frequency.get(allKeyList[i]) / sum);
            } else {
                termFrequency[i] = 0;
            }
        }
        return termFrequency;
    }

    public static void writeRelation(String outputFileName) {
        Util.writeFile("@relation github-malicious" + "\n\n", outputFileName);
    }

    /*
     * attribute about label :
     * (1) author name
     * (2) gender
     */
    public static void writeAttributeAboutLabel(String outputFileName) {
        Util.writeFile("@attribute AuthorName numeric\n", outputFileName);
        // Util.writeFile("@attribute Gender {male, female}\n", outputFileName);
    }

    // attribute lexical features
    public static void writeAttributeAboutLexicalFeatures(String[] wordUnigramList, String outputFileName) {
        StringBuilder writeString = new StringBuilder();
        for (int i = 0; i < wordUnigramList.length; i++) {
            wordUnigramList[i] = wordUnigramList[i].replace("'", "apostrophesymbol");
            writeString.append("@attribute 'WordUnigramTF " + i + "=[" + wordUnigramList[i] + "]' numeric\n");
        }
        writeString.append("@attribute ControlStructureRatio numeric\n");
        writeString.append("@attribute TernaryRatio numeric\n");
        writeString.append("@attribute TokenRatio numeric\n");
        writeString.append("@attribute CommentRatio numeric\n");
        writeString.append("@attribute LiteralRatio numeric\n");
        writeString.append("@attribute KeywordRatio numeric\n");
        writeString.append("@attribute FunctionRatio numeric\n");
        writeString.append("@attribute NestingDepth numeric\n");
        writeString.append("@attribute BranchingFactor numeric\n");
        writeString.append("@attribute AverageFunctionParamNumber numeric\n");
        writeString.append("@attribute VarianceFunctionParamNumber numeric\n");
        writeString.append("@attribute AverageLineLength numeric\n");
        writeString.append("@attribute VarianceAverageLineLength numeric\n");
        Util.writeFile(writeString.toString(), outputFileName);
    }

    // attribute layout features
    public static void writeAttributeAboutLayoutFeatures(String outputFileName) {
        StringBuilder writeString = new StringBuilder();
        writeString.append("@attribute TabRatio numeric\n");
        writeString.append("@attribute SpaceRatio numeric\n");
        writeString.append("@attribute EmptyLineRatio numeric\n");
        writeString.append("@attribute WhiteSpaceRatio numeric\n");
        writeString.append("@attribute NewLineBeforeOpenBrance {true, false}\n");
        writeString.append("@attribute TabLeadLines {true, false}\n");
        Util.writeFile(writeString.toString(), outputFileName);
    }

    // attribute syntantic features
    public static void writeAttributeAboutSyntanticFeatures(String[] keywordList, String[] typeNodeList,
            String[] leafNodeList, String outputFileName) {
        StringBuilder writeString = new StringBuilder();
        writeString.append("@attribute MaxDepthASTNode numeric\n");
        for (int i = 0; i < typeNodeList.length; i++) {
            typeNodeList[i] = typeNodeList[i].replace("'", "apostrophesymbol");
            writeString.append("@attribute 'TypeNodeTF " + i + "=[" + typeNodeList[i] + "]' numeric\n");
        }
        for (int i = 0; i < typeNodeList.length; i++) {
            typeNodeList[i] = typeNodeList[i].replace("'", "apostrophesymbol");
            writeString.append("@attribute 'TypeNodeTFIDF " + i + "=[" + typeNodeList[i] + "]' numeric\n");
        }
        writeString.append("@attribute TypeNodeAverageDepth numeric\n");
        for (int i = 0; i < keywordList.length; i++) {
            keywordList[i] = keywordList[i].replace("'", "apostrophesymbol");
            writeString.append("@attribute 'KeywordTF " + i + "=[" + keywordList[i] + "]' numeric\n");
        }
        for (int i = 0; i < leafNodeList.length; i++) {
            leafNodeList[i] = leafNodeList[i].replace("'", "apostrophesymbol");
            writeString.append("@attribute 'LeafNodeTF " + i + "=[" + leafNodeList[i] + "]' numeric\n");
        }
        for (int i = 0; i < leafNodeList.length; i++) {
            leafNodeList[i] = leafNodeList[i].replace("'", "apostrophesymbol");
            writeString.append("@attribute 'LeafNodeTFIDF " + i + "=[" + leafNodeList[i] + "]' numeric\n");
        }
        writeString.append("@attribute LeafNodeAverageDepth numeric\n\n");
        Util.writeFile(writeString.toString(), outputFileName);
    }

    public static void writeDataField(String outputFileName) {
        Util.writeFile("@data\n", outputFileName);
    }

    public static void writeDataAboutLabel(String authorName, String gender, String outputFileName) {
        Util.writeFile(authorName + ",", outputFileName);
    }

    // write data lexical features
    public static void writeDataAboutLexicalFeatures(Map<String, Result> fileFeatures, String[] wordUnigramList,
            String outputFileName) {
        StringBuilder writeString = new StringBuilder();
        double fileLength = fileFeatures.get("FileLength").getScalarResult();
        Map<String, Double> wordUnigramTF = fileFeatures.get("WordUnigramTF").getDictResult();
        for (String key : wordUnigramList) {
            key = key.replace("'", "apostrophesymbol");
            if (wordUnigramTF.containsKey(key)) {
                writeString.append(wordUnigramTF.get(key) + ",");
            } else {
                writeString.append("0,");
            }
        }
        writeString.append(fileFeatures.get("ControlStructureNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("ControlStructureNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("TernaryNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("TokenNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("CommentNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("LiteralNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("KeywordNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("FunctionNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("NestingDepth").getScalarResult() + ",");
        writeString.append(fileFeatures.get("BranchingFactor").getScalarResult() + ",");
        writeString.append(fileFeatures.get("AverageOfFunctionParamNumber").getScalarResult() + ",");
        writeString.append(fileFeatures.get("VarianceOfFunctionParamNumber").getScalarResult() + ",");
        writeString.append(fileFeatures.get("AverageOfLineLength").getScalarResult() + ",");
        writeString.append(fileFeatures.get("VarianceOfLineLength").getScalarResult() + ",");
        Util.writeFile(writeString.toString(), outputFileName);
    }

    // write data layout features
    public static void writeDataAboutLayoutFeatures(Map<String, Result> fileFeatures, String outputFileName) {
        StringBuilder writeString = new StringBuilder();
        double fileLength = fileFeatures.get("FileLength").getScalarResult();
        writeString.append(fileFeatures.get("TabNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("SpaceNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("EmptyLineNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("WhiteSpaceNumber").getScalarResult() / fileLength + ",");
        writeString.append(fileFeatures.get("NewLineBeforeOpenBrance").getScalarResult() == 1 ? "true" : "false" + ",");
        writeString.append(fileFeatures.get("TabLeadLines").getScalarResult() == 1 ? "true" : "false" + ",");
        Util.writeFile(writeString.toString(), outputFileName);
    }

    // write data syntantic features
    public static void writeDataAboutSyntanticFeatures(Map<String, Result> fileFeatures, String[] keywordList,
            String[] typeNodeList, String[] leafNodeList, String outputFileName) {
        StringBuilder writeString = new StringBuilder();
        writeString.append(fileFeatures.get("MaxDepthASTNode").getScalarResult() + ",");
        float[] typeNodeTF = calculateTermFrequency(fileFeatures.get("TypeNodeFrequency").getDictResult(),
                typeNodeList);
        for (float tf : typeNodeTF) {
            writeString.append(tf + ",");
        }
        writeString.append(fileFeatures.get("TypeNodeAverageDepth").getScalarResult() + ",");
        Map<String, Double> keywordTF = fileFeatures.get("KeywordTF").getDictResult();
        for (String key : keywordList) {
            key = key.replace("'", "apostrophesymbol");
            if (keywordTF.containsKey(key)) {
                writeString.append(keywordTF.get(key) + ",");
            } else {
                writeString.append("0,");
            }
        }
        float[] leafNodeTF = calculateTermFrequency(fileFeatures.get("LeafNodeFrequency").getDictResult(),
                leafNodeList);
        for (float tf : leafNodeTF) {
            writeString.append(tf + ",");
        }
        writeString.append(fileFeatures.get("LeafNodeAverageDepth").getScalarResult() + "\n");
        Util.writeFile(writeString.toString(), outputFileName);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String outputFileName = "D:\\Code\\dataset\\40authors.arff";
        String sourceCodeDir = "D:\\Code\\dataset\\40authors";
        List<File> filePathList = Util.listJavaFiles(sourceCodeDir);
        List<Map<String, Result>> parseResultList = parseFilePathList(filePathList);
        String[] wordUnigramList = extractWordUnigramList(parseResultList);
        String[] keywordList = extractKeywordList(parseResultList);
        String[] typeNodeList = extractTypeNodeList(parseResultList);
        String[] leafNodeList = extractLeafNodeList(parseResultList);

        System.out.println("parse time cost : ");
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();

        // write relation field
        writeRelation(outputFileName);

        // write attribute field
        writeAttributeAboutLabel(outputFileName);
        writeAttributeAboutLexicalFeatures(wordUnigramList, outputFileName);
        writeAttributeAboutLayoutFeatures(outputFileName);
        writeAttributeAboutSyntanticFeatures(keywordList, typeNodeList, leafNodeList, outputFileName);

        writeDataField(outputFileName);
        // write data field
        for (int i = 0; i < parseResultList.size(); i++) {
            System.out.println("-------- save file: " + filePathList.get(i).getPath() + " --------\n");
            Map<String, Result> fileFeatures = parseResultList.get(i);
            String authorName = filePathList.get(i).getPath().split("/|\\\\")[4];
            String gender = filePathList.get(i).getPath().split("/|\\\\")[0];
            writeDataAboutLabel(authorName, gender, outputFileName);
            writeDataAboutLexicalFeatures(fileFeatures, wordUnigramList, outputFileName);
            writeDataAboutLayoutFeatures(fileFeatures, outputFileName);
            writeDataAboutSyntanticFeatures(fileFeatures, keywordList, typeNodeList, leafNodeList, outputFileName);
        }
        System.out.println("save time cost : ");
        System.out.println(System.currentTimeMillis() - start);
    }
}
