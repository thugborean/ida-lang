package io.github.thugborean.ast.node.expression.literal;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeNumericLiteral extends NodeExpression{
    public Token token; // The value of the literal, I'm not quite sure about this

    public NodeNumericLiteral(Token token) {
        this.token = token;
    }

    // I'm not sure...
    public Integer getValue() {
        return (Integer)token.literal;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeNumericLiteral(this);
    }
}