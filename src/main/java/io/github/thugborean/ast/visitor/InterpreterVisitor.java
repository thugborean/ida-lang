package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.types.*;
import io.github.thugborean.syntax.TokenType;
import io.github.thugborean.vm.Environment;
import io.github.thugborean.vm.symbol.*;

public class InterpreterVisitor implements ASTVisitor<Value> {
    private Environment environment;

    public InterpreterVisitor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void walkTree(Program program) {
        for (NodeStatement statement : program.nodes) {
            statement.accept(this);
        }
    }

    @Override
    public Value visitNodeBinaryExpression(NodeBinaryExpression node) {
        Value left = (Value) node.leftHandSide.accept(this);
        Value right = (Value) node.rightHandSide.accept(this);
        // Determine the operator
        // Number and Double are the only types allowed in this expression
        switch (node.operator.tokenType) {
            case TokenType.Plus: {
                return new Value((left.getType() == ValType.NUMBER ? left.asNumber() : left.asDouble()) +
                        (right.getType() == ValType.NUMBER ? right.asNumber() : right.asDouble()));
            }
            case TokenType.Minus: {
                return new Value((left.getType() == ValType.NUMBER ? left.asNumber() : left.asDouble()) -
                        (right.getType() == ValType.NUMBER ? right.asNumber() : right.asDouble()));
            }
            case TokenType.Multiply: {
                return new Value((left.getType() == ValType.NUMBER ? left.asNumber() : left.asDouble()) *
                        (right.getType() == ValType.NUMBER ? right.asNumber() : right.asDouble()));
            }
            case TokenType.Divide: {
                return new Value((left.getType() == ValType.NUMBER ? left.asNumber() : left.asDouble()) /
                        (right.getType() == ValType.NUMBER ? right.asNumber() : right.asDouble()));
            }
            case TokenType.Modulo: {
                return new Value((left.getType() == ValType.NUMBER ? left.asNumber() : left.asDouble()) %
                        (right.getType() == ValType.NUMBER ? right.asNumber() : right.asDouble()));
            }
            default:
                throw new RuntimeException("Interpreter Error: Couldn't find operator for expression!");
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
        // Adding the variable to the table with null as a default first before
        // assigning it
        environment.defineVariable(node.identifier.lexeme, new Variable(node.type.type, null));
        // Assign the variable with the given intializer
        node.initializer.accept(this);
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
        Value printable = (Value) node.printable.accept(this);
        // Print the pritable
        System.out.println(printable);
        // Nothing meaningful
        return null;
    }

    // This function is a total disaster
    // This defines the new variable and gets its type and value
    @Override
    public Value visitAssignStatement(NodeAssignStatement node) {
        Value assigned = node.assignedValue.accept(this);
        ValType type = environment.getVariable(node.identifier).getType();
        Object raw = assigned.value;
        Value finalValue;
        // Check if casting is needed
        if (type == ValType.DOUBLE) {
            if (assigned.getType() == ValType.NUMBER)
                finalValue = new Value(((Integer)raw).doubleValue());
            else if (assigned.getType() == ValType.DOUBLE)
                finalValue = assigned; // ???? redundant mayhaps
            else if (assigned.getType() == ValType.NULL)
                finalValue = null;
            else
                throw new RuntimeException("Cannot Assign");
        } else {
            // Doing some autoboxing and magical casting
            if(raw instanceof Double) {
                double d = (Double)raw;
                Integer i = (int)d;
                finalValue = new Value(i);
            } else finalValue = new Value(raw);
        }


        // Finally assign the variable
        environment.assignVariable(node.identifier, new Variable(type, finalValue));
        return null;
    }

    // private void print(Object x) {
    // System.out.println(x);
    // }

    // NEVER USED NEVER USED!!!!!!!
    public Value visitNodeType(NodeType node) {
        return null;
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

    @Override
    public Value visitNodeNullLiteral(NodeNullLiteral node) {
        return new Value(null);
    }

    @Override
    public Value visitNodeIncrement(NodeIncrement nodeIncrement) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeIncrement'");
    }

    @Override
    public Value visitNodeDecrement(NodeDecrement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visiNodeDecrement'");
    }

    @Override
    public Value visitStringExpression(NodeStringExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitStringExpression'");
    }
}