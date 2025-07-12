package io.github.thugborean.ast.visitor;

import java.util.HashMap;
import java.util.Map;

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
import io.github.thugborean.vm.Environment;
import io.github.thugborean.vm.symbol.ValType;

public class TypeCheckerVisitor implements ASTVisitor<ValType>{
    // private Environment environment;
    private final Map<String, ValType> symbolTable = new HashMap<>();
    public TypeCheckerVisitor(Environment environment) {
        // this.environment = environment;
    }

    @Override
    public void walkTree(Program program) {
        for(NodeStatement statement : program.nodes) {
            statement.accept(this);
        }
    }

    @Override
    public ValType visitNodeBinaryExpression(NodeBinaryExpression node) {
        ValType lhs = node.leftHandSide.accept(this);
        ValType rhs = node.rightHandSide.accept(this);

        // If at least one of the sides are decimal then we're dealing with a Double
        if(lhs == ValType.DOUBLE || rhs == ValType.DOUBLE) return ValType.DOUBLE;
            else return ValType.NUMBER;
    }

    @Override
    public ValType visitUnaryExpression(NodeUnaryExpression node) {
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public ValType visitNodeVariableReference(NodeVariableReference node) {
        if(symbolTable.containsKey(node.identifier)) return symbolTable.get(node.identifier);
            else throw new RuntimeException("Unrecognized Symbol: " + node.identifier);
    }

    @Override
    public ValType visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        symbolTable.put(node.identifier.lexeme, node.type.type);
        // Checkk if the assignment matches the type
        if(!isAssignable(node.type.type, node.initializer.accept(this)))
            throw new RuntimeException("Illegal Assignment: " + node.type.type + "!=" + node.initializer.accept(this));
        else return node.type.type;
    }

    @Override
    public ValType visitExpressionStatement(NodeExpressionStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitExpressionStatement'");
    }

    @Override
    public ValType visitNodePrintStatement(NodePrintStatement node) {
        ValType valType = node.printable.accept(this);
        // Checks if the value can be printed
        if(isPrintable(valType)) return ValType.VOID;
            else throw new RuntimeException("Cannot print value of type: " + valType);
    }

    @Override
    public ValType visitAssignStatement(NodeAssignStatement node) {
        return node.assignedValue.accept(this);
    }

    // Not always needed - DEPRICATED
    public ValType visitNodeType(NodeType node) {
        return null;
    }

    // This vistitor may want these methods
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
        // Assigning a Double to a Number
        if(declared == ValType.NUMBER && actual == ValType.DOUBLE) return false;
        // Assigning a Number to a double 
        if(declared == ValType.DOUBLE && actual == ValType.NUMBER) return true;
        // Assigning a String to a Character
        if(declared == ValType.CHARACTER && actual == ValType.STRING) return false;
        // Assigning a Character to a String
        if(declared == ValType.STRING && actual == ValType.CHARACTER) return true;
        // Assigning Null to anything
        if(actual == ValType.NULL) return true;

        return false;
    }

    private boolean isPrintable(ValType printable) {
        if(printable == ValType.NULL || printable == ValType.VOID) return false;
            else return true;
    }
}