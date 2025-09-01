package io.github.thugborean.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.types.*;
import io.github.thugborean.syntax.*;

public class Parser {
    public List<Token> tokens;
    public Program program = new Program();
    private int index = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // varibleTypes
    public static final Map<TokenType, NodeType> varibleTypes = Map.ofEntries(
            Map.entry(TokenType.Number, new NodeNumber()),
            Map.entry(TokenType.Double, new NodeDouble()),
            Map.entry(TokenType.String, new NodeString())

    // Map.entry(null, null),
    // Map.entry(null, null),
    // Map.entry(null, null)
    );

    // ExpressionTokens
    public static final Set<TokenType> expressionTokens = Set.of(
        TokenType.Identifier,
        TokenType.NumericLiteral,
        TokenType.DoubleLiteral,
        TokenType.CharacterLiteral,
        TokenType.StringLiteral,

        TokenType.Plus,
        TokenType.Minus,
        TokenType.Multiply,
        TokenType.Divide,
        TokenType.Modulo,
        TokenType.AtseriskAsterisk,

        TokenType.EqualsEquals,
        TokenType.NotEquals,
        TokenType.LessThan,
        TokenType.LessThanOrEquals,
        TokenType.GreaterThan,
        TokenType.GreaterThanOrEquals,

        TokenType.ParenthesesOpen,
        TokenType.ParenthesesClosed,

        TokenType.NullLiteral);

    public static final Set<TokenType> operators = Set.of(
            TokenType.Plus,
            TokenType.Minus,
            TokenType.Multiply,
            TokenType.Divide,
            TokenType.Modulo,

            TokenType.AtseriskAsterisk // ²
    );

    public Program createAST() {
        this.index = 0;
        this.program = new Program();
        while (!isAtEnd()) {
            // VARIABLE DECLARATION LOGIC
            // --------------------------------------------------------------------------------
            if (varibleTypes.containsKey(peek().tokenType)) {

                // Get variable type then advance
                NodeType type = varibleTypes.get(peek().tokenType); // Needs keyword new
                advance();

                // Get variable identifer Token then advance
                Token identifier = peek(); // Maybe some checks are needed to see if its valid ???
                advance();

                // Check if the current token is Assign, if not throw error, variables must be initialized upon declaration
                if (peek().tokenType == TokenType.Assign) advance();
                    else throw new RuntimeException("Parser Error: Identifer must be followed by assignment");

                // THIS IS THE POINT AFTER =
                NodeExpression initialValue = parseExpression(false);

                // Create the initial assignment value
                NodeAssignStatement assignment = new NodeAssignStatement(identifier.lexeme, initialValue);
                // We are done! Finally add the variable declaration to the Program AST
                program.addNode(new NodeVariableDeclaration(type, identifier, assignment));
                // Advance past the SemiColon
                endStatement();

                // DIFFERENT OUTCOMES WITH IDENTIFIER
            } else if (peek().tokenType == TokenType.Identifier) {
                switch (peek(1).tokenType) {
                    case TokenType.Assign: {
                        // Get the identifier and advance
                        String identifier = peek().lexeme;
                        advance();

                        // Check if the current token is Assign, if not throw error, variables must be initialized upon declaration
                        if (peek().tokenType == TokenType.Assign) advance();
                            else throw new RuntimeException("Parser Error: Identifer must be followed by assignment");

                        // Assume there is not ()
                        NodeExpression assignment = parseExpression(false);
 
                        // Add NodeAssignstatement with given identifier and assignment
                        program.addNode(new NodeAssignStatement(identifier, assignment));
                        break;
                    }
                    case TokenType.Append:
                        // Truncating
                    case TokenType.Truncate:
                        // Incrementing
                    case TokenType.PlusPlus: {
                        String identifier = peek().lexeme;
                        advance(); // Advances past the identifier
                        advance(); // Advances past the ++

                        program.addNode(new NodeExpressionStatement(identifier, new NodeIncrement()));
                        break;
                    }
                    // Decrementing
                    case TokenType.MinusMinus:
                        // Function call
                    case TokenType.ParenthesesOpen:
                    default:
                        throw new RuntimeException("Parser Error: Unknown operation on Symbol");
                }
                endStatement();
                // PRINT STATEMENT LOGIC
                // -------------------------------------------------------------------------------------
            } else if (peek().tokenType == TokenType.Print) {
                // Advance past the print
                advance();
                // Check if the current token is an open parentheses, if so advance past it
                if (peek().tokenType != TokenType.ParenthesesOpen)
                    throw new RuntimeException("Parser Error: Excepted '(' after print statement");
                advance();

                // Parse the expression
                NodeExpression printable = parseExpression(true);
                program.addNode(new NodePrintStatement(printable));
                endStatement();
            }
        }
        tokens.clear();
        return program;
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

    private void advance() {
        index++;
    }

    private boolean isAtEnd() {
        return index >= tokens.size() || peek().tokenType == TokenType.EOF;
    }

    private boolean isExpressionToken(TokenType tokenType) {
        return expressionTokens.contains(tokenType) ? true : false;
    }

    // This method will from now on be used to parse ALL expressions, typechecker will enforce correctness
    private NodeExpression parseExpression(boolean insideParentheses) {
        List<Token> expressionTokens = new ArrayList<>();
        NodeExpression result;
        int depth = insideParentheses ? 1 : 0;
        while (true) {
            if (peek().tokenType == TokenType.SemiColon) break;
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
        // Check if the expression is ended by a ';'
        if (peek().tokenType != TokenType.SemiColon)
            throw new RuntimeException("Parser Error: Expected ';' after expression");
        if (depth != 0)
            throw new RuntimeException("Parser Error: ( not closed properly");
        // Check if the expression is empty
        if (expressionTokens.isEmpty())
            throw new RuntimeException("Parser Error: Empty expression");
        
        // We will use shuntingyard to turn the expression into RPN
        // RPN
        List<Token> solvingStack = shuntingYard(expressionTokens);
        // Create AST from RPN
        result = createNodeExpressionAST(solvingStack);
        
        return result;
    }

    private List<Token> shuntingYard(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        HoldingStack holdingStack = new HoldingStack();

        for (Token token : tokens) {
            // If we get this far it means the user is trying to do artithemtics with a
            // NullLiteral
            if (token.tokenType == TokenType.NullLiteral)
                throw new RuntimeException("Parser Error: NULL must stand on its own in an expression!");
            // parentheses LOGIC --------------------------------------------------------
            // If open parentheses, push it onto the stack
            if (token.tokenType == TokenType.ParenthesesOpen)
                holdingStack.push(token);
            else if (token.tokenType == TokenType.ParenthesesClosed) {
                // Logic for parentheses
                while (holdingStack.peek().tokenType != TokenType.ParenthesesOpen) {
                    output.add(holdingStack.pop());
                    // If we drain and find no open parentheses, we throw an error
                    if (holdingStack.peek() == null)
                        throw new RuntimeException("Parser Error: No '(' found to match ')'");
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
            if (op.tokenType == TokenType.ParenthesesOpen || op.tokenType == TokenType.ParenthesesClosed)
                throw new RuntimeException("Parser Error: Mismatched parentheses");
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
            } else if (token.tokenType == TokenType.BooleanLiteral) {
                throw new RuntimeException("Bool is unimplemented!");
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
            throw new RuntimeException("Parser Error: Expression did not reduce to a single node. Stack contains: "
                    + stack.size() + " elements!");
        }
        return stack.pop();
    }

    private int getPrecedence(Token token) {
        switch (token.tokenType) {
            case Plus:
            case Minus:
                return 1;
            case Multiply:
            case Divide:
            case Modulo:
                return 2;
            case AtseriskAsterisk:
                return 3;
            default:
                return 0;
        }
    }

    private void endStatement() {
        if (peek().tokenType != TokenType.SemiColon)
            throw new RuntimeException("Parser Error: ';' needed to end statement");
        else advance();
    }

    public void loadTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void print(Object x) {
        System.out.println(x);
    }
}

// Custom datatype, a stack that you can iterate through
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