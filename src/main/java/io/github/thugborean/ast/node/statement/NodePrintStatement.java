package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodePrintStatement extends NodeStatement{
    public NodeStatement printable;
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodePrintStatement(this);
    }
    
}
