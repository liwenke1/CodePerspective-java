package com.hust.model;

public class Variable {
    public String name;
    public int startLine;
    public int endLine;
    public String type;

    public Variable(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Variable(String name, String type, int startLine, int endLine) {
        this.name = name;
        this.type = type;
        this.startLine = startLine;
        this.endLine = endLine;
    }
}
