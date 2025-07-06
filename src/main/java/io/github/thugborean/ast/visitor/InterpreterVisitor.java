package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeUnaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.NodeAssignStatement;
import io.github.thugborean.ast.node.statement.NodeExpressionStatement;
import io.github.thugborean.ast.node.statement.NodePrintStatement;
import io.github.thugborean.ast.node.statement.NodeStatement;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.syntax.TokenType;
import io.github.thugborean.vm.Environment;
import io.github.thugborean.vm.symbol.Value;
import io.github.thugborean.vm.symbol.Variable;

public class InterpreterVisitor implements ASTVisitor<Value>{
    private Environment environment;

    public InterpreterVisitor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void walkTree(Program program) {
        for(NodeStatement statement : program.nodes) {
            statement.accept(this);
        }
    }

    @Override
    public Value visitNodeBinaryExpression(NodeBinaryExpression node) {
        Value left = (Value)node.leftHandSide.accept(this);
        Value right = (Value)node.rightHandSide.accept(this);
        // Determine the operator
        switch(node.operator.tokenType) {
            case TokenType.Plus: {
                return new Value(left.asNumber() + right.asNumber());
            }
            case TokenType.Minus: {
                return new Value(left.asNumber() - right.asNumber());
            }
            case TokenType.Multiply: {
                return new Value(left.asNumber() * right.asNumber());
            }
            case TokenType.Divide: {
                return new Value(left.asNumber() / right.asNumber());
            }
            case TokenType.Modulo: {
                return new Value(left.asNumber() % right.asNumber());
            }
            default: throw new RuntimeException("Interpreter Error: Couldn't find operator for expression!");
        }
    }

    @Override
    public Value visitUnaryExpression(NodeUnaryExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public Value visitNodeVariableReference(NodeVariableReference node) {
        return environment.getVariable(node.identifier).getvalue();
    }

    @Override
    public Value visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        // Make a new variable with given identifier and evaluated epxression... type.type is not a banger...
        Variable var = new Variable(node.type.type, (Value)node.initialValue.accept(this));
        environment.defineVariable(node.identifier.lexeme, var);
        // Nothing meaningful
        return null;
    }

    @Override
    public Value visitExpressionStatement(NodeExpressionStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitExpressionStatement'");
    }

    @Override
    public Value visitNodePrintStatement(NodePrintStatement node) {
        Value printable = (Value)node.printable.accept(this);
        // Print the pritable
        System.out.println(printable);
        // Nothing meaningful
        return null;
    }

    @Override
    public Value visitAssignStatement(NodeAssignStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignStatement'");
    }

    private void print(Object x) {
        System.out.println(x);
    }

    // NEVER USED NEVER USED!!!!!!!
    public Value visitNodeType(NodeType node) {
        return null;
    }

    @Override
    // For all the literal types
    public Value visitNodeLiteral(NodeLiteral node) {
        return new Value(node.getValue());
    }

    @Override
    public Value visitNodeNumericLiteral(NodeNumericLiteral node) {
        return new Value(node.getValue());
    }

    @Override
    public Value visitNodeDoubleLiteral(NodeDoubleLiteral node) {
        return new Value(node.getValue());
    }

    @Override
    public Value visitNodeStringLiteral(NodeStringLiteral node) {
        return new Value(node.getValue());
    }
}