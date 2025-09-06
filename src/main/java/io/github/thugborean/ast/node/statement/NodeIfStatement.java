package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeIfStatement extends NodeStatement{

    public NodeExpression booleanExpression;
    public boolean isElif = false;

    public NodeIfStatement(NodeExpression booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    public NodeIfStatement(NodeExpression booleanExpression, boolean isElif) {
        this.booleanExpression = booleanExpression;
        isElif = true;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }
}