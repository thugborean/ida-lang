package io.github.thugborean.ast.node.expression.literal;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeStringLiteral extends NodeExpression{
    public Token token;

    public NodeStringLiteral(Token token) {
        this.token = token;
    }
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }
    
}
