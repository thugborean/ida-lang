package io.github.thugborean.ast.node.expression;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.vm.symbol.ValType;

public class NodeVariableReference extends NodeExpression{
    public String identifier;
    public ValType resolvedType; // ?

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