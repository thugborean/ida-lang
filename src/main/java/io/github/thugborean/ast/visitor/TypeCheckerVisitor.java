package io.github.thugborean.ast.visitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.types.*;
import io.github.thugborean.logging.LoggingManager;
import io.github.thugborean.vm.symbol.ValType;

public class TypeCheckerVisitor implements ASTVisitor<ValType> {
    // Create the logger and give it the class' name
    private static final Logger logger = LoggingManager.getLogger(TypeCheckerVisitor.class);
    private final Map<String, ValType> symbolTable = new HashMap<>();
    private final Deque<ValType> expectedTypes = new ArrayDeque<>();
    private final Set<ValType> reugularExpressionTypes = Set.of(
        ValType.NUMBER,
        ValType.DOUBLE
    );

    private final Map<String, Set<ValType>> binaryOperatorRules = Map.of(
        "+", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING),
        "-", Set.of(ValType.NUMBER, ValType.DOUBLE),
        "*", Set.of(ValType.NUMBER, ValType.DOUBLE),
        "/", Set.of(ValType.NUMBER, ValType.DOUBLE),
        "%", Set.of(ValType.NUMBER, ValType.DOUBLE),
        "==", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN)
    );

    @Override
    public void walkTree(Program program) {
        logger.info("TypeChecking Program...");
        for(NodeStatement statement : program.nodes) {
            statement.accept(this);
        }
        logger.info("Finished TypeChecking Program!");
        logger.info("Complete Symbol Table...");
        logger.info("##################################################");
        for(Map.Entry<String, ValType> entry : symbolTable.entrySet()) {
            logger.info(String.format("Identifier %-15s, Type: %-10s", entry.getKey(), entry.getValue()));
        }
        logger.info("##################################################");
    }

    @Override
    public ValType visitNodeBinaryExpression(NodeBinaryExpression node, ValType type) {
        ValType lhs = node.leftHandSide.accept(this);
        ValType rhs = node.rightHandSide.accept(this);
        logger.info(String.format("Checking Binary Expression, types %s, %s", lhs, rhs));
        if (!reugularExpressionTypes.contains(lhs) || !reugularExpressionTypes.contains(rhs)) {
            logger.severe(String.format("Illegal type in numeric expression: %s + %s!", lhs, rhs));
            throw new RuntimeException(String.format("Illegal type in numeric expression: %s + %s!", lhs, rhs));
        }
        // If at least one of the sides are decimal then we're dealing with a Double
        if(lhs == ValType.DOUBLE || rhs == ValType.DOUBLE) return ValType.DOUBLE;
            else return ValType.NUMBER;
    }

    @Override
    public ValType visitUnaryExpression(NodeUnaryExpression node, ValType type) {
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public ValType visitNodeVariableReference(NodeVariableReference node) {
        logger.info("Checking if Symbol is present: " + node.identifier);
        if(symbolTable.containsKey(node.identifier)) return symbolTable.get(node.identifier);
            else throw new RuntimeException("Unrecognized Symbol: " + node.identifier);
    }

    @Override
    public ValType visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        logger.info("Checking a variable declaration...");
        symbolTable.put(node.identifier.lexeme, node.type.type);
        logger.info("Variable identifier is: " + node.identifier.lexeme);

        ValType declaredType = node.type.type;
        logger.info("Variable type is " + node.type.type);

        // Check if the assignment matches the type
        logger.info("Checking if initializer is type-compatible...");
        ValType initType = node.initializer.accept(this);

        logger.info(String.format("Type: %s is compatible with initializer: %s", declaredType, initType));
        return node.type.type;
    }

    @Override
    public ValType visitExpressionStatement(NodeExpressionStatement node) {
        node.accept(this);
        return null;
    }

    @Override
    public ValType visitAssignStatement(NodeAssignStatement node) {
        ValType type = symbolTable.get(node.identifier);
        if(!isAssignable(type, node.assignedValue.accept(this))) {
            logger.severe(String.format("Found illegal assignment: %s != %s", type, node.assignedValue.accept(this)));
            throw new RuntimeException("Illegal Assignment: " + type + "!=" + node.assignedValue.accept(this));
        }
        return node.assignedValue.accept(this);
    }

    // DEPRICATED
    public ValType visitNodeType(NodeType node) {
        return null;
    }
    // This vistitor needs these specific methods
    @Override
    public ValType visitNodeNumericLiteral(NodeNumericLiteral node) {
        return ValType.NUMBER;
    }
    @Override
    public ValType visitNodeDoubleLiteral(NodeDoubleLiteral node) {
        return ValType.DOUBLE;
    }
    @Override
    public ValType visitNodeStringLiteral(NodeStringLiteral node) {
        return ValType.STRING;
    }
    @Override
    public ValType visitNodeNullLiteral(NodeNullLiteral node) {
        return ValType.NULL;
    }

    private boolean isAssignable(ValType declared, ValType actual) {
        if(declared == actual) return true;
        if(declared == ValType.STRING && actual == ValType.CHARACTER) return true;
        if(declared == ValType.STRING && (actual == ValType.NUMBER || actual == ValType.DOUBLE)) return false;
        if(declared == ValType.NUMBER && actual == ValType.DOUBLE) return false;
        if(declared == ValType.DOUBLE && actual == ValType.NUMBER) return true; // Is allowed 
        return false;
    }

    private boolean isValidInExpression(String operator, ValType type) {
        return false;
    }

    @Override
    public ValType visitNodeIncrement(NodeIncrement nodeIncrement) {
        return null;
    }

    @Override
    public ValType visitNodeDecrement(NodeDecrement node) {
        return null;
    }

    @Override
    public ValType visitNodePrintStatement(NodePrintStatement node) {
        logger.info("Checking Print Statement...");
        // Checks if the value can be printed
        node.printable.accept(this);
        logger.info("Print Statement has passed");
        return null;
    }

    public void print(Object x) {
        System.out.println(x);
    }
}