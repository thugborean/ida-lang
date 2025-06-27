package io.github.thugborean.ast.node.statement;
import org.w3c.dom.Node;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeExpressionStatement extends NodeStatement {
    public NodeExpression value;

    public NodeExpressionStatement(NodeExpression value) {
        this.value = value;
    }
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }
}