package io.github.thugborean.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.logging.LoggingManager;
import io.github.thugborean.vm.symbol.*;
import io.github.thugborean.syntax.*;

public class Parser {
    private final static Logger logger = LoggingManager.getLogger(Parser.class);
    public List<Token> tokens;
    private int index = 0;
    private int curlyDepth = 0;
    private boolean insideFunction = false;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private static final Map<TokenType, ValType> variableTypes = Map.ofEntries(
            Map.entry(TokenType.Number, ValType.NUMBER),
            Map.entry(TokenType.Double, ValType.DOUBLE),
            Map.entry(TokenType.String, ValType.STRING),
            Map.entry(TokenType.Boolean, ValType.BOOLEAN)
    );

    private static final Set<TokenType> modifiers = Set.of(
        TokenType.Final,
        TokenType.Public,
        TokenType.Private
    );

    private static final Set<TokenType> expressionTokens = Set.of(
        TokenType.Identifier,
        TokenType.NumericLiteral,
        TokenType.DoubleLiteral,
        TokenType.CharacterLiteral,
        TokenType.StringLiteral,
        TokenType.BooleanLiteral,
        TokenType.True,
        TokenType.False,

        TokenType.Plus,
        TokenType.PlusPlus,
        TokenType.Minus,
        TokenType.MinusMinus,
        TokenType.Multiply,
        TokenType.Divide,
        TokenType.Modulo,
        TokenType.AtseriskAsterisk,

        TokenType.And,
        TokenType.Or,
        TokenType.EqualsEquals,
        TokenType.BangEquals,
        TokenType.Bang,
        TokenType.LessThan,
        TokenType.LessThanOrEquals,
        TokenType.GreaterThan,
        TokenType.GreaterThanOrEquals,

        TokenType.Comma,
        TokenType.Dot,

        TokenType.ParenthesesOpen,
        TokenType.ParenthesesClosed,

        TokenType.NullLiteral,

        TokenType.Assign
    );
/* 
    private static final Set<TokenType> atoms = Set.of(
        TokenType.NullLiteral,
        TokenType.NumericLiteral,
        TokenType.DoubleLiteral,
        TokenType.StringLiteral,
        TokenType.Identifier
    );

    private static final Set<TokenType> operators = Set.of(
        TokenType.Plus, // +
        TokenType.Minus,// -
        TokenType.Multiply, // *
        TokenType.Divide, // /
        TokenType.Modulo, // %

        TokenType.AtseriskAsterisk, // ²
        TokenType.PlusPlus, // ++
        TokenType.MinusMinus, // --

        TokenType.LessThan, // <
        TokenType.LessThanOrEquals, // <=
        TokenType.GreaterThan, // >
        TokenType.GreaterThanOrEquals, // >=
        TokenType.EqualsEquals, // ==
        TokenType.BangEquals, // !=

        TokenType.Bang // !
    );
*/
    public Program parseProgram() {
        logger.info("Parsing program...");
        this.index = 0;
        List<NodeStatement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(parseStatement());
        }
        if(curlyDepth != 0) throw new RuntimeException("Parser Error: Mismatched curly-braces!");
        curlyDepth = 0;
        logger.info("Finished parsing program!");
        return new Program(statements);
    }

    private NodeStatement parseStatement() {
        if(match(TokenType.If)) return parseIfStatement();
        if(match(TokenType.While)) return parseWhileStatement();
        if(match(TokenType.Number, TokenType.Double, TokenType.String, TokenType.Boolean)) return parseVariableDeclaration();
        if(match(TokenType.Print)) return parsePrintStatement();
        if(match(TokenType.CurlyOpen)) return parseBlock();
        if(match(TokenType.Function, TokenType.Public, TokenType.Private)) return parseFunctionDeclaration();
        if(match(TokenType.Return)) return parseReturnStatement();
        return parseExpressionStatement();
    }

    private BindingPair getInfixBindingPower(Token operator) {
        return switch (operator.tokenType) {
            case Assign -> {
                yield new BindingPair(0.1f, 0.0f);
            }
            case LessThan, GreaterThan, LessThanOrEquals, GreaterThanOrEquals, EqualsEquals -> {
                yield new BindingPair(1.1f, 1.0f);
            }
            case And -> {
                yield new BindingPair(2.1f, 2.0f);
            }
            case Plus, Minus -> {
                yield new BindingPair(3.1f, 3.0f);
            }
            case Multiply, Divide, Modulo -> {
                yield new BindingPair(4.1f, 4.0f);
            }
            case AtseriskAsterisk -> {
                yield new BindingPair(5.1f, 5.0f);
            }
            case PlusPlus -> {
                yield new BindingPair(5.1f, 5.0f);
            }
            case MinusMinus -> {
                yield new BindingPair(5.1f, 5.0f);
            }
            case ParenthesesOpen -> {
                yield new BindingPair(9.1f, 0f);
            }
            default -> {
                yield new BindingPair(0.0f, 0.0f);
            }
        };
    }

    private BindingPair getPrefixBindingPower(Token operator) {
        if(operator.tokenType == TokenType.Minus) return new BindingPair(7.1f, 7.0f);
        
        return new BindingPair(0f, 0f);
    }

    private NodeExpression pratt(float minumumBindingPower) {
        if (!expressionTokens.contains(peek().tokenType))
            return null;

        Token token = advance();

        isExpressionToken(token); // Useless?
        NodeExpression left = parsePrefix(token);

        while (true) {
            Token next = peek();
            float leftBindingPower = getInfixBindingPower(next).left;

            if (leftBindingPower <= minumumBindingPower) break;

            Token operator = advance();
            left = parseInfix(left, operator);
        }
        
        return left;
    }

    private NodeExpression parsePrefix(Token token) {
        switch(token.tokenType) {
            case NumericLiteral: {
                return new NodeNumericLiteral(token);
            }
            case DoubleLiteral: {
                return new NodeDoubleLiteral(token);
            }
            case StringLiteral: {
                return new NodeStringLiteral(token);
            }
            case BooleanLiteral: {
                return new NodeBooleanLiteral(token);
            }
            case NullLiteral: {
                return new NodeNullLiteral(token);
            }
            case Identifier: {
                return new NodeVariableReference(token.lexeme);
            }
            case Minus: {
                return new NodeUnaryExpression(token, pratt(getPrefixBindingPower(token).left));
            }
            case AtseriskAsterisk: {
                return new NodeUnaryExpression(token, pratt(getInfixBindingPower(token).left));
            }
            case ParenthesesOpen: {
                NodeExpression expr = pratt(0);
                consume(TokenType.ParenthesesClosed, "Missing ')'");
                return expr;
            }
            default: {
                logger.severe("Unrecognized token: " + token.lexeme + " in expression!");
                throw new RuntimeException("Unrecognized token: " + token.lexeme + " in expression!");
            }
        }
    }

    // Add ++ and -- !
    private NodeExpression parseInfix(NodeExpression left, Token operator) {
    // function call
    if (operator.tokenType == TokenType.ParenthesesOpen) {
        NodeFunctionCall functionCall = new NodeFunctionCall();
        functionCall.identifier = left.toString();

        if (peek().tokenType != TokenType.ParenthesesClosed) {
            do {
                functionCall.arguments.add(pratt(0));
            } while (match(TokenType.Comma) && advance() != null);
        }

        consume(TokenType.ParenthesesClosed, "Missing ')'");
        return functionCall;
    }

    // binary expr
    NodeBinaryExpression expression = new NodeBinaryExpression();
    expression.leftHandSide = left;
    expression.operator = operator;
    float bindingPower = getInfixBindingPower(operator).right;
    expression.rightHandSide = pratt(bindingPower);
    return expression;
}

    private void isExpressionToken(Token token) {
        if(!expressionTokens.contains(token.tokenType)) {
            logger.severe("Token: " + token.lexeme + " is disallowed in expression!");
            throw new RuntimeException("Token: " + token.lexeme + " is disallowed in expression!");
        }
    }

    private NodeExpressionStatement parseExpressionStatement() {
            NodeExpression expressionStatement = pratt(0);
            endStatement();
            return new NodeExpressionStatement(expressionStatement);
    }

    private NodeVariableDeclaration parseVariableDeclaration() {
        logger.info("Parsing variable declaration");
        Token typeToken = advance();
        ValType declaredType = variableTypes.get(typeToken.tokenType);

        Token identifierToken = consume(TokenType.Identifier, "Identifier missing after type in variable declaration!");
        String identifier = identifierToken.lexeme;

        consume(TokenType.Assign, "Assign operator missing after variable declaration!");
        NodeExpression assignment = null;

        assignment = pratt(0);

        endStatement();
        return new NodeVariableDeclaration(declaredType, identifier, assignment);
    }

    private NodePrintStatement parsePrintStatement() {
        logger.info("Parsing print-statement...");
        consume(TokenType.Print, "Missing print-statement!");

        consume(TokenType.ParenthesesOpen, "Missing '(' on print statement!");
        NodeExpression printable = pratt(0);
        consume(TokenType.ParenthesesClosed, "Missing ')' before expression!");

        endStatement();
        logger.info("Finished parsing print-statement");
        return new NodePrintStatement(printable);
    }

    private NodeIfStatement parseIfStatement() {
        logger.info("Parsing if-statement...");
        consume(TokenType.If, "Missing 'if' token in if-statement!");
        consume(TokenType.ParenthesesOpen, "Missing '(' before expression!");
        NodeExpression condition = pratt(0);
        consume(TokenType.ParenthesesClosed, "Missing ')' before expression!");
        NodeBlock thenBlock;
        if(match(TokenType.CurlyOpen)) thenBlock = parseBlock();
            else {
                logger.severe("Missing '{' in if-statement!");
                throw new RuntimeException("Missing '{' in if-statement!");
            }
        if(match(TokenType.Else)) {
            consume(TokenType.Else, "Missing else block in if-statement!");
            NodeStatement elseBlock = parseStatement();

            logger.info("Finished parsing if-statement");
            return new NodeIfStatement(condition, thenBlock, elseBlock);
        } else {
            logger.info("Finished parsing if-statement");
            return new NodeIfStatement(condition, thenBlock);
        }
    }

    private NodeBlock parseBlock() {
        List<NodeStatement> statements = new ArrayList<>();
        consume(TokenType.CurlyOpen, "Missing '{' in block!");
        while(!match(TokenType.CurlyClosed)) {
            statements.add(parseStatement());
        }
        consume(TokenType.CurlyClosed, "Missing '}' in block!");
        return new NodeBlock(statements);
    }

    private NodeWhileStatement parseWhileStatement() {
        logger.info("Parsing while-statement...");
        consume(TokenType.While, "Missing 'while' in while-statement");
        consume(TokenType.ParenthesesOpen, "Missing '(' in while-statement");
        NodeExpression condition = pratt(0);
        consume(TokenType.ParenthesesClosed, "Missing ')' before expression!");
        NodeBlock thenBlock;

        if(match(TokenType.CurlyOpen)) thenBlock = parseBlock();
            else {
                logger.severe("Missing '{' in while-statement!");
                throw new RuntimeException("Missing '{' in while-statement!");
            }
        logger.info("Finished parsig while-statement");
        return new NodeWhileStatement(condition, thenBlock);
    }

    // If this is called we have either encountered fn, pb, pr
    private NodeFunctionDeclaration parseFunctionDeclaration() {
        logger.info("Parsing function declaration...");
        insideFunction = true;
        // Look for modifiers
        Set<Modifier> functionModifiers = new HashSet<>();
        while(modifiers.contains(peek().tokenType)) {
            functionModifiers.add(addModifier(advance().tokenType));
        }
        // Check if modifiers have both public and private
        if(functionModifiers.contains(Modifier.PB) && functionModifiers.contains(Modifier.PR)) {
            logger.severe(String.format("Conflicting modifiers for function declaration %s and %s", "pb", "pr"));
            throw new RuntimeException(String.format("Conflicting modifiers for function declaration %s and %s", "pb", "pr"));
        }
        consume(TokenType.Function, "Missing 'fn' in function declaration!");
        String identifier = consume(TokenType.Identifier, "Missing identifier in function declaration!").lexeme;
        consume(TokenType.ParenthesesOpen, "Missing '(' in function declaration!");

        Set<Param> params = new HashSet<>();
        boolean firstParam = true; // Ugly?
        while(match(TokenType.Number, TokenType.Double, TokenType.String, TokenType.Boolean, TokenType.Comma)) {
            // If it's the first param and there is a comma
            if(firstParam && peek().tokenType == TokenType.Comma) {
                logger.severe("Error on token ','!");
                throw new RuntimeException("Error on token ','!");
            // If it's not first param we need the comma
            } else if(!firstParam && peek().tokenType != TokenType.Comma) {
                logger.severe("Expected ','!");
                throw new RuntimeException("Expected ','!");
            } else if(!firstParam) {
                consume(TokenType.Comma, "Expected ','!");
            }
            /// ^ this might genuinely be in the top 5 worst pieces of code I have ever written

            if(match(TokenType.Number, TokenType.Double, TokenType.String, TokenType.Boolean));
                // The type of the param
                ValType paramType = translateToValType(advance().tokenType);

            // The name of the param
            String paramIdentifier = consume(TokenType.Identifier, "Identifier expected after type!").lexeme;

            // Add the param
            params.add(new Param(paramType, paramIdentifier));
            firstParam = false;
            logger.info("Finished parsing function declaration");
        }

        // Close of the function header
        consume(TokenType.ParenthesesClosed, "Missing ')'' in function declaration!");

        // Now for returnType
        consume(TokenType.Return, "Missing return type in function declaration!");
        ValType returnType = translateToValType(advance().tokenType);

        // Now for function body
        NodeBlock contents;
        if(match(TokenType.CurlyOpen)) {
            contents = parseBlock();
        } else {
            logger.severe("Missing '{' for body of function!");
            throw new RuntimeException("Missing '{' for body of function!");
        }
        insideFunction = false;
        logger.info("Finished parsing function declaration");
        return new NodeFunctionDeclaration(identifier, returnType, params, functionModifiers, contents);
    }

    private Modifier addModifier(TokenType modifier) {
        return switch (modifier) {
            case Final   -> Modifier.FIN;
            case Public  -> Modifier.PB;
            case Private -> Modifier.PR;
            default      -> throw new RuntimeException("This is no modifier!");
        };
    }

    private ValType translateToValType(TokenType type) {
        return switch(type) {
            case Number  -> ValType.NUMBER;
            case Double  -> ValType.DOUBLE;
            case String  -> ValType.STRING;
            case Boolean -> ValType.BOOLEAN;
            case Void    -> ValType.VOID;
            default      -> throw new RuntimeException("Not a valid returntype: " + type + "!");
        };
    }
/* 
    private ValType translateLiteralToValType(TokenType literal) {
        return switch(literal) {
            case NumericLiteral  -> ValType.NUMBER;
            case DoubleLiteral  -> ValType.DOUBLE;
            case StringLiteral  -> ValType.STRING;
            case BooleanLiteral -> ValType.BOOLEAN;
            default      -> throw new RuntimeException("Not a valid returntype: " + literal + "!");
        };
    }
*/
    private NodeReturnStatement parseReturnStatement() {
        logger.info("Parsing return-statement");
        if(!insideFunction) {
            logger.severe("Return-statement outside of function!");
            throw new RuntimeException("Return statement outside of function!");
        }
        consume(TokenType.Return, "Missing '->' in return-statement!");
        // Typechecker will handle validating the returnType
        NodeExpression toReturn = pratt(0);
        endStatement();
        return new NodeReturnStatement(null, toReturn);
    }

    private void endStatement() {
        if(!match(TokenType.SemiColon)) {
            logger.severe("';' needed to end statement");
            throw new RuntimeException("';' needed to end statement");
        }
        advance();
    }

    public void print(Object x) {
        System.out.println(x);
    }

    // This will not advance but just check for TokenType
    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if(check(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if(isAtEnd()) return false;
        return peek().tokenType == type;
    }

    // This will check for a single TokenType, advance and returns the token if found or throws exception if not
    private Token consume(TokenType tokenType, String errorMsg) {
        if(check(tokenType)) {
            return advance();
        } else {
            logger.severe(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    private Token peek(int amount) {
        if (index <= tokens.size())
            return tokens.get(index + amount);
        else
            return null;
    }

    private Token peek() {
        return peek(0);
    }

    private Token advance() {
        Token currentToken = peek();
        index++;
        return currentToken;
    }

    private boolean isAtEnd() {
        return index >= tokens.size() || peek().tokenType == TokenType.EOF;
    }
/* 
    private boolean isExpressionToken(TokenType tokenType) {
        return expressionTokens.contains(tokenType) ? true : false;
    }
*/
    record BindingPair(float left, float right) {}
}