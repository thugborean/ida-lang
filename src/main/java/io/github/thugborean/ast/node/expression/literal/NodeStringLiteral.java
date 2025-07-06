package io.github.thugborean.ast.node.expression.literal;

import io.github.thugborean.syntax.Token;

public class NodeStringLiteral extends NodeLiteral{

    public NodeStringLiteral(Token token) {
        super(token);
    }

    // THIS IS POSTPONING COMPLEXITY !!!! THIS MUST BE CHANGED LATER DOWN THE LINE!!!!!!!!!!!
    public String getValue() {
        String str = (String)value;
        return str.substring(1, str.length() - 1);
    }
}