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

public interface ASTVisitor<T> {

    void walkTree(Program program);
    // Visiting NodeExpression
    // For all the literals
    T visitNodeLiteral(NodeLiteral node);
    T visitNodeNumericLiteral(NodeNumericLiteral node);
    T visitNodeDoubleLiteral(NodeDoubleLiteral node);
    T visitNodeStringLiteral(NodeStringLiteral node);
    T visitNodeBinaryExpression(NodeBinaryExpression node);
    T visitUnaryExpression(NodeUnaryExpression node);
    T visitNodeVariableReference(NodeVariableReference node);

    // Visiting NodeStatement
    T visitNodeVariableDeclaration(NodeVariableDeclaration node);
    T visitExpressionStatement(NodeExpressionStatement node);
    T visitNodePrintStatement(NodePrintStatement node);
    T visitAssignStatement(NodeAssignStatement node);

    //Visit misc
    T visitNodeType(NodeType node);
    // T visitProgram(Program program);
}