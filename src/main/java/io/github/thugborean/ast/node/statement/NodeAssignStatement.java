package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.ast.node.expression.NodeExpression;

public class NodeAssignStatement extends NodeExpressionStatement {
    public String identifier;

    public NodeAssignStatement(String identifier, NodeExpression assignedValue) {
        super(assignedValue);
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignStatement(this);
    }
}