package com.hust;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hust.arff.Arff;
import com.hust.arff.SparseArff;
import com.hust.output.Result;
import com.hust.parse.FileParser;
import com.hust.tools.Util;

public class Main {

    public static List<Map<String, Result>> parseFilePathList(List<File> filePathList) {
        List<Map<String, Result>> parseResultList = new ArrayList<>();
        FileParser fileParser = new FileParser();
        for (int i = 0; i < filePathList.size(); i++) {
            System.out.println("-------- parse file: " + filePathList.get(i).getPath() + " --------\n");
            parseResultList.add(fileParser.parseFile(filePathList.get(i).getAbsolutePath()));
        }
        return parseResultList;
    }

    public static Map<String, Integer> extractAuthorId(List<File> filePathList) {
        Map<String, Integer> authorId = new HashMap<>();
        for (File file : filePathList) {
            String authorName = file.getPath().split("/|\\\\")[6];
            if (!authorId.containsKey(authorName)) {
                authorId.put(authorName, authorId.size() + 1);
            }
        }
        return authorId;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String outputFileName = "D:\\Code\\dataset\\github-malicious-copy2.arff";
        String sourceCodeDir = "D:\\Code\\dataset\\github-malicious-copy";
        List<File> filePathList = Util.listJavaFiles(sourceCodeDir);
        List<Map<String, Result>> parseResultList = parseFilePathList(filePathList);
        Map<String, Integer> authorId = extractAuthorId(filePathList);

        Arff arff = new SparseArff(parseResultList);
        for (int i = 0; i < parseResultList.size(); i++) {
            System.out.println("-------- save file: " + filePathList.get(i).getPath() + " --------\n");
            Map<String, Result> fileFeatures = parseResultList.get(i);
            String authorName = filePathList.get(i).getPath().split("/|\\\\")[6];
            String gender = filePathList.get(i).getPath().split("/|\\\\")[5];
            arff.writeFile(authorId.get(authorName), gender, fileFeatures, outputFileName);
        }
        System.out.println("time cost : ");
        System.out.println(System.currentTimeMillis() - start);
    }
}
