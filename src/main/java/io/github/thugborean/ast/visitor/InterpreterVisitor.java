package io.github.thugborean.ast.visitor;

import java.util.logging.Logger;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.statement.scope.*;
import io.github.thugborean.logging.LoggingManager;
import io.github.thugborean.syntax.TokenType;
import io.github.thugborean.vm.Environment;
import io.github.thugborean.vm.VM;
import io.github.thugborean.vm.symbol.*;

public class InterpreterVisitor implements ASTVisitor<Value> {
    private final static Logger logger = LoggingManager.getLogger(InterpreterVisitor.class);
    private final VM vm;

    public InterpreterVisitor(VM vm) {
        this.vm = vm;
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
                    throw new RuntimeException("Couldn't find operator for arithmetic expression!");
                }
            }
        } else if(node.resolvedType == ValType.STRING) {
            logger.info(String.format("Performing string concatenation: %s %s %s", left, node.operator.lexeme, right));
            StringBuilder sB = new StringBuilder();
            sB.append(left.toString()).append(right.toString());
            return new Value(sB.toString());
        } else if(node.resolvedType == ValType.BOOLEAN) {
            Object lval = left.raw();
            Object rval = right.raw();

            switch(node.operator.tokenType) {
                case TokenType.LessThan: {
                    return new Value(lessThan(lval, rval));
                }
                case TokenType.LessThanOrEquals: {
                    return new Value(lessThanOrEquals(lval, rval));
                }
                case TokenType.GreaterThan: {
                    return new Value(greaterThan(lval, rval));
                }
                case TokenType.GreaterThanOrEquals: {
                    return new Value(greaterThanOrEquals(lval, rval));
                }
                case TokenType.EqualsEquals: {
                    return new Value(Equals(lval, rval));
                }
                case TokenType.BangEquals: {
                    return new Value(bangEquals(lval, rval));
                }
                default: {
                    logger.severe("Unknown operator " + node.operator.lexeme);
                    throw new RuntimeException("Couldn't find operator for boolean expression!");
                }
            }
        }

        return new Value(null); // This shouldn't happen
    }

    @Override
    public Value visitNodeUnaryExpression(NodeUnaryExpression node, ValType type) {
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public Value visitNodeVariableReference(NodeVariableReference node) {
        logger.info("Getting value of reference: " + node.identifier);
        return vm.getCurrentEnv().getVariable(node.identifier).getvalue();
    }

    @Override
    public Value visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        logger.info("Declaring Variable...");
        String identifier = node.identifier;
        // Adding the variable to the table with null as a default first before assigning it
        logger.info("Adding Variable identifier to envirionment: " + identifier);
        vm.getCurrentEnv().declareVariable(identifier, new Variable(node.type, null));
        // Assign the variable with the given intializer
        logger.info("Assigning Variable with given initializer...");
        node.initializer.accept(this);
        logger.info(String.format("Finished declaring Variable: %s", identifier));
        // Nothing meaningful
        return null;
    }

    @Override
    public Value visitNodeExpressionStatement(NodeExpressionStatement node) {
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
    public Value visitNodeAssignStatement(NodeAssignStatement node) {
        String identifier = node.identifier;
        logger.info("Assigning to: " + identifier);
        Value assigned = node.expression.accept(this);
        ValType type = vm.getCurrentEnv().getVariable(node.identifier).getType();
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
        vm.getCurrentEnv().assignVariable(identifier, finalValue);
        logger.info("Finished assigning Variable: " + identifier + " -> " + finalValue);
        return null;
    }

    @Override
    public Value visitNodeIfStatement(NodeIfStatement node) {
        logger.info("Visiting if-statement...");
        Value condition = node.booleanExpression.accept(this);
        if(condition.getType() != ValType.BOOLEAN) throw new RuntimeException("If-statement condition was not a boolean, we messed up somewhere!");
        if(condition.asBool()) {
            logger.info("Condition was evaluated as true, executing then-block");
            node.thenBlock.accept(this);
        } else if(node.elseBlock != null) {
                node.elseBlock.accept(this);
                logger.info("Condition was evaluated as false, executing else-block");
        } else logger.info("Condition was evaluated as false, continuing program");
        return null;
    }

    @Override
    public Value visitNodeWhileStatement(NodeWhileStatement node) {
        logger.info("Visiting while-statement...");
        Value condition = node.booleanExpression.accept(this);
        if(condition.getType() != ValType.BOOLEAN) throw new RuntimeException("While-statement condition was not a boolean, we messed up somewhere!");


        if(condition.asBool()) {
            while(condition.asBool()) {
                logger.info("Condition was evaluated to true, executing then-block");
                node.thenBlock.accept(this);
                condition = node.booleanExpression.accept(this); // REMEMBER TO UPDATE CONDITION
            }
        } else logger.info("Condition was evaluated as false, continuing program");

        logger.info("Breaking the while-loop");
        return null;
    }

    public void print(Object x) {
        System.out.println(x);
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
    public Value visitNodeBooleanLiteral(NodeBooleanLiteral node) {
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

    @Override
    public Value visitNodeBlock(NodeBlock node) {
        vm.enterScope();
        logger.info("Entering scope, level: " + Environment.globalScopeDepth);
        try {
            for(NodeStatement statement : node.statements) statement.accept(this);
        } finally {
            vm.exitScope();
            logger.info("Exiting scope, level: " + Environment.globalScopeDepth);
        }
        return null;
    }

    // SCOPE
    @Override
    public Value visitNodeEnterScope(NodeEnterScope node) {
        vm.enterScope();
        logger.info("Entering scope, level: " + Environment.globalScopeDepth);
        return null;
    }

    @Override
    public Value visitNodeExitScope(NodeExitScope node) {
        logger.info("Exiting scope, level: " + Environment.globalScopeDepth);
        vm.exitScope();
        return null;
    }

    private Boolean lessThan(Object l, Object r) {
        if(!(l instanceof Integer || l instanceof Double) || !(r instanceof Integer || r instanceof Double))
            throw new RuntimeException("The typechecker messed up! We shouldn't get to this point!");
        // Abomination...
        if(l instanceof Integer li && r instanceof Integer ri) return Boolean.valueOf(li < ri);
        if(l instanceof Double ld && r instanceof Double rd) return Boolean.valueOf(ld < rd);
        if(l instanceof Integer li && r instanceof Double rd) return Boolean.valueOf(li < rd);
        if(l instanceof Double ld && r instanceof Integer ri) return Boolean.valueOf(ld < ri);

        return false;
    }
    private Boolean lessThanOrEquals(Object l, Object r) {
        if(!(l instanceof Integer || l instanceof Double) || !(r instanceof Integer || r instanceof Double))
            throw new RuntimeException("The typechecker messed up! We shouldn't get to this point!");
        // Abomination...
        if(l instanceof Integer li && r instanceof Integer ri) return Boolean.valueOf(li <= ri);
        if(l instanceof Double ld && r instanceof Double rd) return Boolean.valueOf(ld <= rd);
        if(l instanceof Integer li && r instanceof Double rd) return Boolean.valueOf(li <= rd);
        if(l instanceof Double ld && r instanceof Integer ri) return Boolean.valueOf(ld <= ri);

        return false;
    }
        private Boolean greaterThan(Object l, Object r) {
            if(!(l instanceof Integer || l instanceof Double) || !(r instanceof Integer || r instanceof Double))
                throw new RuntimeException("The typechecker messed up! We shouldn't get to this point!");
            // Abomination...
            if(l instanceof Integer li && r instanceof Integer ri) return Boolean.valueOf(li > ri);
            if(l instanceof Double ld && r instanceof Double rd) return Boolean.valueOf(ld > rd);
            if(l instanceof Integer li && r instanceof Double rd) return Boolean.valueOf(li > rd);
            if(l instanceof Double ld && r instanceof Integer ri) return Boolean.valueOf(ld > ri);

            return false;
    }
        private Boolean greaterThanOrEquals(Object l, Object r) {
            if(!(l instanceof Integer || l instanceof Double) || !(r instanceof Integer || r instanceof Double))
                throw new RuntimeException("The typechecker messed up! We shouldn't get to this point!");
            // Abomination...
            if(l instanceof Integer li && r instanceof Integer ri) return Boolean.valueOf(li >= ri);
            if(l instanceof Double ld && r instanceof Double rd) return Boolean.valueOf(ld >= rd);
            if(l instanceof Integer li && r instanceof Double rd) return Boolean.valueOf(li >= rd);
            if(l instanceof Double ld && r instanceof Integer ri) return Boolean.valueOf(ld >= ri);

            return false;
    }
        private Boolean Equals(Object l, Object r) {
            return Boolean.valueOf(l.equals(r));
    }
        private Boolean bangEquals(Object l, Object r) {
            return Boolean.valueOf(!l.equals(r));
    }
}