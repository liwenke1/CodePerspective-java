package com.hust.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hust.antlr.JavaParserBaseListener;
import com.hust.antlr.JavaParser.CatchTypeContext;
import com.hust.antlr.JavaParser.ClassDeclarationContext;
import com.hust.antlr.JavaParser.ClassOrInterfaceModifierContext;
import com.hust.antlr.JavaParser.CompilationUnitContext;
import com.hust.antlr.JavaParser.ExpressionContext;
import com.hust.antlr.JavaParser.ImportDeclarationContext;
import com.hust.antlr.JavaParser.LambdaExpressionContext;
import com.hust.antlr.JavaParser.LiteralContext;
import com.hust.antlr.JavaParser.MethodDeclarationContext;
import com.hust.antlr.JavaParser.PackageDeclarationContext;
import com.hust.antlr.JavaParser.QualifiedNameContext;
import com.hust.antlr.JavaParser.StatementContext;

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
    public void enterPackageDeclaration(PackageDeclarationContext ctx) {
        String packageName = ctx.qualifiedName().getText();
        this.packageNameList.add(packageName);
        this.packageNumber += 1;
        super.enterPackageDeclaration(ctx);
    }

    @Override
    public void enterImportDeclaration(ImportDeclarationContext ctx) {
        String importName = ctx.qualifiedName().getText();
        this.importNameList.add(importName);
        this.importNumber += 1;
        super.enterImportDeclaration(ctx);
    }

    @Override
    public void enterClassDeclaration(ClassDeclarationContext ctx) {
        String className = ctx.identifier().getText();
        this.classNameList.add(className);
        this.classNumber += 1;
        super.enterClassDeclaration(ctx);
    }

    @Override
    public void enterCatchType(CatchTypeContext ctx) {
        List<QualifiedNameContext> exceptions = ctx.qualifiedName();
        for (QualifiedNameContext exception : exceptions) {
            String exceptionName = exception.getText();
            this.exceptionNameList.add(exceptionName);
            this.exceptionNumber += 1;
        }
        super.enterCatchType(ctx);
    }

    @Override
    public void enterMethodDeclaration(MethodDeclarationContext ctx) {
        // capture exception name and number
        if (ctx.THROWS() != null) {
            List<QualifiedNameContext> exceptions = ctx.qualifiedNameList().qualifiedName();
            for (QualifiedNameContext exception : exceptions) {
                String exceptionName = exception.getText();
                this.exceptionNameList.add(exceptionName);
                this.exceptionNumber += 1;
            }
        }

        // capture function feature
        String functionName = ctx.identifier().getText();
        String functionBody = ctx.getText();
        int functionStartLine = ctx.start.getLine();
        int functionStopLine = ctx.stop.getLine();

        // capture function params
        List<Variable> functionParams = new ArrayList<Variable>();
        // capture function params --- receiver parameter
        if (ctx.formalParameters().receiverParameter() != null) {

        }
        // capture function params --- formal parameter
        if (ctx.formalParameters().formalParameterList() != null) {

        }

        super.enterMethodDeclaration(ctx);
    }

    @Override
    public void enterLambdaExpression(LambdaExpressionContext ctx) {
        this.lambdaFunctionNumber += 1;
        super.enterLambdaExpression(ctx);
    }

    @Override
    public void enterExpression(ExpressionContext ctx) {
        if (ctx.bop != null && ctx.bop.getText() == "?") {
            this.ternaryOperatorNumber += 1;
        }
        super.enterExpression(ctx);
    }

    @Override
    public void enterStatement(StatementContext ctx) {
        if (ctx.IF() != null || ctx.ELSE() != null || ctx.DO() != null || ctx.WHILE() != null || ctx.FOR() != null
                || ctx.SWITCH() != null) {
            this.controlStructureNumber += 1;
        }
        super.enterStatement(ctx);
    }

    @Override
    public void enterLiteral(LiteralContext ctx) {
        this.literalNumber += 1;
        super.enterLiteral(ctx);
    }

    @Override
    public void enterClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
        if (ctx.PUBLIC() != null) {
            this.accessControlCount.put("Public", this.accessControlCount.get("Public") + 1);
        } else if (ctx.PROTECTED() != null) {
            this.accessControlCount.put("Protected", this.accessControlCount.get("Protected") + 1);
        } else if (ctx.PRIVATE() != null) {
            this.accessControlCount.put("Private", this.accessControlCount.get("Private") + 1);
        } else {
            this.accessControlCount.put("Default", this.accessControlCount.get("Default") + 1);
        }
        super.enterClassOrInterfaceModifier(ctx);
    }
}
