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

public interface ASTVisitor<T> {

    // Visiting NodeExpression
    T visitNodeNumericLiteral(NodeNumericLiteral node);
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