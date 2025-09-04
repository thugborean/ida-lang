package io.github.thugborean.ast.visitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.statement.scope.NodeEnterScope;
import io.github.thugborean.ast.node.statement.scope.NodeExitScope;
import io.github.thugborean.logging.LoggingManager;
import io.github.thugborean.vm.Environment;
import io.github.thugborean.vm.VM;
import io.github.thugborean.vm.symbol.*;

public class TypeCheckerVisitor implements ASTVisitor<ValType> {
    // Create the logger and give it the class' name
    private static final Logger logger = LoggingManager.getLogger(TypeCheckerVisitor.class);
    private final Deque<ValType> expectedTypes = new ArrayDeque<>();
    private final VM vm;

    public TypeCheckerVisitor(Environment environment, VM vm) {
        this.vm = vm;
    }

    private final Set<ValType> reugularExpressionTypes = Set.of(
        ValType.NUMBER,
        ValType.DOUBLE
    );

    // Types of literals that can be present within string expressions
    private final Set<ValType> stringExpressionTypes = Set.of(
        ValType.STRING,
        ValType.CHARACTER
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
    }

    @Override
    public ValType visitNodeBinaryExpression(NodeBinaryExpression node, ValType type) {
        ValType lhs = node.leftHandSide.accept(this);
        ValType rhs = node.rightHandSide.accept(this);
        String operator = node.operator.lexeme;
        // Does an overall check for rules regarding the binary operator
        if(!(binaryOperatorRules.get(operator).contains(lhs)) || !(binaryOperatorRules.get(operator).contains(rhs))) {
            logger.severe(String.format("Operator %s does not support operands %s and %s", operator, lhs, rhs));
            throw new RuntimeException(String.format("Operator %s does not support operands %s and %s", operator, lhs, rhs));
        }
        logger.info(String.format("Checking Binary Expression, types %s, %s", lhs, rhs));
        // This means we're dealing with an arithmetic expression
        if(expectedTypes.peek() == ValType.NUMBER || expectedTypes.peek() == ValType.DOUBLE) {
            node.resolvedType = ValType.NUMBER;
            if (!reugularExpressionTypes.contains(lhs) || !reugularExpressionTypes.contains(rhs)) {
                logger.severe(String.format("Illegal type in arithmetic expression: %s and %s!", lhs, rhs));
                throw new RuntimeException(String.format("Illegal type in arithmetic expression: %s and %s!", lhs, rhs));
            }
            // If at least one of the sides are decimal then we're dealing with a Double
            if(lhs == ValType.DOUBLE || rhs == ValType.DOUBLE) {
                node.resolvedType = ValType.DOUBLE;
                return ValType.DOUBLE;
            }
            else return ValType.NUMBER;
        }
        // This means we're dealing with a string expression
        else if(expectedTypes.peek() == ValType.STRING) {
            node.resolvedType = ValType.STRING;
            // We want to enforce rules on literal values but not variable references
            if(node.leftHandSide instanceof NodeLiteral && !stringExpressionTypes.contains(lhs)) {
                logger.severe(String.format("Illegal literal in string expression: %s + %s", lhs, rhs));
                throw new RuntimeException(String.format("Illegal literal in string expression: %s + %s", lhs, rhs));
            }
            return ValType.STRING;
        }

        return ValType.NULL;
    }

    @Override
    public ValType visitNodeUnaryExpression(NodeUnaryExpression node, ValType type) {
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public ValType visitNodeVariableReference(NodeVariableReference node) {
        logger.info("Checking if Symbol is present: " + node.identifier);
        if(!vm.getCurrentEnv().variableExists(node.identifier)) throw new RuntimeException("Unrecognized symbol " + node.identifier);
        return vm.getCurrentEnv().getVariable(node.identifier).getType();
    }

    @Override
    public ValType visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        logger.info("Checking a Variable declaration...");
        vm.getCurrentEnv().declareVariable(node.identifier.lexeme, new Variable(node.type.type, null));
        logger.info("Variable identifier is: " + node.identifier.lexeme);

        // This is the type we are expecting
        ValType declaredType = node.type.type;
        expectedTypes.offerFirst(declaredType);

        logger.info("Variable type is " + node.type.type);

        // Check if the assignment matches the type
        logger.info("Checking if initializer is type-compatible...");
        ValType initType = node.initializer.accept(this);
        logger.info(String.format("Type: %s is compatible with initializer: %s", declaredType, initType));
        // Remove the expected type from the context
        expectedTypes.pollFirst();
        return node.type.type;
    }

    @Override
    public ValType visitExpressionStatement(NodeExpressionStatement node) {
        // WIP
        node.accept(this);
        return null;
    }

    @Override
    public ValType visitAssignStatement(NodeAssignStatement node) {
        ValType type = vm.getCurrentEnv().getVariable(node.identifier).getType();
        // Check if the value we are assigning can be assigned to the current type
        if(!isAssignable(type, node.assignedValue.accept(this))) {
            logger.severe(String.format("Found illegal assignment: %s != %s", type, node.assignedValue.accept(this)));
            throw new RuntimeException("Illegal Assignment: " + type + "!=" + node.assignedValue.accept(this));
        }
        return node.assignedValue.accept(this);
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

    @Override
    public ValType visitNodeEnterScope(NodeEnterScope node) {
        vm.enterScope();
        return null;
    }

    @Override
    public ValType visitNodeExitScope(NodeExitScope node) {
        vm.exitScope();
        return null;
    }

    public void print(Object x) {
        System.out.println(x);
    }
}