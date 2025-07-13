package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeStringExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodePrintStatement extends NodeStatement{
    public NodeStringExpression printable;
    
    public NodePrintStatement(NodeStringExpression printable) {
        this.printable = printable;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodePrintStatement(this);
    }
}