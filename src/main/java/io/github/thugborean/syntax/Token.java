package io.github.thugborean.syntax;

public class Token {
    public final TokenType tokenType; // The type of token
    public final Object literal; // A literal value of the token
    public final String lexeme; // A string of the source code
    public final int line; // What line it appears

    public Token(TokenType tokenType, Object literal, String lexeme, int line) {
        this.tokenType = tokenType;
        this.literal = literal;
        this.lexeme = lexeme;
        this.line = line;
    }

    public String toString() {
        return String.format("TokenType: %-20s, Value: %-20s, Lexeme: %-20s, Line: %-5s", tokenType, literal, lexeme, line);
    }
}

