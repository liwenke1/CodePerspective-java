package com.hust;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.hust.antlr.JavaLexer;
import com.hust.antlr.JavaParser;
import com.hust.antlr.JavaParserListener;
import com.hust.model.JavaExtract;

/**
 * Hello world!
 *
 */
public class App {
    public static List<String> readfile(String filepath) {
        List<String> file_list = new ArrayList<>();
        File file = new File(filepath);
        if (!file.isDirectory()) {
            file_list.add(file.getAbsolutePath());
        }

        else if (file.isDirectory()) {
            String[] filelist = file.list();
            // System.out.println(filelist);
            for (int i = 0; i < filelist.length; i++) {
                // System.out.println(filepath + "/" + filelist[i]);
                File readfile = new File(filepath + "/" + filelist[i]);
                if (!readfile.isDirectory()) {
                    file_list.add(readfile.getAbsolutePath());
                } else if (readfile.isDirectory()) {
                    file_list.addAll(readfile(filepath + "/" + filelist[i]));
                }
            }

        }
        return file_list;
    }

    private static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String filepath = "D:\\Code\\MyCodeClone\\dataset\\id2sourcecode\\id2sourcecode";
        List<String> fileList = readfile(filepath);
        long startTime = System.currentTimeMillis();
        System.out.println(fileList.size());
        int count = 0;
        for (int i = 0; i < fileList.size(); i++) {
            String code = readToString(fileList.get(i));
            code = "class _a {" + code + "}";
            try {
                ANTLRInputStream input = new ANTLRInputStream(code);
                JavaLexer lexer = new JavaLexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                JavaParser parser = new JavaParser(tokens);
                ParseTreeWalker walker = new ParseTreeWalker();
                ParseTreeListener listener = new JavaExtract();
                walker.walk(listener, parser.compilationUnit());
            } catch (Exception a) {
                count += 1;
                System.out.println(count);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
