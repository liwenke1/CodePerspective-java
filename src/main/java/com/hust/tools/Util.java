package com.hust.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

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

    public static String[] readFileAllLines(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray(new String[lines.size()]);
        } finally {
            br.close();
        }
    }

    public static List<File> listJavaFiles(String dirPath) {

        File topDir = new File(dirPath);

        List<File> directories = new ArrayList<>();
        directories.add(topDir);

        List<File> textFiles = new ArrayList<>();

        List<String> filterWildcards = new ArrayList<>();
        filterWildcards.add("*.java");

        FileFilter typeFilter = new WildcardFileFilter(filterWildcards);

        while (directories.isEmpty() == false) {
            List<File> subDirectories = new ArrayList<File>();
            for (File f : directories) {
                subDirectories.addAll(Arrays.asList(f.listFiles((FileFilter) DirectoryFileFilter.INSTANCE)));
                textFiles.addAll(Arrays.asList(f.listFiles(typeFilter)));
            }
            directories.clear();
            directories.addAll(subDirectories);
        }
        return textFiles;
    }

    public static void writeFile(String allLines, String fileName) {
        boolean append = true;
        File aFile = new File(fileName);
        FileWriter aFileWriter;
        try {
            if (aFile.exists() == false)
                aFile.createNewFile();
            aFileWriter = new FileWriter(aFile, append);
            {
                aFileWriter.write(allLines);
            }
            aFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
