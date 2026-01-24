package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.vm.symbol.ValType;

public class NodeExpressionStatement extends NodeStatement {
    public NodeExpression expression;
    public ValType resolvedType;

    public NodeExpressionStatement(NodeExpression expression) {
        this.expression = expression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeExpressionStatement(this);
    }
}