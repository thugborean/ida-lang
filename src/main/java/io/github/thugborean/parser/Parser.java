package io.github.thugborean.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodePrintStatement;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeNumber;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.syntax.Token;
import io.github.thugborean.syntax.TokenType;
/*TODO:
 * Implement associativity in expression parsing
 * Fix prettyprinter
 * implement string declaration
 * implement new types in lexer and clean it up, Double works in lexer for now but not in parser
 * Decide on NodeExpressionStatement
 */
public class Parser {
    public List<Token> tokens;
    public Program program;
    private int index;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.program = new Program();
        this.index = 0;
    }

    // Questionable ????
    public Parser() {
        this.program = new Program();
        this.index = 0;
    }

    // varibleTypes
    public static final Map<TokenType, NodeType> varibleTypes = Map.ofEntries(
        Map.entry(TokenType.Number, new NodeNumber())

        // WIP, unimplemented types
        // Map.entry(null, null),
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
        TokenType.ParenthesesClosed
    );

    public static final Set<TokenType> operators = Set.of(
        TokenType.Plus,
        TokenType.Minus,
        TokenType.Multiply,
        TokenType.Divide,
        TokenType.Modulo,

        TokenType.AtseriskAsterisk //²
    );

    public Program createAST() {
        this.index = 0;
        this.program = new Program();
        while(!isAtEnd()) {
            // VARIABLE DECLARATION LOGIC --------------------------------------------------------------------------------
            if(varibleTypes.containsKey(peek().tokenType)) {
                // Get variable type then advance
                NodeType type = varibleTypes.get(peek().tokenType); // Needs keyword new
                advance();

                // Get variable identifer Token then advance
                Token identifier = peek(); // Maybe some checks are needed to see if its valid ???
                advance();

                // Check if the current token is Assign, if not throw error, variables must be initialized upon declaration
                if(peek().tokenType == TokenType.Assign) advance(); 
                    else throw new RuntimeException("Parser Error: Identifer must be followed by assignment");

                // We have to assume that the next tokens are expression-friendly, otherwise it's over
                List<Token> expression = new ArrayList<>();
                // This function does all the heavy lifting
                NodeExpression initialValue = parseExpression(expression, false);
                if(peek().tokenType != TokenType.SemiColon) throw new RuntimeException("Parser Error: ';' expected after expression");
                // We are done! Finally add the variable declaration to the Program AST
                program.addNode(new NodeVariableDeclaration(type, identifier, initialValue));
                advance();
            // PRINT STATEMENT LOGIC -------------------------------------------------------------------------------------
            } else if (peek().tokenType == TokenType.Print) {
                // Advance past the print
                advance();
                // Check if the current token is an open parentheses
                if(peek().tokenType != TokenType.ParenthesesOpen) throw new RuntimeException("Parser Error: Excepted '(' after print statement");
                advance();

                List<Token> expressionTokens = new ArrayList<>();
                // What is to be printed
                // This SHOULD advance to the SemiColon!!!!!!!!!
                NodeExpression printable = parseExpression(expressionTokens, true);
                if(peek().tokenType != TokenType.SemiColon) throw new RuntimeException("Parser Error: ';' expected after expression");
                program.addNode(new NodePrintStatement(printable));
                advance();
            }   
        }
        this.tokens.clear();
        return this.program;
    }

    private Token peek(int amount) {
        if (index <= tokens.size()) return tokens.get(index + amount);
        else return null;
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
        if(expressionTokens.contains(tokenType)) return true;
            else return false;
    }

    private NodeExpression parseExpression(List<Token> expressionTokens, boolean insideParentheses) {
        NodeExpression result;
        int depth = insideParentheses ? 1 : 0;
        print("depth: " + depth);
        while(true) {
            if(peek().tokenType == TokenType.SemiColon) {
                break;
            }
            // PARENTHESIS LOGIC
            if (peek().tokenType == TokenType.ParenthesesOpen) {
                depth++;
                expressionTokens.add(peek());
                advance();
                continue;
            } else if(peek().tokenType == TokenType.ParenthesesClosed) {
                // This means that it's the clsoing parentheses
                if(insideParentheses && depth == 1) {
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
            if(isExpressionToken(peek().tokenType)) {
                expressionTokens.add(peek());
                advance();
                continue;
            } else throw new RuntimeException("Parser Error: Unexpected symbol '" + peek().tokenType + "' in expression");
        }
        // Check if the expression is ended by a ';'
        if(peek().tokenType != TokenType.SemiColon) throw new RuntimeException("Parser Error: Expected ';' after expression");
        if(depth != 0) throw new RuntimeException("Parser Error: ( not closed properly");
        // Check if the expression is empty
        if(expressionTokens.isEmpty()) throw new RuntimeException("Parser Error: Empty expression");
        // If it's just a single value, return that value
        else if(expressionTokens.size() == 1) {
            return new NodeNumericLiteral(expressionTokens.get(0));
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

        for(Token token : tokens) {
            // parentheses LOGIC --------------------------------------------------------
            // If open parentheses, push it onto the stack
            if(token.tokenType == TokenType.ParenthesesOpen) holdingStack.push(token);
                else if(token.tokenType == TokenType.ParenthesesClosed) {
                    // Logic for parentheses
                    while(holdingStack.peek().tokenType != TokenType.ParenthesesOpen) {
                        output.add(holdingStack.pop());
                        // If we drain and find no open parentheses, we throw an error
                        if(holdingStack.peek() == null) throw new RuntimeException("Parser Error: No '(' found to match ')'");
                    }
                    // It should always get to this point if it works and if it doesn't it SHOULD throw an error before we get to this point
                    holdingStack.pop(); // Remove the open parentheses
            // OPERATOR LOGIC -----------------------------------------------------------
                // Check if it is an operator
                } else if(operators.contains(token.tokenType)) { 
                    // While the stack is not empty and the current top operator has a lesser precedence than the token operator
                    // Pop the top operator of the stack and add it to output
                    while (!holdingStack.holdingCell.isEmpty() &&
                        getPrecedence(token) <= getPrecedence(holdingStack.peek())) {
                        output.add(holdingStack.pop());
                        }

                    holdingStack.push(token);
            // IDENTIFIER LOGIC ---------------------------------------------------------
                    } else if(token.tokenType == TokenType.Identifier) {
                        output.add(token);
                    }
            // NUMERICAL LOGIC ----------------------------------------------------------
                // If it's a numeric value add it to the ouput
                else output.add(token);
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
            throw new RuntimeException("Parser Error: Expression did not reduce to a single node. Stack contains: " + stack.size() + " elements!");
        }
        return stack.pop();
    }

    private int getPrecedence(Token token) {
        switch (token.tokenType) {
            case Plus: 
            case Minus: return 1;
            case Multiply: 
            case Divide:
            case Modulo: return 2;
            case AtseriskAsterisk: return 3;
            // case ParenthesesOpen: return 1000; Maybe? NO!
            default: return 0;
        }
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
        if(!holdingCell.isEmpty()) {
        // Get the token
        Token token = holdingCell.get(stackPointer);
        // Remove the token
        holdingCell.remove(stackPointer);
        stackPointer--;
        // Return the token
        return token;
        } else return null;
    }

    public Token peek() {
        if(!holdingCell.isEmpty()) {
            return holdingCell.get(stackPointer);
        } else return null;
    }
}