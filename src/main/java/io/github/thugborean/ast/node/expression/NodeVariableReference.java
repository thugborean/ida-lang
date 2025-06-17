package io.github.thugborean.ast.node.expression;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeVariableReference extends NodeExpression{
    private Token token;

    public NodeVariableReference(Token token) {
        this.token = token;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {

        return visitor.visitNodeVariableReference(this);
    }
    
}
