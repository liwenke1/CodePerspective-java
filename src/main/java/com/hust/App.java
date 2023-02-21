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

    public static List<Map<String, Result>> parseFilePathList(List<File> filepathList) {
        List<Map<String, Result>> parseResultList = new ArrayList<>();
        FileParser fileParser = new FileParser();
        for (int i = 0; i < filepathList.size(); i++) {
            parseResultList.add(fileParser.parseFile(filepathList.get(i).getAbsolutePath()));
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
        Util.writeFile("@attribute Gender {male, female}\n", outputFileName);
    }

    // attribute lexical features
    public static void writeAttributeAboutLexicalFeatures(String[] wordUnigramList, String outputFileName) {
        for (int i = 0; i < wordUnigramList.length; i++) {
            wordUnigramList[i] = wordUnigramList[i].replace("'", "apostrophesymbol");
            Util.writeFile("@attribute 'WordUnigramTF " + i + "=[" + wordUnigramList[i] + "]' numeric\n",
                    outputFileName);
        }
        Util.writeFile("@attribute ControlStructureRatio numeric\n", outputFileName);
        Util.writeFile("@attribute TernaryRatio numeric\n", outputFileName);
        Util.writeFile("@attribute TokenRatio numeric\n", outputFileName);
        Util.writeFile("@attribute CommentRatio numeric\n", outputFileName);
        Util.writeFile("@attribute LiteralRatio numeric\n", outputFileName);
        Util.writeFile("@attribute KeywordRatio numeric\n", outputFileName);
        Util.writeFile("@attribute FunctionRatio numeric\n", outputFileName);
        Util.writeFile("@attribute NestingDepth numeric\n", outputFileName);
        Util.writeFile("@attribute BranchingFactor numeric\n", outputFileName);
        Util.writeFile("@attribute AverageFunctionParamNumber numeric\n",
                outputFileName);
        Util.writeFile("@attribute VarianceFunctionParamNumber numeric\n",
                outputFileName);
        Util.writeFile("@attribute AverageLineLength numeric\n", outputFileName);
        Util.writeFile("@attribute VarianceAverageLineLength numeric\n",
                outputFileName);
    }

    // attribute layout features
    public static void writeAttributeAboutLayoutFeatures(String outputFileName) {
        Util.writeFile("@attribute TabRatio numeric\n", outputFileName);
        Util.writeFile("@attribute SpaceRatio numeric\n", outputFileName);
        Util.writeFile("@attribute EmptyLineRatio numeric\n", outputFileName);
        Util.writeFile("@attribute WhiteSpaceRatio numeric\n", outputFileName);
        Util.writeFile("@attribute NewLineBeforeOpenBrance {true, false}\n",
                outputFileName);
        Util.writeFile("@attribute TabLeadLines {true, false}\n", outputFileName);
    }

    // attribute syntantic features
    public static void writeAttributeAboutSyntanticFeatures(String[] keywordList, String[] typeNodeList,
            String[] leafNodeList, String outputFileName) {
        Util.writeFile("@attribute MaxDepthASTNode numeric\n", outputFileName);
        for (int i = 0; i < typeNodeList.length; i++) {
            typeNodeList[i] = typeNodeList[i].replace("'", "apostrophesymbol");
            Util.writeFile("@attribute 'TypeNodeTF " + i + "=[" + typeNodeList[i] + "]' numeric\n", outputFileName);
        }
        for (int i = 0; i < typeNodeList.length; i++) {
            typeNodeList[i] = typeNodeList[i].replace("'", "apostrophesymbol");
            Util.writeFile("@attribute 'TypeNodeTFIDF " + i + "=[" + typeNodeList[i] + "]' numeric\n", outputFileName);
        }
        Util.writeFile("@attribute TypeNodeAverageDepth numeric\n", outputFileName);
        for (int i = 0; i < keywordList.length; i++) {
            keywordList[i] = keywordList[i].replace("'", "apostrophesymbol");
            Util.writeFile("@attribute 'KeywordTF " + i + "=[" + keywordList[i] + "]' numeric\n", outputFileName);
        }
        for (int i = 0; i < leafNodeList.length; i++) {
            leafNodeList[i] = leafNodeList[i].replace("'", "apostrophesymbol");
            Util.writeFile("@attribute 'LeafNodeTF " + i + "=[" + leafNodeList[i] + "]' numeric\n", outputFileName);
        }
        for (int i = 0; i < leafNodeList.length; i++) {
            leafNodeList[i] = leafNodeList[i].replace("'", "apostrophesymbol");
            Util.writeFile("@attribute 'LeafNodeTFIDF " + i + "=[" + leafNodeList[i] + "]' numeric\n", outputFileName);
        }
        Util.writeFile("@attribute LeafNodeAverageDepth numeric\n\n", outputFileName);
    }

    public static void writeDataField(String outputFileName) {
        Util.writeFile("@data\n", outputFileName);
    }

    public static void writeDataAboutLabel(String authorName, String gender, String outputFileName) {
        Util.writeFile(authorName + "," + gender + ",", outputFileName);
    }

    // write data lexical features
    public static void writeDataAboutLexicalFeatures(Map<String, Result> fileFeatures, String[] wordUnigramList,
            String outputFileName) {
        double fileLength = fileFeatures.get("FileLength").getScalarResult();
        Map<String, Double> wordUnigramTF = fileFeatures.get("WordUnigramTF").getDictResult();
        for (String key : wordUnigramList) {
            if (wordUnigramTF.containsKey(key)) {
                Util.writeFile(wordUnigramTF.get(key) + ",", outputFileName);
            } else {
                Util.writeFile("0,", outputFileName);
            }
        }
        Util.writeFile(fileFeatures.get("ControlStructureNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("TernaryNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("TokenNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("CommentNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("LiteralNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("KeywordNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("FunctionNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("NestingDepth").getScalarResult() + ",", outputFileName);
        Util.writeFile(fileFeatures.get("BranchingFactor").getScalarResult() + ",", outputFileName);
        Util.writeFile(fileFeatures.get("AverageOfFunctionParamNumber").getScalarResult() + ",", outputFileName);
        Util.writeFile(fileFeatures.get("VarianceOfFunctionParamNumber").getScalarResult() + ",", outputFileName);
        Util.writeFile(fileFeatures.get("AverageOfLineLength").getScalarResult() + ",", outputFileName);
        Util.writeFile(fileFeatures.get("VarianceOfLineLength").getScalarResult() + ",", outputFileName);
    }

    // write data layout features
    public static void writeDataAboutLayoutFeatures(Map<String, Result> fileFeatures, String outputFileName) {
        double fileLength = fileFeatures.get("FileLength").getScalarResult();
        Util.writeFile(fileFeatures.get("TabNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("SpaceNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("EmptyLineNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("WhiteSpaceNumber").getScalarResult() / fileLength + ",", outputFileName);
        Util.writeFile(fileFeatures.get("NewLineBeforeOpenBrance").getScalarResult() == 1 ? "true" : "false" + ",",
                outputFileName);
        Util.writeFile(fileFeatures.get("TabLeadLines").getScalarResult() == 1 ? "true" : "false" + ",",
                outputFileName);
    }

    // write data syntantic features
    public static void writeDataAboutSyntanticFeatures(Map<String, Result> fileFeatures, String[] keywordList,
            String[] typeNodeList, String[] leafNodeList, String outputFileName) {
        Util.writeFile(fileFeatures.get("MaxDepthASTNode").getScalarResult() + ",", outputFileName);
        float[] typeNodeTF = calculateTermFrequency(fileFeatures.get("TypeNodeFrequency").getDictResult(),
                typeNodeList);
        for (float tf : typeNodeTF) {
            Util.writeFile(tf + ",", outputFileName);
        }
        Util.writeFile(fileFeatures.get("TypeNodeAverageDepth").getScalarResult() + ",", outputFileName);
        Map<String, Double> keywordTF = fileFeatures.get("KeywordTF").getDictResult();
        for (String key : keywordList) {
            if (keywordTF.containsKey(key)) {
                Util.writeFile(keywordTF.get(key) + ",", outputFileName);
            } else {
                Util.writeFile("0,", outputFileName);
            }
        }
        float[] leafNodeTF = calculateTermFrequency(fileFeatures.get("LeafNodeFrequency").getDictResult(),
                leafNodeList);
        for (float tf : leafNodeTF) {
            Util.writeFile(tf + ",", outputFileName);
        }
        Util.writeFile(fileFeatures.get("LeafNodeAverageDepth").getScalarResult() + "\n", outputFileName);
    }

    public static void main(String[] args) {
        String outputFileName = "D:\\Code\\dataset\\github-malicious.arff";
        String sourceCodeDir = "D:\\Code\\dataset\\github-malicious";
        List<File> filePathList = Util.listJavaFiles(sourceCodeDir);
        List<Map<String, Result>> parseResultList = parseFilePathList(filePathList);
        String[] wordUnigramList = extractWordUnigramList(parseResultList);
        String[] keywordList = extractKeywordList(parseResultList);
        String[] typeNodeList = extractTypeNodeList(parseResultList);
        String[] leafNodeList = extractLeafNodeList(parseResultList);

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
            Map<String, Result> fileFeatures = parseResultList.get(i);
            String authorName = filePathList.get(i).getPath().split("/|\\\\")[6];
            String gender = filePathList.get(i).getPath().split("/|\\\\")[5];
            writeDataAboutLabel(authorName, gender, outputFileName);
            writeDataAboutLexicalFeatures(fileFeatures, wordUnigramList, outputFileName);
            writeDataAboutLayoutFeatures(fileFeatures, outputFileName);
            writeDataAboutSyntanticFeatures(fileFeatures, keywordList, typeNodeList, leafNodeList, outputFileName);
        }
    }
}
