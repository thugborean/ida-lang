package io.github.thugborean.parser;

import java.util.List;
import java.util.Set;

import io.github.thugborean.ast.node.NodeAST;
import io.github.thugborean.ast.node.Program;
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

    // Types
    public static final Set<TokenType> nodeTypes = Set.of(TokenType.Number, TokenType.Double, TokenType.String, TokenType.Character, TokenType.Boolean);

    public Program createAST() {
        while(peek() != null) {
            parse();
        }
        return this.program;
    }

    private Token peek(int amount) {
        if (tokens.get(index + amount) != null) return tokens.get(index + amount);
        else return null;

    }

    private Token peek() {
        return peek(0);
    }


    private NodeAST parse() {
        return null;
    }

    // private NodeExpression parseExpression() {
    //     return parseTerm();
    // }

    // private NodeExpression parseTerm() {

    // }

    private NodeStatement parseStatement() {
        if (currentToken().tokenType == TokenType.Number) {
            
        }
        return null;
    }
}