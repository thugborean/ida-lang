package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeType;

public interface ASTVisitor<T> {

    // Visiting NodeExpression
    T visitNodeNumericLiteral(NodeNumericLiteral node);
    T visitNodeBinaryExpression(NodeBinaryExpression node);
    T visitNodeVariableReference(NodeVariableReference node);

    // Visiting NodeStatement
    T visitNodeVariableDeclaration(NodeVariableDeclaration node);

    //Visit misc
    T visitNodeType(NodeType node);
    T visitProgram(Program program);
}
