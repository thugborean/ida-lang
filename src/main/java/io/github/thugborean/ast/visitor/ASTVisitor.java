package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.statement.scope.*;
import io.github.thugborean.ast.node.types.*;
import io.github.thugborean.vm.symbol.ValType;

public interface ASTVisitor<T> {

    void walkTree(Program program);
    // Visiting NodeExpression
    // For all the literals
    // T visitNodeLiteral(NodeLiteral node);
    T visitNodeNumericLiteral(NodeNumericLiteral node);
    T visitNodeDoubleLiteral(NodeDoubleLiteral node);
    T visitNodeStringLiteral(NodeStringLiteral node);
    // T visitNodeCharacterLiteral(NodeCharacterLiteral node);
    T visitNodeBooleanLiteral(NodeBooleanLiteral node);
    T visitNodeNullLiteral(NodeNullLiteral node);
    // Arithmetic Expressions
    T visitNodeBinaryExpression(NodeBinaryExpression node, ValType type);
    T visitNodeUnaryExpression(NodeUnaryExpression node, ValType type);
    T visitNodeVariableReference(NodeVariableReference node);
    // Stand-alone expressions
    T visitNodeIncrement(NodeIncrement node);
    T visitNodeDecrement(NodeDecrement node);
    // Visiting NodeStatement
    T visitNodeVariableDeclaration(NodeVariableDeclaration node);
    T visitNodeExpressionStatement(NodeExpressionStatement node);
    T visitNodePrintStatement(NodePrintStatement node);
    T visitNodeAssignStatement(NodeAssignStatement node);
    // Visit NodeScope
    T visitNodeEnterScope(NodeEnterScope node);
    T visitNodeExitScope(NodeExitScope node);
    //Visit misc
    default T visitNodeType(NodeType node){return null;}
    default T visitNodeBlock(NodeBlock nodeBlock){return null;}
    default T visitNodeIfStatement(NodeIfStatement nodeIfStatement){return null;}
}