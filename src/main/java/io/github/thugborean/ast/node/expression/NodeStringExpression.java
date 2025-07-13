package io.github.thugborean.ast.node.expression;

import java.util.ArrayList;
import java.util.List;

import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeStringExpression extends NodeExpression{
    public List<NodeExpression> stringElements;

    public NodeStringExpression(List<NodeExpression> stringElements) {
        this.stringElements = new ArrayList<>(stringElements);
    }
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitStringExpression(this);
    }
}