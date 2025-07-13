package io.github.thugborean.ast.node.expression;

import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeDecrement extends NodeExpression{

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeDecrement(this);
    }
    
}
