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
import io.github.thugborean.logging.LoggingManager;
import io.github.thugborean.vm.Environment;
import io.github.thugborean.vm.VM;
import io.github.thugborean.vm.symbol.*;

// TODO: Fix variables being able to use themselves upon declaration, fix double casting inside boolean expressions, fix better context and resolved types, fix return checker for typechecker
// fix function calls into expressions
public class TypeCheckerVisitor implements ASTVisitor<ValType> {
    // Create the logger and give it the class name
    private static final Logger logger = LoggingManager.getLogger(TypeCheckerVisitor.class);
    private final Deque<ValType> contextStack = new ArrayDeque<>();
    private NodeFunctionDeclaration currentFunction;
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
        Map.entry(">=", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN)),
        Map.entry("=", Set.of(ValType.NUMBER, ValType.DOUBLE, ValType.CHARACTER, ValType.STRING, ValType.BOOLEAN))
    );

    @Override
    public void walkTree(Program program) {
        logger.info("Populating function pool...");
        populateFunctionPool(program);
        logger.info("Finished populating function pool");
        logger.info("TypeChecking Program...");
        for(NodeStatement statement : program.nodes) {
            statement.accept(this);
        }
        // If we don't find the entry function
        String entryName = vm.functionPool.entryName;
        if(!vm.functionPool.checkForEntry()) {
            logger.severe("No entry point found! Function with name " + entryName + " required!");
            throw new RuntimeException("No entry point found! Function with name " + entryName + " required!");
        } else {
            vm.entryPoint = vm.functionPool.getFunction(entryName);
            logger.info("Entry point: " + entryName + " has been set");
        }
        logger.info("Finished TypeChecking Program!");
    }

    public void populateFunctionPool(Program program) {
        for(NodeStatement statement : program.nodes) {
            if(statement instanceof NodeFunctionDeclaration) {
                NodeFunctionDeclaration functionDeclaration = (NodeFunctionDeclaration)statement;
                // We only need the identifier and the return type for now
                vm.functionPool.declareFunction(functionDeclaration.identifier, new Function(functionDeclaration.returnType, null, null, null));
            }
        }
    } 

    @Override
    public ValType visitNodeBinaryExpression(NodeBinaryExpression node, ValType type) {
        ValType lhs = node.leftHandSide.accept(this);
        ValType rhs = node.rightHandSide.accept(this);
        String operator = node.operator.lexeme;

        // BANDAID
        if (operator.equals("=")) {
            contextStack.push(lhs);
            rhs = node.rightHandSide.accept(this);
            contextStack.pop();

            if (!isAssignable(lhs, rhs)) {
                logger.severe(String.format("Cannot assign %s to %s", lhs, rhs));
                throw new RuntimeException(String.format("Cannot assign %s to %s", lhs, rhs));
            }
            node.resolvedType = lhs; // or rhs
            return node.resolvedType;
        }
        
        // Does an overall check for rules regarding the binary operators
        if(!(binaryOperatorRules.get(operator).contains(lhs)) || !(binaryOperatorRules.get(operator).contains(rhs))) {
            logger.severe(String.format("Operator %s does not support operands %s and %s", operator, lhs, rhs));
            throw new RuntimeException(String.format("Operator %s does not support operands %s and %s", operator, lhs, rhs));
        }
        logger.info(String.format("Checking Binary Expression, types %s, %s", lhs, rhs));
        // This means we're dealing with an arithmetic expression
        if(contextStack.peekLast() == ValType.NUMBER || contextStack.peekLast() == ValType.DOUBLE) {
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
        else if(contextStack.peekLast() == ValType.STRING) {
            node.resolvedType = ValType.STRING;
            // We want to enforce rules on literal values but not variable references
            if(node.leftHandSide instanceof NodeLiteral && !stringExpressionTypes.contains(lhs)) {
                logger.severe(String.format("Illegal literal in string expression: %s and %s", lhs, rhs));
                throw new RuntimeException(String.format("Illegal literal in string expression: %s and %s", lhs, rhs));
            }
            return ValType.STRING;
        // Expected types being BOOLEAN means that essentialy the entire expression will be evaluated as true or false, does not mean that there can't be other expressions types in the expression
        } else if(contextStack.peekLast() == ValType.BOOLEAN) {
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
        if(vm.getCurrentEnv().getVariable(node.identifier) == null) {
            logger.severe("Unrecognized symbol " + node.identifier);
            throw new RuntimeException("Unrecognized symbol " + node.identifier);
        }
        return vm.getCurrentEnv().getVariable(node.identifier).getType();
    }

    @Override
    public ValType visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        logger.info("Checking a Variable declaration...");
        vm.getCurrentEnv().declareVariable(node.identifier, new Variable(node.type, null));
        logger.info("Variable identifier is: " + node.identifier);

        // This is the type we are expecting
        ValType declaredType = node.type;
        contextStack.offerLast(declaredType);

        logger.info("Variable type is " + declaredType);

        // Check if the assignment matches the type
        logger.info("Checking if initializer is type-compatible...");
        ValType initializerType = null;
        if(node.initializer != null)
            initializerType = node.initializer.accept(this);
            if(!isAssignable(declaredType, initializerType)) {
                logger.severe(String.format("Cannot assign %s to %s", declaredType, initializerType));
                throw new RuntimeException(String.format("Cannot assign %s to %s", declaredType, initializerType));
            }

        // Remove the expected type from the context
        contextStack.pollLast();
        return node.type;
    }

    @Override
    public ValType visitNodeExpressionStatement(NodeExpressionStatement node) {
        node.expression.accept(this);
        return null;
    }

    @Override
    public ValType visitNodeIfStatement(NodeIfStatement node) {
        logger.info("Checking an if-statement...");
        evaluateContext(ValType.BOOLEAN);
        logger.info("Checking the condition...");
        node.booleanExpression.accept(this);
        logger.info("Condition is compatible");
        contextStack.pollLast();

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
        evaluateContext(ValType.BOOLEAN);
        logger.info("Checking the condition...");
        node.booleanExpression.accept(this);
        logger.info("Condition is compatible");
        contextStack.pollLast();

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

    private void evaluateContext(ValType type) {
        ValType currentContext = getCurrentContext();
        // ?
        if(currentContext == null) contextStack.offerLast(type);
    }

    private ValType getCurrentContext() {
        return contextStack.peekLast();
    }

    @Override
    public ValType visitNodePrintStatement(NodePrintStatement node) {
        logger.info("Checking print-statement...");
        // Checks if the value can be printed
        node.printable.accept(this);
        logger.info("Print-statement has passed");
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

    public void print(Object x) {
        System.out.println(x);
    }

    @Override
    public ValType visitNodeFunctionDeclaration(NodeFunctionDeclaration node) {
        currentFunction = node;
        try{
            for(NodeStatement statement : node.contents.statements) {
                statement.accept(this);
            }
            if(node.returnType != ValType.VOID && !doesReturn(node.contents)) {
                logger.severe("Function does not have all valid return paths!");
                throw new RuntimeException("Function does not have all valid return paths!");
            }
        } finally {
            currentFunction = null;
        }
        // If all passes then add the the body to the function, this is inneficient but will do for now
        vm.functionPool.declareFunctionBody(node.identifier, new Function(node.returnType, node.parameters, node.modifiers, node.contents));
        return null;
    }

    // It's returnType is null by default we need to figure it out, We need to know the returnType of the function that this node resides inside
    @Override
    public ValType visitNodeReturnStatement(NodeReturnStatement node) {
        // Figure out the type of the return expression
        if(node.returnValue == null) {
            if(currentFunction.returnType != ValType.VOID) {
                logger.severe("No expression after return-statement!");
                throw new RuntimeException("No expression after return-statement!");
            } else node.returnType = ValType.VOID;
        } else node.returnType = node.returnValue.accept(this);

        if(node.returnType != currentFunction.returnType) {
            logger.severe(String.format("Function does not have compatible return types %s != %s", node.returnType, currentFunction.returnType));
            throw new RuntimeException(String.format("Function does not have compatible return types %s != %s", node.returnType, currentFunction.returnType));
        }

        if(currentFunction.returnType == ValType.VOID && node.returnValue != null) {
            logger.severe("Cannot return an expression withing a void-function!");
            throw new RuntimeException("Cannot return an expression withing a void-function!");
        }
        return null;
    }

    // if and while-statements are the only nested statements for now 
    private boolean doesReturn(NodeStatement node) {
        if(node instanceof NodeReturnStatement) {
            return true;
        }
        if(node instanceof NodeIfStatement) {
            NodeIfStatement ifStatement = (NodeIfStatement)node;
            // If there is no else-block then we don't need to check it
            if(ifStatement.elseBlock == null) return false;
            return doesReturn(ifStatement.thenBlock) && doesReturn(ifStatement.elseBlock);
        }
        if(node instanceof NodeWhileStatement) {
            // NodeWhileStatement whileStatement = (NodeWhileStatement)node;
            // return doesReturn(whileStatement.thenBlock);
            return false;
        }
        if(node instanceof NodeBlock) {
            NodeBlock block = (NodeBlock)node;
            for(NodeStatement statement : block.statements) {
                if(doesReturn(statement)) return true;
            }
        }
        return false;
    }

    // WIP
    @Override
    public ValType visitNodeFunctionCall(NodeFunctionCall node) {
        return vm.functionPool.getFunction(node.identifier).returnType;
    }
}