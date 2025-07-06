package io.github.thugborean.ast.node.expression.literal;

import io.github.thugborean.syntax.Token;

public class NodeNumericLiteral extends NodeLiteral{

    public NodeNumericLiteral(Token token) {
        super(token);
    }

    public Integer getValue() {
        return (Integer)this.value;
    }
}