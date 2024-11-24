package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TokenInfo {
    private String lexeme;
    private String lexicalComp;
    private int line;
    private int column;

    // Constructor
    public TokenInfo(String lexeme, String lexicalComp, int line, int column) {
        this.lexeme = lexeme;
        this.lexicalComp = lexicalComp;
        this.line = line;
        this.column = column;
    }

    // Getters and Setters
    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public String getLexicalComp() {
        return lexicalComp;
    }

    public void setLexicalComp(String lexicalComp) {
        this.lexicalComp = lexicalComp;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
