package io.github.thugborean.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.thugborean.syntax.Token;
import io.github.thugborean.syntax.TokenType;

public class Lexer {
    private int index;
    private int line;
    private String source;

    private List<Token> tokens;

    public Lexer() {
        this.tokens = new ArrayList<>();
        this.index = 0;
        this.line = 1;
    }

    public static final Map<String, TokenType> keywords = Map.ofEntries(
            // Literals
            Map.entry("null", TokenType.NullLiteral), // Evaluates to a null literal
            Map.entry("true", TokenType.True), // Evaluates to a bool literal
            Map.entry("false", TokenType.False), // Evaluates to a bool literal

            // Variables
            Map.entry("num", TokenType.Number),
            Map.entry("double", TokenType.Double),
            Map.entry("string", TokenType.String),
            Map.entry("char", TokenType.Character),
            Map.entry("bool", TokenType.Bool),

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

            // Visibility
            Map.entry("export", TokenType.Export),
            Map.entry("global", TokenType.Global),
            Map.entry("hidden", TokenType.Hidden)
            );

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

            Map.entry("->", TokenType.Return)
            );

            public static final Map<String, TokenType> scopes = Map.ofEntries(
            // Scope
            Map.entry("[", TokenType.OpenBracket),
            Map.entry("]", TokenType.ClosedBracket),
            Map.entry("(", TokenType.OpenParenthesis),
            Map.entry(")", TokenType.CloseParenthesis),
            Map.entry("{", TokenType.OpenCurly),
            Map.entry("}", TokenType.ClosedCurly),
            Map.entry(";", TokenType.SemiColon),
            Map.entry(".", TokenType.Dot) // Used to access members of a struct
            );

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

            // KEYWORD LOGIC ------------------------------------------------------------------------------
            if (Character.isAlphabetic(currentChar())) {
                StringBuilder subString = new StringBuilder();

                do {
                    subString.append(currentChar()); // Add the current char to the subString if it is alphanumerical
                    incrementIndex();
                } while (Character.isLetterOrDigit(currentChar()));

                tokens.add(evaluateKeyword(subString.toString())); // If it's not a keyword, it is an identifier

            // LITERAL LOGIC ------------------------------------------------------------------------------
            // NUMERIC LITERAL
            } else if (Character.isDigit(currentChar())) {
                StringBuilder subString = new StringBuilder();

                do {
                    subString.append(currentChar());  // Add the current char to the subString if it is numerical
                    incrementIndex();

                    // Check if the next character is alphabetical
                    if(Character.isAlphabetic(peek()))
                        throw new RuntimeException("Lexer Error: No alphabetical characters allowed in a numeric literal!"); // WIP placeholder Exception
                } while (Character.isDigit(currentChar()));

                tokens.add(evaluateNumericLiteral(subString.toString())); // Returns the literal token WIP!! For now only numerical
            // STRING LITERAL
            } else if(currentChar() == '\"') {
                StringBuilder subString = new StringBuilder();
                do {
                    subString.append(currentChar());
                    incrementIndex();
                } while(currentChar() != '\"');

                // Bad solution, but works for now
                subString.append(currentChar());
                incrementIndex();

                tokens.add(evaluateStringLiteral(subString.toString()));
            // OPERATOR LOGIC ------------------------------------------------------------------------------
            } else if (operators.containsKey(Character.toString(currentChar()))) {
                StringBuilder subString = new StringBuilder();

                do {
                    subString.append(currentChar()); 
                    incrementIndex();
                } while (operators.containsKey(Character.toString(currentChar())));
                
                tokens.add(evaluateOperator(subString.toString()));
            
            // SCOPE LOGIC ---------------------------------------------------------------------------------
            } else if(scopes.containsKey(Character.toString(currentChar()))) {
                tokens.add(evaluateScope(Character.toString(currentChar())));
                incrementIndex();
            // WHITESPACE AND NEWLINE LOGIC ----------------------------------------------------------------
            } else if(currentChar() == ' ') {
                incrementIndex();

            } else if(currentChar() == '\n') {
                line++;
                incrementIndex();
            // If all else fails just throw and error and quit, WIP
            } else throw new RuntimeException("Lexer Error: Unexpected symbol during lexing: " + currentChar()); // WIP placeholder Exception
        }

        // Add EOF token
        tokens.add(new Token(TokenType.EOF, null, null, line));
        return tokens;
    }

    // Returns the current char for which the index points to
    private char currentChar() {
        if (index < source.length())
            return source.charAt(index);
        else return '\0';
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
        if(index + amount < source.length()) return source.charAt(index + amount);
            else return '\0';
    }
    private char peek() {
        return peek(1);
    }

    private Token evaluateKeyword(String token) {
        // Check if keyword exists, if so return its Token, else return an identifier Token
        if(keywords.containsKey(token)) return new Token(keywords.get(token), null, token, line);
            else return new Token(TokenType.Identifier, null, token, line);
    }

    private Token evaluateNumericLiteral(String literal) {
        return new Token(TokenType.NumericLiteral, Integer.parseInt(literal), literal, line);
    }
    // WIP
    private Token evaluateDoubleLiteral(String literal) {
        return new Token(TokenType.DoubleLiteral, Double.parseDouble(literal), literal, line);
    }

    private Token evaluateStringLiteral(String literal) {
        return new Token(TokenType.StringLiteral, literal, literal, line);
    }
    // WIP
    private Token evaluateCharLiteral(String literal) {
        return new Token(TokenType.CharLiteral, literal, literal, line);
    }

    private Token evaluateOperator(String operator) {
        if(operators.containsKey(operator)) return new Token(operators.get(operator), null, operator, line);
            else throw new RuntimeException("Unknown Operator!");
    }

    private Token evaluateScope(String scope) {
        if(scopes.containsKey(scope)) return new Token(scopes.get(scope), null, scope, line);
            else throw new RuntimeException("Unknown Symbol: " + scope + "!");
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