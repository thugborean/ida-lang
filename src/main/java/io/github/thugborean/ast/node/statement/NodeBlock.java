package io.github.thugborean.ast.node.statement;

import java.util.ArrayList;
import java.util.List;

import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeBlock extends NodeStatement{
    public List<NodeStatement> statements = new ArrayList<>();

    public NodeBlock(List<NodeStatement> statements) {
        this.statements = statements;
    }

    public void addNode(NodeStatement node) {
        statements.add(node);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeBlock(this);
    }
}