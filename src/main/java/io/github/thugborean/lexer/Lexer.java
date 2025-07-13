package io.github.thugborean.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.thugborean.syntax.Token;
import io.github.thugborean.syntax.TokenType;

public class Lexer {
    private int index = 0;
    private int line = 1;
    private List<Token> tokens = new ArrayList<>();

    // What is to be lexed
    private String source;

    public static final Map<String, TokenType> keywords = Map.ofEntries(
            // Literals
            Map.entry("null", TokenType.NullLiteral), // Evaluates to a null literal
            Map.entry("true", TokenType.True), // Evaluates to a boolean literal
            Map.entry("false", TokenType.False), // Evaluates to a boolean literal

            // Variables
            Map.entry("num", TokenType.Number),
            Map.entry("double", TokenType.Double),
            Map.entry("string", TokenType.String),
            Map.entry("char", TokenType.Character),
            Map.entry("bool", TokenType.Boolean),

            // Functions and structures
            Map.entry("func", TokenType.Function),
            Map.entry("void", TokenType.Void),
            Map.entry("struct", TokenType.Structure),

            Map.entry("print", TokenType.Print),

            // Loops and control flow
            Map.entry("if", TokenType.If),
            Map.entry("while", TokenType.While),
            Map.entry("for", TokenType.For),
            Map.entry("do", TokenType.Do),

            // Visibility WIP
            Map.entry("export", TokenType.Export),
            Map.entry("global", TokenType.Global),
            Map.entry("hidden", TokenType.Hidden));

    public static final Map<String, TokenType> operators = Map.ofEntries(
            // Operators
            Map.entry("=", TokenType.Assign),
            Map.entry("+", TokenType.Plus),
            Map.entry("-", TokenType.Minus),
            Map.entry("*", TokenType.Multiply),
            Map.entry("/", TokenType.Divide),
            Map.entry("%", TokenType.Modulo),

            Map.entry("<", TokenType.LessThan),
            Map.entry(">", TokenType.GreaterThan),

            // Two-character operators
            Map.entry("++", TokenType.PlusPlus),
            Map.entry("--", TokenType.MinusMinus),
            Map.entry("**", TokenType.AtseriskAsterisk),

            Map.entry("==", TokenType.EqualsEquals),
            Map.entry("!=", TokenType.NotEquals),
            Map.entry("<=", TokenType.LessThanOrEquals),
            Map.entry(">=", TokenType.GreaterThanOrEquals),

            Map.entry("+=", TokenType.Append),
            Map.entry("-=", TokenType.Truncate),

            Map.entry("->", TokenType.Return));

    public static final Map<String, TokenType> scopes = Map.ofEntries(
            // Scope
            Map.entry("[", TokenType.BracketOpen),
            Map.entry("]", TokenType.BracketClosed),
            Map.entry("(", TokenType.ParenthesesOpen),
            Map.entry(")", TokenType.ParenthesesClosed),
            Map.entry("{", TokenType.CurlyOpen),
            Map.entry("}", TokenType.CurlyClosed),
            Map.entry(";", TokenType.SemiColon),
            Map.entry(".", TokenType.Dot),
            Map.entry(",", TokenType.Comma));

    public List<Token> tokenize(String source) {
        this.tokens.clear();
        this.index = 0;
        this.line = 1;
        this.source = source;
        // Replace windows-style newlines with unix-based newlines
        source.replace("\r\n", "\n");

        // Create a new empty ArrayList of tokens for which to return
        this.tokens = new ArrayList<>();

        // Loop until index exceeds the source.length()
        while (index < source.length()) {
            // KEYWORD LOGIC
            // ------------------------------------------------------------------------------
            if (Character.isAlphabetic(peek())) {
                StringBuilder subString = new StringBuilder();
                do {
                    subString.append(peek()); // Add the current char to the subString if it is alphanumerical
                    incrementIndex();
                } while (Character.isLetterOrDigit(peek()));
                tokens.add(evaluateKeyword(subString.toString())); // If it's not a keyword, it is an identifier
                // LITERAL LOGIC
                // ------------------------------------------------------------------------------
                // NUMERIC LITERAL & DOUBLE LITERAL
            } else if (Character.isDigit(peek())) {
                StringBuilder subString = new StringBuilder();
                do {
                    subString.append(peek()); // Add the current char to the subString if it is numerical
                    incrementIndex();
                    // Check if the next character is alphabetical
                    if (Character.isAlphabetic(peek(1)))
                        throw new RuntimeException(
                                "Lexer Error: No alphabetical characters allowed in a numeric literal!"); // WIP
                                                                                                          // placeholder
                                                                                                          // Exception
                } while (Character.isDigit(peek()));
                // If it's a double we need to keep reading
                if (peek() == '.') {
                    do {
                        subString.append(peek());
                        incrementIndex();
                        if (Character.isAlphabetic(peek()))
                            throw new RuntimeException(
                                    "Lexer Error: No alphabetical characters allowed in a double literal!");
                    } while (Character.isDigit(peek()));
                    tokens.add(evaluateDoubleLiteral(subString.toString()));
                } else
                    tokens.add(evaluateNumericLiteral(subString.toString()));
                // Returns the literal token WIP!! For now only numerical
                // STRING LITERAL
            } else if (peek() == '\"') {
                StringBuilder subString = new StringBuilder();
                do {
                    subString.append(peek());
                    incrementIndex();
                } while (peek() != '\"');
                // Bad solution, but works for now
                subString.append(peek());
                incrementIndex();
                tokens.add(evaluateStringLiteral(subString.toString()));
                // CHARACTER LITERAL
            } else if (peek() == '\'') {
                StringBuilder subString = new StringBuilder();
                // Get the first '\''
                subString.append(peek());
                incrementIndex();
                // Get the character
                subString.append(peek());
                incrementIndex();
                // Do some checks
                if (Character.isAlphabetic(peek()))
                    throw new RuntimeException("Lexer Error: Only one character allowed in a char");
                else if (peek() != '\'')
                    throw new RuntimeException("Lexer Error: Character literal was not closed properly");
                subString.append(peek());
                incrementIndex();
                tokens.add(evaluateCharacterLiteral(subString.toString()));
                // OPERATOR LOGIC
                // ------------------------------------------------------------------------------
            } else if (operators.containsKey(Character.toString(peek()))) {
                StringBuilder subString = new StringBuilder();
                do {
                    subString.append(peek());
                    incrementIndex();
                } while (operators.containsKey(Character.toString(peek())));
                tokens.add(evaluateOperator(subString.toString()));
                // SCOPE LOGIC
                // ---------------------------------------------------------------------------------
            } else if (scopes.containsKey(Character.toString(peek()))) {
                tokens.add(evaluateScope(Character.toString(peek())));
                incrementIndex();
                // WHITESPACE AND NEWLINE LOGIC
                // ----------------------------------------------------------------
            } else if (peek() == ' ') {
                incrementIndex();
            } else if (peek() == '\n') {
                line++;
                incrementIndex();
                // If all else fails just throw and error and quit, WIP
            } else
                throw new RuntimeException("Lexer Error: Unexpected symbol during lexing: " + peek());
        }
        // Add EOF token
        tokens.add(new Token(TokenType.EOF, null, "This is it! This is it!", line)); // The Khayembii Communique - The Death Of An Aspiring Icon
        return tokens;
    }

    // Increments index by amount
    private void incrementIndex(int amount) {
        index += amount;
    }

    private void incrementIndex() {
        incrementIndex(1);
    }

    // Peek ahead by amount and return the character
    private char peek(int amount) {
        if (index + amount < source.length())
            return source.charAt(index + amount);
        else
            return '\0';
    }

    private char peek() {
        return peek(0);
    }

    private Token evaluateKeyword(String token) {
        // Check if keyword exists, if so return its Token, else return an identifier Token
        if (keywords.containsKey(token))
            return new Token(keywords.get(token), null, token, line);
        else
            return new Token(TokenType.Identifier, null, token, line);
    }

    private Token evaluateNumericLiteral(String literal) {
        return new Token(TokenType.NumericLiteral, Integer.parseInt(literal), literal, line);
    }

    private Token evaluateDoubleLiteral(String literal) {
        return new Token(TokenType.DoubleLiteral, Double.parseDouble(literal), literal, line);
    }

    private Token evaluateStringLiteral(String literal) {
        return new Token(TokenType.StringLiteral, literal, literal, line);
    }

    private Token evaluateCharacterLiteral(String literal) {
        if (literal.length() > 3)
            throw new RuntimeException("Lexer Error: Only one character allowed in character literal!");
        return new Token(TokenType.CharacterLiteral, literal, literal, line);
    }

    private Token evaluateOperator(String operator) {
        if (operators.containsKey(operator))
            return new Token(operators.get(operator), null, operator, line);
        else
            throw new RuntimeException("Unknown Operator!");
    }

    private Token evaluateScope(String scope) {
        if (scopes.containsKey(scope))
            return new Token(scopes.get(scope), null, scope, line);
        else
            throw new RuntimeException("Unknown Symbol: " + scope + "!");
    }

    // Returns a string of all the lexed tokens
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Token token : tokens) {
            stringBuilder.append(token.toString() + '\n');
        }
        return stringBuilder.toString();
    }
}