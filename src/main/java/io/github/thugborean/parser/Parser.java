package io.github.thugborean.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import io.github.thugborean.ast.node.NodeAST;
import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodeStatement;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeNumber;
import io.github.thugborean.ast.node.types.NodeType;
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

    // NodeTypes
    public static final Map<TokenType, NodeType> nodeTypes = Map.ofEntries(
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

        TokenType.ParenthesisOpen,
        TokenType.ParenthesisClosed
    );

    public static final Set<TokenType> operators = Set.of(
        TokenType.Plus,
        TokenType.Minus,
        TokenType.Multiply,
        TokenType.Divide,

        TokenType.AtseriskAsterisk
    );

    public Program createAST() {
        while(peek() != null) {
            // Parse variable declaration
            if(nodeTypes.containsKey(peek().tokenType)) {
                // Get variable type then advance
                NodeType type = nodeTypes.get(peek().tokenType);
                advance();

                // Get variable identifer Token then advance
                Token identifier = peek();
                advance();

                // Check if the current token is Assign, if not throw error, variables must be initialized upon declaration
                if(peek().tokenType == TokenType.Assign) advance(); 
                    else throw new RuntimeException("Parser Error: Identifer must be followed by assignment");

                // We have to assume that the next tokens are expression-friendly, otherwise it's over
                List<Token> expressionTokens = new ArrayList<>();
                while(true) {
                    if(expressionTokens.contains(peek())) {
                        expressionTokens.add(peek());
                        advance();
                    }
                    else if (peek().tokenType == TokenType.SemiColon) {
                        break;
                    } else throw new RuntimeException("Parser Error: Expected ';' after expression");
                }
                
                NodeExpression initialValue = parseExpression(expressionTokens);
                // We are done! Finally add the variable declaration to the Program AST
                program.addNode(new NodeVariableDeclaration(identifier, type, initialValue));
            // Parse print statement
            } else if (peek().tokenType == TokenType.Print) {

            }
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

    private void advance() {
        index++;
    }

    private NodeExpression parseExpression(List<Token> expressionTokens) {
        NodeExpression result;

        // If it's just a single value, return that value
        if(expressionTokens.size() == 1) {
            return new NodeNumericLiteral(expressionTokens.get(0));
        } else {
            List<Token> solvingStack = shuntingYard(expressionTokens);
            
            
        }
        
        return null;
    }

    private List<Token> shuntingYard(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        HoldingStack holdingStack = new HoldingStack();

        for(Token token : tokens) {
            // If open parenthesis, push it onto the stack
            if(token.tokenType == TokenType.ParenthesisOpen) holdingStack.push(token);
                else if(token.tokenType == TokenType.ParenthesisClosed) {
                    // Logic for parenthesis
                    while(holdingStack.peek().tokenType != TokenType.ParenthesisOpen) {
                        output.add(holdingStack.pop());
                        // If we drain and find no open parenthesis, we throw an error
                        if(holdingStack.peek() == null) throw new RuntimeException("Parser error: No '(' found to match ')'");
                    }
                    // It should always get to this point if it works and if it doesn't it SHOULD throw an error
                    holdingStack.pop(); // Remove the open parenthesis
                // Check if it is an operator
                } else if(operators.contains(token.tokenType)) { 
                    // While the stack is not empty and the current top operator has a lesser precedence than the token operator
                    // pop the top operator of the stack and add it to output
                    while(holdingStack.peek() != null && getPrecedence(token) <= getPrecedence(holdingStack.peek())) {
                        output.add(holdingStack.pop());
                    }
                    holdingStack.push(token);
                    }
                // If it's a numeric value add it to the ouput
                else output.add(token);
        }
        // Add the rest of the operators to the output
        while(holdingStack.peek() != null) {
            
            output.add(holdingStack.pop());
        }
        return output;
    }

    private NodeExpression BuildNodeExpressionAST(List<Token> rpnTokens) {
        NodeExpression result;
        return result;
    }

    private int getPrecedence(Token token) {
        switch (token.tokenType) {
            case Plus: 
            case Minus: return 1;
            case Multiply: 
            case Divide:
            case Modulo: return 2;
            case PlusPlus:
            case MinusMinus:
            case AtseriskAsterisk: return 3;
            // case ParenthesisOpen: return 1000; Maybe? 
            default: return 0;
        }
    }

    private NodeStatement parseStatement() {
        if (peek().tokenType == TokenType.Number) {
            
        }
        return null;
    }
}

// Custom datatype, a stack that you can iterate through
class HoldingStack {
    public int stackPointer = 0;
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