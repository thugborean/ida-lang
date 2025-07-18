package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.types.*;

public interface ASTVisitor<T> {

    void walkTree(Program program);
    // Visiting NodeExpression
    // For all the literals
    // T visitNodeLiteral(NodeLiteral node);
    T visitNodeNumericLiteral(NodeNumericLiteral node);
    T visitNodeDoubleLiteral(NodeDoubleLiteral node);
    T visitNodeStringLiteral(NodeStringLiteral node);
    // T visitNodeCharacterLiteral(NodeCharacterLiteral node);
    // T visitNodeBooleanLiteral(NodeBooleanLiteral node);
    T visitNodeNullLiteral(NodeNullLiteral node);
    // Arithmetic Expressions
    T visitNodeBinaryExpression(NodeBinaryExpression node);
    T visitUnaryExpression(NodeUnaryExpression node);
    T visitNodeVariableReference(NodeVariableReference node);
    // Stand-alone expressions
    T visitNodeIncrement(NodeIncrement node);
    T visitNodeDecrement(NodeDecrement node);
    T visitStringExpression(NodeStringExpression node);
    // Visiting NodeStatement
    T visitNodeVariableDeclaration(NodeVariableDeclaration node);
    T visitExpressionStatement(NodeExpressionStatement node);
    T visitNodePrintStatement(NodePrintStatement node);
    T visitAssignStatement(NodeAssignStatement node);
    //Visit misc
    T visitNodeType(NodeType node);
}