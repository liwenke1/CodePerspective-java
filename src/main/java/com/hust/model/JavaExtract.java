package com.hust.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.hust.antlr.JavaParserBaseListener;
import com.hust.antlr.JavaParser.CatchTypeContext;
import com.hust.antlr.JavaParser.ClassDeclarationContext;
import com.hust.antlr.JavaParser.ClassOrInterfaceModifierContext;
import com.hust.antlr.JavaParser.ConstructorDeclarationContext;
import com.hust.antlr.JavaParser.ExpressionContext;
import com.hust.antlr.JavaParser.FieldDeclarationContext;
import com.hust.antlr.JavaParser.FormalParameterContext;
import com.hust.antlr.JavaParser.FormalParameterListContext;
import com.hust.antlr.JavaParser.IdentifierContext;
import com.hust.antlr.JavaParser.ImportDeclarationContext;
import com.hust.antlr.JavaParser.InterfaceCommonBodyDeclarationContext;
import com.hust.antlr.JavaParser.LambdaExpressionContext;
import com.hust.antlr.JavaParser.LastFormalParameterContext;
import com.hust.antlr.JavaParser.LiteralContext;
import com.hust.antlr.JavaParser.LocalVariableDeclarationContext;
import com.hust.antlr.JavaParser.MethodCallContext;
import com.hust.antlr.JavaParser.MethodDeclarationContext;
import com.hust.antlr.JavaParser.PackageDeclarationContext;
import com.hust.antlr.JavaParser.QualifiedNameContext;
import com.hust.antlr.JavaParser.ReceiverParameterContext;
import com.hust.antlr.JavaParser.StatementContext;
import com.hust.antlr.JavaParser.VariableDeclaratorContext;

public class JavaExtract extends JavaParserBaseListener {

    // function based features
    public int functionNumber;
    public List<Function> functionList;
    public int lambdaFunctionNumber;

    // class based features
    public List<String> classNameList;
    public int classNumber;
    public List<String> classVariableNameList;
    public int classVariableNumber;

    // quote
    public List<String> importNameList;
    public int importNumber;

    // code style
    public List<String> exceptionNameList;
    public int exceptionNumber;
    public List<String> packageNameList;
    public int packageNumber;
    public int ternaryOperatorNumber;
    public int controlStructureNumber;
    public int literalNumber;
    public int nestingDepth;
    private int nestingLocalDepth;
    public List<Integer> branchingNumberList;
    public Map<String, Integer> accessControlCount;

    // AST based features
    public List<Integer> typeNodeDepth;
    public List<Integer> leafNodeDepth;
    public Map<String, Double> typeNodeFrequency;
    public Map<String, Double> leafNodeFrequency;
    public Map<String, Double> keywordFrequency;

    public JavaExtract() {
        // function based features
        functionNumber = 0;
        functionList = new ArrayList<Function>();
        lambdaFunctionNumber = 0;

        // class based features
        classNameList = new ArrayList<String>();
        classNumber = 0;
        classVariableNameList = new ArrayList<String>();
        classVariableNumber = 0;

        // quote
        importNameList = new ArrayList<String>();
        importNumber = 0;

        // code style
        exceptionNameList = new ArrayList<String>();
        exceptionNumber = 0;
        packageNameList = new ArrayList<String>();
        packageNumber = 0;
        ternaryOperatorNumber = 0;
        controlStructureNumber = 0;
        literalNumber = 0;
        nestingDepth = 0;
        nestingLocalDepth = 0;

        accessControlCount = new HashMap<String, Integer>();
        accessControlCount.put("Default", 0);
        accessControlCount.put("Public", 0);
        accessControlCount.put("Protected", 0);
        accessControlCount.put("Private", 0);

        typeNodeDepth = new ArrayList<Integer>();
        leafNodeDepth = new ArrayList<Integer>();
        typeNodeFrequency = new HashMap<String, Double>();
        leafNodeFrequency = new HashMap<String, Double>();
        keywordFrequency = new HashMap<String, Double>();
    }

    @Override
    public void enterPackageDeclaration(PackageDeclarationContext ctx) {
        String packageName = ctx.qualifiedName().getText();
        packageNameList.add(packageName);
        packageNumber += 1;
        super.enterPackageDeclaration(ctx);
    }

    @Override
    public void enterImportDeclaration(ImportDeclarationContext ctx) {
        String importName = ctx.qualifiedName().getText();
        importNameList.add(importName);
        importNumber += 1;
        super.enterImportDeclaration(ctx);
    }

    @Override
    public void enterClassDeclaration(ClassDeclarationContext ctx) {
        String className = ctx.identifier().getText();
        classNameList.add(className);
        classNumber += 1;
        super.enterClassDeclaration(ctx);
    }

    @Override
    public void enterCatchType(CatchTypeContext ctx) {
        List<QualifiedNameContext> exceptionList = ctx.qualifiedName();
        for (QualifiedNameContext exception : exceptionList) {
            String exceptionName = exception.getText();
            exceptionNameList.add(exceptionName);
            exceptionNumber += 1;
        }
        super.enterCatchType(ctx);
    }

    @Override
    public void enterMethodDeclaration(MethodDeclarationContext ctx) {
        // capture exception name and number
        if (ctx.THROWS() != null) {
            List<QualifiedNameContext> exceptionList = ctx.qualifiedNameList().qualifiedName();
            for (QualifiedNameContext exception : exceptionList) {
                String exceptionName = exception.getText();
                exceptionNameList.add(exceptionName);
                exceptionNumber += 1;
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
            ReceiverParameterContext receiverParams = ctx.formalParameters().receiverParameter();
            String typeName = receiverParams.typeType().getText();
            List<IdentifierContext> params = receiverParams.identifier();
            for (IdentifierContext param : params) {
                functionParams.add(new Variable(typeName, param.getText()));
            }
        }
        // capture function params --- formal parameter
        if (ctx.formalParameters().formalParameterList() != null) {
            FormalParameterListContext formalParamList = ctx.formalParameters().formalParameterList();
            if (formalParamList.lastFormalParameter() != null) {
                LastFormalParameterContext lastFormalParam = formalParamList.lastFormalParameter();
                functionParams.add(new Variable(lastFormalParam.typeType().getText(),
                        lastFormalParam.variableDeclaratorId().getText()));
            }
            if (formalParamList.formalParameter() != null) {
                List<FormalParameterContext> formalParams = formalParamList.formalParameter();
                for (FormalParameterContext formalParam : formalParams) {
                    functionParams.add(new Variable(formalParam.typeType().getText(),
                            formalParam.variableDeclaratorId().getText()));
                }
            }
        }

        // add function list
        functionList
                .add(new Function(functionName, functionBody, functionStartLine, functionStopLine, functionParams));
        functionNumber += 1;
        super.enterMethodDeclaration(ctx);
    }

    @Override
    public void enterInterfaceCommonBodyDeclaration(InterfaceCommonBodyDeclarationContext ctx) {
        if (ctx.THROWS() != null) {
            List<QualifiedNameContext> exceptionList = ctx.qualifiedNameList().qualifiedName();
            for (QualifiedNameContext exception : exceptionList) {
                String exceptionName = exception.getText();
                exceptionNameList.add(exceptionName);
                exceptionNumber += 1;
            }
        }
        super.enterInterfaceCommonBodyDeclaration(ctx);
    }

    @Override
    public void enterConstructorDeclaration(ConstructorDeclarationContext ctx) {
        if (ctx.THROWS() != null) {
            List<QualifiedNameContext> exceptionList = ctx.qualifiedNameList().qualifiedName();
            for (QualifiedNameContext exception : exceptionList) {
                String exceptionName = exception.getText();
                exceptionNameList.add(exceptionName);
                exceptionNumber += 1;
            }
        }
        super.enterConstructorDeclaration(ctx);
    }

    @Override
    public void enterFieldDeclaration(FieldDeclarationContext ctx) {
        List<VariableDeclaratorContext> variableList = ctx.variableDeclarators().variableDeclarator();
        for (VariableDeclaratorContext variable : variableList) {
            classNameList.add(variable.variableDeclaratorId().getText());
            classNumber += 1;
        }
        super.enterFieldDeclaration(ctx);
    }

    @Override
    public void enterLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
        String typeName = ctx.typeType().getText();
        if (functionList.size() != 0) {
            if (ctx.identifier() != null) {
                IdentifierContext identifier = ctx.identifier();
                functionList.get(functionList.size() - 1).localVariables
                        .add(new Variable(identifier.getText(),
                                typeName, identifier.start.getLine(), identifier.start.getCharPositionInLine()));
            } else {
                List<VariableDeclaratorContext> variableList = ctx.variableDeclarators().variableDeclarator();
                for (VariableDeclaratorContext variable : variableList) {
                    functionList.get(functionList.size() - 1).localVariables
                            .add(new Variable(variable.variableDeclaratorId().getText(), typeName,
                                    variable.variableDeclaratorId().start.getLine(),
                                    variable.variableDeclaratorId().start.getCharPositionInLine()));
                }
            }
        }
        super.enterLocalVariableDeclaration(ctx);
    }

    @Override
    public void enterMethodCall(MethodCallContext ctx) {
        int functionCallLine = ctx.start.getLine();
        int functionCallColumn = ctx.start.getCharPositionInLine();
        String functionCallName = new String("");
        if (ctx.identifier() != null) {
            functionCallName = ctx.identifier().getText();
        }
        if (functionList.size() != 0
                && functionCallLine >= functionList.get(functionList.size() - 1).startLine
                && functionCallLine <= functionList.get(functionList.size() - 1).endLine) {
            functionList.get(functionList.size() - 1).functionCalls
                    .add(new FunctionCall(functionCallName, functionCallLine, functionCallColumn));
        }
        super.enterMethodCall(ctx);
    }

    @Override
    public void enterLambdaExpression(LambdaExpressionContext ctx) {
        lambdaFunctionNumber += 1;
        super.enterLambdaExpression(ctx);
    }

    @Override
    public void enterExpression(ExpressionContext ctx) {
        if (ctx.bop != null && ctx.bop.getText() == "?") {
            ternaryOperatorNumber += 1;
        }
        super.enterExpression(ctx);
    }

    @Override
    public void enterStatement(StatementContext ctx) {
        if (ctx.IF() != null || ctx.ELSE() != null || ctx.DO() != null || ctx.WHILE() != null || ctx.FOR() != null
                || ctx.SWITCH() != null) {
            controlStructureNumber += 1;

            // record current nesting depth and refresh nestingDepth
            nestingLocalDepth += 1;
            if (nestingLocalDepth > nestingDepth) {
                nestingDepth = nestingLocalDepth;
            }

            // record the number of subtree of control or loop statement
            branchingNumberList.add(ctx.getChildCount());
        }
        super.enterStatement(ctx);
    }

    @Override
    public void exitStatement(StatementContext ctx) {
        // record current nesting depth
        nestingLocalDepth -= 1;
        super.exitStatement(ctx);
    }

    @Override
    public void enterLiteral(LiteralContext ctx) {
        literalNumber += 1;
        super.enterLiteral(ctx);
    }

    @Override
    public void enterClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
        if (ctx.PUBLIC() != null) {
            accessControlCount.put("Public", accessControlCount.get("Public") + 1);
        } else if (ctx.PROTECTED() != null) {
            accessControlCount.put("Protected", accessControlCount.get("Protected") + 1);
        } else if (ctx.PRIVATE() != null) {
            accessControlCount.put("Private", accessControlCount.get("Private") + 1);
        } else {
            accessControlCount.put("Default", accessControlCount.get("Default") + 1);
        }
        super.enterClassOrInterfaceModifier(ctx);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        // calculate node depth, including type node and leaf node
        typeNodeDepth.add(ctx.depth());
        for (ParseTree child : ctx.children) {
            if (child instanceof TerminalNode) {
                leafNodeDepth.add(ctx.depth() + 1);
            }
        }

        // calculate type node frequency
        String type = String.valueOf(ctx.getRuleIndex());
        if (typeNodeFrequency.containsKey(type)) {
            typeNodeFrequency.put(type, typeNodeFrequency.get(type) + 1);
        } else {
            typeNodeFrequency.put(type, Double.valueOf(1));
        }
        super.enterEveryRule(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        int type = node.getSymbol().getType();
        String text = node.getText();

        // calculate java keyword frequency
        if (type >= 1 && type <= 66) {
            if (keywordFrequency.containsKey(text)) {
                keywordFrequency.put(text, keywordFrequency.get(text) + 1);
            } else {
                keywordFrequency.put(text, Double.valueOf(1));
            }
        }

        // calculate leaf node frequency
        if (leafNodeFrequency.containsKey(text)) {
            leafNodeFrequency.put(text, leafNodeFrequency.get(text) + 1);
        } else {
            leafNodeFrequency.put(text, Double.valueOf(1));
        }
        super.visitTerminal(node);
    }
}
