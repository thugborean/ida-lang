package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeUnaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodeAssignStatement;
import io.github.thugborean.ast.node.statement.NodeExpressionStatement;
import io.github.thugborean.ast.node.statement.NodePrintStatement;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeType;

public class InterpreterVisitor implements ASTVisitor{

    @Override
    public Object visitNodeNumericLiteral(NodeNumericLiteral node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeNumericLiteral'");
    }

    @Override
    public Object visitNodeBinaryExpression(NodeBinaryExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeBinaryExpression'");
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

    @Override
    public Object visitNodeType(NodeType node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeType'");
    }
    
}
