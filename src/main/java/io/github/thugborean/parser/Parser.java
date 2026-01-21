package io.github.thugborean.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import javax.management.RuntimeErrorException;

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

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // VariableTypes
    private static final Map<TokenType, ValType> variableTypes = Map.ofEntries(
            Map.entry(TokenType.Number, ValType.NUMBER),
            Map.entry(TokenType.Double, ValType.DOUBLE),
            Map.entry(TokenType.String, ValType.STRING),
            Map.entry(TokenType.Boolean, ValType.BOOLEAN)
    );

    // ExpressionTokens
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

        TokenType.ParenthesesOpen,
        TokenType.ParenthesesClosed,

        TokenType.NullLiteral);

    private static final Set<TokenType> operators = Set.of(
        TokenType.Plus,
        TokenType.Minus,
        TokenType.Multiply,
        TokenType.Divide,
        TokenType.Modulo,

        TokenType.AtseriskAsterisk, // ²

        TokenType.LessThan,
        TokenType.LessThanOrEquals,
        TokenType.GreaterThan,
        TokenType.GreaterThanOrEquals,
        TokenType.EqualsEquals,
        TokenType.BangEquals
    );

    public Program parseProgram() {
        logger.info("Parsing program...");
        this.index = 0;
        List<NodeStatement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(parseStatement());
        }
        if(curlyDepth != 0) throw new RuntimeException("Parser Error: Mismatched curly-braces!");
        curlyDepth = 0;
        return new Program(statements);
    }

    private NodeStatement parseStatement() {
        if(match(TokenType.If)) return parseIfStatement();
        if(match(TokenType.While)) return parseWhileStatement();
        if(match(TokenType.Number, TokenType.Double, TokenType.String, TokenType.Boolean)) return parseVariableDeclaration();
        if(match(TokenType.Print)) return parsePrintStatement();
        if(match(TokenType.CurlyOpen)) return parseBlock();
        return parseExpressionStatement();
    }

    // This method will from now on be used to parse ALL expressions, typechecker will enforce correctness
    private NodeExpression parseExpression(boolean insideParentheses) {
        logger.info("Parsing expression...");
        List<Token> expressionTokens = new ArrayList<>();
        NodeExpression result;
        int depth = insideParentheses ? 1 : 0;
        while (true) {
            if (peek().tokenType == TokenType.SemiColon || peek().tokenType == TokenType.CurlyOpen) break;
            // PARENTHESIS LOGIC
            if (peek().tokenType == TokenType.ParenthesesOpen) {
                depth++;
                expressionTokens.add(peek());
                advance();
                continue;
            } else if (peek().tokenType == TokenType.ParenthesesClosed) {
                // This means that it's the clsoing parentheses
                if (insideParentheses && depth == 1) {
                    depth--;
                    advance();
                } else {
                    depth--;
                    expressionTokens.add(peek());
                    advance();
                    continue;
                }
            } else
            // EVERYTHING ELSE
            // If the token is an expressionToken add it
            if (isExpressionToken(peek().tokenType)) {
                expressionTokens.add(peek());
                advance();
                continue;
            } else
                throw new RuntimeException("Parser Error: Unexpected Symbol '" + peek().tokenType + "' in expression");
        }
        if (depth != 0)
            throw new RuntimeException("Parser Error: '(' not closed properly");
        // Check if the expression is empty
        if (expressionTokens.isEmpty())
            throw new RuntimeException("Parser Error: Empty expression");

        // We will use shuntingyard to turn the expression into RPN
        // RPN
        List<Token> solvingStack = shuntingYard(expressionTokens);
        // Create AST from RPN
        result = createNodeExpressionAST(solvingStack);
        
        logger.info("Finished parsing expression");
        return result;
    }
    /*
                case Or:
            case LessThan:
            case LessThanOrEquals:
            case GreaterThan:
            case GreaterThanOrEquals:
            case EqualsEquals:
                return 1;
            case And:
                return 2;
            case Plus:
            case Minus:
                return 3;
            case Multiply:
            case Divide:
            case Modulo:
                return 4;
            case AtseriskAsterisk:
                return 5;
            case Assign:
                return -1;
            default:
                return 0;
    */
    private int getBindingPower(Token operator) {
        return switch (operator.tokenType) {
            case Assign -> {
                yield 0;
            }
            case LessThan, GreaterThan, LessThanOrEquals, GreaterThanOrEquals, EqualsEquals -> {
                yield 1;
            }
            case And -> {
                yield 2;
            }
            case Plus, Minus -> {
                yield 3;
            }
            case Multiply, Divide, Modulo -> {
                yield 3;
            }
            case AtseriskAsterisk -> {
                yield 4;
            }
            default -> {
                logger.severe("Unrecognized operator in expression: " + operator.lexeme);
                throw new RuntimeException("Unrecognized operator in expression: " + operator.lexeme);
            }
        };
    }

    // Needs peek and next
    private NodeExpression pratt(List<Token> tokens) {
        Stack<NodeExpression> expressionStack = new Stack<>();
        for(Token token : tokens) {

        }
        if (expressionStack.empty()) {
            logger.severe("Expression stack was empty!");
            throw new RuntimeException("Expression stack was empty!");
        }
        return expressionStack.pop();
    }

    private List<Token> shuntingYard(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        HoldingStack holdingStack = new HoldingStack();

        for (Token token : tokens) {
            // If we get this far it means the user is trying to do artithemtics with a
            // NullLiteral
            if (token.tokenType == TokenType.NullLiteral) {
                logger.severe("Found null in arithmetic expression!");
                throw new RuntimeException("Parser Error: NULL must stand on its own in an expression!");
            }
            // parentheses LOGIC --------------------------------------------------------
            // If open parentheses, push it onto the stack
            if (token.tokenType == TokenType.ParenthesesOpen)
                holdingStack.push(token);
            else if (token.tokenType == TokenType.ParenthesesClosed) {
                // Logic for parentheses
                while (holdingStack.peek().tokenType != TokenType.ParenthesesOpen) {
                    output.add(holdingStack.pop());
                    // If we drain and find no open parentheses, we throw an error
                    if (holdingStack.peek() == null) {
                        logger.info("Mismatched paranthesis in expression!");
                        throw new RuntimeException("Mismatched paranthesis in expression!");
                    }
                }
                // It should always get to this point if it works and if it doesn't it SHOULD
                // throw an error before we get to this point
                holdingStack.pop(); // Remove the open parentheses
            // OPERATOR LOGIC -----------------------------------------------------------
            // Check if it is an operator
            } else if (operators.contains(token.tokenType)) {
                // While the stack is not empty and the current top operator has a lesser
                // precedence than the token operator
                // Pop the top operator of the stack and add it to output
                while (!holdingStack.holdingCell.isEmpty() &&
                        getPrecedence(token) <= getPrecedence(holdingStack.peek())) {
                    output.add(holdingStack.pop());
                }
                holdingStack.push(token);
            // LITERAL AND IDENTIFIER LOGIC ----------------------------------------------
            } else output.add(token);
        }
        // Add the rest of the operators to the output
        while (!holdingStack.holdingCell.isEmpty()) {
            Token op = holdingStack.pop();
            if (op.tokenType == TokenType.ParenthesesOpen || op.tokenType == TokenType.ParenthesesClosed) {
                logger.severe("Mismatched paranthesis in expression!");
                throw new RuntimeException("Mismatched paranthesis in expression!");
            }
            output.add(op);
        }
        return output;
    }

    private NodeExpression createNodeExpressionAST(List<Token> rpnTokens) {
        Stack<NodeExpression> stack = new Stack<>();
        for (Token token : rpnTokens) {
            if (token.tokenType == TokenType.Identifier) {
                stack.push(new NodeVariableReference(token.lexeme));
            } else if (token.tokenType == TokenType.NumericLiteral) {
                stack.push(new NodeNumericLiteral(token));
            } else if (token.tokenType == TokenType.DoubleLiteral) {
                stack.push(new NodeDoubleLiteral(token));
            } else if (token.tokenType == TokenType.StringLiteral) {
                stack.push(new NodeStringLiteral(token));
            } else if (token.tokenType == TokenType.CharacterLiteral) {
                throw new RuntimeException("Char is unimplemented!");
            } else if (token.tokenType == TokenType.True || token.tokenType == TokenType.False) {
                stack.push(new NodeBooleanLiteral(token));
            } else if (operators.contains(token.tokenType)) {
                if (stack.size() < 2) {
                    throw new RuntimeException("Parser Error: Not enough operands for operator: " + token.lexeme);
                }
                NodeExpression right = stack.pop();
                NodeExpression left = stack.pop();
                stack.push(new NodeBinaryExpression(left, right, token));
            } else {
                throw new RuntimeException("Parser Error: Unrecognized token in RPN expression: " + token.lexeme);
            }
        }
        if (stack.size() != 1) {
            logger.severe("Expression did not reduce to a single node. Stack contains: " + stack.size() + " elements!");
            throw new RuntimeException("Expression did not reduce to a single node. Stack contains: " + stack.size() + " elements!");
        }
        return stack.pop();
    }

    private int getPrecedence(Token token) {
        switch (token.tokenType) {
            case Or:
            case LessThan:
            case LessThanOrEquals:
            case GreaterThan:
            case GreaterThanOrEquals:
            case EqualsEquals:
                return 1;
            case And:
                return 2;
            case Plus:
            case Minus:
                return 3;
            case Multiply:
            case Divide:
            case Modulo:
                return 4;
            case AtseriskAsterisk:
                return 5;
            case Assign:
                return -1;
            default:
                return 0;
        }
    }

    private NodeExpressionStatement parseExpressionStatement() {
        // This is ugly but will have to do for now
        if(peek(1).tokenType == TokenType.Assign) return parseAssignStatement();
        else {
            NodeExpression expressionStatement = parseExpression(false);
            endStatement();
            return new NodeExpressionStatement(expressionStatement);
        }
    }

    // Will need clean-up!
    private NodeVariableDeclaration parseVariableDeclaration() {
        logger.info("Parsing variable declaration");
        Token typeToken = advance();
        ValType declaredType = variableTypes.get(typeToken.tokenType);

        // Explicitly require IDENTIFIER next
        Token identifierToken = consume(TokenType.Identifier, "Expected variable name after type");
        String identifier = identifierToken.lexeme;

        // Optional initializer
        NodeAssignStatement assignment = null;
        if (match(TokenType.Assign)) {
            assignment = parseAssignStatement(identifier);
        }

        endStatement();
        return new NodeVariableDeclaration(declaredType, identifier, assignment);
    }

    private NodePrintStatement parsePrintStatement() {
        logger.info("Parsing print-statement...");
        consume(TokenType.Print, "Missing print-statement!");

        consume(TokenType.ParenthesesOpen, "Missing '(' on print statement!");

        NodeExpression printable = parseExpression(true);

        endStatement();
        logger.info("Finished parsing print-statement");
        return new NodePrintStatement(printable);
    }

    private NodeIfStatement parseIfStatement() {
        logger.info("Parsing if-statement...");
        consume(TokenType.If, "Missing 'if' token in if-statement!");
        consume(TokenType.ParenthesesOpen, "Missing '(' before expression!");
        NodeExpression condition = parseExpression(true);
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

        NodeExpression condition = parseExpression(true);
        NodeBlock thenBlock;

        if(match(TokenType.CurlyOpen)) thenBlock = parseBlock();
            else throw new RuntimeException("Missing '{' in while-statement!");

        logger.info("Finished parsig while-statement");
        return new NodeWhileStatement(condition, thenBlock);
    }

    private NodeAssignStatement parseAssignStatement() {
        logger.info("Parsing assign-statement");
        String identifier = consume(TokenType.Identifier, "Missing identifier!").lexeme;
        consume(TokenType.Assign, "Missing '=' in assign-statement!");

        NodeExpression expression = parseExpression(false);

        endStatement();
        return new NodeAssignStatement(identifier, expression);
    }

    private NodeAssignStatement parseAssignStatement(String identifier) {
        logger.info("Parsing assign-statement");
        consume(TokenType.Assign, "Missing '=' in assign-statement!");
        NodeExpression expression = parseExpression(false);
        return new NodeAssignStatement(identifier, expression);
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

    private boolean isExpressionToken(TokenType tokenType) {
        return expressionTokens.contains(tokenType) ? true : false;
    }
}

class HoldingStack {
    public int stackPointer = -1;
    public List<Token> holdingCell = new ArrayList<>(); // Ampere - Holding Pattern

    public void push(Token token) {
        holdingCell.add(token);
        stackPointer++;
    }

    public Token pop() {
        if (!holdingCell.isEmpty()) {
            // Get the token
            Token token = holdingCell.get(stackPointer);
            // Remove the token
            holdingCell.remove(stackPointer);
            stackPointer--;
            // Return the token
            return token;
        } else
            return null;
    }

    public Token peek() {
        if (!holdingCell.isEmpty()) {
            return holdingCell.get(stackPointer);
        } else
            return null;
    }
}