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
import io.github.thugborean.syntax.Token;
import io.github.thugborean.syntax.TokenType;
import io.github.thugborean.vm.symbol.ValType;

/*TODO:
 * Implement associativity in expression parsing
 * implement string declaration
 * Double works in lexer for now but not in parser
 */
public class Parser {
    public List<Token> tokens;
    public Program program = new Program();
    private int index = 0;
    private boolean canAbsorb = true; // For stringExpressions

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

            TokenType.Plus,
            TokenType.Minus,
            TokenType.Multiply,
            TokenType.Divide,
            TokenType.Modulo,

            TokenType.ParenthesesOpen,
            TokenType.ParenthesesClosed,

            TokenType.NullLiteral);

    public static final Set<TokenType> stringExpressionTokens = Set.of(
        TokenType.Identifier,
        TokenType.StringLiteral,
        TokenType.CharacterLiteral,
        TokenType.BooleanLiteral,
        TokenType.Plus,
        TokenType.ParenthesesClosed,
        TokenType.SemiColon);

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

                // Check if the current token is Assign, if not throw error, variables must be
                // initialized upon declaration
                if (peek().tokenType == TokenType.Assign)
                    advance();
                else
                    throw new RuntimeException("Parser Error: Identifer must be followed by assignment");

                // THIS IS THE POINT AFTER =
                NodeExpression initialValue;
                // LOGIC FOR NUMBER
                if (type.type == ValType.NUMBER) {
                    // We have to assume that the next tokens are expression-friendly, otherwise it's over
                    // This function does all the heavy lifting
                    initialValue = parseExpression( false);
                    // LOGIC FOR DOUBLE
                } else if (type.type == ValType.DOUBLE) {
                    // We have to assume that the next tokens are expression-friendly, otherwise it's over
                    // This function does all the heavy lifting
                    initialValue = parseExpression( false);
                    // LOGIC FOR STRING
                } else if (type.type == ValType.STRING) {
                    initialValue = parseStringExpression(false);
                } else
                    throw new RuntimeException("Parser Error: Unrecognized type!");

                // Create the initial assignment value
                NodeAssignStatement assignment = new NodeAssignStatement(identifier.lexeme, initialValue);
                // We are done! Finally add the variable declaration to the Program AST
                program.addNode(new NodeVariableDeclaration(type, identifier, assignment));
                // Advance past the SemiColon
                endStatement();

                // DIFFERENT OUTCOMES WITH IDENTIFIER
            } else if (peek().tokenType == TokenType.Identifier) {
                switch (peek(1).tokenType) {
                    // Assignment
                    case TokenType.Assign: {
                        // String identifier = peek().lexeme;
                        
                        // NodeExpression assignedValue = parseExpression(tokens, false);
                    }
                        // Appending
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
                NodeStringExpression printable = parseStringExpression(true);
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

    private boolean isStringExpressionToken(TokenType tokenType) {
        return stringExpressionTokens.contains(tokenType) ? true : false;
    }


    // For now only single strings!!!!!!
    private NodeStringExpression parseStringExpression(boolean insideParentheses) {
        List<NodeExpression> stringElements = new ArrayList<>();
        NodeStringExpression result;
        boolean willLoop = true;
        // We will only allow StringLiterals. CharLiterals, NumberLiterals, DoubleLiterals and Identifiers
        while (willLoop) {
            Token currentToken = peek();
            if(!isStringExpressionToken(currentToken.tokenType))
                throw new RuntimeException("Parser Error: Unrecognized token: " + currentToken.lexeme + " in string expression");
            else {
                switch(currentToken.tokenType) {
                    case TokenType.Identifier: {
                        check();
                        stringElements.add(new NodeVariableReference(currentToken.lexeme));
                        canAbsorb = false;
                        advance();
                        break;
                    }
                    case TokenType.NumericLiteral: {
                        check();
                        stringElements.add(new NodeNumericLiteral(currentToken));
                        canAbsorb = false;
                        advance();
                        break;
                    }
                    case TokenType.DoubleLiteral: {
                        check();
                        stringElements.add(new NodeDoubleLiteral(currentToken));
                        canAbsorb = false;
                        advance();
                        break;
                    }
                    case TokenType.StringLiteral: {
                        check();
                        stringElements.add(new NodeStringLiteral(currentToken));
                        canAbsorb = false;
                        advance();
                        break;
                    }
                    case TokenType.CharacterLiteral: { 
                        throw new RuntimeException("Unimplemented type Character TODO!");
                    }
                    case TokenType.BooleanLiteral: {
                        throw new RuntimeException("Unimplemented type Boolean TODO!");
                    }
                    case TokenType.Plus: {
                        if(canAbsorb) throw new RuntimeException("Parser Error: Mismatched '+' in string expression");
                            canAbsorb = true;
                            advance();
                            break;
                    }
                    case TokenType.ParenthesesClosed: {
                        if(insideParentheses) {
                            if(canAbsorb) throw new RuntimeException("Parser Error: Mismatched '+' in string expression");
                            advance();
                            willLoop = false;
                            break;
                        } else throw new RuntimeException("Parser Error: Delete ')' inside string expression");
                    }
                    case TokenType.SemiColon: {
                        if(!insideParentheses) {
                            willLoop = false;
                            break;
                        } else throw new RuntimeException("Parser Error: ')' expected to close string expression");
                    }
                    default: throw new RuntimeException("Parser Error: This should not happen, there's an error parseStringExpression");
                }
            }
        }
        canAbsorb = true;
        result = new NodeStringExpression(stringElements);
        return result;
    }

    private void check() {
        if(!canAbsorb) throw new RuntimeException("Parser Error: Mismatched '+' in string expression");
    }

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
        // If it's just a single value, return that value
        else if (expressionTokens.size() == 1) {
            Token token = expressionTokens.get(0);
            if (token.tokenType == TokenType.Identifier) {
                return new NodeVariableReference(token.lexeme);
            } else if (token.tokenType == TokenType.NumericLiteral) {
                return new NodeNumericLiteral(token);
            } else if (token.tokenType == TokenType.DoubleLiteral) {
                return new NodeDoubleLiteral(token);
            } else if (token.tokenType == TokenType.NullLiteral) { // This is if the expression is null;
                return new NodeNullLiteral(token);
            } else
                throw new RuntimeException("Parser Error: Unexpected token type in expression: " + token.tokenType);
        } else {
            // RPN
            List<Token> solvingStack = shuntingYard(expressionTokens);
            // Create AST from RPN
            result = createNodeExpressionAST(solvingStack);
        }
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
                // IDENTIFIER LOGIC ---------------------------------------------------------
            } else if (token.tokenType == TokenType.Identifier) {
                output.add(token);
            }
            // NUMERICAL LOGIC ----------------------------------------------------------
            // If it's a numeric value add it to the ouput
            else
                output.add(token);
        }
        // Add the rest of the operators to the output
        while (!holdingStack.holdingCell.isEmpty()) {
            Token op = holdingStack.pop();
            if (op.tokenType == TokenType.ParenthesesOpen || op.tokenType == TokenType.ParenthesesClosed) {
                throw new RuntimeException("Parser Error: Mismatched parentheses");
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
            throw new RuntimeException("Parser Error: ';' expected after expression");
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