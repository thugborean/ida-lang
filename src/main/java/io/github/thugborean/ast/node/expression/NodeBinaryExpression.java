package io.github.thugborean.ast.node.expression;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeBinaryExpression extends NodeExpression{
    private NodeExpression leftHandSide;
    private NodeExpression rightHandSide;
    private Token operator;

    public NodeBinaryExpression(NodeExpression leftHandSide, NodeExpression rightHandSide, Token operator) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.operator = operator;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeBinaryExpression(this);
    }
}