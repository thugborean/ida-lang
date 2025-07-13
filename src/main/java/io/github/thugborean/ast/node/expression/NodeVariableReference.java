package io.github.thugborean.ast.node.expression;

import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeVariableReference extends NodeExpression{
    public String identifier;

    public NodeVariableReference(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeVariableReference(this);
    }

    public String toString() {
        return this.identifier;
    }
}