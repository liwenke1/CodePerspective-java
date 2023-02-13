package com.hust.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hust.antlr.JavaParserBaseListener;
import com.hust.antlr.JavaParser.ClassOrInterfaceModifierContext;
import com.hust.antlr.JavaParser.CompilationUnitContext;

public class JavaExtract extends JavaParserBaseListener {

    // function based features
    int functionNumber;
    List<Function> functionList;
    int lambdaFunctionNumber;

    // class based features
    List<String> classNameList;
    int classNumber;
    List<String> classVariableNameList;
    int classVariableNumber;

    // quote
    List<String> importNameList;
    int importNumber;

    // code style
    List<String> exceptionNameList;
    int exceptionNumber;
    List<String> packageNameList;
    int packageNumber;
    int ternaryOperatorNumber;
    int controlStructureNumber;
    int literalNumber;

    Map<String, Integer> accessControlCount;

    public JavaExtract() {
        // function based features
        this.functionNumber = 0;
        this.functionList = new ArrayList<Function>();
        this.lambdaFunctionNumber = 0;

        // class based features
        this.classNameList = new ArrayList<String>();
        this.classNumber = 0;
        this.classVariableNameList = new ArrayList<String>();
        this.classVariableNumber = 0;

        // quote
        this.importNameList = new ArrayList<String>();
        this.importNumber = 0;

        // code style
        this.exceptionNameList = new ArrayList<String>();
        this.exceptionNumber = 0;
        this.packageNameList = new ArrayList<String>();
        this.packageNumber = 0;
        this.ternaryOperatorNumber = 0;
        this.controlStructureNumber = 0;
        this.literalNumber = 0;

        this.accessControlCount = new HashMap<String, Integer>();
        this.accessControlCount.put("Default", 0);
        this.accessControlCount.put("Public", 0);
        this.accessControlCount.put("Protected", 0);
        this.accessControlCount.put("Private", 0);
    }

    @Override
    public void enterClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
        if (ctx.PUBLIC() != null) {
        }
        super.enterClassOrInterfaceModifier(ctx);
    }
}
