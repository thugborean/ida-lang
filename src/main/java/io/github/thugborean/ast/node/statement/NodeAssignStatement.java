package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.ast.node.expression.NodeExpression;

public class NodeAssignStatement extends NodeStatement{
    public String identifier;
    public NodeExpression assignedValue;

    public NodeAssignStatement(String identifier, NodeExpression assignedValue) {
        this.identifier = identifier;
        this.assignedValue = assignedValue;
    }
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignStatement(this);
    }
}