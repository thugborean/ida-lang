package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeUnaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.NodeAssignStatement;
import io.github.thugborean.ast.node.statement.NodeExpressionStatement;
import io.github.thugborean.ast.node.statement.NodePrintStatement;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.vm.Environment;

public class TypeCheckerVisitor implements ASTVisitor<Object>{
    private Environment environment;

    public TypeCheckerVisitor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void walkTree(Program program) {
        // TODO Auto-generated method stub
        
    }

    // This vistitor may want these methods
    public Object visitNodeNumericLiteral(NodeNumericLiteral node) {
        
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeNumericLiteral'");
    } 
    public Object visitNodeDoubleLiteral(NodeDoubleLiteral node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeDoubleLiteral'");
    }
    public Object visitStringLiteral(NodeStringLiteral node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitStringLiteral'");
    }
    @Override
    public Object visitNodeLiteral(NodeLiteral node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeLiteral'");
    }

    @Override
    public Object visitNodeBinaryExpression(NodeBinaryExpression node) {
        node.leftHandSide.accept(this);
        node.rightHandSide.accept(this);
        return null;
    }

    @Override
    public Object visitUnaryExpression(NodeUnaryExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public Object visitNodeVariableReference(NodeVariableReference node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeVariableReference'");
    }

    @Override
    public Object visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeVariableDeclaration'");
    }

    @Override
    public Object visitExpressionStatement(NodeExpressionStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitExpressionStatement'");
    }

    @Override
    public Object visitNodePrintStatement(NodePrintStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodePrintStatement'");
    }

    @Override
    public Object visitAssignStatement(NodeAssignStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignStatement'");
    }

    // Not always needed
    public Object visitNodeType(NodeType node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeType'");
    }

    @Override
    public Object visitNodeStringLiteral(NodeStringLiteral node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeStringLiteral'");
    }
}