package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeWhileStatement extends NodeStatement{

    public NodeExpression booleanExpression;
    public NodeBlock thenBlock;

    public NodeWhileStatement(NodeExpression booleanExpression, NodeBlock thenBlock) {
        this.booleanExpression = booleanExpression;
        this.thenBlock = thenBlock;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeWhileStatement(this);
    }
}