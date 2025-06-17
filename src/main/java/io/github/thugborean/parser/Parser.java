package io.github.thugborean.parser;

import java.util.List;

import io.github.thugborean.ast.node.NodeAST;
import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.node.statement.NodeStatement;
import io.github.thugborean.syntax.Token;
import io.github.thugborean.syntax.TokenType;

public class Parser {
    private List<Token> tokens;
    private Program program;
    private int index;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.program = new Program();
        this.index = 0;

    }

    public Program createAST() {
        while(currentToken() != null) {
            parse();
        }
        return this.program;
    }

    private Token peek(int amount) {
        if (tokens.get(index + amount) != null) return tokens.get(index + amount);
        else return null;

    }

    private Token peek() {
        return peek(1);
    }

    // Returns current token to which index points to
    private Token currentToken() {
        if(tokens.get(index) != null) return tokens.get(index);
        else return null;

    }

    private NodeAST parse() {
        return null;
    }

    private NodeExpression parseExpression() {
        return null;
    }

    private NodeStatement parseStatement() {
        if (currentToken().tokenType == TokenType.Number) {
            
        }
        return null;
    }


}
