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

    private final Set<String> booleanOperatorTypes = Set.of(
        "==",
        "!=",
        "<",
        "<=",
        ">",
        ">="
    );

    private final Map<String, Set<ValType>> binaryOperatorRules = Map.ofEntries(
        Map.entry("+", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING)),
        Map.entry("-", Set.of(ValType.NUMBER, ValType.DOUBLE)),
        Map.entry( "*", Set.of(ValType.NUMBER, ValType.DOUBLE)),
        Map.entry("/", Set.of(ValType.NUMBER, ValType.DOUBLE)),
        Map.entry("%", Set.of(ValType.NUMBER, ValType.DOUBLE)),
        Map.entry("==", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN)),
        Map.entry("!=", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN)),
        Map.entry("<", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN)),
        Map.entry("<=", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN)),
        Map.entry(">", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN)),
        Map.entry(">=", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN))
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
        if(expectedTypes.peekLast() == ValType.NUMBER || expectedTypes.peekLast() == ValType.DOUBLE) {
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
        else if(expectedTypes.peekLast() == ValType.STRING) {
            node.resolvedType = ValType.STRING;
            // We want to enforce rules on literal values but not variable references
            if(node.leftHandSide instanceof NodeLiteral && !stringExpressionTypes.contains(lhs)) {
                logger.severe(String.format("Illegal literal in string expression: %s and %s", lhs, rhs));
                throw new RuntimeException(String.format("Illegal literal in string expression: %s and %s", lhs, rhs));
            }
            return ValType.STRING;
        // Expected types being BOOLEAN means that essentialy the entire expression will be evaluated as true or false, does not mean that there can't be other expressions types in the expression
        } else if(expectedTypes.peekLast() == ValType.BOOLEAN) {
            if(booleanOperatorTypes.contains(operator) && !isComparable(lhs, rhs)) {
                    logger.info(String.format("Illegal comparison: %S and %s", lhs, rhs));
                    throw new RuntimeException(String.format("Illegal comparison: %S and %s", lhs, rhs));
            }
            // This is for comparisons
            if(booleanOperatorTypes.contains(operator)) {
                    node.resolvedType = ValType.BOOLEAN;
                    return ValType.BOOLEAN;
            } else {
                // If it's not a straight comparison we won't allow strings or chars
                if((lhs == ValType.STRING || rhs == ValType.STRING) || (rhs == ValType.CHARACTER || rhs == ValType.CHARACTER)) {
                    logger.severe("Strings and Characters can only be used in straight comparison inside boolean expressions!");
                    throw new RuntimeException("Strings and Characters can only be used in straight comparison inside boolean expressions!");
                }
                if(lhs == ValType.DOUBLE || rhs == ValType.DOUBLE) {
                    node.resolvedType = ValType.DOUBLE;
                    return ValType.DOUBLE;
                } else
                if(lhs == ValType.NUMBER || rhs == ValType.NUMBER) {
                    node.resolvedType = ValType.NUMBER;
                    return ValType.NUMBER;
                } else
                if(lhs == ValType.BOOLEAN || rhs == ValType.BOOLEAN) {
                    node.resolvedType = ValType.BOOLEAN;
                    return ValType.BOOLEAN;
                }
                // Might be all...
            }
        }
        return ValType.NULL; // This seems to be the culprit of some bugs...
    }

    @Override
    public ValType visitNodeUnaryExpression(NodeUnaryExpression node, ValType type) {
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public ValType visitNodeVariableReference(NodeVariableReference node) {
        logger.info("Checking if Symbol is present: " + node.identifier);
        if(vm.getCurrentEnv().getVariable(node.identifier) == null) throw new RuntimeException("Unrecognized symbol " + node.identifier);
        return vm.getCurrentEnv().getVariable(node.identifier).getType();
    }

    @Override
    public ValType visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        logger.info("Checking a Variable declaration...");
        vm.getCurrentEnv().declareVariable(node.identifier, new Variable(node.type, null));
        logger.info("Variable identifier is: " + node.identifier);

        // This is the type we are expecting
        ValType declaredType = node.type;
        expectedTypes.offerLast(declaredType);

        logger.info("Variable type is " + declaredType);

        // Check if the assignment matches the type
        logger.info("Checking if initializer is type-compatible...");
        if(node.initializer != null)
            node.initializer.accept(this);

        // Remove the expected type from the context
        expectedTypes.pollLast();
        return node.type;
    }

    @Override
    public ValType visitNodeExpressionStatement(NodeExpressionStatement node) {
        // WIP
        node.accept(this);
        return null;
    }

    @Override
    public ValType visitNodeAssignStatement(NodeAssignStatement node) {
        ValType type = vm.getCurrentEnv().getVariable(node.identifier).getType();

        expectedTypes.offerLast(type);
        // Check if the value we are assigning can be assigned to the current type
        if(!isAssignable(type, node.expression.accept(this))) {
            logger.severe(String.format("Found illegal assignment: %s != %s", type, node.expression.accept(this)));
            throw new RuntimeException("Illegal Assignment: " + type + "!=" + node.expression.accept(this));
        }
        expectedTypes.pollLast();

        logger.info(String.format("Type: %s is compatible with initializer: %s", type, node.expression.accept(this)));
        return node.expression.accept(this);
    }

    @Override
    public ValType visitNodeIfStatement(NodeIfStatement node) {
        logger.info("Checking an if-statement...");
        expectedTypes.offerLast(ValType.BOOLEAN);
        logger.info("Checking the condition...");
        node.booleanExpression.accept(this);
        logger.info("Condition is compatible");
        expectedTypes.pollLast();

        logger.info("Evaluating the then-block");
        node.thenBlock.accept(this);
        logger.info("The then-block has been evaluated");

        if(node.elseBlock != null) {
            logger.info("Evaluating the else-block");
            node.elseBlock.accept(this);
            logger.info("The else-block has been evaluated");
        }
        return null;
    }

    @Override
    public ValType visitNodeWhileStatement(NodeWhileStatement node) {
        logger.info("Checking a while-statement...");
        expectedTypes.offerLast(ValType.BOOLEAN);
        logger.info("Checking the condition...");
        node.booleanExpression.accept(this);
        logger.info("Condition is compatible");
        expectedTypes.pollLast();

        logger.info("Evaluating the then-block");
        node.thenBlock.accept(this);
        logger.info("The then-block has been evaluated");

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
    public ValType visitNodeBooleanLiteral(NodeBooleanLiteral node) {
        return ValType.BOOLEAN;
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

    private boolean isComparable(ValType left, ValType right) {
        if(left == right) return true;
        if((left == ValType.NUMBER && right == ValType.DOUBLE) || left == ValType.DOUBLE && right == ValType.NUMBER) return true;
        if((left == ValType.STRING && right == ValType.CHARACTER) || left == ValType.CHARACTER && right == ValType.STRING) return true;
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
    public ValType visitNodeBlock(NodeBlock node) {
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

    @Override
    public ValType visitNodeEnterScope(NodeEnterScope node) {
        vm.enterScope();
        logger.info("Entering scope, level: " + Environment.globalScopeDepth);
        return null;
    }

    @Override
    public ValType visitNodeExitScope(NodeExitScope node) {
        logger.info("Exiting scope, level: " + Environment.globalScopeDepth);
        vm.exitScope();
        return null;
    }

    public void print(Object x) {
        System.out.println(x);
    }
}