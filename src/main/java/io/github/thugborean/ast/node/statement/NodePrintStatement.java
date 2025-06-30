package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodePrintStatement extends NodeStatement{
    public NodeExpression printable;
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodePrintStatement(this);
    }
}