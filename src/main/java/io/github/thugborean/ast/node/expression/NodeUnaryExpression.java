package io.github.thugborean.ast.node.expression;

import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeUnaryExpression extends NodeExpression{

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        visitor.visit(this);
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }
    
}
