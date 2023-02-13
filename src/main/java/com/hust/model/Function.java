package com.hust.model;

import java.util.ArrayList;
import java.util.List;

public class Function {
    public String name;
    public String body;
    public int startLine;
    public int endLine;
    public List<Variable> params;
    public List<Variable> localVariables;
    public List<FunctionCall> functionCalls;

    public Function(String name, String body, int startLine, int endLine) {
        this.name = name;
        this.body = body;
        this.startLine = startLine;
        this.endLine = endLine;
        params = new ArrayList<Variable>();
        localVariables = new ArrayList<Variable>();
        functionCalls = new ArrayList<FunctionCall>();
    }
}
