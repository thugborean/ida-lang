package io.github.thugborean.ast.node.statement;

import java.util.List;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeIfStatement extends NodeStatement{
    public NodeExpression booleanExpression;
    public NodeBlock thenBlock;
    public NodeBlock elseBlock;
    public List<NodeBlock> elifBlocks;

    public NodeIfStatement(NodeExpression booleanExpression, NodeBlock thenBlock) {
        this.booleanExpression = booleanExpression;
        this.thenBlock = thenBlock;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeIfStatement(this);
    }
}