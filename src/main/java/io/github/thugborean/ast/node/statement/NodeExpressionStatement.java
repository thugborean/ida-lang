package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeExpressionStatement extends NodeStatement {
    public String identifier;
    public NodeExpression value;

    public NodeExpressionStatement(String identifier, NodeExpression value) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }
}