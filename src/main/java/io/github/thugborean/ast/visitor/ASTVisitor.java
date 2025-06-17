package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;

public interface ASTVisitor<T> {

    // Visiting NodeExpression
    T visitNodeNumericLiteral(NodeNumericLiteral node);
    T visitNodeBinaryExpression(NodeBinaryExpression node);
    T visitNodeVariableReference(NodeVariableReference node);

    // Visiting NodeStatement
    T visitNodeVariableDeclaration(NodeVariableDeclaration node);
}
