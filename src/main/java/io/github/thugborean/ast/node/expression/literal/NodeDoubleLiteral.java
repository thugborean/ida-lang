package io.github.thugborean.ast.node.expression.literal;

import io.github.thugborean.syntax.Token;

public class NodeDoubleLiteral extends NodeLiteral{
    
    public NodeDoubleLiteral(Token token) {
        super(token);
    }

    @Override
    public Double getValue() {
        return (Double) this.value;
    }
}