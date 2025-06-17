package io.github.thugborean.ast.node.expression.literal;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeNumericLiteral extends NodeExpression{
    private Token token;

    public NodeNumericLiteral(Token token) {
        this.token = token;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
    
        return visitor.visitNodeNumericLiteral(this);
    }
    
}
