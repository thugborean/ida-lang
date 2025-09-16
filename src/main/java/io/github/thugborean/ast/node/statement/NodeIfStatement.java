package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeIfStatement extends NodeStatement{
    public NodeExpression booleanExpression;
    public NodeBlock thenBlock;
    public NodeStatement elseBlock;

    public NodeIfStatement(NodeExpression booleanExpression, NodeBlock thenBlock) {
        this.booleanExpression = booleanExpression;
        this.thenBlock = thenBlock;
    }

    public NodeIfStatement(NodeExpression booleanExpression, NodeBlock thenBlock, NodeStatement elseBlock) {
        this.booleanExpression = booleanExpression;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeIfStatement(this);
    }
}