package io.github.thugborean.ast.visitor;

import java.util.logging.Logger;
import java.util.stream.Stream;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.types.*;
import io.github.thugborean.logging.LoggingManager;
import io.github.thugborean.syntax.TokenType;
import io.github.thugborean.vm.Environment;
import io.github.thugborean.vm.symbol.*;

public class InterpreterVisitor implements ASTVisitor<Value> {
    private Environment environment;
    private final static Logger logger = LoggingManager.getLogger(InterpreterVisitor.class);

    public InterpreterVisitor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void walkTree(Program program) {
        logger.info("Interpreting Program...");
        for (NodeStatement statement : program.nodes) {
            statement.accept(this);
        }
        logger.info("Finished Interpreting Program!");
    }

    @Override
    public Value visitNodeBinaryExpression(NodeBinaryExpression node, ValType type) {
        Value left = (Value)node.leftHandSide.accept(this);
        Value right = (Value)node.rightHandSide.accept(this);

        if(node.resolvedType == ValType.NUMBER || node.resolvedType == ValType.DOUBLE) {
            logger.info(String.format("Performing arithmetic: %s %s %s", left, node.operator.lexeme, right));
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
                default: {
                    logger.severe("Unknown operator " + node.operator.lexeme);
                    throw new RuntimeException("Interpreter Error: Couldn't find operator for expression!");
                }
            }
        } else if(node.resolvedType == ValType.STRING) {
            logger.info(String.format("Performing string concatenation: %s %s %s", left, node.operator.lexeme, right));
            StringBuilder sB = new StringBuilder();
            sB.append(left).append(right);
            return new Value(sB.toString());
        }

        return new Value(null); // This shouldn't happen
    }

    @Override
    public Value visitUnaryExpression(NodeUnaryExpression node, ValType type) {
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public Value visitNodeVariableReference(NodeVariableReference node) {
        logger.info("Getting value of reference: " + node.identifier);
        return environment.getVariable(node.identifier).getvalue();
    }

    @Override
    public Value visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        logger.info("Declaring Variable...");
        String identifier = node.identifier.lexeme;
        // Adding the variable to the table with null as a default first before assigning it
        logger.info("Adding Variable identifier to envirionment: " + identifier);
        environment.declareVariable(identifier, new Variable(node.type.type, null));
        // Assign the variable with the given intializer
        logger.info("Assigning Variable with given initializer...");
        node.initializer.accept(this);
        logger.info(String.format("Finished declaring Variable: %s", identifier));
        // Nothing meaningful
        return null;
    }

    @Override
    public Value visitExpressionStatement(NodeExpressionStatement node) {

        throw new UnsupportedOperationException("Unimplemented method 'visitExpressionStatement'");
    }

    @Override
    public Value visitNodePrintStatement(NodePrintStatement node) {
        logger.info("Printing to output...");
        Value printable = node.printable.accept(this);
        // Print the pritable
        System.out.println(printable);
        logger.info("Finished printing to output");
        // Nothing meaningful
        return null;
    }

    // This function is a total disaster
    // This defines the new variable and gets its type and value
    @Override
    public Value visitAssignStatement(NodeAssignStatement node) {
        String identifier = node.identifier;
        logger.info("Assigning to: " + identifier);
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
            else {
                logger.severe(String.format("Couldn't assign %s to %s", assigned.toString(), identifier));
                throw new RuntimeException("Cannot Assign");
            }
        } else {
            // Doing some autoboxing and magical casting
            if(raw instanceof Double) {
                double d = (Double)raw;
                Integer i = (int)d;
                finalValue = new Value(i);
            } else finalValue = new Value(raw);
        }
        // Finally assign the variable
        environment.assignVariable(identifier, finalValue);
        logger.info("Finished assigning Variable: " + identifier);
        return null;
    }

    public void print(Object x) {
    System.out.println(x);
    }

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
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeIncrement'");
    }

    @Override
    public Value visitNodeDecrement(NodeDecrement node) {
        throw new UnsupportedOperationException("Unimplemented method 'visiNodeDecrement'");
    }
}