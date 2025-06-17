package io.github.thugborean.ast.node;

import java.util.List;

import io.github.thugborean.ast.node.statement.NodeStatement;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class Program implements NodeAST {

    private List<NodeStatement> nodes;
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addNode(NodeStatement node) {
        nodes.add(node);
    }
}
