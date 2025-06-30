package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.NodeExpression;

public class NodeAssignStatement extends NodeStatement{
    public NodeVariableReference variableReference;
    public NodeExpression assignedValue;

    public NodeAssignStatement(NodeVariableReference variableReference, NodeExpression assignedValue) {
        this.variableReference = variableReference;
        this.assignedValue = assignedValue;
    }
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignStatement(this);
    }
}