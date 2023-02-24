package com.hust.arff;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.hust.output.Result;
import com.hust.tools.Util;

public abstract class Arff {

    protected String[] wordUnigramList;
    protected String[] keywordList;
    protected String[] typeNodeList;
    protected String[] leafNodeList;

    public Arff(List<Map<String, Result>> parseResultList) {
        wordUnigramList = extractWordUnigramList(parseResultList);
        keywordList = extractKeywordList(parseResultList);
        typeNodeList = extractTypeNodeList(parseResultList);
        leafNodeList = extractLeafNodeList(parseResultList);
    }

    private String[] extractWordUnigramList(List<Map<String, Result>> parseResultList) {
        HashSet<String> wordUnigramSet = new HashSet<>();
        for (Map<String, Result> fileFeature : parseResultList) {
            wordUnigramSet.addAll(fileFeature.get("WordUnigramTF").getDictResult().keySet());
        }
        return wordUnigramSet.toArray(new String[0]);
    }

    private String[] extractKeywordList(List<Map<String, Result>> parseResultList) {
        HashSet<String> keywordSet = new HashSet<>();
        for (Map<String, Result> fileFeature : parseResultList) {
            keywordSet.addAll(fileFeature.get("KeywordTF").getDictResult().keySet());
        }
        return keywordSet.toArray(new String[0]);
    }

    private String[] extractTypeNodeList(List<Map<String, Result>> parseResultList) {
        HashSet<String> typeNodeSet = new HashSet<>();
        for (Map<String, Result> fileFeature : parseResultList) {
            typeNodeSet.addAll(fileFeature.get("TypeNodeFrequency").getDictResult().keySet());
        }
        return typeNodeSet.toArray(new String[0]);
    }

    private String[] extractLeafNodeList(List<Map<String, Result>> parseResultList) {
        HashSet<String> leafNodeSet = new HashSet<>();
        for (Map<String, Result> fileFeature : parseResultList) {
            leafNodeSet.addAll(fileFeature.get("LeafNodeFrequency").getDictResult().keySet());
        }
        return leafNodeSet.toArray(new String[0]);
    }

    /*
     * attribute about label :
     * (1) author name
     * (2) gender
     */
    private void writeAttributeAboutLabel(String outputFileName) {
        Util.writeFile("@attribute AuthorName numeric\n", outputFileName);
        Util.writeFile("@attribute Gender {male, female}\n", outputFileName);
    }

    // attribute lexical features
    private void writeAttributeAboutLexicalFeatures(String[] wordUnigramList, String outputFileName) {
        StringBuilder writeString = new StringBuilder();
        for (int i = 0; i < wordUnigramList.length; i++) {
            wordUnigramList[i] = wordUnigramList[i].replace("'", "apostrophesymbol");
            wordUnigramList[i] = wordUnigramList[i].replace("\n", "carriagereturn");
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
        writeString.append("@attribute VarianceAverageLineLength numeric\n\n");
        Util.writeFile(writeString.toString(), outputFileName);
    }

    // attribute layout features
    private void writeAttributeAboutLayoutFeatures(String outputFileName) {
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
    private void writeAttributeAboutSyntanticFeatures(String[] keywordList, String[] typeNodeList,
            String[] leafNodeList, String outputFileName) {
        StringBuilder writeString = new StringBuilder();
        writeString.append("@attribute MaxDepthASTNode numeric\n");
        for (int i = 0; i < typeNodeList.length; i++) {
            typeNodeList[i] = typeNodeList[i].replace("'", "apostrophesymbol");
            typeNodeList[i] = typeNodeList[i].replace("\n", "carriagereturn");
            writeString.append("@attribute 'TypeNodeTF " + i + "=[" + typeNodeList[i] + "]' numeric\n");
        }
        // for (int i = 0; i < typeNodeList.length; i++) {
        // typeNodeList[i] = typeNodeList[i].replace("'", "apostrophesymbol");
        // writeString.append("@attribute 'TypeNodeTFIDF " + i + "=[" + typeNodeList[i]
        // + "]' numeric\n");
        // }
        writeString.append("@attribute TypeNodeAverageDepth numeric\n");
        for (int i = 0; i < keywordList.length; i++) {
            keywordList[i] = keywordList[i].replace("'", "apostrophesymbol");
            keywordList[i] = keywordList[i].replace("\n", "carriagereturn");
            writeString.append("@attribute 'KeywordTF " + i + "=[" + keywordList[i] + "]' numeric\n");
        }
        for (int i = 0; i < leafNodeList.length; i++) {
            leafNodeList[i] = leafNodeList[i].replace("'", "apostrophesymbol");
            leafNodeList[i] = leafNodeList[i].replace("\n", "carriagereturn");
            writeString.append("@attribute 'LeafNodeTF " + i + "=[" + leafNodeList[i] + "]' numeric\n");
        }
        // for (int i = 0; i < leafNodeList.length; i++) {
        // leafNodeList[i] = leafNodeList[i].replace("'", "apostrophesymbol");
        // writeString.append("@attribute 'LeafNodeTFIDF " + i + "=[" + leafNodeList[i]
        // + "]' numeric\n");
        // }
        writeString.append("@attribute LeafNodeAverageDepth numeric\n");
        Util.writeFile(writeString.toString(), outputFileName);
    }

    // write data label
    protected abstract void writeDataAboutLabel(int authorId, String gender, String outputFileName);

    // write data lexical features
    protected abstract void writeDataAboutLexicalFeatures(Map<String, Result> fileFeatures, String outputFileName);

    // write data layout features
    protected abstract void writeDataAboutLayoutFeatures(Map<String, Result> fileFeatures, String outputFileName);

    // write data syntantic features
    protected abstract void writeDataAboutSyntanticFeatures(Map<String, Result> fileFeatures, String outputFileName);

    // write relation field
    private void writeRelation(String outputFileName) {
        Util.writeFile("@relation github-malicious" + "\n\n", outputFileName);
    }

    // write attribute field
    private void writeAttribute(String outputFileName) {
        writeAttributeAboutLabel(outputFileName);
        writeAttributeAboutLexicalFeatures(wordUnigramList, outputFileName);
        writeAttributeAboutLayoutFeatures(outputFileName);
        writeAttributeAboutSyntanticFeatures(keywordList, typeNodeList, leafNodeList, outputFileName);
        Util.writeFile("\n", outputFileName);
    }

    // write data start field
    private void writeDataField(String outputFileName) {
        Util.writeFile("@data\n", outputFileName);
    }

    // write each data field
    private void writeData(int authorId, String gender, Map<String, Result> fileFeatures,
            String outputFileName) {
        writeDataAboutLabel(authorId, gender, outputFileName);
        writeDataAboutLexicalFeatures(fileFeatures, outputFileName);
        writeDataAboutLayoutFeatures(fileFeatures, outputFileName);
        writeDataAboutSyntanticFeatures(fileFeatures, outputFileName);
        Util.writeFile("\n", outputFileName);
    }

    protected abstract void flush();

    public void writeFile(int authorId, String gender, Map<String, Result> fileFeatures, String outputFileName) {
        flush();
        if (!Util.checkFileIsExist(outputFileName)) {
            writeRelation(outputFileName);
            writeAttribute(outputFileName);
            writeDataField(outputFileName);
        }
        writeData(authorId, gender, fileFeatures, outputFileName);
    }
}
