package io.github.thugborean.ast.node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.github.thugborean.ast.node.statement.NodeBlock;
import io.github.thugborean.ast.node.statement.NodeStatement;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class Program implements NodeAST {
    public Deque<NodeBlock> blockStack = new ArrayDeque<>();
    public List<NodeStatement> nodes;

    public Program() {
        nodes = new ArrayList<>();
    }

    public Program(List<NodeStatement> nodes) {
        this.nodes = nodes;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }

    // If we have an empty blockStack we add to the top block/env, otherwise we add it to the current block
    public void addNode(NodeStatement node) {
        if(blockStack.isEmpty())
            nodes.add(node);
        else blockStack.peekLast().addNode(node);
    }
}
