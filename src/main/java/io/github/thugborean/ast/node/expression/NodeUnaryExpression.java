package io.github.thugborean.ast.node.expression;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeUnaryExpression extends NodeExpression{
    Token operator;
    NodeExpression expression;

    public NodeUnaryExpression(Token operator, NodeExpression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitUnaryExpression(this);
    }   
}