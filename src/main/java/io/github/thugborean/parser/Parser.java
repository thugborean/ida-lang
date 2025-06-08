package io.github.thugborean.parser;

import java.util.List;

import io.github.thugborean.syntax.Token;
import io.github.thugborean.ast.AST;

public class Parser {
    private List<Token> tokens;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public AST createAST() {
        AST ast = new AST();
        return ast;
    }


}
