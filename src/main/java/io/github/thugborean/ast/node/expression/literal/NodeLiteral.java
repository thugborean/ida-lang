package io.github.thugborean.ast.node.expression.literal;
import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.syntax.Token;

public abstract class NodeLiteral extends NodeExpression{
    public final Token token;
    public final Object value;

    public NodeLiteral(Token token) {
        this.token = token;
        this.value = token.literal;
    }
    
    public abstract Object getValue();

    public String toString() {
        return this.value.toString();
    }
}