package io.github.thugborean.ast.node;

import java.util.ArrayList;
import java.util.List;

import io.github.thugborean.ast.node.statement.NodeStatement;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class Program implements NodeAST {

    public List<NodeStatement> nodes;

    public Program() {
        nodes = new ArrayList<>();
    }
    // WIP MAYBE NEEDED IDK
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }

    public void addNode(NodeStatement node) {
        nodes.add(node);
    }
}
